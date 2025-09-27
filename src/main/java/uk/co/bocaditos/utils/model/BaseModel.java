package uk.co.bocaditos.utils.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import uk.co.bocaditos.utils.Utils;


/**
 * .
 * 
 * @author aasco
 *
 * @param <T> class extending BaseModel.
 */
public abstract class BaseModel<T extends BaseModelInterface<T>> implements BaseModelInterface<T> {

	/** The format to write date and time as string. */
	public static DateTimeFormatter FORMATER_DATETIME 
			= DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm:ss.SSS");
	/** The format to write date as string. */
	public static DateTimeFormatter FORMATER_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");


	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof BaseModel)) {
			return false;
		}

		return equalsIt((T) obj);
	}

	@Override
	public final String toString() {
		final StringBuilder buf = new StringBuilder();

		buf.append('{');

		return toString(buf).append('}').toString();
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param value the value.
	 * @return the passed buffer.
	 */
	public static StringBuilder append(final StringBuilder buf, final String value) {
		return append(buf, null, value);
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param value the value.
	 * @return the passed buffer.
	 */
	public static StringBuilder append(final StringBuilder buf, final String fieldName, 
			final String value) {
		if (value != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('"')
				.append(value)
				.append('"');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param value the value.
	 * @return the passed buffer.
	 */
	public static StringBuilder append(final StringBuilder buf, final String fieldName, 
			final WithID value) {
		if (value != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('"')
				.append(value.getID())
				.append('"');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param values the values.
	 * @return the passed buffer.
	 */
	public static StringBuilder appendIDs(final StringBuilder buf, final String fieldName, 
			final Collection<? extends WithID> values) {
		if (!Utils.isEmpty(values)) {
			separator(buf);
			if (!Utils.isEmpty(fieldName)) {
				buf.append(fieldName)
					.append(": ");
			}

			final int length;

			buf.append('[');
			length = buf.length();
			for (final WithID id : values) {
				if (buf.length() > length) {
					buf.append(", ");
				}
				buf.append('"')
					.append(id.getID())
					.append('"');
			}
			buf.append(']');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param <T> the class of the value.
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param value the value.
	 * @return the passed buffer.
	 */
	public static <T extends BaseModelInterface<T>> StringBuilder append(final StringBuilder buf, 
			final String fieldName, final T value) {
		if (value != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('{');
			value.toString(buf)
				.append('}');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param <E> the enumeration type.
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param value the value.
	 * @return the passed buffer.
	 */
	public static <E extends Enum<E>> StringBuilder append(final StringBuilder buf, 
			final String fieldName, final E value) {
		if (value != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('"')
				.append(value.name())
				.append('"');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param datetime the value.
	 * @return the passed buffer.
	 */
	public static StringBuilder append(final StringBuilder buf, final String fieldName, 
			final LocalDateTime datetime) {
		if (datetime != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('"')
				.append(datetime.format(FORMATER_DATETIME))
				.append('"');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param date the value.
	 * @return the passed buffer.
	 */
	public static StringBuilder append(final StringBuilder buf, final String fieldName, 
			final LocalDate date) {
		if (date != null) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('"')
				.append(date.format(FORMATER_DATE))
				.append('"');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param values a list of values.
	 * @return the passed buffer.
	 */
	public static StringBuilder appendTxt(final StringBuilder buf, final String fieldName, 
			final Collection<String> values) {
		if (!Utils.isEmpty(values)) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('[');
			for (final String value : values) {
				append(buf, value);
			}
			buf.append(']');
		}

		return buf;
	}

	/**
	 * Appends the passed field value to the provided buffer.
	 * 
	 * @param <T> the values class.
	 * @param buf a buffer.
	 * @param fieldName the field names.
	 * @param values a list of values.
	 * @return the passed buffer.
	 */
	public static <T extends BaseModelInterface<T>> StringBuilder append(final StringBuilder buf, 
			final String fieldName, final Collection<T> values) {
		if (!Utils.isEmpty(values)) {
			separator(buf);
			appendFieldName(buf, fieldName)
				.append('[');
			for (final T value : values) {
				value.toString(buf);
			}
			buf.append(']');
		}

		return buf;
	}

	/**
	 * Appends the separator if appropriate into the passed buffer.
	 * 
	 * @param buf a buffer.
	 * @return the passed buffer.
	 */
	static StringBuilder separator(final StringBuilder buf) {
		if (buf.length() > 0) {
			int index = buf.length() - 1;

			if (buf.charAt(index) != '{' && buf.charAt(index) != '[') {
				buf.append(", ");
			}
		}

		return buf;
	}

	private static StringBuilder appendFieldName(final StringBuilder buf, final String fieldName) {
		if (!Utils.isEmpty(fieldName)) {
			buf.append(fieldName)
				.append(": ");
		}

		return buf;
	}

} // end class BaseModel
