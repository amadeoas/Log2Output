package uk.co.bocaditos.log2xlsx.model;

import uk.co.bocaditos.utils.Utils;


/**
 * .
 */
public class LogSet extends LogEntry {

	private FieldNames fieldNames;


	public LogSet() {
		super(null, "");
	}

	public void load(final String line) throws FormatException {
		if (line == null || line.isEmpty()) {
			return;
		}

		String id;
		int offset;

		if (line.charAt(0) == LogField.START) {
			// A Field
			int i;

			offset = offset(LogField.END, line, 1);
			if (offset == line.length()) {
				throw new FormatException("Invalid format; missing log field end character \"{0}\"",
						LogField.END);
			}
			id = line.substring(1, offset);
			i = id.indexOf(LogField.SEPARATOR, 1);
			if (i != -1) {
				id = id.substring(0, i);
			}
			offset = 1;
		} else {
			// Not a field
			offset = offset(LogField.START, line, 0);
			if (offset == -1) {
				id = line;
			} else {
				id = line.substring(0, offset);
			}
			offset = 0;
		}
		loadNexts_(offset, line, id);
	}

	@Override
	public final LogField getField(final String fieldName) {
		if (Utils.isEmpty(fieldName)) {
			return null;
		}

		for (LogEntry entry : getNexts()) {
			entry = entry.getField(fieldName);
			if (entry != null) {
				return (LogField) entry;
			}
		}

		return null;
	}

	public FieldNames getFieldNames() {
		if (this.fieldNames == null) {
			synchronized (LogSet.class) {
				if (this.fieldNames == null) {
					this.fieldNames = buildFieldNames();
				}
			}
		}

		return this.fieldNames;
	}

	/**
	 * Extracts the fields from the passed log line.
	 * 
	 * @param line a log line.
	 * @return the field in passed log line base on this set.
	 * @throws FormatException when failed to extract fields from line.
	 */
	public FieldsLine process(final String line) throws FormatException {
		if (Utils.isEmpty(line)) {
			return null;
		}

		final FieldsLine fields = new FieldsLine();

		for (final LogEntry entry : getNexts()) {
			if (entry.process(fields, line, 0)) {
				return fields;
			}
		}

		throw new FormatException("Invalid format: unable to extract fields from line \"{0}\"",
				line);
	}

	@Override
	boolean addNext(final String line, int offset) throws FormatException {
		return add(line, offset);
	}

	@Override
	LogEntry build(final String line, int offset) throws FormatException {
		if (offset == 0) {
			return new LogEntry(this, line, offset);
		}

		if (line.charAt(offset - 1) == LogField.START) {
			return new LogField(this, line, offset);
		}

		return new LogEntry(this, line, offset);
	}

	FieldNames buildFieldNames() {
		return buildFieldNames_(new FieldNames());
	}

} // end class LogSet
