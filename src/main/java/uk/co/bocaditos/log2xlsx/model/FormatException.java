package uk.co.bocaditos.log2xlsx.model;

import uk.co.bocaditos.utils.UtilsException;


/**
 * .
 */
@SuppressWarnings("serial")
public class FormatException extends UtilsException {

	public FormatException(final String format, final Object... args) {
		this(null, format, args);
	}

	public FormatException(final Throwable cause, final String format, final Object... args) {
		super(cause, format, args);
	}

} // end class FormatException
