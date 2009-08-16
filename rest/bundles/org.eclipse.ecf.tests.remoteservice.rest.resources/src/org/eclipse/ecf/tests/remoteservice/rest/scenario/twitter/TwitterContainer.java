/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.tests.remoteservice.rest.scenario.twitter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.rest.RestContainer;
import org.eclipse.ecf.remoteservice.rest.util.GetRestCall;
import org.eclipse.ecf.tests.remoteservice.rest.twitter.ITwitter;

public class TwitterContainer extends RestContainer {
	
	public TwitterContainer(ID restId) {
		super(restId);
		Map twitterMethods = new HashMap();
		try {
			twitterMethods.put("getUserTimeline", new GetRestCall(new URI("/statuses/user_timeline.json"), "ecf.rest.resource.json.org", new Object[]{"count=2"}, null, 10000));
			registerRestService(new String[] { ITwitter.class.getName() }, new TwitterService(), twitterMethods, null);			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ECFException e) {
			e.printStackTrace();
		}
	}

}
