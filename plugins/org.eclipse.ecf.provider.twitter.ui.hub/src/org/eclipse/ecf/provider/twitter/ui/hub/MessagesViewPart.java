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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
		
//		StringBuffer textBuffer = new StringBuffer();
//		textBuffer.append("<form><p><b>Name:</b>");
//		textBuffer.append("James");
//		textBuffer.append("</p><p><b>Bio:</b></p>");
//		textBuffer.append("<p><b>Location:</b></p>");
//		textBuffer.append("<p><b>Web:</b></p></form>");
//		
//		TwitterMessage message = new TwitterMessage(null, textBuffer.toString());
//		displayMessage(message);
		
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
		displayMessage(message);
		form.reflow(true);
		form.redraw();
		
	}
	
	private void displayMessage(IStatus message)
	{
		Composite composite = toolkit.createComposite(formComposite, SWT.NONE );
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		String username;
		if(message.getUser()!=null)
		{
			username =  message.getUser().getName();
		}
		else
		{
			username = "Unknown";
		}
		
		StyledText nameLabel = new StyledText(composite, SWT.WRAP | SWT.MULTI);
		nameLabel.setText(username);
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = username.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		nameLabel.setStyleRange(styleRange);
	
		nameLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 2));
		
		
//		statusTxt.setWordWrap(true);
//		
//		GridData gd = new GridData();
//		gd.horizontalSpan = 2;
//		nameLabel.setLayoutData(gd);
		
		Label imageLabel = toolkit.createLabel(composite,"");
		if(message.getUser() != null && message.getUser().getProperties().get("image") != null)
		{
		InputStream is = null;
		try {
			
			URL url = new URL((String)message.getUser().getProperties().get("image"));
			is = url.openStream();
			Image image = new Image(Display.getCurrent(), is);
			imageLabel.setImage(image);
			}
			catch (Exception e)
			{
			   e.printStackTrace();
			}
			finally
			{
			   try {
				if(is != null) is.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		
		/**
		 * Display the status of this twitter message
		 */
		FormText statusTxt = toolkit.createFormText(composite,false);
		statusTxt.addHyperlinkListener(this);
		statusTxt.setParagraphsSeparated(true);
		statusTxt.setLayoutData(td);
		//FIXME Put the timestamp in a better format
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a MMM d");
		String timestamp = format.format(message.getCreatedAt());
		statusTxt.setText(message.getText() + " - "+ timestamp, false, true);
		//statusTxt.setText(message.getMessage(), true, true);
		
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


	public void linkExited(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
