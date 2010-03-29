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

/*
 * @since 3.0
 */
package org.eclipse.ecf.internal.provider.google.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.provider.google.events.MailNotificationEvent;
import org.eclipse.ecf.provider.google.events.NotificationEvent;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.*;

public class NotifierDialog {

	// how long the the tray popup is displayed after fading in (in
	// milliseconds)
	private static final int DISPLAY_TIME = 3000;
	// how long each tick is when fading in (in ms)
	private static final int FADE_TIMER = 10;
	// how long each tick is when fading out (in ms)
	private static final int FADE_IN_STEP = 10;
	// how many tick steps we use when fading out
	private static final int FADE_OUT_STEP = 2;

	// how high the alpha value is when we have finished fading in
	private static final int FINAL_ALPHA = 225;

	// title foreground color
	private static Color _titleFgColor = ColorCache.getColor(40, 73, 97);
	// text foreground color
	private static Color _fgColor = _titleFgColor;

	// shell gradient background color - top
	private static Color _bgFgGradient = ColorCache.getColor(226, 239, 249);
	// shell gradient background color - bottom
	private static Color _bgBgGradient = ColorCache.getColor(177, 211, 243);
	// shell border color
	private static Color _borderColor = ColorCache.getColor(40, 73, 97);

	// contains list of all active popup shells
	private static List<Shell> _activeShells = new ArrayList<Shell>();

	// image used when drawing
	private static Image _oldImage;

	private static Shell _shell;

	private static NotifierDialog self = null;

	private NotifierDialog(GoogleContainer container) {
	}

	public static NotifierDialog getDefault(GoogleContainer container) {
		if (self == null) {
			self = new NotifierDialog(container);
		}
		return self;

	}

	public void notify(final NotificationEvent event) {

		String title = event.getTitle();
		String message = event.getNotification();
		if (event.getType().equals(NotificationEvent.TYPE_MAIL_NOTIFICATION)) {
			message = (String) event.getProperty(MailNotificationEvent.SNIPPET);
		}
		NotificationType type = NotificationType.INFO;

		_shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.NO_TRIM);

		// _shell = PlatformUI.createDisplay().getActiveShell();
		_shell.setLayout(new FillLayout());
		_shell.setForeground(_fgColor);
		_shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		_shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				_activeShells.remove(_shell);
			}
		});

		final Composite inner = new Composite(_shell, SWT.NONE);

		GridLayout gl = new GridLayout(3, false);
		gl.marginLeft = 5;
		gl.marginTop = 0;
		gl.marginRight = 5;
		gl.marginBottom = 5;

		inner.setLayout(gl);
		_shell.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event e) {
				try {
					// get the size of the drawing area
					Rectangle rect = _shell.getClientArea();
					// create a new image with that size
					Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
					// create a GC object we can use to draw with
					GC gc = new GC(newImage);

					// fill background
					gc.setForeground(_bgFgGradient);
					gc.setBackground(_bgBgGradient);
					gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

					// draw shell edge
					gc.setLineWidth(2);
					gc.setForeground(_borderColor);
					gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
					// remember to dipose the GC object!
					gc.dispose();

					// now set the background image on the shell
					_shell.setBackgroundImage(newImage);

					// remember/dispose old used iamge
					if (_oldImage != null) {
						_oldImage.dispose();
					}
					_oldImage = newImage;
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		});

		GC gc = new GC(_shell);

		String lines[] = message.split("\n");
		Point longest = null;
		int typicalHeight = gc.stringExtent("X").y;

		for (String line : lines) {
			Point extent = gc.stringExtent(line);
			if (longest == null) {
				longest = extent;
				continue;
			}

			if (extent.x > longest.x) {
				longest = extent;
			}
		}
		gc.dispose();

		int minHeight = typicalHeight * lines.length;

		CLabel imgLabel = new CLabel(inner, SWT.NONE);
		imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
		imgLabel.setImage(type.getImage());

		/*
		 * CLabel titleLabel = new CLabel(inner, SWT.NONE);
		 * titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
		 * GridData.VERTICAL_ALIGN_CENTER)); titleLabel.setText(title);
		 * titleLabel.setForeground(_titleFgColor); Font f =
		 * titleLabel.getFont(); FontData fd = f.getFontData()[0];
		 * fd.setStyle(SWT.BOLD); fd.height = 11;
		 * titleLabel.setFont(FontCache.getFont(fd));
		 */
		if (event.getType().equals(NotificationEvent.TYPE_MAIL_NOTIFICATION)) {
			final NotificationHyperlink itemLink = new NotificationHyperlink(inner, SWT.BEGINNING | SWT.NO_FOCUS);
			// GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
			// SWT.TOP).applyTo(itemLink);
			itemLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
			itemLink.setText(title);
			itemLink.setForeground(_titleFgColor);
			Font f = itemLink.getFont();
			FontData fd = f.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			fd.height = 11;
			itemLink.setFont(FontCache.getFont(fd));

			// itemLink.setText(title); //
			// itemLink.setImage(event.getNotificationImage());
			// itemLink.setBackground(parent.getBackground());
			itemLink.addHyperlinkListener(new NotificationHyperlinkAdapter(event));

			CLabel blnkLabel = new CLabel(inner, SWT.NONE);
			blnkLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));

		} else if (event.getType().equals(NotificationEvent.TYPE_INCOMING_CALL)) {

			final NotificationHyperlink callAcceptItemLink = new NotificationHyperlink(inner, SWT.CENTER | SWT.FOCUSED);
			// GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
			// SWT.TOP).applyTo(itemLink);
			callAcceptItemLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
			callAcceptItemLink.setText("Answer");
			callAcceptItemLink.setForeground(_titleFgColor);
			Font f = callAcceptItemLink.getFont();
			FontData fd = f.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			fd.height = 10;
			callAcceptItemLink.setFont(FontCache.getFont(fd));

			// itemLink.setText(title); //
			// itemLink.setImage(event.getNotificationImage());
			// itemLink.setBackground(parent.getBackground());
			callAcceptItemLink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {

					ICallSessionRequestEvent callSessionRequestEvent = (ICallSessionRequestEvent) event.getProperty(NotificationEvent.INCOMING_CALL_EVENT);
					try {
						callSessionRequestEvent.accept(null, null);
					} catch (CallException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/*
					 * container.getVoiceCallInterface().AnswerReceivingCall(
					 * container.getVoiceCallInterface()
					 * .getIncomingCallerID());
					 */}
			});

			final NotificationHyperlink callRejectItemLink = new NotificationHyperlink(inner, SWT.CENTER | SWT.NO_FOCUS);
			// GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
			// SWT.TOP).applyTo(itemLink);
			callRejectItemLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
			callRejectItemLink.setText("Reject");
			callRejectItemLink.setForeground(_titleFgColor);
			f = callRejectItemLink.getFont();
			fd = f.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			fd.height = 10;
			callRejectItemLink.setFont(FontCache.getFont(fd));

			// itemLink.setText(title); //
			// itemLink.setImage(event.getNotificationImage());
			// itemLink.setBackground(parent.getBackground());
			callRejectItemLink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					ICallSessionRequestEvent callSessionRequestEvent = (ICallSessionRequestEvent) event.getProperty(NotificationEvent.INCOMING_CALL_EVENT);
					callSessionRequestEvent.reject();
				}
			});

		} else if (event.getType().equals(NotificationEvent.TYPE_INCOMING_FILE)) {

			final NotificationHyperlink callAcceptItemLink = new NotificationHyperlink(inner, SWT.CENTER | SWT.FOCUSED);
			// GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
			// SWT.TOP).applyTo(itemLink);
			callAcceptItemLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
			callAcceptItemLink.setText("Accept");
			callAcceptItemLink.setForeground(_titleFgColor);
			Font f = callAcceptItemLink.getFont();
			FontData fd = f.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			fd.height = 10;
			callAcceptItemLink.setFont(FontCache.getFont(fd));

			// itemLink.setText(title); //
			// itemLink.setImage(event.getNotificationImage());
			// itemLink.setBackground(parent.getBackground());
			callAcceptItemLink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {

					IFileTransferRequestEvent callSessionRequestEvent = (IFileTransferRequestEvent) event.getProperty(NotificationEvent.INCOMING_FILE_EVENT);

					try {
						callSessionRequestEvent.accept(null, null);
					} catch (Exception e2) { // TODO
						e2.printStackTrace();
					}
				}
			});

			final NotificationHyperlink callRejectItemLink = new NotificationHyperlink(inner, SWT.CENTER | SWT.NO_FOCUS);
			// GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL,
			// SWT.TOP).applyTo(itemLink);
			callRejectItemLink.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
			callRejectItemLink.setText("Reject");
			callRejectItemLink.setForeground(_titleFgColor);
			f = callRejectItemLink.getFont();
			fd = f.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			fd.height = 10;
			callRejectItemLink.setFont(FontCache.getFont(fd));

			// itemLink.setText(title); //
			// itemLink.setImage(event.getNotificationImage());
			// itemLink.setBackground(parent.getBackground());
			callRejectItemLink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					IFileTransferRequestEvent fileTransferRequestEvent = (IFileTransferRequestEvent) event.getProperty(NotificationEvent.INCOMING_FILE_EVENT);
					fileTransferRequestEvent.reject();
				}
			});

		}

		Label text = new Label(inner, SWT.WRAP);
		Font tf = text.getFont();
		FontData tfd = tf.getFontData()[0];
		tfd.setStyle(SWT.BOLD);
		tfd.height = 8;
		text.setFont(FontCache.getFont(tfd));
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 3;
		text.setLayoutData(gd);
		text.setForeground(_fgColor);
		text.setText(message);

		minHeight = 100;

		_shell.setSize(350, minHeight);

		if (Display.getDefault().getActiveShell() == null || Display.getDefault().getActiveShell().getMonitor() == null) {
			return;
		}

		Rectangle clientArea = Display.getDefault().getActiveShell().getMonitor().getClientArea();

		int startX = clientArea.x + clientArea.width - 352;
		int startY = clientArea.y + clientArea.height - 102;

		// move other shells up
		if (!_activeShells.isEmpty()) {
			List<Shell> modifiable = new ArrayList<Shell>(_activeShells);
			Collections.reverse(modifiable);
			for (Shell shell : modifiable) {
				Point curLoc = shell.getLocation();
				shell.setLocation(curLoc.x, curLoc.y - 100);
				if (curLoc.y - 100 < 0) {
					_activeShells.remove(shell);
					shell.dispose();
				}
			}
		}

		_shell.setLocation(startX, startY);
		_shell.setAlpha(0);
		_shell.setVisible(true);

		_activeShells.add(_shell);

		fadeIn(_shell);
	}

	public class NotificationHyperlinkAdapter implements IHyperlinkListener {

		NotificationEvent event;

		public NotificationHyperlinkAdapter(NotificationEvent event) {
			this.event = event;
		}

		public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
			URL url = null;
			try {
				url = new URL((String) event.getProperty(MailNotificationEvent.URL));
				System.out.println("URL" + url);
			} catch (MalformedURLException e2) { // TODO Auto-generated
				// catch block
				e2.printStackTrace();
			}
			try {
				PlatformUI.getWorkbench().getBrowserSupport().createBrowser(null).openURL(url);
			} catch (PartInitException e1) { // TODO
				/* Auto-generated catch block */e1.printStackTrace();
			}
		}

		public void linkEntered(HyperlinkEvent e) {
			// TODO Auto-generated method stub

		}

		public void linkExited(HyperlinkEvent e) {
			// TODO Auto-generated method stub

		}

	}

	private void fadeIn(final Shell _shell) {
		Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					int cur = _shell.getAlpha();
					cur += FADE_IN_STEP;

					if (cur > FINAL_ALPHA) {
						_shell.setAlpha(FINAL_ALPHA);
						startTimer(_shell);
						return;
					}

					_shell.setAlpha(cur);
					Display.getDefault().timerExec(FADE_TIMER, this);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(FADE_TIMER, run);
	}

	private void startTimer(final Shell _shell) {
		Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					fadeOut(_shell);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(DISPLAY_TIME, run);

	}

	private void fadeOut(final Shell _shell) {
		final Runnable run = new Runnable() {

			public void run() {
				try {
					if (_shell == null || _shell.isDisposed()) {
						return;
					}

					int cur = _shell.getAlpha();
					cur -= FADE_OUT_STEP;

					if (cur <= 0) {
						_shell.setAlpha(0);
						if (_oldImage != null) {
							_oldImage.dispose();
						}
						_shell.dispose();
						_activeShells.remove(_shell);
						return;
					}

					_shell.setAlpha(cur);

					Display.getDefault().timerExec(FADE_TIMER, this);

				} catch (Exception err) {
					err.printStackTrace();
				}
			}

		};
		Display.getDefault().timerExec(FADE_TIMER, run);

	}

}
