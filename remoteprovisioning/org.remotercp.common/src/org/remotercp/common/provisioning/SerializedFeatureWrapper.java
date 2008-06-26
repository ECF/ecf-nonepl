package org.remotercp.common.provisioning;

import java.net.URL;

public class SerializedFeatureWrapper implements
		SerializedWrapper<SerializedFeatureWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2616898190602839096L;

	private URL updateUrl;

	private String identifier;

	private String version;

	private String label;

	public URL getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(URL updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object feature) {
		if (feature instanceof SerializedFeatureWrapper) {
			SerializedFeatureWrapper wrapper = (SerializedFeatureWrapper) feature;
			return getIdentifier().equals(wrapper.getIdentifier());
		}
		return super.equals(feature);
	}

	public int compareTo(SerializedFeatureWrapper feature) {
		return getIdentifier().compareTo(feature.getIdentifier());
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
