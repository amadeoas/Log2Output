package uk.co.bocaditos.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

	@Test
	public void concatenateTest() {
		assertNull(Utils.concatenate((String) null));
		assertEquals("", Utils.concatenate((String[]) null));
		assertEquals("", Utils.concatenate(new String[] {}));
		assertEquals("", Utils.concatenate(new String[0]));
		assertEquals("first", Utils.concatenate("first"));
		assertEquals("first, second", Utils.concatenate("first", "second"));
	}

} // end class UtilsTest
