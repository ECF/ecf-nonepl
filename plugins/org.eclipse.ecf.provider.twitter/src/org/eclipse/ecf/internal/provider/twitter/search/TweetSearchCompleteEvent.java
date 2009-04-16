package org.eclipse.ecf.internal.provider.twitter.search;

import java.util.List;

import org.eclipse.ecf.provider.twitter.search.ITweetSearchCompleteEvent;

public class TweetSearchCompleteEvent implements ITweetSearchCompleteEvent {

	protected List result;
	
	public TweetSearchCompleteEvent(List result) {
		this.result = result;
	}

	public List getSearchResult() {
		return result;
	}

}
