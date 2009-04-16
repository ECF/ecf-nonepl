/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.twitter.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.twitter.Activator;
import org.eclipse.ecf.provider.twitter.search.ITweetSearch;
import org.eclipse.ecf.provider.twitter.search.ITweetSearchCompleteEvent;
import org.eclipse.ecf.provider.twitter.search.ITweetSearchListener;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * 
 *@since 3.0
 */
public class TweetSearch implements ITweetSearch {

	protected Twitter twitter;

	public TweetSearch(Twitter twitter) {
		this.twitter = twitter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.twitter.search.ITweetSearch#search(java.lang
	 * .String)
	 */
	public List search(String criteria) throws ECFException {
		Assert.isNotNull(criteria);
		Assert.isNotNull(twitter);
		try {
			Query query = new Query(criteria);
			QueryResult result = twitter.search(query);

			List tweets = result.getTweets();
			List resultTweets = new ArrayList(tweets.size());
			for (Iterator iterator = tweets.iterator(); iterator.hasNext();) {
				Tweet tweet = (Tweet) iterator.next();
				TweetItem item = new TweetItem(tweet);
				resultTweets.add(item);
			}

			return resultTweets;
			
		} catch (TwitterException e) {
			throw new ECFException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.provider.twitter.search.ITweetSearch#search(java.lang
	 * .String, org.eclipse.ecf.provider.twitter.search.ITweetSearchListener)
	 */
	public void search(final String criteria, final ITweetSearchListener listener) {
		Assert.isNotNull(criteria);
		Assert.isNotNull(listener);
		Job job = new Job(Messages.getString("SearchMessage.0")) { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				try {
					List result = search(criteria);
					ITweetSearchCompleteEvent complete = new TweetSearchCompleteEvent(result);
					listener.handleTweetSearchEvent(complete);
				} catch (ECFException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,IStatus.ERROR,"Exception in user search",e); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

}
