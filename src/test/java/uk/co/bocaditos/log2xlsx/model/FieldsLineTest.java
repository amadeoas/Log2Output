package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * JUnit tests for class FieldsLine.
 */
public class FieldsLineTest {

	@Test
	public void test() {
		final FieldsLine line = new FieldsLine();

		assertNull(line.get((String) null));
		assertNull(line.get("name"));

		assertFalse(line.equalsIt(null));
	}

} // end class FieldsLineTest
