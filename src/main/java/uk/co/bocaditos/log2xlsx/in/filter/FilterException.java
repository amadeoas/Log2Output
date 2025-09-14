package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.utils.UtilsException;


/**
 * .
 */
@SuppressWarnings("serial")
public class FilterException extends UtilsException {

	public FilterException(final String format, final Object... args) {
		super(format, args);
	}

} // end class FilterException
