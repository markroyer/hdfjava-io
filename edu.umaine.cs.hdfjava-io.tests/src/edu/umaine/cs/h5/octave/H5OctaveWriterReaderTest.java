/**
 * 
 */
package edu.umaine.cs.h5.octave;

import static edu.umaine.cs.h5.octave.TestUtil.checkNameValuePairs;
import static edu.umaine.cs.h5.octave.TestUtil.listOfNames;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.H5ConnectorException;
import edu.umaine.cs.h5.H5Reader;
import edu.umaine.cs.h5.H5Writer;
import edu.umaine.cs.h5.NameValuePair;
import edu.umaine.cs.h5.TypeException;

/**
 * @author Mark Royer
 *
 */
public class H5OctaveWriterReaderTest {

	H5Writer writer;
	H5Reader reader;

	final String testFileName = "test.h5";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		writer = new H5OctaveWriter();
		reader = new H5OctaveReader();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File testFile = new File(testFileName);
		if (testFile.exists())
			testFile.delete();
	}

	@Test
	public void testWriteReadPrimitives() throws H5ConnectorException {

		final boolean booleanFalse = false;
		final boolean booleanTrue = true;
		final byte theByte = 7;
		final char theChar = '!';
		final short theShort = 255;
		final int theInt = 1024;
		final long theLong = -35678l;
		final float theFloat = -3495.3455f;
		final double theDouble = 30405.2291;
		final Object[] primitivesArray = { booleanFalse, booleanTrue, theByte, theChar, theShort, theInt, theLong,
				theFloat, theDouble };

		writer.writeHDF5File(testFileName, primitivesArray);
		List<NameValuePair> result = reader.readHDF5File(new File(testFileName));
		checkNameValuePairs(listOfNames("p", 0, 8), Arrays.asList(primitivesArray), result);

	}

	@Test
	public void testWriteReadUnsignedPrimitives() throws H5ConnectorException, URISyntaxException {

		final byte bytePos = 127;
		final byte byteNeg = Byte.MIN_VALUE; // 128 unsigned
		final short shortPos = 32767;
		final short shortNeg = Short.MIN_VALUE; // 32768 unsigned
		final int intPos = 2147483647;
		final int intNeg = Integer.MIN_VALUE; // 2147483648
		final long longPos = 9223372036854775807l;
		final long longNeg = Long.MIN_VALUE; // 9223372036854775808l

		final Object[] primitivesArray = { byteNeg, bytePos, intNeg, intPos, longNeg, longPos, shortNeg, shortPos };

		Bundle bundle = Platform.getBundle("edu.umaine.cs.hdfjavaio.tests");
		URL fileURL = bundle.getEntry("target/classes/edu/umaine/cs/h5/octave/unsignedTypes.h5");
		File file = null;
		try {
			file = new File(FileLocator.resolve(fileURL).toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> result = reader.readHDF5File(file);

		List<String> names = Arrays.asList(new String[] { "byteNeg", "bytePos", "intNeg", "intPos", "longNeg",
				"longPos", "shortNeg", "shortPos" });

		checkNameValuePairs(names, Arrays.asList(primitivesArray), result);
	}

	@Test
	public void testWriteReadStrings() throws H5ConnectorException {

		final String[] stringArray = { "Hello cruel world!", "first", "second", "third", "??????????" };

		writer.writeHDF5File(testFileName, stringArray);
		List<NameValuePair> result = reader.readHDF5File(new File(testFileName));
		checkNameValuePairs(listOfNames("p", 0, stringArray.length - 1), Arrays.asList((Object[]) stringArray), result);

	}

	@Test
	public void testWriteReadStringMatrix() throws H5ConnectorException {

		final String[] str1dMatrix = { "aaaa", "bbbb", "cccc", "dddd", "eeee", "ffff", "gggg", "hhhh", "iiii" };
		checkMatrix(str1dMatrix);

		final String[][] str2dMatrix = { { "aaaa", "bbbb", "cccc" }, { "dddd", "eeee", "ffff" },
				{ "gggg", "hhhh", "iiii" } };
		checkMatrix(str2dMatrix);

		final String[][][] str3dMatrix = {
				{ { "aaaa", "bbbb", "cccc" }, { "dddd", "eeee", "ffff" }, { "gggg", "hhhh", "iiii" } },
				{ { "aaaa", "bbbb", "cccc" }, { "dddd", "eeee", "ffff" }, { "gggg", "hhhh", "iiii" } } };
		checkMatrix(str3dMatrix);

	}

	@Test
	public void testWriteReadDifferentLengthStringMatrix() throws H5ConnectorException {

		final String[] str1dMatrix = { "Once", "upon", "a", "midnight", "dreary", "while", "I", "pondered", "many" };
		final String[] str1dMatrixExpected = { "Once    ", "upon    ", "a       ", "midnight", "dreary  ", "while   ",
				"I       ", "pondered", "many    " };
		checkMatrix(str1dMatrix, str1dMatrixExpected);

		final String[][] str2dMatrix = { { "Once", "upon", "a" }, { "midnight", "dreary", "while" },
				{ "I", "pondered", "many" } };
		final String[][] str2dMatrixExpected = { { "Once    ", "upon    ", "a       " },
				{ "midnight", "dreary  ", "while   " }, { "I       ", "pondered", "many    " } };
		checkMatrix(str2dMatrix, str2dMatrixExpected);

		// final String[][][] str3dMatrix = { { { "Once", "upon" } }, { { "a",
		// "midnight" } }, { { "dreary", "while" } },
		// { { "I", "pondered" } } };
		// final String[][][] str3dMatrixExpected = { { { "Once ", "upon " } },
		// { { "a ", "midnight" } },
		// { { "dreary ", "while " } }, { { "I ", "pondered" } } };
		// checkMatrix(str3dMatrix, str3dMatrixExpected);

		final String[][][] char4dMatrix = {
				{ { "Once", "upon", "a" }, { "midnight", "dreary", "while" }, { "I", "pondered", "many" } } };
		final String[][][] char4dMatrixExpected = { { { "Once    ", "upon    ", "a       " },
				{ "midnight", "dreary  ", "while   " }, { "I       ", "pondered", "many    " } } };
		checkMatrix(char4dMatrix, char4dMatrixExpected);
	}

	@Test(expected = H5ConnectorException.class)
	public void testWriteReadEmptyIterable() throws H5ConnectorException {
		checkMatrix(new ArrayList<Double>());
	}

	@Test(expected = H5ConnectorException.class)
	public void testWriteReadMismatchedTypeIterable() throws H5ConnectorException {

		List<Number> nums = new ArrayList<Number>();
		nums.add(5.5);
		nums.add(1);
		checkMatrix(nums);
	}

	@Test
	public void testWriteReadIterableObjects() throws H5ConnectorException {

		final List<Integer> intGiven = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
		final int[] intExpected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		checkMatrix(intGiven, intExpected);

		final List<Double> doubleGiven = Arrays.asList(new Double[] { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10. });
		final double[] doubleExpected = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10. };
		checkMatrix(doubleGiven, doubleExpected);

		final List<String> stringGiven = Arrays
				.asList(new String[] { "Move", "over", "ham", "and", "quartered", "cow," });
		final String[] stringExpected = { "Move     ", "over     ", "ham      ", "and      ", "quartered",
				"cow,     " };
		checkMatrix(stringGiven, stringExpected);

		final Set<Double> doubleSetGiven = new TreeSet<>(
				Arrays.asList(new Double[] { 10., 9., 8., 7., 6., 5., 4., 3., 2., 1. }));
		final double[] doubleSetExpected = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10. };
		checkMatrix(doubleSetGiven, doubleSetExpected);

		final ConcurrentSkipListSet<Integer> intSkipListGiven = new ConcurrentSkipListSet<>(
				Arrays.asList(new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }));
		
		final int[] intSkipListExpected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		checkMatrix(intSkipListGiven, intSkipListExpected);

	}

	@Test(expected = H5ConnectorException.class)
	public void testWriteReadEmptyMatrixThrowsException() throws H5ConnectorException {

		final boolean[] booleanEmpty1d = new boolean[0];
		final byte[] byteEmpty1d = new byte[0];
		final char[] charEmpty1d = new char[0];
		final short[] shortEmpty1d = new short[0];
		final int[] intEmpty1d = new int[0];
		final long[] longEmpty1d = new long[0];
		final float[] floatEmpty1d = new float[0];
		final double[] doubleEmpty1d = new double[0];

		final Object[] emptyArraysArray = { booleanEmpty1d, byteEmpty1d, charEmpty1d, shortEmpty1d, intEmpty1d,
				longEmpty1d, floatEmpty1d, doubleEmpty1d };

		writer.writeHDF5File(testFileName, emptyArraysArray);

	}

	@Test
	public void testWriteReadEmptyMatrix() throws TypeException, H5ConnectorException {
		checkMatrix(new EmptyMatrix());
	}

	@Test
	public void testWriteReadbooleanMatrix() throws H5ConnectorException {
		final boolean[][][] boolean3dMatrix = { { { true, false }, { true, false }, { true, false } },
				{ { false, true }, { false, true }, { false, true } } };
		checkMatrix(boolean3dMatrix);
	}

	@Test
	public void testWriteReadBooleanMatrix() throws H5ConnectorException {
		final boolean[][][] boolean3dMatrix = { { { true, false }, { true, false }, { true, false } },
				{ { false, true }, { false, true }, { false, true } } };
		final Boolean[][][] Boolean3dMatrix = { { { true, false }, { true, false }, { true, false } },
				{ { false, true }, { false, true }, { false, true } } };
		checkMatrix(Boolean3dMatrix, boolean3dMatrix);
	}

	@Test
	public void testWriteReadbyteMatrix() throws H5ConnectorException {
		final byte[][][] byte3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(byte3dMatrix);
	}

	@Test
	public void testWriteReadByteMatrix() throws H5ConnectorException {
		final byte[][][] byte3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Byte[][][] Byte3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(Byte3dMatrix, byte3dMatrix);
	}

	@Test
	public void testWriteReadCharMatrix() throws H5ConnectorException {
		final char[][][] char3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(char3dMatrix);

		final char[][][][] char4dMatrix = {
				{ { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } }, { { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
						{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } } };
		checkMatrix(char4dMatrix);
	}

	@Test
	public void testWriteReadCharacterMatrix() throws H5ConnectorException {
		final char[][][] char3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Character[][][] Character3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(Character3dMatrix, char3dMatrix);
	}

	@Test
	public void testWriteReadshortMatrix() throws H5ConnectorException {
		final short[][][] short3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(short3dMatrix);
	}

	@Test
	public void testWriteReadShortMatrix() throws H5ConnectorException {
		final short[][][] short3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Short[][][] Short3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(Short3dMatrix, short3dMatrix);
	}

	@Test
	public void testWriteReadIntMatrix() throws H5ConnectorException {

		final int[] int1done = { 1 };
		final int[][] int2done = { { 1 } };
		final int[][][] int3done = { { { 1 } } };
		final int[][][][] int4done = { { { { 1 } } } };

		checkMatrix(int1done);
		checkMatrix(int2done);
		checkMatrix(int3done);
		checkMatrix(int4done);

		final int[][][] int3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(int3dMatrix);
	}

	@Test
	public void testWriteReadIntegerMatrix() throws H5ConnectorException {
		final int[][][] int3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Integer[][][] Integer3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(Integer3dMatrix, int3dMatrix);
	}

	@Test
	public void testWriteReadlongMatrix() throws H5ConnectorException {
		final long[][][] long3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(long3dMatrix);
	}

	@Test
	public void testWriteReadLongMatrix() throws H5ConnectorException {
		final long[][][] long3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Long[][][] Long3dMatrix = { { { 1l, 2l, 3l }, { 4l, 5l, 6l }, { 7l, 8l, 9l } },
				{ { 10l, 11l, 12l }, { 13l, 14l, 15l }, { 16l, 17l, 18l } },
				{ { 19l, 20l, 21l }, { 22l, 23l, 24l }, { 25l, 26l, 27l } } };
		checkMatrix(Long3dMatrix, long3dMatrix);
	}

	@Test
	public void testWriteReadfloatMatrix() throws H5ConnectorException {
		final float[][][] float3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(float3dMatrix);
	}

	@Test
	public void testWriteReadFloatMatrix() throws H5ConnectorException {
		final float[][][] float3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Float[][][] Float3dMatrix = { { { 1f, 2f, 3f }, { 4f, 5f, 6f }, { 7f, 8f, 9f } },
				{ { 10f, 11f, 12f }, { 13f, 14f, 15f }, { 16f, 17f, 18f } },
				{ { 19f, 20f, 21f }, { 22f, 23f, 24f }, { 25f, 26f, 27f } } };
		checkMatrix(Float3dMatrix, float3dMatrix);
	}

	@Test
	public void testWriteReaddoubleMatrix() throws H5ConnectorException {
		final double[][][] double3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		checkMatrix(double3dMatrix);
	}

	@Test
	public void testWriteReadDoubleMatrix() throws H5ConnectorException {
		final double[][][] double3dMatrix = { { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } },
				{ { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } },
				{ { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } };
		final Double[][][] Double3dMatrix = { { { 1., 2., 3. }, { 4., 5., 6. }, { 7., 8., 9. } },
				{ { 10., 11., 12. }, { 13., 14., 15. }, { 16., 17., 18. } },
				{ { 19., 20., 21. }, { 22., 23., 24. }, { 25., 26., 27. } } };
		checkMatrix(Double3dMatrix, double3dMatrix);
	}

	@Test(expected = H5ConnectorException.class)
	public void testInvalidFile() throws H5ConnectorException {
		writer.writeHDF5File("", new Object[] { 1, 2, 4 });
	}

	@Test
	public void testWriteReadNamedVariables() throws H5ConnectorException {

		String[] labels = new String[] { "int", "double" };
		Object[] args = new Object[] { 1, 2.0 };
		writer.writeHDF5File(testFileName, labels, args);

		List<NameValuePair> pairs = reader.readHDF5File(new File(testFileName), labels);

		for (int i = 0; i < labels.length; i++) {
			assertEquals(labels[i], pairs.get(i).getName());
			assertEquals(args[i], pairs.get(i).getValue());
		}
	}

	private void checkMatrix(Object matrix) throws H5ConnectorException {
		checkMatrix(matrix, matrix);
	}

	private void checkMatrix(Object matrix, Object expectedMatrix) throws H5ConnectorException {
		Object[] params = { matrix };
		writer.writeHDF5File(testFileName, params);
		List<NameValuePair> result = reader.readHDF5File(new File(testFileName));
		Object[] expectedParams = { expectedMatrix };
		checkNameValuePairs(listOfNames("p", 0, 0), Arrays.asList(expectedParams), result);
	}
}
