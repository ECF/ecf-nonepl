/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core;

import static org.apache.zookeeper.server.ServerConfig.getClientPort;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.PurgeTxnLog;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.NIOServerCnxn.Factory;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.zookeeper.core.internal.Advertiser;
import org.eclipse.ecf.provider.zookeeper.core.internal.Configurator;
import org.eclipse.ecf.provider.zookeeper.core.internal.Localizer;
import org.eclipse.ecf.provider.zookeeper.node.internal.ReadRoot;
import org.eclipse.ecf.provider.zookeeper.node.internal.WatchManager;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author Ahmed Aadel
 * @since 0.1
 */
public class DiscoveryContainer extends AbstractDiscoveryContainerAdapter {

	private static DiscoveryContainer discovery;
	public static final ExecutorService CACHED_THREAD_POOL = Executors
			.newCachedThreadPool();;
	private Thread quorumPeerThread;
	private QuorumPeer quorumPeer;
	public final static int DEFAUL_PORT = 2181;
	private Properties DiscoveryProperties = new Properties();
	private static Configurator config = new Configurator();
	protected Advertiser advertiser;
	protected Localizer localizer;
	protected Thread zookeeperThread;
	private ZooKeeperServer zooKeeperServer;
	private ID targetId;
	protected static boolean isQuorumPeerReady;
	private boolean hasShutDown;
	private DiscoveryNamespace namespace;
	private WatchManager watchManager;
	private ID id;
	private boolean initialized;

	public enum FLAVOR {
		STANDALONE, CENTRALIZED, REPLICATED;

		public String toString() {
			switch (this) {
			case STANDALONE:
				return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_STANDALONE;
			case CENTRALIZED:
				return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_CENTRALIZED;
			case REPLICATED:
				return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_REPLICATED;
			}
			throw new AssertionError("Unsupported configuration");
		}
	}

	private DiscoveryContainer() {
		super(DiscoveryNamespace.NAME, config);
		this.namespace = new DiscoveryNamespace();
		this.id = IDFactory.getDefault().createGUID();
	}

	public static DiscoveryContainer getSingleton() {
		if (discovery == null) {
			discovery = new DiscoveryContainer();
		}
		return discovery;
	}

	public void init(ServiceReference reference) {
		if (initialized)
			return;
		config.configure(reference);
		doStart();
		initialized = true;
	}

	private void init(ID targetID) {
		if (initialized)
			return;
		config.configure(targetID);
		doStart();
		initialized = true;
	}

	private void doStart() {
		this.watchManager = WatchManager.getSingleton(getConf());
		this.advertiser = Advertiser.getSingleton(this.watchManager);
		this.localizer = Localizer.getSingleton();
		if (getConf().isCentralized()) {
			if (Geo.getHost().equals(getConf().getServerIps().split(":")[0])) { //$NON-NLS-1$
				CACHED_THREAD_POOL.execute(new Runnable() {
					public void run() {
						startStandAlone();
						try {
							DiscoveryContainer.this.zookeeperThread.join();
						} catch (InterruptedException e) {
							Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
						}
						DiscoveryContainer.this.watchManager.watch();
						DiscoveryContainer.this.localizer.init();					
						DiscoveryContainer.this.advertiser.autoPublish();
					}
				});

			} else {
				this.watchManager.watch();
				this.localizer.init();				
				DiscoveryContainer.this.advertiser.autoPublish();
			}

		} else if (getConf().isQuorum()) {
			CACHED_THREAD_POOL.execute(new Runnable() {
				public void run() {
					startQuorumPeer();
					try {
						DiscoveryContainer.this.quorumPeerThread.join();
					} catch (InterruptedException e) {
						Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
					}
					DiscoveryContainer.this.watchManager.watch();
					DiscoveryContainer.this.localizer.init();					
					DiscoveryContainer.this.advertiser.autoPublish();
				}
			});
		}

		else if (getConf().isStandAlone()) {
			CACHED_THREAD_POOL.execute(new Runnable() {
				public void run() {
					startStandAlone();
					try {
						DiscoveryContainer.this.zookeeperThread.join();
					} catch (InterruptedException e) {
						Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
					}
					DiscoveryContainer.this.watchManager.watch();
					DiscoveryContainer.this.localizer.init();					
					DiscoveryContainer.this.advertiser.autoPublish();
				}
			});
		}
	}

	/**
	 * Start a ZooKeeer server locally to write nodes to. Implied by
	 * {@link IDiscoveryConfig#ZOODISCOVERY_FLAVOR_STANDALONE} configuration.
	 */
	void startStandAlone() {
		if (this.zooKeeperServer != null && this.zooKeeperServer.isRunning())
			return;
		else if (this.zooKeeperServer != null
				&& !this.zooKeeperServer.isRunning())
			try {
				this.zooKeeperServer.startup();
				return;
			} catch (Exception e) {
				Logger.log(LogService.LOG_DEBUG,
						"Zookeeper server cannot be started! ", e);//$NON-NLS-1$				
			}

		// create brand new zooKeeper server
		this.zookeeperThread = new Thread(new Runnable() {
			public void run() {
				try {
					DiscoveryContainer.this.zooKeeperServer = new ZooKeeperServer();
					FileTxnSnapLog fileTxnSnapLog = new FileTxnSnapLog(
							getConf().getZookeeperData(), getConf()
									.getZookeeperData());
					DiscoveryContainer.this.zooKeeperServer
							.setTxnLogFactory(fileTxnSnapLog);
					DiscoveryContainer.this.zooKeeperServer
							.setTickTime(ZooKeeperServer.DEFAULT_TICK_TIME);
					Factory cnxnFactory = new NIOServerCnxn.Factory(DEFAUL_PORT);
					cnxnFactory
							.startup(DiscoveryContainer.this.zooKeeperServer);
				} catch (Exception e) {
					Logger
							.log(
									LogService.LOG_ERROR,
									"Zookeeper server cannot be started! Possibly another instance is already running. ",
									e);
				}
			}
		});
		this.zookeeperThread.setDaemon(true);
		this.zookeeperThread.start();

	}

	/**
	 * Start a local ZooKeeer server to write nodes to. It plays as a peer
	 * within a replicated servers configuration. Implied by
	 * {@link IDiscoveryConfig#ZOODISCOVERY_FLAVOR_REPLICATED} configuration.
	 */
	void startQuorumPeer() {
		if (this.quorumPeer != null && this.quorumPeer.isAlive()) {
			return;
		} else if (this.quorumPeer != null && !this.quorumPeer.isAlive()) {
			this.quorumPeer.start();
			return;
		}

		this.quorumPeerThread = new Thread(new Runnable() {
			public void run() {
				try {
					QuorumPeerConfig.parse(new String[] { getConf()
							.getConfFile() });
					runPeer(new QuorumPeer.Factory() {
						public QuorumPeer create(
								NIOServerCnxn.Factory cnxnFactory)
								throws IOException {
							QuorumPeer peer = new QuorumPeer();
							peer.setClientPort(ServerConfig.getClientPort());
							peer.setTxnFactory(new FileTxnSnapLog(new File(
									ServerConfig.getDataLogDir()), new File(
									ServerConfig.getDataDir())));
							peer.setQuorumPeers(QuorumPeerConfig.getServers());
							peer.setElectionType(QuorumPeerConfig
									.getElectionAlg());
							peer.setMyid(QuorumPeerConfig.getServerId());
							peer.setTickTime(ServerConfig.getTickTime());
							peer.setInitLimit(QuorumPeerConfig.getInitLimit());
							peer.setSyncLimit(QuorumPeerConfig.getSyncLimit());
							peer.setCnxnFactory(cnxnFactory);
							DiscoveryContainer.this.quorumPeer = peer;
							return peer;
						}

						public NIOServerCnxn.Factory createConnectionFactory()
								throws IOException {
							return new NIOServerCnxn.Factory(getClientPort());
						}
					});

				} catch (Exception e) {
					Logger.log(LogService.LOG_ERROR,
							"Zookeeper quorum cannot be started! ", e); //$NON-NLS-1$
				}

			}

		});
		this.quorumPeerThread.setDaemon(true);
		this.quorumPeerThread.start();

	}

	public void setDiscoveryProperties(Properties discoveryProperties) {
		this.DiscoveryProperties = discoveryProperties;
	}

	public Properties getDiscoveryProperties() {
		return this.DiscoveryProperties;
	}

	public void shutdown() {
		try {
			if (this.watchManager != null) {
				this.watchManager.unpublishAll();
			}
			if (this.advertiser != null) {
				this.advertiser.close();
			}
			if (this.localizer != null) {
				this.localizer.close();
			}
			if (this.zooKeeperServer != null) {
				// purge snaps and logs. Keep only last three of each
				PurgeTxnLog.purge(this.zooKeeperServer.getTxnLogFactory()
						.getDataDir(), this.zooKeeperServer.getTxnLogFactory()
						.getSnapDir(), 3);
				this.zooKeeperServer.shutdown();
			}
			if (this.quorumPeer != null) {
				// purge snaps and logs. Keep only last three of each
				PurgeTxnLog.purge(this.quorumPeer.getTxnFactory().getDataDir(),
						this.quorumPeer.getTxnFactory().getSnapDir(), 3);
				// shut down server
				if (this.quorumPeer.isAlive()) {
					this.quorumPeer.shutdown();
				}
				// shutdown sockets
				this.quorumPeer.getCnxnFactory().shutdown();
				// a bit too far
				this.quorumPeerThread = null;

			}
		} catch (Throwable t) {
			Logger.log(LogService.LOG_ERROR, t.getMessage(), t);
		}
		// prompt we'r gone!
		PrettyPrinter.prompt(PrettyPrinter.DEACTIVATED, null);
	}

	public static void runPeer(QuorumPeer.Factory qpFactory) {
		try {
			QuorumPeer self = qpFactory.create(qpFactory
					.createConnectionFactory());
			self.start();
			isQuorumPeerReady = true;
		} catch (Exception e) {
			Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
		}
	}

	public void setConf(Configurator c) {
		config = c;
	}

	public Configurator getConf() {
		return config;
	}

	public ZooKeeperServer getLocalServer() {
		return this.zooKeeperServer;
	}

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		this.targetId = targetID;
		init(targetID);
	}

	public void disconnect() {
		this.hasShutDown = true;
		initialized = false;
		shutdown();

	}

	public Namespace getConnectNamespace() {
		return this.namespace;
	}

	public ID getConnectedID() {
		if (this.hasShutDown)
			return null;
		return this.targetId;
	}

	public ID getID() {
		return this.id;
	}

	public IServiceInfo getServiceInfo(IServiceID serviceID) {
		return ReadRoot.discoverdServices.get(serviceID.getServiceTypeID()
				.getName());
	}

	public IServiceTypeID[] getServiceTypes() {
		IServiceTypeID ids[] = new IServiceTypeID[getServices().length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = getServices()[i].getServiceID().getServiceTypeID();
		}
		return ids;
	}

	public IServiceInfo[] getServices() {
		return ReadRoot.discoverdServices.values().toArray(
				new IServiceInfo[ReadRoot.discoverdServices.size()]);
	}

	public IServiceInfo[] getServices(IServiceTypeID type) {
		return new DiscoverdService[] { ReadRoot.discoverdServices.get(type
				.getInternal()) };
	}

	public Namespace getServicesNamespace() {
		return this.namespace;
	}

	public void registerService(IServiceInfo serviceInfo) {
		if (serviceInfo instanceof AdvertisedService) {
			this.watchManager.publish((AdvertisedService) serviceInfo);
		} else
			this.watchManager.publish(new AdvertisedService(serviceInfo));

	}

	public void unregisterAllServices() {
		this.watchManager.unpublishAll();
	}

	public void unregisterService(IServiceInfo serviceInfo) {
		this.watchManager.unpublish(serviceInfo.getServiceID()
				.getServiceTypeID().getInternal());

	}

	public Set<IServiceListener> getAllServiceListener() {
		return super.allServiceListeners;
	}

	public Collection<IServiceListener> getListeners(IServiceTypeID aServiceType) {
		return super.getListeners(aServiceType);
	}

}
