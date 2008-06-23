package org.remotercp.common.provisioning;

import java.net.URL;

public class SerializedFeatureWrapper {

	private URL updateUrl;

	private String label;

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

}
