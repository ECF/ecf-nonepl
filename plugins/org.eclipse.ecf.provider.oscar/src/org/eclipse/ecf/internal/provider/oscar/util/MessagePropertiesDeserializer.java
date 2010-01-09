/*******************************************************************************
 * Copyright (c) 2009-2010 Pavel Samolisov and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.oscar.util;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.util.Base64;

/**
 * Deserialize message from follows format:<br><code>
 * message//--ecf#hash---//key1//--ecf--//value1//--ecf--//...
 * </code>
 * to message and properties map
 */
public class MessagePropertiesDeserializer {

	public static final String SEPARATOR = "//---ecf---//"; //$NON-NLS-1$

	public static final String EMPTY = ""; //$NON-NLS-1$

	public static Message deserialize(String str) {
		if (str == null || str.length() < 1)
			return new Message(EMPTY, new HashMap());

		String[] array = str.split(SEPARATOR);
		if (array.length == 1)
			return new Message(array[0], new HashMap());

		Map properties = new HashMap();
		for (int i = 1; i < array.length; i += 2)
			properties.put(deserializeObject(array[i]), deserializeObject(array[i + 1]));

		return new Message(array[0], properties);
	}

	private static Object deserializeObject(String obj) {
		if (obj == null)
			return null;

		try {
			ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(obj)));
			return stream.readObject();
		} catch (Exception e) {
			// if could not read object - return String as object
			return obj;
		}
	}

	public static class Message {
		private String message;
		private Map properties;

		public Message(String message, Map properties) {
			this.message = message;
			this.properties = properties;
		}

		public String getMessage() {
			return message;
		}

		public Map getProperties() {
			return properties;
		}
	}
}
