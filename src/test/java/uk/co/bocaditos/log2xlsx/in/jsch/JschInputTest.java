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
				CmdArgs.START + JschInput.ARG_HOST, 	 "localhost",
				CmdArgs.START + JschInput.ARG_PORT,		 "22999",
				CmdArgs.START + JschInput.ARG_KNOWHOSTS, "jschPassword",
				CmdArgs.START + JschInput.ARG_USERNAME , "jschUsername",
				CmdArgs.START + JschInput.ARG_PASSWORD , "jschPassword",
				CmdArgs.START + JschInput.ARG_DIR,		 "jschDir",
				CmdArgs.START + JschInput.ARG_FILENAMES, "jschFiles"
			};
		final CmdArgs cmdArgs = new CmdArgs(args);
		final String[] args1 = {
				CmdArgs.START + Input.ARG_LOGS, JschInput.ID
			};
//		final JschInput input = new JschInput(cmdArgs, args1); 
//
//		
	}

} // end class JschInputTest
