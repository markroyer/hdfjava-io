/**
 * 
 */
package edu.umaine.cs.simple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
import ncsa.hdf.hdf5lib.HDFArray;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * @author Mark Royer
 *
 */
public class H5FileExample {

	public void runHDFJavaIOTests(String[] args)
			throws IOException, H5Exception {

		int numWrites = 30;// 0;// 00;
		int maxRows = 26843545 / 4; // Aprox. will be generated 2GB // 100000;
		int cols = 10;
		double startVal = 0.0;
		double endVal = 1000.0;

		List<long[]> allTimes = new ArrayList<>();

		File file = File.createTempFile("test", ".h5");

		int inc = 100000;
		for (int r = 1; r <= maxRows; r *= 2) {// inc) {
			System.out.println("Performing " + r + " <= " + maxRows);
			allTimes.add(performFileWritesAndReads(file, numWrites, r, cols,
					startVal, endVal));
		}

		System.out.printf("%-12s%-11s%-11s%-11s%-11s%n", "Size", "HDFJWrites",
				"HDFJReads", "HDFWrites", "HDFReads");
		for (long[] t : allTimes) {
			System.out.printf("%12d%11d%13d%11d%11d%n", t[0], t[1], t[2], t[3],
					t[4]);
		}

		System.out.printf("File successfully written to %s.\n",
				file.getAbsolutePath());
	}

	public void runHDFJavaIOandJavaOctaveTests(String[] args)
			throws IOException, H5Exception {

		int numWrites = 10;// 00;
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

		System.out.printf("%-12s%-11s%-13s%-11s%n", "Size", "HDFJavaIO",
				"JavaOctave", "TabFile");
		for (long[] t : allTimes) {
			System.out.printf("%12d%11d%13d%11d%n", t[0], t[1], t[2], t[3]);
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

		final String inputTabFileName = "input";
		final File inputTabFile = new File("/tmp/" + inputTabFileName + ".txt");
		final File outputTabFile = new File("/tmp/output.txt");

		long sum = 0;
		long josum = 0;
		long tabsum = 0;
		for (int i = 0; i < n; i++) {

			// START H5Writer and H5Reader

			long startTime = System.currentTimeMillis();

			out.writeHDF5File(file.getAbsolutePath().toString(), args2);

			try {
				processOctave(file.getAbsolutePath(),
						outputFile.getAbsolutePath(), "-hdf5", "p0");
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
			System.out.println("hdfjio: " + runTime);

			// End H5Writer and H5Reader

			// START Tab write read

			startTime = System.currentTimeMillis();

			writeTABFile(inputTabFile, double2D);

			try {
				processOctave(inputTabFile.getAbsolutePath(),
						outputTabFile.getAbsolutePath(), "-ascii",
						inputTabFileName);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			readTABFile(outputTabFile,
					new double[double2D.length][double2D[0].length]);

			endTime = System.currentTimeMillis();

			long tabRunTime = endTime - startTime;
			System.out.println("tabRunTime: " + tabRunTime);

			// End Tab write read

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

			long joRunTime = endTime - startTime;
			System.out.println("joRunTime: " + joRunTime);

			// End Java Octave timing

			sum += runTime;
			josum += joRunTime;
			tabsum += tabRunTime;
		}

		// Actual bytes used by matrix is rows*cols*8
		return new long[] { rows * cols * 8, sum / n, josum / n, tabsum / n };

	}

	public void writeTABFile(File file, double[][] matrix) {

		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length - 1; j++) {
					out.write(String.valueOf(matrix[i][j]));
					out.write("\t");
				}
				out.write(String.valueOf(matrix[i][matrix[0].length - 1]));
				out.write("\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void readTABFile(File file, double[][] matrix) {

		try {

			Scanner scan = new Scanner(file);

			int count = 0;
			while (count < matrix.length * matrix[0].length) {
				matrix[count % matrix.length][count / matrix.length
						% matrix[0].length] = scan.nextDouble();
				count++;
			}

			scan.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		double[][] matrix = new double[20][10];
		File file = new File("/home/mroyer/Desktop/small.tab");
		H5FileExample example = new H5FileExample();
		example.readTABFile(file, matrix);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.printf("%f\t", matrix[i][j]);
			}
			System.out.println();
		}
		example.writeTABFile(new File("smallWrite.tab"), matrix);
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
	public long[] performFileWritesAndReads(File file, int n, int rows,
			int cols, double start, double end) throws H5Exception {

		H5OctaveWriter out = new H5OctaveWriter();
		H5OctaveReader in = new H5OctaveReader();

		final double[][] double2D = create2Darray(rows, cols, start, end);

		Object[] args2 = new Object[] { double2D };

		long writeSum = 0;
		long readSum = 0;
		long h5WriteSum = 0;
		long h5ReadSum = 0;

		for (int i = 0; i < n; i++) {
			long startTime = System.currentTimeMillis();

			out.writeHDF5File(file.getAbsolutePath().toString(), args2);

			long endTime = System.currentTimeMillis();

			long writeRunTime = endTime - startTime;

			startTime = System.currentTimeMillis();

			in.readHDF5File(file.getAbsoluteFile());

			endTime = System.currentTimeMillis();

			long readRunTime = endTime - startTime;

			startTime = System.currentTimeMillis();

			writeUsingStandardHDFJava(file, "p0", double.class,
					new long[] { cols, rows }, double2D);

			endTime = System.currentTimeMillis();

			long h5WriteRunTime = endTime - startTime;

			startTime = System.currentTimeMillis();

			double[][] readArray = readUsingStandardHDFJava(file, "p0",
					new long[] { cols, rows });

//			for (int j=0; j < readArray.length; j++)
//				System.out.println(Arrays.toString(readArray[j]));
//			
//			if (rows > 8)
//				System.exit(0);

			endTime = System.currentTimeMillis();

			long h5ReadRunTime = endTime - startTime;

			writeSum += writeRunTime;
			readSum += readRunTime;
			h5WriteSum += h5WriteRunTime;
			h5ReadSum += h5ReadRunTime;
		}

		return new long[] { rows * cols * 8, writeSum / n, readSum / n,
				h5WriteSum / n, h5ReadSum / n };

	}

	private double[][] readUsingStandardHDFJava(File file, String name,
			long[] dims) {

		try {

			H5File hdfFile = new H5File(file.getAbsolutePath(),
					FileFormat.READ);
			int id = hdfFile.open();

			if (id < 0)
				throw new H5Exception(
						"Unable to open the file " + file.getAbsolutePath());

			H5Group h5Grp = ((H5Group) hdfFile.get(name));

			List<HObject> members = h5Grp.getMemberList();

			HObject val = members.get(1);

			double[][] readArray = new double[(int)dims[0]][(int)dims[1]];
			
			HDFArray arr = new HDFArray(readArray);

			byte[] bits = ((H5ScalarDS) val).readBytes();

			arr.arrayify(bits);

			hdfFile.close();

//			int dataset_id = H5.H5Dopen(id, name + "/value",
//					HDF5Constants.H5P_DEFAULT);
//
//			// Allocate array of pointers to two-dimensional arrays (the
//			// elements of the dataset.
//			double[][] dataRead = new double[(int) dims[0]][(int) (dims[1])];
//
//			if (dataset_id >= 0)
//				H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_DOUBLE,
//						HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
//						HDF5Constants.H5P_DEFAULT, dataRead);
//			
//			hdfFile.close();

			return readArray;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void processOctave(String fileName, String outputFileName,
			String format, String loadString)
			throws IOException, InterruptedException {

		ProcessBuilder pb = new ProcessBuilder("octave", "-qf", "--no-gui");

		Process process = pb.start();

		PrintWriter bis = new PrintWriter(
				new OutputStreamWriter(process.getOutputStream()));

		bis.printf("load '%s'%n", fileName);

		bis.println(String.format("r0 = %s;", loadString));

		bis.printf("save('%s', '%s', 'r0')%n", format, outputFileName);

		bis.flush();
		bis.close();

		int errCode = process.waitFor();
//		System.out.println("Echo command executed, any errors? "
//				+ (errCode == 0 ? "No" : "Yes"));
//		System.out.println("Echo Output:\n" + output(process.getErrorStream()));

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
