/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerConnection;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.NNTPConnectException;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;


public class ServerStoreFacade implements IServerStoreFacade {

	private UpdateRunner updateRunner;

	private IStore[] stores;

	public ServerStoreFacade(IStore[] stores) {
		this.stores = stores;
	}

	public void init() {
		startUpdateThread();
	}

	private void startUpdateThread() {

		if (updateRunner == null) {
			updateRunner = new UpdateRunner();
		}

		if (!updateRunner.isThreadRunning())
			new Thread(updateRunner, "Salvo newsreader update thread").start();

	}

	public IStore[] getStores() {
		return stores;
	}

	public boolean postArticle(IArticle article) {
		return false;
	}

	public IArticle fetchArticle(INewsgroup newsgroup, int articleId,
			int fetchType) throws NNTPIOException, UnexpectedResponseException {

		IArticle article = null;
		for (int i = 0; i < stores.length;) {
			article = stores[i].fetchArticle(newsgroup, articleId, fetchType);
			break;
		}

		if (article == null) {
			return newsgroup.getServer().getServerConnection().fetchArticle(
					newsgroup, articleId, fetchType);
		}
		return article;
	}

	public Exception getLastException() {
		return null;
	}

	public void catchUp(INewsgroup newsgroup) throws NNTPIOException {
		// FIXME Implement
	}

	public void unsubscribeNewsgroup(INewsgroup newsGroup, boolean permanent) {
		for (int i = 0; i < stores.length; i++) {
			stores[i].unsubscribeNewsgroup(newsGroup, permanent);
		}
	}

	public boolean cancelArticle(IArticle article) {
		// TODO Auto-generated method stub
		return false;
	}

	public void subscribeNewsgroup(INewsgroup group) throws NNTPIOException, UnexpectedResponseException {
		for (int i = 0; i < stores.length; i++) {
			stores[i].subscribeNewsgroup(group);
		}
		updateAttributes(group);
	}

	public void subscribeServer(IServer server, String passWord) {
		for (int i = 0; i < stores.length; i++) {
			stores[i].subscribeServer(server, passWord);
		}
	}

	public void unsubscribeServer(IServer server, boolean permanent) {
		for (int i = 0; i < stores.length; i++) {
			stores[i].unsubscribeServer(server, permanent);
		}
	}

	public void updateAttributes(INewsgroup newsgroup) throws NNTPIOException, UnexpectedResponseException {
		try {
			newsgroup.getServer().getServerConnection()
					.setWaterMarks(newsgroup);
		} catch (NNTPConnectException e) {
			throw new NNTPIOException(e.getMessage(), e);
		}
		for (int i = 0; i < stores.length; i++) {
			stores[i].updateAttributes(newsgroup);
		}
	}

	public INewsgroup[] getSubscribedNewsgroups(IServer server) {
		for (int i = 0; i < stores.length;) {
			return stores[i].getSubscribedNewsgroups(server);
		}
		return new INewsgroup[0];
	}

	// public IArticle[] getArticles(INewsgroup newsgroup, int from,
	// int to) throws NNTPIOException {
	//
	// try {
	// IServerConnection connection = newsgroup.getServer()
	// .getServerConnection();
	//
	// // Adjust for sanity
	// if ((to - from) > SALVO.BATCH_SIZE)
	// from = to - SALVO.BATCH_SIZE;
	//
	// // Check what is first in store
	// IStore firstStore = getFirstStore();
	// int firstInStore = 0;
	// if (firstStore != null
	// && firstStore.getFirstArticle(newsgroup) != null)
	// firstInStore = getFirstStore().getFirstArticle(newsgroup)
	// .getArticleNumber();
	//
	// // Fetch from the server what is not in store
	// if (firstInStore > 0 && firstInStore > from) {
	// IArticle[] result = connection.getArticles(newsgroup,
	// from, firstInStore);
	// if (result != null) {
	// for (int i = 0; i < stores.length; i++) {
	// stores[i].storeArticles(result);
	// }
	//
	// // Adjust the requested values
	// if (firstStore != null)
	// firstInStore = firstStore.getFirstArticle(newsgroup)
	// .getArticleNumber();
	// if (firstInStore > from)
	// from = firstInStore;
	// }
	//
	// // Check what is last in store
	// int lastInStore = 0;
	// if (firstStore != null
	// && firstStore.getLastArticle(newsgroup) != null)
	// lastInStore = firstStore.getLastArticle(newsgroup)
	// .getArticleNumber();
	//
	// // Fetch from the server what is not in store
	// if (lastInStore > 0 && lastInStore < to) {
	// result = connection.getArticles(newsgroup,
	// lastInStore, to);
	// if (result != null)
	// for (int i = 0; i < stores.length; i++) {
	// stores[i].storeArticles(result);
	//
	// // Adjust the requested values
	// if (firstStore != null)
	// lastInStore = firstStore.getLastArticle(newsgroup)
	// .getArticleNumber();
	// if (lastInStore < to)
	// to = lastInStore;
	// }
	//
	// result = null;
	// if (firstStore != null)
	// result = firstStore.getArticles(newsgroup, from, to);
	//
	// if (result == null) {
	// result = connection.getArticles(newsgroup, from, to);
	// if (result != null)
	// for (int i = 0; i < stores.length; i++) {
	// stores[i].storeArticles(result);
	// }
	// if (firstStore != null)
	// result = firstStore.getArticles(newsgroup, from, to);
	// }
	// if (result == null)
	// result = new IArticle[0];
	// return result;
	//
	// }}} catch (NNTPIOException e) {
	// throw new NNTPIOException(e.getMessage(), e);
	// }
	// }

	public IArticle[] getArticles(INewsgroup newsgroup, int from, int to)
			throws NNTPIOException, UnexpectedResponseException, StoreException {

		try {
			IServerConnection connection = newsgroup.getServer()
					.getServerConnection();

			// Adjust for sanity
			if ((to - from) > SALVO.BATCH_SIZE)
				from = to - SALVO.BATCH_SIZE;

			// Check what is first in store
			IStore firstStore = getFirstStore();
			int firstArticleInStore = 0;
			if (firstStore != null
					&& firstStore.getFirstArticle(newsgroup) != null)
				firstArticleInStore = getFirstStore().getFirstArticle(newsgroup)
						.getArticleNumber();

			// Fetch from the server what is not in store
			if (firstArticleInStore > 0 && firstArticleInStore > from) {
				IArticle[] result = connection.getArticles(newsgroup, from,
						firstArticleInStore);
				if (result != null) {
					for (int i = 0; i < stores.length; i++) {
						stores[i].storeArticles(result);
					}
				}

				// Adjust the requested values
				if (firstStore != null)
					firstArticleInStore = firstStore.getFirstArticle(newsgroup)
							.getArticleNumber();
				if (firstArticleInStore > from)
					from = firstArticleInStore;
			}

			// Check what is last in store
			int lastArticleInStore = 0;
			if (firstStore != null
					&& firstStore.getLastArticle(newsgroup) != null)
				lastArticleInStore = firstStore.getLastArticle(newsgroup)
						.getArticleNumber();

			// Fetch from the server what is not in store
			if (lastArticleInStore > 0 && lastArticleInStore < to) {
				IArticle[] result = connection.getArticles(newsgroup,
						lastArticleInStore, to);
				if (result != null) {
					for (int i = 0; i < stores.length; i++) {
						stores[i].storeArticles(result);
					}
				}
				// Adjust the requested values
				if (firstStore != null)
					lastArticleInStore = firstStore.getLastArticle(newsgroup)
							.getArticleNumber();
				if (lastArticleInStore < to)
					to = lastArticleInStore;
			}

			IArticle[] result = null;
			if (firstStore != null)
				result = firstStore.getArticles(newsgroup, from, to);

			if (result == null) {
				result = connection.getArticles(newsgroup, from, to);
				if (result != null) {
					for (int i = 0; i < stores.length; i++) {
						stores[i].storeArticles(result);
					}
				}
				if (firstStore != null)
					result = firstStore.getArticles(newsgroup, from, to);
			}
			if (result == null)
				result = new IArticle[0];

			return result;

		} catch (NNTPConnectException e) {
			throw new NNTPIOException(e.getMessage(), e);
		}
	}

	public IStore getFirstStore() {
		if (stores.length > 0)
			return stores[0];
		return null;
	}

//	public String[] getBody(IArticle article) throws NNTPIOException,
//			UnexpectedResponseException {
//
//		// FIXME Decide thru preference if article bodies should be stored in
//		// the store or always fetched from server?
//
//		// Get From Store
//		String[] body = null;
//		if (getFirstStore() != null)
//			try {
//				body = getFirstStore().getArticleBody(article);
//			} catch (NNTPConnectException e) {
//				Debug.log(getClass(), e);
//				throw new NNTPIOException(e.getMessage(), e);
//			}
//
//		// Not in store get from server
//		if (body == null) {
//			try {
//				body = article.getServer().getServerConnection()
//						.getArticleBody(article);
//			} catch (UnexpectedResponseException e) {
//				Debug.log(getClass(), e);
//				throw e;
//			}
//
//			if (!(body == null)) {
//				for (int i = 0; i < stores.length; i++) {
//					stores[i].storeArticleBody(article, body);
//				}
//			}
//		}
//		return body;
//	}

	public IArticle[] getFollowUps(IArticle article) throws NNTPIOException, UnexpectedResponseException, StoreException {

		// FIXME Decide if article bodies should be stored in the store or
		// always fetched from server.
		IArticle[] result = null;
		if (getFirstStore() != null)
			result = getFirstStore().getFollowUps(article);
		if (result == null) {
			try {
				result = article.getServer().getServerConnection()
						.getFollowUps(article);
			} catch (NNTPConnectException e) {
				throw new NNTPIOException(e.getMessage(), e);
			}
			if (!(result == null)) {
				if (getFirstStore() != null)
					getFirstStore().storeArticles(result);
			}
		}
		return result;
	}

	// public IArticle[] getAllFollowUps(IArticle article)
	// throws NNTPIOException {
	//
	// // FIXME Decide if article bodies should be stored in the store or
	// // always fetched from server.
	// IArticle[] result2 = getFollowUps(article);
	// Collection result = new ArrayList<IArticle>(result2);
	// for (IArticle reply : result) {
	// Collection<IArticle> r2 = getAllFollowUps(reply);
	// result2.addAll(r2);
	// }
	// return result2;
	// }

	public IArticle[] getAllFollowUps(IArticle article) throws NNTPIOException, UnexpectedResponseException, StoreException {

		// FIXME Decide if article bodies should be stored in the store or
		// always fetched from server.
		ArrayList result2 = new ArrayList();
		result2.addAll(Arrays.asList(getFollowUps(article)));
		
		Collection result = new ArrayList(result2);
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			IArticle reply = (IArticle) iterator.next();
			Collection r2 = Arrays.asList(getAllFollowUps(reply));
			result2.addAll(r2);
		}
		return (IArticle[]) result2.toArray(new IArticle[0]);
	}

	public void updateArticle(IArticle article) {
		for (int i = 0; i < stores.length; i++) {
			stores[i].updateArticle(article);
		}
	}

	public void replyToArticle(IArticle article, String body)
			throws NNTPIOException, UnexpectedResponseException {
		article.getServer().getServerConnection().replyToArticle(article, body);
		updateAttributes(article.getNewsgroup());
	}

	public void postNewArticle(INewsgroup[] newsgroups, String subject,
			String body) throws NNTPIOException {

		try {
			IServerConnection connection = newsgroups[0].getServer()
					.getServerConnection();
			connection.postNewArticle(newsgroups, subject, body);
			for (int i = 0; i < newsgroups.length; i++) {

				updateAttributes(newsgroups[i]);
			}
		} catch (UnexpectedResponseException e) {
			throw new NNTPIOException(e.getMessage(), e);
		}
	}

	public INewsgroup[] listNewsgroups(IServer server) throws NNTPIOException,
			NNTPIOException, UnexpectedResponseException {
		return server.getServerConnection().listNewsgroups(server);
	}

	public INewsgroup[] listNewsgroups(IServer server, Date since)
			throws NNTPIOException, UnexpectedResponseException {

		// FIXME implement
		return listNewsgroups(server);
	}

	public String[] getArticleBody(IArticle article) throws NNTPIOException,
			UnexpectedResponseException {
		try {
			String[] articleBody = stores[0].getArticleBody(article);
			if (articleBody.length > 0)
				return articleBody;
		} catch (UnexpectedResponseException e) {
			// cannot happen
		}

		String[] articleBody = article.getServer().getServerConnection()
				.getArticleBody(article);
		for (int i = 0; i < stores.length; i++) {
			stores[i].storeArticleBody(article, articleBody);
		}

		return articleBody;
	}

	public void setWaterMarks(INewsgroup newsgroup) throws NNTPIOException,
			UnexpectedResponseException {
		newsgroup.getServer().getServerConnection().setWaterMarks(newsgroup);
		for (int i = 0; i < stores.length; i++) {
			stores[i].setWaterMarks(newsgroup);
		}
	}

	public String[] getOverviewHeaders(IServer server) throws NNTPIOException,
			UnexpectedResponseException {
		return server.getServerConnection().getOverviewHeaders(server);
	}

	public void setModeReader(IServer server) throws NNTPIOException, UnexpectedResponseException {
setModeReader(server);
	}

	public IServer[] getSubscribedServers() {
		return stores[0].getSubscribedServers();
	}
}
