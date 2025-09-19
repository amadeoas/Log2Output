package uk.co.bocaditos.log2xlsx.in;

import static org.junit.Assert.assertThrows;

import org.junit.Test;


/**
 * JUnits tests for class Input.
 */
public class InputTest {

	@Test
	public void buildTest() {
		final String[][] argss = {
				null, new String[0], {"filename.txt"}
			};

		for (final String[] args : argss) {
			assertThrows(InputException.class, () -> {
				Input.build((String[]) args);
			});
		}
	}

} // end class InputTest
