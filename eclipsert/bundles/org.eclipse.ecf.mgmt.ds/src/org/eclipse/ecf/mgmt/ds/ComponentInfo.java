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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.scr.Component;
import org.eclipse.ecf.mgmt.framework.BundleId;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.mgmt.framework.IServiceInfo;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.service.component.ComponentInstance;

public class ComponentInfo implements IComponentInfo, Serializable {

	private static final long serialVersionUID = 8848557838339851546L;
	
	private long id;
	private long componentId;
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
	private Properties properties;
	private ReferenceInfo[] referenceInfos;
	private boolean isActivated;
	private String activate;
	private boolean isActivateDeclared;
	private String deactivate;
	private boolean isDeactivateDeclared;
	private String modified;
	private String configurationPolicy;
	private IServiceInfo serviceInstance;
	
	public ComponentInfo(long id, Component component, BundleDescription bd, IServiceInfo serviceInstance) {
		this.id = id;
		this.componentId = component.getId();
		this.name = component.getName();
		this.state = component.getState();
		this.bundleId = new BundleId(bd.getSymbolicName(),bd.getVersion().toString());
		this.factory = component.getFactory();
		this.isServiceFactory = component.isServiceFactory();
		this.className = component.getClassName();
		this.isDefaultEnabled = component.isDefaultEnabled();
		this.isImmediate = component.isImmediate();
		this.services = component.getServices();
		this.properties = convertProperties(component.getProperties());
		org.apache.felix.scr.Reference[] cRefs = component.getReferences();
		if (cRefs != null) {
			this.referenceInfos = new ReferenceInfo[cRefs.length];
			for(int i=0; i < cRefs.length; i++) {
				referenceInfos[i] = new ReferenceInfo(cRefs[i]);
			}
		}
		ComponentInstance componentInstance = component.getComponentInstance();
		this.isActivated = (componentInstance == null)?false:true;
		this.activate = component.getActivate();
		this.isActivateDeclared = component.isActivateDeclared();
		this.deactivate = component.getDeactivate();
		this.isDeactivateDeclared = component.isDeactivateDeclared();
		this.modified = component.getModified();
		this.configurationPolicy = component.getConfigurationPolicy();
		this.serviceInstance = serviceInstance;
	}
	
	private static boolean isSerializable(Object o) {
		try {
			ObjectOutputStream ois = new ObjectOutputStream(
					new ByteArrayOutputStream());
			ois.writeObject(o);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Properties convertProperties(Dictionary dict) {
		Properties result = new Properties();
		for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Object value = dict.get(key);
			if (isSerializable(value))
				result.put(key, value);
			else
				result.put(key, value.toString());
		}
		return result;
	}
	
	public long getId() {
		return id;
	}

	public long getComponentId() {
		return componentId;
	}
	
	public String getName() {
		return name;
	}

	public int getState() {
		return state;
	}

	public IBundleId getBundleId() {
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

	public IReferenceInfo[] getReferences() {
		return referenceInfos;
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

	public IServiceInfo getActiveService() {
		return serviceInstance;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ComponentInfo[id=");
		buffer.append(id);
		buffer.append(", componentid=");
		buffer.append(componentId);
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
		buffer.append(", referenceInfos=");
		buffer.append(referenceInfos != null ? Arrays.asList(referenceInfos) : null);
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
		buffer.append(", serviceInstance=");
		buffer.append(serviceInstance);
		buffer.append("]");
		return buffer.toString();
	}

	
}
