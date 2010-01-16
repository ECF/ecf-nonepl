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

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.soap.client.AbstractSoapClientService;

import com.aonaware.services.webservices.Definition;
import com.aonaware.services.webservices.DictServiceLocator;
import com.aonaware.services.webservices.WordDefinition;

public class DictionarySoapClientService extends AbstractSoapClientService {

	public DictionarySoapClientService(AbstractClientContainer container,
			RemoteServiceClientRegistration registration) {
		super(container, registration);
	}

	protected Object invokeRemoteCall(IRemoteCall call, IRemoteCallable callable)
			throws ECFException {

		if ("define".equals(callable.getMethod())) {
			// Setup and make remote call via axis client
			try {
				// Now make blocking remote call
				com.aonaware.services.webservices.WordDefinition wordDefinition = new DictServiceLocator()
						.getDictServiceSoap12().define((String) call.getParameters()[0]);
				// convert results and return
				return convertWordDefinition(wordDefinition);
			} catch (ServiceException e) {
				handleInvokeException("Exception setting up SOAP call", e);
			} catch (RemoteException e) {
				handleInvokeException("Exception setting up SOAP call", e);
			}
		} else
			throw new ECFException("invalid method");
		// can't happen
		return null;
	}

	private org.eclipse.ecf.examples.remoteservices.dictionary.common.WordDefinition convertWordDefinition(
			WordDefinition wordDefinition) {
		String word = wordDefinition.getWord();
		Definition[] definitions = wordDefinition.getDefinitions();
		if (definitions != null) {
			String[] defs = new String[definitions.length];
			for (int i = 0; i < definitions.length; i++) {
				defs[i] = definitions[i].getWordDefinition();
			}
			return new org.eclipse.ecf.examples.remoteservices.dictionary.common.WordDefinition(word,defs);
		} else
			return null;
	}

}
