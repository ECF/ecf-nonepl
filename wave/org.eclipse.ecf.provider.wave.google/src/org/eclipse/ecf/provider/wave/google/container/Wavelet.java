/*******************************************************************************
 * Copyright (c) 2010 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.wave.google.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.provider.wave.google.identity.WaveletID;
import org.eclipse.ecf.wave.IWavelet;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.algorithm.Composer;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuffer;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.operation.OpComparators;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class Wavelet implements IWavelet {

	private WaveletID waveletId;

	private List<ParticipantId> participants = new ArrayList<ParticipantId>();

	private Map<String, BufferedDocOp> documents = new HashMap<String, BufferedDocOp>();

	public Wavelet(WaveletID waveletId) {
		this.waveletId = waveletId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.waveprotocol.wave.model.wave.data.WaveletData#addParticipant(org.waveprotocol.wave.model.wave.ParticipantId)
	 */
	@Override
	public boolean addParticipant(ParticipantId participant) {
		if (participants.contains(participant)) {
			return false;
		}

		participants.add(participant);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.waveprotocol.wave.model.wave.data.WaveletData#getDocuments()
	 */
	@Override
	public Map<String, BufferedDocOp> getDocuments() {
		return documents;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.wave.IWavelet#getDocument(java.lang.String)
	 */
	@Override
	public BufferedDocOp getDocument(String documentId) {
		return documents.get(documentId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.waveprotocol.wave.model.wave.data.WaveletData#getParticipants()
	 */
	@Override
	public List<ParticipantId> getParticipants() {
		return participants;
	}

	/*
	 * (non-Javadoc)
	 * @see org.waveprotocol.wave.model.wave.data.WaveletData#getWaveletName()
	 */
	@Override
	@Deprecated
	public WaveletName getWaveletName() {
		return null; // do we need this?
	}

	/*
	 * (non-Javadoc)
	 * @see org.waveprotocol.wave.model.wave.data.WaveletData#modifyDocument(java.lang.String, org.waveprotocol.wave.model.document.operation.BufferedDocOp)
	 */
	@Override
	public boolean modifyDocument(String documentId, BufferedDocOp update)
			throws OperationException {

		if (documents.get(documentId) == null) {
			documents.put(documentId, new DocOpBuffer().finish());
		}

		BufferedDocOp doc = documents.get(documentId);
		BufferedDocOp updated = Composer.compose(doc, update);

		if (OpComparators.SYNTACTIC_IDENTITY.equal(updated, new DocOpBuffer().finish())) {
			documents.remove(documentId);
		} else {
			documents.put(documentId, updated);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.waveprotocol.wave.model.wave.data.WaveletData#removeParticipant(org.waveprotocol.wave.model.wave.ParticipantId
	 * )
	 */
	@Override
	public boolean removeParticipant(ParticipantId participant) {
		return participants.remove(participant);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.wave.IWavelet#getWaveletId()
	 */
	@Override
	public WaveletID getWaveletId() {
		return this.waveletId;
	}
}
