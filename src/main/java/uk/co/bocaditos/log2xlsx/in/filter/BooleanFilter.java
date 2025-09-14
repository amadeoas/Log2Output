package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for boolean.
 */
public class BooleanFilter extends ComparableFilter<Boolean> {

	BooleanFilter(final LogField field, final Boolean value) throws FilterException {
		super(field, value);
	}

} // end class BooleanFilter
