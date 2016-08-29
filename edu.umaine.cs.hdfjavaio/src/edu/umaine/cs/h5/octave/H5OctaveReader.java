/**
 * 
 */
package edu.umaine.cs.h5.octave;

import static edu.umaine.cs.h5.octave.OctaveUtil.getJavaValueFromH5Group;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umaine.cs.h5.H5ConnectorException;
import edu.umaine.cs.h5.H5Reader;
import edu.umaine.cs.h5.NameValuePair;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.structs.H5G_info_t;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;

/**
 * Reads an H5 file using Octave conventions.
 * 
 * @author Mark Royer
 *
 */
public class H5OctaveReader implements H5Reader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umaine.cs.h5.H5Reader#readHDF5File(java.io.File)
	 */
	@Override
	public List<NameValuePair> readHDF5File(File file) throws H5ConnectorException {
		return readHDF5File(file, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umaine.cs.h5.H5Reader#readHDF5File(java.io.File,
	 * java.lang.String[])
	 */
	@Override
	public List<NameValuePair> readHDF5File(File file, String[] names) throws H5ConnectorException {

		List<NameValuePair> results = new ArrayList<NameValuePair>();

		try {

			// Open an existing file.
			H5File hdfFile = new H5File(file.getAbsolutePath(), FileFormat.READ);
			int id = hdfFile.open();

			if (id < 0)
				throw new Exception();

			// Get group info.
			H5G_info_t ginfo = H5.H5Gget_info(id);

			// Traverse links in the primary group using alphabetical indices
			// (H5_INDEX_NAME).
			// TODO This only seems to work for index by name?
			int sortOrder = HDF5Constants.H5_INDEX_NAME;// CRT_ORDER;
			List<String> namesList = null;
			if (names == null) {
				sortOrder = HDF5Constants.H5_INDEX_NAME;
			} else {
				namesList = Arrays.asList(names);
			}

			for (int i = 0; i < ginfo.nlinks; i++) {
				// Retrieve the name of the ith link in a group
				String name = H5.H5Lget_name_by_idx(id, ".", sortOrder, HDF5Constants.H5_ITER_INC, i,
						HDF5Constants.H5P_DEFAULT);

				if (namesList == null || namesList.contains(name)) {

					H5Group h5Grp = ((H5Group) hdfFile.get(name));

					Object javaValue = getJavaValueFromH5Group(h5Grp);

					NameValuePair r = new NameValuePair(name, javaValue);

					results.add(r);
				}
			}

			if (namesList != null) { // Order according to the give name list
				List<NameValuePair> tmpResult = new ArrayList<>(results.size());
				for (int i = 0; i < namesList.size(); i++) {
					String curName = namesList.get(i);
					for (NameValuePair nameValuePair : results) {
						if (curName.equals(nameValuePair.getName())) {
							tmpResult.add(nameValuePair);
							break;
						}
					}
				}
				results = tmpResult;
			}

			hdfFile.close();

		} catch (Exception e) {
			throw new H5ConnectorException(e);
		}

		return results;

	}

}
