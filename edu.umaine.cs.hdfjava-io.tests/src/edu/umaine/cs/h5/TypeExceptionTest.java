/**
 * 
 */
package edu.umaine.cs.h5;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mark Royer
 *
 */
public class TypeExceptionTest {

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
	 * {@link edu.umaine.cs.h5.TypeException#TypeException(java.lang.Class)}.
	 */
	@Test
	public void testTypeExceptionClassOfQ() {

		TypeException e = new TypeException(int.class);
		assertEquals(int.class.getName(), e.getType());

		e = new TypeException(Object.class);
		assertEquals(Object.class.getName(), e.getType());
		
		e = new TypeException("Unknown type");
		assertEquals(null, e.getType());

	}

	/**
	 * Test method for
	 * {@link edu.umaine.cs.h5.TypeException#TypeException(java.lang.Class, java.lang.String)}
	 * .
	 */
	@Test
	public void testTypeExceptionClassOfQString() {
		
		String message = "Unknown type";
		TypeException e = new TypeException(message);
		assertEquals(message, e.getMessage());
		
		e = new TypeException(Object.class, message);
		assertEquals(message + " Type was java.lang.Object", e.getMessage());
		assertEquals(Object.class.getName(), e.getType());
		
	}

}
