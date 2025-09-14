package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * .
 */
public class StringFilter extends FieldFilter<String> {

	public StringFilter(final LogField field, final String value) throws FilterException {
		super(field, value);

		if (value == null) {
			throw new FilterException("Missing the date to use in filter");
		}
	}

} // end class StringFilter
