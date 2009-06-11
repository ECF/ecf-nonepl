package org.remotercp.util.dialogs;

import org.junit.Before;
import org.junit.Test;

public class TestExceptionWizard {

	private Exception ex;

	private String errorText;

	@Before
	public void setupException() {
		ex = new Exception("Exception occured");
		errorText = "This is an error text";
	}

	@Test
	public void testExceptionWizard() {
//		RemoteExceptionHandler.handleException(ex, errorText);
	}
}
