/**
 * 
 */
package edu.umaine.cs.h5.octave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.umaine.cs.h5.NameValuePair;

/**
 * JUnit utility methods.
 * 
 * @author Mark Royer
 *
 */
public class TestUtil {

	public static void checkNameValuePairs(List<String> expectedNames, List<Object> expectedValues,
			List<NameValuePair> actualNameValuePairs) {

		if (expectedNames.size() != expectedValues.size() || expectedNames.size() != actualNameValuePairs.size())
			fail("Lists differ in size.");

		for (int i = 0; i < expectedNames.size(); i++) {
			assertEquals(expectedNames.get(i), actualNameValuePairs.get(i).getName());
			if (expectedValues.get(i).getClass().isArray())
				assertEqualArray(expectedValues.get(i), actualNameValuePairs.get(i).getValue());
			else
				assertEquals(expectedValues.get(i), actualNameValuePairs.get(i).getValue());
		}

	}

	public static void assertEqualArray(Object expectedValues, Object actualValues) {

		Class<?> evType = expectedValues.getClass();
		Class<?> avType = actualValues.getClass();

		if (!evType.isArray() || !avType.isArray())
			fail("Both types must be array, but expected is " + evType.getName() + " and actual is "
					+ avType.getName());

		assertEquals(evType, avType);

		int eLength = Array.getLength(expectedValues);

		assertEquals(eLength, Array.getLength(actualValues));

		for (int i = 0; i < eLength; i++) {
			Object o = Array.get(expectedValues, i);
			if (o.getClass().isArray())
				assertEqualArray(o, Array.get(actualValues, i));
			else
				assertEquals(o, Array.get(actualValues, i));
		}
	}

	public static List<String> listOfNames(String prefix, int start, int end) {
		List<String> result = new ArrayList<>(end - start + 1);
		for (int i = start; i <= end; i++) {
			result.add(prefix + i);
		}

		return result;
	}

}
