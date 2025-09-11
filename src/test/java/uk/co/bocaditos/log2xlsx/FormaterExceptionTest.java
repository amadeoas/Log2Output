package uk.co.bocaditos.log2xlsx;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * JUnit tests for class FormaterException.
 */
public class FormaterExceptionTest {

	@Test
	public void test() {
		final String ARG = "TEST!";
		final FormaterException excp = new FormaterException("{0}", ARG);

		assertEquals(ARG, excp.getMessage());
	}

} // end class FormaterExceptionTest
