package org.eclipse.ecf.provider.twitter.ui.hub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;


public class MessagesViewPart extends ViewPart implements Observer, IHyperlinkListener {
	
	
	private ArrayList<Long> previousMessages;

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.messagesView";
	//for sorting 
	private Date newestMessageDate;
	private Date oldestMessageDate;
	
	private MessageComposite oldestMessage = null;
	private MessageComposite newestMessage = null;
	
	
	private Composite formComposite;
	private ScrolledForm form; 
	private FormToolkit toolkit;
	
	
	private BrowserViewPart browser;
	
	private Shell tip;

	private SortedMap<Long,MessageComposite> sortedMessages;
	
	public MessagesViewPart() {
		
		previousMessages = new ArrayList<Long>();
		sortedMessages = new TreeMap<Long, MessageComposite>();
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		toolkit = new FormToolkit(parent.getDisplay());
		//FraGuid
		//form = toolkit.createScrolledForm(parent);
		form = new ScrolledForm(parent, SWT.V_SCROLL |  Window.getDefaultOrientation());
		form.setExpandVertical(true);
		form.setBackground(toolkit.getColors().getBackground());
		form.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		form.setFont(JFaceResources.getHeaderFont());

		form.setText("Your Messages");
		//FraGuid
		//GridLayout layout = new GridLayout();
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		
		
		
		//RowLayout layout = new RowLayout(SWT.VERTICAL);
		//TableWrapLayout layout = new TableWrapLayout();
//		FormLayout layout = new FormLayout();
		//GridLayout layout = new GridLayout();
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
	
	private boolean checkRepeat(long id)
	{
		if(previousMessages.contains(id))
		{
			return true;
		}
		previousMessages.add(id);
		return false;
		
	}

	/**
	 * Updates the view with the latest message
	 * Determines if it's a repeat, and where it should appear in the timeline.
	 */
	public void update(Observable o, Object arg) {
		IStatus message = (IStatus)arg;
		boolean seenAlready  = checkRepeat(message.getId());
		
		//either add to the top of the latest, or at the bottom. 
		//bottom is default.
		boolean addToTop = true;
		//check if we've seen this message already (by id?) 
		//if so drop it.
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a MMM d");
		
		if(!seenAlready)
		{
			if(newestMessageDate == null && oldestMessageDate == null)
			{
				newestMessageDate = message.getCreatedAt();
				oldestMessageDate = message.getCreatedAt();
				
			}
			else
			{
				//if this is a newer message.
				//if(message.getCreatedAt().after(newestMessageDate))
//				System.err.println("-------");
//				System.err.println("Before Date Compare....");
//				System.err.println("Incoming message date from " + message.getUser().getName() + " is " + format.format(message.getCreatedAt()) + " or "  + message.getCreatedAt());
//				System.err.println("Previous new message date from " + newestMessage.getMessage().getUser().getName() + " is " + format.format(newestMessage.getMessage().getCreatedAt()) + " or "  + newestMessage.getMessage().getCreatedAt());
//				System.err.println("-------");
				Calendar incoming = GregorianCalendar.getInstance();
				incoming.setTime(message.getCreatedAt());
				
				Calendar newest = GregorianCalendar.getInstance();
				newest.setTime(newestMessageDate);
				if(incoming.after(newest))
				{
					newestMessageDate = message.getCreatedAt();
					addToTop = true;
				}
				Calendar oldest = GregorianCalendar.getInstance();
				oldest.setTime(oldestMessageDate);
				
				if(incoming.before(oldest))	
				{
					oldestMessageDate = message.getCreatedAt();
					addToTop = false;
				}
			}
			
			
			MessageComposite messageComposite;
			if(addToTop)
			{
				//System.err.println("<TOP>Should appear at top: " + message.getBody());
				messageComposite = new MessageComposite(formComposite,SWT.NONE, message, toolkit, addToTop, newestMessage);
				
				//SimpleDateFormat format = new SimpleDateFormat("hh:mm a MMM d");
				//System.err.println("TOP Message date is now : "  + format.format(message.getCreatedAt()));
//				if(newestMessage != null)
//				{
//					System.err.println("TOP Message date WAS : "  + format.format(newestMessage.getMessage().getCreatedAt()));
//				}
//				System.err.println("New top composite is " + message.getBody());
				newestMessage = messageComposite;
				//this message can be the newest and oldest if it's the first.
				if(oldestMessage == null)
				{
					oldestMessage = messageComposite;
					
				}
			}
			else
			{
				//System.err.println("<END>Should move to bottom: " + message.getBody());
				messageComposite = new MessageComposite(formComposite,SWT.NONE, message, toolkit, addToTop,oldestMessage);
//				System.err.println("At the bottom : " + message.getBody());
				oldestMessage = messageComposite;
			}
			
			
			form.reflow(true);
			form.redraw();
		}
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
