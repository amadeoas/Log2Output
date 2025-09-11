package uk.co.bocaditos.utils.cmd;

import java.text.MessageFormat;

import uk.co.bocaditos.utils.UtilsException;


/**
 * Expetion within the Command Line support.
 * 
 * @author aasco
 */
public class CmdException extends UtilsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7067808533100144704L;


	/**
	 * Build the exception with the specified message.
	 * 
	 * @param msg error message.
	 */
	public CmdException(final String msg) {
		this((Throwable) null, msg);
	}

	/**
	 * Builds the exception with error from the provided parameters.
	 * 
	 * @param msgFormat the message format.
	 * @param msgParams the parameters used in the message format to build the error message.
	 */
	public CmdException(final String msgFormat, final Object... msgParams) {
		this(null, msgFormat, msgParams);
	}

	/**
	 * Builds the exception with error from the provided parameters.
	 * 
	 * @param cause the cause
	 * @param msgFormat the message format.
	 * @param msgParams the parameters used in the message format to build the error message.
	 */
	public CmdException(final Throwable cause, final String msgFormat, final Object... msgParams) {
		super(MessageFormat.format(msgFormat, msgParams), cause);
	}

} // end class CmdException
