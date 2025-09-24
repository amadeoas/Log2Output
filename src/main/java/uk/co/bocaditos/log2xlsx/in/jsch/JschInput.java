package uk.co.bocaditos.log2xlsx.in.jsch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;
import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to use JSch library to access remote log files.
 * 
 * -logs JSCH h:<host> [p:<port|22>] k:<known_host> u:<user> ps:<password> d:<dir> [f:<file>[,<file>...]]
 */
public class JschInput extends Input {

	public static final String ID = "JSCH";

	static final String CDM_HOST	  = "jschHost";
	static final String CDM_PORT	  = "jschPort";
	static final String CDM_KNOWHOSTS = "jschKnownHost";
	static final String CDM_USERNAME  = "jschUsername";
	static final String CDM_PASSWORD  = "jschPassword";
	static final String CDM_DIR		  = "jschDir";
	static final String CDM_FILENAMES = "jschFiles";
	static final String CDM_STRICT_HOST_KEY_CHECKING = "stricthostkeychecking";

	// JSCH h:<host> [p:<port|22>] k:<known_host> u:<user> ps:<password> d:<dir> [f:<file>[,<file>...]]
	static final String ARG_HOST	  = "h:";
	static final String ARG_PORT	  = "p:";
	static final String ARG_KNOWHOSTS = "k:";
	static final String ARG_USERNAME  = "u:";
	static final String ARG_PASSWORD  = "ps:";
	static final String ARG_DIR		  = "d:";
	static final String ARG_FILENAMES = "f:";

	private static final String DEFAULT_KNOWHOSTS 
			= System.getProperty("os.name").startsWith("Windows") 
				? System.getProperty("USERPROFILE") + "\\.ssh" : "~/.ssh/known_hosts";
	private static final int DEFAULT_PORT = 22;

	private final ChannelSftp sftp;
	private BufferedReader in;
	private String[] filenames;
	private int indexFile;


	public JschInput(final CmdArgs cmdArgs, final String... args) 
			throws InputException, CmdException {
		super(new String[] {""});

		this.sftp = setupJsch(cmdArgs, args);

		final String dir = getValue(cmdArgs, CDM_DIR, args, "d:");

		if (dir == null) {
			try {
				this.sftp.cd(dir);
			} catch (final SftpException se) {
				throw new InputException(se, "Failed to cd to directory \"{1}\" for \"{0}\"", 
						ID, dir);
			}

			try {
				List<LsEntry> entries = this.sftp.ls(".");
				final String str = getValue(cmdArgs, CDM_FILENAMES, args, ARG_FILENAMES);

				if (str != null) {
					entries = entries.stream()
						.filter(entry -> entry.getFilename().startsWith(str))
						.collect(Collectors.toList());
				}

				this.filenames = new String[entries.size()];
				for (int index = 0; index < entries.size(); ++index) {
					final LsEntry entry = entries.get(index);

					this.filenames[index] = entry.getFilename();
				}
			} catch (final SftpException se) {
				throw new InputException(se, 
						"Failed to get the files in directory \"{1}\" for \"{0}\"", 
						ID, dir);
			}
		} else {
			this.filenames = getValue(cmdArgs, CDM_FILENAMES, args, ARG_FILENAMES).split(",");
		}
	}

	@Override
	public void close() throws IOException {
		IOException exc = null;

		if (this.in != null) {
			final BufferedReader in = this.in;

			this.in = null;
			try {
				in.close();
			} catch (final IOException ioe) {
				exc = ioe;
			}
		}

		if (this.sftp != null) {
			this.sftp.exit();
		}

		if (exc != null) {
			throw exc;
		}
	}

	@Override
	public String readLine() throws InputException {
		try {
			String line = "";

			do {
				if (line == null) {
					this.in.close();
					this.in = null;
				}

				if (this.in == null) {
					if (++this.indexFile >= this.filenames.length) {
						return null;
					}

					final InputStreamReader reader 
							= new InputStreamReader(this.sftp.get(this.filenames[this.indexFile]));

					this.in = new BufferedReader(reader);
				}
				line = this.in.readLine();
			} while (line == null);

			return line;
		} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed to access file(s) for \"{0}\"", ID);
		} catch (final SftpException se) {
			throw new InputException(se, "Failed to access file(s) with SFTP for \"{0}\"", ID);
		}
	}

	@Override
	public String getId() {
		return ID;
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(CDM_HOST, "Sets the host to JSCH.", false, 
				new CmdHelpArgParamDef("hos", "The name of the host.", true));
		new CmdHelpArgDef(CDM_PORT, "Sets the port. Default " + DEFAULT_PORT, 
				false, new CmdHelpArgParamDef("port", "The port number.", true));
		new CmdHelpArgDef(CDM_KNOWHOSTS, "Sets the know hosts. Default " + DEFAULT_KNOWHOSTS, 
				false, new CmdHelpArgParamDef("filename", "The name of the knowhots.", true));
		new CmdHelpArgDef(CDM_USERNAME, "Sets the user's name.", false, 
				new CmdHelpArgParamDef("username", "The user's name.", true));
		new CmdHelpArgDef(CDM_PASSWORD, "Sets the pasword.", false, 
				new CmdHelpArgParamDef("password", "The password.", true));
		new CmdHelpArgDef(CDM_DIR, "Sets the directory from where to reade the loag files.", false, 
				new CmdHelpArgParamDef("dir", "The directory.", true));
		new CmdHelpArgDef(CDM_FILENAMES, "Sets the log files to read as comma separated list if " 
				+ CDM_DIR + " has not been set otherwise the starting text in the name of the " 
				+ "files to read from the set directory.", false, 
				new CmdHelpArgParamDef("filenames", "The directory.", true));
		new CmdHelpArgDef(CDM_STRICT_HOST_KEY_CHECKING, 
				"If exists set the stricthostkeychecking as \"yes\", otherwise \"no\".", false);
	}

	private ChannelSftp setupJsch(final CmdArgs cmdArgs, final String... args) 
			throws InputException, CmdException {
	    final JSch jsch = new JSch();
	    final String password = getValue(cmdArgs, CDM_PASSWORD, args, ARG_PASSWORD);

	    try {
	    	final Session jschSession;

	    	jsch.setKnownHosts(getValue(cmdArgs, CDM_KNOWHOSTS, args, ARG_KNOWHOSTS, DEFAULT_KNOWHOSTS));
		    jschSession = jsch.getSession(
		    		getValue(cmdArgs, CDM_USERNAME, args, ARG_USERNAME), 
		    		getValue(cmdArgs, CDM_HOST, args, ARG_HOST), 
		    		getValue(cmdArgs, CDM_PORT, args, ARG_PORT, DEFAULT_PORT));
		    jschSession.setPassword(password);
		    jschSession.setConfig("stricthostkeychecking", 
		    		cmdArgs.contains(CDM_STRICT_HOST_KEY_CHECKING) ? "yes" : "no");
		    jschSession.connect();

		    return (ChannelSftp) jschSession.openChannel("sftp");
		} catch (final JSchException je) {
			throw new InputException(je, "Failed to setup \"{0}\"", ID);
		}
	}

	private static String getValue(final CmdArgs cmdArgs, final String cmdKey, final String[] args, 
			final String argsKey) throws CmdException {
		return getValue(cmdArgs, cmdKey, args, argsKey, null);
	}

	private static String getValue(final CmdArgs cmdArgs, final String cmdKey, final String[] args, 
			final String argsKey, final String defaultValue) throws CmdException {
		final String value = cmdArgs.getParam(cmdKey, getValue(args, argsKey, defaultValue));

		if (value == null) {
			throw new CmdException("Missing argument \"{1}\" for \"{0}\"", ID, cmdKey);
		}

		return value;
	}

	private static int getValue(final CmdArgs cmdArgs, final String cmdKey, final String[] args, 
			final String argsKey, final int defaultValue) throws CmdException {
		final String value = cmdArgs.getParam(cmdKey, getValue(args, argsKey, ""));

		if (value.isEmpty()) {
			return defaultValue;
		}

		return Integer.parseInt(value);
	}

	private static String getValue(final String[] args, final String key, 
			final String defaultValue) {
		if (Utils.isEmpty(args)) {
			return defaultValue;
		}

		for (final String arg : args) {
			if (arg.startsWith(key)) {
				return arg.substring(key.length());
			}
		}

		return defaultValue;
	}

} // end class JschInput
