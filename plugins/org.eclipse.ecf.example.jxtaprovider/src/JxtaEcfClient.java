

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;

/**
 * 
 */
public class JxtaEcfClient implements Runnable {

	protected IContainer container;
	public void setContainer(IContainer container) {
		this.container = container;
	}
	public IContainer getContainer() {
		return container;
	}
	public void run() {
		try {
			// Create instance of trivial container
			container = ContainerFactory.getDefault().createContainer( "org.eclipse.ecf.provider.jxta" );
			System.out.println("container started...");
			// Get appropriate container adapter...e.g. IChannelContainerAdapter
			// IChannelContainerAdapter containerAdapter =
			// (IChannelContainerAdapter)
			// container.getAdapter(IChannelContainerAdapter.class);

			// Connect
//			ID targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), "myid");
//			container.connect(targetID, null);

		} catch (ECFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public JxtaEcfClient() {
		super();
	}
}
