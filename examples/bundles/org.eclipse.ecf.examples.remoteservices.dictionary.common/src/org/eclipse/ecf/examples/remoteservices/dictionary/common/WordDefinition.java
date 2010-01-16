/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.remoteservices.dictionary.common;

import java.util.Arrays;

public class WordDefinition {
 
	private String word;
	private String[] definitions;

	public WordDefinition(String word, String[] definitions) {
		this.word = word;
		this.definitions = definitions;
	}
	
	public WordDefinition(String word, String definitions) {
		this(word, new String[] { definitions });
	}
	
	public String getWord() {
		return word;
	}
	
	public String[] getDefinitions() {
		return definitions;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WordDefinition[definitions=");
		builder.append(Arrays.toString(definitions));
		builder.append(", word=");
		builder.append(word);
		builder.append("]");
		return builder.toString();
	}
	
}
