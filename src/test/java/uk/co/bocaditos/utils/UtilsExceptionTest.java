package uk.co.bocaditos.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * JUnit tests for class UtilsException.
 */
public class UtilsExceptionTest {

	private static final String FIELD = "Field";
	private static final int NUMBER = 20;
	private static final String MSG = "\"" + FIELD + "\" at " + NUMBER;
	private static final String MSG_FORMAT = "\"{0}\" at {1, number}";


	@Test
	public void test() {
		final UtilsException excp = new UtilsException(MSG_FORMAT, FIELD, NUMBER);

		assertEquals(MSG, excp.getMessage());
	}

	@Test
	public void buildMsgTest() {
		assertEquals(MSG, 
				UtilsException.buildMsg(null, MSG_FORMAT, null, new Object[] {FIELD, NUMBER}));
		assertEquals(MSG, 
				UtilsException.buildMsg(null, MSG_FORMAT, new Object[] {FIELD, NUMBER}, (Object[]) null));
		assertEquals(MSG, 
				UtilsException.buildMsg(null, MSG_FORMAT, new Object[] {FIELD, NUMBER}, new Object[] {}));
		assertEquals(MSG, 
				UtilsException.buildMsg(null, MSG_FORMAT, new Object[] {FIELD}, new Object[] {NUMBER}));

		final String msg = "The message";

		assertEquals(msg, UtilsException.buildMsg(null, msg, null));
		assertEquals(msg, UtilsException.buildMsg(new Exception(msg), null, null));
	}

} // end class UtilsExceptionTest
