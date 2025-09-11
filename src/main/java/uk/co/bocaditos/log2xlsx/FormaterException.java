package uk.co.bocaditos.log2xlsx;

import uk.co.bocaditos.utils.UtilsException;


/**
 * .
 * 
 * @author aasco
 */
public class FormaterException extends UtilsException {

    /**
     * 
     */
    private static final long serialVersionUID = -2659556768931935765L;


    public FormaterException(final String format, final Object... values) {
        this(null, format, values);
    }

    public FormaterException(final Throwable throwed, final String format, final Object... values) {
        super(build(format, values), throwed);
    }

} // end class FormaterException
