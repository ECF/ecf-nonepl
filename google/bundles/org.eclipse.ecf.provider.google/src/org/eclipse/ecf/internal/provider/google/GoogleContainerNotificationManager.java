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
package org.eclipse.ecf.internal.provider.google;

import java.util.ArrayList;
import org.eclipse.ecf.provider.google.INotificationListener;
import org.eclipse.ecf.provider.google.events.NotificationEvent;

public class GoogleContainerNotificationManager {
	private ArrayList<INotificationListener> notificationListeners = new ArrayList<INotificationListener>();

	public void addNotificationListener(INotificationListener listener) {
		notificationListeners.add(listener);
	}

	public void removeNotificationListener(INotificationListener listener) {
		notificationListeners.remove(listener);
	}

	public void notifyListeners(NotificationEvent event) {
		for (INotificationListener listener : notificationListeners) {
			listener.fireNotificationEvent(event);
		}
	}
}
