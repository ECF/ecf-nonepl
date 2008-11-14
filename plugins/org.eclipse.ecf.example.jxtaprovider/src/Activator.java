import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class Activator extends Plugin {

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		final JxtaEcfClient jc=new JxtaEcfClient();
		
	}

}
