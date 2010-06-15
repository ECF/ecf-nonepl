package org.remotercp.contacts;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ContactsActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.contacts.ui";

	// The shared instance
	private static ContactsActivator plugin;

	private static BundleContext bundlecontext;

	/**
	 * The constructor
	 */
	public ContactsActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		bundlecontext = context;

		this.registerListener();
	}

	/*
	 * Register listener for incoming chat events. Check if this is the
	 * appropriate place to register a listener. The listener has to be
	 * registered on start up, otherwise the chat editor will never be opened.
	 */
	private void registerListener() {
//		ISessionService session = OsgiServiceLocatorUtil.getOSGiService(
//				bundlecontext, ISessionService.class);
//
//		if (session != null) {
//
//			// nachrichten
//			session.addMessageListener(new IIMMessageListener() {
//
//				public void handleMessageEvent(IIMMessageEvent messageEvent) {
//					Logger.getAnonymousLogger().log(Level.INFO,
//							"Message received: " + messageEvent.getFromID());
//
//					new OpenChatEditorAction(messageEvent).run();
//				}
//			});
//
//			// inform chat user about arriving and leaving of other chat user
//			session.getRosterManager().addPresenceListener(
//					new IPresenceListener() {
//						public void handlePresence(ID fromID, IPresence presence) {
//							new ChatUserStatusChangedAction(fromID, presence)
//									.run();
//
//						}
//					});
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ContactsActivator getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundlecontext;
	}

}
