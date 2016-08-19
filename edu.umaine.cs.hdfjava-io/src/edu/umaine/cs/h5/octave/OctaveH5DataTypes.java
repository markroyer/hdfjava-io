/**
 * Copyright (C) Jul 16, 2014 Mark Royer
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

import static edu.umaine.cs.h5.H5Util.getArrayType;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.TypeException;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.h5.H5Datatype;
/**
 * A collection of {@link H5Datatype}s that are formatted the way that Octave
 * expects in the HDF5 file.
 * 
 * @author Mark Royer
 *
 */
public class OctaveH5DataTypes {

	// Chars are stored in single byte
	final static H5Datatype CHAR = new H5Datatype(Datatype.CLASS_INTEGER, 1, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype UBYTE = new H5Datatype(Datatype.CLASS_INTEGER, 1, Datatype.ORDER_LE, Datatype.SIGN_NONE);

	final static H5Datatype BYTE = new H5Datatype(Datatype.CLASS_INTEGER, 1, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype SHORT = new H5Datatype(Datatype.CLASS_INTEGER, 2, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype INT = new H5Datatype(Datatype.CLASS_INTEGER, 4, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype LONG = new H5Datatype(Datatype.CLASS_INTEGER, 8, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype FLOAT = new H5Datatype(Datatype.CLASS_FLOAT, 4, Datatype.ORDER_LE, Datatype.SIGN_2);

	final static H5Datatype DOUBLE = new H5Datatype(Datatype.CLASS_FLOAT, 8, Datatype.ORDER_LE, Datatype.SIGN_2);

	/**
	 * Attempts to return the corresponding H5 data type expected by Octave.
	 * This may not always be what is expected; for example, a boolean type is
	 * stored as a double 1 or 0 so that is the type returned for boolean. If
	 * the given class is an array, then it will return the underlying data
	 * type.
	 * 
	 * @param clazz
	 *            (Not null)
	 * @return The corresponding H5 type expected by Octave (Never null)
	 * @throws TypeException
	 *             Thrown if a matching type can't be found
	 */
	public static H5Datatype toH5(Class<?> clazz) throws TypeException {

		if (boolean.class == clazz || Boolean.class == clazz)
			return DOUBLE; // Booleans are stored in a double as 1 or 0
		else if (char.class == clazz || Character.class == clazz)
			return CHAR;
		else if (byte.class == clazz || Byte.class == clazz)
			return BYTE;
		else if (short.class == clazz || Short.class == clazz)
			return SHORT;
		else if (int.class == clazz || Integer.class == clazz)
			return INT;
		else if (long.class == clazz || Long.class == clazz)
			return LONG;
		else if (float.class == clazz || Float.class == clazz)
			return FLOAT;
		else if (double.class == clazz || Double.class == clazz)
			return DOUBLE;
		else if (clazz.isArray()) { // Return the underlying type
			String compType = getArrayType(clazz);
			switch (compType) {
			case "Z":
			case "Ljava.lang.Boolean;":
				return INT; // For arrays boolean are stored as int32
			case "B":
			case "Ljava.lang.Byte;":
				return BYTE;
			case "C":
			case "Ljava.lang.Character;":
				return CHAR;
			case "S":
			case "Ljava.lang.Short;":
				return SHORT;
			case "I":
			case "Ljava.lang.Integer;":
				return INT;
			case "J":
			case "Ljava.lang.Long;":
				return LONG;
			case "F":
			case "Ljava.lang.Float;":
				return FLOAT;
			case "D":
			case "Ljava.lang.Double;":
				return DOUBLE;
			case "Ljava.lang.String;":
				return CHAR;
			}
		} else if (String.class == clazz) {
			return CHAR;
		} else if (EmptyMatrix.class == clazz) {
			return INT;
		}

		throw new TypeException(clazz, "Unsupported type");
	}

}
