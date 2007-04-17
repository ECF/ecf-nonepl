/*******************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.presence.bot.kosmos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class JavadocAnalyzer {

	private final Map javadocs = new HashMap();

	JavadocAnalyzer() {
		try {
			initialize();
		} catch (IOException e) {
			// ignored
			e.printStackTrace(System.err);
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
	}

	private void initialize() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				JavadocAnalyzer.class.getResourceAsStream("javadoc.txt")));
		String input = reader.readLine();
		while (input != null) {
			String className = input.substring(input.lastIndexOf('.') + 1);
			Object o = javadocs.get(className);
			Javadoc doc = new Javadoc(javadocs, input);
			if (o != null) {
				if (o instanceof Javadoc) {
					Javadoc[] docs = new Javadoc[2];
					docs[0] = (Javadoc) o;
					docs[1] = doc;
					javadocs.put(className, docs);
				} else {
					Javadoc[] docs = (Javadoc[]) o;
					Javadoc[] copy = new Javadoc[docs.length + 1];
					System.arraycopy(docs, 0, copy, 0, docs.length);
					copy[docs.length] = doc;
					javadocs.put(className, copy);
				}
			} else {
				javadocs.put(className, doc);
			}
			javadocs.put(input, doc);
			input = reader.readLine();
		}
		reader.close();
	}

	String getJavadocs(String className) {
		Object docs = javadocs.get(className);
		if (docs == null) {
			return "No javadocs found for " + className;
		} else if (docs instanceof Javadoc) {
			return ((Javadoc) docs).getDefault();
		} else {
			Javadoc[] array = (Javadoc[]) docs;
			String reply = "";
			for (int i = 0; i < array.length; i++) {
				reply += array[i].getDefault() + " ";
			}
			reply = reply.substring(0, reply.length() - 1);
			return reply;
		}
	}

	String getJavadocs(String className, String field) {
		Object docs = javadocs.get(className);
		if (docs == null) {
			return "No javadocs found for " + className;
		} else if (docs instanceof Javadoc) {
			return ((Javadoc) docs).getField(field);
		} else {
			Javadoc[] array = (Javadoc[]) docs;
			String reply = "";
			for (int i = 0; i < array.length; i++) {
				reply += array[i].getField(field) + " ";
			}
			reply = reply.substring(0, reply.length() - 1);
			return reply;
		}
	}

	String getJavadocs(String className, String methodName, String[] parameters) {
		Object docs = javadocs.get(className);
		if (docs == null) {
			return "No javadocs found for " + className;
		} else if (docs instanceof Javadoc) {
			String javadocs = ((Javadoc) docs)
					.getMethod(methodName, parameters);
			if (javadocs == null) {
				return "The request could not be processed.";
			} else {
				return javadocs;
			}
		} else {
			Javadoc[] array = (Javadoc[]) docs;
			String reply = "";
			for (int i = 0; i < array.length; i++) {
				String ret = array[i].getMethod(methodName, parameters);
				if (ret != null) {
					reply = reply + ret + " ";
				}
			}
			reply = reply.substring(0, reply.length() - 1);
			return reply;
		}
	}
}
