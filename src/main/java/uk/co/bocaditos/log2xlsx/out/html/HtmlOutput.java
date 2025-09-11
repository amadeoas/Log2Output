package uk.co.bocaditos.log2xlsx.out.html;

import freemarker.template.*;

import java.util.*;
import java.io.*;

import uk.co.bocaditos.log2xlsx.model.FieldNames;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.log2xlsx.out.OutException;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to output log entries into HTML.
 */
public class HtmlOutput implements LogOutput {

	public static final String ARG_DIR4TEMPLATE = "dir4template";
	public static final String ARG_TEMPLATE 	= "template";
	public static final String ARG_ENCODING 	= "encoding";
	public static final String ARG_FREEMAKER_VERSION = "freemakerVersion";
	public static final String ARG_HTML_DEFAULT_SIZE = "htmlDefaultSize";
	public static final String ARG_HTML_SIZE	= "htmlSize";

	// Defaults
	public static final String DEFAULT_TEMPLATE 	= "htmlLogs.ftl";
	public static final String DEFAULT_DIR4TEMPLATE = "/templates";
	public static final String DEFAULT_ENCODING 	= "UTF-8";
	public static final String DEFAULT_FREEMAKER_VERSION = "2.3.34";
	public static final String DEFAULT_HTML_SIZE	= "auto";


	public void write(final CmdArgs cmdArgs, final FieldsSet set) throws UtilsException {
		final Configuration config = config(cmdArgs);
		final Map<String, Object> variables = new HashMap<>();
		final String filename = cmdArgs.getArgument(ARG_OUT);

		variables.put("set", set);
		variables.put("headers", update(cmdArgs, LogOutput.sorted(cmdArgs, set)));
		try (final Writer writer = new FileWriter(filename)) {
			final Template temp 
					= config.getTemplate(cmdArgs.getParam(ARG_TEMPLATE, DEFAULT_TEMPLATE));

			temp.process(variables, writer);
		} catch (final TemplateException te) {
			throw new OutException(te, "Issue with template file \"{0}\": {1}", 
					cmdArgs.getParam(ARG_TEMPLATE, DEFAULT_TEMPLATE), te.getMessage());
		} catch (final IOException ioe) {
			throw new OutException(ioe, 
					"Access to template file \"{0}\" or output file \"{1}\": {2}", 
					cmdArgs.getParam(ARG_TEMPLATE, DEFAULT_TEMPLATE), filename, ioe.getMessage());
		}
	}

	public static void initHelp() {
		new CmdHelpArgDef(ARG_DIR4TEMPLATE, "Sets the template directory, default \"" 
				+ DEFAULT_DIR4TEMPLATE + "\".", 
				false, new CmdHelpArgParamDef("dir", "the template directory.", true));
		new CmdHelpArgDef(ARG_TEMPLATE, "Sets the template to convert to HTML, default \"" 
				+ DEFAULT_TEMPLATE + "\".", 
				false, new CmdHelpArgParamDef("name", "the template to convert to HTML.", true));
		new CmdHelpArgDef(ARG_ENCODING, "Sets the tool name, default \"" + DEFAULT_ENCODING + "\".", 
				false, new CmdHelpArgParamDef("name", "The name of the tool.", true));
		new CmdHelpArgDef(ARG_FREEMAKER_VERSION, "Sets the tool name, default \"" 
				+ DEFAULT_FREEMAKER_VERSION + "\".", 
				false, new CmdHelpArgParamDef("name", "The name of the tool.", true));
		new CmdHelpArgDef(ARG_HTML_DEFAULT_SIZE, "Sets the table default column size, default \"" 
				+ DEFAULT_HTML_SIZE + "\".", 
				false, new CmdHelpArgParamDef("size", "A size, e.g. 150px.", true));
		new CmdHelpArgDef(ARG_HTML_SIZE, "<name> Sets the table column size.", 
				false, new CmdHelpArgParamDef("size", "A size, e.g. 150px.", true));
	}

	private Configuration config(final CmdArgs args) throws CmdException, OutException {
        final File dir4template = new File(args.getParam(ARG_DIR4TEMPLATE, DEFAULT_DIR4TEMPLATE));

        try {
        	final String[] version = args.getParam(ARG_FREEMAKER_VERSION, DEFAULT_FREEMAKER_VERSION)
        		.split("\\.");
			// Create and adjust the configuration singleton
	        final Configuration config = new Configuration(new Version(Integer.parseInt(version[0]),
	        		Integer.parseInt(version[1]), Integer.parseInt(version[2])));

	        if (!dir4template.exists()) {
	        	throw new OutException("Missing directory for template \"{0}\"", 
	        			dir4template.getPath());
	        }

	        if (!dir4template.isDirectory()) {
	        	throw new OutException("Provided directory for template \"{0}\" is not a directory", 
	        			dir4template.getPath());
	        }
			config.setDirectoryForTemplateLoading(dir4template);
	        config.setDefaultEncoding(DEFAULT_ENCODING);
	        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	        config.setLogTemplateExceptions(false);
	        config.setWrapUncheckedExceptions(true);
	        config.setFallbackOnNullLoopVariable(false);
	        config.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
	
	        return config;
		} catch (final IOException ioe) {
			throw new OutException(ioe, "Failed to set directory for template to \"{0}\"", 
					dir4template.getPath());
		}
	}

	private FieldNames update(final CmdArgs cmdArgs, final FieldNames names) {
		final String defaultSize = cmdArgs.getParam(ARG_HTML_DEFAULT_SIZE, DEFAULT_HTML_SIZE);

		for (final LogField field : names) {
			final String argName = ARG_HTML_SIZE + field.getTitle().replaceAll(" ", "");

			field.setSize(cmdArgs.getParam(argName, defaultSize));
		}

		return names;
	}

} // end class HtmlOutput
