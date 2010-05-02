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
import org.eclipse.ecf.provider.internal.wave.google.DebugOptions;
import org.eclipse.ecf.provider.wave.google.identity.WaveBackendID;
import org.eclipse.ecf.provider.wave.google.identity.WaveBackendNamespace;
import org.eclipse.ecf.provider.wave.google.identity.WaveNamespace;
import org.eclipse.ecf.provider.wave.google.identity.WaveletNamespace;
import org.eclipse.ecf.wave.IWaveClientContainerAdapter;
import org.eclipse.ecf.wave.IWaveClientView;
import org.eclipse.osgi.util.NLS;
import org.waveprotocol.wave.examples.fedone.common.CommonConstants;
import org.waveprotocol.wave.examples.fedone.common.HashedVersion;
import org.waveprotocol.wave.examples.fedone.common.WaveletOperationSerializer;
import org.waveprotocol.wave.examples.fedone.model.util.HashedVersionZeroFactoryImpl;
import org.waveprotocol.wave.examples.fedone.rpc.ClientRpcChannel;
import org.waveprotocol.wave.examples.fedone.util.URLEncoderDecoderBasedPercentEncoderDecoder;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientIdGenerator;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.common.IndexEntry;
import org.waveprotocol.wave.examples.fedone.waveclient.common.RandomIdGenerator;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.examples.fedone.waveserver.WaveClientRpc.ProtocolOpenRequest;
import org.waveprotocol.wave.examples.fedone.waveserver.WaveClientRpc.ProtocolWaveClientRpc;
import org.waveprotocol.wave.examples.fedone.waveserver.WaveClientRpc.ProtocolWaveletUpdate;
import org.waveprotocol.wave.model.id.IdURIEncoderDecoder;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.id.URIEncoderDecoder.EncodingException;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.NoOp;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.operation.wave.WaveletDelta;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;
import org.waveprotocol.wave.federation.Proto.ProtocolWaveletDelta;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class WaveClientContainer extends AbstractContainer implements IWaveClientContainerAdapter {

	private ID localID;
	private WaveBackendID connectedID;
	private Object connectLock = new Object();

	/** User id of the user of the backend (encapsulating both user and server). */
	private ParticipantId userId;

	/** Waves this backend is aware of. */
	private Map<WaveId, ClientWaveView> waves = Maps.newHashMap();

	/** RPC controllers for the open wave connections. */
	private Map<WaveId, RpcController> waveControllers = Maps.newHashMap();

	/** Listeners waiting on wave updates. */
	private List<WaveletOperationListener> waveletOperationListeners = Lists
			.newArrayList();

	/** Id generator used for this (server, user) pair. */
	private ClientIdGenerator idGenerator;

	/** Id URI encoder and decoder. */
	private IdURIEncoderDecoder uriCodec;

	/** RPC stub for communicating with server. */
	private ProtocolWaveClientRpc.Stub rpcServer;

	/** RPC channel for communicating with server. */
	private ClientRpcChannel rpcChannel;

	private void logWaveletOperationEvent(String event, String author, WaveletData wavelet, ParticipantId participantId) {
		logInfo("container id="+getID()+" event="+event+" author="+author+" wavelet="+wavelet+" participantId="+participantId);
	}
	
	private void logWaveletDocumentOperationEvent(String author, WaveletData wavelet, WaveletDocumentOperation docOp) {
		logInfo("container id="+getID()+" waveletDocumentUpdated author="+author+" wavelet="+wavelet+" documentOperation="+docOp);
	}

	private void logWaveletCommitNoticeEvent(WaveletData wavelet, HashedVersion hashedVersion) {
		logInfo("container id="+getID()+" commitNotice wavelet="+wavelet+" hashedVersion="+hashedVersion);
	}

	private WaveletOperationListener waveletOperationListener = new WaveletOperationListener() {

		public void noOp(String author, WaveletData wavelet) {
			logWaveletOperationEvent("noOp",author,wavelet,null);
			// TODO Auto-generated method stub
		}

		public void onDeltaSequenceEnd(WaveletData wavelet) {
			logWaveletOperationEvent("onDeltaSequenceEnd",null,wavelet,null);
			// TODO Auto-generated method stub
		}

		public void onDeltaSequenceStart(WaveletData wavelet) {
			logWaveletOperationEvent("onDeltaSequenceStart",null,wavelet,null);
			// TODO Auto-generated method stub
		}

		public void participantAdded(String author, WaveletData wavelet,
				ParticipantId participantId) {
			logWaveletOperationEvent("participantAdded",author,wavelet,participantId);
			// TODO Auto-generated method stub

		}

		public void participantRemoved(String author, WaveletData wavelet,
				ParticipantId participantId) {
			logWaveletOperationEvent("participantRemoved",author,wavelet,participantId);
			// TODO Auto-generated method stub

		}

		public void waveletDocumentUpdated(String author, WaveletData wavelet,
				WaveletDocumentOperation docOp) {
			logWaveletDocumentOperationEvent(author,wavelet,docOp);
			// TODO Auto-generated method stub

		}

		public void onCommitNotice(WaveletData wavelet, HashedVersion version) {
			logWaveletCommitNoticeEvent(wavelet,version);
			// TODO Auto-generated method stub

		}

	};

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		
		if (targetID == null)
			throw new ContainerConnectException(
					"WaveBackend targetID cannot be null");
		
		if (!(targetID instanceof WaveBackendID))
			throw new ContainerConnectException(
					"targetID must be a WaveBackendID");

		WaveBackendID backendID = (WaveBackendID) targetID;
		fireContainerEvent(new ContainerConnectingEvent(localID, targetID, null));

		synchronized (connectLock) {
			if (backendConnected())
				throw new ContainerConnectException("Already connected");

			connectToBackend(backendID);
		}

		fireContainerEvent(new ContainerConnectedEvent(localID, targetID));
	}

	private boolean backendConnected() {
		synchronized (connectLock) {
			return userId != null;
		}
	}

	private void connectToBackend(WaveBackendID waveBackendConnectID)
			throws ContainerConnectException {
	
		String userAtDomain = waveBackendConnectID.getUserAtDomain();
		String host = waveBackendConnectID.getHost();
		int port = waveBackendConnectID.getPort();
		
		synchronized (connectLock) {
			if (backendConnected())
				throw new ContainerConnectException(
						"Wave client already connected");
			try {
				this.uriCodec = new IdURIEncoderDecoder(
						new URLEncoderDecoderBasedPercentEncoderDecoder());
				this.rpcChannel = new ClientRpcChannel(new InetSocketAddress(
						host, port));
				this.rpcServer = ProtocolWaveClientRpc.newStub(rpcChannel);
				this.userId = new ParticipantId(userAtDomain);
				this.idGenerator = new RandomIdGenerator(userId.getDomain());
				connectedID = waveBackendConnectID;
				
				// open wave
				openWave(CommonConstants.INDEX_WAVE_ID, "");

				// add operation listener
				addWaveletOperationListener(waveletOperationListener);
			} catch (IOException e) {
				logException("Exception connecting to wave backend", e);
				throw new ContainerConnectException(NLS.bind(
						"Cannot connect to {0}:{1} with user {2}",
						new Object[] { host, new Integer(port), userAtDomain }));
			}
		}
	}

	private void disconnectFromBackend() {
		fireContainerEvent(new ContainerDisconnectingEvent(localID, connectedID));
		ID previouslyConnectedID = connectedID;
		synchronized (connectLock) {
			for (RpcController rpcController : waveControllers.values()) {
				rpcController.startCancel();
			}
			this.userId = null;
			this.idGenerator = null;
			this.uriCodec = null;
			this.rpcChannel = null;
			this.rpcServer = null;
			removeWaveletOperationListener(waveletOperationListener);
			
			connectedID = null;
			waves.clear();
			waveControllers.clear();
		}
		fireContainerEvent(new ContainerDisconnectedEvent(localID,
				previouslyConnectedID));
	}

	public void disconnect() {
		disconnectFromBackend();
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(
				WaveBackendNamespace.NAME);
	}

	public ID getConnectedID() {
		return connectedID;
	}

	public ID getID() {
		return localID;
	}

	private void openWave(WaveId waveId, String waveletIdPrefix) {
		if (waveControllers.containsKey(waveId)) {
			throw new IllegalArgumentException(waveId + " is already open");
		} else {
			// May already be there if created with createNewWave
			if (!waves.containsKey(waveId)) {
				createWave(waveId);
			}
		}

		ProtocolOpenRequest.Builder openRequest = ProtocolOpenRequest
				.newBuilder();

		openRequest.setParticipantId(getUserId().getAddress());
		openRequest.setWaveId(waveId.serialise());
		openRequest.addWaveletIdPrefix(waveletIdPrefix);

		final RpcController rpcController = rpcChannel.newRpcController();
		waveControllers.put(waveId, rpcController);

		logInfo("Opening wave " + waveId + " for prefix \"" + waveletIdPrefix
				+ '"');
		rpcServer.open(rpcController, openRequest.build(),
				new RpcCallback<ProtocolWaveletUpdate>() {
					public void run(ProtocolWaveletUpdate update) {
						if (update == null) {
							logWarning("RPC failed: "
									+ rpcController.errorText());
						} else {
							receiveWaveletUpdate(update);
						}
					}
				});
	}

	private void logWarning(String message) {
		trace(DebugOptions.TRACE, message);
	}

	private void logException(String message, Throwable t) {
		trace(DebugOptions.TRACE, message);
	}

	private void logInfo(String message) {
		trace(DebugOptions.TRACE, message);
	}

	private void trace(String option, String message) {
		Trace.trace(Activator.PLUGIN_ID, option, message);
	}

	private ClientWaveView createWave(WaveId waveId) {
		ClientWaveView wave = new ClientWaveView(
				new HashedVersionZeroFactoryImpl(), waveId);
		waves.put(waveId, wave);
		return wave;
	}

	public ParticipantId getUserId() {
		return userId;
	}

	public void receiveWaveletUpdate(ProtocolWaveletUpdate waveletUpdate) {
		logInfo("Received update " + waveletUpdate);

		WaveletName waveletName;
		try {
			waveletName = uriCodec.uriToWaveletName(waveletUpdate
					.getWaveletName());
		} catch (EncodingException e) {
			throw new IllegalArgumentException(e);
		}

		ClientWaveView wave = waves.get(waveletName.waveId);
		if (wave == null) {
			// The wave view should always be present, since openWave adds them
			// immediately
			throw new AssertionError("Received update on absent waveId "
					+ waveletName.waveId);
		}

		WaveletData wavelet = wave.getWavelet(waveletName.waveletId);
		if (wavelet == null) {
			wavelet = wave.createWavelet(waveletName.waveletId);
		}

		if (waveletUpdate.hasCommitNotice()) {
			Preconditions.checkArgument(waveletUpdate.getAppliedDeltaList()
					.isEmpty());
			Preconditions.checkArgument(!waveletUpdate.hasResultingVersion());

			for (WaveletOperationListener listener : waveletOperationListeners) {
				listener.onCommitNotice(wavelet, WaveletOperationSerializer
						.deserialize(waveletUpdate.getCommitNotice()));
			}
		} else {
			Preconditions.checkArgument(waveletUpdate.hasResultingVersion());
			Preconditions.checkArgument(!waveletUpdate.getAppliedDeltaList()
					.isEmpty());

			for (WaveletOperationListener listener : waveletOperationListeners) {
				listener.onDeltaSequenceStart(wavelet);
			}

			// Apply operations to the wavelet
			List<Pair<String, WaveletOperation>> successfulOps = Lists
					.newArrayList();

			for (ProtocolWaveletDelta protobufDelta : waveletUpdate
					.getAppliedDeltaList()) {
				Pair<WaveletDelta, HashedVersion> deltaAndVersion = WaveletOperationSerializer
						.deserialize(protobufDelta);
				List<WaveletOperation> ops = deltaAndVersion.first
						.getOperations();

				for (WaveletOperation op : ops) {
					try {
						op.apply(wavelet);
						successfulOps.add(Pair
								.of(protobufDelta.getAuthor(), op));
					} catch (OperationException e) {
						// It should be okay (if cheeky) for the client to just
						// ignore failed ops. In any case,
						// this should never happen if our server is behaving
						// correctly.
						logException("DocumentOperationException when applying " + op
								+ " to " + wavelet, e);
					}
				}
			}

			wave.setWaveletVersion(waveletName.waveletId,
					WaveletOperationSerializer.deserialize(waveletUpdate
							.getResultingVersion()));

			// Notify listeners separately to avoid them operating on invalid
			// wavelet state
			// TODO: take this out of the network thread
			for (Pair<String, WaveletOperation> authorAndOp : successfulOps) {
				notifyWaveletOperationListeners(authorAndOp.first, wavelet,
						authorAndOp.second);
			}

			// If we have been removed from this wavelet then remove the data
			// too, since if we're re-added
			// then the deltas will come from version 0, not the latest version
			// we've seen
			if (!wavelet.getParticipants().contains(getUserId())) {
				wave.removeWavelet(waveletName.waveletId);
			}

			// If it was an update to the index wave, might need to open/close
			// some more waves
			if (wave.getWaveId().equals(CommonConstants.INDEX_WAVE_ID)) {
				syncWithIndexWave(wave);
			}

			for (WaveletOperationListener listener : waveletOperationListeners) {
				listener.onDeltaSequenceEnd(wavelet);
			}
		}
	}

	private void notifyWaveletOperationListeners(String author,
			WaveletData wavelet, WaveletOperation op) {
		for (WaveletOperationListener listener : waveletOperationListeners) {
			try {
				if (op instanceof WaveletDocumentOperation) {
					listener.waveletDocumentUpdated(author, wavelet,
							(WaveletDocumentOperation) op);
				} else if (op instanceof AddParticipant) {
					listener.participantAdded(author, wavelet,
							((AddParticipant) op).getParticipantId());
				} else if (op instanceof RemoveParticipant) {
					listener.participantRemoved(author, wavelet,
							((RemoveParticipant) op).getParticipantId());
				} else if (op instanceof NoOp) {
					listener.noOp(author, wavelet);
				}
			} catch (RuntimeException e) {
				logException("RuntimeException for listener " + listener, e);
			}
		}
	}

	private void syncWithIndexWave(ClientWaveView indexWave) {
		List<IndexEntry> indexEntries = ClientUtils.getIndexEntries(indexWave);

		for (IndexEntry indexEntry : indexEntries) {
			if (!waveControllers.containsKey(indexEntry.getWaveId())) {
				WaveId waveId = indexEntry.getWaveId();
				openWave(waveId, ClientUtils.getConversationRootId(waveId)
						.serialise());
			}
		}
	}

	/**
	 * Add a {@link WaveletOperationListener} to be notified whenever a wave is
	 * updated.
	 * 
	 * @param listener
	 *            new listener
	 */
	public void addWaveletOperationListener(WaveletOperationListener listener) {
		waveletOperationListeners.add(listener);
	}

	/**
	 * @param listener
	 *            listener to be removed
	 */
	public void removeWaveletOperationListener(WaveletOperationListener listener) {
		waveletOperationListeners.remove(listener);
	}

	  /**
	   * @return the id generator which generates wave, wavelet, and document ids
	   */
	  public ClientIdGenerator getIdGenerator() {
	    return idGenerator;
	  }

	public Namespace getWaveNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveNamespace.NAME);
	}

	public IWaveClientView getIndexWaveClientView() {
		// TODO Auto-generated method stub
		return null;
	}

	public Namespace getDocumentNamespace() {
		// XXX todo
		return null;
	}

	public Namespace getParticipantNamespace() {
		// XXX todo
		return null;
	}

	public Namespace getWaveletNamespace() {
		return IDFactory.getDefault().getNamespaceByName(WaveletNamespace.NAME);
	}


}
