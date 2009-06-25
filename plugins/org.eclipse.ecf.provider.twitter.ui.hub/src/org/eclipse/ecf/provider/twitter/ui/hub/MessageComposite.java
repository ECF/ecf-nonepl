package org.eclipse.ecf.provider.twitter.ui.hub;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.internal.provider.twitter.search.TweetItem;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.ecf.provider.twitter.search.ITweetItem;
import org.eclipse.ecf.provider.twitter.ui.utils.ImageUtils;
import org.eclipse.ecf.provider.twitter.ui.utils.TwitterStringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import org.osgi.framework.Bundle;

import com.ocpsoft.pretty.time.PrettyTime;

public class MessageComposite implements MouseTrackListener, IHyperlinkListener, Listener
{

	private static PrettyTime timeFormatter = new PrettyTime();
	
	private FormToolkit toolkit; 
	private Composite composite; 
	
	//statically load the images for now.
	private static Image replyImg = ImageUtils.loadImage("reply.png");
	private static Image retweetImg = ImageUtils.loadImage("retweet.png");
	private static Image blankUserImg = ImageUtils.loadImage("blankUserImage.png");
	private Button reply; 
	private Button retweet;
	private IStatus message; 
	private ITweetItem searchResult;
	
	
	
	
	/**
	 * The set of details we display
	 */
	private String username;
	private String realName; 
	private String messageText;
	private Date messageCreation;
	private String userID;
	private String imagePath;
	private boolean addToTop;
	
	
	/**
	 * This version of the MessageComposite constructor takes an IStatus message to 
	 * display within the composite.
	 * @param parent
	 * @param style
	 * @param message
	 * @param toolkit
	 */
	public MessageComposite(Composite parent, int style, IStatus message,
					FormToolkit toolkit, boolean addToTop, MessageComposite referenceComposite, 
					int widthHint)
	{
		composite = toolkit.createComposite(parent, style);
		
		
		this.addToTop = addToTop;
		
		if(referenceComposite != null)
		{
			
			if(addToTop)
			{
			//	System.err.println("<move above>Trying to move " + message + " above " + referenceComposite.getText());
				composite.moveAbove(referenceComposite.getComposite());
			}
			else
			{	
				//otherwise default behaviour should be enough?
				//System.err.println("Oldest: " + message.getBody());
				//add it to the end.
				//composite.moveBelow(referenceComposite.getComposite());
			}
		}
//		else
//		{
//	//		System.err.println("reference composite is null");
//		}
//		
		this.message = message;
		this.toolkit = toolkit;
		
		extractMessageDetails(message);
		
		createContents();
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = widthHint;
		gd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gd);
		
		composite.addMouseTrackListener(this);
	}
	/**
	 * This version takes a TweetItem, from the Search API.
	 * @param parent
	 * @param style
	 * @param searchResult
	 * @param toolkit
	 */
	public MessageComposite(Composite parent, int style, ITweetItem searchResult, FormToolkit toolkit)
	{
		composite = toolkit.createComposite(parent, style);
		
		
		//this.message = message;
		this.searchResult = searchResult;
		this.toolkit = toolkit;
		extractMessageDetails(searchResult);
		createContents();
		composite.addMouseTrackListener(this);
	}

	public IStatus getMessage()
	{
		return message;
	}
	public String getText()
	{
		return messageText;
	}
	
	public Composite getComposite()
	{
		return composite;
	}
	
	/**
	 * Extract the details for display from this message.
	 * 
	 * @param message
	 */
	private void extractMessageDetails(ITweetItem message)
	{
		/**
		 * Is there a way to seperate the user's real name 
		 * and screenname in the search api.
		 * 
		 */
		if(message.getFromUser() != null)
		{
			realName = message.getFromUser();
			username = message.getFromUser();
		}
		

		//get the message timestamp
		messageCreation = message.getCreatedAt();
		//user id 
		//userID = message.getUser().getID().getName();
		userID = message.getFromUser();
		//message text
		messageText = message.getText();
		//the path to the user's image.
		imagePath = message.getProfileImageUrl();
	}
	

	/**
	 * Extract the details for display from this message.
	 * 
	 * @param message
	 */
	private void extractMessageDetails(IStatus message)
	{
		/**
		 * Get the user's screenname, and real name
		 * (if possible)
		 */
		if(message.getUser() != null)
		{
			realName = message.getUser().getName();
		}
		if(message.getUser().getProperties().get("screenName") != null)
		{
			username = (String)message.getUser().getProperties().get("screenName");
		}
		else
		{
			if(realName !=null)
			{
				username = realName;
			}
			else
			{
				username = "Unknown";
			}
		}
		//get the message timestamp
		messageCreation = message.getCreatedAt();
		//user id 
		userID = message.getUser().getID().getName();
		//message text
		messageText = message.getText();
		//the path to the user's image.
		imagePath = (String)message.getUser().getProperties().get("image");
	}
	
	
	private void createContents()
	{
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;//was 2
		composite.setLayout(layout);
		/**
		 * JS - my preference is to show the user's nickname rather than their real 
		 * name
		 */
		StyledText nameLabel = new StyledText(composite, SWT.WRAP | SWT.MULTI);
		
		//SimpleDateFormat format = new SimpleDateFormat("hh:mm a MMM d");
		String timestamp = timeFormatter.format(messageCreation);
		
		if(realName == null || (realName.equals(username)))
		{
			nameLabel.setText(username + " - " + timestamp);
		}
		else
		{
			nameLabel.setText(username + "(" + realName + ") - " + timestamp);
		}
		
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
		
//		if(message.getUser() != null && message.getUser().getProperties().get("image") != null)
//		{
			//Get user image from cache
			Image userImage = TwitterCache.getUserImage(userID);
			if (userImage!=null) {
				imageLabel.setImage(userImage);
			} else {
				//if not cached => queue label for image
				imageLabel.setImage(blankUserImg);
				TwitterCache.queueMessageForUserImageLoading(userID, imagePath, imageLabel);
			}
//		}
		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		/**
		 * Display the status of this twitter message
		 */
		String msgTxt = TwitterStringUtils.decorateUserTags(messageText);
		//TODO: do this better.?? 
		msgTxt = "<form><p>"+msgTxt+"</p></form>";
		FormText statusTxt = toolkit.createFormText(composite,false);
		statusTxt.addMouseTrackListener(this);
		statusTxt.addHyperlinkListener(this);
		statusTxt.setParagraphsSeparated(true);
		statusTxt.setLayoutData(td);
		try
		{
			statusTxt.setText(msgTxt, true, true);
		} 
		catch(IllegalArgumentException iae)
		{
			//form text was invalid - so just use plain
			statusTxt.setText(messageText, false, true);
		}
	}
	
	
	
	
	
	public void mouseEnter(MouseEvent e) {
		// TODO do nothing
		
	}

	public void mouseExit(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

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

	public void linkEntered(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void linkExited(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void handleEvent(Event event) {
		/**
		 * Find the tweet view part 
		 */
		TweetViewPart tweetView = 
			(TweetViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findViewReference(TweetViewPart.VIEW_ID).getView(false);
			
		
		
		if(event.widget.equals(reply))
		{
			//send a reply to this user 
			tweetView.setTweetText("@"+username);
		}
		
		if(event.widget.equals(retweet))
		{
			StringBuffer rtBuffer = new StringBuffer();
			rtBuffer.append("RT @");
			rtBuffer.append(username);
			rtBuffer.append(" ");
			rtBuffer.append(messageText);
			
			tweetView.setTweetText(rtBuffer.toString());
		}
		
	}


	
	private static void main(String[] args)
	{
		
	}
	
}
