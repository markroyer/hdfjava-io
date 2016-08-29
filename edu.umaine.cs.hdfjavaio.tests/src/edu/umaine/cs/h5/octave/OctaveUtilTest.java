/**
 * 
 */
package edu.umaine.cs.h5.octave;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.TypeException;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * @author Mark Royer
 *
 */
public class OctaveUtilTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link edu.umaine.cs.h5.octave.OctaveUtil#getOctaveType(java.lang.Class)}
	 * .
	 * 
	 * @throws TypeException
	 */
	@Test
	public void testGetOctaveType() throws TypeException {

		assertEquals("bool", OctaveUtil.getOctaveType(boolean.class));
		assertEquals("bool", OctaveUtil.getOctaveType(Boolean.class));

		assertEquals("int8 scalar", OctaveUtil.getOctaveType(byte.class));
		assertEquals("int8 scalar", OctaveUtil.getOctaveType(Byte.class));

		assertEquals("sq_string", OctaveUtil.getOctaveType(char.class));
		assertEquals("sq_string", OctaveUtil.getOctaveType(Character.class));

		assertEquals("int16 scalar", OctaveUtil.getOctaveType(short.class));
		assertEquals("int16 scalar", OctaveUtil.getOctaveType(Short.class));

		assertEquals("int32 scalar", OctaveUtil.getOctaveType(int.class));
		assertEquals("int32 scalar", OctaveUtil.getOctaveType(Integer.class));

		assertEquals("int64 scalar", OctaveUtil.getOctaveType(long.class));
		assertEquals("int64 scalar", OctaveUtil.getOctaveType(Long.class));

		assertEquals("float scalar", OctaveUtil.getOctaveType(float.class));
		assertEquals("float scalar", OctaveUtil.getOctaveType(Float.class));

		assertEquals("scalar", OctaveUtil.getOctaveType(double.class));
		assertEquals("scalar", OctaveUtil.getOctaveType(Double.class));

		assertEquals("string", OctaveUtil.getOctaveType(String.class));

		// Arrays...
		testMatrix("bool matrix", boolean.class);
		testMatrix("bool matrix", Boolean.class);
		
		testMatrix("int8 matrix", byte.class);
		testMatrix("int8 matrix", Byte.class);

		testMatrix("sq_string", char.class);
		testMatrix("sq_string", Character.class);
		
		testMatrix("int16 matrix", short.class);
		testMatrix("int16 matrix", Short.class);
		
		testMatrix("int32 matrix", int.class);
		testMatrix("int32 matrix", Integer.class);
		
		testMatrix("int64 matrix", long.class);
		testMatrix("int64 matrix", Long.class);
		
		testMatrix("float matrix", float.class);
		testMatrix("float matrix", Float.class);
		
		testMatrix("matrix", double.class);
		testMatrix("matrix", Double.class);
		
		testMatrix("string", String.class);
		
		assertEquals("matrix",  OctaveUtil.getOctaveType(EmptyMatrix.class));
	}

	private void testMatrix(String expectedType, Class<?> arrayComponent) throws TypeException {
		assertEquals(expectedType, OctaveUtil.getOctaveType(Array.newInstance(arrayComponent, 5).getClass()));
		assertEquals(expectedType, OctaveUtil.getOctaveType(Array.newInstance(arrayComponent, 5, 5).getClass()));
		assertEquals(expectedType, OctaveUtil.getOctaveType(Array.newInstance(arrayComponent, 5, 5, 5).getClass()));
		assertEquals(expectedType, OctaveUtil.getOctaveType(Array.newInstance(arrayComponent, 5, 5, 5, 5).getClass()));
	}
	
	@Test(expected=TypeException.class)
	public void testUnknownOctaveType() throws TypeException {
		OctaveUtil.getOctaveType(Object.class);
	}

	/**
	 * Test method for
	 * {@link edu.umaine.cs.h5.octave.OctaveUtil#getJavaValue(java.lang.String, ncsa.hdf.object.HObject)}
	 * .
	 * @throws Exception 
	 */
	@Test(expected=TypeException.class)
	public void testGetJavaValue() throws Exception {
		OctaveUtil.getJavaValue("????", new H5ScalarDS(new H5File(), "theName", "/thePath"));
	}

}
