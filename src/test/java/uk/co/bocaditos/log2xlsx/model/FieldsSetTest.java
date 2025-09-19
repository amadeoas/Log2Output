package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import uk.co.bocaditos.log2xlsx.in.FormatsTest;
import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;


/**
 * JUnit tests for class.
 */
public class FieldsSetTest {

	@Test
	public void test() {
		final String ID = "id";
		final LogSet set = mock(LogSet.class);

		assertThrows(RuntimeException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new FieldsSet(set, null);
			}
			
		});
		assertThrows(FormatException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				final FieldsSet fileSet = new FieldsSet(set, ID);

				assertEquals(ID, fileSet.getIdFieldName());
				assertFalse(fileSet.add((FieldsGroup) null));
				assertEquals("", fileSet.toString());
				assertFalse(fileSet.equals((FieldsSet) null));
				assertFalse(fileSet.equals(""));
				fileSet.load(null, (Input) null);
			}

		});
	}

	@Test
	public void tests() throws FormatException, InputException {
		final LogSet logSet = FormatsTest.load();
		final FieldsSet fileSet = FormatsTest.load(logSet);
		FieldsSet fileSet1;

		assertNotNull(fileSet.getHeaders());
		assertEquals(fileSet, fileSet);
		fileSet1 = new FieldsSet(logSet, "datetime");
		assertNotEquals(fileSet, fileSet1);
		fileSet1 = FormatsTest.load(logSet);
		assertEquals(fileSet, fileSet1);
		fileSet1.remove(1);
		assertNotEquals(fileSet, fileSet1);
	}

	@Test
	public void valueTest() throws FormatException, InputException {
		final LogSet logSet = FormatsTest.load();
		final FieldsSet fileSet = FormatsTest.load(logSet);
		final FieldsLine fieldsLine = fileSet.get(0).get(0);

		assertNull(FieldsSet.value(fieldsLine, null));
		assertNull(FieldsSet.value(fieldsLine, ""));
		assertNull(FieldsSet.value(fieldsLine, ".NONE."));

		fieldsLine.remove(3);
		assertFalse(fileSet.add(fieldsLine));
	}

} // end class FieldsSetTest
