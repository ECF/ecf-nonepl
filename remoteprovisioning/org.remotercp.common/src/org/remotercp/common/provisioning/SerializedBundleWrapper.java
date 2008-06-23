package org.remotercp.common.provisioning;


public class SerializedBundleWrapper implements
		SerializedWrapper<SerializedBundleWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8234041937066066069L;

	private long bundleId;

	private String identifier;

	private int state;

	private String bundleVersion;

	public long getBundleId() {
		return bundleId;
	}

	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
			return this.getIdentifier().equals(bundleWrapper.getIdentifier());
		}
		return super.equals(bundle);
	}

	public int compareTo(SerializedBundleWrapper bundle) {
		return this.getIdentifier().compareTo(bundle.getIdentifier());
	}

	public String getVersion() {
		return bundleVersion;
	}

	public void setVersion(String version) {
		this.bundleVersion = version;
	}
}
