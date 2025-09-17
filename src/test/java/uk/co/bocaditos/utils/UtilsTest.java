package uk.co.bocaditos.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * JUnit tests for class Utils.
 */
public class UtilsTest {

	@Test
	public void test() {
		assertFalse(Utils.allToUpperCase(null));
		assertFalse(Utils.allToUpperCase("LLLL kK"));
		assertTrue(Utils.allToUpperCase("LLLL KK"));

		assertFalse(Utils.allToLowerCase(null));
		assertFalse(Utils.allToLowerCase("llll Kk"));
		assertTrue(Utils.allToLowerCase("llll kk"));
	}

} // end class UtilsTest
