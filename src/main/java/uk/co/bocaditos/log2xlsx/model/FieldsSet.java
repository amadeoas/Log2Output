package uk.co.bocaditos.log2xlsx.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import uk.co.bocaditos.log2xlsx.in.filter.FieldFilter;
import uk.co.bocaditos.log2xlsx.in.filter.Filter;
import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.BaseModelInterface;


/**
 * .
 */
@SuppressWarnings("serial")
public class FieldsSet extends ArrayList<FieldsGroup> implements BaseModelInterface<FieldsSet> {

	private final String idFieldName;
	private final LogSet set;


	public FieldsSet(final LogSet set, final String idFieldName) {
		if (Utils.isEmpty(idFieldName)) {
			throw new RuntimeException("Missing ID field name");
		}

		this.idFieldName = idFieldName;
		this.set = set;
	}

	public final String getIdFieldName() {
		return this.idFieldName;
	}

	public FieldNames getHeaders() {
		return this.set.getFieldNames();
	}

	public FieldNames getHeaders(final String... fieldNames) {
		final FieldNames names = this.set.getFieldNames();
		final FieldNames newNames;

		if (Utils.isEmpty(fieldNames)) {
			return names;
		}

		newNames = new FieldNames(names.size());
		for (final String name : fieldNames) {
			if (Utils.isEmpty(name)) {
				continue;
			}

			final LogField field = names.stream()
				.filter(f -> name.equals(f.getId()))
				.findFirst()
				.orElse(null);

			if (field != null) {
				newNames.add(field);
			}
		}

		return newNames;
	}

	/**
	 * Updates from log lines in specified file.
	 * 
	 * @param filter a filter, may be null.
	 * @param logFilenames a list of log files.
	 * @throws FormatException when .
	 */
	public void load(Filter filter, final String... logFilenames) 
			throws FormatException {
		if (filter == null) {
			filter = FieldFilter.build();
		}

		if (Utils.isEmpty(logFilenames)) {
			throw new FormatException("Missing the log files");
		}

		for (String logFilename : logFilenames) {
			int lineNum = 0;
	
			logFilename = logFilename.trim();
			try (final BufferedReader in = new BufferedReader(new FileReader(logFilename, StandardCharsets.UTF_8))) {
				String line;
	
				while ((line = in.readLine()) != null) {
					final FieldsLine f = this.set.process(line);

					if (f != null && filter.valid(f)) {
						add(f);
					}
					++lineNum;
				}
			} catch (final FormatException fe) {
				throw new FormatException(fe, 
						"Failed to load logs from file \"{0}\" at line {2, number}: {1}", 
						logFilename, fe.getMessage(), lineNum);
			} catch (final IOException ioe) {
				throw new FormatException(ioe, "Failed to read file \"{0}\" at line {1, number}", 
						logFilenames, lineNum);
			}
		}
	}

	@Override
	public boolean add(final FieldsGroup grp) {
		return false;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof FieldsSet)) {
			return false;
		}

		return equalsIt((FieldsSet) obj);
	}

	@Override
	public boolean equalsIt(final FieldsSet set) {
		if (size() != set.size()) {
			return false;
		}

		for (int index = 0; index < size(); ++index) {
			final FieldsGroup grp = get(index);
			final FieldsGroup grp1 = set.get(index);

			if (!grp.equals(grp1)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		return BaseModel.append(buf, null, (ArrayList<FieldsGroup>) this);
	}

	/**
	 * Adds specified the fields from a log line.
	 * 
	 * @param fields the fields from a log line.
	 * @return true when field has been added.
	 * @throws FormatException when .
	 */
	boolean add(final FieldsLine fields) throws FormatException {
		if (Utils.isEmpty(fields)) {
			return false;
		}

		final String id = (String) value(fields, this.idFieldName);

		if (id != null) {
			for (int index = (size() - 1); index >= 0; --index) {
				final FieldsGroup grp = get(index);

				if (id.equals(grp.getId())) {
					return grp.add(fields);
				}
			}

			// New one
			return super.add(new FieldsGroup(id, fields));
		}

		return false;
	}

	public static Object value(final List<Field> fields, final String fieldName) {
		if (fieldName == null) {
			return null;
		}

		final Field field = fields
			.stream()
			.filter(f -> fieldName.equals(f.getFieldName()))
			.findFirst()
			.orElse(null);

		if (field == null) {
			return null;
		}

		return field.getValue();
	}

} // end class FieldsSet
