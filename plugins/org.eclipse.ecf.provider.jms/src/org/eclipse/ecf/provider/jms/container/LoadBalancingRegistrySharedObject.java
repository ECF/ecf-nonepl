/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jms.container;

import java.io.IOException;
import java.io.Serializable;
import javax.jms.*;
import org.eclipse.ecf.provider.remoteservice.generic.*;
import org.eclipse.ecf.remoteservice.IRemoteCall;

public class LoadBalancingRegistrySharedObject extends RegistrySharedObject {

	private IJMSQueueContainer container;

	public LoadBalancingRegistrySharedObject(IJMSQueueContainer container) {
		this.container = container;
		// Set to LoadBalancingRemoteServiceRegistryImpl rather than to RemoteServiceRegistryImpl
		localRegistry = new LoadBalancingRemoteServiceRegistryImpl();
	}

	// this class LoadBalancingRemoteServiceRegistryImpl is here only because the org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject.getLocalRegistrationForRequest(Request)
	// is private...and since this subclass needs access to it (protected needed), we have to create a new method
	// getLocalRegistrationForJMSRequest and have the LoadBalancingRemoteServiceRegistryImpl implementation of this method
	// call the RemoteServiceRegistryImpl.findRegistrationForServiceId.  When this bug is addressed (ECF 3.1)
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=284676 then this code can be removed.
	public class LoadBalancingRemoteServiceRegistryImpl extends RemoteServiceRegistryImpl {
		public RemoteServiceRegistrationImpl findRegistrationForJMSRequest(Request request) {
			return findRegistrationForServiceId(request.getServiceId());
		}
	}

	private RemoteServiceRegistrationImpl getLocalRegistrationForJMSRequest(Request request) {
		synchronized (localRegistry) {
			return ((LoadBalancingRemoteServiceRegistryImpl) localRegistry).findRegistrationForJMSRequest(request);
		}
	}

	// JMS message handling entry point for incoming Request messages (i.e. requests for remote method invocation).
	public void handleJMSMessage(Message jmsMessage) {
		if (jmsMessage == null)
			return;
		Request request = null;
		// First get Request instance from jms message (see sendCallRequest for send code)
		try {
			if (jmsMessage instanceof ObjectMessage) {
				Serializable object = ((ObjectMessage) jmsMessage).getObject();
				if (object instanceof Request)
					request = (Request) object;
			}
			if (request == null)
				throw new JMSException("Invalid message=" + jmsMessage);
		} catch (JMSException e) {
			log("handleJMSMessage message=" + jmsMessage, e);
			return;
		}
		// Process call request locally
		handleJMSRequest(jmsMessage, request);
	}

	void handleJMSRequest(Message jmsMessage, Request request) {
		// XXX should have some use of Job to actually make synchronous call in response to request
		// and send result
		final RemoteServiceRegistrationImpl localRegistration = getLocalRegistrationForJMSRequest(request);
		// Else we've got a local service and we invoke it
		final RemoteCallImpl call = request.getCall();
		Response response = null;
		Object result = null;
		// Actually call local service here
		try {
			result = localRegistration.callService(call);
			response = new Response(request.getRequestId(), result);
		} catch (final Exception e) {
			response = new Response(request.getRequestId(), e);
			log(208, "Exception invoking service", e);
		}
		// Then send response back to initial sender
		try {
			ObjectMessage responseMessage = container.getSession().createObjectMessage();
			responseMessage.setObject(response);
			responseMessage.setJMSCorrelationID(jmsMessage.getJMSCorrelationID());
			container.getMessageProducer().send(jmsMessage.getJMSReplyTo(), responseMessage);
		} catch (JMSException e) {
			log("sendCallResponse jmsMessage=" + jmsMessage + ", response=" + response, e);
		}
		// XXX end need for job
	}

	private MessageListener responseHandler = new MessageListener() {
		public void onMessage(Message arg0) {
			handleJMSResponse(arg0);
		}
	};

	void handleJMSResponse(Message jmsMessage) {
		Response response = null;
		// Get Response from jms message
		try {
			if (jmsMessage instanceof ObjectMessage) {
				Serializable object = ((ObjectMessage) jmsMessage).getObject();
				if (object instanceof Response)
					response = (Response) object;
			}
			if (response == null)
				throw new JMSException("handleJMSResponse invalid message=" + jmsMessage);
		} catch (JMSException e) {
			log("handleJMSResponse exception for message=" + jmsMessage, e);
			return;
		}
		// Actually handle the response (via superclass)
		handleCallResponse(response);
	}

	// Override of RegistrySharedObject.sendCallRequest.  This is called when RegistrySharedObject.callSynch is
	// called (i.e. when an IRemoteService proxy or IRemoteService.callSync is called.
	protected Request sendCallRequest(RemoteServiceRegistrationImpl remoteRegistration, final IRemoteCall call) throws IOException {
		Request request = new Request(this.getLocalContainerID(), remoteRegistration.getServiceId(), RemoteCallImpl.createRemoteCall(null, call.getMethod(), call.getParameters(), call.getTimeout()), null);
		requests.add(request);
		try {
			//Get a temporary queue that this client will listen for responses
			Destination tempDest = container.getResponseQueue();
			MessageConsumer responseConsumer = container.getSession().createConsumer(tempDest);

			//This class will handle the messages to the temp queue as well 
			responseConsumer.setMessageListener(responseHandler);

			// Create the request message and set the object (the Request itself)
			ObjectMessage objMessage = container.getSession().createObjectMessage();
			objMessage.setObject(request);

			//Set the reply to field to the temp queue you created above, this is the queue the server 
			//will respond to 
			objMessage.setJMSReplyTo(tempDest);

			//Set a correlation ID so when you get a response you know which sent message the response is for 
			objMessage.setJMSCorrelationID(request.getRequestContainerID().getName() + "-" + request.getRequestId());
			container.getMessageProducer().send(objMessage);
		} catch (JMSException e) {
			log("sendCallRequest request=" + request, e);
		}
		return request;
	}

}
