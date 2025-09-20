package uk.co.bocaditos.utils.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import org.junit.Test;


/**
 * JUnit tests for class CmdHelpArgParamDef.
 */
public class CmdHelpArgParamDefTest {

	@Test
	public void test() throws CmdException {
		final String NAME = "name";
		final String DESCRIPTION = "descritpion";
		final CmdHelpArgParamDef def = new CmdHelpArgParamDef(NAME, DESCRIPTION);

		assertFalse(def.isRequired());
		assertEquals(NAME, def.getName());
		assertEquals(DESCRIPTION + ".", def.getDescription());
		assertEquals("name: \"name\", description: \"descritpion.\", required: false", 
				def.toString());
		
		assertEquals("[<name>]", def.buildHeader(new StringBuilder()).toString());
		assertEquals("\n\tname\n\t\tdescritpion.\n", def.buildBody(new StringBuilder()).toString());

		assertThrows(CmdException.class, () -> {
				new CmdHelpArgParamDef(null, DESCRIPTION);
			});
		assertThrows(CmdException.class, () -> {
			new CmdHelpArgParamDef("", DESCRIPTION);
		});
		assertThrows(CmdException.class, () -> {
				new CmdHelpArgParamDef(NAME, null);
			});
		assertThrows(CmdException.class, () -> {
				new CmdHelpArgParamDef(NAME, "");
			});
	}

} // end class CmdHelpArgParamDefTest
