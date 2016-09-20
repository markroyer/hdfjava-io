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
public class H5Exception extends Exception {

	public H5Exception(String message) {
		super(message);
	}

	public H5Exception(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
