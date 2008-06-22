/*******************************************************************************
 * Copyright (c) 2004, 2005 Jean-Michel Lemieux, Jeff McAffer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Hyperbola is an RCP application developed for the book 
 *     Eclipse Rich Client Platform - 
 *         Designing, Coding, and Packaging Java Applications 
 * See http://eclipsercp.org
 * 
 * Contributors:
 *     Jean-Michel Lemieux and Jeff McAffer - initial implementation
 *******************************************************************************/
package org.remotercp.contacts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ContactsLabelProvider extends LabelProvider implements
		IColorProvider {

	private Map<ImageDescriptor, Image> imageTable = new HashMap<ImageDescriptor, Image>(
			7);

	// private IRosterItem selection;

	protected final IWorkbenchAdapter getAdapter(Object element) {
		IWorkbenchAdapter adapter = null;
		if (element instanceof IAdaptable)
			adapter = (IWorkbenchAdapter) ((IAdaptable) element)
					.getAdapter(IWorkbenchAdapter.class);
		if (adapter == null)
			adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
					.loadAdapter(element, IWorkbenchAdapter.class.getName());
		return adapter;
	}

	public final Image getImage(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null) {
			return null;
		}
		ImageDescriptor descriptor = adapter.getImageDescriptor(element);
		if (descriptor == null) {
			return null;
		}
		Image image = (Image) imageTable.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageTable.put(descriptor, image);
		}
		return image;
	}

	public final String getText(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null) {
			return "";
		}
		return adapter.getLabel(element);
	}

	public void dispose() {
		if (imageTable != null) {
			for (Iterator<Image> i = imageTable.values().iterator(); i
					.hasNext();) {
				i.next().dispose();
			}
			imageTable = null;
		}
	}

	public Color getBackground(Object element) {
		return null;
	}

	/**
	 * This is a breach with the IAdaptable pattern but no other simple solution
	 * found yet.
	 * 
	 * The idea behind getForeground(...) is to color all selected online user,
	 * which are going to be remote managed in a different color. Hence if the
	 * administrator will be disturbed for a while he still knows which user he
	 * is going to remote manage.
	 * 
	 * @param selection
	 */
	public Color getForeground(Object element) {
		// if (this.selection != null) {
		// IRosterItem rosterItem = (IRosterItem) element;
		//
		// // check whether selecttion matches the element
		// if (this.selection.equals(rosterItem)) {
		// // color the root element
		// return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		// }
		//
		// // check whether children of a group have to be colored either
		// if (this.selection instanceof IRosterGroup
		// && rosterItem instanceof IRosterEntry) {
		// IRosterEntry rosterEntry = (IRosterEntry) rosterItem;
		//
		// List<IRosterEntry> filterOnlineUser = RosterUtil
		// .filterOnlineUser(this.selection);
		//
		// /*
		// * The current elements has to be a member of the selected group
		// * and online in order to be colored
		// */
		// if (filterOnlineUser.contains(rosterEntry)
		// && rosterEntry.getParent().equals(this.selection)) {
		// return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		//				}
		//			}
		//		}
		return null;
	}
	//
	// public void setSelection(IRosterItem selection) {
	// this.selection = selection;
	// }
}
