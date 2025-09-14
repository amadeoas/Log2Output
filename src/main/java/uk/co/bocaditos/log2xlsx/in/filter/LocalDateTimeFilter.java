package uk.co.bocaditos.log2xlsx.in.filter;

import java.time.LocalDateTime;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class LocalDateTimeFilter extends FieldFilter<LocalDateTime> {

	LocalDateTimeFilter(final LogField field, final LocalDateTime from) throws FilterException {
		super(field, from);

		if (from == null) {
			throw new FilterException("Missing the date to use in filter");
		}
	}

	LocalDateTimeFilter(final LogField field, final LocalDateTime from, final LocalDateTime to) 
			throws FilterException {
		super(field, from, to);

		if (from == null || to == null) {
			throw new FilterException("Missing at least on of the dates and times to use in filter");
		}

		if (from.isAfter(to)) {
			throw new FilterException("Date and time 'from' bigger than the 'to' one");
		}
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

		if (valid && to() != null) {
			valid = to().equals(value) || to().isAfter(value);
		}

		return valid;
	}

} // end class LocalDateTimeFilter
