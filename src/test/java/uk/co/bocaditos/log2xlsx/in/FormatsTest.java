package uk.co.bocaditos.log2xlsx.in;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogSet;


/**
 * JUnit tests for class Formats.
 */
public class FormatsTest {

	private static final String[] lines = {
			// [20241223 10:11:10.203] {app1} [uk.co.bocaditos.app1.Test1] - (id-1) 
			"[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \\{{computer}\\} [{class}] - ({id}) {request, enum, REQUEST_RECEIVED, SENT_REQUEST, REQUEST_RESPONSE, SENT_RESPONSE} \\{\"headers\": {headers}, \"payload\": {body}\\} - END",
			"[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \\{{computer}\\} [{class}] - ({id}) {request, enum, REQUEST_RECEIVED, SENT_REQUEST, REQUEST_RESPONSE, SENT_RESPONSE} {message} - END",
			"[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \\{{computer}\\} [{class}] - ({id}) {message}"
		};


	@Test
	public void processTest() throws FormatException {
		final LogSet set = load();
		final FieldsSet fields = load(set);

		assertNotNull(fields);
		assertEquals(2, fields.size());
	}

	@Test
	public void loadLinesTest() throws FormatException {
		final String[][] formatLines = {
				// Success
				FormatsTest.lines,
				new String[] {lines[0], null, "", lines[1]},

				// Failed
				null,
				new String[] {}
			};

		for (int index = 0; index < formatLines.length; ++index) {
			final String[] formats = formatLines[index];

			try {
				final LogSet set = Formats.load(formats);

				assertTrue(index < 2);
				assertNotNull(set);
			} catch (final FormatException fe) {
				assertTrue(index > 1);
				assertEquals("Missing format lines", fe.getMessage());
			}
		}
	}

	@Test
	public void loadFileTest() {
		final String[] filenames = {
				"src/test/resources/formats.txt",
				null,
				"",
				".invalid_"
			};

		for (int index = 0; index < filenames.length; ++index) {
			final String filename = filenames[index];
			final String desc = "Index " + index;

			try {
				final LogSet set = Formats.loadFiles(filename);
	
				assertEquals(desc, 0, index);
				assertNotNull(desc, set);
			} catch (final FormatException fe) {
				assertNotEquals(0, index);
				if (index <= 2) {
					assertEquals(desc, "Missing the format file name", fe.getMessage());
				} else {
					assertTrue(desc + " - " + fe.getMessage(), 
							fe.getMessage().startsWith("Failed reading file \""));
				}
			}
		}
	}

	@Test
	public void loadAllTest() throws FormatException {
		final String[][] allFiles = {
				{"src/test/resources/formats.txt"},
				{"formats.txt"}
			};

		for (int index = 0; index < allFiles.length; ++index) {
			final String[] files = allFiles[index];

			try {
				final LogSet set = Formats.loadAll(files);

				assertEquals(0, index);
				assertNotNull(set);
				assertThrows(FormatException.class, new ThrowingRunnable() {
		
					@Override
					public void run() throws Throwable {
						Formats.loadFile(set, (File) null);
					}
					
				});
				assertThrows(FormatException.class, new ThrowingRunnable() {
		
					@Override
					public void run() throws Throwable {
						Formats.loadAll((String[]) null);
					}
					
				});
				assertThrows(FormatException.class, new ThrowingRunnable() {
		
					@Override
					public void run() throws Throwable {
						Formats.loadFiles((String[]) null);
					}
					
				});
				assertThrows(FormatException.class, new ThrowingRunnable() {
		
					@Override
					public void run() throws Throwable {
						Formats.process(null, null, set, (String[]) null);
					}
					
				});
			} catch (final FormatException fe) {
				assertEquals(1, index);
			}
		}
	}

	public static LogSet load() throws FormatException {
		return Formats.load(FormatsTest.lines);
	}

	public static FieldsSet load(final LogSet set) throws FormatException {
		return load("id", set);
	}

	public static FieldsSet load(final String idFieldName, final LogSet set) 
			throws FormatException {
		return Formats.process(null, idFieldName, set, 
				"src/test/resources/logs/app1.log", "src/test/resources/logs/app2.log");
	}

} // end class FormatsTest
