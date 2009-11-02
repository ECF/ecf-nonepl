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

public class SignaturePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Button button;
	private Button button_1;
	private Button button_2;
	private Button button_3;
	private Browser browser;

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
		drawing.setLayout(new GridLayout(4, false));

		button = new Button(drawing, SWT.NONE);
		button.addMouseListener(new ButtonMouseListener());
		button.setText("New Button");

		button_1 = new Button(drawing, SWT.NONE);
		button_1.setText("New Button");
		button_1.addMouseListener(new ButtonMouseListener());

		button_2 = new Button(drawing, SWT.NONE);
		button_2.setText("New Button");
		button_2.addMouseListener(new ButtonMouseListener());

		button_3 = new Button(drawing, SWT.NONE);
		button_3.setText("New Button");
		button_3.addMouseListener(new ButtonMouseListener());

		browser = new Browser(drawing, SWT.NONE);
		browser
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4,
						1));

		return drawing;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	private class ButtonMouseListener extends MouseAdapter {
		private IWebBrowser browser2;

		@Override
		public void mouseUp(MouseEvent e) {

			if (browser2 == null)
				try {
					browser2 = PlatformUI.getWorkbench().getBrowserSupport()
							.createBrowser("dd");
				} catch (PartInitException e1) {
				}

			try {
				URL url1 = null;
				if (url1 == null) {
					url1 = new URL("http://www.remainsoftware.com");
				}
				if (e.widget.equals(button))
					browser2.openURL(url1);
				if (e.widget.equals(button_1))
					browser2.openURL(new URL("http://www.industrial-tsi.com"));
				if (e.widget.equals(button_2))
					browser2.openURL(new URL("http://www.google.nl"));
				if (e.widget.equals(button_3))
					browser2.openURL(new URL("http://www.eclipse.org"));
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
}
