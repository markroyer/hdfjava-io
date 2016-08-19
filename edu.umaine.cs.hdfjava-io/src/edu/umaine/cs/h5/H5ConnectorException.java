/**
 * 
 */
package edu.umaine.cs.h5;

/**
 * Used for H5 related exceptions.
 * 
 * @author Mark Royer
 *
 */
public class H5ConnectorException extends Exception {

	public H5ConnectorException(String message) {
		super(message);
	}

	public H5ConnectorException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
