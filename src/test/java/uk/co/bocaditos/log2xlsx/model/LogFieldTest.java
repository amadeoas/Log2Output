package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;


/**
 * JUnits tests for class LogField.
 */
public class LogFieldTest {

	private final String ID = "id";


	@Test
	public void test() {
		assertThrows(FormatException.class, () -> {
				LogField.getId("", -1);
			});
		assertThrows(FormatException.class, () -> {
			LogField.getId(" ", 1);
		});
		assertThrows(FormatException.class, () -> {
			LogField.getId(" ", 0);
		});
		assertThrows(FormatException.class, () -> {
			LogField.getId_(LogField.START + "value", 1);
		});
		
		assertNull(LogField.split(null, ""));
	}

	@Test
	public void buildTest() throws FormatException {
		final String PATTERN = "^[a-z]{1,10}$";
		final String[] values = {"abz", "", "a1", "aaaaaaaaaaa"};
		LogField field = new LogField(null, ID, String.class, null, PATTERN);
		final LogEntry entry;

		assertEquals(ID, field.getHeaderName());
		assertNull(field.getFormat());
		assertEquals(PATTERN, field.getPattern().toString());
		assertEquals("id: \"" + ID + "\", numNexts: 0, pattern: \"" + PATTERN + '"', 
				field.toString());

		entry = field.build("- END", 0);
		assertNotNull(entry);

		for (final String value : values) {
			try {
				assertEquals(value, field.build(value));
				assertEquals(values[0], value);
			} catch (final FormatException fe) {
				assertNotEquals(values[0], value);
			}
		}
		numTests();
		charTests();
		enumTests();
		field = new LogField(null, ID, LocalDateTime.class, "yyyyMMdd HH:mm:ss.SSS", null);
		assertNotNull(field.build("20241223 10:11:10.203"));
		assertEquals("id: \"" + ID + "\", numNexts: 0, format: " 
				+ "\"Value(YearOfEra,4,19,EXCEEDS_PAD)Value(MonthOfYear,2)Value(DayOfMonth,2)' " 
				+ "'Value(HourOfDay,2)':'Value(MinuteOfHour,2)':'Value(SecondOfMinute,2)'." 
				+ "'Fraction(NanoOfSecond,3,3)\"", 
				field.toString());
		field = new LogField(null, ID, LocalDateTime.class, null, null);
		assertEquals(LocalDateTime.of(2024, 12, 23, 10, 11, 10, 203000000), 
				field.build("2024-12-23T10:11:10.203"));
		field = new LogField(null, ID, LocalDate.class, "yyyyMMdd", null);
		assertEquals(LocalDate.of(2024, 12, 23), field.build("20241223"));
		field = new LogField(null, ID, LocalDate.class, null, null);
		assertEquals(LocalDate.of(2024, 12, 23), field.build("2024-12-23"));
		classEnumTests();
		// Boolean
		field = new LogField(null, ID, Boolean.class, null, null);
		assertEquals(Boolean.TRUE, field.build(""  + Boolean.TRUE));
		assertEquals(Boolean.FALSE, field.build(""  + Boolean.FALSE));
		assertEquals(Boolean.FALSE, field.build("yes"));
		assertEquals(Boolean.FALSE, field.build("no"));
		jsonTests();

		final LogField field1 = new LogField(null, ID, Boolean.class, null, null);

		assertThrows(FormatException.class, () -> {
				field1.set_("value, enum");
			});
		field1.set_("value, enum, uk.co.bocaditos.log2xlsx.model.Level");
		assertThrows(FormatException.class, () -> {
				field1.set_("value, class:uk.co.bocaditos.log2xlsx.model.noTing");
			});
		assertNotNull(field1.set_("value, class:uk.co.bocaditos.log2xlsx.model.LogFieldTest"));
	}

	@Test
	public void getValueTest() throws FormatException {
		final LogField field = new LogField(null, "ID", LocalDate.class, "yyyyMMdd", null);
		final LocalDate now = LocalDate.now();

		field.add("} Testing {", 1);
		assertNull(field.getValue((Object) null));
		assertNotNull(field.getValue(now));
		assertNull(field.getField(""));
		assertNotNull(new LogField(null, "ID", String.class, "yyyyMMdd", null));

		assertEquals("1.22", new LogField(null, "ID", String.class, "%.2f", null).getValue(1.222));
		assertThrows(FormatException.class, () -> {
				new LogField(null, "ID", String.class, "%.2f", null).getValue("text");
			});
		assertThrows(FormatException.class, () -> {
				new LogField(null, "ID", String.class, "%.2f", null).getValue("text");
			});
	}

	@Test
	public void verifyTest() {
		final LogField field = new LogField(null, "ID", LocalDate.class, "yyyyMMdd", null);

		assertThrows(FormatException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				field.verify("{int, int}", 1);
			}
			
		});
		assertThrows(FormatException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				field.verify("{ID, int}", 1);
			}
			
		});
		assertThrows(FormatException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				field.verify("{ID, date, p:^\\[A-Z\\]\\{1\\,30\\}}", 1);
			}
			
		});

		final LogField field1 = new LogField(null, "ID", LocalDate.class, null, null);

		assertThrows(FormatException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				field1.verify("{ID, date, f:yyyyMMdd}", 1);
			}
			
		});
	}

	private void numTests() throws FormatException {
		LogField field;

		field = new LogField(null, ID, int.class, null, null);
		assertEquals(-20, field.build("-20"));
		field = new LogField(null, ID, long.class, null, null);
		assertEquals(-20l, field.build("-20"));
		field = new LogField(null, ID, float.class, null, null);
		assertEquals(-20f, field.build("-20"));
		field = new LogField(null, ID, double.class, null, null);
		assertEquals(-20d, field.build("-20"));

		final LogField f = new LogField(null, ID, int.class, null, null);

		assertThrows(FormatException.class, () -> {
			f.build("12.1");
		});
	}

	private void charTests() throws FormatException {
		final LogField field = new LogField(null, ID, char.class, null, null);

		assertEquals('c', field.build("c"));
		assertThrows(FormatException.class, () -> {
				field.build("more");
			});
	}

	private void enumTests() throws FormatException {
		final LogField field = new LogField(null, "{" + ID + ", enum, FIRST, SECOND}", 1);

		assertEquals("SECOND", field.build("SECOND"));
		assertThrows(FormatException.class, () -> {
				field.build("THIRD");
			});
	}

	private void classEnumTests() throws FormatException {
		final LogField field = new LogField(null, ID, Level.class, null, null);

		assertEquals(Level.WARN, field.build(Level.WARN.name()));
		assertEquals("id: \"" + ID + "\", numNexts: 0, enum: [\"ERROR\", \"WARN\", \"INFO\", \"DEBUG\"]", 
				field.toString());
		assertThrows(FormatException.class, () -> {
				field.build(".LOWEST.");
			});
	}

	private void jsonTests() throws FormatException {
		final String VALUE = "something";
		final NameValue nv = new NameValue();
		final LogField field = new LogField(null, ID, NameValue.class, null, null);

		nv.setName(ID);
		nv.setValue(VALUE);
		assertEquals(nv, field.build("{\"name\": \"" + ID + "\", \"value\": \"" + VALUE + "\"}"));
		assertThrows(FormatException.class, () -> {
				field.build(VALUE);
			});
	}

} // end class LogFieldTest


enum Level {
	ERROR,
	WARN,
	INFO,
	DEBUG;
	
} // end enum Level
