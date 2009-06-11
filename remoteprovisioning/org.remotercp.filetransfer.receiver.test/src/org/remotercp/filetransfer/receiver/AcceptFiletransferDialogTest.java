package org.remotercp.filetransfer.receiver;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.remotercp.filetransfer.receiver.IncomingFiletransferReceiver;

public class AcceptFiletransferDialogTest {

	@Test
	public void acceptFileTransferTest() {
		Display display = new Display();
		Shell shell = new Shell(display);

		IncomingFiletransferReceiver fileHandler = new IncomingFiletransferReceiver();

		FileDialog fileDialog = new FileDialog(shell);
		String filename = fileDialog.open();

		File path = new File(filename);

		try {
			int handleIncomingFile = fileHandler.acceptIncomingFile(path
					.getAbsolutePath(), "Eugen");

			if (handleIncomingFile == Window.OK) {
				fileHandler.getStoreFileLocation(path);
				// fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

	}
}
