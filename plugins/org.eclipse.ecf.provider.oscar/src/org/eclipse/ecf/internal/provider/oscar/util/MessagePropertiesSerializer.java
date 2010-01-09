/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.util;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.ecf.core.util.Base64;

/**
 * If message has not null properties map then serialize message and properties to
 * follows format:<br><code>
 * message//---ecf---//key1//---ecf---//value1//---ecf---//...
 * </code>
 */
public class MessagePropertiesSerializer {

	public static final String SEPARATOR = "//---ecf---//"; //$NON-NLS-1$

	public static final String EMPTY = ""; //$NON-NLS-1$

	public static String serialize(String message, Map properties) {
		StringBuilder result = new StringBuilder(message == null ? EMPTY : message);
		if (properties != null) {
			for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
				Entry entry = (Entry) iter.next();
				result.append(SEPARATOR);
				result.append(serializeObject(entry.getKey()));
				result.append(SEPARATOR);
				result.append(serializeObject(entry.getValue()));
			}
		}

		return result.toString();
	}

	private static String serializeObject(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof String)
			return (String) obj;

		if (obj instanceof Serializable) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream stream = new ObjectOutputStream(baos);
				stream.writeObject(obj);
				return Base64.encode(baos.toByteArray());
			} catch (IOException e) {
				return EMPTY;
			}
		}

		return obj.toString();
	}
}
