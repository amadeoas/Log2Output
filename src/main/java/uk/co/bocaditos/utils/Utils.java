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

	private Utils() {
	}

} // end class Utils
