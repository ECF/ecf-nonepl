/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.internal.remoteservice.soap.host;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.AxisEngine;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.xml.sax.SAXException;

/**
 * This class is responsible to deploy the remote ECF service as a webservice on
 * axis engine, which handles common functionality like dealing with the
 * service registries.
 * 
 * @since 3.4
 * 
 */
public class SOAPRemoteService {

	private AxisEngine engine = null;
	private String serviceName = null;
	private IRemoteService remoteService = null;
	private String allowedMethods = null;

	/**
	 * 
	 * @param engine which will deploy the service as a webservice. Must not be <code>null</code>.
	 * @param serviceName the name to locate the remote service.  Must not be <code>null</code>.
	 * @param remoteService.  Must not be <code>null</code>.
	 * @param allowedMethods.  Must not be <code>null</code>.
	 */
	public SOAPRemoteService(AxisEngine engine, String serviceName,
			IRemoteService remoteService, String allowedMethods) {

		Assert.isNotNull(engine);
		Assert.isNotNull(serviceName);
		Assert.isNotNull(remoteService);
		Assert.isNotNull(allowedMethods);
		
		this.remoteService = remoteService;
		this.serviceName = serviceName;
		this.engine = engine;
		this.allowedMethods = allowedMethods;
	}

	/**
	 * This consider allowedMethods as "*"
	 * @param engine which will deploy the service as a webservice. Must not be <code>null</code>.
	 * @param serviceName the name to locate the remote service.  Must not be <code>null</code>.
	 * @param remoteService.  Must not be <code>null</code>.
	 * 
	 */
	public SOAPRemoteService(AxisServer engine, String clazz, IRemoteService remoteService) {
		this(engine, clazz, remoteService, "*");
	}

	/**
	 * Deploy the {@link IRemoteService} passed on constructor as a webservice
	 * @throws ECFException
	 */
	public void deployService() throws ECFException {
		engine.getClassCache().registerClass(serviceName,
				remoteService.getProxy().getClass());

		Object service = engine.getApplicationSession().get(serviceName);

		if (service == null) {
			deployWSDD(deploymentDescriptor());
			engine.getApplicationSession().set(serviceName,
					remoteService.getProxy());
		}
	}

	/**
	 * Undeploy the {@link IRemoteService} passed on constructor as a webservice
	 * @throws ECFException
	 */
	public void undeployService() throws ECFException {
		Object service = engine.getApplicationSession().get(serviceName);

		if (service != null) {
			deployWSDD(undeployDescriptor());
			engine.getApplicationSession().remove(serviceName);
		}
	}

	private String deploymentDescriptor() {
		return "<deployment"
				+ " xmlns=\"http://xml.apache.org/axis/wsdd/\"\n"
				+ " xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"\n"
				+ ">\n" + " <service name     = \"" + serviceName + "\"\n"
				+ "          provider = \"java:RPC\">\n"
				+ "   <parameter name  = \"allowedMethods\"\n"
				+ "              value = \"" + allowedMethods + "\"/>\n"
				+ "   <parameter name  = \"className\"\n"
				+ "              value=\"" + serviceName + "\"/>\n"
				+ "   <parameter name=\"scope\"\n"
				+ "              value=\"Application\"/>\n" + " </service>\n"
				+ "</deployment>";
	}

	private void deployWSDD(String descriptor) throws ECFException {
		try {
			WSDDEngineConfiguration cfg = (WSDDEngineConfiguration) engine
					.getConfig();
			WSDDDeployment wsddDeployment = cfg.getDeployment();
			WSDDDocument wsddDocument = new WSDDDocument(
					XMLUtils.newDocument(new ByteArrayInputStream(descriptor
							.getBytes())));
			wsddDocument.deploy(wsddDeployment);
			engine.refreshGlobalOptions();

		} catch (WSDDException e) {
			throw new ECFException(Messages.AbstractSoapContainer_WEBSERVICE_DEPLOY, e);
		} catch (ParserConfigurationException e) {
			throw new ECFException(Messages.AbstractSoapContainer_WEBSERVICE_DEPLOY, e);
		} catch (SAXException e) {
			throw new ECFException(Messages.AbstractSoapContainer_WEBSERVICE_DEPLOY, e);
		} catch (IOException e) {
			throw new ECFException(Messages.AbstractSoapContainer_WEBSERVICE_DEPLOY, e);
		}

	}

	private String undeployDescriptor() {
		return "<undeployment" + " xmlns=\"http://xml.apache.org/axis/wsdd/\">"
				+ "<service name=\"" + serviceName + "\"/>" + "</undeployment>";
	}

}