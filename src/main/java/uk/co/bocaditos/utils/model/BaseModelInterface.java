package uk.co.bocaditos.utils.model;


/**
 * .
 * 
 * @param <T> class extending BaseModel.
 */
public interface BaseModelInterface<T extends BaseModelInterface<T>> {

	/**
	 * @param obj the passed instance.
	 * @return true if equal to the passed one.
	 */
	public abstract boolean equalsIt(T obj);

	/**
	 * @param buf text buffer.
	 * @return the provided buffer.
	 */
	public abstract StringBuilder toString(StringBuilder buf);

} // end class BaseModelInterface
