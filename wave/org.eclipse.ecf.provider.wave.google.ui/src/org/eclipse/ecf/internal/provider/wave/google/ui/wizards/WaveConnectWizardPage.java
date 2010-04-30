/****************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sebastian Schmidt <mail@schmidt-seb.de> - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.wave.google.ui.wizards;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ecf.internal.provider.wave.google.ui.Messages;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

final class WaveConnectWizardPage extends WizardPage {

	private Combo emailText;

	private Text serverText;

	private Text passwordText;

	private static Pattern emailPattern = Pattern.compile(".+@.+.[a-z]+"); //$NON-NLS-1$ 

	private static Pattern serverPattern = Pattern
			.compile(".[^:]+(:[0-9]{2,5})?"); //$NON-NLS-1$ 

	public WaveConnectWizardPage() {
		super(WaveConnectWizardPage.class.getName());

		setTitle(Messages.WaveConnectWizardPage_Title);
		setDescription(Messages.WaveConnectWizardPage_WIZARD_PAGE_DESCRIPTION);
		setPageComplete(false);
		setImageDescriptor(SharedImages
				.getImageDescriptor(SharedImages.IMG_CHAT_WIZARD));
	}

	public void createControl(Composite parent) {
		parent = new Composite(parent, SWT.NONE);

		parent.setLayout(new GridLayout(2, false));

		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);

		Label label = new Label(parent, SWT.LEFT);
		label.setText(Messages.WaveConnectWizardPage_EmailAddressLabel);
		emailText = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
		emailText.setLayoutData(data);

		label = new Label(parent, SWT.LEFT);
		label.setText(Messages.WaveConnectWizardPage_ServerLabel);
		serverText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		serverText.setLayoutData(data);

		label = new Label(parent, SWT.LEFT);
		label.setText(Messages.WaveConnectWizardPage_PasswordLabel);
		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(data);

		addListeners();

		Dialog.applyDialogFont(parent);
		setControl(parent);
	}

	private void addListeners() {
		emailText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		serverText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});

		serverText.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				generateServerUri();
			}
		});

		emailText.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				verify();
			}

			public void widgetSelected(SelectionEvent e) {
				verify();
			}
		});

		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
	}

	private void generateServerUri() {
		if (verifyEmail() && getServer().equals("")) {
			int index = getEmail().lastIndexOf("@");
			serverText.setText(getEmail().substring(index + 1));
		}
	}

	private void verify() {
		if (verifyEmail()) {
			if (verifyServer()) {
				if (passwordText.getText().equals("")) {
					setErrorMessage(Messages.WaveConnectWizardPage_PasswordRequired);
				} else {
					setErrorMessage(null);
				}
			}
		}
	}

	private boolean verifyServer() {
		String server = serverText.getText().trim();
		if (server.equals("")) {
			setErrorMessage(Messages.WaveConnectWizardPage_ServerRequired);
			return false;
		} else {
			Matcher matcher = serverPattern.matcher(server);
			if (!matcher.matches()) {
				setErrorMessage(Messages.WaveConnectWizardPage_ServerInvalid);
				return false;
			}
		}

		return true;
	}

	private boolean verifyEmail() {
		String email = emailText.getText().trim();
		if (email.equals("")) {
			setErrorMessage(Messages.WaveConnectWizardPage_EmailAddressRequired);
			return false;
		} else {
			Matcher matcher = emailPattern.matcher(email);
			if (!matcher.matches()) {
				setErrorMessage(Messages.WaveConnectWizardPage_EmailAddressInvalid);
				return false;
			}
		}

		return true;
	}

	public void setErrorMessage(String message) {
		super.setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getEmail() {
		return emailText.getText();
	}

	public String getPassword() {
		return passwordText.getText();
	}

	public String getServer() {
		return serverText.getText();
	}

}
