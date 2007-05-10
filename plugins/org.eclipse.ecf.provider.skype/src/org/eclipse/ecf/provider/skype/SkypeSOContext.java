/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.skype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.events.RemoteSharedObjectEvent;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.util.Base64;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.generic.SOContext;
import org.eclipse.ecf.provider.generic.SOWrapper;

import com.skype.Application;
import com.skype.ApplicationListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.StreamListener;

/**
 * 
 */
public class SkypeSOContext extends SOContext {

	private Application application = null;
	
	private Map streams = new Hashtable();

	private List membership = new Vector();
	
	class SOContextStreamListener implements StreamListener {

		ID memberID = null;
		
		public SOContextStreamListener(ID memberID) {
			this.memberID = memberID;
		}
		public void datagramReceived(String receivedDatagram)
				throws SkypeException {
		}

		public void textReceived(String receivedText) throws SkypeException {
			handleReceived(memberID, receivedText);
		}
		
	};
	
	private ApplicationListener applicationListener = new ApplicationListener() {
		public void connected(Stream stream) throws SkypeException {
			ID memberID = createIDFromName(stream.getFriend().getId());
			membership.add(memberID);
			stream.addStreamListener(new SOContextStreamListener(memberID));
			streams.put(memberID,stream);
			// XXX fire containerconnected event
		}

		public void disconnected(Stream stream) throws SkypeException {
			ID member = createIDFromName(stream.getFriend().getId());
			membership.remove(member);
			streams.remove(createIDFromName(stream.getFriend().getId()));
			// XXX fire containerdisconnected event
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContext#getGroupMemberIDs()
	 */
	public ID[] getGroupMemberIDs() {
		return (ID[]) membership.toArray(new ID[] {});
	}
	
	/**
	 * @param memberID
	 * @param receivedText
	 */
	protected void handleReceived(ID memberID, String receivedText) {
		// First get stream...make sure it's still active
		Stream stream = (Stream) streams.get(memberID);
		if (stream != null) {
			try {
				Object o = deserialize(Base64.decode(receivedText));
				// Deliver to queue for processing
				queue.enqueue(new SOWrapper.ProcEvent(new RemoteSharedObjectEvent(SkypeSOContext.this.sharedObjectID,memberID,o)));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param objID
	 * @param homeID
	 * @param cont
	 * @param props
	 * @param queue
	 */
	public SkypeSOContext(ID objID, ID homeID, SOContainer cont, Map props,
			IQueueEnqueue queue) {
		super(objID, homeID, cont, props, queue);
		try {
			membership.add(cont.getID());
			application = Skype.addApplication(objID.getName());
			application.addApplicationListener(applicationListener);
		} catch (SkypeException e) {
			application = null;
		}
	}

	protected ID createIDFromName(String id) {
		try {
			return IDFactory.getDefault().createStringID(id);
		} catch (IDCreateException e) {
			// Should never happen
			return null;
		}
	}
	
	protected Stream getStreamForId(String id) throws IOException {
		if (application == null) throw new IOException("application not available");
		return (Stream) streams.get(id);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContext#sendMessage(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object)
	 */
	public void sendMessage(ID toContainerID, Object data) throws IOException {
		if (toContainerID == null) {
			ID [] ids = getGroupMemberIDs();
			for(int i=0; i < 0; i++) sendMessage(ids[i],data);
		} else {
			Stream stream = getStreamForId(toContainerID.getName());
			if (stream != null) {
				try {
					stream.write(Base64.encode(serialize(data)));
				} catch (Exception e) {
					throw new IOException(e.getLocalizedMessage());
				}
			}
		}
	}
	
	protected byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		return bos.toByteArray();
	}

	protected Object deserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
		ObjectInputStream oins = new ObjectInputStream(bins);
		return oins.readObject();
	}

}
