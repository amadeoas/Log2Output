package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for double numbers.
 */
public class DoubleFilter extends ComparableFilter<Double> {

	DoubleFilter(final LogField field, final Double equal) throws FilterException {
		super(field, equal);
	}

	DoubleFilter(final LogField field, final Double from, final Double to) throws FilterException {
		super(field, from, to);
	}

} // end class DoubleFilter
