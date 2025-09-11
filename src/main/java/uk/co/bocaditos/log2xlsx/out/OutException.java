package uk.co.bocaditos.log2xlsx.out;

import uk.co.bocaditos.utils.UtilsException;


/**
 * .
 */
@SuppressWarnings("serial")
public class OutException extends UtilsException {

	public OutException(final String format, final Object... args) {
		this(null, format, args);
	}

	public OutException(final Throwable cause, final String format, final Object... args) {
		super(cause, format, args);
	}

} // end class OutException
