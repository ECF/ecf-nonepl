package org.eclipse.ecf.provider.jgroups.connection;

import java.util.Map;

import org.jgroups.View;

import urv.machannel.MChannel;
import urv.olsr.mcast.MulticastAddress;

public interface IChannelConfigurator {

	/**
	 * This method returns and initiates a MChannel instance when the channel is used in a not emulated environment.
	 * You only need to know the mcastAddress to start up the group.
	 * 
	 * @param mcastAddr
	 * @return MChannel
	 */
	public abstract MChannel createMChannel(MulticastAddress mcastAddr);

	/**
	 * This method returns and initiates a MChannel instance when the channel is used in a not emulated environment.
	 * You need to set the mcastAddress (MulticastAddress) and the group ID to start up the group.
	 * 
	 * @param mcastAddr
	 * @param groupId 
	 * @return MChannel
	 */
	public abstract MChannel createMChannel(MulticastAddress mcastAddr,	String groupId);

	/**
	 * This method returns and initiates a MChannel instance when the channel is used in a not emulated environment.
	 * You need to set the mcastAddress (String) and the group ID to start up the group.
	 * 
	 * @param mcastAddr
	 * @param groupId 
	 * @return MChannel
	 */
	public abstract MChannel createMChannel(String mcastAddr, String groupId);
	public MChannel createMChannel(String mcastAddr, String groupId, View managerView) throws Exception;
	public abstract Map<String,String> getStacks();
	
}