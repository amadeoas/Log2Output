package uk.co.bocaditos.utils.cmd;

import uk.co.bocaditos.utils.ToString;
import uk.co.bocaditos.utils.Utils;


/**
 * Command Line help argument parameter definition.
 * 
 * @author aasco
 */
public class CmdHelpArgParamDef implements ToString {
	
	private final String name;
	private final String description;
	private final boolean required;


	/**
	 * Builds argument parameter help definition.
	 * 
	 * @param name the parameter name. It's required.
	 * @param description the parameter description. It's required.
	 * @throws CmdException when missing any of the required values.
	 */
	public CmdHelpArgParamDef(final String name, final String description) throws CmdException {
		this(name, description, false);
	}

	/**
	 * Builds argument parameter help definition.
	 * 
	 * @param name the parameter name. It's required.
	 * @param description the parameter description. It's required.
	 * @param required true if parameter to be required. Default false.
	 * @throws CmdException when missing any of the required values.
	 */
	public CmdHelpArgParamDef(final String name, final String description, final boolean required) 
			throws CmdException {
		if (Utils.isEmpty(name)) {
			throw new CmdException("Missing CMD Line argument name in description");
		}
		this.name = name;
		if (Utils.isEmpty(description)) {
			throw new CmdException("Missing CMD Line argument description for arg name \"{0}\"", 
					this.name);
		}
		this.description = CdmHelp.build(description, CdmHelp.DEFAULT_MAX_LINE_SIZE);
		this.required = required;
	}

	/**
	 * @return the name of the parameter.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the parameter description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return true if parameter to be required.
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Builds property help header into provided buffer.
	 * 
	 * @param buf the buffer
	 * @return the buffer.
	 */
	public StringBuilder buildHeader(final StringBuilder buf) {
		if (buf.length() > 0 && buf.charAt(buf.length() - 1) != '[') {
			buf.append(' ');
		}
		if (!this.required) {
			buf.append('[');
		}
		buf.append('<');
		buf.append(this.name);
		buf.append('>');
		if (!this.required) {
			buf.append(']');
		}

		return buf;
		
	}

	/**
	 * Builds property help body into provided buffer.
	 * 
	 * @param buf the buffer
	 * @return the buffer.
	 */
	public StringBuilder buildBody(final StringBuilder buf) {
		return buf.append("\n\t")
			.append(this.name)
			.append("\n\t\t")
			.append(this.description)
			.append('\n');
	}
	
	@Override
	public final String toString() {
		return toString(new StringBuilder()).toString();
	}

	/**
	 * Builds the parameter textual representation.
	 * 
	 * @param buf the buffer
	 * @return the buffer.
	 */
	@Override
	public StringBuilder toString(final StringBuilder buf) {
		Utils.append(buf, "name", this.name);
		Utils.append(buf, "description", this.description);
		Utils.append(buf, "required", this.required);

		return buf;
	}

} // end class CmdArgParamDef
