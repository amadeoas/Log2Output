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
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to use JSch library to access remote log files.
 */
public class JschOutput extends Input {

	private static final String ARG_HOST	  = "jschKnownHost";
	private static final String ARG_KNOWHOSTS = "jschPassword";
	private static final String ARG_USERNAME  = "jschUsername";
	private static final String ARG_PASSWORD  = "jschPassword";
	private static final String ARG_DIR		  = "jschDir";
	private static final String ARG_FILENAMES = "jschFiles";

	private static final String DEFAULT_KNOWHOSTS 
			= System.getProperty("os.name").startsWith("Windows") 
				? System.getProperty("USERPROFILE") + "\\.ssh" : "/Users/john/.ssh/known_hosts";

	private final ChannelSftp sftp;
	private BufferedReader in;
	private String[] filenames;
	private int indexFile;


	public JschOutput(final CmdArgs cmdArgs) throws UtilsException {
		this.sftp = setupJsch(cmdArgs);

		final String dir = cmdArgs.getParam(ARG_DIR, (String) null);

		if (dir == null) {
			try {
				this.sftp.cd(dir);
			} catch (final SftpException se) {
				throw new InputException(se, "Failed to cd to directory \"{0}\"", dir);
			}

			try {
				List<LsEntry> entries = this.sftp.ls(".");
				final String str = cmdArgs.getParam(ARG_FILENAMES, (String) null);

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
				throw new InputException(se, "Failed to get the files in directory \"{0}\"", dir);
			}
		} else {
			this.filenames = cmdArgs.getArgument(ARG_FILENAMES).split(",");
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
					this.in = new BufferedReader(new InputStreamReader(this.sftp.get(this.filenames[this.indexFile])));
				}
				line = this.in.readLine();
			} while (line == null);

			return line;
		} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed to access file(s)");
		} catch (final SftpException se) {
			throw new InputException(se, "Failed to access file(s) with SFTP");
		}
	}

	@Override
	public String getId() {
		return "JSCH";
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(ARG_HOST, "Sets the host to JSCH.", false, 
				new CmdHelpArgParamDef("hos", "The name of the host.", true));
		new CmdHelpArgDef(ARG_KNOWHOSTS, "Sets the know hosts. Default " + DEFAULT_KNOWHOSTS, 
				false, new CmdHelpArgParamDef("filename", "The name of the knowhots.", true));
		new CmdHelpArgDef(ARG_USERNAME, "Sets the user's name.", false, 
				new CmdHelpArgParamDef("username", "The user's name.", true));
		new CmdHelpArgDef(ARG_PASSWORD, "Sets the pasword.", false, 
				new CmdHelpArgParamDef("password", "The password.", true));
		new CmdHelpArgDef(ARG_DIR, "Sets the directory from where to reade the loag files.", false, 
				new CmdHelpArgParamDef("dir", "The directory.", true));
		new CmdHelpArgDef(ARG_FILENAMES, "Sets the log files to read as comma separated list if " 
				+ ARG_DIR + " has not been set otherwise the starting text in the name of the " 
				+ "files to read from the set directory.", false, 
				new CmdHelpArgParamDef("filenames", "The directory.", true));
	}

	private ChannelSftp setupJsch(final CmdArgs cmdArgs) throws InputException, CmdException {
	    final JSch jsch = new JSch();
	    final String password = cmdArgs.getArgument(ARG_PASSWORD);

	    try {
	    	final Session jschSession;

	    	jsch.setKnownHosts(cmdArgs.getParam(ARG_KNOWHOSTS, DEFAULT_KNOWHOSTS));
		    jschSession = jsch.getSession(cmdArgs.getArgument(ARG_USERNAME), 
		    		cmdArgs.getArgument(ARG_HOST));
		    jschSession.setPassword(password);
		    jschSession.connect();

		    return (ChannelSftp) jschSession.openChannel("sftp");
		} catch (final JSchException je) {
			throw new InputException(je, "Failed to setup JSch");
		}
	}

} // end class JschOutput
