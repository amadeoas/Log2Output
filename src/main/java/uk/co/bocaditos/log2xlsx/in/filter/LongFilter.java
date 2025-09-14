package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for long numbers.
 */
public class LongFilter extends ComparableFilter<Long> {

	LongFilter(final LogField field, final Long equal) throws FilterException {
		super(field, equal);
	}

	LongFilter(final LogField field, final Long from, final Long to) throws FilterException {
		super(field, from, to);
	}

} // end class LongFilter
