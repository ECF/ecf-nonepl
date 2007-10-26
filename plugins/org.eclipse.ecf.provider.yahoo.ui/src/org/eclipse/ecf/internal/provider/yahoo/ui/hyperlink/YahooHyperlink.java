/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.yahoo.ui.hyperlink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.provider.yahoo.ui.Activator;
import org.eclipse.ecf.internal.provider.yahoo.ui.Messages;
import org.eclipse.ecf.internal.provider.yahoo.ui.wizards.YahooConnectWizard;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.ecf.provider.yahoo.container.YahooContainer;
import org.eclipse.ecf.provider.yahoo.identity.YahooID;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * 
 */
public class YahooHyperlink extends AbstractURLHyperlink {

	private static final String ECF_YAHOO_CONTAINER_NAME = "ecf.yahoo.jymsg"; //$NON-NLS-1$

	private static final IContainer[] EMPTY = new IContainer[0];

	/**
	 * Creates a new URL hyperlink.
	 * 
	 * @param region
	 * @param uri
	 */
	public YahooHyperlink(IRegion region, URI uri) {
		super(region, uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createConnectWizard()
	 */
	protected IConnectWizard createConnectWizard() {
		return new YahooConnectWizard(getURI().getAuthority());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createContainer()
	 */
	protected IContainer createContainer() throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(ECF_YAHOO_CONTAINER_NAME);
	}

	protected IContainer[] getContainers() {
		final IContainerManager manager = Activator.getDefault().getContainerManager();
		if (manager == null)
			return EMPTY;
		final List results = new ArrayList();
		final IContainer[] containers = manager.getAllContainers();
		for (int i = 0; i < containers.length; i++) {
			final ID connectedID = containers[i].getConnectedID();
			// Must be connected
			if (connectedID != null && containers[i] instanceof YahooContainer) {
				results.add(containers[i]);
			}
		}
		return (IContainer[]) results.toArray(EMPTY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#open()
	 */
	public void open() {
		final IContainer[] containers = getContainers();
		if (containers.length > 0)
			chooseAccountAndOpenMessagesView(containers);
		else {
			if (MessageDialog.openQuestion(null, Messages.YahooHyperlink_CONNECT_DIALOG_TITLE, NLS.bind(Messages.YahooHyperlink_CONNECT_DIALOG_MESSAGE, getURI().getAuthority()))) {
				super.open();
			}
		}
	}

	/**
	 * @param adapters
	 */
	private void chooseAccountAndOpenMessagesView(final IContainer[] containers) {
		// If there's only one choice then use it
		if (containers.length == 1) {
			openMessagesView((IPresenceContainerAdapter) containers[0].getAdapter(IPresenceContainerAdapter.class));
			return;
		} else {
			final IPresenceContainerAdapter[] adapters = new IPresenceContainerAdapter[containers.length];
			for (int i = 0; i < containers.length; i++)
				adapters[i] = (IPresenceContainerAdapter) containers[i].getAdapter(IPresenceContainerAdapter.class);
			final ListDialog dialog = new ListDialog(null);
			dialog.setContentProvider(new IStructuredContentProvider() {

				public Object[] getElements(Object inputElement) {
					return adapters;
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
			});
			dialog.setInput(adapters);
			dialog.setAddCancelButton(true);
			dialog.setBlockOnOpen(true);
			dialog.setTitle(Messages.YahooHyperlink_SELECT_ACCOUNT_DIALOG_TITLE);
			dialog.setMessage(Messages.YahooHyperlink_SELECT_ACCOUNT_DIALOG_MESSAGE);
			dialog.setHeightInChars(adapters.length > 4 ? adapters.length : 4);
			dialog.setInitialSelections(new IPresenceContainerAdapter[] {adapters[0]});
			dialog.setLabelProvider(new ILabelProvider() {
				public Image getImage(Object element) {
					return null;
				}

				public String getText(Object element) {
					final IRosterManager manager = ((IPresenceContainerAdapter) element).getRosterManager();
					if (manager == null)
						return null;
					return manager.getRoster().getUser().getID().getName();
				}

				public void addListener(ILabelProviderListener listener) {
				}

				public void dispose() {
				}

				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
				}
			});
			final int result = dialog.open();
			if (result == ListDialog.OK) {
				final Object[] res = dialog.getResult();
				if (res.length > 0)
					openMessagesView((IPresenceContainerAdapter) res[0]);
			}
		}
	}

	/**
	 * @param presenceContainerAdapter
	 */
	private void openMessagesView(IPresenceContainerAdapter presenceContainerAdapter) {
		final IChatManager chatManager = presenceContainerAdapter.getChatManager();
		final IRosterManager rosterManager = presenceContainerAdapter.getRosterManager();
		if (chatManager != null && rosterManager != null) {
			final IChatMessageSender icms = chatManager.getChatMessageSender();
			final ITypingMessageSender itms = chatManager.getTypingMessageSender();
			try {
				final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				final MessagesView view = (MessagesView) ww.getActivePage().showView(MessagesView.VIEW_ID);
				final ID localID = rosterManager.getRoster().getUser().getID();
				view.selectTab(icms, itms, localID, new YahooID(localID.getNamespace(), getURI().getAuthority()));
			} catch (final Exception e) {
				MessageDialog.openError(null, Messages.YahooHyperlink_ERROR_MESSAGE_TITLE, NLS.bind(Messages.YahooHyperlink_ERROR_MESSAGE_MESSAGE, e.getLocalizedMessage()));
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.YahooHyperlink_ERROR_STATUS_MESSAGE, e));
			}
		}
	}

}
