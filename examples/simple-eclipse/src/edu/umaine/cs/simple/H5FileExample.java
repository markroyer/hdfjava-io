/**
 * 
 */
package edu.umaine.cs.simple;

import java.io.File;

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
		
		final double[][] double2D = {{1,2,3,4,5},{15.1,13.2,14.3,16.4,17.5}};
		
		Object[] args2 = new Object[] { double2D };

		out.writeHDF5File(file.getAbsolutePath().toString(), args2);
		
	}

}
