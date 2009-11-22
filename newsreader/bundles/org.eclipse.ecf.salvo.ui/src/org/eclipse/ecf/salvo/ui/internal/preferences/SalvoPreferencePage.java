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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SalvoPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private DataBindingContext m_bindingContext;
	private Button btnMoreArticleViews;

	private PreferenceModel model = PreferenceModel.instance;

	public SalvoPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite drawing = new Composite(parent, SWT.None);
		drawing.setLayout(new GridLayout(1, false));

		btnMoreArticleViews = new Button(drawing, SWT.CHECK);
		btnMoreArticleViews.setText("More Article Views");
		m_bindingContext = initDataBindings();

		return drawing;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	public void setMoreArticleViews() {

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue btnMoreArticleViewsSelectionObserveWidget = SWTObservables
				.observeSelection(btnMoreArticleViews);
		IObservableValue modelObs = PojoObservables.observeValue(model,
				PreferenceModel.VIEW_PER_GROUP);
		bindingContext.bindValue(btnMoreArticleViewsSelectionObserveWidget,
				modelObs, null, null);
		//
		return bindingContext;
	}
}
