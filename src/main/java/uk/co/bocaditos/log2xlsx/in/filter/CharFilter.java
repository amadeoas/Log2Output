package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * Filter for character.
 */
public class CharFilter extends ComparableFilter<Character> {

	CharFilter(final LogField field, final Character value) throws FilterException {
		super(field, value);
	}

} // end class CharFilter
