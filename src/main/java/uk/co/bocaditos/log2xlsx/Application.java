package uk.co.bocaditos.log2xlsx;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import uk.co.bocaditos.log2xlsx.in.Formats;
import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;
import uk.co.bocaditos.log2xlsx.in.filter.FieldFilter;
import uk.co.bocaditos.log2xlsx.in.filter.Filter;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.log2xlsx.out.html.HtmlOutput;
import uk.co.bocaditos.log2xlsx.out.xlsx.XlsxOutput;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CdmHelp;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Starting point of the application.
 * 
 * [{string, pattern=^[a-zA-Z\d]\{1,3\}$}] [{string, pattern=^[a-zA-Z\d]\{1,3\}$}] [{string}] - [{}]: [{}]
 * [{string, pattern=^[a-zA-Z\d]\{1,3\}$}] [{string, pattern=^[a-zA-Z\d]\{1,3\}$}] [{string}] - [{}]
 * 
 * @author aasco
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static final String ARG_FORMATS		 = "formats";
    public static final String ARG_ID_FIELD_NAME = "idFieldName";
    public static final String ARG_LOGS			 = "logs";

    @Autowired
    Environment env;

	@Value("${app.name}")
	private String appName;

	@Value("${app.version}")
	private String version;


    public static void main(final String[] cmdArgs) {
		final SpringApplication app = new SpringApplication(Application.class);

	    app.setBannerMode(Mode.OFF);

	    final ConfigurableApplicationContext ctx = app.run(cmdArgs);

	    SpringApplication.exit(ctx, new ExitCodeGenerator() {

			@Override
			public int getExitCode() {
				logger.info("Finished!");

				return 0;
		    }

		});
	}

	@Override
	public void run(final String... args) throws UtilsException {
		final CmdArgs cmdArgs = new CmdArgs(this.env, args);

		if (cmdArgs.hasHelp()) {
			help(cmdArgs);

			return;
		}

		process(cmdArgs);
	}

	public static void initHelp() {
		new CmdHelpArgDef(ARG_FORMATS, "Sets the log formats to use.", true, 
				new CmdHelpArgParamDef("filename", "The format filename",
					true));
		new CmdHelpArgDef(ARG_ID_FIELD_NAME, "Sets the field name to use as ID for groups.", true, 
				new CmdHelpArgParamDef("idFieldName", "The name of the field used as ID for groups",
					true));
		new CmdHelpArgDef(ARG_LOGS, "Sets the files or directory with the log lines.", true, 
				new CmdHelpArgParamDef("filenames", "If a directory just one wothout '.' where all " 
					+ "files ended with .log will be used other wise will be considered a comma " 
					+ "separated list of log filed", true));
	}

	private void process(final CmdArgs cmdArgs) throws UtilsException {
	    try {
	    	buildOuter(cmdArgs)
	    		.write(cmdArgs, load(cmdArgs));
	    } catch (final UtilsException ue) {
	    	Application.logger.error(ue.getMessage());

	    	throw ue;
	    }
	}

	private void help(final CmdArgs cmdArgs) {
		System.out.println(help_(cmdArgs));
	}

	static String help_(final CmdArgs cmdArgs) {
		final String[] packages = {
				"uk.co.bocaditos.log2xlsx"
			};

		return CdmHelp.help(cmdArgs, packages);
	}

	private FieldsSet load(final CmdArgs cmdArgs) throws UtilsException {
    	final LogSet set = Formats.loadFiles(cmdArgs.getArgument(ARG_FORMATS).split(","));
    	final FieldsSet fieldsSet = new FieldsSet(set, cmdArgs.getArgument(ARG_ID_FIELD_NAME));
    	String[] logFileNames = cmdArgs.getArgument(ARG_LOGS).split(",");

    	if (logFileNames.length == 1) {
    		// Maybe a directory
    		if (logFileNames[0].indexOf('.') == -1) {
    			// It's a directory
    			final File dir = new File(logFileNames[0]);

    			if (dir.isDirectory()) {
    				String dirPath = logFileNames[0];

    				logFileNames = dir.list(new FilenameFilter() {

						@Override
						public boolean accept(final File dir, final String name) {
							return name.endsWith(".log");
						}

    				});
    				if (dirPath.charAt(dirPath.length() - 1) != '\\' 
    						&& dirPath.charAt(dirPath.length() - 1) != '/') {
    					dirPath += '/';
    				}

    				for (int index = 0; index < logFileNames.length; ++index) {
    					logFileNames[index] = dirPath + logFileNames[index];
    				}
    			}
    		}
    	}

    	final Filter filter = FieldFilter.build(cmdArgs, set);
    	
    	try (final Input in = Input.build(logFileNames)) {
    		return fieldsSet.load(filter, in);
    	} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed to proceed inputs");
		}
	}

	private LogOutput buildOuter(final CmdArgs cmdArgs) throws UtilsException {
    	final String outFilename = cmdArgs.getArgument(LogOutput.ARG_OUT);
    	LogOutput output;

    	if (outFilename.endsWith(".xlsx")) {
    		output = new XlsxOutput(cmdArgs);
    	} else if (outFilename.endsWith(".html")) {
    		output = new HtmlOutput(this.appName, this.version);
    	} else {
    		throw new UtilsException("Unsuported output file \"{0}\"", outFilename);
    	}

    	return output;
	}

} // end class Application
