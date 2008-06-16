package org.remotercp.ecf.old;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPConnector {

	private static final Logger logger = Logger.getLogger(XMPPConnector.class
			.getName());
	private XMPPConnection connection;

	public boolean connect(String userName, String password, String server) {
		try {
			ConnectionConfiguration configuration = new ConnectionConfiguration(
					server, 5222);
			connection = new XMPPConnection(configuration);
			if (connection.isConnected()) {
				logger.log(Level.INFO, "XMPP Connection established");
			} else {
				logger.log(Level.SEVERE, "Unable to establish xmpp connection");
			}

			connection.login(userName, password);
			logger.log(Level.INFO, "User login successful");

			// Chat chat = connection.createChat("ecf@eugen.de");
			return true;

			// chat.sendMessage("Hello world");
			// Message message = chat.nextMessage(5000);
			//
			// System.out.println("Returned message: "
			// + (message == null ? "<timed out>" : message.getBody()));
		} catch (XMPPException e) {
			// e.printStackTrace();
			logger.log(Level.SEVERE, "Unable to login user: " + userName);
			return false;
		}
	}

	public XMPPConnection getConnection() {
		return connection;
	}

}
