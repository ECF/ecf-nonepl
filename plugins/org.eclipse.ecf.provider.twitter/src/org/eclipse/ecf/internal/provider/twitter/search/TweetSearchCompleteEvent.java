package org.eclipse.ecf.internal.provider.twitter.search;

import org.eclipse.ecf.provider.twitter.search.IResultTweetList;
import org.eclipse.ecf.provider.twitter.search.ITweetSearchCompleteEvent;

public class TweetSearchCompleteEvent implements ITweetSearchCompleteEvent {

	protected IResultTweetList result;
	
	/**
	 * 
	 * @param result {@link IResultTweetList}
	 */
	public TweetSearchCompleteEvent(IResultTweetList result) {
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.provider.twitter.search.ITweetSearchCompleteEvent#getSearchResult()
	 */
	public IResultTweetList getSearchResult() {
		return result;
	}

}
