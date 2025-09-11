package uk.co.bocaditos.utils.cmd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * JUnit tests for class CmdException
 */
public class CmdExceptionTest {

	@Test
	public void test() {
		final String MSG = "Message here!";

		assertEquals(MSG, new CmdException(MSG).getMessage());
	}

} // end class CmdExceptionTest
