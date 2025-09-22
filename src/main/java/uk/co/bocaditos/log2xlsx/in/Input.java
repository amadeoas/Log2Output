package uk.co.bocaditos.log2xlsx.in;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import uk.co.bocaditos.log2xlsx.in.jsch.JschInput;
import uk.co.bocaditos.log2xlsx.in.local.FilesInput;
import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * .
 */
public abstract class Input implements Closeable {

	public static final String ARG_LOGS = "logs";


	protected Input(final String... args) throws InputException {
		if (Utils.isEmpty(args)) {
			throw new InputException("No arguments to setup input");
		}
	}

	public abstract String readLine() throws InputException;

	public abstract String getId();

	public static Input build(final CmdArgs cmdArgs) throws InputException, CmdException {
		if (cmdArgs == null) {
			throw new CmdException("Missing CMD line");
		}

		List<String> args = cmdArgs.getArguments(ARG_LOGS);

		if (Utils.isEmpty(args)) {
			throw new InputException("Missing arguments to build input logs support");
		}

		if (args.get(0).contains(".")) {
			final String[] filenames = args.get(0).split(",");

			for (int index = 0; index < args.size(); ++index) {
				filenames[index] = args.get(index).trim();
			}

			if (FilesInput.isValid(filenames)) {
				return new FilesInput(filenames);
			}
		} else if (JschInput.ID.equals(args.get(0).toUpperCase())) {
			return new JschInput(cmdArgs);
		} else if (FilesInput.ID.equals(args.get(0).toUpperCase())) {
			if (args.size() == 2) {
				final String[] filenames = args.get(1).split(",");

				for (int index = 0; index < filenames.length; ++index) {
					filenames[index] = filenames[index].trim();
				}

				return new FilesInput(filenames);
			}
		} else if (args.size() == 1) {
	    	// It's a directory
	    	final File dir = new File(args.get(0));

	    	if (dir.isDirectory()) {
	    		String dirPath = args.get(0);
	    		final String[] filenames;

	    		filenames = dir.list(new FilenameFilter() {

					@Override
					public boolean accept(final File dir, final String name) {
						return name.endsWith(".log");
					}

	    		});
	    		if (dirPath.charAt(dirPath.length() - 1) != '\\' 
	    				&& dirPath.charAt(dirPath.length() - 1) != '/') {
					dirPath += '/';
	   			}

	    		for (int index = 0; index < filenames.length; ++index) {
	    			filenames[index] = dirPath + filenames[index];
	    		}

	    		return new FilesInput(filenames);
	    	}
		}

		throw new InputException("Unsupported imput [{0}]", Utils.concatenate(args));
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(ARG_LOGS, "Sets the files or directory with the log lines.", true, 
				new CmdHelpArgParamDef("type", "The type of input, i.e. \"" + FilesInput.ID 
					+ " or \"" + JschInput.ID + "\". \"" + FilesInput.ID + "\" reads the log file " 
					+ "from local file system and \"" + JschInput.ID + "\" from a remote. The " 
					+ "abscence of this value means " + FilesInput.ID,
					false),
				new CmdHelpArgParamDef("others", "If a just one value without '.' then it is " 
					+ "interpreted as a directory. A type of \"" + FilesInput.ID + "\" means the " 
					+ "following arguments are a comma separated list of filenamess, where all " 
					+ "filenames must end with \".log\", in the case of a directory only the "
					+ "contained files ending with \".log\" will be used.", 
					true));
	}

} // end interface Input
