package org.remotercp.serviceprovider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.core.util.ECFException;
import org.remotercp.authorization.domain.service.IAuthorizationService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.preferences.domain.IRemotePreferenceService;
import org.remotercp.provisioning.domain.service.IInstallFeaturesService;

public class RemoteServiceProvider {

	private ISessionService sessionService;

	public void bindSessionService(ISessionService sessionService) {
		this.sessionService = sessionService;
		System.out.println("RemoteServiceProvider.bindSessionService()");

		connect();
	}

	private void connect() {

		try {

			PropertyResourceBundle properties = new PropertyResourceBundle(
					FileLocator.openStream(Activator.getBundleContext()
							.getBundle(), new Path("server.properties"), false));

			String server = properties.getString("org.remotercp.server");
			String userName = properties.getString("org.remotercp.username");
			String password = properties.getString("org.remotercp.password");

			sessionService.connect(userName, password, server);

		} catch (ECFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void unbindSessionService(ISessionService sessionService) {
		this.sessionService = sessionService;

	}

	public void bindInstallFeatureService(
			IInstallFeaturesService installFeatureService) {
		System.out.println("RemoteServiceProvider.bindInstallFeatureService()");

		if (sessionService != null) {
			sessionService.registerRemoteService(
					IInstallFeaturesService.class.getName(),
					installFeatureService, null);
		}

	}

	public void bindAuthorizationService(
			IAuthorizationService authorizationService) {
		System.out.println("RemoteServiceProvider.bindAuthorizationService()");

		if (sessionService != null) {
			sessionService.registerRemoteService(
					IAuthorizationService.class.getName(),
					authorizationService, null);
		}
	}

	public void bindPreferenceService(IRemotePreferenceService preferenceService) {
		System.out.println("RemoteServiceProvider.bindPreferenceService()");

		if (sessionService != null) {
			sessionService.registerRemoteService(
					IRemotePreferenceService.class.getName(),
					preferenceService, null);
		}

	}

}
