package org.eclipse.ecf.wave.document;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public class DocumentOperationException extends ECFException {

	private static final long serialVersionUID = 1425443423865118490L;

	public DocumentOperationException() {
	}

	public DocumentOperationException(String message) {
		super(message);
	}

	public DocumentOperationException(Throwable cause) {
		super(cause);
	}

	public DocumentOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentOperationException(IStatus status) {
		super(status);
	}

}
