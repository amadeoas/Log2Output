package uk.co.bocaditos.utils;

import java.util.Collection;


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
