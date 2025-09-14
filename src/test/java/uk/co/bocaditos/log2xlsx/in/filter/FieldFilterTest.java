package uk.co.bocaditos.log2xlsx.in.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import uk.co.bocaditos.log2xlsx.in.Formats;
import uk.co.bocaditos.log2xlsx.model.FieldsLine;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;


/**
 * JUnit tests for class FieldFilter.
 */
public class FieldFilterTest {

	@Test
	public void valid_Test() {
		final FieldFilter<String> filter = new FieldFilter<>((LogField) null, (String) null) {
			};

		assertTrue(filter.valid_(null));
	}

	@Test
	public void buildTest() throws UtilsException {
		final String[] format = {
				// [20241223 10:11:10.203] {app1} [uk.co.bocaditos.app1.Test1] - (id-1) 
				"[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] [{date, date, f:yyyyMMdd}] "
				+ "[{str, string, p:^\\[a-zA-Z\\]\\{1\\,30\\}$}] [{int, int}] [{long, long}] " 
				+ "[{char, char}] [{double, double}] [{float, float}] [{boolean, boolean}] " 
				+ "[{enum, enum, FIRST, SECOD, THIRD}]"
			};
		//			
		final String LINE 
				= "[20240101 12:01:00.001] [20240101] [value] [10] [100] [c] [10.1] [10.1] [true] [THIRD]";
		final boolean[] valids = {
				// Datetime
				true, true, true, false, false, false,	// datetime
				true, true, true, false, false, false,	// date
				true, false, false,						// string
				true, true, true, true, false, false, 	// integer
				true, true, true, true, false, false, 	// long
				true, true, true, true, false, false, 	// double
				true, true, true, true, false, false, 	// flat
				true, false,							// boolean
				true, false								// character
			};
		final String[][] filterArgs = {
				// Datetime - 0 to 5
				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20231230 13:01:00.001", "20240121 15:01:00.001"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20240101 12:01:00.001"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20231230 13:01:00.001", "20240101 12:01:00.001"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20231230 13:01:00.001", "20240101 12:01:00.000"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20240101 12:01:00.002", "20250101 12:01:00.000"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "datetime", "20231230 13:01:00.001"},
				// Date - 6 to 11
				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20231230", "20240121"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20240101"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20231230", "20240101"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20231230", "20231231"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20240102", "20250101"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "date", "20231230"},
				// String - 12 to 14
				{CmdArgs.START + FieldFilter.ARG_FILTER, "str", "value"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "str", "sd1"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "str", "sdt"},
				// Integer - 15 to 20
				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "8", "45"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "10", "45"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "0", "10"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "10"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "0", "9"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "int", "11", "17"},
				// Long - 21 to 26
				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "80", "450"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "100", "450"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "80", "100"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "100"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "0", "99"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "long", "101", "450"},
				// Double - 27 to 32
				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "10", "50"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "10.1", "50"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "8", "10.1"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "10.1"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "0", "10"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "double", "10.2", "50"},
				// Float - 33 to 38
				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "10", "50"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "10.1", "50"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "8", "10.1"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "10.1"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "0", "10"},
				{CmdArgs.START + FieldFilter.ARG_FILTER, "float", "10.2", "50"},
				// Boolean - 39 to 36
				{CmdArgs.START + FieldFilter.ARG_FILTER, "boolean", "true"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "boolean", "false"},
				// Character - 37 - 38
				{CmdArgs.START + FieldFilter.ARG_FILTER, "char", "c"},

				{CmdArgs.START + FieldFilter.ARG_FILTER, "char", "l"}
		};
		final LogSet set = Formats.load(format);

		assertTrue(FieldFilter.build(null, set) instanceof AllFilter);
		for (int index = 0; index < valids.length; ++index) {
			final CmdArgs cmdArgs = new CmdArgs(filterArgs[index]);

			try {
				final FieldFilter<?> filter = FieldFilter.build(cmdArgs, set);
				final FieldsLine f = set.process(LINE);
		
				assertEquals("Index " + index, valids[index], filter.valid(f));
			} catch (final FormatException fe) {
				assertFalse("Index " + index, valids[index]);
			}
		}
	}

	@Test
	public void byteFilterTest() throws UtilsException {
		final LogField field = new LogField(null, "{byte, byte}", 1);
		ByteFilter filter = new ByteFilter(field, (byte) 10);

		assertNotNull(filter);
		filter = new ByteFilter(field, (byte) 10, (byte) 11);
		assertNotNull(filter);
	}

	@Test
	public void stringTest() {
		final LogField field = mock(LogField.class);

		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new StringFilter(field, null) {};
			}

		});
	}

	@Test
	public void dateTimeTest() throws FilterException {
		final LocalDateTime datetime = LocalDateTime.now();
		final LocalDate date = LocalDate.now();
		final LogField field = mock(LogField.class);
		LocalDateTimeFilter ldtFilter;
		LocalDateFilter ldFilter;

		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateTimeFilter(field, null) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateTimeFilter(field, datetime, null) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateTimeFilter(field, null, datetime) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateTimeFilter(field, datetime, 
						LocalDateTime.parse("2011-12-03T10:15:30")) {};
			}

		});
		ldtFilter = new LocalDateTimeFilter(field, datetime);
		assertFalse(ldtFilter.valid_(LocalDateTime.parse("2011-12-03T10:15:30")));
		assertNull(ldtFilter.to());

		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateFilter(field, null) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateFilter(field, date, null) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateFilter(field, null, date) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new LocalDateFilter(field, date, LocalDate.parse("2020-12-14")) {};
			}

		});
		ldFilter = new LocalDateFilter(field, date);
		assertTrue(ldFilter.valid_(date));
		assertTrue(ldFilter.valid_(date.plusMonths(1)));
		assertFalse(ldFilter.valid_(LocalDate.parse("2011-12-03")));
		assertNull(ldFilter.to());
	}

} // end class FieldFilterTest
