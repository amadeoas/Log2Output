package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class LongFilter extends FieldFilter<Long> {

	LongFilter(final LogField field, final Long equal) {
		super(field, equal);
	}

	LongFilter(final LogField field, final Long from, final Long to) {
		super(field, from, to);
	}

	@Override
	boolean valid_(final Object v) {
		final long value = (long) v;
		final long from = (long) getFirstValue();
		final long to = (long) getValues()[1];

		return (from >= value) ? true : (to <= value);
	}

} // end class LongFilter
