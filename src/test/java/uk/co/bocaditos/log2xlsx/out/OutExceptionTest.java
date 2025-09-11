package uk.co.bocaditos.log2xlsx.out;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * JUnit tests for class OutException.
 */
public class OutExceptionTest {

	@Test
	public void test() {
		final OutException excp = new OutException("\"{0}\" at {1, number}", "Field", 20);

		assertEquals("\"Field\" at 20", excp.getMessage());
	}

} // end class OutExceptionTest
