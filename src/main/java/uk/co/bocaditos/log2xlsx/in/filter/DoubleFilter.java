package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class DoubleFilter extends FieldFilter<Double> {

	DoubleFilter(final LogField field, final Double equal) {
		super(field, equal);
	}

	DoubleFilter(final LogField field, final Double from, final Double to) {
		super(field, from, to);
	}

	@Override
	boolean valid_(final Object v) {
		final double value = (double) v;
		final double from = (double) getFirstValue();
		final double to = (double) getValues()[1];

		return (from >= value) ? true : (to <= value);
	}

} // end class DoubleFilter
