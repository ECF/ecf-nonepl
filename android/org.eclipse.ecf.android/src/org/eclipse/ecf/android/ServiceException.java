package org.eclipse.ecf.android;

public class ServiceException extends RuntimeException {

	public ServiceException(String string, int remote2, Throwable t) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5657906110888602569L;

	// Field descriptor #5 I
	  public static final int UNSPECIFIED = 0;
	  
	  // Field descriptor #5 I
	  public static final int UNREGISTERED = 1;
	  
	  // Field descriptor #5 I
	  public static final int FACTORY_ERROR = 2;
	  
	  // Field descriptor #5 I
	  public static final int FACTORY_EXCEPTION = 3;
	  
	  // Field descriptor #5 I
	  public static final int SUBCLASSED = 4;
	  
	  // Field descriptor #5 I
	  public static final int REMOTE = 5;

}
