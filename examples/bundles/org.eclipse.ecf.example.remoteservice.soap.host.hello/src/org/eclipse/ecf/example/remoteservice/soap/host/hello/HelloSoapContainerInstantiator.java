/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.example.remoteservice.soap.host.hello;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.soap.host.identity.SoapID;
import org.eclipse.ecf.remoteservice.soap.host.identity.SoapNamespace;

public class HelloSoapContainerInstantiator extends BaseContainerInstantiator {


	private static final String CONFIG_TYPE = "org.eclipse.ecf.remoteservice.soap.host.hello"; //$NON-NLS-1$

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {

		SoapID soapID = null;
		if (parameters != null && parameters[0] instanceof SoapID)
			soapID = (SoapID) parameters[0];
		else if (parameters == null || parameters.length == 0)
			soapID = (SoapID) IDFactory.getDefault().createID(SoapNamespace.NAME, "ecf.soap.ws.namespace");
		else
			soapID = (SoapID) IDFactory.getDefault().createID(
					SoapNamespace.NAME, parameters);
		try {
			return new HelloSoapContainer(soapID);
		} catch (ECFException e) {
			throw new ContainerCreateException(e);
		}

	}

	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return getInterfacesAndAdaptersForClass(HelloSoapContainer.class);
	}



	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		SoapNamespace restNamespace = (SoapNamespace) IDFactory.getDefault()
				.getNamespaceByName(SoapNamespace.NAME);
		return restNamespace.getSupportedParameterTypes();
	}

	public String[] getImportedConfigs(ContainerTypeDescription description,
			String[] exporterSupportedConfigs) {
		if (CONFIG_TYPE.equals(description.getName())) {
			List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
			if (supportedConfigs.contains(CONFIG_TYPE))
				return new String[] { CONFIG_TYPE };
		}
		return null;
	}

	public Dictionary getPropertiesForImportedConfigs(
			ContainerTypeDescription description, String[] importedConfigs,
			Dictionary exportedProperties) {
		return null;
	}

	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return new String[] { CONFIG_TYPE };
	}

}
