package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.eclipse.ecf.services.quotes.QuoteService;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.swtdesigner.SWTResourceManager;

public class CopyOfActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/**
	 * Maps URL to images.
	 */
	private static Map<String, Image> imageMap = new HashMap<String, Image>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		CopyOfActivator.context = bundleContext;

		Executors.newSingleThreadExecutor().execute(new Runnable() {

			private ConsumerUI ui;

			@Override
			public void run() {

				ui = new ConsumerUI(null);
				addListener();
				ui.main(null);

			}

			private void addListener() {
				try {
					CopyOfActivator.context.addServiceListener(
							new ServiceListener() {

								@Override
								public void serviceChanged(
										final ServiceEvent event) {

//									Executors.newSingleThreadExecutor()
//											.execute(new Runnable() {
//
//												@Override
//												public void run() {
//													// TODO Auto-generated
//													// method stub

													if (event.getType() == ServiceEvent.REGISTERED) {

														try {
															Object obj = context
																	.getService(event
																			.getServiceReference());

															if (obj instanceof QuoteService) {
																final QuoteService service = (QuoteService) context
																		.getService(event
																				.getServiceReference());

																final String sLabel = service
																		.getServiceDescription();
																final String sQuote = service
																		.getRandomQuote();

																Display.getDefault()
																		.syncExec(
																				new Runnable() {

																					@Override
																					public void run() {
																						ui.getLabel()
																								.setText(
																										sLabel);
																						ui.getStyledText()
																								.setText(
																										sQuote);
																						ui.getDispatcher()
																								.setValue(
																										1);
																						ui.redraw();

																					}
																				});

															}
														} catch (final Exception e) {
															Display.getDefault()
																	.syncExec(
																			new Runnable() {

																				@Override
																				public void run() {
																					ui.getLabel()
																							.setText(
																									e.getLocalizedMessage());
																					ui.getStyledText()
																							.setText(
																									"");
																					ui.getDispatcher()
																							.setValue(
																									-1);
																					ui.redraw();

																				}
																			});
														}

													}
//												}
//											});
								}
							}, "(" + Constants.OBJECTCLASS + "="
									+ QuoteService.class.getName() + ")");
				} catch (InvalidSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		CopyOfActivator.context = null;
		for (Image image : imageMap.entrySet().toArray(new Image[0])) {
			image.dispose();
		}
		Display.getCurrent().dispose();
	}



}
