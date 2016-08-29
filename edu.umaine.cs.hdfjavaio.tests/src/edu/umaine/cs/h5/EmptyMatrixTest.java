package edu.umaine.cs.h5;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.umaine.cs.h5.octave.TestUtil;

public class EmptyMatrixTest {

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
	public void testEmptyMatrix() throws TypeException {
		new EmptyMatrix(0);
		new EmptyMatrix(0, 1);
		new EmptyMatrix(1, 0);
		new EmptyMatrix(0, 0);
		new EmptyMatrix(5, 0);
		new EmptyMatrix(0, 5);
		new EmptyMatrix(0, 0, 0);
		new EmptyMatrix(0, 10, 0);
		new EmptyMatrix(0, 0, 10);
	}

	@Test
	public void testGetDimensions() throws TypeException {

		EmptyMatrix em = new EmptyMatrix();
		assertArrayEquals(new int[] { 0, 0 }, em.getDimensions());
		
		em = new EmptyMatrix(0);
		assertArrayEquals(new int[] { 0, 0 }, em.getDimensions());

		em = new EmptyMatrix(0, 10);
		assertArrayEquals(new int[] { 0, 10 }, em.getDimensions());

		em = new EmptyMatrix(0, 10, 0, 4);
		assertArrayEquals(new int[] { 0, 10, 0, 4 }, em.getDimensions());
	}
	
	@Test
	public void testEquals() throws TypeException {
		
		EmptyMatrix em1 = new EmptyMatrix(0,4,5);
		EmptyMatrix em2 = new EmptyMatrix(0,4,5);
		assertEquals(em1, em2);
		
		em1 = new EmptyMatrix();
		em2 = new EmptyMatrix();
		assertEquals(em1, em2);
		
		em2 = new EmptyMatrix(0);
		assertEquals(em1, em2);
		
		em2 = new EmptyMatrix(0,0);
		assertEquals(em1, em2);
		
		em2 = new EmptyMatrix(0,1);
		assertFalse(em1.equals(em2));
		
		assertFalse(em1.equals(5));
		assertFalse(em2.equals(new Object()));
		
	}
	
	@Test
	public void testZeroDimensionEmptyMatrix() throws TypeException {
		final int[] EMPTY_DIMS = new int[]{0,0};
		
		TestUtil.assertEqualArray(EMPTY_DIMS, new EmptyMatrix().getDimensions());
		
		TestUtil.assertEqualArray(EMPTY_DIMS, new EmptyMatrix(new int[0]).getDimensions());
		
		TestUtil.assertEqualArray(EMPTY_DIMS, new EmptyMatrix(0).getDimensions());
		
		TestUtil.assertEqualArray(EMPTY_DIMS, new EmptyMatrix(0,0).getDimensions());
	}
	
	@Test(expected=TypeException.class)
	public void testNoZeroLengthDimension() throws TypeException {
		new EmptyMatrix(3,4);
	}

}
