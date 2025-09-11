package uk.co.bocaditos.utils;

import java.text.MessageFormat;
import java.util.Arrays;


/**
 *Support to build exception from format message and its arguments.
 */
@SuppressWarnings("serial")
public class UtilsException extends Exception {

	public UtilsException(final String format, final Object... args) {
		this((Throwable) null, format, args);
	}

	public UtilsException(final Throwable cause, final String format, final Object... args) {
		super(build(format, args), cause);
	}

	public static String build(final String format, final Object... args) {
		return MessageFormat.format(format, args);
	}

	/**
	 * Builds a message based on the provided parameters/arguments.
	 * 
	 * @param cause the cause.
	 * @param msgFormat the error message format.
	 * @param baseArgs the base arguments.
	 * @param args the message arguments.
	 * @return the built message  based on the provided parameters/arguments.
	 */
	protected static String buildMsg(final Throwable cause, final String msgFormat, 
			final Object[] baseArgs, final Object... args) {
		final Object[] mergedArgs;

		if (Utils.isEmpty(baseArgs)) {
			mergedArgs = args;
		} else if (Utils.isEmpty(args)) {
			mergedArgs = baseArgs;
		} else {
			mergedArgs = Arrays.copyOf(baseArgs, baseArgs.length +  args.length);
			for (int i = 0, j = baseArgs.length; i < args.length; ++i, ++j) {
				mergedArgs[j] = args[i];
			}
		}

		return buildMsg(cause, msgFormat, mergedArgs);
	}

	private static String buildMsg(final Throwable thrown, final String msgFormat, 
			final Object... args) {
		if (Utils.isEmpty(args)) {
			if (msgFormat == null) {
				return thrown.getMessage();
			}

			return msgFormat;
		}

		return MessageFormat.format(msgFormat, args);
	}

} // end class UtilsException
