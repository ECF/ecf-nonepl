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
package org.eclipse.ecf.internal.provider.google.ui;

/*
 * public class NotificationPopup extends
 * org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup {
 * 
 * private NotificationEvent notification; private GoogleContainer container;
 * 
 * public NotificationEvent getNotification() { return notification; }
 * 
 * public void setNotification(NotificationEvent notification) {
 * this.notification = notification; }
 * 
 * public NotificationPopup(Shell shell, GoogleContainer container) {
 * super(shell.getDisplay()); this.container = container; }
 * 
 * private void createContentAreaForIncomingCallNotification( final Composite
 * parent) { Composite notificationComposite = new Composite(parent,
 * SWT.NO_FOCUS); notificationComposite.setLayout(new GridLayout(1, false));
 * notificationComposite.setBackground(parent.getBackground());
 * 
 * Label l2 = new Label(notificationComposite, SWT.NO_FOCUS);
 * 
 * l2.setText("Incoming call from" + (String)
 * notification.getProperty(NotificationEvent.JID));
 * l2.setBackground(parent.getBackground());
 * 
 * final NotificationHyperlink itemLink = new NotificationHyperlink(
 * notificationComposite, SWT.BEGINNING | SWT.NO_FOCUS);
 * GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
 * SWT.TOP).applyTo(itemLink);
 * 
 * itemLink.setText("Answer"); //
 * itemLink.setImage(notification.getNotificationImage());
 * itemLink.setBackground(parent.getBackground());
 * itemLink.addHyperlinkListener(new HyperlinkAdapter() {
 * 
 * @Override public void linkActivated(
 * org.eclipse.ui.forms.events.HyperlinkEvent e) {
 * System.out.println("Trying to Answer call");
 * 
 * container.getVoiceCallInterface() .AnswerReceivingCall(
 * container.getVoiceCallInterface() .getIncomingCallerID());
 * System.out.println("Answered call"); } });
 * 
 * }
 * 
 * private void createContentAreaForMailNotification(final Composite parent) {
 * Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
 * notificationComposite.setLayout(new GridLayout(1, false));
 * notificationComposite.setBackground(parent.getBackground());
 * 
 * final NotificationHyperlink itemLink = new NotificationHyperlink(
 * notificationComposite, SWT.BEGINNING | SWT.NO_FOCUS);
 * GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
 * SWT.TOP).applyTo(itemLink);
 * 
 * itemLink.setText(notification.getNotification()); //
 * itemLink.setImage(notification.getNotificationImage());
 * itemLink.setBackground(parent.getBackground());
 * itemLink.addHyperlinkListener(new HyperlinkAdapter() {
 * 
 * @Override public void linkActivated(
 * org.eclipse.ui.forms.events.HyperlinkEvent e) { URL url = null; try { url =
 * new URL((String) NotificationPopup.this.notification
 * .getProperty(MailNotificationEvent.URL)); System.out.println("URL" + url); }
 * catch (MalformedURLException e2) { // TODO Auto-generated catch block
 * e2.printStackTrace(); } try { PlatformUI.getWorkbench().getBrowserSupport()
 * .createBrowser(null).openURL(url); } catch (PartInitException e1) { // TODO
 * Auto-generated catch block e1.printStackTrace(); } } });
 * 
 * Label l2 = new Label(notificationComposite, SWT.NO_FOCUS);
 * 
 * l2.setText((String) notification
 * .getProperty(MailNotificationEvent.SNIPPET));
 * l2.setBackground(parent.getBackground());
 * 
 * }
 * 
 * protected void createContentArea(final Composite parent) { if
 * (notification.getType().equals( NotificationEvent.TYPE_MAIL_NOTIFICATION)) {
 * createContentAreaForMailNotification(parent); } if
 * (notification.getType().equals(NotificationEvent.TYPE_INCOMING_CALL)) {
 * createContentAreaForIncomingCallNotification(parent); } else {
 * 
 * createContentAreaForUndefinedType(parent); } }
 * 
 * private void createContentAreaForUndefinedType(Composite parent) { Composite
 * notificationComposite = new Composite(parent, SWT.NO_FOCUS);
 * notificationComposite.setLayout(new GridLayout(1, false));
 * notificationComposite.setBackground(parent.getBackground());
 * 
 * Label l2 = new Label(notificationComposite, SWT.NO_FOCUS);
 * l2.setText(notification.getNotification());
 * l2.setBackground(parent.getBackground());
 * 
 * } }
 */