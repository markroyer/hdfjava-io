/**
 * 
 */
package edu.umaine.cs.h5;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mark Royer
 *
 */
public class NameValuePairTest {

	private NameValuePair nvp;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		nvp = new NameValuePair("test", 10);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.umaine.cs.h5.NameValuePair#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("test", nvp.getName());
	}

	/**
	 * Test method for {@link edu.umaine.cs.h5.NameValuePair#getValue()}.
	 */
	@Test
	public void testGetValue() {
		assertEquals(10, nvp.getValue());
	}

	/**
	 * Test method for {@link edu.umaine.cs.h5.NameValuePair#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("{ \"test\" : 10 }", nvp.toString());

	}

}
