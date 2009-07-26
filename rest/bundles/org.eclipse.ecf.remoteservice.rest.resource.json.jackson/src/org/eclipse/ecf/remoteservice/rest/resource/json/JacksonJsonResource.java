/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.remoteservice.rest.resource.json;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.eclipse.ecf.remoteservice.rest.resource.IRestResource;

public class JacksonJsonResource implements IRestResource {

	public String getIdentifier() {
		return "ecf.rest.resource.json.jackson";
	}

	/**
	 * Parses the string from the response body to a JSONObject.
	 */
	public Object createRepresentation(String responseBody) throws ParseException {
		String parseErrorMsg = "Could not parse JSON Response";	 
		JsonFactory factory = new JsonFactory();
		try {
			JsonParser parser = factory.createJsonParser(new StringReader(responseBody));
			return parser;
		} catch (JsonParseException e) {
			throw new ParseException(parseErrorMsg, 0);
		} catch (IOException e) {
			throw new ParseException(parseErrorMsg, 0);
		}
	}

}
