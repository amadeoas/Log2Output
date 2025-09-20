package uk.co.bocaditos.utils.cmd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created an static method with the name initHelp and no parameters, i.e. 
 * 'public static void initHelp()' where it creates the appropriate instances of CmdArgDef, e.g.
 * 
 *    public static void initHelp() {
 *      new CmdHelpArgDef("h", "The help.");
 *      new CmdHelpArgDef("?", "The help.");
 *	  }
 * 
 * Now, it's ready and you should call the static method CdmHelp.help() get the help
 * 
 * @author aasco
 */
public class CdmHelp {
	
	private static final Logger logger = LoggerFactory.getLogger(CdmHelp.class);

	/**
	 * Help parameter name.
	 */
	public static final String PARAM_HELP  = "h";
	/**
	 * Help parameter name.
	 */
	public static final String PARAM_HELP1 = "?";

	/**
	 * Exit error code.
	 */
	public static final int ERROR_EXIT = -1;
	/**
	 * Own package.
	 */
	public static final String OWN_PACKAGE = "co.uk.bocaditos.utils";

	/**
	 * Name of the parameter for application name.
	 */
	public static final String PARAM_APP_NAME = "app.name";
	/**
	 * Name of the parameter for application description.
	 */
	public static final String PARAM_APP_DESCRIPTION = "app.descrition";
	/**
	 * Name of the parameter for application version.
	 */
	public static final String PARAM_APP_VERSION = "app.version";
	
	// Default
	/**
	 * Default max line size.
	 */
	public static final int DEFAULT_MAX_LINE_SIZE = 80;
	/**
	 * Help description.
	 */
	public static final String DESC_HELP = "Provide this help";

	static List<CmdHelpArgDef> helpComponents;


	private CdmHelp() {}

	/**
	 * Builds the helps for to include the provided parameters.
	 * 
	 * @param cmdArgs the command line arguments.
	 * @param packageNames the packages.
	 * @return the help text.
	 */
	public static String help(final CmdArgs cmdArgs, final String... packageNames) {
		return help(cmdArgs, new StringBuilder(), packageNames);
	}

	/**
	 * Builds the helps for to include the provided parameters in the passed buffer.
	 * 
	 * @param params the parameters.
	 * @param buf the buffer.
	 * @param packageNames the packages.
	 * @return the help text.
	 */
	public static synchronized String help(final CmdArgs cmdArgs, final StringBuilder buf, 
			final String... packageNames) {
		CdmHelp.helpComponents = new ArrayList<>();
		installHelp(packageNames);
		buildHeader(cmdArgs, buf).append('\n');
		buildBody(cmdArgs, buf);
		CdmHelp.helpComponents = null;
		
		return buf.toString();
	}

	/**
	 * Initialisation of the help.
	 * @throws CmdException when missing values.
	 */
	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(PARAM_HELP, DESC_HELP);
		new CmdHelpArgDef(PARAM_HELP1, DESC_HELP);
	}

	/**
	 * Installation of the help.
	 * 
	 * @param packageNames the packages to load help for.
	 */
	public static void installHelp(final String... packageNames) {
		int j;

		installHelp(OWN_PACKAGE);
		for (int i = 0; i < packageNames.length; ++i) {
			final String packageName = packageNames[i];
		
			if (packageName.indexOf(OWN_PACKAGE) == 0) {
				continue;
			}
			j = 0;
			while (j < i && !packageName.equals(packageNames[j])) {
				++j;
			}
			installHelp(packageName);
		}
	}

	private static StringBuilder buildHeader(final CmdArgs cmdArgs, final StringBuilder buf) {
		if (cmdArgs != null) {
			buf.append("java -jar ")
				.append(cmdArgs.getParam(PARAM_APP_NAME, (String) null))
				.append('-')
				.append(cmdArgs.getParam(PARAM_APP_VERSION, (String) null))
				.append(".jar");
			for (final CmdHelpArgDef def : CdmHelp.helpComponents) {
				def.buildHeader(buf);
			}
		}

		return buf;
	}

	private static StringBuilder buildBody(final CmdArgs cmdArgs, final StringBuilder buf) {
		if (cmdArgs != null) {
			final String descrition = cmdArgs.getParam(PARAM_APP_DESCRIPTION, (String) null);
	
			buf.append('\n');
			if (descrition != null && !descrition.isEmpty()) {
				buf.append(build(descrition, DEFAULT_MAX_LINE_SIZE));
				buf.append('\n');
			}
		}

		for (final CmdHelpArgDef def : CdmHelp.helpComponents) {
			def.buildBody(buf);
		}

		return buf;
	}

	static String build(final String value, final int maxLineSize) {
		int sections = (value.length() / maxLineSize) 
				+ ((value.length() % maxLineSize) > 0 ? 1 : 0);

		if (sections == 1) {
			if (value.charAt(value.length() - 1) != '.') {
				return value + '.';
			}

			return value;
		}

		final StringBuilder buf = new StringBuilder(value.length() + (sections - 1) * 2);
		int offset = 0;
		int to = 0;

		while (offset < value.length()) {
			to += maxLineSize;
			if (to > value.length()) {
				to = value.length();
			}
			if (offset >= maxLineSize) {
				buf.append("\n\t");
			}
			buf.append(value, offset, to);
			offset = to;
		}
		if (buf.charAt(buf.length() - 1) != '.') {
			buf.append('.');
		}

		return buf.toString();
	}

	private static void installHelp(final String packageName) {
	    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	    assert classLoader != null;
	    try {
		    final String path = packageName.replace('.', '/');
	    	final Enumeration<URL> resources = classLoader.getResources(path);
		    final List<File> dirs = new ArrayList<>();

		    while (resources.hasMoreElements()) {
		        final URL resource = resources.nextElement();

		        dirs.add(new File(resource.getFile()));
		    }

		    for (final File dir : dirs) {
		    	installHelp(dir, packageName);
		    }
		} catch (final IOException ioe) {
			logger.error("Failed to load package \"" + packageName + "\"", ioe);
		}
	}


	/**
	 * Recursive method used to find all classes in a given directory and sub-directories with the 
	 * method "initHelp", and execute them in the order they are detected.
	 *
	 * @param directory   The base directory.
	 * @param packageName The package name for classes found inside the base directory.
	 * @return The classes.
	 * @throws ClassNotFoundException when a class was not found.
	 */
	private static void installHelp(final File directory, final String packageName) {
	    if (!directory.exists()) {
	        return;
	    }

	    for (final File file : directory.listFiles()) {
	        if (file.isDirectory()) {
	        	initPackage(file, packageName);
	        } else if (file.getName().endsWith(".class")) {
	        	initClass(file, packageName);
	        }
	    }
	}
	
	private static void initPackage(final File dir, final String packageName) {
        assert !dir.getName().contains(".");
        
        String packagePath = dir.getPath();
        int offset = packagePath.indexOf("bin/main/");
        
        if (offset != -1) {
        	packagePath = packagePath.substring(offset + "bin/main/".length());
        } else if (packagePath.contains("bin/test/")) {
        	return; // ignore that package
        }
        packagePath = packagePath.replace('/', '.');

        installHelp(dir, packagePath);
	}
	
	private static void initClass(final File file, final String packageName) {
    	final String className = packageName + '.' 
				+ file.getName().substring(0, file.getName().length() - 6);

    	try {
			final Class<?> clazz = Class.forName(className);
			final Method method = clazz.getMethod("initHelp");

			method.invoke(null);
        } catch (final NoSuchMethodException | SecurityException e) {
			// Nothing to do
		} catch (final IllegalAccessException | IllegalArgumentException 
					| InvocationTargetException e) {
			logger.error("Failed to initialise help for class \"" + className + "\"", e);
		} catch (final ClassNotFoundException cnfe) {
			logger.error("Failed to initialise help for class \"" + className + "\"", cnfe);
		}
	}

} // end class CdmHelp
