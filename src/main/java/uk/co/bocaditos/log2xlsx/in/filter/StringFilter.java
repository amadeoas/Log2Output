package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * .
 */
public class StringFilter extends FieldFilter<String> {

	public StringFilter(final LogField field, final String value) 
			throws CmdException, FormatException {
		super(field, value);
	}

} // end class StringFilter
