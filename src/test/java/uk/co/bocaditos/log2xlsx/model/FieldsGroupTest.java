package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * JUnit test for class FieldsGroup.
 */
public class FieldsGroupTest {

	@Test
	public void equalsTest() throws FormatException {
		final String ID = "ID";
		LogField logField;
		Field field;
		FieldsLine line;
		final FieldsGroup grp;
		FieldsGroup grp1;

		logField = new LogField(null, ID, String.class, null, null);
		field = new Field(logField, ID);
		line = new FieldsLine();
		line.add(field);
		grp = new FieldsGroup(ID, line);
		assertFalse(grp.equals(null));
		assertFalse(grp.equals(field));
		assertTrue(grp.equals(grp));
		line = new FieldsLine();
		assertFalse(grp.add(line));

		assertEquals("id: \"ID\", [[id: \"ID\", value: \"ID\"]]", grp.toString());

		logField = new LogField(null, ID, String.class, null, null);
		field = new Field(logField, ID);
		line = new FieldsLine();
		line.add(field);
		grp1 = new FieldsGroup(ID, line);
		logField = new LogField(null, "!!!", String.class, null, null);
		field = new Field(logField, ID);
		line = new FieldsLine();
		line.add(field);
		grp1.add(line);
		assertFalse(grp.equals(grp1));

		logField = new LogField(null, "!!!", String.class, null, null);
		field = new Field(logField, ID);
		line = new FieldsLine();
		line.add(field);

		final FieldsLine[] flines = {
				line, new FieldsLine()
		};

		for (final FieldsLine fl : flines) {
			try {
				grp1 = new FieldsGroup(ID, fl);
				assertTrue(fl == flines[0]);
				assertFalse(grp.equals(grp1));
			} catch (final FormatException fe) {
				assertFalse(fl == flines[0]);
			}
		}
	}

} // end class FieldsGroupTest
