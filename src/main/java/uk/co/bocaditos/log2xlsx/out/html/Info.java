package uk.co.bocaditos.log2xlsx.out.html;

import uk.co.bocaditos.log2xlsx.Application;
import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.filter.FieldFilter;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * Holds information about the run.
 */
public class Info {

	private final String appName;
	private final String version;
	private final String formats;
	private final String idFiledName;
	private final String logFiles;
	private final String filter;


	public Info(final String appName, final String version, final CmdArgs cmdArgs) 
			throws CmdException {
		if (cmdArgs == null) {
			throw new CmdException("Missing CMD Line areguments/parameters");
		}

		this.appName = appName;
		this.version = version;
		this.formats = cmdArgs.getArgument(Application.CMD_FORMATS);
		this.idFiledName = cmdArgs.getArgument(Application.CMD_ID_FIELD_NAME);
		this.logFiles = cmdArgs.getArgument(Input.CMD_LOGS);
		this.filter = cmdArgs.getParam(FieldFilter.CMD_FILTER, (String) null);
	}

	public String getAppName() {
		return appName;
	}

	public String getVersion() {
		return version;
	}

	public String getFormats() {
		return formats;
	}

	public String getIdFiledName() {
		return idFiledName;
	}

	public String getLogFiles() {
		return logFiles;
	}

	public String getFilter() {
		return filter;
	}

} // end class Info
