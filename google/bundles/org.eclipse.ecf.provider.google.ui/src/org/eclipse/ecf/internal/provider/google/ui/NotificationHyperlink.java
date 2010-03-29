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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class NotificationHyperlink extends ImageHyperlink {

	private boolean strikeThrough;

	protected final MouseTrackListener MOUSE_TRACK_LISTENER = new MouseTrackListener() {

		public void mouseEnter(MouseEvent e) {
			setUnderlined(true);
		}

		public void mouseExit(MouseEvent e) {
			setUnderlined(false);
		}

		public void mouseHover(MouseEvent e) {
		}
	};

	@Override
	public void dispose() {
		removeMouseTrackListener(MOUSE_TRACK_LISTENER);
		super.dispose();
	}

	public boolean isStrikeThrough() {
		return strikeThrough;
	}

	@Override
	protected void paintText(GC gc, Rectangle bounds) {
		super.paintText(gc, bounds);
		if (strikeThrough) {
			Point totalSize = computeTextSize(SWT.DEFAULT, SWT.DEFAULT);
			int textWidth = Math.min(bounds.width, totalSize.x);
			int textHeight = totalSize.y;

			// int descent = gc.getFontMetrics().getDescent();
			int lineY = bounds.y + (textHeight / 2); // - descent + 1;
			gc.drawLine(bounds.x, lineY, bounds.x + textWidth, lineY);
		}
	}

	public void setStrikeThrough(boolean strikethrough) {
		this.strikeThrough = strikethrough;
	}

	@Override
	protected String shortenText(GC gc, String t, int width) {
		if (t == null) {
			return null;
		}

		if ((getStyle() & SWT.SHORT) != 0) {
			return t;
		}

		String returnText = t;
		if (gc.textExtent(t).x > width) {
			for (int i = t.length(); i > 0; i--) {
				String test = t.substring(0, i);
				test = test + "..."; //$NON-NLS-1$
				if (gc.textExtent(test).x < width) {
					returnText = test;
					break;
				}
			}
		}
		return returnText;
	}

	public NotificationHyperlink(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

}
