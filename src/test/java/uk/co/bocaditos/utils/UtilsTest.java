package uk.co.bocaditos.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
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
		assertEquals("first, second", Utils.concatenate("first", null, "second"));
	}

	@Test
	public void appendTest() {
		final Word[] words = {new Word("FIRST"), new Word("SECOND")};
		StringBuilder buf;

		assertEquals("words: [{FIRST}, {SECOND}]", 
				Utils.append(new StringBuilder(), "words", words).toString());
		buf = new StringBuilder();
		buf.append("\"name\"");
		assertEquals("\"name\", words: [{FIRST}, {SECOND}]", 
				Utils.append(buf, "words", words).toString());

		assertEquals("words: {FIRST}", 
				Utils.append(new StringBuilder(), "words", new Word("FIRST")).toString());
		assertEquals("", 
				Utils.append(new StringBuilder(), "words", (ToString) null).toString());

		assertEquals("", Utils.append(new StringBuilder(), "", "FIRST", "SECOND").toString());
		assertEquals("values: [\"FIRST\", \"SECOND\"]", 
				Utils.append(new StringBuilder(), "values", "FIRST", "SECOND").toString());
		buf = new StringBuilder();
		buf.append("\"name\"");
		assertEquals("\"name\", values: [\"FIRST\", \"SECOND\"]", 
				Utils.append(buf, "values", "FIRST", "SECOND").toString());
	}

	@Test
	public void objectMapperTest() {
		assertNotNull(Utils.objectMapper());
	}

	@Test
	public void readTest() {
		assertThrows(UtilsException.class, () -> {
				Utils.read("");
			});
		assertThrows(UtilsException.class, () -> {
			Utils.read("", ToString.class);
		});
	}

	@Test
	public void writeTest() {
		assertThrows(UtilsException.class, () -> {
			Utils.write("", "");
		});
	}

	@Test
	public void saveTest() throws UtilsException {
		assertEquals("null", Utils.save(null));
		assertThrows(UtilsException.class, () -> {
			Utils.save("", "");
		});
	}


	/**
	 * Implementation of ToString.
	 */
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
