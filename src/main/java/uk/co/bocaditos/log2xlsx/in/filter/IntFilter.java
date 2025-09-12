package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class IntFilter extends FieldFilter<Integer> {

	IntFilter(final LogField field, final Integer equal) {
		super(field, equal);
	}

	IntFilter(final LogField field, final Integer from, final Integer to) {
		super(field, from, to);
	}

	@Override
	boolean valid_(final Object v) {
		final int value = (int) v;
		final int from = (int) getFirstValue();
		final int to = (int) getValues()[1];

		return (from >= value) ? true : (to <= value);
	}

} // end class IntFilter
