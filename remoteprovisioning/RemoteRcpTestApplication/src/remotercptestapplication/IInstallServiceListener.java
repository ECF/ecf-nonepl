package remotercptestapplication;

import org.remotercp.provisioning.domain.service.IInstallFeaturesService;

public interface IInstallServiceListener {
	
	void bindInstallService(IInstallFeaturesService service);
	void unbindInstallService();

}
