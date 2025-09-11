package uk.co.bocaditos.utils.cmd;


/**
 * Command Line help argument parameter definition.
 * 
 * @author aasco
 */
public class CmdHelpArgParamDef {
	
	private final String name;
	private final String description;
	private final boolean required;


	/**
	 * Builds argument parameter help definition.
	 * 
	 * @param name the parameter name.
	 * @param description the parameter description.
	 */
	public CmdHelpArgParamDef(final String name, final String description) {
		this(name, description, false);
	}

	/**
	 * Builds argument parameter help definition.
	 * 
	 * @param name the parameter name.
	 * @param description the parameter description.
	 * @param required true if parameter to be required.
	 */
	public CmdHelpArgParamDef(final String name, final String description, final boolean required) {
		this.name = name;
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
		buf.append("\n\t");
		buf.append(this.name);
		if (this.description != null && this.description.length() > 0) {
			buf.append("\n\t\t");
			buf.append(this.description);
		}
		buf.append('\n');

		return buf;
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
	public StringBuilder toString(final StringBuilder buf) {
		return buildHeader(buf);
	}

} // end class CmdArgParamDef
