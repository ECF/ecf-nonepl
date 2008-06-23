package org.remotercp.common.provisioning;

import java.net.URL;

public class SerializedFeatureWrapper {

	private URL updateUrl;

	private String label;
	
	private String version;

	public URL getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(URL updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getLabel() {
		return label;
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

}
