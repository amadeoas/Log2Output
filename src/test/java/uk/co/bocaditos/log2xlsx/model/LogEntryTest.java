package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * JUnits for class LogEntry.
 */
public class LogEntryTest {

	@Test
	public void test() throws FormatException {
		final String ID = "id";
		LogEntry entry = new LogEntry(null, ID);

		assertEquals(ID, entry.getId());
		assertNull(entry.getParent());

		assertThrows(FormatException.class, () -> {
			new LogEntry(null, "}aaa}", 1);
		});
	}

} // end class LogEntryTest
