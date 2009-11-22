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
package org.eclipse.ecf.salvo.ui.internal.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.services.quotes.QuoteService;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

public class SignaturePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Text signature;
	private Table table;
	private Table table_1;
	private Button button;
	private Button button_1;

	public SignaturePreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public SignaturePreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @wbp.parser.constructor
	 */
	public SignaturePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite drawing = new Composite(parent, SWT.None);
		drawing.setLayout(new GridLayout(3, false));

		try {
			ServiceReference[] x = Activator.getDefault().getBundle()
					.getBundleContext().getAllServiceReferences(
							QuoteService.class.getName(), null);
			for (int i = 0; i < x.length; i++) {
				Debug.log(getClass(), x[i].getProperty("component.name")
						.toString());
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				TableViewer tableViewer_2_1 = new TableViewer(drawing, SWT.BORDER
						| SWT.FULL_SELECTION);
				table_1 = tableViewer_2_1.getTable();
				table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		new Label(drawing, SWT.NONE);
		TableViewer tableViewer_1_1 = new TableViewer(drawing, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer_1_1.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				3));
		
		button_1 = new Button(drawing, SWT.NONE);
		button_1.setText(">");
		
		button = new Button(drawing, SWT.NONE);
		button.setText("<");

		GridData layoutData3 = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		layoutData3.horizontalSpan = 3;

		signature = new Text(drawing, SWT.BORDER | SWT.MULTI);
		signature.setLayoutData(layoutData3);

		return drawing;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
}
