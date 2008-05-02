/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.provider.aol.acc.container;

import com.aol.acc.AccAlert;
import com.aol.acc.AccAvManager;
import com.aol.acc.AccAvManagerProp;
import com.aol.acc.AccAvSession;
import com.aol.acc.AccAvStreamType;
import com.aol.acc.AccBartItem;
import com.aol.acc.AccBartItemProp;
import com.aol.acc.AccBuddyList;
import com.aol.acc.AccBuddyListProp;
import com.aol.acc.AccCustomSession;
import com.aol.acc.AccDirEntry;
import com.aol.acc.AccEvents;
import com.aol.acc.AccFileSharingItem;
import com.aol.acc.AccFileSharingSession;
import com.aol.acc.AccFileXfer;
import com.aol.acc.AccFileXferSession;
import com.aol.acc.AccGroup;
import com.aol.acc.AccGroupProp;
import com.aol.acc.AccIm;
import com.aol.acc.AccImInputState;
import com.aol.acc.AccImSession;
import com.aol.acc.AccInstance;
import com.aol.acc.AccInstanceProp;
import com.aol.acc.AccParticipant;
import com.aol.acc.AccParticipantProp;
import com.aol.acc.AccPluginInfo;
import com.aol.acc.AccPluginInfoProp;
import com.aol.acc.AccRateState;
import com.aol.acc.AccResult;
import com.aol.acc.AccSecondarySession;
import com.aol.acc.AccSecondarySessionState;
import com.aol.acc.AccSession;
import com.aol.acc.AccSessionProp;
import com.aol.acc.AccSessionState;
import com.aol.acc.AccStream;
import com.aol.acc.AccUser;
import com.aol.acc.AccUserProp;
import com.aol.acc.AccVariant;

/**
 *
 */
public class AccEventsListener implements AccEvents {

	AIMContainer container;

	public AccEventsListener(AIMContainer container) {
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#BeforeImReceived(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm)
	 */
	public void BeforeImReceived(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {
		System.out.println("BeforeImReceived" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#BeforeImSend(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm)
	 */
	public void BeforeImSend(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {
		System.out.println("BeforeImSend" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnAlertReceived(com.aol.acc.AccSession, com.aol.acc.AccAlert)
	 */
	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {
		System.out.println("OnAlertReceived" + " arg0=" + arg0 + ", arg1=" + arg1);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnAudioLevelChange(com.aol.acc.AccSession, com.aol.acc.AccAvSession, java.lang.String, int)
	 */
	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1, String arg2, int arg3) {
		System.out.println("OnAudioLevelChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnAvManagerChange(com.aol.acc.AccSession, com.aol.acc.AccAvManager, com.aol.acc.AccAvManagerProp, com.aol.acc.AccResult)
	 */
	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1, AccAvManagerProp arg2, AccResult arg3) {
		System.out.println("OnAvManagerChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnAvStreamStateChange(com.aol.acc.AccSession, com.aol.acc.AccAvSession, java.lang.String, com.aol.acc.AccAvStreamType, com.aol.acc.AccSecondarySessionState, com.aol.acc.AccResult)
	 */
	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1, String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4, AccResult arg5) {
		System.out.println("OnAvStreamStateChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4 + ", arg5=" + arg5);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnBartItemRequestPropertyResult(com.aol.acc.AccSession, com.aol.acc.AccBartItem, com.aol.acc.AccBartItemProp, int, com.aol.acc.AccResult, com.aol.acc.AccVariant)
	 */
	public void OnBartItemRequestPropertyResult(AccSession arg0, AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4, AccVariant arg5) {
		System.out.println("OnBartItemRequestPropertyResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4 + ", arg5=" + arg5);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnBuddyAdded(com.aol.acc.AccSession, com.aol.acc.AccGroup, com.aol.acc.AccUser, int, com.aol.acc.AccResult)
	 */
	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2, int arg3, AccResult arg4) {
		System.out.println("OnBuddyAdded" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnBuddyListChange(com.aol.acc.AccSession, com.aol.acc.AccBuddyList, com.aol.acc.AccBuddyListProp)
	 */
	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1, AccBuddyListProp arg2) {
		System.out.println("OnBuddyListChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnBuddyMoved(com.aol.acc.AccSession, com.aol.acc.AccUser, com.aol.acc.AccGroup, int, com.aol.acc.AccGroup, int, com.aol.acc.AccResult)
	 */
	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2, int arg3, AccGroup arg4, int arg5, AccResult arg6) {
		System.out.println("OnBuddyMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4 + ", arg5=" + arg5 + ", arg6=" + arg6);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnBuddyRemoved(com.aol.acc.AccSession, com.aol.acc.AccGroup, com.aol.acc.AccUser, com.aol.acc.AccResult)
	 */
	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2, AccResult arg3) {
		System.out.println("OnBuddyRemoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnChangesBegin(com.aol.acc.AccSession)
	 */
	public void OnChangesBegin(AccSession arg0) {
		System.out.println("OnChangesBegin" + " arg0=" + arg0);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnChangesEnd(com.aol.acc.AccSession)
	 */
	public void OnChangesEnd(AccSession arg0) {
		System.out.println("OnChangesEnd" + " arg0=" + arg0);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnConfirmAccountResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult)
	 */
	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {
		System.out.println("OnConfirmAccountResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnCustomDataReceived(com.aol.acc.AccSession, com.aol.acc.AccCustomSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm)
	 */
	public void OnCustomDataReceived(AccSession arg0, AccCustomSession arg1, AccParticipant arg2, AccIm arg3) {
		System.out.println("OnCustomDataReceived" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnCustomSendResult(com.aol.acc.AccSession, com.aol.acc.AccCustomSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm, com.aol.acc.AccResult)
	 */
	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4) {
		System.out.println("OnCustomSendResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnDeleteStoredImsResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult)
	 */
	public void OnDeleteStoredImsResult(AccSession arg0, int arg1, AccResult arg2) {
		System.out.println("OnDeleteStoredImsResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnDeliverStoredImsResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult)
	 */
	public void OnDeliverStoredImsResult(AccSession arg0, int arg1, AccResult arg2) {
		System.out.println("OnDeliverStoredImsResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnEjectResult(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, java.lang.String, int, com.aol.acc.AccResult)
	 */
	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {
		System.out.println("OnEjectResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnEmbedDownloadComplete(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccIm)
	 */
	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {
		System.out.println("OnEmbedDownloadComplete" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnEmbedDownloadProgress(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccIm, java.lang.String, com.aol.acc.AccStream)
	 */
	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4) {
		System.out.println("OnEmbedDownloadProgress" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnEmbedUploadComplete(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccIm)
	 */
	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {
		System.out.println("OnEmbedUploadComplete" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnEmbedUploadProgress(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccIm, java.lang.String, com.aol.acc.AccStream)
	 */
	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4) {
		System.out.println("OnBuddyMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileSharingRequestListingResult(com.aol.acc.AccSession, com.aol.acc.AccFileSharingSession, com.aol.acc.AccFileSharingItem, int, com.aol.acc.AccResult)
	 */
	public void OnFileSharingRequestListingResult(AccSession arg0, AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3, AccResult arg4) {
		System.out.println("OnBuddyMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileSharingRequestXferResult(com.aol.acc.AccSession, com.aol.acc.AccFileSharingSession, com.aol.acc.AccFileXferSession, int, com.aol.acc.AccFileXfer)
	 */
	public void OnFileSharingRequestXferResult(AccSession arg0, AccFileSharingSession arg1, AccFileXferSession arg2, int arg3, AccFileXfer arg4) {
		System.out.println("OnBuddyMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileXferCollision(com.aol.acc.AccSession, com.aol.acc.AccFileXferSession, com.aol.acc.AccFileXfer)
	 */
	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {
		System.out.println("OnEmbedUploadComplete" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileXferComplete(com.aol.acc.AccSession, com.aol.acc.AccFileXferSession, com.aol.acc.AccFileXfer, com.aol.acc.AccResult)
	 */
	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2, AccResult arg3) {
		System.out.println("OnCustomDataReceived" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileXferProgress(com.aol.acc.AccSession, com.aol.acc.AccFileXferSession, com.aol.acc.AccFileXfer)
	 */
	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {
		System.out.println("OnFileXferProgress" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnFileXferSessionComplete(com.aol.acc.AccSession, com.aol.acc.AccFileXferSession, com.aol.acc.AccResult)
	 */
	public void OnFileXferSessionComplete(AccSession arg0, AccFileXferSession arg1, AccResult arg2) {
		System.out.println("OnFileXferSessionComplete" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnGroupAdded(com.aol.acc.AccSession, com.aol.acc.AccGroup, int, com.aol.acc.AccResult)
	 */
	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2, AccResult arg3) {
		System.out.println("OnGroupAdded" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnGroupChange(com.aol.acc.AccSession, com.aol.acc.AccGroup, com.aol.acc.AccGroupProp)
	 */
	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {
		System.out.println("OnGroupChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnGroupMoved(com.aol.acc.AccSession, com.aol.acc.AccGroup, int, int, com.aol.acc.AccResult)
	 */
	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2, int arg3, AccResult arg4) {
		System.out.println("OnGroupMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnGroupRemoved(com.aol.acc.AccSession, com.aol.acc.AccGroup, com.aol.acc.AccResult)
	 */
	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {
		System.out.println("OnGroupRemoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnIdleStateChange(com.aol.acc.AccSession, int)
	 */
	public void OnIdleStateChange(AccSession arg0, int arg1) {
		System.out.println("OnIdleStateChange" + " arg0=" + arg0 + ", arg1=" + arg1);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnImReceived(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm)
	 */
	public void OnImReceived(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {
		System.out.println("OnImReceived" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnImSendResult(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm, com.aol.acc.AccResult)
	 */
	public void OnImSendResult(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4) {
		System.out.println("OnGroupMoved" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnImSent(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccParticipant, com.aol.acc.AccIm)
	 */
	public void OnImSent(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {
		System.out.println("OnImSent" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnInputStateChange(com.aol.acc.AccSession, com.aol.acc.AccImSession, java.lang.String, com.aol.acc.AccImInputState)
	 */
	public void OnInputStateChange(AccSession arg0, AccImSession arg1, String arg2, AccImInputState arg3) {
		System.out.println("OnInputStateChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnInstanceChange(com.aol.acc.AccSession, com.aol.acc.AccInstance, com.aol.acc.AccInstance, com.aol.acc.AccInstanceProp)
	 */
	public void OnInstanceChange(AccSession arg0, AccInstance arg1, AccInstance arg2, AccInstanceProp arg3) {
		System.out.println("OnInstanceChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnInviteResult(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, java.lang.String, int, com.aol.acc.AccResult)
	 */
	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {
		System.out.println("OnInviteResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnLocalImReceived(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccIm)
	 */
	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2) {
		System.out.println("OnLocalImReceived" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnLookupUsersResult(com.aol.acc.AccSession, java.lang.String[], int, com.aol.acc.AccResult, com.aol.acc.AccUser[])
	 */
	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2, AccResult arg3, AccUser[] arg4) {
		System.out.println("OnLookupUsersResult" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnNewFileXfer(com.aol.acc.AccSession, com.aol.acc.AccFileXferSession, com.aol.acc.AccFileXfer)
	 */
	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {
		System.out.println("OnNewFileXfer" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnNewSecondarySession(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, int)
	 */
	public void OnNewSecondarySession(AccSession arg0, AccSecondarySession arg1, int arg2) {
		System.out.println("OnNewSecondarySession" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnParticipantChange(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, com.aol.acc.AccParticipant, com.aol.acc.AccParticipant, com.aol.acc.AccParticipantProp)
	 */
	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {
		System.out.println("OnParticipantChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnParticipantJoined(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, com.aol.acc.AccParticipant)
	 */
	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2) {
		System.out.println("OnParticipantJoined" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnParticipantLeft(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, com.aol.acc.AccParticipant, com.aol.acc.AccResult, java.lang.String, java.lang.String)
	 */
	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccResult arg3, String arg4, String arg5) {
		System.out.println("OnParticipantLeft" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4 + ", arg5=" + arg5);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnPluginChange(com.aol.acc.AccSession, com.aol.acc.AccPluginInfo, com.aol.acc.AccPluginInfoProp)
	 */
	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1, AccPluginInfoProp arg2) {
		System.out.println("OnPluginChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnPluginUninstall(com.aol.acc.AccSession, com.aol.acc.AccPluginInfo)
	 */
	public void OnPluginUninstall(AccSession arg0, AccPluginInfo arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnPreferenceChange(com.aol.acc.AccSession, java.lang.String, com.aol.acc.AccResult)
	 */
	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnPreferenceInvalid(com.aol.acc.AccSession, java.lang.String, com.aol.acc.AccResult)
	 */
	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnPreferenceResult(com.aol.acc.AccSession, java.lang.String, int, java.lang.String, com.aol.acc.AccResult)
	 */
	public void OnPreferenceResult(AccSession arg0, String arg1, int arg2, String arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnRateLimitStateChange(com.aol.acc.AccSession, com.aol.acc.AccImSession, com.aol.acc.AccRateState)
	 */
	public void OnRateLimitStateChange(AccSession arg0, AccImSession arg1, AccRateState arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnReportUserResult(com.aol.acc.AccSession, com.aol.acc.AccUser, int, com.aol.acc.AccResult, int, int)
	 */
	public void OnReportUserResult(AccSession arg0, AccUser arg1, int arg2, AccResult arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnRequestServiceResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult, java.lang.String, int, byte[])
	 */
	public void OnRequestServiceResult(AccSession arg0, int arg1, AccResult arg2, String arg3, int arg4, byte[] arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnRequestSummariesResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult, com.aol.acc.AccVariant)
	 */
	public void OnRequestSummariesResult(AccSession arg0, int arg1, AccResult arg2, AccVariant arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSearchDirectoryResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult, com.aol.acc.AccDirEntry)
	 */
	public void OnSearchDirectoryResult(AccSession arg0, int arg1, AccResult arg2, AccDirEntry arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSecondarySessionChange(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, int)
	 */
	public void OnSecondarySessionChange(AccSession arg0, AccSecondarySession arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSecondarySessionStateChange(com.aol.acc.AccSession, com.aol.acc.AccSecondarySession, com.aol.acc.AccSecondarySessionState, com.aol.acc.AccResult)
	 */
	public void OnSecondarySessionStateChange(AccSession arg0, AccSecondarySession arg1, AccSecondarySessionState arg2, AccResult arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSendInviteMailResult(com.aol.acc.AccSession, int, com.aol.acc.AccResult)
	 */
	public void OnSendInviteMailResult(AccSession arg0, int arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSessionChange(com.aol.acc.AccSession, com.aol.acc.AccSessionProp)
	 */
	public void OnSessionChange(AccSession arg0, AccSessionProp arg1) {
		System.out.println("OnSessionChange" + " arg0=" + arg0 + ", arg1=" + arg1);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnSoundEffectReceived(com.aol.acc.AccSession, com.aol.acc.AccAvSession, java.lang.String, java.lang.String)
	 */
	public void OnSoundEffectReceived(AccSession arg0, AccAvSession arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnStateChange(com.aol.acc.AccSession, com.aol.acc.AccSessionState, com.aol.acc.AccResult)
	 */
	public void OnStateChange(AccSession arg0, AccSessionState arg1, AccResult arg2) {
		System.out.println("OnStateChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnUserChange(com.aol.acc.AccSession, com.aol.acc.AccUser, com.aol.acc.AccUser, com.aol.acc.AccUserProp, com.aol.acc.AccResult)
	 */
	public void OnUserChange(AccSession arg0, AccUser arg1, AccUser arg2, AccUserProp arg3, AccResult arg4) {
		System.out.println("OnUserChange" + " arg0=" + arg0 + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4);
	}

	/* (non-Javadoc)
	 * @see com.aol.acc.AccEvents#OnUserRequestPropertyResult(com.aol.acc.AccSession, com.aol.acc.AccUser, com.aol.acc.AccUserProp, int, com.aol.acc.AccResult, com.aol.acc.AccVariant)
	 */
	public void OnUserRequestPropertyResult(AccSession arg0, AccUser arg1, AccUserProp arg2, int arg3, AccResult arg4, AccVariant arg5) {
		// TODO Auto-generated method stub

	}

}
