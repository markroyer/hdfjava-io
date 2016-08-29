package edu.umaine.cs.h5;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful functions working with H5 data.
 * 
 * @author Mark Royer
 *
 */
public class H5Util {

	/**
	 * Converts double[][] to Double[]
	 * 
	 * @param d2
	 *            Two dimensional array (Not null)
	 * @return One dimensional version of the array (Never null)
	 */
	public static Double[] double2DToDouble(double[][] d2) {

		if (d2.length == 0)
			return new Double[0];

		Double[] d = new Double[d2.length * d2[0].length];
		for (int n = 0; n < d2[0].length; n++) {
			for (int m = 0; m < d2.length; m++) {
				d[n * d2.length + m] = d2[n][m];
			}
		}
		return d;
	}

	/**
	 * Returns the length of each dimension of the given object. For primitives it returns empty arrays. The type char
	 * is 1x1. For Strings it returns String.length()x1, and for arrays it returns all dimensions. Assumes that the
	 * multidimensional array is not jagged and not empty. For arrays containing zero length dimensions use the
	 * {@link EmptyMatrix} class.
	 * 
	 * @param obj
	 * @return
	 * @throws TypeException
	 */
	public static long[] getDimensions(Object obj) throws TypeException {
		Class<?> objClass = obj.getClass();

		if (!objClass.isArray()) {
			if (String.class == objClass)
				return new long[] { ((String) obj).length(), 1 };
			else if (Character.class == objClass)
				return new long[] { 1, 1 }; // chars are in 1x1 for some reason
			else if (isPrimitiveWrapper(objClass))
				return new long[] {}; // All the rest are empty
			else if (objClass == EmptyMatrix.class)
				return new long[] { 2 };

			throw new TypeException(obj.getClass(), "Unhandled type");

		} else { // It's an array!

			int numDim = objClass.getName().lastIndexOf('[') + 1;
			List<Long> result = new ArrayList<Long>(numDim);

			Object current = obj;
			for (int i = 0; i < numDim; i++) {

				long arrLength = Array.getLength(current);

				if (arrLength == 0) {
					throw new TypeException(objClass,
							String.format("Array has zero length dimension. Use the %s type for this purpose. ",
									EmptyMatrix.class.getName()));
				}

				result.add(arrLength);

				// We are guaranteed at least 1 element in the current array
				// because
				// of the exception thrown above.
				current = Array.get(current, 0);
			}

			if ("Ljava.lang.String;".equals(getArrayType(objClass))) {
				result.add(getLongestStr(obj, 0));
			}

			return toArray(result);
		}
	}

	public static long getLongestStr(Object array, long currentLongest) throws TypeException {

		if (array instanceof String) {
			String str = (String) array;
			return str.length() > currentLongest ? str.length() : currentLongest;
		} else { // it's still an array

			int len = Array.getLength(array);

			if (len == 0)
				throw new TypeException(array.getClass(),
						String.format("Array has zero length dimension. Use the %s type for this purpose. ",
								EmptyMatrix.class.getName()));

			long curr = currentLongest;
			for (int i = 0; i < len; i++) {
				curr = getLongestStr(Array.get(array, i), curr);
			}
			return curr;
		}
	}

	public static long[] toArray(List<Long> list) {
		long[] result = new long[list.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	/**
	 * Returns true if the given class represents a primitive wrapper type. Note this method does not treat Void as a
	 * primitive wrapper type.
	 * 
	 * @param objClass
	 *            (Not null)
	 * @return True iff the given type is a wrapper for a primitive other than void
	 */
	public static boolean isPrimitiveWrapper(Class<?> objClass) {
		return Boolean.class == objClass || Character.class == objClass || Byte.class == objClass
				|| Short.class == objClass || Integer.class == objClass || Long.class == objClass
				|| Float.class == objClass || Double.class == objClass;
	}

	/**
	 * Converts a boolean array of unspecified dimensions to an array of integers of the same dimensions. False is
	 * mapped to 0 and true is mapped to 1.
	 * 
	 * @param obj
	 * @return
	 * @throws TypeException
	 *             Thrown if the given object is not a boolean array.
	 */
	public static Object booleanArrayToIntegers(Object obj) throws TypeException {
		return convertArray(obj, boolean.class, int.class);
	}

	public static Object intArrayToBoolean(Object array) throws TypeException {
		return convertArray(array, int.class, boolean.class);
	}

	public static Object charArrayToByte(Object array) throws TypeException {
		return convertArray(array, char.class, byte.class);
	}

	public static Object byteArrayToChar(Object array) throws TypeException {

		List<Integer> dims = getDims(array);

		if (dims.size() == 2 && dims.get(0) == 1 && dims.get(1) == 1) {
			// It's just a single character
			return (char) ((byte[][]) array)[0][0];
		} else { // Return an array of characters
			return convertArray(array, byte.class, char.class);
		}
	}

	public static Object stringArrayToByte(Object array, long[] realDims) throws TypeException {

		Class<?> clazz = array.getClass();

		if (clazz == String.class) {
			try {

				String str = (String) array;// .toString();

				long longestStr = realDims[realDims.length - 1];
				if (str.length() < longestStr) // Pad if necessary
					str = String.format("%1$-" + longestStr + "s", str);

				return str.getBytes("US-ASCII");
			} catch (UnsupportedEncodingException e) {
				throw new TypeException(String.class, "Unable to create byte array from string array.");
			}
		} else {

			List<Integer> dims = getDims(array);

			int[] is = new int[dims.size() + 1];
			for (int i = 0; i < dims.size(); i++) {
				is[i] = dims.get(i);
			}
			// Last dimension is the length of the strings
			Object cur = array;
			while (cur.getClass().isArray()) {
				// None of these can be zero lenght because this was tested in getDims above?
				// if (Array.getLength(cur) == 0)
				// throw new TypeException(cur.getClass(), "Zero length matrix not allow. Use EmptyMatrix class.");
				cur = Array.get(cur, 0);
			}
			if (!(cur instanceof String))
				throw new TypeException(cur.getClass(), "Expected an object of type String.class");
			is[is.length - 1] = ((String) cur).length();

			Object result = Array.newInstance(byte.class, is);

			for (int i = 0; i < is[0]; i++)
				Array.set(result, i, stringArrayToByte(Array.get(array, i), realDims));

			return result;

		}
	}

	public static Object byteArrayToString(Object array) throws TypeException {

		List<Integer> dims = getDims(array);

		if (dims.size() == 2 && dims.get(1) == 1) { // Just return a string
			byte[] res = new byte[dims.get(0)];
			byte[][] arr2d = (byte[][]) array;
			for (int i = 0; i < res.length; i++)
				res[i] = arr2d[i][0];
			return new String(res);
		}

		int[] is = new int[dims.size() - 1];
		for (int i = 0; i < is.length; i++) {
			is[i] = dims.get(i);
		}

		if (is.length == 0) {
			return new String((byte[]) array);
		} else {
			Object result = Array.newInstance(String.class, is);

			for (int i = 0; i < is[0]; i++)
				Array.set(result, i, byteArrayToString(Array.get(array, i)));

			return result;
		}

	}

	private static List<Integer> getDims(Object array) throws TypeException {

		if (!array.getClass().isArray())
			throw new TypeException(array.getClass(), "Expected array.");

		List<Integer> dims = new ArrayList<Integer>();
		Object cur = array;
		while (cur.getClass().isArray()) {
			int length = Array.getLength(cur);
			dims.add(length);
			if (length > 0)
				cur = Array.get(cur, 0);
			else
				throw new TypeException(cur.getClass(), "Zero length matrix is not allowed.  See EmptyMatrix.class.");

		}
		return dims;
	}

	private static Object convertArray(Object obj, Class<?> fromType, Class<?> toType) throws TypeException {
		List<Integer> dims = new ArrayList<Integer>();

		Object cur = obj;
		while (cur.getClass().isArray()) {
			int length = Array.getLength(cur);
			dims.add(length);
			if (length > 0)
				cur = Array.get(cur, 0);
			else
				throw new TypeException(cur.getClass(), "Zero length matrix is not allowed.  See EmptyMatrix.class.");

		}

		int[] is = new int[dims.size()];
		for (int i = 0; i < is.length; i++) {
			is[i] = dims.get(i);
		}

		return setArray(Array.newInstance(toType, is), obj, fromType, toType);
	}

	private static Object setArray(Object resultArray, Object currentObject, Class<?> fromType, Class<?> toType)
			throws TypeException {

		if (currentObject.getClass().isArray()) {
			int length = Array.getLength(currentObject);

			if (length == 0)
				throw new TypeException(currentObject.getClass(), "Can't be zero length array");

			for (int i = 0; i < length; i++) {

				Object val = setArray(Array.get(resultArray, i), Array.get(currentObject, i), fromType, toType);
				if (val.getClass().isArray())
					Array.set(resultArray, i, val);
				else { // it's something else ...

					if (fromType == boolean.class && toType == int.class)
						Array.set(resultArray, i, (boolean) val ? 1 : 0);
					else if (fromType == int.class && toType == boolean.class)
						Array.set(resultArray, i, ((int) val) == 1);
					else if (fromType == char.class && toType == byte.class)
						Array.set(resultArray, i, (byte) ((Character) val).charValue());
					else if (fromType == byte.class && toType == char.class)
						Array.set(resultArray, i, (char) ((Byte) val).byteValue());
					else
						throw new TypeException(toType, "Don't know how to handle this type.");
				}
			}

			return resultArray;

		} else {
			return currentObject;
		}

	}

	/**
	 * Returns the underlying type of the array. For example:
	 * 
	 * String[]
	 * 
	 * JVM representation is
	 * 
	 * [Ljava.lang.String;
	 * 
	 * The result would be
	 * 
	 * Ljava.lang.String;
	 * 
	 * @param clazz
	 *            The array class to check
	 * @return The underlying type of the array without dimension information
	 */
	public static String getArrayType(Class<?> clazz) {
		String compType = clazz.getName().substring(clazz.getName().lastIndexOf('[') + 1);
		return compType;
	}

	public static boolean isStringArray(Class<?> clazz) {
		return "Ljava.lang.String;".equals(getArrayType(clazz));
	}

	public static boolean isBooleanArray(Class<?> clazz) {
		String type = getArrayType(clazz);
		return "Z".equals(type) || "Ljava.lang.Boolean;".equals(type);
	}

	public static boolean isCharArray(Class<?> clazz) {
		String type = getArrayType(clazz);
		return "C".equals(type) || "Ljava.lang.Character;".equals(type);
	}

}
