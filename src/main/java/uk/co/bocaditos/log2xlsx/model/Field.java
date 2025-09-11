package uk.co.bocaditos.log2xlsx.model;

import java.util.Objects;

import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.BaseModelInterface;


/**
 * Represent a log field with an appropriate value from a log line.
 */
public class Field implements BaseModelInterface<Field> {

	private final Object value;
	private final LogField lf;


	public Field(final LogField lf, final String value) throws FormatException {
		this.lf = lf;
		this.value = this.lf.build(value);
	}

	public String getFieldName() {
		return this.lf.getId();
	}

	public Object getValue() {
		return this.value;
	}

	public String getFormatedValue() throws FormatException {
		return this.lf.getValue(this.value);
	}

	public Class<?> getFieldClass() {
		return this.lf.getFieldClass();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.lf);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Field)) {
			return false;
		}

		return equalsIt((Field) obj);
	}

	@Override
	public boolean equalsIt(final Field field) {
		return Objects.equals(this.lf, field.lf)
				&& Objects.equals(this.value, field.value);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		BaseModel.append(buf, "id", this.lf.getId());
		BaseModel.append(buf, "value", this.value.toString());

		return buf;
	}

} // end class Field
