package uk.co.bocaditos.log2xlsx.model;

import java.util.LinkedHashSet;

import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.UtilsException;


/**
 * List of all the valid fields - no repetitions.
 */
@SuppressWarnings("serial")
public class FieldNames extends LinkedHashSet<LogField> {

	public FieldNames() {
	}

	public FieldNames(final int initialCapacity) {
		super(initialCapacity);
	}

	public int index(final String fieldName) throws UtilsException {
		if (Utils.isEmpty(fieldName)) {
			throw new UtilsException("No field name was pass to search");
		}

		int index = 0;

		for (final LogField fn : this) {
			if (fieldName.equals(fn.getHeaderName())) {
				return index;
			}
			++index;
		}

		throw new UtilsException("Field with name \"{0}\" doesn't exist", fieldName);
	}

} // end class FieldNames
