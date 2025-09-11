package uk.co.bocaditos.log2xlsx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.model.BaseModel;
import uk.co.bocaditos.utils.model.BaseModelInterface;


/**
 * Representation of a group of line with the same ID.
 */
@SuppressWarnings("serial")
public class FieldsGroup extends ArrayList<FieldsLine> implements BaseModelInterface<FieldsGroup> {

	static final String FIELDNAME_ID = "id";

	private String id;


	FieldsGroup(final String id, final FieldsLine fields) throws FormatException {
		this.id = id;
		if (!add(fields)) {
			throw new FormatException("Group \"{0}\" without field lines", id);
		}
	}

	public boolean add(final FieldsLine fields) {
		if (Utils.isEmpty(fields)) {
			return false;
		}

		return super.add(fields);
	}

	public String getId() {
		return this.id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof FieldsGroup)) {
			return false;
		}

		return equalsIt((FieldsGroup) obj);
	}

	@Override
	public boolean equalsIt(final FieldsGroup grp) {
		return Objects.equals(this.id, grp.id) && equals_(grp);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		BaseModel.append(buf, "id", this.id);
		BaseModel.append(buf, null, (List<FieldsLine>) this);

		return buf;
	}

	private boolean equals_(final List<FieldsLine> list) {
		if (size() != list.size()) {
			return false;
		}

		for (int index = 0; index < size(); ++index) {
			final FieldsLine field = get(index);
			final FieldsLine listField = list.get(index);

			if (!field.equals(listField)) {
				return false;
			}
		}

		return true;
	}

} // end class FieldsGroup
