package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for integer numbers.
 */
public class IntFilter extends ComparableFilter<Integer> {

	IntFilter(final LogField field, final Integer equal) throws FilterException {
		super(field, equal);
	}

	IntFilter(final LogField field, final Integer from, final Integer to) throws FilterException {
		super(field, from, to);
	}

} // end class IntFilter
