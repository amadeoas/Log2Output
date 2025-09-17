package uk.co.bocaditos.log2xlsx.model;

import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.bocaditos.utils.Utils;


/**
 * {<id>[, <class>[, f:<format>][,p:<pattern>]]}.
 */
public class LogField extends LogEntry {

    private static final Logger logger = LoggerFactory.getLogger(LogField.class);

	public static final char START = '{';
	public static final char END   = '}';
	public static final char SEPARATOR = ',';

	private static final int INDEX_ID		= 0;
	private static final int INDEX_CLASS	= 1;
	private static final int INDEX_PATTERN	= 2;
	private static final int INDEX_FORMAT	= 3;

	private Class<?> clazz;
	private Object format;
	private Pattern pattern;
	private String[] enumValues;
	private String title; // ID with first character upper case
	private String size;


	/**
	 * {<id>[, type[, ]]}
	 * 
	 * @param parent the parent entry, if any - may be null.
	 * @param line a log line of text.
	 * @param offset the line offset.
	 * @throws ClassNotFoundException 
	 * @throws FormatException 
	 */
	public LogField(final LogEntry parent, final String line, int offset) throws FormatException {
		super(parent, getId(line, offset));

		int i = offset(END, line, offset);

		this.title = Character.toUpperCase(this.id.charAt(0)) + this.id.substring(1);
		set(line.substring(offset, i));
		loadNexts(i + 1, line);
	}

	public LogField(final LogEntry parent, final String id, final Class<?> clazz, 
			final String format, final String pattern) {
		super(parent, id);

		this.clazz = clazz;
		loadEnumValues();
		if (format != null) {
			if (clazz == LocalDateTime.class || clazz == LocalDate.class) {
				this.format = DateTimeFormatter.ofPattern(format);
			} else {
				this.format = format;
			}
		}

		if (pattern != null) {
			this.pattern = Pattern.compile(pattern);
		}
	}

	public String getValue(final Object value) throws FormatException {
		if (value == null) {
			return null;
		}

		if (this.format != null && this.format instanceof DateTimeFormatter) {
			if (value instanceof LocalDateTime) {
				final LocalDateTime datetime = (LocalDateTime) value;

				return ((DateTimeFormatter) this.format).format(datetime);
			} else if (value instanceof LocalDate) {
				final LocalDate date = (LocalDate) value;

				return ((DateTimeFormatter) this.format).format(date);
			}
		}

		return value.toString();
	}

	public void setSize(final String size) {
		this.size = size;
	}

	public String getHeaderName() {
		return getId();
	}

	public String getTitle() {
		return this.title;
	}

	public String getSize() {
		return this.size;
	}

	public Class<?> getFieldClass() {
		return this.clazz;
	}

	public Object getFormat() {
		return this.format;
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public void validate(final String value) throws FormatException {
		if (this.enumValues != null) {
			for (final String v : this.enumValues) {
				if (v.equals(value)) {
					return;
				}
			}

			throw new FormatException(
					"Invalid format: value of \"{0}\" is not valid for data of type \"{1}\" and " 
					+ "field \"{2}\"",
					value, this.clazz.getName(), getHeaderName());
		}

		if (this.pattern == null) {
			return;
		}

		final Matcher matcher = this.pattern.matcher(value);

		if (!matcher.matches()) {
			throw new FormatException(
					"Invalid format: value \"{0}\" for log field \"{1}\" is not valid for pattern " 
					+ "\"{2}\"",
					value, getId(), getPattern().pattern());
		}
	}

	@Override
	public boolean isField() {
		return true;
	}

	@Override
	LogEntry build(final String line, int offset) throws FormatException {
		return new LogEntry(this, line, offset);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		super.toString(buf);
		if (this.format != null) {
			buf.append(", format: \"").append(this.format).append('"');
		}

		if (this.pattern != null) {
			buf.append(", pattern: \"").append(this.pattern).append('"');
		}

		if (this.enumValues != null) {
			final int length;

			buf.append(", enum: [");
			length = buf.length();
			for (final String value : this.enumValues) {
				if (buf.length() != length) {
					buf.append("\", ");
				}
				buf.append('"').append(value);
			}
			buf.append("\"]");
		}

		return buf;
	}

	@Override
	LogField getField(final String fieldName) {
		if (getId().equals(fieldName)) {
			return this;
		}

		for (LogEntry entry : getNexts()) {
			entry = entry.getField(fieldName);
			if (entry != null) {
				return (LogField) entry;
			}
		}

		return null;
	}

	public Object build(final String value) throws FormatException {
		validate(value);
		if (getFieldClass() == String.class) {
			if (this.enumValues != null) {
				for (final String v : this.enumValues) {
					if (v.equals(value)) {
						return v;
					}
				}

				throw new FormatException(
						"Invalid format: the log field \"{0}\" value \"{1}\" is not valid for " 
						+ "enumeration",
						getId(), value);
			}

			return value;
		}

		try {
			if (getFieldClass() == int.class) {
				return Integer.valueOf(value);
			}

			if (getFieldClass() == long.class) {
				return Long.valueOf(value);
			}

			if (getFieldClass() == char.class) {
				if (value.length() != 1) {
					throw new FormatException(
							"Invalid fiedl \"{0}\" value of \"{1}\" as it must be a char log field",
							getId(), value);
				}

				return value.charAt(0);
			}

			if (getFieldClass() == double.class) {
				return Double.valueOf(value);
			}

			if (getFieldClass() == float.class) {
				return Float.valueOf(value);
			}

			if (getFieldClass() == LocalDateTime.class) {
				if (this.format != null) {
					return LocalDateTime.parse(value, (DateTimeFormatter) this.format);
				}

				return LocalDateTime.parse(value);
			}

			if (getFieldClass() == LocalDate.class) {
				if (this.format != null) {
					return LocalDate.parse(value, (DateTimeFormatter) this.format);
				}

				return LocalDate.parse(value);
			}

			if (getFieldClass() == Boolean.class) {
				return Boolean.valueOf(value);
			}

			if (getFieldClass().isEnum()) {
				try {
					return getFieldClass()
							.getDeclaredMethod("valueOf", String.class)
							.invoke(null, value);
				} catch (final IllegalAccessException | IllegalArgumentException 
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new FormatException(
							"Invalid format; value \"{0}\" not of enumeration \"{1}\"",
							value, getFieldClass());
				}
			}

			// Must be JSON
			try {
				return new ObjectMapper().readValue(value, getFieldClass());
			} catch (final JsonProcessingException jpe) {
				throw new FormatException(jpe, "Failed to convert log field \"{0}\" to \"{1}\"", 
						getId(), getFieldClass());
			}
		} catch (final NumberFormatException nfe) {
			throw new FormatException(nfe, "Invalid numeric field \"{0}\"; for value \"{1}\"", 
					getId(), value);
		}
	}

	@Override
	boolean process(final FieldsLine fields, final String line, int offset) 
			throws FormatException {
		String value;

		if (getNexts() == null || getNexts().isEmpty()) {
			value = line.substring(offset);

			return fields.add(new Field(this, value));
		}

		for (final LogEntry le : getNexts()) {
			final int i = line.indexOf(le.getId(), offset);

			if (i != -1) {
				value = line.substring(offset, i); System.out.println();
				try {
					if (fields.add(new Field(this, value))) {
						if (le.process(fields, line, i)) {
							return true;
						}
		
						throw new FormatException(
								"Invalid format: field with ID \"{0}\", value of \"{1}\" and end " 
								+ "delimiter \"{2}\"", 
							getId(), value, le.getId());
					}
				} catch (final FormatException fe) {
					// Continue
					LogField.logger.debug(fe.getMessage());
				}
			}
		}

		return false;
	}

	private void set(final String value) throws FormatException {
		final Object[] elements = set_(value);

		this.clazz = (Class<?>) elements[INDEX_CLASS];
		loadEnumValues();
		if (elements[INDEX_PATTERN] != null) {
			this.pattern = (Pattern) elements[INDEX_PATTERN];
		}

		if (elements[INDEX_FORMAT] != null) {
			this.format = elements[INDEX_FORMAT];
		}
	}

	private Object[] set_(final String value) throws FormatException {
		final Object[] elements = {null, null, null, null};
		final String[] parts = split(value, ",");

		elements[INDEX_ID] = parts[0];
		if (parts.length > 1 && !parts[1].isEmpty()) {
			final String type = parts[1].trim().toLowerCase();

			if (type.isEmpty() || "string".equals(type)) {
				elements[INDEX_CLASS] = String.class;
			} else if ("int".equals(type)) {
				elements[INDEX_CLASS] = int.class;
			} else if ("long".equals(type)) {
				elements[INDEX_CLASS] = long.class;
			} else if ("char".equals(type)) {
				elements[INDEX_CLASS] = char.class;
			} else if ("double".equals(type)) {
				elements[INDEX_CLASS] = double.class;
			} else if ("float".equals(type)) {
				elements[INDEX_CLASS] = float.class;
			} else if ("date".equals(type)) {
				elements[INDEX_CLASS] = LocalDate.class;
			} else if ("datetime".equals(type)) {
				elements[INDEX_CLASS] = LocalDateTime.class;
			} else if ("boolean".equals(type)) {
				elements[INDEX_CLASS] = boolean.class;
			} else if ("byte".equals(type)) {
				elements[INDEX_CLASS] = byte.class;
			} else if ("enum".equals(type)) {
				if (parts.length > 3) {
					// Enum of fixed value - verification type
					this.enumValues = Arrays.copyOfRange(parts, 2, parts.length);
					for (int index = 0; index < this.enumValues.length; ++index) {
						this.enumValues[index] = this.enumValues[index].trim();
					}
					elements[INDEX_CLASS] = String.class;
				} else {
					// Enum class
					if (parts.length < 2) {
						throw new FormatException("Invalid format \"{0}\"", parts[1]);
					}
	
					if (parts.length >= 3 && Utils.allToUpperCase(parts[2])) {
						this.enumValues = Arrays.copyOfRange(parts, 2, parts.length);
						for (int index = 0; index < this.enumValues.length; ++index) {
							this.enumValues[index] = this.enumValues[index].trim();
						}
						elements[INDEX_CLASS] = String.class;
					} else {
						// Used for JSON conversion
						try {
							elements[INDEX_CLASS] =  Class.forName(parts[2]);
						} catch (final ClassNotFoundException cnfe) {
							throw new FormatException(cnfe, 
									"Unsupported class {1} for log field \"{0}\"", 
									elements[INDEX_ID], parts[1]);
						}
					}
				}

				return elements;
			} else if (type.startsWith("class:")) {
				// Will be used for Json conversion
				try {
					elements[INDEX_CLASS] = Class.forName(parts[1]);
				} catch (final ClassNotFoundException cnfe) {
					throw new FormatException(cnfe, "Unsupported class {0}", parts[1]);
				}
			}
		} else {
			elements[INDEX_CLASS] = String.class;
		}

		for (int i = 2; i < parts.length; ++i) {
			final String part = decode(parts[i]);

			if (part.startsWith("f:")) {
				if (elements[INDEX_FORMAT] != null) {
					throw new FormatException(
							"Invalid format for field {0}: the format was already initialised", 
							elements[INDEX_ID]);
				}

				elements[INDEX_FORMAT] = part.substring("f:".length());
				if (elements[INDEX_CLASS] == LocalDateTime.class 
						|| elements[INDEX_CLASS] == LocalDate.class) {
					elements[INDEX_FORMAT] = DateTimeFormatter.ofPattern((String) elements[INDEX_FORMAT]);
				}
			} else if (part.startsWith("p:")) {
				if (elements[INDEX_PATTERN] != null) {
					throw new FormatException(
							"Invalid format for field \"{0}\": pattern was already initialised", 
							elements[INDEX_ID]);
				}

				elements[INDEX_PATTERN] = Pattern.compile(part.substring(2));
			} else {
				throw new FormatException(
						"Invalid format for field \"{0}\"; invalid field part with index {1, number}", 
						elements[INDEX_ID], i);
			}
		}

		return elements;
	}

	protected void verify(final String line, int offset) throws FormatException {
		int i = offset(END, line, offset); // never will return -1
		final Object[] elements = set_(line.substring(offset, i)); 

		if (!getId().equals(elements[0])) {
			throw new FormatException("Invalid format; different log field IDs \"{0}\" != \"{1}\"", 
					getId(), elements[0]);
		}

		if (this.clazz != elements[INDEX_CLASS]) {
			throw new FormatException(
					"Invalid format; different log field classes \"{0}\" != \"{1}\"", 
					this.clazz, elements[INDEX_CLASS]);
		}

		if ((this.format == null && elements[INDEX_FORMAT] != null)
				|| (this.format != null && elements[INDEX_FORMAT] == null)
				|| (this.format != null && this.format.toString().equals(elements[INDEX_FORMAT]))) { // <- not the faster comparison
			throw new FormatException(
					"Invalid format; different log field format \"{0}\" != \"{1}\"", 
					this.pattern, elements[INDEX_FORMAT]);
		}

		if (!Objects.equals(this.pattern, elements[INDEX_PATTERN])) {
			throw new FormatException(
					"Invalid format; different log field pattern \"{0}\" != \"{1}\"", 
					this.pattern, elements[INDEX_PATTERN]);
		}
	}

	private void loadEnumValues() {
		if (this.clazz.isEnum()) {
			final Object[] values = this.clazz.getEnumConstants();

			this.enumValues = new String[values.length];
			for (int index = 0; index < this.enumValues.length; ++index) {
				this.enumValues[index] = ((Enum<?>) values[index]).name();
			}
		}
	}

	protected static String getId(final String value, int offset) throws FormatException {
		if (offset < 0 || (offset != 0 && value.charAt(offset - 1) != START)) {
			throw new FormatException(
					"Invalid format; missing log field start character \"{0}\"", START);
		}

		int i = offset(END, value, offset);

		if (i == -1) {
			throw new FormatException(
					"Invalid format; missing log field end character \"{0}\"", END);
		}

		int j = value.indexOf(SEPARATOR, offset);

		if (j == -1 || j >= i) {
			j = i;
		}

		return value.substring(offset, j).trim();
	}

	protected static String getId_(final String value, int offset) throws FormatException {
		int i = offset(END, value, offset);

		if (i == -1) {
			throw new FormatException(
					"Invalid format; missing log field end character \"{0}\"", END);
		}

		int j = value.indexOf(SEPARATOR, offset);

		if (j == -1 || j >= i) {
			j = i;
		}

		return value.substring(offset, j).trim();
	}

	private String decode(String value) {
		final String[][] chars = {
				{"\\{", "{"}, {"\\}", "}"}, {"\\[", "["}, {"\\]", "]"}, {"\\,", ","}
		};

		value = value.trim();
		for (int index = 0; index < chars.length; ++index) {
			value = value.replace(chars[index][0], chars[index][1]);
		}


		return value;
	}

	private String[] split(String value, final String character) {
		if (value == null) {
			return null;
		}

		String[] values = value.split(character);

		if (values.length > 1) {
			final int lastIndex = values.length - 1;

			for (int index = 0; index < values.length; ++index) {
				values[index] = values[index].trim();
			}

			for (int index = 0; index < lastIndex; ++index) {
				final String v = values[index].trim();

				if (v.charAt(v.length() - 1) == '\\') {
					final String[] newValues = new String[lastIndex];

					for (int i = 0; i < index; ++i) {
						newValues[i] = values[i];
					}
					newValues[index] = v.substring(0, values[index].length() - 1) 
							+ character + values[index + 1];
					for (int i = index + 1, j = index + 2; j < values.length; ++i) {
						newValues[i] = values[j];
					}
					values = newValues;
					++index;
				}
			}
		}

		return values;
	}

} // end class LogField
