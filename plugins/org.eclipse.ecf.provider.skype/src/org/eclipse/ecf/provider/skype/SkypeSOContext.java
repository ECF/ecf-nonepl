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

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.events.RemoteSharedObjectEvent;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.sharedobject.util.QueueException;
import org.eclipse.ecf.core.util.Base64;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.internal.provider.skype.Messages;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.generic.SOContext;
import org.eclipse.ecf.provider.generic.SOWrapper;

import com.skype.Application;
import com.skype.ApplicationListener;
import com.skype.Friend;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.StreamListener;

/**
 * 
 */
public class SkypeSOContext extends SOContext {

	private Application application = null;

	private final Map streams = new Hashtable();

	private final List membership = new Vector();

	class SOContextStreamListener implements StreamListener {

		ID memberID = null;

		public SOContextStreamListener(ID memberID) {
			this.memberID = memberID;
		}

		public void datagramReceived(String receivedDatagram) throws SkypeException {
		}

		public void textReceived(String receivedText) throws SkypeException {
			handleReceived(memberID, receivedText);
		}

	};

	private final ApplicationListener applicationListener = new ApplicationListener() {
		public void connected(Stream stream) throws SkypeException {
			final ID memberID = createIDFromName(stream.getFriend().getId());
			membership.add(memberID);
			stream.addStreamListener(new SOContextStreamListener(memberID));
			streams.put(memberID, stream);
			enqueue(new ContainerConnectedEvent(getLocalContainerID(), memberID));
		}

		public void disconnected(Stream stream) throws SkypeException {
			final ID memberID = createIDFromName(stream.getFriend().getId());
			membership.remove(memberID);
			streams.remove(memberID);
			enqueue(new ContainerDisconnectedEvent(getLocalContainerID(), memberID));
		}
	};

	/**
	 * @param application 
	 * @param objID
	 * @param homeID
	 * @param cont
	 * @param props
	 * @param queue
	 */
	public SkypeSOContext(Application application, ID objID, ID homeID, SOContainer cont, Map props, IQueueEnqueue queue) {
		super(objID, homeID, cont, props, queue);
		Assert.isNotNull(application);
		this.application = application;
		membership.add(cont.getID());
		this.application.addApplicationListener(applicationListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContext#getGroupMemberIDs()
	 */
	public ID[] getGroupMemberIDs() {
		return (ID[]) membership.toArray(new ID[] {});
	}

	private void enqueue(Event event) {
		try {
			queue.enqueue(new SOWrapper.ProcEvent(event));
		} catch (final QueueException e) {
			// Should not happen
		}
	}

	/**
	 * @param memberID
	 * @param receivedText
	 */
	protected void handleReceived(ID memberID, String receivedText) {
		// First get stream...make sure it's still active
		final Stream stream = (Stream) streams.get(memberID);
		if (stream != null) {
			try {
				final Object o = deserialize(Base64.decode(receivedText));
				// Deliver to queue for processing
				enqueue(new RemoteSharedObjectEvent(sharedObjectID, memberID, o));
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ID createIDFromName(String id) {
		try {
			return IDFactory.getDefault().createStringID(id);
		} catch (final IDCreateException e) {
			// Should never happen
			return null;
		}
	}

	protected Stream getStreamForId(String id) throws IOException {
		if (application == null)
			throw new IOException(Messages.SkypeSOContext_EXCEPTION_APP_NOT_AVAILABLE);
		return (Stream) streams.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContext#sendMessage(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object)
	 */
	public void sendMessage(ID targetID, Object data) throws IOException {
		if (targetID == null) {
			final ID[] ids = getGroupMemberIDs();
			for (int i = 0; i < 0; i++)
				sendMessage(ids[i], data);
		} else {
			try {
				final String friendID = targetID.getName();
				Stream stream = getStreamForId(friendID);
				if (stream == null) {
					final Friend friend = (Friend) Friend.getInstance(friendID);
					final Stream[] streams = application.connect(new Friend[] {friend});
					if (streams.length == 0)
						throw new IOException(Messages.SkypeSOContext_EXCEPTION_SKYPE_FRIEND_NO_APPLICATION);
					stream = streams[0];
				}
				stream.write(Base64.encode(serialize(data)));
			} catch (final SkypeException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
	}

	protected byte[] serialize(Object o) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		return bos.toByteArray();
	}

	protected Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
		final ObjectInputStream oins = new ObjectInputStream(bins);
		return oins.readObject();
	}

}
