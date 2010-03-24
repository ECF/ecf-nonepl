/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.zookeeper.DiscoveryActivator;
import org.eclipse.ecf.provider.zookeeper.core.DefaultDiscoveryConfig;
import org.eclipse.ecf.provider.zookeeper.core.DiscoveryContainer.FLAVOR;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public class Configurator extends DefaultDiscoveryConfig {

	private File zooConfFile;
	private File bundleRoot;
	private File zookeeperData;
	private ServiceReference reference;
	private List<String> serverIps = new ArrayList<String>();
	private FLAVOR flavor;

	public void configure(ID targetId) {
		Assert.isNotNull(targetId);
		configure(targetId.getName());
	}

	public void configure(ServiceReference reference) {
		Assert.isNotNull(reference);
		Set<String> legalKeys = getProperties().keySet();
		for (String key : reference.getPropertyKeys()) {
			if (legalKeys.contains(key) || key.startsWith("zoodiscovery"))
				getProperties().put(key, reference.getProperty(key));
		}
		init();
	}

	public void configure(String propsAsString) {
		String ss[] = propsAsString.split(",");//$NON-NLS-1$
		for (String s : ss) {
			String key_value[] = s.split("=");//$NON-NLS-1$
			getProperties().put(key_value[0], key_value[1]);
		}
		init();
	}

	private void init() {
		PrintWriter writer = null;
		boolean isNewZookeeperData = false;
		try {
			this.bundleRoot = FileLocator.getBundleFile(DiscoveryActivator
					.getContext().getBundle());
			// TODO consider creating zooKeeper configuration files in temp-dir
			setZookeeperData(new File(this.bundleRoot.getParent()
					+ File.separator + DATADIR_DEFAULT));
			isNewZookeeperData = getZookeeperData().mkdir();
			getZookeeperData().deleteOnExit();
			if (!isNewZookeeperData) {
				clean();
			}
			this.zooConfFile = new File(getZookeeperData() + "/zoo.cfg");//$NON-NLS-1$
			this.zooConfFile.createNewFile();
			this.zooConfFile.deleteOnExit();
			Map<String, Object> props = getProperties();
			if (getProperties().containsKey(ZOODISCOVERY_FLAVOR_CENTRALIZED)) {
				this.setFlavor(FLAVOR.CENTRALIZED);
				this.serverIps = parseIps();
				if (this.serverIps.size() != 1) {
					String msg = "Industrial Discovery property "
							+ ZOODISCOVERY_FLAVOR_CENTRALIZED
							+ " must contain exactly one ip address";
					Logger.log(LogService.LOG_ERROR, msg, null);
					throw new ServiceException(msg);

				}

			} else if (getProperties().containsKey(ZOODISCOVERY_FLAVOR_REPLICATED)) {
				this.setFlavor(FLAVOR.REPLICATED);
				this.serverIps = parseIps();
				this.serverIps.remove("localhost"); //$NON-NLS-1$	
				if (!this.serverIps.contains(Geo.getHost())) {
					this.serverIps.add(Geo.getHost());
				}
				if (this.serverIps.size() < 2) {
					String msg = "Industrial Discovery property "//$NON-NLS-1$
							+ ZOODISCOVERY_FLAVOR_REPLICATED
							+ " must contain at least one IP address which is not localhost.";
					Logger.log(LogService.LOG_ERROR, msg, null);
					throw new ServiceException(msg);

				}

			} else if (getProperties().containsKey(ZOODISCOVERY_FLAVOR_STANDALONE)) {
				this.setFlavor(FLAVOR.STANDALONE);
				this.serverIps = parseIps();
				this.serverIps.remove("localhost"); //$NON-NLS-1$						

			}

			props.put(ZOOKEEPER_DATADIR,
			/*
			 * zooKeeper seems not understanding Windows file backslash !!
			 */
			getZookeeperData().getAbsolutePath().replace("\\", "/"));//$NON-NLS-1$ //$NON-NLS-2$
			props.put(ZOOKEEPER_DATALOGDIR, getZookeeperData()
					.getAbsolutePath().replace("\\", "/"));//$NON-NLS-1$ //$NON-NLS-2$
			Collections.sort(this.serverIps);
			if (this.isQuorum()) {
				String myip = Geo.getHost();
				int myId = this.serverIps.indexOf(myip);
				File myIdFile = new File(getZookeeperData() + "/myid");//$NON-NLS-1$
				myIdFile.createNewFile();
				myIdFile.deleteOnExit();
				writer = new PrintWriter(myIdFile);
				writer.print(myId);
				writer.flush();
				writer.close();
			}
			writer = new PrintWriter(this.zooConfFile);
			if (this.isQuorum()) {
				for (int i = 0; i < this.serverIps.size(); i++) {
					writer.println("server."//$NON-NLS-1$
							+ i + "="//$NON-NLS-1$
							+ this.serverIps.get(i) + ":"//$NON-NLS-1$
							+ props.get(ZOOKEEPER_SERVER_PORT) + ":"//$NON-NLS-1$
							+ props.get(ZOOKEEPER_ELECTION_PORT));

				}
			}
			for (String k : props.keySet()) {
				if (k.startsWith("discovery")) {//$NON-NLS-1$
					/*
					 * Ignore properties that are not intended for ZooKeeper
					 * internal configuration
					 */
					continue;
				}
				writer.println(k + "=" + props.get(k));//$NON-NLS-1$
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private List parseIps() {
		return Arrays.asList(((String) getProperties().get(flavor.toString()))
				.split(","));//$NON-NLS-1$

	}

	private void clean() {
		for (File file : this.zookeeperData.listFiles()) {
			try {
				if (file.isDirectory()) {
					for (File f : file.listFiles())
						f.delete();
				}
				file.delete();
			} catch (Throwable t) {
				continue;
			}
		}
	}

	public String getConfFile() {
		return this.zooConfFile.toString();
	}

	public String getServerIps() {
		String ipsString = ""; //$NON-NLS-1$
		for (String i : this.serverIps) {
			ipsString += i + ":" + CLIENT_PORT_DEFAULT + ",";//$NON-NLS-1$ //$NON-NLS-2$
		}
		return ipsString.substring(0, ipsString.lastIndexOf(","));//$NON-NLS-1$
	}

	public List<String> getServerIpsAsList() {
		return this.serverIps;
	}

	public void setZookeeperData(File zookeeperData) {
		this.zookeeperData = zookeeperData;
	}

	public File getZookeeperData() {
		return this.zookeeperData;
	}

	public void setFlavor(FLAVOR flavor) {
		this.flavor = flavor;
	}

	public FLAVOR getFlavor() {
		return this.flavor;
	}

	public boolean isQuorum() {
		return this.flavor == FLAVOR.REPLICATED;
	}

	public boolean isCentralized() {
		return this.flavor == FLAVOR.CENTRALIZED;
	}

	public boolean isStandAlone() {
		return this.flavor == FLAVOR.STANDALONE;
	}

	public ServiceReference getReference() {
		return this.reference;
	}

	public static boolean isValid(String flavorInput) {
		Assert.isNotNull(flavorInput);
		boolean valid = flavorInput.contains("=");//$NON-NLS-1$
		String f = flavorInput.split("=")[0];//$NON-NLS-1$
		valid &= f.equals(ZOODISCOVERY_FLAVOR_CENTRALIZED) || f.equals(ZOODISCOVERY_FLAVOR_REPLICATED)
				|| f.equals(ZOODISCOVERY_FLAVOR_STANDALONE);
		return valid;
	}

	public static void validateFlavor(String f) {
		if (!isValid(f))
			throw new IllegalArgumentException(f);
	}

	//public boolean isAutoPublish() {
		//return Boolean.valueOf(getProperties().get(ZOODISCOVERY_ADVERTISE_AUTO).toString());
	//}
}
