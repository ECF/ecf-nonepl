/*******************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.presence.bot.kosmos;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.bot.IChatRoomBotEntry;
import org.eclipse.ecf.presence.bot.IChatRoomMessageHandler;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessage;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.osgi.util.NLS;

public class ChatRoomMessageHandler implements IChatRoomMessageHandler {

	private static final String BUG_DATABASE_PREFIX = "https://bugs.eclipse.org/bugs/show_bug.cgi?id="; //$NON-NLS-1$
	private static final String BUG_DATABASE_POSTFIX = "&ctype=xml"; //$NON-NLS-1$
	private static final String SUM_OPEN_TAG = "<short_desc>"; //$NON-NLS-1$
	private static final String SUM_CLOSE_TAG = "</short_desc>"; //$NON-NLS-1$

	private Map messageSenders;

	private IContainer container;

	public ChatRoomMessageHandler() {
		messageSenders = new HashMap();
	}

	private void sendMessage(ID roomID, String message) {
		try {
			if (container != null) {
				IChatRoomMessageSender sender = (IChatRoomMessageSender) messageSenders
						.get(roomID);
				if (sender != null) {
					sender.sendMessage(message);
				}
			}
		} catch (ECFException e) {
			e.printStackTrace();
			container.disconnect();
			container = null;
		}
	}

	private void sendBugzillaLink(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.EclipseBugzilla);
		} else {
			sendMessage(roomID, NLS
					.bind(Messages.EclipseBugzilla_Reply, target));
		}
	}

	public void sendBug(ID roomID, String target, String number) {
		String urlString = BUG_DATABASE_PREFIX + number;
		if (target == null) {
			try {
				HttpURLConnection hURL = (HttpURLConnection) new URL(
						BUG_DATABASE_PREFIX + number + BUG_DATABASE_POSTFIX)
						.openConnection();
				hURL.setAllowUserInteraction(true);
				hURL.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(hURL.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				try {
					if (hURL.getResponseCode() != HttpURLConnection.HTTP_OK) {
						sendMessage(roomID, NLS.bind(Messages.Bug, number,
								urlString));
						return;
					}

					String input = reader.readLine();
					buffer.append(input);
					while (input.indexOf(SUM_CLOSE_TAG) == -1) {
						input = reader.readLine();
						buffer.append(input);
					}
					hURL.disconnect();
				} catch (EOFException e) {
					hURL.disconnect();
					sendMessage(roomID, NLS.bind(Messages.Bug, number,
							urlString));
					e.printStackTrace();
					return;
				}
				String webPage = buffer.toString();
				int summaryStartIndex = webPage.indexOf(SUM_OPEN_TAG);
				int summaryEndIndex = webPage.indexOf(SUM_CLOSE_TAG,
						summaryStartIndex);
				if (summaryStartIndex != -1 & summaryEndIndex != -1) {
					String summary = webPage.substring(summaryStartIndex
							+ SUM_OPEN_TAG.length(), summaryEndIndex);
					sendMessage(roomID, NLS.bind(Messages.BugContent,
							new Object[] { number, summary, urlString }));
				} else {
					sendMessage(roomID, NLS.bind(Messages.Bug, new Object[] {
							number, urlString }));
				}
			} catch (IOException e) {
				sendMessage(roomID, NLS.bind(Messages.Bug, new Object[] {
						number, urlString }));
				e.printStackTrace();
			}
		} else {
			try {
				HttpURLConnection hURL = (HttpURLConnection) new URL(
						BUG_DATABASE_PREFIX + number + BUG_DATABASE_POSTFIX)
						.openConnection();
				hURL.setAllowUserInteraction(true);
				hURL.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(hURL.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				try {
					if (hURL.getResponseCode() != HttpURLConnection.HTTP_OK) {
						sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
								new Object[] { target, number, urlString }));
						return;
					}

					String input = reader.readLine();
					buffer.append(input);
					while (input.indexOf(SUM_CLOSE_TAG) == -1) {
						input = reader.readLine();
						buffer.append(input);
					}
					hURL.disconnect();
				} catch (EOFException e) {
					hURL.disconnect();
					sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
							new Object[] { target, number, urlString }));
					return;
				}
				String webPage = buffer.toString();
				int summaryStartIndex = webPage.indexOf(SUM_OPEN_TAG);
				int summaryEndIndex = webPage.indexOf(SUM_CLOSE_TAG,
						summaryStartIndex);
				if (summaryStartIndex != -1 & summaryEndIndex != -1) {
					String summary = webPage.substring(summaryStartIndex
							+ SUM_OPEN_TAG.length(), summaryEndIndex);
					sendMessage(roomID, NLS
							.bind(Messages.BugContent_Reply, new Object[] {
									target, number, summary, urlString }));
				} else {
					sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
							new Object[] { target, number, urlString }));
				}
			} catch (IOException e) {
				sendMessage(roomID, NLS.bind(Messages.Bug_Reply, new Object[] {
						target, number, urlString }));
			}
		}
	}

	private void sendBugAndComment(ID roomID, String target, String number,
			String comment) {
		String urlString = BUG_DATABASE_PREFIX + number + "#c" + comment; //$NON-NLS-1$
		if (target == null) {
			try {
				HttpURLConnection hURL = (HttpURLConnection) new URL(
						BUG_DATABASE_PREFIX + number + BUG_DATABASE_POSTFIX)
						.openConnection();
				hURL.setAllowUserInteraction(true);
				hURL.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(hURL.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				try {
					if (hURL.getResponseCode() != HttpURLConnection.HTTP_OK) {
						sendMessage(roomID, NLS.bind(Messages.Bug,
								new Object[] { number, urlString }));
						return;
					}

					String input = reader.readLine();
					buffer.append(input);
					while (input.indexOf(SUM_CLOSE_TAG) == -1) {
						input = reader.readLine();
						buffer.append(input);
					}
					hURL.disconnect();
				} catch (EOFException e) {
					hURL.disconnect();
					sendMessage(roomID, NLS.bind(Messages.Bug, new Object[] {
							number, urlString }));
					return;
				}
				String webPage = buffer.toString();
				int summaryStartIndex = webPage.indexOf(SUM_OPEN_TAG);
				int summaryEndIndex = webPage.indexOf(SUM_CLOSE_TAG,
						summaryStartIndex);
				if (summaryStartIndex != -1 & summaryEndIndex != -1) {
					String summary = webPage.substring(summaryStartIndex
							+ SUM_OPEN_TAG.length(), summaryEndIndex);
					sendMessage(roomID, NLS.bind(Messages.BugContent,
							new Object[] { number, summary, urlString }));
				} else {
					sendMessage(roomID, NLS.bind(Messages.Bug, new Object[] {
							number, urlString }));
				}
			} catch (IOException e) {
				sendMessage(roomID, NLS.bind(Messages.Bug, new Object[] {
						number, urlString }));
			}
		} else {
			try {
				HttpURLConnection hURL = (HttpURLConnection) new URL(
						BUG_DATABASE_PREFIX + number + BUG_DATABASE_POSTFIX)
						.openConnection();
				hURL.setAllowUserInteraction(true);
				hURL.connect();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(hURL.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				try {
					if (hURL.getResponseCode() != HttpURLConnection.HTTP_OK) {
						sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
								new Object[] { target, number, urlString }));
						return;
					}

					String input = reader.readLine();
					buffer.append(input);
					while (input.indexOf(SUM_CLOSE_TAG) == -1) {
						input = reader.readLine();
						buffer.append(input);
					}
					hURL.disconnect();
				} catch (EOFException e) {
					hURL.disconnect();
					sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
							new Object[] { target, number, urlString }));
					return;
				}
				String webPage = buffer.toString();
				int summaryStartIndex = webPage.indexOf(SUM_OPEN_TAG);
				int summaryEndIndex = webPage.indexOf(SUM_CLOSE_TAG,
						summaryStartIndex);
				if (summaryStartIndex != -1 & summaryEndIndex != -1) {
					String summary = webPage.substring(summaryStartIndex
							+ SUM_OPEN_TAG.length(), summaryEndIndex);
					sendMessage(roomID, NLS
							.bind(Messages.BugContent_Reply, new Object[] {
									target, number, summary, urlString }));
				} else {
					sendMessage(roomID, NLS.bind(Messages.Bug_Reply,
							new Object[] { target, number, urlString }));
				}
			} catch (IOException e) {
				sendMessage(roomID, NLS.bind(Messages.Bug_Reply, new Object[] {
						target, number, urlString }));
			}
		}
	}

	private void sendManagementInformation(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Manage);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Manage_Reply, target));
		}
	}

	private void sendLogInformation(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Logs);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Logs_Reply, target));
		}
	}

	private void sendAbout(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.About);
		} else {
			sendMessage(roomID, NLS.bind(Messages.About_Reply, target));
		}
	}

	private void sendPastebin(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Pastebin);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Pastebin_Reply, target));
		}
	}

	private void sendSnippets(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Snippets);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Snippets_Reply, target));
		}
	}

	private void sendJavaDoc(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Javadoc);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Javadoc_Reply, target));
		}
	}

	private void sendPlugins(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Plugins);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Plugins_Reply, target));
		}
	}

	private void sendWTP(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Webtools);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Webtools_Reply, target));
		}
	}

	private void sendPHP(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.PHP);
		} else {
			sendMessage(roomID, NLS.bind(Messages.PHP_Reply, target));
		}
	}

	private void sendSubversion(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Subversion);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Subversion_Reply, target));
		}
	}

	private void sendNewsgroup(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Newsgroup);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Newsgroup_Reply, target));
		}
	}

	private void sendHelp(ID roomID, String target) {
		if (target == null) {
			sendMessage(roomID, Messages.Help);
		} else {
			sendMessage(roomID, NLS.bind(Messages.Help_Reply, target));
		}
	}

	private void sendGoogle(ID roomID, String target, String searchString) {
		searchString = searchString.replace(' ', '+');
		if (target == null) {
			sendMessage(roomID, NLS.bind(Messages.Google, searchString));
		} else {
			sendMessage(roomID, NLS.bind(Messages.Google_Reply, target,
					searchString));
		}
	}

	private void sendWiki(ID roomID, String target, String articleName) {
		articleName = articleName.replace(' ', '_');
		if (target == null) {
			sendMessage(roomID, NLS.bind(Messages.Wiki, articleName));
		} else {
			sendMessage(roomID, NLS.bind(Messages.Wiki_Reply, target,
					articleName));
		}
	}

	private void sendEclipseHelp(ID roomID, String target, String searchString) {
		searchString = searchString.replace(' ', '+');
		if (target == null) {
			sendMessage(roomID, NLS.bind(Messages.EclipseHelp, searchString));
		} else {
			sendMessage(roomID, NLS.bind(Messages.EclipseHelp_Reply, target,
					searchString));
		}
	}

	private void send(ID roomID, String target, String msg) {
		if (msg.equals("bug")) { //$NON-NLS-1$
			sendBugzillaLink(roomID, target);
		} else if (msg.startsWith("bug")) { //$NON-NLS-1$
			int index = msg.indexOf('c');
			if (index == -1) {
				sendBug(roomID, target, msg.substring(3));
			} else {
				sendBugAndComment(roomID, target, msg.substring(3, index), msg
						.substring(index + 1));
			}
		} else if (msg.equals("log") || msg.equals("logs")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendLogInformation(roomID, target);
		} else if (msg.equals("manage")) { //$NON-NLS-1$
			sendManagementInformation(roomID, target);
		} else if (msg.equals("about") || msg.equals("bot")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendAbout(roomID, target);
		} else if (msg.equals("paste") || msg.equals("pastebin")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendPastebin(roomID, target);
		} else if (msg.equals("snippet") || msg.equals("snippets")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendSnippets(roomID, target);
		} else if (msg.equals("javadoc") || msg.equals("api")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendJavaDoc(roomID, target);
		} else if (msg.equals("news") || msg.equals("newsgroup")) { //$NON-NLS-1$ //$NON-NLS-2$
			sendNewsgroup(roomID, target);
		} else if (msg.equals("plugin")) { //$NON-NLS-1$
			sendPlugins(roomID, target);
		} else if (msg.equals("wtp")) { //$NON-NLS-1$
			sendWTP(roomID, target);
		} else if (msg.equals("php")) { //$NON-NLS-1$
			sendPHP(roomID, target);
		} else if (msg.equals("svn")) { //$NON-NLS-1$
			sendSubversion(roomID, target);
		} else if (msg.equals("cmd") || msg.equals("commands") //$NON-NLS-1$ //$NON-NLS-2$
				|| msg.equals("help")) { //$NON-NLS-1$
			sendHelp(roomID, target);
		} else if (msg.startsWith("g ")) { //$NON-NLS-1$
			sendGoogle(roomID, target, msg.substring(2));
		} else if (msg.startsWith("wiki ")) { //$NON-NLS-1$
			sendWiki(roomID, target, msg.substring(5));
		} else if (msg.startsWith("eh")) { //$NON-NLS-1$
			sendEclipseHelp(roomID, target, msg.substring(3));
		} else {
			int index = msg.indexOf('c');
			if (index == -1) {
				try {
					// check if what's before the 'c' is a valid number
					Integer.parseInt(msg);
					sendBug(roomID, target, msg);
				} catch (NumberFormatException e) {
					return;
				}
			} else {
				try {
					// check if what's before the 'c' is a valid number
					Integer.parseInt(msg.substring(0, index));
					sendBugAndComment(roomID, target, msg.substring(0, index),
							msg.substring(index + 1));
				} catch (NumberFormatException e) {
					return;
				}
			}
		}
	}

	private String[] parseInput(String msg) {
		if (msg.startsWith("tell")) { //$NON-NLS-1$
			msg = msg.substring(5);
			int index = msg.indexOf(' ');
			if (index == -1) {
				return null;
			}
			String user = msg.substring(0, index);
			msg = msg.substring(index + 1);
			index = msg.indexOf(' ');
			if (index == -1) {
				return null;
			}
			String tmp = msg.substring(0, index);
			if (tmp.equals("about")) { //$NON-NLS-1$
				msg = msg.substring(index + 1);
			}
			return new String[] { user, msg };
		} else {
			return new String[] { null, msg };
		}
	}

	public void handleRoomMessage(IChatRoomMessage message) {
		try {
			String[] info = parseInput(message.getMessage().trim().substring(1));
			if (info != null) {
				send(message.getChatRoomID(), info[0], info[1]);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void init(IChatRoomBotEntry robot) {
		// nothing to do
	}

	public void preChatRoomConnect(IChatRoomContainer roomContainer, ID roomID) {
		messageSenders.put(roomID, roomContainer.getChatRoomMessageSender());
	}

	public void preContainerConnect(IContainer container, ID targetID) {
		this.container = container;
	}

}
