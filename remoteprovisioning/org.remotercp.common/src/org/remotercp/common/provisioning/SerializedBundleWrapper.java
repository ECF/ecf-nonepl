package org.remotercp.common.provisioning;

import java.io.Serializable;

public class SerializedBundleWrapper implements Serializable,
		Comparable<SerializedBundleWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8234041937066066069L;

	private long bundleId;

	private String symbolicName;

	private int state;

	public long getBundleId() {
		return bundleId;
	}

	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object bundle) {
		if (bundle instanceof SerializedBundleWrapper) {
			SerializedBundleWrapper bundleWrapper = (SerializedBundleWrapper) bundle;
			return this.getSymbolicName().equals(
					bundleWrapper.getSymbolicName());
		}
		return super.equals(bundle);
	}

	public int compareTo(SerializedBundleWrapper bundle) {
		return this.getSymbolicName().compareTo(bundle.getSymbolicName());
	}
}
