/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.ds;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.scr.Component;
import org.eclipse.ecf.mgmt.framework.BundleId;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.osgi.service.resolver.BundleDescription;

public class ComponentInfo implements IComponentInfo, Serializable {

	private static final long serialVersionUID = 8848557838339851546L;
	
	private long id;
	private String name;
	private int state;
	private BundleId bundleId;
	private String factory;
	private boolean isServiceFactory;
	private String className;
	private boolean isDefaultEnabled;
	private boolean isImmediate;
	private String[] services;
	@SuppressWarnings("rawtypes")
	private Map properties;
	private Reference[] references;
	private boolean isActivated;
	private String activate;
	private boolean isActivateDeclared;
	private String deactivate;
	private boolean isDeactivateDeclared;
	private String modified;
	private String configurationPolicy;
	
	public ComponentInfo(Component component, BundleDescription bd) {
		this.id = component.getId();
		this.name = component.getName();
		this.state = component.getState();
		this.bundleId = new BundleId(bd.getSymbolicName(),bd.getVersion().toString());
		this.factory = component.getFactory();
		this.isServiceFactory = component.isServiceFactory();
		this.className = component.getClassName();
		this.isDefaultEnabled = component.isDefaultEnabled();
		this.isImmediate = component.isImmediate();
		this.services = component.getServices();
		this.properties = convertDictionaryToMap(component.getProperties());
		org.apache.felix.scr.Reference[] cRefs = component.getReferences();
		if (cRefs != null) {
			this.references = new Reference[cRefs.length];
			for(int i=0; i < cRefs.length; i++) {
				references[i] = new Reference(cRefs[i]);
			}
		}
		this.isActivated = (component.getComponentInstance() != null);
		this.activate = component.getActivate();
		this.isActivateDeclared = component.isActivateDeclared();
		this.deactivate = component.getDeactivate();
		this.isDeactivateDeclared = component.isDeactivateDeclared();
		this.modified = component.getModified();
		this.configurationPolicy = component.getConfigurationPolicy();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map convertDictionaryToMap(Dictionary dict) {
		Map result = new Properties();
		for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			result.put(key, dict.get(key));
		}
		return result;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getState() {
		return state;
	}

	public IBundleId getBundle() {
		return bundleId;
	}

	public String getFactory() {
		return factory;
	}

	public boolean isServiceFactory() {
		return isServiceFactory;
	}

	public String getClassName() {
		return className;
	}

	public boolean isDefaultEnabled() {
		return isDefaultEnabled;
	}

	public boolean isImmediate() {
		return isImmediate;
	}

	public String[] getServices() {
		return services;
	}

	@SuppressWarnings("rawtypes")
	public Map getProperties() {
		return properties;
	}

	public IReference[] getReferences() {
		return references;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public String getActivate() {
		return activate;
	}

	public boolean isActivateDeclared() {
		return isActivateDeclared;
	}

	public String getDeactivate() {
		return deactivate;
	}

	public boolean isDeactivateDeclared() {
		return isDeactivateDeclared;
	}

	public String getModified() {
		return modified;
	}

	public String getConfigurationPolicy() {
		return configurationPolicy;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ComponentInfo[id=");
		buffer.append(id);
		buffer.append(", name=");
		buffer.append(name);
		buffer.append(", state=");
		buffer.append(state);
		buffer.append(", bundleId=");
		buffer.append(bundleId);
		buffer.append(", factory=");
		buffer.append(factory);
		buffer.append(", isServiceFactory=");
		buffer.append(isServiceFactory);
		buffer.append(", className=");
		buffer.append(className);
		buffer.append(", isDefaultEnabled=");
		buffer.append(isDefaultEnabled);
		buffer.append(", isImmediate=");
		buffer.append(isImmediate);
		buffer.append(", services=");
		buffer.append(services != null ? Arrays.asList(services) : null);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", references=");
		buffer.append(references != null ? Arrays.asList(references) : null);
		buffer.append(", isActivated=");
		buffer.append(isActivated);
		buffer.append(", activate=");
		buffer.append(activate);
		buffer.append(", isActivateDeclared=");
		buffer.append(isActivateDeclared);
		buffer.append(", deactivate=");
		buffer.append(deactivate);
		buffer.append(", isDeactivateDeclared=");
		buffer.append(isDeactivateDeclared);
		buffer.append(", modified=");
		buffer.append(modified);
		buffer.append(", configurationPolicy=");
		buffer.append(configurationPolicy);
		buffer.append("]");
		return buffer.toString();
	}

	
}
