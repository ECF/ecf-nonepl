/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.wizards;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.provider.NewsContentProvider;
import org.eclipse.ecf.salvo.ui.internal.provider.NewsGroupProvider;
import org.eclipse.ecf.salvo.ui.internal.provider.NewsLabelProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;


public class SubscribeGroupWizardPage extends WizardPage {

	private Tree tree;
	private CheckboxTreeViewer checkboxTreeViewer;

	protected SubscribeGroupWizardPage(String pageName) {
		super(pageName);
		setDescription("Select the groups you want to subscribe to.");
	}

	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		setControl(composite);

		checkboxTreeViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
		tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		checkboxTreeViewer.setContentProvider(new NewsContentProvider());
		checkboxTreeViewer.setLabelProvider(new NewsLabelProvider());
		checkboxTreeViewer.setComparator(new ViewerComparator());

	}

	public Collection<INewsgroup> getGroups() {

		Collection<INewsgroup> result = new ArrayList<INewsgroup>();
		if (checkboxTreeViewer == null)
			return result;

		for (Object object : checkboxTreeViewer.getCheckedElements()) {

			if (object instanceof ISalvoResource) {
				if (((ISalvoResource) object).getObject() instanceof INewsgroup) {
					result.add((INewsgroup) ((ISalvoResource) object).getObject());
				}
			}
		}
		return result;
	}

	public void setInput(IServer server) {
		setTitle(server.getAddress());
		ISalvoResource s2 = SalvoResourceFactory.getResource(server.getAddress(), server);
		s2.setChildProvider(new NewsGroupProvider(s2));
		checkboxTreeViewer.setInput(s2);
	}
}
