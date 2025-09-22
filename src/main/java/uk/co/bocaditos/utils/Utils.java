package uk.co.bocaditos.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 * Utility methods.
 */
public abstract class Utils {

	public static boolean isEmpty(final String value) {
		return (value == null) || value.isEmpty();
	}

	public static boolean isEmpty(final String[] values) {
		return (values == null) || values.length == 0;
	}

	public static boolean isEmpty(final Object[] values) {
		return (values == null) || values.length == 0;
	}

	public static <T> boolean isEmpty(final Collection<T> values) {
		return (values == null) || values.isEmpty();
	}

	public static boolean allToUpperCase(final String value) {
		if (isEmpty(value)) {
			return false;
		}

		for (int index = 0; index < value.length(); ++index) {
			if (Character.isLetter(value.charAt(index)) 
					&& !Character.isUpperCase(value.charAt(index))) {
				return false;
			}
		}

		return true;
	}

	public static void save(final String filename, final Object obj) throws UtilsException {
		try {
			final ObjectMapper mapper = new ObjectMapper();

			mapper.registerModule(new JavaTimeModule());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.writeValue(new File(filename), obj);
		} catch (final IOException ioe) {
			throw new UtilsException(ioe, "Failed to write JSON to specified file \"{0}\"", 
					filename);
		}
	}

	public static String save(final Object obj) throws UtilsException {
		try {
			return objectMapper().writeValueAsString(obj);
		} catch (final IOException ioe) {
			throw new UtilsException(ioe, "Failed to write JSON to an string");
		}
	}

	public static <T> T read(final String filename, final Class<T> clazz) throws UtilsException {
		try {
			return objectMapper().readValue(new File(filename), clazz);
		} catch (final IOException ioe) {
			throw new UtilsException(ioe, "Failed to read JSON from the specified file \"{0}\"", 
					filename);
		}
	}

	public static void write(final String filename, final String value) throws UtilsException {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(value);
		} catch (final IOException ioe) {
			throw new UtilsException(ioe, "Failed to write string to the specified file \"{0}\"", 
					filename);
		}
	}

	public static String read(final String filename) throws UtilsException {
		try (final BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			final StringBuilder buf = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				if (buf.length() > 0) {
					buf.append('\n');
				}
				buf.append(line);
			}

			return buf.toString();
		} catch (final IOException ioe) {
			throw new UtilsException(ioe, "Failed to read JSON from the specified file \"{0}\"", 
					filename);
		}
	}

	public static ObjectMapper objectMapper() {
		final ObjectMapper mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper;
	}

	public static boolean allToLowerCase(final String value) {
		if (isEmpty(value)) {
			return false;
		}

		for (int index = 0; index < value.length(); ++index) {
			if (Character.isLetter(value.charAt(index)) 
					&& !Character.isLowerCase(value.charAt(index))) {
				return false;
			}
		}

		return true;
	}

	public static String concatenate(final String... values) {
		if (isEmpty(values)) {
			return "";
		}

		if (values.length == 1) {
			return values[0];
		}

		final StringBuilder buf = new StringBuilder();

		for (final String value : values) {
			if (value == null) {
				continue;
			}

			if (buf.length() > 0) {
				buf.append(", ");
			}
			buf.append(value);
		}

		return buf.toString();
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final String... values) {
		return append(buf, name, false, values);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final boolean appendNulls, final String... values) {
		if (isEmpty(name) || (values == null && !appendNulls)) {
			return buf;
		}

		if (buf.length() > 0) {
			final char c = buf.charAt(buf.length() - 1);

			if (c != '{' && c != '[' && c != '\t') {
				buf.append(", ");
			}
		}

		buf.append(name)
			.append(": [");
		for (final String value : values) {
			append_(buf, null, value, appendNulls);
		}
		buf.append(']');

		return buf;
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final Number value) {
		return append(buf, name, value, false);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final Number value, final boolean appendNulls) {
		return append_(buf, name, value, appendNulls);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final boolean value) {
		return append(buf, name, value, false);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final boolean value, final boolean appendNulls) {
		return append_(buf, name, value, appendNulls);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final String value) {
		return append(buf, name, value, false);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final String value, final boolean appendNulls) {
		return append_(buf, name, value, appendNulls);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final ToString value) {
		return append(buf, name, value, false);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final ToString value, final boolean appendNulls) {
		return append_(buf, name, value, appendNulls);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final ToString[] values) {
		return append(buf, name, values, false);
	}

	public static StringBuilder append(final StringBuilder buf, final String name, 
			final ToString[] values, final boolean appendNulls) {
		if (isEmpty(name) || (values == null && !appendNulls)) {
			return buf;
		}

		if (buf.length() > 0) {
			final char c = buf.charAt(buf.length() - 1);

			if (c != '{' && c != '[') {
				buf.append(", ");
			}
		}

		buf.append(name)
			.append(": [");
		for (final ToString value : values) {
			append_(buf, null, value, appendNulls);
		}
		buf.append(']');

		return buf;
	}

	private static StringBuilder append_(final StringBuilder buf, final String name, 
			final Object value, final boolean appendNulls) {
		if (value == null && !appendNulls) {
			return buf;
		}

		if (buf.length() > 0) {
			final char c = buf.charAt(buf.length() - 1);

			if (c != '{' && c != '[') {
				buf.append(", ");
			}
		}

		if (!isEmpty(name)) {
			buf.append(name);
			buf.append(": ");
		}

		if (value instanceof String) {
			buf.append('"')
				.append(value)
				.append('"');
		} else if (value instanceof ToString) {
			buf.append('{')
				.append(value)
				.append('}');
		} else {
			buf.append(value);
		}

		return buf;
	}

	private Utils() {
	}

} // end class Utils
