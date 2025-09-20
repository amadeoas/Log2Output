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

		assertEquals("value: 1", Utils.append(new StringBuilder(), "value", 1).toString());
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

	@Test
	public void appendTest() {
		final Word[] words = {new Word("FIRST"), new Word("SECOND")};

		assertEquals("words: [{FIRST}, {SECOND}]", 
				Utils.append(new StringBuilder(), "words", words).toString());
	}


	class Word implements ToString {

		private String value;


		Word(final String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(final StringBuilder buf) {
			return buf.append(this.value);
		}
		
	} // end class Word

} // end class UtilsTest
