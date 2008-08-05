package org.remotercp.provisioning.features;

import org.remotercp.util.serialize.SerializeUtil;

public class RemoteServiceTestImpl implements IRemoteServiceTest {

	public String getObject() {
		Exception exception = new Exception("Remote service exception");
		String remoteObject = null;

		remoteObject = SerializeUtil.convertObjectToXML(exception);

		return remoteObject;
	}

}
