package edu.umaine.cs.h5;

import ncsa.hdf.object.h5.H5File;

public interface H5Writer {

	/**
	 * Writes the given arguments to an HDF5 file using p0 for the name of
	 * args[0], p1 for args[1], etc.
	 * 
	 * @param fileName
	 *            The name of the file to write to
	 * @param args
	 * @throws H5Exception
	 *             If there is an issue writing to the file
	 */
	public void writeHDF5File(String fileName, Object[] args) throws H5Exception;

	/**
	 * For each argument object write out to hdf5 file in Matlab format using
	 * the corresponding label. The labels must be the same length as the args
	 * array.
	 * 
	 * @param fileName
	 *            The name of the file to write to (Not null)
	 * @param labels
	 *            The labels for the objects (Not null)
	 * @param args
	 *            The objects to write (Not null)
	 * @throws H5Exception
	 *             If there is an issue writing to the file
	 */
	public void writeHDF5File(String fileName, String[] labels, Object[] args) throws H5Exception;

	/**
	 * Object is written to file using the subtype's convention. File must be
	 * open and ready for writing.
	 * 
	 * @param file
	 *            An open H5 file (Not null)
	 * @param label
	 *            The label for the object (Not null)
	 * @param obj
	 *            The object to write
	 * @throws H5Exception
	 */
	public void writeObjectToFile(H5File file, String label, Object obj) throws H5Exception;

}
