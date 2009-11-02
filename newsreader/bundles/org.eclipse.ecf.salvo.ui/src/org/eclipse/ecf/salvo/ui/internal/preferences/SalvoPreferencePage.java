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


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SalvoPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{
	public SalvoPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite drawing = new Composite(parent, SWT.None);
		drawing.setLayout(new GridLayout(8, false));

		final ProgressBar p1 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);

		final ProgressBar p2 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);

		final ProgressBar p3 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);
		p3.setBounds(0, 0, 170, 17);

		final ProgressBar p4 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);
		p4.setBounds(0, 0, 170, 17);

		final ProgressBar p5 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);
		p5.setBounds(0, 0, 170, 17);

		final ProgressBar p6 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);
		p6.setBounds(0, 0, 170, 17);

		final ProgressBar p7 = new ProgressBar(drawing, SWT.SMOOTH | SWT.VERTICAL);
		p7.setBounds(0, 0, 170, 17);

		final ProgressBar p8 = new ProgressBar(drawing, SWT.SMOOTH
				| SWT.VERTICAL);
		p8.setBounds(0, 0, 170, 17);

		final Scale scale = new Scale(drawing, SWT.NONE);
		scale.setMaximum(800);
		scale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 8,
				1));

		scale.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
		
				if (scale.getSelection() > 700)
					p8.setSelection(scale.getSelection() - 700);
				else
					p8.setSelection(0);

				if (scale.getSelection() > 600)
					p7.setSelection(scale.getSelection() - 600);
				else
					p7.setSelection(0);

				if (scale.getSelection() > 500)
					p6.setSelection(scale.getSelection() - 500);
				else
					p6.setSelection(0);

				if (scale.getSelection() > 400)
					p5.setSelection(scale.getSelection() - 400);
				else
					p5.setSelection(0);

				if (scale.getSelection() > 300)
					p4.setSelection(scale.getSelection() - 300);
				else
					p4.setSelection(0);

				if (scale.getSelection() > 200)
					p3.setSelection(scale.getSelection() - 200);
				else
					p3.setSelection(0);

				if (scale.getSelection() > 100)
					p2.setSelection(scale.getSelection() - 100);
				else
					p2.setSelection(0);

				if (scale.getSelection() > 0)
					p1.setSelection(scale.getSelection());
				else
					p1.setSelection(0);

				// TODO Auto-generated method stub

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		new Label(drawing, SWT.NONE);

		return drawing;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
}
