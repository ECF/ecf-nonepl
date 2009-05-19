package org.eclipse.ecf.provider.twitter.ui.hub;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.IViewDescriptor;
import org.osgi.framework.Bundle;

public class MessageComposite implements MouseTrackListener, IHyperlinkListener, Listener
{

	private IStatus message; 
	private FormToolkit toolkit; 
	private Composite composite; 
	
	//statically load the images for now.
	private static Image replyImg = loadImage("reply.png");
	private static Image retweetImg = loadImage("retweet.png");
	private Button reply; 
	private Button retweet;
	
	
	public MessageComposite(Composite parent, int style, IStatus message, FormToolkit toolkit)
	{
		composite = toolkit.createComposite(parent, style);
		
		this.message = message;
		this.toolkit = toolkit;
		createContents();
		
		composite.addMouseTrackListener(this);
	}
	
	public IStatus getMessage()
	{
		return message;
	}
	
	
	private void createContents()
	{
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;//was 2
		composite.setLayout(layout);
		String username;
		/**
		 * JS - my preference is to show the user's nickname rather than their real 
		 * name
		 */
		
		
		if(message.getUser().getProperties().get("screenName") != null)
		{
			username = (String)message.getUser().getProperties().get("screenName");
			
		}
		else
		{
			if(message.getUser()!=null)
			{
				username =  message.getUser().getName();
			}
			else
			{
				username = "Unknown";
			}
		}
		
		StyledText nameLabel = new StyledText(composite, SWT.WRAP | SWT.MULTI);
		
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a MMM d");
		String timestamp = format.format(message.getCreatedAt());
		
		nameLabel.setText(username + " - " + timestamp);
		
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = username.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		nameLabel.setStyleRange(styleRange);
		nameLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 3));//was 1,2
		

		/**
		 * Add action buttons for message
		 * Currently this include Reply and Retweet only
		 */
		TableWrapData td = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		Composite buttons = toolkit.createComposite(composite);
		buttons.setLayoutData(td);
		
		buttons.setLayout(new GridLayout());
		
		//two buttons (reply, favourite) and a drop down.... 
		reply = toolkit.createButton(buttons, "", SWT.FLAT);
		reply.setToolTipText("Reply");
		reply.addListener(SWT.Selection, this);
		reply.setImage(replyImg);
		
		retweet = toolkit.createButton(buttons, "", SWT.FLAT);
		retweet.setToolTipText("Retweet");
		retweet.setImage(retweetImg);
		retweet.addListener(SWT.Selection, this);
		
		/**
		 * Show the image for this user.
		 */
		final Label imageLabel = toolkit.createLabel(composite,"");
		imageLabel.addMouseTrackListener(this);
		
		if(message.getUser() != null && message.getUser().getProperties().get("image") != null)
		{
			new Job("Images"){
				protected org.eclipse.core.runtime.IStatus run(
						IProgressMonitor monitor) {
					InputStream is=null ;
					try{
						URL url = new URL((String)message.getUser().getProperties().get("image"));
						is = url.openStream();
						Image aux = new Image(Display.getCurrent(), is);
						final Image image = new Image(Display.getCurrent(),aux.getImageData().scaledTo(50, 50));
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
							public void run() {
								imageLabel.setImage(image);
							}
						});
					}catch (Exception e){
						   e.printStackTrace();
					} finally{
						if(is != null)
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					return Status.OK_STATUS;
				}
			}.schedule();
		}
		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		/**
		 * Display the status of this twitter message
		 */
		FormText statusTxt = toolkit.createFormText(composite,false);
		statusTxt.addMouseTrackListener(this);
		statusTxt.addHyperlinkListener(this);
		statusTxt.setParagraphsSeparated(true);
		statusTxt.setLayoutData(td);
		statusTxt.setText(message.getText(), false, true);
	}
	
	
	private static Image loadImage(String img)
	{
		Bundle bundle = Platform.getBundle("org.eclipse.ecf.provider.twitter.ui.hub"); // aka your plugin's id
		IPath imagePath = new Path("icons/" + img);
		URL imageUrl = Platform.find(bundle, imagePath);
		ImageDescriptor desc = ImageDescriptor.createFromURL(imageUrl);
		Image image = desc.createImage();
		return image;
	}
	
	
	

	@Override
	public void mouseEnter(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExit(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkActivated(HyperlinkEvent e) {
		//open the browser...
		try {
			PlatformUI.getWorkbench().getBrowserSupport().createBrowser("tweetbrowse")
				.openURL(new URL((String)e.getHref()));
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	@Override
	public void linkEntered(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkExited(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(Event event) {
		/**
		 * Find the tweet view part 
		 */
		TweetViewPart tweetView = 
			(TweetViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(TweetViewPart.VIEW_ID).getView(false);
			
		String screenName = (String)message.getUser().getProperties().get("screenName");
		
		if(event.widget.equals(reply))
		{
			//send a reply to this user 
			tweetView.setTweetText("@"+screenName);
		}
		
		if(event.widget.equals(retweet))
		{
			StringBuffer rtBuffer = new StringBuffer();
			rtBuffer.append("RT @");
			rtBuffer.append(screenName);
			rtBuffer.append(" ");
			rtBuffer.append(message.getBody());
			
			tweetView.setTweetText(rtBuffer.toString());
		}
		
	}


	
	
	
}
