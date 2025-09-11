package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * JUnits for class LogEntry.
 */
public class LogEntryTest {

	@Test
	public void test() {
		final String ID = "id";
		final LogEntry entry = new LogEntry(null, ID);

		assertEquals(ID, entry.getId());
		assertNull(entry.getParent());
	}

} // end class LogEntryTest
