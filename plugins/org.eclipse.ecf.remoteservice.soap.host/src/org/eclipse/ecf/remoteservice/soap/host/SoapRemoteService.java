/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/

package org.eclipse.ecf.remoteservice.soap.host;

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
import org.eclipse.ecf.internal.remoteservice.soap.host.Messages;
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
public class SoapRemoteService {

	private AxisEngine engine = null;
	private String serviceName = null;
	private Object remoteService = null;
	private String allowedMethods = null;
	private IServiceDescription description = null;;

	/**
	 * 
	 * @param engine which will deploy the service as a webservice. Must not be <code>null</code>.
	 * @param serviceName the name to locate the remote service.  Must not be <code>null</code>.
	 * @param remoteService.  Must not be <code>null</code>.
	 * @param allowedMethods.  Must not be <code>null</code>.
	 */
	public SoapRemoteService(AxisEngine engine, String serviceName,	Object remoteService, String allowedMethods) {

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
	public SoapRemoteService(AxisServer engine, String serviceName, Object remoteService) {
		this(engine, serviceName, remoteService, "*");
	}

	/**
	 * The service descriptor will be generated based on the IServiceDescription
	 * @param engine which will deploy the service as a webservice. Must not be <code>null</code>.
	 * @param description contains the properties description for the service.  Must not be <code>null</code>.
	 * @param remoteService.  Must not be <code>null</code>.
	 * 
	 */
	public SoapRemoteService(AxisServer engine,	IServiceDescription description, Object remoteService) {
		Assert.isNotNull(engine);
		Assert.isNotNull(description);
		Assert.isNotNull(remoteService);
		
		this.engine = engine;
		this.description = description;
		this.remoteService = remoteService;		

	}

	/**
	 * Deploy the {@link IRemoteService} passed on constructor as a webservice
	 * @throws ECFException
	 */
	public void deployService() throws ECFException {

		String desc = null;
		
		if(description == null){			
			desc = deploymentDefaultDescriptor();
		}else{
			serviceName = (String) description.getProperty(ISoapServerConstants.SERVICE_NAME);
			desc = deploymentCustomDescriptor(description);
		}

		engine.getClassCache().registerClass(serviceName, remoteService.getClass());

		Object service = engine.getApplicationSession().get(serviceName);

		if (service == null) {
			deployWSDD(desc);
			
			engine.getApplicationSession().set(serviceName,	remoteService);
		}
	}

	private String deploymentCustomDescriptor(IServiceDescription description) {

		String serviceName = (String) description.getProperty(ISoapServerConstants.SERVICE_NAME);
		String allowedMethods = (String) description.getProperty(ISoapServerConstants.ALLOWED_METHODS);//*
		String provider = (String) description.getProperty(ISoapServerConstants.PROVIDER);//ex.: java:RPC
		String scope = (String) description.getProperty(ISoapServerConstants.SCOPE);//ex.: Application
		//TODO add others elements to the wsdd
		String desc = "<deployment"
		+ " xmlns=\"http://xml.apache.org/axis/wsdd/\"\n"
		+ " xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"\n"
		+ ">\n" + " <service name     = \"" + serviceName + "\"\n"
		+ "          provider = \"" + provider + "\">\n"
		+ "   <parameter name  = \"allowedMethods\"\n"
		+ "              value = \"" + allowedMethods + "\"/>\n"
		+ "   <parameter name  = \"className\"\n"
		+ "              value=\"" + serviceName + "\"/>\n"
		+ "   <parameter name=\"scope\"\n"
		+ "              value=\"" +scope+"\"/>\n" + " </service>\n"
		+ "</deployment>";
		
		return desc;
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

	private String deploymentDefaultDescriptor() {
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
