/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.container;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

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
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.provider.internal.wave.google.Activator;
import org.eclipse.ecf.provider.internal.wave.google.CommonConstants;
import org.eclipse.ecf.provider.internal.wave.google.DebugOptions;
import org.eclipse.ecf.provider.wave.google.identity.WaveBackendID;
import org.eclipse.ecf.provider.wave.google.identity.WaveBackendNamespace;
import org.eclipse.ecf.provider.wave.google.identity.WaveID;
import org.eclipse.ecf.provider.wave.google.identity.WaveNamespace;
import org.eclipse.ecf.provider.wave.google.identity.WaveletID;
import org.eclipse.ecf.provider.wave.google.identity.WaveletNamespace;
import org.eclipse.ecf.wave.IWaveClientContainerAdapter;
import org.eclipse.ecf.wave.IWaveletListener;
import org.eclipse.ecf.wave.rpc.WaveClientRpc.ProtocolOpenRequest;
import org.eclipse.ecf.wave.rpc.WaveClientRpc.ProtocolWaveClientRpc;
import org.eclipse.ecf.wave.rpc.WaveClientRpc.ProtocolWaveletUpdate;
import org.eclipse.osgi.util.NLS;
import org.waveprotocol.wave.examples.fedone.common.HashedVersion;
import org.waveprotocol.wave.examples.fedone.common.WaveletOperationSerializer;
import org.waveprotocol.wave.examples.fedone.rpc.ClientRpcChannel;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientIdGenerator;
import org.waveprotocol.wave.examples.fedone.waveclient.common.RandomIdGenerator;
import org.waveprotocol.wave.federation.Proto.ProtocolWaveletDelta;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.wave.WaveletDelta;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class WaveClientContainer extends AbstractContainer implements IWaveClientContainerAdapter {

	private WaveID indexWave = CommonConstants.INDEX_WAVE_ID;

	private ID localID;
	
	private WaveBackendID connectedID;
	
	private Object connectLock = new Object();

	/** User id of the user of the backend (encapsulating both user and server). */
	private ParticipantId userId;

	/** Waves this backend is aware of. */
	private Map<WaveID, Wave> waves = Maps.newHashMap();

	/** RPC controllers for the open wave connections. */
	private Map<WaveID, RpcController> waveControllers = Maps.newHashMap();

	/** Listeners waiting on wave updates. */
	private List<IWaveletListener> waveletOperationListeners = Lists.newArrayList();

	/** Id generator used for this (server, user) pair. */
	private ClientIdGenerator idGenerator;

	/** RPC stub for communicating with server. */
	private ProtocolWaveClientRpc.Stub rpcServer;

	/** RPC channel for communicating with server. */
	private ClientRpcChannel rpcChannel;

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {

		if (targetID == null) {
			throw new ContainerConnectException(
					"WaveBackend targetID cannot be null");
		}
	
		if (!(targetID instanceof WaveBackendID)) {
			throw new ContainerConnectException(
					"targetID must be a WaveBackendID");
		}
		
		WaveBackendID backendID = (WaveBackendID) targetID;
		fireContainerEvent(new ContainerConnectingEvent(localID, targetID, null));

		synchronized (connectLock) {
			if (backendConnected()) {
				throw new ContainerConnectException("Already connected");
			}

			connectToBackend(backendID);
		}

		fireContainerEvent(new ContainerConnectedEvent(localID, targetID));
	}

	private boolean backendConnected() {
		synchronized (connectLock) {
			return userId != null;
		}
	}

	private void connectToBackend(WaveBackendID waveBackendConnectID) throws ContainerConnectException {
		String userAtDomain = waveBackendConnectID.getUserAtDomain();
		String host = waveBackendConnectID.getHost();
		int port = waveBackendConnectID.getPort();

		synchronized (connectLock) {
			if (backendConnected()) {
				throw new ContainerConnectException("Wave client already connected");
			}
			
			try {
				this.rpcChannel = new ClientRpcChannel(new InetSocketAddress(host, port));
				this.rpcServer = ProtocolWaveClientRpc.newStub(rpcChannel);
				this.userId = waveBackendConnectID.getParticipant();
				this.idGenerator = new RandomIdGenerator(userId.getDomain());
				connectedID = waveBackendConnectID;

				// open wave
				openWave(indexWave, "");
			} catch (IOException e) {
				throw new ContainerConnectException(NLS.bind("Cannot connect to {0}:{1} with user {2}",
						new Object[] { host, new Integer(port), userAtDomain }));
			}
		}
	}

	public void disconnect() {
		fireContainerEvent(new ContainerDisconnectingEvent(localID, connectedID));
		ID previouslyConnectedID = connectedID;

		synchronized (connectLock) {
			for (RpcController rpcController : waveControllers.values()) {
				rpcController.startCancel();
			}
			this.userId = null;
			this.idGenerator = null;
			this.rpcChannel = null;
			this.rpcServer = null;

			connectedID = null;
			waves.clear();
			waveControllers.clear();
		}

		fireContainerEvent(new ContainerDisconnectedEvent(localID, previouslyConnectedID));
	}

	private void openWave(WaveID waveId, String waveletIdPrefix) {
		if (waveControllers.containsKey(waveId)) {
			throw new IllegalArgumentException(waveId + " is already open");
		} else {
			// May already be there if created with createNewWave
			if (!waves.containsKey(waveId)) {
				addWave(waveId);
			}
		}

		ProtocolOpenRequest.Builder openRequest = ProtocolOpenRequest.newBuilder();

		openRequest.setParticipantId(getUserId().getAddress());
		openRequest.setWaveId(waveId.toString());
		openRequest.addWaveletIdPrefix(waveletIdPrefix);

		final RpcController rpcController = rpcChannel.newRpcController();
		waveControllers.put(waveId, rpcController);

		logInfo("Opening wave " + waveId + " for prefix \"" + waveletIdPrefix + '"');
		
		rpcServer.open(rpcController, openRequest.build(),
			new RpcCallback<ProtocolWaveletUpdate>() {
				public void run(ProtocolWaveletUpdate update) {
					if (update == null) {
						logWarning("RPC failed: " + rpcController.errorText());
					} else {
						receiveWaveletUpdate(update);
					}
				}
			});
	}

	private Wave addWave(WaveID waveId) {
		Wave wave = new Wave(waveId);
		waves.put(waveId, wave);

		return wave;
	}

	public void receiveWaveletUpdate(ProtocolWaveletUpdate waveletUpdate) {
		Wave wave = getWaveFromWaveletUpdate(waveletUpdate);
		Wavelet wavelet = getWaveletFromWaveletUpdate(waveletUpdate, wave);

		// commit
		if (waveletUpdate.hasCommitNotice()) {
			for (IWaveletListener listener : waveletOperationListeners) {
				listener.commit(wavelet, waveletUpdate.getCommitNotice());
			}
		} else {
			handleWaveletUpdateOperation(wave, wavelet, waveletUpdate);
		}
	}

	private void handleWaveletUpdateOperation(Wave wave, Wavelet wavelet, ProtocolWaveletUpdate waveletUpdate) {
		List<Pair<String, WaveletOperation>> successfulOps = Lists.newArrayList();

		for (ProtocolWaveletDelta protobufDelta : waveletUpdate.getAppliedDeltaList()) {
			Pair<WaveletDelta, HashedVersion> deltaAndVersion = WaveletOperationSerializer.deserialize(protobufDelta);
			List<WaveletOperation> ops = deltaAndVersion.first.getOperations();

			for (WaveletOperation op : ops) {
				try {
					op.apply(wavelet);
					successfulOps.add(Pair.of(protobufDelta.getAuthor(), op));
				} catch (OperationException e) {
					logException("DocumentOperationException when applying " + op + " to " + wavelet, e);
				}
			}
		}

		wave.setWaveletVersion(wavelet.getWaveletId(), waveletUpdate.getResultingVersion());

		// Notify listeners separately to avoid them operating on invalid wavelet state
		for (Pair<String, WaveletOperation> authorAndOp : successfulOps) {
			notifyWaveletOperationListeners(authorAndOp.first, wavelet,	authorAndOp.second);
		}

		// If we have been removed from this wavelet then remove the data too
		if (!wavelet.getParticipants().contains(getUserId())) {
			wave.removeWavelet(wavelet.getWaveletId());
		}

		// If it was an update to the index wave, might need to open/close
		// some more waves
		if(wave.isIndexWave()) {
			createNewWaveHandlers(wave);
		}
	}

	private void createNewWaveHandlers(Wave wave) {
		for(Wavelet wavelet : wave.getWavelets()) {
			WaveletID waveletId = wavelet.getWaveletId();
			WaveID waveId = (WaveID) getWaveNamespace().createInstance(new String[] 
			                      { waveletId.getName() });
			
			if(!waves.containsKey(waveId)) {
				openWave(waveId, "");
			}
		}
	}

	private Wavelet getWaveletFromWaveletUpdate(ProtocolWaveletUpdate waveletUpdate, Wave wave) {
		WaveletID waveletId = (WaveletID) getWaveletNamespace().createInstance(
				new String[] { waveletUpdate.getWaveletName() });

		Wavelet wavelet = wave.getWavelet(waveletId);
		if (wavelet == null) {
			wavelet = wave.createWavelet(waveletId);
		}

		return wavelet;
	}

	private Wave getWaveFromWaveletUpdate(ProtocolWaveletUpdate waveletUpdate) {
		WaveID waveId = (WaveID) getWaveNamespace().createInstance(
				new String[] { waveletUpdate.getWaveletName() });

		Wave wave = waves.get(waveId);
		if (wave == null) {
			throw new AssertionError("Received update on absent waveID " + waveId);
		}

		return wave;
	}

	private void notifyWaveletOperationListeners(String author,	Wavelet wavelet, WaveletOperation operation) {
		for (IWaveletListener listener : waveletOperationListeners) {
			try {
				listener.notify(wavelet, author, operation);
			} catch (RuntimeException e) {
				logException("RuntimeException for listener " + listener, e);
			}
		}
	}

	/**
	 * Add a {@link WaveletOperationListener} to be notified whenever a wave is updated.
	 * 
	 * @param listener
	 *            new listener
	 */
	public void addWaveletOperationListener(IWaveletListener listener) {
		waveletOperationListeners.add(listener);
	}

	/**
	 * @param listener
	 *            listener to be removed
	 */
	public void removeWaveletOperationListener(IWaveletListener listener) {
		waveletOperationListeners.remove(listener);
	}

	public Wave getIndexWaveClientView() {
		return waves.get(indexWave);
	}

	/**
	 * @return the id generator which generates wave, wavelet, and document ids
	 */
	public ClientIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public ID getID() {
		return localID;
	}

	public ParticipantId getUserId() {
		return userId;
	}

	public ID getConnectedID() {
		return connectedID;
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveBackendNamespace.NAME);
	}

	public Namespace getWaveNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveNamespace.NAME);
	}

	public Namespace getWaveletNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveletNamespace.NAME);
	}

	public Namespace getDocumentNamespace() {
		// XXX todo
		return null;
	}

	public Namespace getParticipantNamespace() {
		// XXX todo
		return null;
	}

	private void logInfo(String message) {
		trace(DebugOptions.TRACE, message);
	}
	
	private void logWarning(String message) {
		trace(DebugOptions.TRACE, message);
	}

	private void logException(String message, Throwable t) {
		trace(DebugOptions.TRACE, message);
	}

	private void trace(String option, String message) {
		Trace.trace(Activator.PLUGIN_ID, option, message);
	}
}