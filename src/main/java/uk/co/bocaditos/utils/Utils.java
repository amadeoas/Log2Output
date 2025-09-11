package uk.co.bocaditos.utils;

import java.util.Collection;


/**
 * .
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

	private Utils() {
	}

} // end class Utils
