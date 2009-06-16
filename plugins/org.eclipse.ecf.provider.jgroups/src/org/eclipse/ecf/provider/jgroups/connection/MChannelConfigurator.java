package org.eclipse.ecf.provider.jgroups.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecf.internal.provider.jgroups.Activator;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.conf.ConfiguratorFactory;
import org.jgroups.conf.XmlConfigurator;
import org.jgroups.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import urv.conf.PropertiesLoader;
import urv.machannel.MChannel;
import urv.machannel.MChannelImpl;
import urv.olsr.mcast.MulticastAddress;

public class MChannelConfigurator implements IChannelConfigurator {

	private Log log = LogFactory.getLog(getClass());

	private String stackName = "manet";
	// CONSTANTS --

	public static final String OMOLSR_PROTOCOL = "OMOLSR";
	public static final String SMCAST_PROTOCOL = "SMCAST";

	private final static String PROTOCOL_STACKS = "protocol_stacks";
	private final static String STACK = "stack";
	private static final String NAME = "name";
	private static final String CONFIG = "config";
	
	/**
	 * Map<String,String>. Hashmap which maps stack names to JGroups
	 * configurations. Keys are stack names, values are plain JGroups stack
	 * configs. This is (re-)populated whenever a setMultiplexerConfig() method
	 * is called
	 */
	private final Map<String, String> stacks = Collections
			.synchronizedMap(new HashMap<String, String>());

	public MChannelConfigurator(String stackName) {
		this.stackName= stackName;
	}

	public MChannelConfigurator() {
		this.stackName=Activator.getDefault().STACK_CONFIG_ID;
	}

	/**
	 * Returns the stack configuration as a string (to be fed into new
	 * JChannel()). Throws an exception if the stack_name is not found. One of
	 * the setMultiplexerConfig() methods had to be called beforehand
	 * @param stack_name 
	 * 
	 * @return The protocol stack config as a plain string
	 * @throws Exception 
	 */
	public String getConfig(String stack_name) throws Exception {
		String cfg = stacks.get(stack_name);
		if (cfg == null)
			throw new Exception("stack \"" + stack_name + "\" not found in "
					+ stacks.keySet());
		return cfg;
	}

	public Map<String, String> getStacks() {
		return stacks;
	}

	private MChannel createMChannel(MulticastAddress mcastAddr, String groupId,	View managerView) throws Exception {
		String props = null;
		String configID=Activator.getDefault().getClass().getPackage().getName()+"."+stackName;
		URL stackConfigURL = Activator.getDefault().getConfigURLForStackID(	configID );
		
		try {
			this.parseConfig(stackConfigURL, false);
		} catch (Exception e) {
			log.error("error parsing stackConfigURL:"
					+ stackConfigURL.toExternalForm()+" detail error: "+e.getLocalizedMessage() );
		}
		
		props = stacks.get( stackName );
		print(props);

		Channel jc=createJChannel(props, managerView);
		MChannel mChannel = new MChannelImpl(jc, mcastAddr,	stackName);
		return mChannel;
	}
	public MChannel createMChannel(String mcastAddr, String groupId, View managerView) throws Exception {
		MulticastAddress multicastRealAddress = new MulticastAddress();
		multicastRealAddress.setValue(mcastAddr);
		return createMChannel(multicastRealAddress, groupId, managerView);
	}

	MulticastAddress multicastRealAddress = new MulticastAddress();

	public MChannel createMChannel(String mcastAddr, String groupId) {
		multicastRealAddress.setValue(mcastAddr);
		return createMChannel(multicastRealAddress, groupId);
	}

	public MChannel createMChannel(MulticastAddress mcastAddr) {
		// TODO Auto-generated method stub
		return null;
	}

	public MChannel createMChannel(MulticastAddress mcastAddr, String groupId) {
		try {
			return createMChannel(mcastAddr, groupId, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// PRIVATE METHODS --

	/**
	 * Creates a Channel. If emulated mode, notifies the group membership
	 * notifier that a new node has joined a group
	 * 
	 * @param props
	 * @return Channel
	 */
	private Channel createJChannel(String props, View creator) {
		
		Channel c = null;
		try {
			c = new JChannel(props, creator);
			c.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);
			c.connect(stackName);
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * This method is used to print the properties loaded on the channel
	 * 
	 * @param txt
	 */
	private void print(String txt) {
		while (true) {
			if (txt.length() > 80) {
				String substring = txt.substring(0, 80);
				System.out.println(substring);
				txt = txt.substring(80, txt.length());
			} else {
				System.out.println(txt);
				break;
			}
		}
	}

	// PRIVATE METODS --

	/**
	 * Protocol used to fragment big amounts of information in smaller packets
	 */
	private static String getFC() {
		// Note: FC.min_credits must to be > than FRAG2.frag_size
		return "FC(max_credits=150000;lowest_max_credits=110000;min_credits=60000;min_threshold=0.25)";
	}

	/**
	 * Protocol used to fragment big amounts of information in smaller packets
	 */
	private static String getFRAG2() {
		// Note: FC.min_credits must to be > than FRAG2.frag_size
		return "FRAG2(frag_size=50000;" + "down_thread=false;up_thread=false)";
	}

	private static String getOMOLSR(InetAddress group) {
		return "OMOLSR(mcast_addr=" + group.getHostAddress() + ")"; /*
																	 * ;bc_port=5555
																	 */// ;bcast_min_neigh=3
		// TODO Maybe we could pass a multicast port number (that obviously will
		// not be used) only for transparency purposes
	}

	private static String getSMCAST(InetAddress group) {
		return "SMCAST(mcast_addr=" + group.getHostAddress() + ")";
		// TODO Maybe we could pass a multicast port number (that obviously will
		// not be used) only for transparency purposes
	}

	private static String getMulticastProtocol(InetAddress group) {
		String multicastProtocol = PropertiesLoader.getMulticastProtocol();
		if (multicastProtocol.equalsIgnoreCase(SMCAST_PROTOCOL)) {
			return getSMCAST(group);
		} else if (multicastProtocol.equalsIgnoreCase(OMOLSR_PROTOCOL)) {
			return getOMOLSR(group);
		}
		return "";
	}

	/**
	 * This method returns a configured protocol to perform the message
	 * reliability
	 * 
	 * @param nodeNumber
	 * @return reliable transmission protocol
	 */
	private static String getReliability(int nodeNumber) {
		return "JOLSR_UNICAST(timeout=1200,1800,2400,5000,8000;use_gms=false)";
	}

	private static String getOLSR(InetAddress group) {
		if (group == null) {
			// Using OLSR without upper multicast protocol
			return "OLSR";
		} else {
			return "OLSR(mcast_addr=" + group.getHostAddress() + ")";
		}
	}

	private static String getBW_CALC() {
		StringBuffer buff = new StringBuffer();
		buff.append("BW_CALC(" + "info_millis=2000;"
				+ "minimumCapacityInBytes=180000;"
				+ "minimumCapacityInMessages=100)");
		return buff.toString();
	}

	private static String getJOLSR_UDP(int port) {
		return "JOLSR_UDP(" + "bind_port=" + port + ";" + "tos=8;"
				+ "port_range=1000;" + "ucast_recv_buf_size=64000000;"
				+ "ucast_send_buf_size=64000000;" + "loopback=false;"
				+ "discard_incompatible_packets=true;"
				+ "max_bundle_size=64000;" + "max_bundle_timeout=30;"
				+ "use_incoming_packet_handler=true;" + "ip_ttl=32;"
				+ "enable_bundling=true;" + "enable_diagnostics=false;"
				+ "thread_naming_pattern=cl;" + "use_concurrent_stack=true;"
				+ "thread_pool.enabled=true;" + "thread_pool.min_threads=2;"
				+ "thread_pool.max_threads=8;"
				+ "thread_pool.keep_alive_time=5000;"
				+ "thread_pool.queue_enabled=true;"
				+ "thread_pool.queue_max_size=1000;"
				+ "thread_pool.rejection_policy=discard;"
				+ "oob_thread_pool.enabled=true;"
				+ "oob_thread_pool.min_threads=1;"
				+ "oob_thread_pool.max_threads=8;"
				+ "oob_thread_pool.keep_alive_time=5000;"
				+ "oob_thread_pool.queue_enabled=false;"
				+ "oob_thread_pool.queue_max_size=100;"
				+ "oob_thread_pool.rejection_policy=Run" + ")";
	}

	/**
	 * Protocol stack WITH multicast protocol
	 * @param nodeNumber 
	 * @param port 
	 * @param group 
	 * @return String
	 */
	public static String getProtocolStackConfig(int nodeNumber, int port,
			InetAddress group) {
		StringBuffer stack = new StringBuffer();

		stack.append(getJOLSR_UDP(port));
		if (PropertiesLoader.isDynamicCredit()
				&& PropertiesLoader
						.isThroughputOptimizationNetworkSelfKnowledgementEnabled()) {
			stack.append(":" + getBW_CALC());
		}
		stack.append(":" + getOLSR(group));
		if (PropertiesLoader.isReliabilityEnabled()) {
			stack.append(":" + getReliability(nodeNumber));
		}
		stack.append(":" + getMulticastProtocol(group));
		stack.append(":" + getFC());
		stack.append(":" + getFRAG2());
		return stack.toString();
	}

	public void parseConfig(URL url, boolean replace) throws Exception {
		InputStream input = ConfiguratorFactory.getConfigStream(url);
		if (input == null)
			throw new FileNotFoundException(url.toString());
		try {
			parse(input, replace);
		} catch (Exception ex) {
			throw new Exception("failed parsing " + url.toString(), ex);
		} finally {
			Util.close(input);
		}
	}

	private void parse(InputStream input, boolean replace) throws Exception {
		/**
		 * CAUTION: crappy code ahead ! I (bela) am not an XML expert, so the
		 * code below is pretty amateurish... But it seems to work, and it is
		 * executed only on startup, so no perf loss on the critical path. If
		 * somebody wants to improve this, please be my guest.
		 */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false); // for now
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(input);

		// The root element of the document should be the "config" element,
		// but the parser(Element) method checks this so a check is not
		// needed here.
		Element configElement = document.getDocumentElement();
		parse(configElement, replace);
	}

	private void parse(Element root, boolean replace) throws Exception {
		/**
		 * CAUTION: crappy code ahead ! I (bela) am not an XML expert, so the
		 * code below is pretty amateurish... But it seems to work, and it is
		 * executed only on startup, so no perf loss on the critical path. If
		 * somebody wants to improve this, please be my guest.
		 */
		String root_name = root.getNodeName();
		if (!PROTOCOL_STACKS.equals(root_name.trim().toLowerCase())) {
			String error = "XML protocol stack configuration does not start with a '<config>' element; "
					+ "maybe the XML configuration needs to be converted to the new format ?\n"
					+ "use 'java org.jgroups.conf.XmlConfigurator <old XML file> -new_format' to do so";
			throw new IOException("invalid XML configuration: " + error);
		}

		NodeList tmp_stacks = root.getChildNodes();
		for (int i = 0; i < tmp_stacks.getLength(); i++) {
			Node node = tmp_stacks.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element stack = (Element) node;
			String tmp = stack.getNodeName();
			if (!STACK.equals(tmp.trim().toLowerCase())) {
				throw new IOException("invalid configuration: didn't find a \""
						+ STACK + "\" element under \"" + PROTOCOL_STACKS
						+ "\"");
			}

			NamedNodeMap attrs = stack.getAttributes();
			Node name = attrs.getNamedItem(NAME);
			// Node descr=attrs.getNamedItem(DESCR);
			String st_name = name.getNodeValue();
			// String stack_descr=descr.getNodeValue();
			// System.out.print("Parsing \"" + st_name + "\" (" + stack_descr +
			// ")");
			NodeList configs = stack.getChildNodes();
			for (int j = 0; j < configs.getLength(); j++) {
				Node tmp_config = configs.item(j);
				if (tmp_config.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element cfg = (Element) tmp_config;
				tmp = cfg.getNodeName();
				if (!CONFIG.equals(tmp))
					throw new IOException(
							"invalid configuration: didn't find a \"" + CONFIG
									+ "\" element under \"" + STACK + "\"");

				XmlConfigurator conf = XmlConfigurator.getInstance(cfg);
				// fixes http://jira.jboss.com/jira/browse/JGRP-290
				ConfiguratorFactory.substituteVariables(conf); // replace vars
																// with system
																// props
				String val = conf.getProtocolStackString();
				if (replace) {
					stacks.put(st_name, val);
					if (log.isTraceEnabled())
						log.trace("added config '" + st_name + "'");
				} else {
					if (!stacks.containsKey(st_name)) {
						stacks.put(st_name, val);
						if (log.isTraceEnabled())
							log.trace("added config '" + st_name + "'");
					} else {
						if (log.isTraceEnabled())
							log
									.trace("didn't add config '"
											+ st_name
											+ " because one of the same name already existed");
					}
				}
			}
		}
	}



}
