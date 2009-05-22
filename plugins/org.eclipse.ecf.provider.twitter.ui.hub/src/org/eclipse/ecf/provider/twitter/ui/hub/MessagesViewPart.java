package org.eclipse.ecf.provider.twitter.ui.hub;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;


public class MessagesViewPart extends ViewPart implements Observer, IHyperlinkListener {

	
	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.messagesView";
	
	private Composite formComposite;
	private ScrolledForm form; 
	private FormToolkit toolkit;
	
	
	private BrowserViewPart browser;
	
	private Shell tip;
	
	public MessagesViewPart() {
		
	
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Your Messages");
		TableWrapLayout layout = new TableWrapLayout();
	
		form.getBody().setLayout(layout);
		
		formComposite = form.getBody();
	
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		form.setFocus();
	}
	public void dispose()
	{
		toolkit.dispose();
		super.dispose();
	}

	public void update(Observable o, Object arg) {
		IStatus message = (IStatus)arg;
		MessageComposite messageComposite = new MessageComposite(formComposite,SWT.NONE, message, toolkit);
		form.reflow(true);
		form.redraw();
		
	}
	
	
	public void linkActivated(HyperlinkEvent e) {
		//open up the link in a browser window.
		
		String link = (String)e.getHref();
		
		//get the browser view. 
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			browser = (BrowserViewPart) workbench.getActiveWorkbenchWindow().getActivePage().showView(browser.VIEW_ID);
			browser.setURL(link);
		} 
		catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void linkEntered(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkExited(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	

}
