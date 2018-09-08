/**
 * 
 */
package edu.umaine.cs.simple;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jopas.Jopas;
import org.jopas.Matrix;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import edu.umaine.cs.h5.H5Exception;
import edu.umaine.cs.h5.H5Reader;
import edu.umaine.cs.h5.H5Writer;
import edu.umaine.cs.h5.NameValuePair;
import edu.umaine.cs.h5.octave.H5OctaveReader;
import edu.umaine.cs.h5.octave.H5OctaveWriter;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;

/**
 * @author Mark Royer
 *
 */
public class H5FileExample {

	public void run(String[] args) throws IOException, H5Exception {

		int numWrites = 100;
		int maxRows = 26843545 / 20; // Aprox. 2GB // 100000;
		int cols = 10;
		double startVal = 0.0;
		double endVal = 1000.0;

		List<long[]> allTimes = new ArrayList<>();

		File file = File.createTempFile("test", ".h5");

		int inc = 100000;
		for (int r = 1; r <= maxRows; r *= 2) {// inc) {
			System.out.println("Performing " + r + " <= " + maxRows);
//				allTimes.add(performFileWrites(file, numWrites, r, cols,
//						startVal, endVal));
			try {
				allTimes.add(performJavaOctaveComparison(file, numWrites, r,
						cols, startVal, endVal));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Array Size  HDFJavaIO  Standard HDF5");
		for (long[] t : allTimes) {
			System.out.printf("%12d%11d%13d%n", t[0], t[1], t[2]);
		}

		System.out.printf("File successfully written to %s.\n",
				file.getAbsolutePath());
	}

	public long[] performJavaOctaveComparison(File file, int n, int rows,
			int cols, double start, double end) throws H5Exception {

		H5OctaveWriter out = new H5OctaveWriter();

		final double[][] double2D = create2Darray(rows, cols, start, end);

		final double[] double1D = to1DArray(double2D);

		Object[] args2 = new Object[] { double2D };

		final File outputFile = new File("/tmp/output.h5");

		long sum = 0;
		long josum = 0;
		for (int i = 0; i < n; i++) {

			// START H5Writer and H5Reader

			long startTime = System.currentTimeMillis();

			out.writeHDF5File(file.getAbsolutePath().toString(), args2);

			try {
				processBuilder(file.getAbsolutePath(),
						outputFile.getAbsolutePath());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			H5Reader reader = new H5OctaveReader();
			List<NameValuePair> pairs = reader.readHDF5File(outputFile);
			System.out
					.println(((double[][]) pairs.get(0).getValue())[0].length);

			long endTime = System.currentTimeMillis();

			long runTime = endTime - startTime;

			// End H5Writer and H5Reader

			// Start Jave Octave timing

			startTime = System.currentTimeMillis();

			OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();

			startTime = System.currentTimeMillis();

			OctaveDouble matA = new OctaveDouble(double1D, rows, cols);
			octave.put("p0", matA);
			octave.eval("r0 = p0;");

			OctaveDouble a = (OctaveDouble) octave.get("r0");

			endTime = System.currentTimeMillis();

			octave.close();

			long h5runTime = endTime - startTime;

			sum += runTime;
			josum += h5runTime;
		}

		return new long[] { rows, sum / n, josum / n };

	}

	/**
	 * @param n     The number of writes to perform (>= 0)
	 * @param rows  The number of rows in the file (>= 0)
	 * @param cols  The number of columns in the file (>= 0)
	 * @param start The starting value
	 * @param end   The ending value
	 * @return The average number of milliseconds to write the file
	 * @throws H5ConnectorException
	 */
	public long[] performFileWrites(File file, int n, int rows, int cols,
			double start, double end) throws H5Exception {

		H5OctaveWriter out = new H5OctaveWriter();

		final double[][] double2D = create2Darray(rows, cols, start, end);

		Object[] args2 = new Object[] { double2D };

		long sum = 0;
		long h5sum = 0;
		for (int i = 0; i < n; i++) {
			long startTime = System.currentTimeMillis();

			out.writeHDF5File(file.getAbsolutePath().toString(), args2);

			long endTime = System.currentTimeMillis();

			long runTime = endTime - startTime;

			startTime = System.currentTimeMillis();

			writeUsingStandardHDFJava(file, "p0", double.class,
					new long[] { cols, rows }, double2D);

			endTime = System.currentTimeMillis();

			long h5runTime = endTime - startTime;

			sum += runTime;
			h5sum += h5runTime;
		}

		return new long[] { rows, sum / n, h5sum / n };

	}

	public void processBuilder(String fileName, String outputFileName)
			throws IOException, InterruptedException {

		ProcessBuilder pb = new ProcessBuilder("octave", "-qf", "--no-gui");

		Process process = pb.start();

		PrintWriter bis = new PrintWriter(
				new OutputStreamWriter(process.getOutputStream()));

		bis.printf("load '%s'%n", fileName);

		bis.println("r0 = p0;");

		bis.printf("save('-hdf5', '%s', 'r0')%n", outputFileName);

		bis.flush();
		bis.close();

		int errCode = process.waitFor();
		System.out.println("Echo command executed, any errors? "
				+ (errCode == 0 ? "No" : "Yes"));
		System.out.println("Echo Output:\n" + output(process.getInputStream()));

	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

	public void writeUsingStandardHDFJava(File f, String label, Class<?> clazz,
			long[] dims, double[][] data) {

		H5File file = null;

		try {

			file = new H5File(f.getAbsolutePath(), FileFormat.CREATE);

			final H5Datatype UBYTE = new H5Datatype(Datatype.CLASS_INTEGER, 1,
					Datatype.ORDER_LE, Datatype.SIGN_NONE);
			final H5Datatype DOUBLE = new H5Datatype(Datatype.CLASS_FLOAT, 8,
					Datatype.ORDER_LE, Datatype.SIGN_2);

			H5Group objLabelGroup = (H5Group) file.createGroup(label, null);

			ncsa.hdf.object.Attribute attr = new ncsa.hdf.object.Attribute(
					"OCTAVE_NEW_FORMAT", UBYTE, new long[] {});
			attr.setValue(new int[] { 1 });
			file.writeAttribute(objLabelGroup, attr, false);

			String octaveType = "matrix"; // For double type

			// Need +1 for null string termination
			final H5Datatype typeString = new H5Datatype(Datatype.CLASS_STRING,
					octaveType.length() + 1, -1, -1);

			file.createScalarDS("type", objLabelGroup, typeString,
					new long[] {}, null, null, 0, new String[] { octaveType });

			file.createScalarDS("value", objLabelGroup, DOUBLE, dims, null,
					null, 0, null, data);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null) {
				// Finish and close file
				try {
					file.close();
				} catch (HDF5Exception e) {
					e.printStackTrace(); // Shouldn't happen
				}
			}
		}
	}

	/**
	 * Create a 2D array starting at the given start value.
	 * 
	 * @param rows  (>= 0)
	 * @param cols  (>= 0)
	 * @param start
	 * @param end
	 * @return
	 */
	private double[][] create2Darray(int rows, int cols, double start,
			double end) {

		double[][] result = new double[cols][];

		double inc = (end - start) / cols;

		for (int i = 0; i < cols; i++) {
			result[i] = createArray(rows, start + inc * i,
					start + inc * (i + 1));
		}

		return result;
	}

	/**
	 * Create an array of size n starting at the given start and ending at start
	 * + (n-1)*(start-end)/n.
	 * 
	 * @param n     The size of the resulting array (>= 0)
	 * @param start The starting value
	 * @param end   The end value (>= start)
	 * @return An array starting of size n
	 */
	private double[] createArray(int n, double start, double end) {

		double inc = (end - start) / n;
		double[] result = new double[n];

		for (int i = 0; i < result.length; i++) {
			result[i] = start + inc * i;
		}

		return result;
	}

	public void hdf5WriteAndReadExample() throws Exception {

		File file = new File("/tmp/testh5.h5");
		int rows = 20, cols = 10;

		String filePath = file.getAbsolutePath().toString();

		H5Writer out = new H5OctaveWriter();

		/* Create2Darray is defined elsewhere */
		double[][] double2D = create2Darray(rows, cols, 0, 30);
		String lbl = "l0";

		for (int i = 0; i < double2D.length; i++)
			System.out.println(Arrays.toString(double2D[i]));

		out.writeHDF5File(filePath, new String[] { lbl },
				new Object[] { double2D });

		H5Reader in = new H5OctaveReader();

		H5File h5File = new H5File(filePath, H5File.READ);
		h5File.open();

		double[][] readResult = null;
		readResult = in.readHDF5Object(h5File, lbl, double[][].class);

		System.out.println("After");
		for (int i = 0; i < readResult.length; i++)
			System.out.println(Arrays.toString(readResult[i]));

		h5File.close();

	}

	private double[] to1DArray(double[][] double2d) {
		double[] result = new double[double2d.length * double2d[0].length];
		for (int i = 0; i < double2d.length; i++) {
			for (int j = 0; j < double2d[i].length; j++) {
				result[double2d.length * j + i] = double2d[i][j];
			}
		}
		return result;
	}

	public void runJoPASExample() {
		// This is Example1 from the joPAS website
		// It does not seem to work with Octave 4.2.2
		
		Jopas jopas = new Jopas(); // joPAS inicialitation

		double a = 6;
		jopas.Load(a, "a");

		double b = 2;
		Matrix mb = new Matrix(b, "b");
		jopas.Load(mb);

		Matrix A = jopas.Save("a");
		System.out.println(A.getRealAt(0, 0));

		Matrix B = jopas.Save("b");
		System.out.println(B.getRealAt(0, 0));

		System.exit(0);
	}
}
