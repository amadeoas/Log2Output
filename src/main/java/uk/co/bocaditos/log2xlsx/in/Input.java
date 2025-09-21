package uk.co.bocaditos.log2xlsx.in;

import java.io.Closeable;
import java.util.Arrays;

import uk.co.bocaditos.log2xlsx.in.jsch.JschOutput;
import uk.co.bocaditos.log2xlsx.in.local.FilesInput;
import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * .
 */
public abstract class Input implements Closeable {

	protected Input(final String... args) throws InputException {
		if (Utils.isEmpty(args)) {
			throw new InputException("No arguments to setup input");
		}
	}

	public abstract String readLine() throws InputException;

	public abstract String getId();

	public static Input build(final CmdArgs cmdArgs, String... args) 
			throws InputException, CmdException {
		if (Utils.isEmpty(args)) {
			throw new InputException("Missing arguments to build input support");
		}

		if (JschOutput.ID.equals(args[0])) {
			return new JschOutput(cmdArgs, args);
		}

		if (JschOutput.ID.equals(args[0])) {
			args = Arrays.copyOf(args, 1);
		}

		if (FilesInput.isValid(args)) {
			return new FilesInput(args);
		}

		throw new InputException("Unsupported imput [{0}]", Utils.concatenate(args));
	}

} // end interface Input
