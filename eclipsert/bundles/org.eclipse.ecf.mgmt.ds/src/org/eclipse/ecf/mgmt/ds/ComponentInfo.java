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
	private Map properties;
	private ReferenceInfo[] referenceInfos;
	private boolean isActivated;
	private String activate;
	private boolean isActivateDeclared;
	private String deactivate;
	private boolean isDeactivateDeclared;
	private String modified;
	private String configurationPolicy;
	private IServiceInfo[] serviceInstances;
	
	public ComponentInfo(long id, Component component, BundleDescription bd, IServiceInfo[] serviceInstances) {
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
		this.properties = convertDictionaryToMap(component.getProperties());
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
		this.serviceInstances = serviceInstances;
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

	public long getComponentId() {
		return componentId;
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

	public IServiceInfo[] getServiceInstances() {
		return serviceInstances;
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
		buffer.append(", serviceInstances=");
		buffer.append(serviceInstances != null ? Arrays.asList(serviceInstances) : null);
		buffer.append("]");
		return buffer.toString();
	}

	
}
