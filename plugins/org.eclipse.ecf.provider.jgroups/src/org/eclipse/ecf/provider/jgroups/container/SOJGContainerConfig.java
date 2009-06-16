package org.eclipse.ecf.provider.jgroups.container;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.provider.jgroups.connection.IChannelConfigurator;
import org.eclipse.ecf.provider.jgroups.connection.MChannelConfigurator;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.jgroups.View;
import org.jgroups.ViewId;
import org.jgroups.stack.IpAddress;


public class SOJGContainerConfig implements ISharedObjectContainerConfig, IAdaptable {

	private JGroupsID targetID;
	private String groupname;

	

	protected static final String JGROUPS_UDP_MCAST_PORT_PROPNAME = "jgroups.udp.mcast_port";
	protected static final String JGROUPS_UDP_MCAST_ADDR_PROPNAME = "jgroups.udp.mcast_addr";

	protected String oldHost = null;
	protected int oldPort = -1;
	private View managerView;

	
	public SOJGContainerConfig(JGroupsID targetID) {
		this.targetID = targetID;
		try {
			init();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	@SuppressWarnings("unchecked")
	private void init() throws Exception{
		groupname = targetID.getChannelName();
		// we are coordinator
		Vector v = new Vector();
		v.addElement( targetID );
		IpAddress myIpAddress= new IpAddress( targetID.getHost(), targetID.getPort());
		
		this.managerView= new View( new ViewId(	myIpAddress ), v );
	}

	public View getManagerView() {
		return managerView;
	}
	public String getGroupname() {
		return groupname;
	}

	public ID getID() {
		return targetID;
	}

	protected void resetPropertiesForStack(JGroupsID targetID) {
		final String stackName = targetID.getStackName();
		if (stackName == null)
			return;
		if (stackName.equalsIgnoreCase(JGroupsID.DEFAULT_STACK_NAME)) {
			if (oldHost != null) {
				System.setProperty(JGROUPS_UDP_MCAST_ADDR_PROPNAME, oldHost);
				if (oldPort != -1)
					System.setProperty(JGROUPS_UDP_MCAST_PORT_PROPNAME, ""
							+ oldPort);
			} else if (System.getProperty(JGROUPS_UDP_MCAST_ADDR_PROPNAME) != null) {
				System.getProperties().remove(JGROUPS_UDP_MCAST_ADDR_PROPNAME);
				if (System.getProperty(JGROUPS_UDP_MCAST_PORT_PROPNAME) != null)
					System.getProperties().remove(
							JGROUPS_UDP_MCAST_PORT_PROPNAME);
			}
		}
	}

	private static byte first=0;
	
	protected static InetAddress getNextAvailable() throws UnknownHostException{
		return  InetAddress.getByAddress( new byte[]{ (byte) 192, (byte) 168, 1, ++first });
	}


	public Object getAdapter(IChannelConfigurator configurator) {
		return null;
	}
	public String getStackName() {
		return targetID.getStackName();
	}
	public Object getAdapter(Class adapter) {
		if( adapter.equals(IChannelConfigurator.class) )
			return new MChannelConfigurator( getGroupname() );
		return null;
	}
	public Map getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
