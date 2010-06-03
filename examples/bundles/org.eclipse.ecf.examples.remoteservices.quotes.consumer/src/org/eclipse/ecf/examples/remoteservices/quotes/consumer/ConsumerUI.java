package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainerInstantiator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.remainsoftware.osgilloscope.OSGilloscope;

public class ConsumerUI extends Shell {
	private Text servers;
	private Label label;
	private StyledText styledText;
	private OSGilloscope gilloscope;
	private Dispatcher dispatcher;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public void main(String args[]) {
		try {
			// Display display = Display.getDefault();
			// ConsumerUI shell = new ConsumerUI(display);
			getShell().open();
			getShell().layout();
			while (!getShell().isDisposed()) {
				if (!getDisplay().readAndDispatch()) {
					getDisplay().sleep();
				}
			}
			getDisplay().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public ConsumerUI(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		Label lblCommaSeperatedList = new Label(composite, SWT.NONE);
		lblCommaSeperatedList.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblCommaSeperatedList.setText("Servers");

		servers = (new Text(composite, SWT.BORDER));
		getServers().setText("yazafatutu.com");
		getServers().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnConnect = new Button(composite, SWT.NONE);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				startZookeeper();
			}
		});
		btnConnect.setText("Connect");

		gilloscope = new OSGilloscope(composite, SWT.NONE);
		gilloscope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				3, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Composite composite_1 = new Composite(composite, SWT.BORDER);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				3, 1));

		label = (new Label(composite_1, SWT.NONE));
		getLabel().setAlignment(SWT.CENTER);
		getLabel().setFont(new Font(null, "Segoe UI", 15, SWT.BOLD));
		getLabel().setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		getLabel().setText("New Label");

		Group group = new Group(composite_1, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		styledText = (new StyledText(group, SWT.BORDER | SWT.WRAP));
		getStyledText().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createContents();
	}

	protected void startZookeeper() {

		IContainer singleton = null;
		try {
			singleton = ContainerFactory.getDefault().createContainer(
					ZooDiscoveryContainerInstantiator.NAME);
		} catch (ContainerCreateException e1) {
		}

		// ZooDiscoveryContainer singleton =
		// ZooDiscoveryContainer.getSingleton();
		if (singleton.getConnectedID() != null)
			singleton.disconnect();

		try {
			singleton.connect(
					singleton.getConnectNamespace().createInstance(
							new String[] { "zoodiscovery.flavor.centralized="
									+ getServers().getText() }), null);
		} catch (Exception e) {
			getLabel().setText(e.getLocalizedMessage());
			getStyledText().setText(e.getCause().toString());
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("ECF Zookeeper Service Consumer Service");
		setSize(450, 381);

		dispatcher = new Dispatcher() {

			@Override
			public void setValue(int value) {
				if (isSoundRequired())
					clipper.playClip(getActiveSoundfile(), 0);

				getGilloscope().setValues(OSGilloscope.HEARTBEAT);

			}

			public OSGilloscope getGilloscope() {
				return gilloscope;
			};

			@Override
			public boolean isServiceActive() {
				return true;
			}
		};

		dispatcher.dispatch();

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Label getLabel() {
		return label;
	}

	public StyledText getStyledText() {
		return styledText;
	}

	public Text getServers() {
		return servers;
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}
}
