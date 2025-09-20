package uk.co.bocaditos.utils.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;


/**
 * JUnit tests from class CmdHelpArgDef.
 */
public class CmdHelpArgDefTest {

	@Test
	public void test() throws CmdException {
		final String NAME = "name";
		final String DESCRIPTION = "descritpion";
		final CmdHelpArgDef def = new CmdHelpArgDef(NAME, DESCRIPTION);

		assertEquals(NAME, def.getName());
		assertEquals(DESCRIPTION + ".", def.getDescription());
		assertEquals(
				"name: \"" + NAME + "\", description: \"" + DESCRIPTION + ".\", required: false", 
				def.toString());
		
		assertEquals(" [-name]", def.buildHeader(new StringBuilder()).toString());
		assertEquals(" [-name]\n	descritpion.\n", def.buildBody(new StringBuilder()).toString());

		assertThrows(CmdException.class, () -> {
				new CmdHelpArgDef(null, DESCRIPTION);
			});
		assertThrows(CmdException.class, () -> {
			new CmdHelpArgDef("", DESCRIPTION);
		});
		assertThrows(CmdException.class, () -> {
				new CmdHelpArgDef(NAME, null);
			});
		assertThrows(CmdException.class, () -> {
				new CmdHelpArgDef(NAME, "");
			});
	}

} // end class CmdHelpArgDefTest
