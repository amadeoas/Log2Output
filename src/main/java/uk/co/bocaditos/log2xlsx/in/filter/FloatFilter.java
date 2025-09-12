package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


public class FloatFilter extends FieldFilter<Float> {

	FloatFilter(final LogField field, final Float equal) {
		super(field, equal);
	}

	FloatFilter(final LogField field, final Float from, final Float to) {
		super(field, from, to);
	}

	@Override
	boolean valid_(final Object v) {
		final float value = (float) v;
		final float from = (float) getFirstValue();
		final float to = (float) getValues()[1];

		return (from >= value) ? true : (to <= value);
	}

} // end class FloatFilter
