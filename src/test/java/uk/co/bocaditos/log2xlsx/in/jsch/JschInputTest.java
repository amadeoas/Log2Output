package uk.co.bocaditos.log2xlsx.in.jsch;

import org.junit.Test;

import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * JUnit tests for class JschInput.
 */
public class JschInputTest {

	@Test
	public void test() throws InputException, CmdException {
		final String[] args = {
				CmdArgs.START + JschInput.CMD_HOST, 	 "localhost",
				CmdArgs.START + JschInput.CMD_PORT,		 "22999",
				CmdArgs.START + JschInput.CMD_KNOWHOSTS, "jschPassword",
				CmdArgs.START + JschInput.CMD_USERNAME , "jschUsername",
				CmdArgs.START + JschInput.CMD_PASSWORD , "jschPassword",
				CmdArgs.START + JschInput.CMD_DIR,		 "jschDir",
				CmdArgs.START + JschInput.CMD_FILENAMES, "jschFiles"
			};
		final CmdArgs cmdArgs = new CmdArgs(args);
		final String[] args1 = {
				CmdArgs.START + Input.CMD_LOGS, JschInput.ID
			};
//		final JschInput input = new JschInput(cmdArgs, args1); 
//
//		
	}

} // end class JschInputTest
