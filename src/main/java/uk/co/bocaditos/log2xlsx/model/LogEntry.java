package uk.co.bocaditos.log2xlsx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * [{]<fix_text>[}]
 */
public class LogEntry implements Comparable<LogEntry> {

	private static final String END   = "\\" + LogField.END;
	private static final String START = "\\" + LogField.START;
	static final int INDEX_FIELD = 0;
	static final int INDEX_ENTRY = 1;

	public String id;
	public LogEntry parent;
	private List<LogEntry> nexts;


	protected LogEntry(final LogEntry parent, final String line, int offset) 
			throws FormatException {
		this(parent, getId(line, offset));

		int i = offset;

		offset = offset(LogField.START, line, offset);
		while ((i = line.indexOf(LogField.END, i)) < offset && i != -1) {
			if (i == 0 || line.charAt(i - 1) != '\\') {
				throw new FormatException(
						"Invalid format: missing end character \"{0}\" for log field for parent \"{1}\"", 
						LogField.START, (this.parent == null) ? null : this.parent.getId());
			}
			++i;
		}
		loadNexts(++offset, line);
	}

	protected LogEntry(final LogEntry parent, final String id) {
		this.id = id;
		this.parent = parent;
		this.nexts = new ArrayList<>();
	}

	public final LogEntry getParent() {
		return this.parent;
	}

	public final String getId() {
		return this.id;
	}

	public boolean isField() {
		return false;
	}

	public void setNexts(final List<LogEntry> nexts) {
		this.nexts = nexts;
	}

	@Override
	public int compareTo(final LogEntry o) {
		return 1; // to make sure to order by order of addition to the collection
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public final String toString() {
		return toString(new StringBuilder()).toString();
	}

	public StringBuilder toString(final StringBuilder buf) {
		buf.append("id: \"").append(this.id).append('"')
			.append(", numNexts: ").append(this.nexts.size());
		if (this.parent != null) {
			buf.append(", parent: \"").append(this.parent.id).append('"');
		}

		return buf;
	}

	LogField getField(final String fieldName) {
		for (LogEntry entry : getNexts()) {
			entry = entry.getField(fieldName);
			if (entry != null) {
				return (LogField) entry;
			}
		}

		return null;
	}

	protected List<LogEntry> getNexts() {
		return this.nexts;
	}

	protected boolean loadNexts(int offset, final String line) throws FormatException {
		if (offset >= line.length()) {
			return true;
		}

		final String id;

		if (isField()) {
			id = LogEntry.getId_(line, offset);
		} else {
			if (line.charAt(offset) == LogField.START) {
				++offset;
			}
			id = LogField.getId_(line, offset);
		}

		return loadNexts_(offset, line, id);
	}

	boolean process(final FieldsLine fields, final String line, int offset) 
			throws FormatException {
		if (!line.startsWith(getId(), offset)) {
			return false;
		} 

		offset += getId().length();
		if (offset >= line.length()) {
			return true;
		}

		for (final LogEntry field : this.nexts) {
			if (field.process(fields, line, offset)) {
				return true;
			}
		}

		throw new FormatException("Invalid format: \"{0}\"", line.substring(offset));
	}

	boolean loadNexts_(int offset, final String line, final String id) throws FormatException {
		if (id.length() == 0) {
			throw new FormatException("Invalid format; missing log field ID for next to \"{0}\"}",
					getId());
		}

		for (final LogEntry lf : this.nexts) {
			if (id.equals(lf.getId())) {
				final char c;

				if (lf.isField()) {
					((LogField) lf).verify(line, offset);
					c = LogField.END;
				} else {
					c = LogField.START;
				}
				offset = offset(c, line, offset);
				if (offset == -1) {
					throw new FormatException(
							"Invalid format; missing {0} of log field character \"{1}\"", 
							(c == LogField.END) ? "end" : "start", c);
				}

				return lf.loadNexts(++offset, line);
			}
		}

		return addNext(line, offset);
	}

	boolean addNext(final String line, int offset) throws FormatException {
		final LogEntry entry;

		if (isField()) {
			// Must be and entity constant, i.e. a base LogEntry
			entry = new LogEntry(this, line, offset);
		} else {
			// Must be an entity field
			entry = new LogField(this, line, offset);
		}

		return this.nexts.add(entry);
	}

	boolean add(final String line, int offset) throws FormatException {
		return this.nexts.add(build(line, offset));
	}

	LogEntry build(final String line, int offset) throws FormatException {
		return new LogField(this, line, offset);
	}

	static String getId(String value) {
		final int offset = value.indexOf(',');

		if (offset != -1) {
			value = value.substring(offset);
		}

		return value;
	}

	protected static int offset(final char c, final String value, int offset) {
		while ((offset = value.indexOf(c, offset)) != -1 && value.charAt(offset - 1) == '\\') {
			++offset;
		}

		if (offset == -1) {
			offset = value.length();
		}

		return offset;
	}

	protected static String getId(String value, int offset) throws FormatException {
		if (offset < 0 || value == null 
				|| (offset > 0 && value.charAt(offset - 1) != LogField.END)) {
			throw new FormatException(
					"Invalid format; constant log entry is missing start delimiter \"{0}\"",
					LogField.END);
		}

		return getId_(value, offset);
	}

	protected static String getId_(String value, int offset) throws FormatException {
		int i = offset(LogField.START, value, offset);

		value = value.substring(offset, i);
		value = removeAll(value, END);

		return removeAll(value, START);
	}

	private static String removeAll(String value, final String txt) {
		StringBuilder buf = null;
		int from = 0;
		int to;

		while ((to = value.indexOf(txt, from)) != -1) {
			if (buf == null) {
				buf = new StringBuilder(value.length());
			}
			buf.append(value, from, to)
				.append(txt.charAt(1));
			from = to + txt.length();
		}

		if (buf != null) {
			if (from < value.length()) {
				buf.append(value, from, value.length());
			}
			value = buf.toString();
		}

		return value;
	}

	FieldNames buildFieldNames(final FieldNames fieldNames) {
		if (this instanceof LogField) {
			final LogField logField = (LogField) this;
			final String fieldName = logField.getHeaderName();
			final LogField field = fieldNames
				.stream()
				.filter(f -> fieldName.equals(f.getHeaderName()))
				.findFirst()
				.orElse(null);

			if (field == null) {
				fieldNames.add(logField);
			}
		}

		return buildFieldNames_(fieldNames);
	}

	FieldNames buildFieldNames_(final FieldNames fieldNames) {
		for (final LogEntry entry : getNexts()) {
			entry.buildFieldNames(fieldNames);
		}

		return fieldNames;
	}

} // end class LogEntry
