package uk.co.bocaditos.log2xlsx.in.filter;

import java.time.LocalDate;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class LocalDateFilter extends FieldFilter<LocalDate> {

	LocalDateFilter(final LogField field, final LocalDate from) {
		super(field, from);
	}

	LocalDateFilter(final LogField field, final LocalDate from, final LocalDate to) {
		super(field, from, to);
	}

	public final LocalDate from() {
		return getValues()[0];
	}

	public final LocalDate to() {
		return (getValues().length == 1) ? null : getValues()[1];
	}

	@Override
	public boolean valid_(final Object v) {
		final LocalDate value = (LocalDate) v;
		boolean valid = from().equals(value) || from().isBefore(value);

		if (valid) {
			valid = to().equals(value) || to().isAfter(value);
		}

		return valid;
	}

} // end class LocalDateFilter
