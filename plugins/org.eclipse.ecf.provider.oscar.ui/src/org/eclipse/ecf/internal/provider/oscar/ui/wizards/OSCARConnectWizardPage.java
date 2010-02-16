/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.ui.wizards;

import java.util.*;
import java.util.List;
import org.eclipse.ecf.internal.provider.oscar.ui.Activator;
import org.eclipse.ecf.internal.provider.oscar.ui.Messages;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class OSCARConnectWizardPage extends WizardPage {

	public static final String EMPTY = ""; //$NON-NLS-1$

	Combo connectText;

	Text passwordText;

	String uin;

	OSCARConnectWizardPage() {
		super(EMPTY);
		setTitle(Messages.OSCAR_CONNECT_WIZARD_PAGE_WIZARD_TITLE);
		setDescription(Messages.OSCAR_CONNECT_WIZARD_PAGE_WIZARD_DESCRIPTION);
		setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_CHAT_WIZARD));
		setPageComplete(false);
	}

	OSCARConnectWizardPage(String uin) {
		this();
		this.uin = uin;
	}

	private void verify() {
		final String text = connectText.getText();
		if (text.equals(EMPTY)) {
			updateStatus(Messages.OSCAR_CONNECT_WIZARD_PAGE_STATUS_UIN_NULL);
		} else if (!isUinValid(text)) {
			updateStatus(Messages.OSCAR_CONNECT_WIZARD_PAGE_STATUS_UIN_NOT_VALID);
		} else {
			restorePassword(text);
			updateStatus(null);
		}
	}

	private boolean isUinValid(String uin) {
		if (uin.length() < 5)
			return false;

		final char[] chars = uin.toCharArray();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] < '0' || chars[i] > '9')
				return false;

		return true;
	}

	public void createControl(Composite parent) {

		parent = new Composite(parent, SWT.NONE);

		parent.setLayout(new GridLayout());
		final GridData fillData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		Label label = new Label(parent, SWT.LEFT);
		label.setText(Messages.OSCAR_CONNECT_WIZARD_PAGE_LABEL_USERID);

		connectText = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
		connectText.setLayoutData(fillData);
		connectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		connectText.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				verify();
			}

			public void widgetSelected(SelectionEvent e) {
				verify();
			}
		});

		label = new Label(parent, SWT.LEFT);
		label.setText(Messages.OSCAR_CONNECT_WIZARD_PAGE_LABEL_PASSWORD);
		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(fillData);

		restoreCombo();

		if (uin != null) {
			connectText.setText(uin);
			restorePassword(uin);
			passwordText.setFocus();
		}

		if (connectText.getText().equals("")) { //$NON-NLS-1$
			updateStatus(null);
			//setPageComplete(false);
		} else if (isPageComplete())
			passwordText.setFocus();

		Dialog.applyDialogFont(parent);
		setControl(parent);
	}

	protected void restorePassword(String username) {
		final PasswordCacheHelper pwStorage = new PasswordCacheHelper(username);
		final String pw = pwStorage.retrievePassword();
		if (pw != null) {
			passwordText.setText(pw);
			passwordText.setSelection(0, pw.length());
		}
	}

	String getUin() {
		return connectText.getText();
	}

	String getPassword() {
		return passwordText.getText();
	}

	protected void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private static final String PAGE_SETTINGS = OSCARConnectWizardPage.class.getName();
	private static final int MAX_COMBO_VALUES = 40;
	private static final String COMBO_TEXT_KEY = "connectTextValue"; //$NON-NLS-1$
	private static final String COMBO_BOX_ITEMS_KEY = "comboValues"; //$NON-NLS-1$

	protected void saveComboText() {
		final IDialogSettings pageSettings = getPageSettings();
		if (pageSettings != null)
			pageSettings.put(COMBO_TEXT_KEY, connectText.getText());
	}

	protected void saveComboItems() {
		final IDialogSettings pageSettings = getPageSettings();
		if (pageSettings != null) {
			final String connectTextValue = connectText.getText();
			final List rawItems = Arrays.asList(connectText.getItems());
			// If existing text item is not in combo box then add it
			final List items = new ArrayList();
			if (!rawItems.contains(connectTextValue))
				items.add(connectTextValue);
			items.addAll(rawItems);
			int itemsToSaveLength = items.size();
			if (itemsToSaveLength > MAX_COMBO_VALUES)
				itemsToSaveLength = MAX_COMBO_VALUES;
			final String[] itemsToSave = new String[itemsToSaveLength];
			System.arraycopy(items.toArray(new String[] {}), 0, itemsToSave, 0, itemsToSaveLength);
			pageSettings.put(COMBO_BOX_ITEMS_KEY, itemsToSave);
		}
	}

	public IDialogSettings getDialogSettings() {
		return Activator.getDefault().getDialogSettings();
	}

	private IDialogSettings getPageSettings() {
		IDialogSettings pageSettings = null;
		final IDialogSettings dialogSettings = this.getDialogSettings();
		if (dialogSettings != null) {
			pageSettings = dialogSettings.getSection(PAGE_SETTINGS);
			if (pageSettings == null)
				pageSettings = dialogSettings.addNewSection(PAGE_SETTINGS);
			return pageSettings;
		}
		return null;
	}

	protected void restoreCombo() {
		final IDialogSettings pageSettings = getPageSettings();
		if (pageSettings != null) {
			final String[] items = pageSettings.getArray(COMBO_BOX_ITEMS_KEY);
			if (items != null)
				connectText.setItems(items);
			final String text = pageSettings.get(COMBO_TEXT_KEY);
			if (text != null)
				connectText.setText(text);
		}
	}
}
