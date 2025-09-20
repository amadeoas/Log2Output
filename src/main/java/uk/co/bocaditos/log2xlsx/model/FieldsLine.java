package uk.co.bocaditos.log2xlsx.model;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.BaseModelInterface;


/**
 * Fields from a file line.
 */
@SuppressWarnings("serial")
public class FieldsLine extends ArrayList<Field> implements BaseModelInterface<FieldsLine> {

	public Object get(final String fieldName) {
		if (Utils.isEmpty(fieldName)) {
			return null;
		}

		final Field field = stream()
			.filter(f -> fieldName.equals(f.getFieldName()))
			.findFirst()
			.orElse(null);

		return (field == null) ? null : field.getValue();
	}

	public final Object get(final LogField field) {
		return get(field.getHeaderName());
	}

	@Override
	public boolean equalsIt(final FieldsLine obj) {
		return Objects.equals((ArrayList<Field>) this, (ArrayList<Field>) obj);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		return BaseModel.append(buf, null, (ArrayList<Field>) this);
	}

} // end class FieldsLine
