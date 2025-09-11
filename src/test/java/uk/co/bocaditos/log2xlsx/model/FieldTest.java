package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDateTime;

import org.junit.Test;


/**
 * JUnit tests for class Field,
 */
public class FieldTest {

	@Test
	public void getFieldClassTest() throws FormatException {
		LogField lf = new LogField(null, "{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}", 1);
		Field field = new Field(lf, "20241223 10:11:10.209");

		assertEquals(LocalDateTime.class, field.getFieldClass());
		assertEquals(1793702841, field.hashCode());
		assertFalse(field.equals(null));
		assertFalse(field.equals(""));
		assertEquals(field, field);
		assertEquals("id: \"datetime\", value: \"2024-12-23T10:11:10.209\"", field.toString());

		lf = new LogField(null, "{date, date, f:yyyyMMdd}", 1);
		field = new Field(lf, "20241223");
		assertEquals("id: \"date\", value: \"2024-12-23\"", field.toString());
	}

} // end class FieldTest
