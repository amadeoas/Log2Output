package uk.co.bocaditos.log2xlsx.in.filter;

import uk.co.bocaditos.log2xlsx.model.FieldsLine;


/**
 * Support to filter log lines.
 */
public interface Filter {

	/**
	 * @param line a log line.
	 * @return true if the line is to be processed further.
	 */
	public boolean valid(FieldsLine line);

} // end interface Filter
