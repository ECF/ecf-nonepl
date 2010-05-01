/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.identity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.internal.wave.google.Activator;
import org.eclipse.ecf.provider.internal.wave.google.Messages;

public class WaveBackendNamespace extends Namespace {

	private static final long serialVersionUID = 5628390764537972030L;

	public static final String SCHEME = "wavebackend";

	public static final String NAME = "ecf.namespace.googlewave.wavebackend";

	private static final String DEFAULT_GOOGLE_WAVE_PORT = "9876";

	private static Pattern emailPattern = Pattern.compile(".+@.+.[a-z]+"); //$NON-NLS-1$ 

	private static Pattern serverPattern = Pattern.compile(".[^:]+(:[0-9]{2,5})?"); //$NON-NLS-1$

	public ID createInstance(Object[] parameters) throws IDCreateException {
		Assert.isNotNull(parameters, Messages.WaveBackendNamespace_ParameterIsNull);

		if(!(parameters[0] instanceof String)) {
			invalidParameterException(Messages.WaveBackendNamespace_InvalidParameter);
		}

		String userAtDomain = getUserAtDomain(parameters);

		String[] server = getServerString(parameters).split(":");

		return new WaveBackendID(this, userAtDomain, server[0], Integer.parseInt(server[1]));
	}
	
	private String getServerString(Object[] parameters) throws IDCreateException {
		String server;

		if(parameters.length > 1 && parameters[1] instanceof String) {
			server = (String) parameters[1];
		} else {
			String userAtDomain = (String) parameters[0];
			server = userAtDomain.substring(userAtDomain.indexOf("@") + 1);
		}

		validateServerString(server);

		if(server.indexOf(":") == -1) {
			server += ":" + DEFAULT_GOOGLE_WAVE_PORT;
		}

		return server;
	}

	private void validateServerString(String server) {
		if(server.equals("")) {
			invalidParameterException(Messages.WaveBackendNamespace_InvalidServerParameter);
		}

		Matcher matcher = serverPattern.matcher(server);
		if(!matcher.matches()) {
			invalidParameterException(Messages.WaveBackendNamespace_InvalidServerParameter);
		}
	}

	private String getUserAtDomain(Object[] parameters) throws IDCreateException {
		String userAtDomain = (String) parameters[0];

		if(userAtDomain.equals("")) {
			invalidParameterException(Messages.WaveBackendNamespace_InvalidEmailParameter);
		}

		Matcher matcher = emailPattern.matcher(userAtDomain);
		if(!matcher.matches()) {
			invalidParameterException(Messages.WaveBackendNamespace_InvalidEmailParameter);
		}

		return userAtDomain;
	}

	private void invalidParameterException(String message) {
		Status status = new Status(Status.ERROR, Activator.PLUGIN_ID, message);
		throw new IDCreateException(status);
	}

	public String getScheme() {
		return SCHEME;
	}
}
