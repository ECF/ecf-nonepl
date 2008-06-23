package org.remotercp.common.provisioning;

import java.io.Serializable;

public interface SerializedWrapper<T> extends Comparable<T>, Serializable {

	public String getIdentifier();

	public void setIdentifier(String identifier);

	public String getVersion();

	public void setVersion(String version);

}
