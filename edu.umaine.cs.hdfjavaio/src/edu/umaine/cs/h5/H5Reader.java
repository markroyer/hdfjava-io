package edu.umaine.cs.h5;

import java.io.File;
import java.util.List;

public interface H5Reader {

	/**
	 * Read all of the data from the given file and place it in the result pair,
	 * which is (name, value).
	 * 
	 * @param inputFile
	 *            An HDF5 file formatted using Octave style. (Not null)
	 * @return All variables sorted by name. (Never null)
	 * @throws H5Exception
	 *             If unable to access file.
	 */
	public List<NameValuePair> readHDF5File(File file) throws H5Exception;

	/**
	 * Reads the specified values from the HDF5 file. If names is null all
	 * values in the file are read.
	 * 
	 * @param file
	 * @param names
	 *            (Null allowed)
	 * @return Variables sorted by the order given in names.
	 * @throws H5Exception
	 *             If unable to access file.
	 */
	public List<NameValuePair> readHDF5File(File file, String[] names) throws H5Exception;
}
