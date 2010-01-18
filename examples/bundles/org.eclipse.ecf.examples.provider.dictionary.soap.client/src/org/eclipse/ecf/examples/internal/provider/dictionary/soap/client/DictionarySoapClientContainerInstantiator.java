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

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.remoteservice.soap.identity.SoapID;
import org.eclipse.ecf.remoteservice.soap.identity.SoapNamespace;

public class DictionarySoapClientContainerInstantiator extends
		BaseContainerInstantiator {

	protected static final String[] intents = {"passByValue", "exactlyOnce", "ordered",}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String CONFIG_TYPE = "ecf.examples.dictionary.soap.client"; //$NON-NLS-1$

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			SoapID soapID = null;
			if (parameters != null && parameters[0] instanceof SoapID)
				soapID = (SoapID) parameters[0];
			else if (parameters == null || parameters.length == 0) soapID = (SoapID) IDFactory.getDefault().createID(SoapNamespace.NAME, "http://services.aonaware.com/DictService");
			else soapID = (SoapID) IDFactory.getDefault().createID(SoapNamespace.NAME, parameters);
			return new DictionarySoapClientContainer(soapID);
		} catch (Exception e) {
			throw new ContainerCreateException("Could not create RestClientContainer", e); //$NON-NLS-1$
		}
	}
	
	public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
		return getInterfacesAndAdaptersForClass(DictionarySoapClientContainer.class);
	}

	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return intents;
	}

	public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
		SoapNamespace restNamespace = (SoapNamespace) IDFactory.getDefault().getNamespaceByName(SoapNamespace.NAME);
		return restNamespace.getSupportedParameterTypes();
	}

	public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
		if (CONFIG_TYPE.equals(description.getName())) {
			List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
			if (supportedConfigs.contains(CONFIG_TYPE))
				return new String[] {CONFIG_TYPE};
		}
		return null;
	}

	public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
		return null;
	}

	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return new String[] {CONFIG_TYPE};
	}


}
