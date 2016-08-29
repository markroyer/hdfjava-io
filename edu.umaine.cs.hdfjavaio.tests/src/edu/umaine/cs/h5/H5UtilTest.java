package edu.umaine.cs.h5;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class H5UtilTest {

	final long[] EMPTYARRAY = new long[0];

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDouble2DToDouble() {

		Double[] result = H5Util.double2DToDouble(new double[0][0]);

		assertEquals(0, result.length);

		double[][] oneToFour = new double[][] { { 1, 2 }, { 3, 4 } };

		result = H5Util.double2DToDouble(oneToFour);

		assertArrayEquals(new Double[] { 1., 2., 3., 4. }, result);

	}

	@Test
	public void testGetDimensions() {

		try {
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions(false));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions((byte) 1));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions((short) 1));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions(1));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions(1l));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions((float) 1.));
			assertArrayEquals(EMPTYARRAY, H5Util.getDimensions(1.));

			assertArrayEquals(new long[] { 1, 1 }, H5Util.getDimensions('a'));

			final String EMPTYSTR = "";
			final String HELLO = "Hello";
			assertArrayEquals(new long[] { EMPTYSTR.length(), 1 }, H5Util.getDimensions(EMPTYSTR));
			assertArrayEquals(new long[] { HELLO.length(), 1 }, H5Util.getDimensions(HELLO));

			final double[][] TWODIM = new double[3][3];
			assertArrayEquals(new long[] { 3, 3 }, H5Util.getDimensions(TWODIM));

			final double[][][] THREEDIM = new double[3][3][4];
			assertArrayEquals(new long[] { 3, 3, 4 }, H5Util.getDimensions(THREEDIM));

		} catch (TypeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@Test(expected=TypeException.class)
	public void testEmptyArrayTypeException() throws TypeException {
		H5Util.getDimensions(new double[0]);
	}

	@Test(expected=TypeException.class)
	public void testObjectTypeException() throws TypeException {
		H5Util.getDimensions(new Object());
	}
	
	
	@Test
	public void testIsPrimitiveWrapper() {

		assertTrue(H5Util.isPrimitiveWrapper(Boolean.class));
		assertTrue(H5Util.isPrimitiveWrapper(Byte.class));
		assertTrue(H5Util.isPrimitiveWrapper(Character.class));
		assertTrue(H5Util.isPrimitiveWrapper(Short.class));
		assertTrue(H5Util.isPrimitiveWrapper(Integer.class));
		assertTrue(H5Util.isPrimitiveWrapper(Long.class));
		assertTrue(H5Util.isPrimitiveWrapper(Float.class));
		assertTrue(H5Util.isPrimitiveWrapper(Double.class));

		assertFalse(H5Util.isPrimitiveWrapper(String.class));
		assertFalse(H5Util.isPrimitiveWrapper(Object.class));

	}

	@Test
	public void testGetDimensionsString() throws TypeException {

		String str = "Hello";
		assertArrayEquals(new long[] { str.length(), 1 }, H5Util.getDimensions(str));

	}

	@Test(expected = TypeException.class)
	public void testGetEmptyString() throws TypeException {
		H5Util.getLongestStr(new String[][] {}, 0);
	}

	@Test(expected = TypeException.class)
	public void testGetEmptyMatrix() throws TypeException {
		H5Util.byteArrayToChar(new char[][] {});
	}

	@Test(expected = TypeException.class)
	public void testStringArrayToByteNotStringMatrix() throws TypeException {
		H5Util.stringArrayToByte(new char[][] { { 'c' }, { 'b' } }, new long[] { 1, 1 });
	}
	
	@Test(expected = TypeException.class)
	public void testBooleanArrayToIntegersEmptyMatrix() throws TypeException {
		H5Util.booleanArrayToIntegers(new boolean[][] {});
	}
	
	@Test(expected = TypeException.class)
	public void testByteArrayToCharNonMatrix() throws TypeException {
		H5Util.byteArrayToChar(2);
	}

	@Test(expected = TypeException.class)
	public void testGetNonMatrix() throws TypeException {
		H5Util.getDimensions(new Object());
	}

	@Test
	public void testGetDimensionsStringMatrix() throws TypeException {

		String[] strArray = { "Hello", "World" };
		assertArrayEquals(new long[] { 2, strArray[0].length() }, H5Util.getDimensions(strArray));

		String[][] strArray2d = { { "Hello", "World" }, { "12345", "67890" }, { "ccccc", "ddddd" } };
		assertArrayEquals(new long[] { 3, 2, strArray2d[0][0].length() }, H5Util.getDimensions(strArray2d));

	}
}
