package org.eclipse.ecf.provider.twitter.ui.hub.product;

import org.eclipse.ecf.provider.twitter.ui.hub.DirectMessagesViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.FriendsViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.MessagesViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.ReplyViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.SearchViewPart;
import org.eclipse.ecf.provider.twitter.ui.hub.TweetViewPart;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "org.eclipse.ecf.provider.twitter.ui.hub.perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		String editorArea = layout.getEditorArea();
		layout.addView(TweetViewPart.VIEW_ID, IPageLayout.TOP, 0.2f,editorArea);
		
		IFolderLayout friends = layout.createFolder("friendsFolder", IPageLayout.BOTTOM, 0.2f,TweetViewPart.VIEW_ID);
		friends.addView(FriendsViewPart.VIEW_ID);
		//layout.addView(FriendsViewPart.VIEW_ID, IPageLayout.BOTTOM, 0.2f,TweetViewPart.VIEW_ID);
		
		
		
//		layout.addView(MessagesViewPart.VIEW_ID, IPageLayout.RIGHT, 0.25f,FriendsViewPart.VIEW_ID);//	
//		layout.addView(ReplyViewPart.VIEW_ID, IPageLayout.RIGHT, 0.5f, MessagesViewPart.VIEW_ID);
//		layout.addView(DirectMessagesViewPart.VIEW_ID, IPageLayout.RIGHT, 0.75f, ReplyViewPart.VIEW_ID);
//		
		//new
		IFolderLayout messages = layout.createFolder("messageFolder", IPageLayout.RIGHT, 0.25f, "friendsFolder"); //FriendsViewPart.VIEW_ID);
		messages.addView(MessagesViewPart.VIEW_ID);//	
		messages.addView(ReplyViewPart.VIEW_ID);
		messages.addView(DirectMessagesViewPart.VIEW_ID);
		messages.addView(SearchViewPart.VIEW_ID);
		

		//			
//		
		System.err.println("Perspective opened...");
		/** 
		 * Still to add.
		 */
		//Search
		//Favourites
		//Groups
		//TODO: add a preferences page. 
		layout.setEditorAreaVisible(false);
	}
	
	

}