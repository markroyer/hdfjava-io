package edu.umaine.cs.h5.octave;

import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.BYTE;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.CHAR;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.DOUBLE;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.FLOAT;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.INT;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.LONG;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.SHORT;
import static edu.umaine.cs.h5.octave.OctaveH5DataTypes.toH5;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.umaine.cs.h5.EmptyMatrix;
import edu.umaine.cs.h5.TypeException;
import ncsa.hdf.object.h5.H5Datatype;

public class OctaveH5DataTypesTest {

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
	public void testToH5() throws TypeException {

		// Boolean is stored in a double
		assertEquals(DOUBLE, toH5(boolean.class));
		assertEquals(DOUBLE, toH5(Boolean.class));
		// Stored as int 32 for arrays
		checkMatrix(INT, boolean.class);
		checkMatrix(INT, Boolean.class);

		assertEquals(BYTE, toH5(byte.class));
		assertEquals(BYTE, toH5(Byte.class));
		checkMatrix(BYTE, byte.class);
		checkMatrix(BYTE, Byte.class);

		assertEquals(CHAR, toH5(char.class));
		assertEquals(CHAR, toH5(Character.class));
		checkMatrix(CHAR, char.class);
		checkMatrix(CHAR, Character.class);

		assertEquals(SHORT, toH5(short.class));
		assertEquals(SHORT, toH5(Short.class));
		checkMatrix(SHORT, short.class);
		checkMatrix(SHORT, Short.class);

		assertEquals(INT, toH5(int.class));
		assertEquals(INT, toH5(Integer.class));
		checkMatrix(INT, int.class);
		checkMatrix(INT, Integer.class);

		assertEquals(LONG, toH5(long.class));
		assertEquals(LONG, toH5(Long.class));
		checkMatrix(LONG, long.class);
		checkMatrix(LONG, Long.class);

		assertEquals(FLOAT, toH5(float.class));
		assertEquals(FLOAT, toH5(Float.class));
		checkMatrix(FLOAT, float.class);
		checkMatrix(FLOAT, Float.class);

		assertEquals(DOUBLE, toH5(double.class));
		assertEquals(DOUBLE, toH5(Double.class));
		checkMatrix(DOUBLE, double.class);
		checkMatrix(DOUBLE, Double.class);

		assertEquals(CHAR, toH5(String.class));
		checkMatrix(CHAR, String.class);

		assertEquals(INT, toH5(EmptyMatrix.class));

	}

	@Test(expected = TypeException.class)
	public void testUnknownType() throws TypeException {
		toH5(Object.class);
	}

	private void checkMatrix(H5Datatype h5Type, Class<?> javaType) throws TypeException {
		assertEquals(h5Type, toH5(Array.newInstance(javaType, 1).getClass()));
		assertEquals(h5Type, toH5(Array.newInstance(javaType, 1, 2).getClass()));
		assertEquals(h5Type, toH5(Array.newInstance(javaType, 1, 2, 3).getClass()));
		assertEquals(h5Type, toH5(Array.newInstance(javaType, 1, 2, 3, 4).getClass()));
	}

}
