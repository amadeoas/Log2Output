package uk.co.bocaditos.log2xlsx.in.filter;

import java.time.LocalDateTime;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class LocalDateTimeFilter extends FieldFilter<LocalDateTime> {

	LocalDateTimeFilter(final LogField field, final LocalDateTime from) {
		super(field, from);
	}

	LocalDateTimeFilter(final LogField field, final LocalDateTime from, final LocalDateTime to) {
		super(field, from, to);
	}

	public final LocalDateTime from() {
		return getValues()[0];
	}

	public final LocalDateTime to() {
		return (getValues().length == 1) ? null : getValues()[1];
	}

	@Override
	public boolean valid_(final Object v) {
		final LocalDateTime value = (LocalDateTime) v;
		boolean valid = from().equals(value) || from().isBefore(value);

		if (valid) {
			valid = to().equals(value) || to().isAfter(value);
		}

		return valid;
	}

} // end class LocalDateTimeFilter
