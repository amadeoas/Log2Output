package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for byte numbers.
 */
public class ByteFilter extends ComparableFilter<Byte> {

	ByteFilter(final LogField field, final Byte equal) throws FilterException {
		super(field, equal);
	}

	ByteFilter(final LogField field, final Byte from, final Byte to) throws FilterException {
		super(field, from, to);
	}

} // end class LongFilter