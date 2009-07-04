/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.remoteservice.rest.resource.rss;

import java.io.InputStream;
import java.text.ParseException;

import org.eclipse.ecf.remoteservice.rest.resource.IRestResource;

public class RssResource implements IRestResource {

	/**
	 * Returns a RssFeed Object.
	 */
	public Object createRepresentation(InputStream inputStream) throws ParseException {
		RssParser parser = new RssParser(inputStream);
		parser.parse();
		return parser.getFeed();
	}

	public String getIdentifier() {
		return "ecf.rest.resource.rss";
	}

}
