/**
 * Copyright (C) Apr 15, 2014 Mark Royer
 *
 * This file is part of OctaveInterface.
 *
 * OctaveInterface is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OctaveInterface is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with OctaveInterface.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.umaine.cs.h5.octave;

import java.lang.reflect.Array;
import java.util.List;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.H5Util;
import edu.umaine.cs.h5.TypeException;
import ncsa.hdf.hdf5lib.HDFArray;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5Group;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * A collection of Octave helper methods.
 * 
 * @author Mark Royer
 * 
 */
public class OctaveUtil {

	/**
	 * Returns the string representing the corresponding type in Octave.
	 * 
	 * @param type
	 *            Some Java data type (Not null)
	 * @return String representing the Octave type (Never null)
	 * @throws TypeException
	 *             If an Octave type corresponding to the give Java type is not
	 *             found.
	 */
	public static String getOctaveType(Class<?> type) throws TypeException {

		if (Boolean.class == type || boolean.class == type)
			return "bool";
		else if (Byte.class == type || byte.class == type)
			return "int8 scalar";
		else if (Character.class == type || char.class == type)
			return "sq_string";
		else if (Short.class == type || short.class == type)
			return "int16 scalar";
		else if (Integer.class == type || int.class == type)
			return "int32 scalar";
		else if (Long.class == type || long.class == type)
			return "int64 scalar";
		else if (Float.class == type || float.class == type)
			return "float scalar";
		else if (Double.class == type || double.class == type)
			return "scalar";
		else if (String.class == type)
			return "string";
		else if (type.isArray()) {
			String compType = type.getName().substring(type.getName().lastIndexOf('[') + 1);
			switch (compType) {
			case "Z":
			case "Ljava.lang.Boolean;":
				return "bool matrix";
			case "B":
			case "Ljava.lang.Byte;":
				return "int8 matrix";
			case "C":
			case "Ljava.lang.Character;":
				return "sq_string";
			case "S":
			case "Ljava.lang.Short;":
				return "int16 matrix";
			case "I":
			case "Ljava.lang.Integer;":
				return "int32 matrix";
			case "J":
			case "Ljava.lang.Long;":
				return "int64 matrix";
			case "F":
			case "Ljava.lang.Float;":
				return "float matrix";
			case "D":
			case "Ljava.lang.Double;":
				return "matrix";
			case "Ljava.lang.String;":
				return "string";
			}
		} else if (EmptyMatrix.class == type) {
			return "matrix";
		}

		throw new TypeException(type, "Couldn't find corresponding Octave type.");
	}

	public static Object getJavaValue(String type, HObject hVal) throws Exception {

		if (type.contains("matrix") || type.equals("sq_string") || type.equals("string")) {

			H5ScalarDS h5val = (H5ScalarDS) hVal;

			h5val.init();

			long[] dims = h5val.getDims();

			long[] sDims = h5val.getSelectedDims();
			System.arraycopy(h5val.getDims(), 0, sDims, 0, sDims.length);
			h5val.getData();

			Object array = null;

			Class<?> clazz = null;

			switch (type) {
			case "bool matrix":
				clazz = int.class; // Boolean stored as integer
				break;
			case "int8 matrix":
			case "sq_string":
			case "string":
				clazz = byte.class;
				break;
			case "int16 matrix":
				clazz = short.class;
				break;
			case "int32 matrix":
				clazz = int.class;
				break;
			case "int64 matrix":
				clazz = long.class;
				break;
			case "float matrix":
				clazz = float.class;
				break;
			case "matrix":
				clazz = double.class;
				Datatype dt = h5val.getDatatype();
				// Empty matrices are stored as an integer matrix.
				if (Datatype.CLASS_INTEGER == dt.getDatatypeClass())
					clazz = int.class;
				break;
			}

			int[] iDims = new int[dims.length];
			for (int i = 0; i < iDims.length; i++) {

				if (dims[i] > Integer.MAX_VALUE)
					throw new TypeException(clazz, "Dimensions are too big.");

				iDims[i] = (int) dims[i];
			}

			// // Initialize arrays
			array = Array.newInstance(clazz, iDims);

			HDFArray arr = new HDFArray(array);

			byte[] bits = h5val.readBytes();

			arr.arrayify(bits);

			// Is it an empty matrix?
			if (type.equals("matrix") && array instanceof int[]) {
				array = new EmptyMatrix((int[])array);
			} else if (type.equals("bool matrix")) {
				// Need to convert boolean array stored as ints to boolean array
				array = H5Util.intArrayToBoolean(array);
			} else if (type.equals("sq_string")) {
				// Need to convert to char array
				array = H5Util.byteArrayToChar(array);
			} else if (type.equals("string")) {
				// Need to convert to String array
				array = H5Util.byteArrayToString(array);
			}

			return array;

		} else if ("cell".equals(type)) {

			H5Group h5Grp = (H5Group) hVal;

			// Should look like _0, _1, _2, etc.
			List<HObject> members = h5Grp.getMemberList();

			if (members.size() <= 1) { // Last item is dims
				System.err.println("Warning: empty cell read.  The result will be null");
				return null;
			}

			Object firstValue = getJavaValueFromH5Group((H5Group) members.get(0));

			// Last member is the dimension, which we really don't need right
			// now, but is required by octave.
			Object[] arr = (Object[]) Array.newInstance(firstValue.getClass(), members.size() - 1);
			arr[0] = firstValue;

			for (int i = 1; i < arr.length; i++)
				// Ignore last item which is dims.
				arr[i] = getJavaValueFromH5Group((H5Group) members.get(i));

			return arr;

		} else { // Primitives...

			H5ScalarDS h5val = (H5ScalarDS) hVal;

			h5val.init();

			Object val = h5val.read();

			if ("bool".equals(type))
				return ((double[]) val)[0] == 1.0 ? true : false;
			else if ("int8 scalar".equals(type) || "uint8 scalar".equals(type))
				return ((byte[]) val)[0];
			else if ("int16 scalar".equals(type) || "uint16 scalar".equals(type))
				return ((short[]) val)[0];
			else if ("int32 scalar".equals(type) || "uint32 scalar".equals(type))
				return ((int[]) val)[0];
			else if ("int64 scalar".equals(type) || "uint64 scalar".equals(type))
				return ((long[]) val)[0];
			else if ("float scalar".equals(type))
				return ((float[]) val)[0];
			else if ("scalar".equals(type))
				return ((double[]) val)[0];

			// sq_string and string are handled above
		}

		throw new TypeException(type, "Couldn't find corresponding Java type.");

	}

	public static Object getJavaValueFromH5Group(H5Group h5Grp) throws Exception, OutOfMemoryError {
		List<HObject> members = h5Grp.getMemberList();

		H5ScalarDS type = ((H5ScalarDS) members.get(0));

		type.init();
		long[] sDims = type.getSelectedDims();
		System.arraycopy(type.getDims(), 0, sDims, 0, sDims.length);
		type.getData();

		String typeStr = ((String[]) type.read())[0];

		HObject val = members.get(1);

		Object javaValue = getJavaValue(typeStr, val);
		return javaValue;
	}

}
