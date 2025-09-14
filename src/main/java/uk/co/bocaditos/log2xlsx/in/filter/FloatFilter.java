package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/*float
 * Filter for long numbers.
 */
public class FloatFilter extends ComparableFilter<Float> {

	FloatFilter(final LogField field, final Float equal) throws FilterException {
		super(field, equal);
	}

	FloatFilter(final LogField field, final Float from, final Float to) throws FilterException {
		super(field, from, to);
	}

} // end class FloatFilter
