package uk.co.bocaditos.log2xlsx.in;

import java.io.Closeable;

import uk.co.bocaditos.utils.Utils;


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

	public static Input build(final String... args) throws InputException {
		if (Utils.isEmpty(args)) {
			throw new InputException("Missing arguments to build input support");
		}

		if (FilesInput.isValid(args)) {
			return new FilesInput(args);
		}

		throw new InputException("Unsupported imput [{0}]", Utils.concatenate(args));
	}

} // end interface Input
