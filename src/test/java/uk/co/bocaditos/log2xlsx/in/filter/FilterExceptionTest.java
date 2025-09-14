package uk.co.bocaditos.log2xlsx.in.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * JUnit tests for class FilterException.
 */
public class FilterExceptionTest {

	@Test
	public void test() {
		final FilterException exception = new FilterException("Value {0}", "THIS");

		assertEquals("Value THIS", exception.getMessage());
	}

} // end class FilterExceptionTest
