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
public class H5ConnectorExceptionTest {

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
	 * Test method for {@link edu.umaine.cs.h5.H5Exception#H5ConnectorException(java.lang.String)}.
	 */
	@Test
	public void testH5ConnectorExceptionString() {
	
		String msg = "Failure!";
		H5Exception e = new H5Exception(msg);
		
		assertEquals(msg, e.getMessage());
	}

	/**
	 * Test method for {@link edu.umaine.cs.h5.H5Exception#H5ConnectorException(java.lang.Exception)}.
	 */
	@Test
	public void testH5ConnectorExceptionException() {
		
		IllegalArgumentException e1 = new IllegalArgumentException();
		H5Exception e = new H5Exception(e1);
		
		assertEquals(e1, e.getCause());
		
	}

}
