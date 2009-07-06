package org.eclipse.ecf.provider.twitter.ui.hub;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.ecf.provider.twitter.search.ITweetItem;
import org.eclipse.ecf.provider.twitter.ui.utils.ImageUtils;
import org.eclipse.ecf.provider.twitter.ui.utils.TwitterStringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
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

import com.ocpsoft.pretty.time.PrettyTime;

public class MessageComposite implements MouseTrackListener, IHyperlinkListener, Listener
{

	private static PrettyTime timeFormatter = new PrettyTime();
	
	private FormToolkit toolkit; 
	private Composite composite; 
	private Composite border;
	
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
	private FormText statusTxt;
	private StyledText nameLabel;
	private Composite buttons;
	private StyleRange styleRange;
	
	
	/**
	 * This version of the MessageComposite constructor takes an IStatus message to 
	 * display within the composite.
	 * @param parent
	 * @param style
	 * @param message
	 * @param toolkit
	 */
	public MessageComposite(Composite parent, int style, IStatus message,
					FormToolkit toolkit, boolean addToTop, MessageComposite referenceComposite)
	{
		//FraGuid
		border= new Composite(parent, SWT.NONE);
		border.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
		TableWrapLayout layout=new TableWrapLayout();
		border.setLayout(layout);
		layout.leftMargin=1;
		layout.rightMargin=1;
		layout.topMargin=1;
		layout.bottomMargin=1;
		toolkit.adapt(border);
		composite = toolkit.createComposite(border, style );
		
		if(referenceComposite != null)
		{
			
			if(addToTop)
			{
			//	System.err.println("<move above>Trying to move " + message + " above " + referenceComposite.getText());
				border.moveAbove(referenceComposite.getContainer());
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
		//FraGuid
		//GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = widthHint;
		//gd.grabExcessHorizontalSpace = true;
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1);
		composite.setLayoutData(td);
		
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
	
	public Composite getContainer()
	{
		return border;
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
		 * Show the image for this user.
		 */
		 //FraGuid
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
		TableWrapData td=new TableWrapData();
		td.rowspan=2;
		imageLabel.setLayoutData(td);
			
		/**
		 * JS - my preference is to show the user's nickname rather than their real 
		 * name
		 */
		nameLabel = new StyledText(composite, SWT.WRAP | SWT.MULTI|SWT.READ_ONLY);
		
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
		nameLabel.addMouseTrackListener(this);
		
		styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = username.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		nameLabel.setStyleRange(styleRange);
		td=new TableWrapData();
		nameLabel.setLayoutData(td);
		nameLabel.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		/**
		 * Add action buttons for message
		 * Currently this include Reply and Retweet only
		 */		
		buttons = toolkit.createComposite(composite);
//		buttons = new Composite(composite, SWT.BORDER);
		td=new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 2, 1);
		buttons.setLayoutData(td);
		buttons.addMouseTrackListener(this);
		buttons.setLayout(new GridLayout(1,false));
		
		reply = toolkit.createButton(buttons, "", SWT.FLAT);
		reply.setToolTipText("Reply");
		reply.addListener(SWT.Selection, this);
		reply.setImage(replyImg);
		reply.addMouseTrackListener(this);
		reply.setLayoutData(new GridData(SWT.RIGHT,SWT.NONE,true,false));
		reply.setVisible(false);
		
		retweet = toolkit.createButton(buttons, "", SWT.FLAT);
		retweet.setToolTipText("Retweet");
		retweet.setImage(retweetImg);
		retweet.addListener(SWT.Selection, this);
		retweet.addMouseTrackListener(this);
		retweet.setLayoutData(new GridData(SWT.RIGHT,SWT.NONE,true,false));
		retweet.setVisible(false);
		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		/**
		 * Display the status of this twitter message
		 */
		String msgTxt = TwitterStringUtils.decorateUserTags(messageText);
		//FraGuid
//		msgTxt = "<form><p>"+msgTxt+"</p></form>";
		statusTxt = toolkit.createFormText(composite,false);
		
		statusTxt.addMouseTrackListener(this);
		statusTxt.addHyperlinkListener(this);
		statusTxt.setParagraphsSeparated(true);
		statusTxt.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
		statusTxt.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
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
		//FraGuid
		Color backgroundColor=new Color(composite.getDisplay(), new RGB(245, 245, 245));
		border.setBackground(new Color(composite.getDisplay(), new RGB(235, 235, 235)));
		composite.setBackground(backgroundColor);
		statusTxt.setBackground(backgroundColor);
		nameLabel.setBackground(backgroundColor);
		buttons.setBackground(backgroundColor);
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		statusTxt.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		nameLabel.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		retweet.setVisible(true);
		reply.setVisible(true);
//		statusTxt.redraw();
	}

	public void mouseExit(MouseEvent e) {
		//FraGuid
		Color backgroundColor=new Color(composite.getDisplay(), new RGB(255,255, 255));
		border.setBackground(new Color(composite.getDisplay(), new RGB(255,255, 255)));
		composite.setBackground(backgroundColor);
		statusTxt.setBackground(backgroundColor);
		nameLabel.setBackground(backgroundColor);
		buttons.setBackground(backgroundColor);
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		nameLabel.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		statusTxt.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		retweet.setVisible(false);
		reply.setVisible(false);
//		statusTxt.redraw();
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
