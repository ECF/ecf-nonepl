package org.eclipse.ecf.provider.twitter.ui.logic;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.ITypingMessageEvent;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.ecf.provider.twitter.container.TwitterContainer;
import org.eclipse.ecf.provider.twitter.container.TwitterUser;
import org.eclipse.ecf.provider.twitter.identity.TwitterID;
import org.eclipse.ecf.provider.twitter.ui.hub.FriendsViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.MessagesViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.TweetViewPart;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

public class TwitterController extends Observable implements IIMMessageListener, IContainerListener{
	
	private TwitterContainer container;
	TwitterUser connectedUser;
	
	
	private TwitterUser[] friendsList;
	
	private FriendsViewPart friendsView;
	private TweetViewPart tweetView;
	
	/**
	 * Add a list of observers.
	 */
	public TwitterController()
	{
		super();
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

	@Override
	public void handleMessageEvent(IIMMessageEvent messageEvent) 
	{
		//System.err.println("Message Received");
		if(messageEvent instanceof IChatMessageEvent)
		{
			
			if(friendsList == null && !stopTrying)
			{
				
				try {
					//System.err.println("Getting Friends");
					friendsList = this.container.getTwitterUsersFromFriends();
					//System.err.println("Got Friends");
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
					friendsView.addFriends(friendsList);
						}});
				} catch (ECFException e) {
					stopTrying = true;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			final IChatMessageEvent msg = (IChatMessageEvent)messageEvent;
			//System.err.println("Getting User");
			final TwitterUser user = findUserWithID(msg.getFromID());
		//	System.err.println("Got User");
			super.setChanged();
			//msg.getChatMessage().getBody();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					
					notifyObservers(new TwitterMessage(user, msg.getChatMessage().getBody()));	
				}
			});
			
			
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
		//container.get
		
		
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



	@Override
	public void handleEvent(IContainerEvent event) {
		if (event instanceof IContainerConnectedEvent) 
		{
	//		System.err.println("******CONNECTION MADE******");
			if(friendsList == null)
			{	
				try 
				{
			//		System.err.println("Getting Friends in handleEvent");
					friendsList = this.container.getTwitterUsersFromFriends();
				//	System.err.println("Got Friends in handleEvent");
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
					friendsView.addFriends(friendsList);
						}});
				} catch (ECFException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

	
	
	
	
	

}
