/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
 *
 * Siemens Enterprise Communications GmbH & Co. KG is a Trademark Licensee 
 * of Siemens AG.
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Enterprise Communications 
 * GmbH & Co. KG and its licensors. All rights are reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.ecf.tests.osgi.services.discovery.local;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * @author Thomas Kiesslich
 * 
 */
public class DistributedOSGiBasedStaticInformationTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * These services come from the default location of this bundle.
	 */
	public void testReadingFromXMLBundle() {
		String interfaceName1 = "org.eclipse.ecf.pojo.hello.HelloService";
		String interfaceName2 = "org.eclipse.ecf.pojo.hello.GreeterService";
		DiscoveredServiceTrackerImpl discoServiceTracker = new DiscoveredServiceTrackerImpl();
		registerDiscoveredServiceTracker(null, null, null, discoServiceTracker);
		assertEquals(2, discoServiceTracker.getAvailNotifications());
		Iterator /* <ServiceEndpointDescription> */result = discoServiceTracker
				.getAvailableDescriptions().iterator();
		boolean foundInterface1 = false;
		boolean foundInterface2 = false;
		while (result.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) result
					.next();
			if (sed.getProvidedInterfaces().contains(interfaceName1)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("SOAP HTTP", props.get("service.intents"));
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("http://localhost:9000/hello", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface1 = true;
			} else if (sed.getProvidedInterfaces().contains(interfaceName2)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("SOAP HTTP", props.get("service.intents"));
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("http://localhost:9005/greeter", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface2 = true;
			} else {
				fail("a ServiceEndpointDescription found that is not expected");
			}
		}
		assertTrue(foundInterface1);
		assertTrue(foundInterface2);
	}

	/**
	 * This service comes from the default location in bundle
	 * org.eclipse.ecf.tests.provider.discovery.staticinformation.poststarted .
	 */
	public void testGetServicesFromDefaultLocation() {
		String interfaceName1 = "com.siemens.helloworld.HelloWorldService";
		DiscoveredServiceTrackerImpl discoServiceTracker = new DiscoveredServiceTrackerImpl();
		Activator ac = Activator.getDefault();
		assertNotNull(ac);
		registerDiscoveredServiceTracker(interfaceName1, null, null,
				discoServiceTracker);
		// start up post started
		try {
			ac
					.startBundle("org.eclipse.ecf.tests.provider.discovery.staticinformation.poststarted");
		} catch (BundleException e) {
			fail(e.getMessage());
		}
		assertEquals(1, discoServiceTracker.getAvailNotifications());
		Iterator /* <ServiceEndpointDescription> */result = discoServiceTracker
				.getAvailableDescriptions().iterator();
		boolean foundInterface1 = false;
		while (result.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) result
					.next();
			if (sed.getProvidedInterfaces().contains(interfaceName1)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("https://localhost:8080/helloworld", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface1 = true;
			} else {
				fail("a ServiceEndpointDescription found that is not expected");
			}
		}
		assertTrue(foundInterface1);
	}

	/**
	 * This services are all referenced from the Manifest Header entry
	 * remote-service in the bundle
	 * org.eclipse.ecf.tests.provider.discovery.staticinformation.poststarted2 .
	 */
	public void testGetServicesFromManifestDefinedLocations() {
		String interfaceName1 = "com.siemens.hellomoon.HelloMoonService";
		String interfaceName2 = "com.siemens.galileo.HelloGalileoService";
		String interfaceName3 = "com.siemens.ganymede.HelloGanymedeService";
		DiscoveredServiceTrackerImpl discoServiceTracker = new DiscoveredServiceTrackerImpl();
		Activator ac = Activator.getDefault();
		assertNotNull(ac);
		registerDiscoveredServiceTracker(interfaceName1, interfaceName2,
				interfaceName3, discoServiceTracker);
		// start up post started
		try {
			ac
					.startBundle("org.eclipse.ecf.tests.provider.discovery.staticinformation.poststarted2");
		} catch (BundleException e) {
			fail(e.getMessage());
		}
		assertEquals(3, discoServiceTracker.getAvailNotifications());
		Iterator /* <ServiceEndpointDescription> */result = discoServiceTracker
				.getAvailableDescriptions().iterator();
		boolean foundInterface1 = false;
		boolean foundInterface2 = false;
		boolean foundInterface3 = false;
		while (result.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) result
					.next();
			if (sed.getProvidedInterfaces().contains(interfaceName1)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("jsoc://moon:4711/hellomoon", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface1 = true;
			} else if (sed.getProvidedInterfaces().contains(interfaceName2)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("jssoc://galileo:4712/hellogalileo", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface2 = true;
			} else if (sed.getProvidedInterfaces().contains(interfaceName3)) {
				Map props = sed.getProperties();
				assertNotNull(props);
				assertEquals("pojo", props
						.get("osgi.remote.configuration.type"));
				assertEquals("jssoc://ganymede:4713/helloganymede", props
						.get("osgi.remote.configuration.pojo.address"));
				foundInterface3 = true;
			} else {
				fail("a ServiceEndpointDescription found that is not expected");
			}
		}
		assertTrue(foundInterface1);
		assertTrue(foundInterface2);
		assertTrue(foundInterface3);
	}

	/**
	 * @param interfaceName1
	 * @param interfaceName2
	 * @param discoServiceTracker
	 */
	private void registerDiscoveredServiceTracker(String interfaceName1,
			String interfaceName2, String interfaceName3,
			DiscoveredServiceTrackerImpl discoServiceTracker) {
		Activator ac = Activator.getDefault();
		assertNotNull(ac);
		BundleContext bc = ac.getBundleContext();

		Dictionary properties = new Hashtable();
		if (interfaceName1 != null) {
			List interfaces = new ArrayList();
			interfaces.add(interfaceName1);
			if (interfaceName2 != null) {
				interfaces.add(interfaceName2);
			}
			if (interfaceName3 != null) {
				interfaces.add(interfaceName3);
			}
			properties
					.put(
							DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES,
							interfaces);
		}
		bc.registerService(DiscoveredServiceTracker.class.getName(),
				discoServiceTracker, properties);
	}
}
