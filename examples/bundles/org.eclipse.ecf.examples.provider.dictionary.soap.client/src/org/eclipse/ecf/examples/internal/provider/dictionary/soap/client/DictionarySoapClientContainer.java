/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.internal.provider.dictionary.soap.client;

import org.eclipse.ecf.examples.remoteservices.dictionary.common.IDictionary;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.soap.client.AbstractSoapClientContainer;
import org.eclipse.ecf.remoteservice.soap.client.SoapCallableFactory;
import org.eclipse.ecf.remoteservice.soap.identity.SoapID;

public class DictionarySoapClientContainer extends AbstractSoapClientContainer {

	public DictionarySoapClientContainer(SoapID containerID) {
		super(containerID);
		// Create a callable that has the single 'define' method
		IRemoteCallable[][] callables = new IRemoteCallable[][] { { SoapCallableFactory.createCallable("define") } };
		// Register it
		registerCallables(new String[] { IDictionary.class.getName() }, callables, null);
	}

	protected IRemoteService createRemoteService(
			RemoteServiceClientRegistration registration) {
		// Return our service
		return new DictionarySoapClientService(this, registration);
	}

}
