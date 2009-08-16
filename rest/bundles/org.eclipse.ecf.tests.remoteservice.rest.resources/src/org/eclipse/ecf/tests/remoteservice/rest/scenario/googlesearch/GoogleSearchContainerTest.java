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

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.rest.RestContainer;
import org.eclipse.ecf.remoteservice.rest.RestService;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.rest.util.RestRemoteCall;

public class GoogleSearchContainerTest extends TestCase {
	
	private static final String URL_GOOGLESEARCH = "http://ajax.googleapis.com";

	public void testSearch() {
		ID restId = IDFactory.getDefault().createID(RestNamespace.NAME, URL_GOOGLESEARCH);
		RestContainer container = new GoogleSearchContainer(restId, "eclipse");
		IRemoteService remoteService = container.getRemoteService(RestService.class.getName());
		try {
			remoteService.callSync(new RestRemoteCall("search"));
			ISearch search = (ISearch) remoteService.getProxy();
			ISearchResult[] results = search.getResults();
			assertNotNull(results);
			boolean eclipseOrgFound = false;
			for (int i = 0; i < results.length && !eclipseOrgFound; i++) {
				ISearchResult result = results[i];
				URL eclipseOrg = new URL("http://www.eclipse.org/");
				if(result.getURL().equals(eclipseOrg))
					eclipseOrgFound = true;
			}
			assertTrue(eclipseOrgFound);
		} catch (ECFException e) {
			fail();;
		} catch (MalformedURLException e) {
			fail();
		}
	}

}
