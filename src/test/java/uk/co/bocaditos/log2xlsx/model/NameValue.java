package uk.co.bocaditos.log2xlsx.model;

import java.util.Objects;

/**
 * Test class.
 */
public class NameValue {

	private String name;
	private String value;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof NameValue)) {
			return false;
		}

		final NameValue nv = (NameValue) obj;

		return Objects.equals(nv.name, nv.name)
				&& Objects.equals(nv.value, nv.value);
	}

} // end class NameValue
