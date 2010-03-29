/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.provider.google.*;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.telephony.call.ui.actions.AbstractCallAction;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

public class GoogleContributionItem extends AbstractRosterEntryContributionItem {

	protected IAction[] makeActions() {
		return null;
	}

	protected IContributionItem[] createContributionItemsForRoster(
			IRoster roster) {

		return null;

	}

	private ArrayList<IContributionItem> createActionUpdate(
			final GoogleContainer container) {
		ArrayList<IContributionItem> items = new ArrayList<IContributionItem>();

		// create the status msg submenu
		MenuManager statusMenuManager = new MenuManager("Set Status");
		final GoogleStatusMessageManager statusManager = (GoogleStatusMessageManager) container
				.getPresenceHelper().getStatusMessageManager();

		// new status menu item
		final Action setStatusAction = new Action() {

			public void run() {
				final InputDialog id = new InputDialog(null,
						"Send Google Status Update", "Status", "",
						new IInputValidator() {
							public String isValid(String newText) {
								if (newText != null && (newText.length() < 512))
									return null;
								return "Over maximum character limit (" + 512
										+ ")";
							}
						});
				id.setBlockOnOpen(true);
				final int result = id.open();
				final String status = id.getValue();
				if (result == Window.OK && status != null) {
					try {
						// c.sendStatusUpdate(status);
						statusManager.setStatusMessage(status);
					} catch (final Exception e) {
					}
				}
			}
		};
		setStatusAction.setText("New Status Message...");

		statusMenuManager.add(new ActionContributionItem(setStatusAction));
		// add seperator
		statusMenuManager.add(new Separator());

		try {
			// default list
			ArrayList<String> list = statusManager
					.getStatusListAsReverseArray(statusManager
							.getDefaultStatusList());
			for (final String status : list) {
				final Action action = new Action() {
					public void run() {
						statusManager.setStatusMessage(status, false);
					}
				};
				action.setText(status);
				statusMenuManager.add(action);
			}

			// dnd list
			list = statusManager.getStatusListAsReverseArray(statusManager
					.getDndStatusList());
			if (list != null) {

				if (list.size() > 0) {
					// add seperator
					statusMenuManager.add(new Separator());

				}
				for (final String status : list) {
					final Action action = new Action() {
						public void run() {
							statusManager.setStatusMessage(status, true);
						}
					};
					action.setText(status);
					statusMenuManager.add(action);
				}
			}
			items.add(statusMenuManager);
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final Action alterDndAction = new Action() {
			public void run() {
				statusManager.alterDnd();

			}
		};
		if (statusManager.isDnd()) {
			alterDndAction.setText("go Available");

		} else {
			alterDndAction.setText("go Busy");
		}
		items.add(new ActionContributionItem(alterDndAction));

		final Action setFileSaveLocationAction = new Action() {
			public void run() {
				final Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();

				final DirectoryDialog fd = new DirectoryDialog(shell, SWT.OPEN);
				// XXX this should be some default path set by preferences
				fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
				fd.setText("Choose folder");
				final String res = fd.open();
				container.setFileSaveLocation(res);
			}
		};
		setFileSaveLocationAction.setText("Set File Save Location...");
		items.add(new ActionContributionItem(setFileSaveLocationAction));
		return items;
	}

	protected IContributionItem[] getContributionItems() {
		final Object selection = getSelection();

		if (selection instanceof Roster) {
			IPresenceContainerAdapter pca = ((Roster) selection)
					.getPresenceContainerAdapter();
			IContainer container = (IContainer) pca
					.getAdapter(IContainer.class);
			if (container instanceof GoogleContainer) {
				ArrayList<IContributionItem> contributions = new ArrayList<IContributionItem>();

				contributions
						.addAll(createActionUpdate((GoogleContainer) container));
				return contributions.toArray(new IContributionItem[] {});
			}
		}

		if (!(selection instanceof IRosterEntry)) {
			return EMPTY_ARRAY;
		}
		final IRosterEntry entry = (IRosterEntry) selection;

		final IContainer container = getContainerForRosterEntry(entry);
		if (container instanceof GoogleContainer) {
			ArrayList<IContributionItem> contributions = new ArrayList<IContributionItem>();

			/*
			 * // file send
			 * 
			 * final ISendFileTransferContainerAdapter ioftca =
			 * (ISendFileTransferContainerAdapter) container
			 * .getAdapter(ISendFileTransferContainerAdapter.class); if ((ioftca
			 * != null && isAvailable(entry))) {
			 * 
			 * final IAction fileSendAction = new Action() { public void run() {
			 * sendFileToTarget(ioftca, entry.getUser().getID()); } };
			 * fileSendAction
			 * .setText(Messages.XMPPCompoundContributionItem_SEND_FILE);
			 * fileSendAction.setImageDescriptor(PlatformUI.getWorkbench()
			 * .getSharedImages().getImageDescriptor(
			 * ISharedImages.IMG_OBJ_FILE)); contributions.add(new
			 * ActionContributionItem(fileSendAction)); }
			 */GoogleContainerAccountManager accountManager = (GoogleContainerAccountManager) ((GoogleContainer) container)
					.getAccountManager();
			HashSet<String> featureSet = accountManager.getFeatures();
			if (((GoogleContainer) container).getCallSessionContainerAdapter()
					.isVoiceEnabled(entry.getUser().getID())) {

				if (((GoogleContainer) container)
						.getCallSessionContainerAdapter().getActiveCallerID()
						.equals(entry.getUser().getID().getName())) {
					final IAction hangupCall = new Action() {
						public void run() {
							hangupCall((GoogleContainer) container, entry);
						}
					};
					hangupCall.setText("Hangup");
					contributions.add(new ActionContributionItem(hangupCall));

					if (((GoogleContainer) container)
							.getCallSessionContainerAdapter()
							.isActiveCallMute()) {
						final IAction unmuteCall = new Action() {
							public void run() {
								muteCall(false, (GoogleContainer) container,
										entry);
							}
						};
						unmuteCall.setText("Unmute call");
						contributions
								.add(new ActionContributionItem(unmuteCall));
					} else {
						final IAction muteCall = new Action() {
							public void run() {
								muteCall(true, (GoogleContainer) container,
										entry);
							}
						};
						muteCall.setText("mute call");
						contributions.add(new ActionContributionItem(muteCall));

					}
				} else {

					final AbstractCallAction voiceCall = new AbstractCallAction(
							"Call") {

						// @Override
						protected IContainer getContainer() { // TODO
							// Auto-generated method stub
							return container;
						}

					};
					voiceCall.setCallReceiver(entry.getUser().getID());

					/*
					 * final IAction voiceCall = new Action() { public void
					 * run() { voiceCall((GoogleContainer) container, entry); }
					 * }; voiceCall.setText("Call_N");
					 */
					contributions.add(new ActionContributionItem(voiceCall));

				}
			}

			final ISendFileTransferContainerAdapter ioftca = (ISendFileTransferContainerAdapter) container
					.getAdapter(ISendFileTransferContainerAdapter.class);
			if (ioftca != null && isAvailable(entry)) {
				// return EMPTY_ARRAY;

				final IAction fileSendAction = new Action() {
					public void run() {
						sendFileToTarget(ioftca, entry.getUser().getID());
					}
				};
				fileSendAction.setText("Google File Send");
				contributions.add(new ActionContributionItem(fileSendAction));
			}

			if (featureSet.contains(GoogleIQ.XMLNS_OFF_THE_RECORD)) {

				final IAction offTheRecordChat = new Action() {
					public void run() {

						AlterArchivingEnabled((GoogleContainer) container,
								entry);
					}
				};
				if (((GoogleContainer) container).getPresenceHelper()
						.getNosaveState(entry.getUser().getID().getName()) == false) {
					offTheRecordChat
							.setText(Messages.GOOGLE_ENABLE_OFF_THE_RECORD);

				} else {
					offTheRecordChat
							.setText(Messages.GOOGLE_DISABLE_OFF_THE_RECORD);

				}
				contributions.add(new ActionContributionItem(offTheRecordChat));
			}

			IContributionItem[] array = new IContributionItem[contributions
					.size()];
			return contributions.toArray(array);
		} else {
			return EMPTY_ARRAY;
		}
	}

	protected void muteCall(boolean b, GoogleContainer container,
			IRosterEntry entry) {
		container.getCallSessionContainerAdapter().muteActiveCall(b);
	}

	protected void voiceCall(GoogleContainer container, IRosterEntry entry) {
		container.getCallSessionContainerAdapter().getVoiceCallInterface()
				.initVoiceCall(entry.getUser().getID().getName());
	}

	protected void hangupCall(GoogleContainer container, IRosterEntry entry) {
		container.getCallSessionContainerAdapter().hangupActiveCall();
	}

	protected void AlterArchivingEnabled(GoogleContainer container,
			IRosterEntry entry) {

		boolean isEnabled = container.getPresenceHelper().getNosaveState(
				entry.getUser().getID().getName());
		container.getPresenceHelper().setNoSaveEnabled(
				entry.getUser().getID().getName(), !isEnabled);

	}

	protected void goOffTheRecord(GoogleContainer container) {

	}

	private void sendFileToTarget(
			ISendFileTransferContainerAdapter fileTransfer, final ID targetID) {
		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		final FileDialog fd = new FileDialog(shell, SWT.OPEN);
		// XXX this should be some default path set by preferences
		fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
		fd.setText(NLS.bind(Messages.XMPPCompoundContributionItem_CHOOSE_FILE,
				targetID.getName()));
		final String res = fd.open();
		if (res != null) {
			final File aFile = new File(res);
			try {
				final IFileID targetFileID = FileIDFactory.getDefault()
						.createFileID(fileTransfer.getOutgoingNamespace(),
								new Object[] { targetID, res });
				fileTransfer.sendOutgoingRequest(targetFileID, aFile,
						new IFileTransferListener() {
							public void handleTransferEvent(
									final IFileTransferEvent event) {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										// XXX This should be handled more
										// gracefully/with better UI (progress
										// bar?)
										if (event instanceof IOutgoingFileTransferResponseEvent) {
											if (!((IOutgoingFileTransferResponseEvent) event)
													.requestAccepted())
												MessageDialog
														.openInformation(
																shell,
																Messages.XMPPCompoundContributionItem_FILE_SEND_REFUSED_TITLE,
																NLS
																		.bind(
																				Messages.XMPPCompoundContributionItem_FILE_SEND_REFUSED_MESSAGE,
																				res,
																				targetID
																						.getName()));
										}
									}
								});
							}
						}, null);
			} catch (final Exception e) {
				MessageDialog
						.openError(
								shell,
								Messages.XMPPCompoundContributionItem_SEND_ERROR_TITLE,
								NLS
										.bind(
												Messages.XMPPCompoundContributionItem_SEND_ERROR_MESSAGE,
												res, e.getLocalizedMessage()));
			}
		}
	}

}