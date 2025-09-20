package uk.co.bocaditos.log2xlsx.out;

import uk.co.bocaditos.log2xlsx.model.FieldNames;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to output log lines into a different format.
 */
public interface LogOutput {

	public static final String ARG_OUT = "out"; // -out <filename>
	public static final String ARG_HEADERS_SORT = "sort";

	/**
	 * Loads the provides fields into output
	 * 
	 * @param set the value of the field, i.e. cell value.
	 * @throws UtilsException when .
	 */
	public void write(CmdArgs args, FieldsSet set) throws UtilsException;

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(ARG_OUT, "Sets the output file name.", true, 
				new CmdHelpArgParamDef("filename", "A filename for the outpur.", true));
		new CmdHelpArgDef(ARG_HEADERS_SORT, "Sets the order of the columns based on the fields " 
				+ "names separated by comma. The field names not present will not be part of the " 
				+ "output", 
				false,
				new CmdHelpArgParamDef("fieldnames", "A list of the field names separated by comma.",
					true));
	}

	public static FieldNames sorted(final CmdArgs cmdArgs, final FieldsSet set) {
		final String names = cmdArgs.getParam(ARG_HEADERS_SORT, (String) null);
		final String[] ns;

		if (names == null) {
			ns = null;
		} else {
			ns = names.split(",");
		}

		return set.getHeaders(ns);
	}

} // end interface LogOutput
