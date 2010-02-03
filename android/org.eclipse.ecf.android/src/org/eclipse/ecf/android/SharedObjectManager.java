package org.eclipse.ecf.android;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

public class SharedObjectManager implements ISharedObjectManager {

	/**
	 * Use a bundle to store all parcelables
	 * Instances of Parcelable can be written to and restored from a parcel
	 */
	private final Bundle b = new Bundle();

	public ID addSharedObject(ID sharedObjectID, ISharedObject sharedObject,
			Map properties) throws SharedObjectAddException {
		
		b.putParcelable(sharedObjectID.getName(), sharedObject);
		return sharedObjectID;
	}

	public ISharedObjectConnector connectSharedObjects(ID sharedObjectFrom,
			ID[] sharedObjectsTo) throws SharedObjectConnectException {
		// TODO Auto-generated method stub
		return null;
	}

	public ID createSharedObject(SharedObjectDescription sd)
			throws SharedObjectCreateException {
		
		Object[] args= new Object[]{}; // TODO what are args ?
		SharedObjectTypeDescription soDesc = sd.getTypeDescription();
		ISharedObject so =  soDesc.getInstantiator().createInstance(soDesc, args);
		return sd.getID();
	}

	public void disconnectSharedObjects(ISharedObjectConnector connector)
			throws SharedObjectDisconnectException {
		// TODO Auto-generated method stub

	}

	public ISharedObject getSharedObject(ID sharedObjectID) {
		if( b.containsKey(sharedObjectID.getName()))
			return b.getParcelable(sharedObjectID.getName());
		else
			return null;
	}

	public List getSharedObjectConnectors(ID sharedObjectFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ID[] getSharedObjectIDs() {
		// TODO [pierre]
		return null;
	}

	public ISharedObject removeSharedObject(ID sharedObjectID) {
		ISharedObject removed = (ISharedObject) b.get(sharedObjectID.getName());
		b.remove(sharedObjectID.getName());
		return removed;
	}

	public void setRemoteAddPolicy(ISharedObjectPolicy policy) {
		// TODO Auto-generated method stub

	}
	
	

}
