/**
 * 
 */
package edu.umaine.cs.h5.octave;

import static edu.umaine.cs.h5.H5Util.getDimensions;
import static edu.umaine.cs.h5.H5Util.isBooleanArray;
import static edu.umaine.cs.h5.H5Util.isCharArray;
import static edu.umaine.cs.h5.H5Util.isStringArray;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.UBYTE;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.toH5;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.H5Exception;
import edu.umaine.cs.h5.H5Util;
import edu.umaine.cs.h5.H5Writer;
import edu.umaine.cs.h5.TypeException;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * Writes an H5 file using Octave conventions.
 * 
 * @author Mark Royer
 *
 */
public class H5OctaveWriter implements H5Writer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umaine.cs.h5.H5Writer#writeHDF5File(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void writeHDF5File(String fileName, Object[] args) throws H5Exception {

		String[] labels = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			labels[i] = "p" + i;
		}
		writeHDF5File(fileName, labels, args);

	}

	/*
	 * (non-Javadoc) throw new TypeException("Dimensions are too big.");
	 * 
	 * @see edu.umaine.cs.h5.H5Writer#writeHDF5File(java.lang.String, java.lang.String[], java.lang.Object[])
	 */
	@Override
	public void writeHDF5File(String fileName, String[] labels, Object[] args) throws H5Exception {

		H5File file = null;

		try {

			file = new H5File(fileName, FileFormat.CREATE);

			int id = file.open();

			if (id < 0) {
				throw new H5Exception("Unable to open the file " + fileName);
			}

			for (int i = 0; i < args.length; i++) {
				writeObjectToFile(file, labels[i], args[i]);
			}

		} catch (Exception e) {
			throw new H5Exception(e);
		} finally {
			if (file != null) {
				// Finish and close file
				try {
					file.close();
				} catch (HDF5Exception e) {
					throw new H5Exception(e); // Shouldn't happen
				}
			}
		}

	}

	/**
	 * Object is written to the file using the octave convention. This means that a group will be created with the given
	 * label with two children. The children are named "type" and "value". The type is a string containing the octave
	 * type, and the value is object value.
	 * 
	 * @param file
	 *            An open HDF5 file (Not null)
	 * @param label
	 *            The name of the object (Not null)
	 * @param obj
	 *            The value to write (Not null)
	 * @throws H5Exception
	 *             Thrown if there is an issue writing to the file (most likely a typing problem)
	 */
	public void writeObjectToFile(H5File file, String label, Object obj) throws H5Exception {

		Class<?> clazz = obj.getClass();

		try {
			// Convert iterable objects to arrays...`
			if (obj instanceof Iterable<?>) {
				Iterable<?> it = (Iterable<?>) obj;

				List<Object> values = new ArrayList<>();
				Class<?> type = null;
				for (Object item : it) {
					if (type != null && type != item.getClass())
						throw new TypeException(item.getClass(), "Mixed types not allowed for iterable types.");
					type = item.getClass();
					values.add(item);
				}

				if (values.size() == 0)
					throw new TypeException(clazz, "Zero length matrix is not allowed.  See EmptyMatrix.class.");

				// Set the current object and type to the converted iterable array.
				obj = values.toArray((Object[]) Array.newInstance(type, values.size()));
				clazz = obj.getClass();
			}

			// Setup the group and set the OCTAVE attribute so that the object is not parsed as a struct when loaded by
			// octave.

			H5Group objLabelGroup = (H5Group) file.createGroup(label, null);

			ncsa.hdf.object.Attribute attr = new ncsa.hdf.object.Attribute("OCTAVE_NEW_FORMAT", UBYTE, new long[] {});
			attr.setValue(new int[] { 1 });
			file.writeAttribute(objLabelGroup, attr, false);

			writeType(file, objLabelGroup, clazz);

			long[] dims = getDimensions(obj);

			if (!clazz.isArray()) {

				// Try to write out the basic types...
				file.createScalarDS("value", objLabelGroup, toH5(clazz), dims, null, null, 0, prepForWrite(obj, dims));

			} else { // Arrays ...

				// TODO this old string code is using the cell structure
				// String arrays are handled as string cells in octave
				// if (isStringArray(clazz)) {
				// if (getDimensions(obj).length > 1)
				// throw new TypeException(clazz, "Only 1 dimensional String
				// arrays are supported at this time.");
				// String[] strArray = (String[]) obj;
				//
				// H5Group subGrp = (H5Group) file.createGroup(label + "/value",
				// null);
				//
				// for (int i = 0; i < strArray.length; i++) {
				// writeObjectToFile(file, subGrp.getFullName() + "/_" + i,
				// strArray[i]);
				// }
				//
				// // writeObjectToFile(file, subGrp.getFullName() + "/dims",
				// // );
				// int[] cellDims = new int[] { strArray.length, 1 };
				// file.createScalarDS("dims", subGrp,
				// toH5(cellDims.getClass()), getDimensions(cellDims), null,
				// null,
				// 0, null, cellDims);
				//
				// } else {

				file.createScalarDS("value", objLabelGroup, toH5(clazz), dims, null, null, 0, null,
						prepForWrite(obj, dims));
				// }

			}
		} catch (Exception e) {
			throw new H5Exception(e);
		}

	}

	/**
	 * Writes out octave data type based on the given Java data type to the file group named objLabelGroup.
	 * 
	 * @param file
	 *            The file to write to (Not null)
	 * @param objLabelGroup
	 *            The name of the group to wrie to (Not null)
	 * @param type
	 *            The data type (Not null)
	 * @return The data set of the create octave type. This is just a string that represents the octave type. (Never
	 *         null)
	 * @throws Exception
	 *             Thrown if the type is not found or there was a problem writing to the file.
	 */
	private H5ScalarDS writeType(H5File file, H5Group objLabelGroup, Class<?> type) throws Exception {

		String octaveType = OctaveUtil.getOctaveType(type);

		// Need +1 for null string termination
		final H5Datatype typeString = new H5Datatype(Datatype.CLASS_STRING, octaveType.length() + 1, -1, -1);
		H5ScalarDS dset2 = (H5ScalarDS) file.createScalarDS("type", objLabelGroup, typeString, new long[] {}, null,
				null, 0, new String[] { octaveType });

		return dset2;
	}

	private Object prepForWrite(Object obj, long[] dims) throws UnsupportedEncodingException, TypeException {

		Class<?> clazz = obj.getClass();

		if (boolean.class == clazz || Boolean.class == clazz) {
			// Booleans are stored in a double as 1 or 0
			return new double[] { ((boolean) obj) ? 1.0 : 0.0 };
		} else if (char.class == clazz || Character.class == clazz || String.class == clazz)
			return obj.toString().getBytes("US-ASCII");
		else if (byte.class == clazz || Byte.class == clazz)
			return new byte[] { ((byte) obj) };
		else if (short.class == clazz || Short.class == clazz)
			return new short[] { ((short) obj) };
		else if (int.class == clazz || Integer.class == clazz)
			return new int[] { ((int) obj) };
		else if (long.class == clazz || Long.class == clazz)
			return new long[] { ((long) obj) };
		else if (float.class == clazz || Float.class == clazz)
			return new float[] { ((float) obj) };
		else if (double.class == clazz || Double.class == clazz)
			return new double[] { ((double) obj) };
		else if (clazz.isArray()) {
			if (isBooleanArray(clazz)) // Need to convert to integers
				return H5Util.booleanArrayToIntegers(obj);
			else if (isCharArray(clazz))
				return H5Util.charArrayToByte(obj);
			else if (isStringArray(clazz))
				return H5Util.stringArrayToByte(obj, dims);
			else
				return obj; // No Op for arrays
		} else if (EmptyMatrix.class == clazz)
			return new int[] { 0, 0 };

		throw new TypeException(clazz, "Unsupported type");

	}

}
