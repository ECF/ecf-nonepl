package org.eclipse.ecf.provider.twitter.ui.hub;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.ecf.provider.twitter.container.TwitterUser;
import org.eclipse.ecf.provider.twitter.ui.logic.TwitterController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

public class FriendsViewPart extends ViewPart implements  Observer, MouseTrackListener, Listener {

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.friendsView";
	private Composite formComposite;
	
	private TwitterUser[] friends;
	
	
	private ScrolledForm form;
	private FormToolkit toolkit;
	private TwitterController controller;
	
	
	private Shell tip = null;
	
	
	public FriendsViewPart() {
		// TODO Auto-generated constructor stub
	}

	
	public void addController(TwitterController controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		form.setText("Your Friends");
		
		formComposite = form.getBody();
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		formComposite.setLayout(layout);


	}

	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void addFriends(TwitterUser[] friends)
	{
		this.friends = friends;
		for(int i =0; i < friends.length; i++)
		{
			addUserToView(friends[i]);
		}
		form.reflow(true);
		form.redraw();
	}
	
		
	
	private void addUserToView(TwitterUser user)
	{
		
		Composite composite = toolkit.createComposite(formComposite, SWT.NONE );
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		
		Label imageLabel = toolkit.createLabel(composite,"");
		imageLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT,TableWrapData.TOP, 1, 1));
		imageLabel.setData(user);
		imageLabel.addMouseTrackListener(this);
		
		
		if(user.getProperties().get("image") != null)
		{
		InputStream is = null;
		try {
			
			URL url = new URL((String)user.getProperties().get("image"));
			is = url.openStream();
			//TODO: get a resized version of the image, and make this a lot more efficient
			Image image = new Image(Display.getCurrent(), is);
			image = new Image(Display.getCurrent(),image.getImageData().scaledTo(50, 50));
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

		
		String username =  user.getName();
		StyledText nameLabel = new StyledText(composite, SWT.WRAP | SWT.MULTI);
		nameLabel.setText(username);
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = username.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		nameLabel.setStyleRange(styleRange);
		nameLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1));
		
		
	
		
		
	}

	
	/**
	 * Process an update from the Observable (TwitterController)
	 * @Override
	 */
	public void update(Observable o, Object arg) 
	{
		// TODO Auto-generated method stub
		
	}

	

	public void mouseEnter(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() instanceof Label)
		{
			Label l = (Label)e.getSource();
			TwitterUser user = (TwitterUser)l.getData();
			
			
			Display display = Display.getCurrent();
			
				if (tip != null  && !tip.isDisposed ()) 
					tip.dispose ();
				
				
				tip = new Shell (l.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
				tip.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
				FillLayout layout = new FillLayout ();
				layout.marginWidth = 2;
				tip.setLayout (layout);
				
				
				
				FormText text = new FormText(tip,SWT.WRAP);
				
//				label = new Label (tip, SWT.NONE);
				text.setForeground (display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
				text.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
				//label.setData ("_TABLEITEM", item);
				
				
				/**
				 * Add all profile data to this
				 */
				
				String bio = (String)user.getProperties().get("description");
				String location = (String)user.getProperties().get("location");
				String url = (String)user.getProperties().get("url");
				
				StringBuffer textBuffer = new StringBuffer();
				textBuffer.append("<form><p><b>Name:</b>");
				textBuffer.append(user.getName());
				textBuffer.append("</p>");
				if(bio != null)
				{
					textBuffer.append("<p><b>Bio:</b>");
					textBuffer.append(bio);
					textBuffer.append("</p>");
				}
				if(location != null)
				{
					textBuffer.append("<p><b>Location:</b>");
					textBuffer.append(location);
					textBuffer.append("</p>");
				}
				if(url != null)
				{
					textBuffer.append("<p><b>Web:</b> <a href='");
					textBuffer.append(url);
					textBuffer.append("'>");
					textBuffer.append(url);
					textBuffer.append("</a></p>");
				}
				textBuffer.append("</form>");
				text.setText(textBuffer.toString(), true, true);
				
				
				
				text.addListener (SWT.MouseExit, this);
				text.addListener (SWT.MouseDown, this);
				
				
				Point size = tip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
				Rectangle rect = l.getBounds ();
				Point pt = l.toDisplay (rect.x, rect.y);
				tip.setBounds (pt.x, pt.y, size.x, size.y);
				tip.setVisible (true);
			
		}
		
	}

	
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		if(tip != null && !tip.isDisposed())
			tip.dispose();
		
	}


	public void mouseExit(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
