package uk.co.bocaditos.utils.cmd;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.bocaditos.utils.ToString;
import uk.co.bocaditos.utils.Utils;


/**
 * Command Line help argument definition.
 * 
 * @author aasco
 */
public class CmdHelpArgDef implements ToString {
	
	private static final Logger logger = LoggerFactory.getLogger(CmdHelpArgDef.class);
	
	private final String name;
	private final String description;
	private final boolean required;
	private final CmdHelpArgParamDef[] params;


	/**
	 * Builds CMD help for argument.
	 * 
	 * @param name the argument name. It's required.
	 * @param description the description. It's required.
	 * @throws CmdException when missing required constructor values.
	 */
	public CmdHelpArgDef(final String name, final String description) throws CmdException {
		this(name, description, false);
	}

	/**
	 * Builds CMD help for argument.
	 * 
	 * @param name the argument name. It's required.
	 * @param description the description. It's required.
	 * @throws CmdException when missing required constructor values.
	 */
	public CmdHelpArgDef(final String name, final String description, final boolean required) 
			throws CmdException {
		this(name, description, required, (CmdHelpArgParamDef[]) null);
	}

	/**
	 * Builds CMD help for argument.
	 * 
	 * @param name the argument name. It's required.
	 * @param description the description. It's required.
	 * @param required true if argument is required.
	 * @param params list of parameters for this argument.
	 * @throws CmdException when missing required constructor values.
	 */
	public CmdHelpArgDef(final String name, final String description, final boolean required, 
			final CmdHelpArgParamDef... params) throws CmdException {
		if (Utils.isEmpty(name)) {
			throw new CmdException("Missing argument name");
		}
		this.name = name;
		if (Utils.isEmpty(description)) {
			throw new CmdException("Missing argument description for argument \"{0}\"", this.name);
		}
		this.description = CdmHelp.build(description, CdmHelp.DEFAULT_MAX_LINE_SIZE);
		this.required = required;
		this.params = params;

		synchronized (CmdHelpArgDef.class) {
			if (CdmHelp.helpComponents == null) {
				CdmHelp.helpComponents = new ArrayList<>();
			}

			for (final CmdHelpArgDef def: CdmHelp.helpComponents) {
				if (this.name.equals(def.name)) {
					logger.warn("Parameter definition \"{}\" already loaded; new value will be ignored", 
							this.name);
					
					return;
				}
			}
			CdmHelp.helpComponents.add(this);
		}
	}

	/**
	 * @return the name of the argument.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description of this argument.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Builds the help header for this argument.
	 * 
	 * @param buf the buffer.
	 * @return the passed buffer.
	 */
	public StringBuilder buildHeader(final StringBuilder buf) {
		if (this.required) {
			buf.append(' ');
		} else {
			buf.append(" [");
		}
		buf.append('-');
		buf.append(this.name);
		if (this.params != null) {
			for (final CmdHelpArgParamDef def : this.params) {
				
				def.buildHeader(buf);
			}
		}
		if (!this.required) {
			buf.append(']');
		}

		return buf;
	}

	/**
	 * Builds the body part of this argument.
	 * 
	 * @param buf the buffer.
	 * @return the passed buffer.
	 */
	public StringBuilder buildBody(final StringBuilder buf) {
		buildHeader(buf);
		buf.append("\n\t");
		buf.append(this.description);
		buf.append('\n');
		if (this.params != null) {
			for (final CmdHelpArgParamDef def : this.params) {
				def.buildBody(buf);
			}
		}

		return buf;
	}

	@Override
	public final String toString() {
		return toString(new StringBuilder()).toString();
	}

	/**
	 * Builds the argument textual representation in the passed buffer.
	 * 
	 * @param buf the buffer.
	 * @return the buffer.
	 */
	@Override
	public StringBuilder toString(final StringBuilder buf) {
		Utils.append(buf, "name", this.name);
		Utils.append(buf, "description", this.description);
		Utils.append(buf, "required", this.required);
		Utils.append(buf, "params", this.params);

		return buf;
	}

} // end class CmdArgDef
