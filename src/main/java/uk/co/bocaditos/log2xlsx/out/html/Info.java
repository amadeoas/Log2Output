package uk.co.bocaditos.log2xlsx.out.html;

import uk.co.bocaditos.log2xlsx.Application;
import uk.co.bocaditos.log2xlsx.in.filter.FieldFilter;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * Holds information about the run.
 */
public class Info {

	private final String formats;
	private final String idFiledName;
	private final String logFiles;
	private final String filter;


	public Info(final CmdArgs cmdArgs) throws CmdException {
		if (cmdArgs == null) {
			throw new CmdException("Missing CMD Line areguments/parameters");
		}

		this.formats = cmdArgs.getArgument(Application.ARG_FORMATS);
		this.idFiledName = cmdArgs.getArgument(Application.ARG_ID_FIELD_NAME);
		this.logFiles = cmdArgs.getArgument(Application.ARG_LOGS);
		this.filter = cmdArgs.getParam(FieldFilter.ARG_FILTER_FIELD_NAME, (String) null);
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
