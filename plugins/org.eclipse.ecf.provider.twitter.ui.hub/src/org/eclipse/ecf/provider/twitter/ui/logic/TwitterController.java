package org.eclipse.ecf.provider.twitter.ui.logic;

import java.util.List;
import java.util.Observable;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.twitter.TwitterMessageChatEvent;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.search.ICriteria;
import org.eclipse.ecf.presence.search.ICriterion;
import org.eclipse.ecf.presence.search.IRestriction;
import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.ecf.presence.search.ISearch;
import org.eclipse.ecf.presence.search.message.IMessageSearchManager;
import org.eclipse.ecf.presence.search.message.MessageSearchException;
import org.eclipse.ecf.provider.twitter.container.IStatus;
import org.eclipse.ecf.provider.twitter.container.TwitterContainer;
import org.eclipse.ecf.provider.twitter.container.TwitterUser;
import org.eclipse.ecf.provider.twitter.ui.hub.FollowersViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.FriendsViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.MessagesViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.SearchViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.TweetViewPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class TwitterController extends Observable implements IIMMessageListener, IContainerListener{
	
	private TwitterContainer container;
	TwitterUser connectedUser;
	private IMessageSearchManager searchMgr;
	
	
	private TwitterUser[] friendsList;
	
	private List<IUser> followers;
	private List<IUser> following;
	
	
	private FriendsViewPart followingView;
	private FollowersViewPart followersView;
	
	
	private TweetViewPart tweetView;
	private SearchViewPart searchView;
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
				followingView = (FriendsViewPart)views[i].getPart(true);
				followingView.addController(this);
			}
			if( views[i].getId().equals(TweetViewPart.VIEW_ID))
			{
				tweetView = (TweetViewPart)views[i].getPart(true);
				tweetView.setController(this);
			}
			if(views[i].getId().equals(SearchViewPart.VIEW_ID))
			{
				searchView = (SearchViewPart)views[i].getPart(true);
				searchView.setController(this);
			}
			
			if(views[i].getId().equals(FollowersViewPart.VIEW_ID))
			{
				followersView = (FollowersViewPart)views[i].getPart(true);
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
			
//			if(friendsList == null && !stopTrying)
//			{
//				try {
//					friendsList = this.container.getTwitterUsersFromFriends();
//					updateFriendsList();
//	
//				} catch (ECFException e) {
//					stopTrying = true;
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
			final TwitterMessageChatEvent msg = (TwitterMessageChatEvent)messageEvent;
			//System.err.println("Getting User");
			//final TwitterUser user = findUserWithID(msg.getFromID());
		//	System.err.println("Got User");
			super.setChanged();
			
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
	
	/**
	 * TODO: We should make this constantly poll...
	 * @param query
	 * @throws MessageSearchException 
	 */
	public IResultList runSearch(String query) throws MessageSearchException
	{
		if(searchMgr == null)
		{
			searchMgr = container.getChatManager().getMessageSearchManager();
		}
		IRestriction term = searchMgr.createRestriction();
		ICriterion criterion = term.eq("", query);
		ICriteria criteria = searchMgr.createCriteria();
		criteria.add(criterion);
		
		ISearch searchResults = searchMgr.search(criteria);
		
		
		
		
		return searchResults.getResultList();
		
	}
	
	
	public void tweet(String message) throws ECFException
	{
		container.sendStatusUpdate(message);
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
					//friendsList = this.container.getTwitterUsersFromFriends();
					
					followers = this.container.getFollowers();
					following = this.container.getFollowing();
					
					updateFollowLists();
	
					
					
				} catch (ECFException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

	public String getUrlShorten(String url) throws ECFException {
		return container.getUrlShorten(url);
	}
	
	
	/**
	 * Update the friends list view. 
	 * This has to be threaded off as it can take a signifigant amount of time.
	 */
	private void updateFollowLists()
	{
		Thread followingThread = new Thread(new FriendsViewRunnable());
	
		Thread followersThread = new Thread(new FollowersViewRunnable());
		
		followingThread.start();
		followersThread.start();
		
	}
	
	
	/**
	 * Thread to add the list of Friends to the Twitter view.
	 * @author jsugrue
	 */
	class FriendsViewRunnable implements Runnable
	{
		public void run()
		{
			display.syncExec(new Runnable() 
			{
				public void run() 
				{	//System.err.println("Number of friends: " + friendsList.length);
						followingView.addFriends(following);
				}});
		}
	}
	
	class FollowersViewRunnable implements Runnable
	{
		public void run()
		{
			display.syncExec(new Runnable() 
			{
				public void run() 
				{	//System.err.println("Number of friends: " + friendsList.length);
						followersView.addFriends(followers);
				}});
		}
	}

}
