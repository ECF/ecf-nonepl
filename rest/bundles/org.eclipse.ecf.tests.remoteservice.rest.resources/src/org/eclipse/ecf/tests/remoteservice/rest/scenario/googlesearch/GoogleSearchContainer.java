/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.tests.remoteservice.rest.scenario.googlesearch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.rest.IRestCall;
import org.eclipse.ecf.remoteservice.rest.IRestResponseProcessor;
import org.eclipse.ecf.remoteservice.rest.RestContainer;
import org.eclipse.ecf.remoteservice.rest.util.GetRestCall;

public class GoogleSearchContainer extends RestContainer {
	
	public GoogleSearchContainer(ID id, String query) {
		super(id);
		Map searchMethods = new HashMap();
		Object[] params = new Object[] {"v=1.0", "q=" + query };		
		IRestCall call;
		try {
			call = new GetRestCall(new URI("/ajax/services/search/web"), "ecf.rest.resource.json.org", params, null, 10000);
			searchMethods.put("search", call);
			registerRestService(new String[]{ISearch.class.getName(), IRestResponseProcessor.class.getName()}, new SearchService(), searchMethods, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ECFException e) {
			e.printStackTrace();
		}		
	}

}
