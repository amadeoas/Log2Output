package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;

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

		entry.setNexts(new ArrayList<>(0));
		assertEquals(1, entry.compareTo(null));
		assertThrows(FormatException.class, () -> {
			entry.loadNexts_(0, "A line", "");
		});

		assertEquals("value", LogEntry.getId("value"));
		assertEquals("value", LogEntry.getId("value", 0));
		assertThrows(FormatException.class, () -> {
				LogEntry.getId((String) null, 0);
			});
		assertThrows(FormatException.class, () -> {
				LogEntry.getId((String) null, -1);
			});
		assertThrows(FormatException.class, () -> {
				LogEntry.getId(" value", 1);
			});
		assertEquals("value", LogEntry.getId_("value" + LogField.START, 0));
		assertEquals("value", LogEntry.getId_("value", 0));
	}

} // end class LogEntryTest
