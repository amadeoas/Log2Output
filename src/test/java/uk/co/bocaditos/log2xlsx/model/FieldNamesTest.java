package uk.co.bocaditos.log2xlsx.model;

import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import uk.co.bocaditos.utils.UtilsException;


/**
 * JUnit test for class FieldNames.
 */
public class FieldNamesTest {

	@Test
	public void test() {
		final FieldNames fieldNames = new FieldNames();
		final String[] names = {null, "!NOTHING!"};

		for (final String name : names) {
			assertThrows(UtilsException.class, new ThrowingRunnable() {
	
				@Override
				public void run() throws Throwable {
					fieldNames.index(name);
				}

			});
		}
	}

} // end class FieldNamesTest
