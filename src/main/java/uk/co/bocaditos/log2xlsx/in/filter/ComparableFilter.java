package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * General representation of a filter for a number.
 * 
 * @param <T> an object of type Number.
 */
public abstract class ComparableFilter<T extends Comparable<T>> extends FieldFilter<T> {

	ComparableFilter(final LogField field, final T equal) throws FilterException {
		super(field, equal);

		if (equal == null) {
			throw new FilterException("Missing the double value to use in filter");
		}
	}

	ComparableFilter(final LogField field, final T from, final T to) throws FilterException {
		super(field, from, to);

		if (from == null) {
			throw new FilterException("Missing the 'from' to use in filter {0}", 
					getClass().getName());
		}

		if (to == null) {
			throw new FilterException("Missing the 'to' to use in filter {0}", getClass().getName());
		}

		if (from.compareTo(to) > 0) {
			throw new FilterException("The 'from' bigger than the 'to' one: {0, number} > {1, number}", 
					from, to);
		}
	}

	public T from() {
		return getValues()[0];
	}

	public T to() {
		return (getValues().length == 1) ? null : getValues()[1];
	}

	@SuppressWarnings("unchecked")
	@Override
	final boolean valid_(final Object v) {
		final T value = (T) v;

		if (from().compareTo(value) > 0) {
			return false;
		}

		return to().compareTo(value) >= 0;
	}

} // end class ComparableFilter
