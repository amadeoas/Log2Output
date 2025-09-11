package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

import uk.co.bocaditos.log2xlsx.in.FormatsTest;


/**
 * JUnit tests for class LogSet.
 */
public class LogSetTest {

	private static final Link link = loadLinks();


	@Test
	public void test() throws FormatException {
		final LogSet set = FormatsTest.load();

		LogSetTest.link.equals(set);
	}

	@Test
	public void loadTest() throws FormatException {
		final LogSet set = new LogSet();
		LogEntry entry;

		assertEquals(0, set.getNexts().size());
		set.load(null);
		assertEquals(0, set.getNexts().size());
		set.load("");
		assertEquals(0, set.getNexts().size());
		set.load("{name}, {datebirth}");
		assertEquals(1, set.getNexts().size());
		entry = set.getNexts().get(0);
		assertEquals(1, entry.getNexts().size());
		entry = entry.getNexts().get(0);
		assertEquals(1, entry.getNexts().size());
		entry = entry.getNexts().get(0);
		assertEquals(0, entry.getNexts().size());
	}

	@Test
	public void processTest() throws FormatException, FileNotFoundException, IOException {
		final LogSet set = FormatsTest.load();
		final String[] logs = {"app1", "app2"};
		final int[][] sizes = {
				{7, 5, 5, 5, 7, 5, 7, 5, 5, 5, 7},
				{7, 5, 5, 5, 7, 5, 7, 5, 5, 5, 7}
			};

		for (int logIndex = 0; logIndex < logs.length; ++logIndex) {
			final String filename = logs[logIndex];
			final String id = "id-" + (logIndex + 1);

			try (final BufferedReader in = new BufferedReader(new FileReader("src/test/resources/logs/" + filename + ".log", StandardCharsets.UTF_8))) {
				int index = 0;
				String line;

				while ((line = in.readLine()) != null) {
					final FieldsLine row = set.process(line);

					if (row == null) {
						continue;
					}
					assertFalse("Log index " + logIndex + ", line index " + index, row.isEmpty());
					assertEquals("Log index " + logIndex + ", line index " + index, 
							sizes[logIndex][index++], row.size());
					assertEquals("Log index " + logIndex + ", line index " + index, 
							logs[logIndex], row.get("computer"));
					assertEquals("Log index " + logIndex + ", line index " + index, 
							id, row.get("id"));
				}
			}
		}
	}

	private static Link loadLinks() {
		final Link link = new Link("");
		// Common part
		final Link l = link
				.addNext("[").addNext("datetime", LocalDateTime.class)
				.addNext("] [\\{").addNext("computer", String.class)
				.addNext("\\}] [").addNext("class", String.class)
				.addNext("] [").addNext("id", String.class);

		// Not common part
		l.addNext("] - ").add("message", String.class);
		l.addNext("] - [").addNext("request", new String[] {"REQUEST_RECEIVED", "REQUEST_REPLIED"})
			.addNext("] ").addNext("message", String.class);

		return link;
	}

} // end class LogSetTest


/**
 * .
 */
@SuppressWarnings("serial")
class Link extends ArrayList<Link> {

	public final String id;
	public final Class<?> clazz;
	public final String[] enums;


	public Link(final String id) {
		this(id, null, null);
	}

	public Link(final String id, final Class<?> clazz) {
		this(id, clazz, null);
	}

	public Link(final String id, final String[] enums) {
		this(id, null, enums);
	}

	public Link(final String id, final Class<?> clazz, final String[] enums) {
		this.id = id;
		this.clazz = clazz;
		this.enums = null;
	}

	public boolean equals(final LogEntry entry) {
		if (entry == null || !this.id.equals(entry.getId())) {
			return false;
		}

		final List<LogEntry> nexts = entry.getNexts();
		final Iterator<LogEntry> iter;

		assertEquals("For log field \"" + this.id + "\"", size(), nexts.size());
		iter = nexts.iterator();
		for (final Link link : this) {
			link.equals(iter.next());
		}

		return true;
	}

	@Override
	public String toString() {
		return "id: \"" + this.id + "\", numNexts: " + size();
	}

	Link addNext(final String id) {
		final Link link = new Link(id);

		assertTrue(add(link));

		return link;
	}

	Link addNext(final String id, final Class<?> clazz) {
		final Link link = new Link(id, clazz);

		assertTrue(add(link));

		return link;
	}

	Link addNext(final String id, final String[] enums) {
		final Link link = new Link(id, enums);

		assertTrue(add(link));

		return link;
	}

	Link add(final String id) {
		final Link link = new Link(id);

		assertTrue(add(link));

		return this;
	}

	Link add(final String id, final Class<?> clazz) {
		final Link link = new Link(id, clazz);

		assertTrue(add(link));

		return this;
	}

	Link add(final String id, final String[] enums) {
		final Link link = new Link(id, enums);

		assertTrue(add(link));

		return this;
	}

} // end class Link
