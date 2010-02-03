package org.eclipse.ecf.android;

import org.eclipse.ecf.android.RegEx.RegularExpression;

/**
 * A Framework exception used to indicate that a filter string has an invalid
 * syntax.
 * 
 * <p>
 * An <code>InvalidSyntaxException</code> object indicates that a filter
 * string parameter has an invalid syntax and cannot be parsed. See
 * {@link Filter} for a description of the filter string syntax.
 * 
 * <p>
 * This exception conforms to the general purpose exception chaining mechanism.
 * 
 * @version $Revision: 1.1 $
 */

public class InvalidSyntaxException extends Exception {
	static final long		serialVersionUID	= -4295194420816491875L;
	/**
	 * The invalid filter string.
	 */
	private final RegularExpression	filter;

	/**
	 * Creates an exception of type <code>InvalidSyntaxException</code>.
	 * 
	 * <p>
	 * This method creates an <code>InvalidSyntaxException</code> object with
	 * the specified message and the filter string which generated the
	 * exception.
	 * 
	 * @param msg The message.
	 * @param createFilter The invalid filter string.
	 */
	public InvalidSyntaxException(String msg, RegularExpression createFilter) {
		super(msg);
		this.filter = createFilter;
	}

	/**
	 * Creates an exception of type <code>InvalidSyntaxException</code>.
	 * 
	 * <p>
	 * This method creates an <code>InvalidSyntaxException</code> object with
	 * the specified message and the filter string which generated the
	 * exception.
	 * 
	 * @param msg The message.
	 * @param filter The invalid filter string.
	 * @param cause The cause of this exception.
	 * @since 1.3
	 */
	public InvalidSyntaxException(String msg, RegularExpression filter, Throwable cause) {
		super(msg, cause);
		this.filter = filter;
	}

	/**
	 * Returns the filter string that generated the
	 * <code>InvalidSyntaxException</code> object.
	 * 
	 * @return The invalid filter string.
	 * @see BundleContext#getServiceReferences
	 * @see BundleContext#addServiceListener(ServiceListener,String)
	 */
	public RegularExpression getFilter() {
		return filter;
	}

	/**
	 * Returns the cause of this exception or <code>null</code> if no cause was
	 * set.
	 * 
	 * @return The cause of this exception or <code>null</code> if no cause was
	 *         set.
	 * @since 1.3
	 */
	@Override
	public Throwable getCause() {
		return super.getCause();
	}

	/**
	 * Initializes the cause of this exception to the specified value.
	 * 
	 * @param cause The cause of this exception.
	 * @return This exception.
	 * @throws IllegalArgumentException If the specified cause is this
	 *         exception.
	 * @throws IllegalStateException If the cause of this exception has already
	 *         been set.
	 * @since 1.3
	 */
	@Override
	public Throwable initCause(Throwable cause) {
		return super.initCause(cause);
	}
}
