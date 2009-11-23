package org.eclipse.ecf.wave.container;

import java.io.IOException;

import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.wave.identity.WaveBackendID;
import org.eclipse.ecf.wave.identity.WaveBackendNamespace;
import org.eclipse.osgi.util.NLS;
import org.waveprotocol.wave.examples.fedone.common.HashedVersion;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

public class WaveClientContainer extends AbstractContainer {

	private ID localID;
	private WaveBackendID connectedID;
	private Object connectLock = new Object();
	private ClientBackend backend;
	
	private WaveletOperationListener waveletOperationListener = new WaveletOperationListener() {

		public void noOp(String author, WaveletData wavelet) {
			// TODO Auto-generated method stub
			
		}

		public void onDeltaSequenceEnd(WaveletData wavelet) {
			// TODO Auto-generated method stub
			
		}

		public void onDeltaSequenceStart(WaveletData wavelet) {
			// TODO Auto-generated method stub
			
		}

		public void participantAdded(String author, WaveletData wavelet,
				ParticipantId participantId) {
			// TODO Auto-generated method stub
			
		}

		public void participantRemoved(String author, WaveletData wavelet,
				ParticipantId participantId) {
			// TODO Auto-generated method stub
			
		}

		public void waveletDocumentUpdated(String author, WaveletData wavelet,
				WaveletDocumentOperation docOp) {
			// TODO Auto-generated method stub
			
		}

		public void onCommitNotice(WaveletData wavelet, HashedVersion version) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		if (targetID == null) throw new ContainerConnectException("WaveBackend targetID cannot be null");
		if (!(targetID instanceof WaveBackendID)) throw new ContainerConnectException("targetID must be a WaveBackendID");
		WaveBackendID backendID = (WaveBackendID) targetID;
		fireContainerEvent(new ContainerConnectingEvent(localID, targetID,null));
		synchronized (connectLock) {
			if (backendConnected()) throw new ContainerConnectException("Already connected");
			connectToBackend(backendID);
		}
		fireContainerEvent(new ContainerConnectedEvent(localID, targetID));
	}

	private boolean backendConnected() {
		synchronized (connectLock) {
			return backend != null;
		}
	}
	
	private void connectToBackend(WaveBackendID waveBackendConnectID) throws ContainerConnectException {
		String userAtDomain = waveBackendConnectID.getUserAtDomain();
		String host = waveBackendConnectID.getHost();
		int port = waveBackendConnectID.getPort();
		synchronized (connectLock) {
			if (backendConnected()) throw new ContainerConnectException("Wave client already connected");
			try{
				backend = new ClientBackend(userAtDomain, host, port);
				backend.addWaveletOperationListener(waveletOperationListener);
				connectedID = waveBackendConnectID;
			} catch (IOException e) {
				throw new ContainerConnectException(NLS.bind("Cannot connect to {0}:{1} with user {2}",new Object[] { host, new Integer(port), userAtDomain}));
			}
		}
	}
	
	private void disconnectFromBackend() {
		fireContainerEvent(new ContainerDisconnectingEvent(localID, connectedID));
		ID previouslyConnectedID = connectedID;
		synchronized (connectLock) {
			if (backend == null) return;
			backend.shutdown();
			backend = null;
			connectedID = null;
		}
		fireContainerEvent(new ContainerDisconnectedEvent(localID, previouslyConnectedID));
	}
	
	public void disconnect() {
		disconnectFromBackend();
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveBackendNamespace.NAME);
	}

	public ID getConnectedID() {
		return connectedID;
	}

	public ID getID() {
		return localID;
	}

}
