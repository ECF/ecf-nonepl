/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.wave;

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.sync.ModelUpdateException;
import org.eclipse.ecf.wave.document.IBufferedDocumentOperation;

public interface IWavelet {

	public Map<ID,IBufferedDocumentOperation> getDocuments();
	
	public ID[] getParticipants();
	
	public boolean addParticipant(ID participantID);
	public boolean removeParticipant(ID paricipantID);
	
	public boolean modifyDocument(ID documentID, IBufferedDocumentOperation operation) throws ModelUpdateException;
	
}
