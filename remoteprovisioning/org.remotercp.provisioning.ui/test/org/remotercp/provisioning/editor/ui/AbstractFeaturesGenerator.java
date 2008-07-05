package org.remotercp.provisioning.editor.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.remotercp.common.provisioning.SerializedFeatureWrapper;

public abstract class AbstractFeaturesGenerator {

	private final static String URL = "http://eugenda.eu.funpic.de/upload/";

	public SerializedFeatureWrapper getFeaturesWrapper(int id, String name,
			String identyfier, String version) {
		SerializedFeatureWrapper feature = new SerializedFeatureWrapper();
		feature.setIdentifier(identyfier);
		feature.setLabel(name);
		feature.setVersion(version);
		try {
			feature.setUpdateUrl(new URL(URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return feature;

	}
}
