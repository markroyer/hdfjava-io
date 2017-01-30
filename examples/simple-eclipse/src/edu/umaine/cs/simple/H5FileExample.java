/**
 * 
 */
package edu.umaine.cs.simple;

import java.io.File;
import java.util.Arrays;

import edu.umaine.cs.h5.H5ConnectorException;
import edu.umaine.cs.h5.octave.H5OctaveWriter;

/**
 * @author Mark Royer
 *
 */
public class H5FileExample {

	private File file;

	/**
	 * @param file
	 *            File to write to access (Not null)
	 */
	public H5FileExample(File file) {
		this.file = file;
	}

	/**
	 * @return The file that is written to (Not null)
	 */
	public File getFile() {
		return file;
	}

	public void writeFile() throws H5ConnectorException {

		H5OctaveWriter out = new H5OctaveWriter();

		final double[][] double2D = create2Darray(100, 10, 0, 1000);

		Object[] args2 = new Object[] { double2D };

		for (int i=0; i < double2D.length; i++)
			System.out.println(Arrays.toString(double2D[i]));

		out.writeHDF5File(file.getAbsolutePath().toString(), args2);

	}

	/**
	 * Create a 2D array starting at the given start value.
	 * 
	 * @param rows
	 *            (>= 0)
	 * @param cols
	 *            (>= 0)
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
	 * @param n
	 *            The size of the resulting array (>= 0)
	 * @param start
	 *            The starting value
	 * @param end
	 *            The end value (>= start)
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

}
