package uk.co.bocaditos.utils.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import uk.co.bocaditos.utils.Utils;


/**
 * Support to access the command line argument, environment, and file properties values.
 * 
 * @author aasco
 */
public class CmdArgs {

	private static final Logger logger = LoggerFactory.getLogger(CmdArgs.class);

	/**
	 * Start character.
	 */
	public static final char START = '-';
	/**
	 * No name.
	 */
	public static final String NO_NAME = "";

	/**
	 * Argument from the Command Line with their values.
	 */
	private  final Map<String, List<String>> args;

	/**
	 * .
	 */
	private final Environment env;


	/**
	 * Builds support to access the provided Command Line arguments, environment variables, and 
	 * property files values.
	 * 
	 * @param args the arguments.
	 */
	public CmdArgs(final String[] args) {
		this(null, args);
	}

	public CmdArgs(final Environment env, final String[] args) {
		this.args = buildArgs(args);
		this.env = env;
	}

	/**
	 * @return true if it has help.
	 */
	public boolean hasHelp() {
		return this.args.get(CdmHelp.PARAM_HELP) != null 
				|| this.args.get(CdmHelp.PARAM_HELP1) != null;
	}
	
	/**
	 * @return the number of parameters.
	 */
	public int getNumArgs() {
		return this.args.size();
	}
	
	/**
	 * @return true if no arguments.
	 */
	public boolean isEmpty() {
		return this.args.isEmpty();
	}

	/**
	 * Gets the string first value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first string value for specified argument name, or the default value of none 
	 * 			found.
	 */
	public String getParam(final String argName, final String defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public <E extends Enum<E>> E getParam(final String argName, final E defaultValue) {
		final String value = getParam(argName, (String) null);

		if (value == null) {
			return defaultValue;
		}

		return (E) defaultValue.valueOf(defaultValue.getClass(), value);
	}

	public void set(final String key, final String... values) {
		this.args.put(key, Arrays.asList(values));
	}

	/**
	 * Gets the string value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public String getParam(final String argName, final int index, final String defaultValue) {
		try {
			return getArgument(argName, index);
		} catch (final CmdException ce) {
			String value = getEnvirontmentVar(argName);

			if (value == null) {
				value = getPropertyValue(argName);
			}

			return (value == null) ? defaultValue : value;
		}
	}

	/**
	 * Gets the integer number value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public int getParam(final String argName, final int index, final int defaultValue) {
		final String value = getParam(argName, index, null);

		if (value == null) {
			return defaultValue;
		}
		
		return Integer.parseInt(value);
	}

	/**
	 * Gets the first integer number value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first integer number value for specified argument name, or the default value of 
	 * 			none found.
	 */
	public int getParam(final String argName, final int defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the long number value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public long getParam(final String argName, final int index, final long defaultValue) {
		final String value = getParam(argName, index, null);

		if (value == null) {
			return defaultValue;
		}

		return Long.parseLong(value);
	}

	/**
	 * Gets the first long number value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first long number value for specified argument name, or the default value of none 
	 * 			found.
	 */
	public long getParam(final String argName, final long defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the double number value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the double value for specified argument name and index, or the default value of none 
	 * 			found.
	 */
	public double getParam(final String argName, final int index, final double defaultValue) {
		final String value = getParam(argName, index, null);

		if (value == null) {
			return defaultValue;
		}

		return Double.parseDouble(value);
	}

	/**
	 * Gets the first double number value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first double number value for specified argument name, or the default value of 
	 * 			none found.
	 */
	public double getParam(final String argName, final double defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the float number value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public float getParam(final String argName, final int index, final float defaultValue) {
		final String value = getParam(argName, index, null);

		if (value == null) {
			return defaultValue;
		}

		return Float.parseFloat(value);
	}

	/**
	 * Gets the first double number value for the specified argument.
	 * float
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first float number value for specified argument name, or the default value of 
	 * 			none found.
	 */
	public float getParam(final String argName, final float defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the boolean value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public boolean getParam(final String argName, final int index, final boolean defaultValue) {
		final String value = getParam(argName, index, null);

		if (value == null) {
			return defaultValue;
		}

		return Boolean.parseBoolean(value);
	}

	/**
	 * Gets the first boolean value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first boolean value for specified argument name, or the default value of none
	 * 			found.
	 */
	public boolean getParam(final String argName, final boolean defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the character value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value for the given argument name.
	 * @param defaultValue the default values.
	 * @return the value for specified argument name and index, or the default value of none found.
	 */
	public char getParam(final String argName, final int index, final char defaultValue) {
		final String value = getParam(argName, index, null);

		if (value != null && value.length() == 1) {
			return value.charAt(0);
		}

		return defaultValue;
	}

	/**
	 * Gets the first character value for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValue the default values.
	 * @return the first character value for specified argument name, or the default value of none 
	 * 			found.
	 */
	public char getParam(final String argName, final char defaultValue) {
		return getParam(argName, 0, defaultValue);
	}

	/**
	 * Gets the value for the specified argument with index zero.
	 * 
	 * @param argName the argument name.
	 * @return the first value for specified argument name.
	 * @throws CmdException when the argument doesn't exist or doesn't have any values.
	 */
	public String getArgument(final String argName) throws CmdException {
		return getArgument(argName, 0);
	}

	/**
	 * Gets the value for the specified argument and index.
	 * 
	 * @param argName the argument name.
	 * @param index the index of the value to obtain.
	 * @return the value for specified argument name and index.
	 * @throws CmdException when the argument doesn't exist or doesn't have any value at the 
	 * 			specified index.
	 */
	public String getArgument(String argName, final int index) throws CmdException {
		if (index == 0 && this.env != null && argName.indexOf('.') != -1) {
			return this.env.getProperty(argName);
		}

		final List<String> values = this.args.get(argName);
		
		if (values == null) {
			if (index == 0 && this.env != null) {
				final String name = buildName(argName);
				final String value = this.env.getProperty(name);

				if (value != null) {
					return value;
				}

				logger.warn("Environment valiable {0} does not exist", name);
			}

			throw new CmdException("Command Line argument {0} doesn't exist", argName);
		}

		if (values.size() <= index) {
			throw new CmdException(
					"Not found command Line value for argument {0}; invalid index of {1, number}", 
					argName, index);
		}

		return values.get(index);
	}

	/**
	 * Gets all the values for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @return all the value for specified argument name.
	 * @throws CmdException when the argument with the specified name doesn't exist.
	 */
	public List<String> getArguments(final String argName) throws CmdException {
		return getArguments(argName, null);
	}

	/**
	 * Gets all the values for the specified argument.
	 * 
	 * @param argName the argument name.
	 * @param defaultValues the default value.
	 * @return all the value for specified argument name.
	 * @throws CmdException when the argument with the specified name doesn't exist and the default 
	 * 			is null.
	 */
	public List<String> getArguments(final String argName, final List<String> defaultValues) 
			throws CmdException {
		List<String> values = this.args.get(argName);
		
		if (values == null) {
			final String value = this.env.getProperty(argName);

			if (value != null) {
				values = new ArrayList<>(1);
				values.add(value);

				return values;
			}

			if (defaultValues != null) {
				return defaultValues;
			}

			throw new CmdException("Command Line argument {0} doesn't exist", argName);
		}

		return values;
	}
	
	/**
	 * @param argName the argument name.
	 * @return true if the specified argument name exist, or false otherwise.
	 */
	public boolean contains(final String argName) {
		return this.args.containsKey(argName);
	}
	
	/**
	 * Builds the arguments and associated values.
	 * 
	 * @param args the command line arguments.
	 * @return the nap of arguments.
	 */
	private Map<String, List<String>> buildArgs(final String[] args) {
		final Map<String, List<String>> map = new HashMap<>();
		int index = -1;
		String argName = null;
		List<String> params = null;

		while (++index < args.length) {
			final String arg = args[index];

			if (Utils.isEmpty(arg)) {
				continue;
			}

			if (arg.charAt(0) == START) {
				if ((argName != null && !argName.isEmpty()) 
						|| (params != null && !params.isEmpty())) {
					if (argName == null) {
						argName = "";
					}
					map.put(argName, Collections.unmodifiableList(params));
				}
				argName = arg.substring(1);
				params = new ArrayList<>();
			} else {
				params.add(arg);
			}
		}

		if (argName != null) {
			map.put(argName, Collections.unmodifiableList(params));
		}

		return map;
	}
	
	private String getEnvirontmentVar(final String argName) {
		final String name = buildEnvironmentArgName(argName);
		final String value = this.env != null ? this.env.getProperty(argName) : System.getenv(name);

		if (value != null) {
			final List<String> values = new ArrayList<>();

			values.add(value);
			this.args.put(argName, values);
		}

		return value;
	}
	
	private String getPropertyValue(final String argName) {
		final String name = buildPropertyArgName(argName);
		final String value = System.getProperty(name);

		if (value != null) {
			final List<String> values = new ArrayList<>();

			values.add(value);
			this.args.put(argName, values);
		}

		return value;
	}
	
	static String buildEnvironmentArgName(final String argName) {
		final StringBuilder buf = new StringBuilder();
		int lastUpper = 0;
		int from = 0;
		int to = 0;

		while (++to < argName.length()) {
			final char c = argName.charAt(to);

			if (Character.isUpperCase(c)) {
				if (to == ++lastUpper) {
					continue;
				}
				buf.append(argName.substring(from, to).toUpperCase());
				buf.append('_');
				lastUpper = from = to;
			} else if (c == '_') {
				lastUpper = to;
			} else if (c == '.') {
				buf.append(argName.substring(from, to).toUpperCase());
				buf.append('_');
				lastUpper = from = to + 1;
			}
		}

		if (from == 0) {
			return argName.toUpperCase();
		}
		buf.append(argName.substring(from).toUpperCase());

		return buf.toString();
	}
	
	static String buildPropertyArgName(final String argName) {
		final StringBuilder buf = new StringBuilder();
		int lastUpper = 0;
		int from = 0;
		int to = 0;

		while (++to < argName.length()) {
			final char c = argName.charAt(to);

			if (Character.isUpperCase(c)) {
				if (to == ++lastUpper) {
					continue;
				}
				buf.append(argName.substring(from, to).toLowerCase());
				buf.append('.');
				from = to;
			} else if (c == '_') {
				buf.append(argName.substring(from, to).toLowerCase());
				buf.append('.');
				lastUpper = to;
				from = to + 1;
			}
		}

		if (from == 0) {
			return argName.toLowerCase();
		}
		if (from < argName.length()) {
			buf.append(argName.substring(from).toLowerCase());
		}

		return buf.toString();
	}

	/**
	 * Converts <word>[<Word>[<Word>...]] to <word>[.<word>[.<word>...]].
	 * 
	 * @param name the name of the parameter.
	 * @return the dot format passed name.
	 */
	private static String buildName(final String name) {
		if (Utils.isEmpty(name)) {
			return name;
		}

		StringBuilder buf = null;
		int from = 0;

		for (int index = 0; index < name.length(); ++index) {
			final char c = name.charAt(index);

			if (Character.isUpperCase(c)) {
				if (buf == null) {
					buf = new StringBuilder();
				}
				buf.append(name.substring(from, index))
					.append('.')
					.append(Character.toLowerCase(c));
				from = index + 1;
			}
		}

		if (buf != null) {
			if (from < name.length()) {
				buf.append(name.substring(from));
			}

			final String n = buf.toString();

			logger.debug("Converted CMD argument \"{0}\" to env argument \"{1}\"", name, n);

			return n;
		}

		return name;
	}

} // end class CmdArgs
