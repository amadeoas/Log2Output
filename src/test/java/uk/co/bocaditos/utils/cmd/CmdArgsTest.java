package uk.co.bocaditos.utils.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * JUnits tests for class CmdArgs.
 */
public class CmdArgsTest {

	@Test
	public void test() throws CmdException {
		String[] args = {};
		CmdArgs cmdArgs = new CmdArgs(args);

		assertFalse(cmdArgs.hasHelp());
		assertEquals(0, cmdArgs.getNumArgs());
		assertTrue(cmdArgs.isEmpty());
		assertEquals("default", cmdArgs.getParam("a", "default"));
		assertEquals(10, cmdArgs.getParam("b", 10));
		assertEquals(10l, cmdArgs.getParam("c", 10l));
		assertEquals(10d, cmdArgs.getParam("d", 10d), 0.1);
		assertEquals(10f, cmdArgs.getParam("e", 10f), 0.1);
		assertTrue(cmdArgs.getParam("f", true));
		assertEquals('l', cmdArgs.getParam("g", 'l'));
		assertFalse(cmdArgs.contains("a"));

		cmdArgs = new CmdArgs(new String[] {CmdArgs.START + CdmHelp.PARAM_HELP});
		assertTrue(cmdArgs.hasHelp());
		cmdArgs = new CmdArgs(new String[] {CmdArgs.START + CdmHelp.PARAM_HELP1});
		assertTrue(cmdArgs.hasHelp());

		args = new String[] {CmdArgs.START + "a", "abc", CmdArgs.START + "b", "11",
				CmdArgs.START + "d", "1.1", CmdArgs.START + "f", "true",
				CmdArgs.START + "g", "c"};
		cmdArgs = new CmdArgs(args);
		assertEquals("abc", cmdArgs.getParam("a", "default"));
		assertEquals(11, cmdArgs.getParam("b", 10));
		assertEquals(11l, cmdArgs.getParam("b", 10l));
		assertEquals(1.1d, cmdArgs.getParam("d", 10d), 0.1);
		assertEquals(1.1f, cmdArgs.getParam("d", 10f), 0.1);
		assertTrue(cmdArgs.getParam("f", false));
		assertEquals('c', cmdArgs.getParam("g", 'l'));
		assertTrue(cmdArgs.contains("a"));

		final String[] argNames = {"a", "something"};

		for (int index = 0; index < 2; ++index) {
			try {
				assertEquals("abc", cmdArgs.getArgument(argNames[index]));
				assertEquals(0, index);
				assertEquals(1, cmdArgs.getArguments(argNames[index]).size());
			} catch (final CmdException ce) {
				assertNotEquals(0, index);
			}
		}
	}

} // end class CmdArgsTest
