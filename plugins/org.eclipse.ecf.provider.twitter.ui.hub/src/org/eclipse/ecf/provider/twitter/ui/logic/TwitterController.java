package org.eclipse.ecf.provider.twitter.ui.logic;

import java.util.Observable;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.twitter.TwitterMessageChatEvent;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.ecf.provider.twitter.container.TwitterContainer;
import org.eclipse.ecf.provider.twitter.container.TwitterUser;
import org.eclipse.ecf.provider.twitter.ui.hub.FriendsViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.MessagesViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.TweetViewPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class TwitterController extends Observable implements IIMMessageListener, IContainerListener{
	
	private TwitterContainer container;
	TwitterUser connectedUser;
	
	
	private TwitterUser[] friendsList;
	
	private FriendsViewPart friendsView;
	private TweetViewPart tweetView;
	private Display display;
	
	/**
	 * Add a list of observers.
	 */
	public TwitterController()
	{
		super();
		display = PlatformUI.getWorkbench().getDisplay();
		addObservers();
	}
	
	private void addObservers()
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		/**
		 * Get all the views.
		 */
		IViewReference[] views = workbench.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for(int i =0; i < views.length; i++)
		{
			/**
			 * Currently we only want the messages view to observe the controller.
			 */
			if( views[i].getId().equals(MessagesViewPart.VIEW_ID))
			{
				MessagesViewPart messagesView = (MessagesViewPart)views[i].getPart(true);
				this.addObserver(messagesView);
			}
			if( views[i].getId().equals(FriendsViewPart.VIEW_ID))
			{
				friendsView = (FriendsViewPart)views[i].getPart(true);
				friendsView.addController(this);
			}
			if( views[i].getId().equals(TweetViewPart.VIEW_ID))
			{
				tweetView = (TweetViewPart)views[i].getPart(true);
				tweetView.setController(this);
			}
			
		}
		
		
		
	}
	
	
	
	
	public boolean addContainer(IContainer container)
	{
		if(container instanceof TwitterContainer)
		{
			this.container = (TwitterContainer) container;
			//System.err.println("Container Added");
			return true;
		}
		return false;
	}
	
	
	boolean stopTrying = false;

	public void handleMessageEvent(IIMMessageEvent messageEvent) 
	{

		if(messageEvent instanceof IChatMessageEvent)
		{
			
			if(friendsList == null && !stopTrying)
			{
				try {
					friendsList = this.container.getTwitterUsersFromFriends();
					updateFriendsList();
	
				} catch (ECFException e) {
					stopTrying = true;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			final TwitterMessageChatEvent msg = (TwitterMessageChatEvent)messageEvent;
			//System.err.println("Getting User");
			//final TwitterUser user = findUserWithID(msg.getFromID());
		//	System.err.println("Got User");
			super.setChanged();
			//msg.getChatMessage().getBody();
			if(!display.isDisposed())
			{
				display.syncExec(new Runnable() {
					public void run() {
						notifyObservers((IStatus) msg.getChatMessage());	
					}
				});
			}
			
			
		}
		
	}
	
	
	public void tweet(String message) throws ECFException
	{
		container.sendStatusUpdate(message);
	}

	
	
	private TwitterUser findUserWithID(ID id)
	{
		/**
		 * First, check if that user is you!
		 */
		try 
		{
			if(connectedUser == null)
			{
				connectedUser = container.getConnectedUser();
			}
			if(connectedUser!=null && connectedUser.getID().getName().equals(id.getName()))
			{
				return connectedUser;
			}
		} catch (ECFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(friendsList != null)
		{
			for(int i = 0; i < friendsList.length; i++)
			{
				if(friendsList[i].getID().getName().equals(id.getName()))
				{
					return friendsList[i];
				}
			}
		}
		return null;
	}



	public void handleEvent(IContainerEvent event) {
		if (event instanceof IContainerConnectedEvent) 
		{
			/**
			 * If we haven't got the friendsList yet, now is the time to 
			 * update it.
			 */
			if(friendsList == null)
			{	
				try 
				{
					friendsList = this.container.getTwitterUsersFromFriends();
					updateFriendsList();
	
					
					
				} catch (ECFException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

	
	
	/**
	 * Update the friends list view. 
	 * This has to be threaded off as it can take a signifigant amount of time.
	 */
	private void updateFriendsList()
	{
		Thread thread = new Thread(new FriendsViewRunnable());

		/**
		 * ToDo: add this back in
		 */
		
		//	thread.start();
	}
	
	
	/**
	 * Thread to add the list of Friends to the Twitter view.
	 * @author jsugrue
	 */
	class FriendsViewRunnable implements Runnable
	{
		public void run()
		{
			display.asyncExec(new Runnable() 
			{
				public void run() 
				{	System.err.println("Number of friends: " + friendsList);
						friendsView.addFriends(friendsList);
				}});
		}
	}
	
	
	

}
