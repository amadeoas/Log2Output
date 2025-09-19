package uk.co.bocaditos.log2xlsx.in;

import uk.co.bocaditos.utils.UtilsException;


/**
 * .
 */
@SuppressWarnings("serial")
public class InputException extends UtilsException {

	public InputException(final String format, final Object... args) {
		this(null, format, args);
	}

	public InputException(final Throwable cause, final String format, final Object... args) {
		super(cause, format, args);
	}

} // end class InputException
