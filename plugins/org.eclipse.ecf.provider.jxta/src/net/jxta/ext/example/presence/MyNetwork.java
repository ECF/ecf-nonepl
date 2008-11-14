/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA"
 *  must not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: MyNetwork.java,v 1.1 2008/11/14 07:31:12 phperret Exp $
 */

package net.jxta.ext.example.presence;

import java.io.File;
import java.net.URI;
import java.util.EventObject;

import net.jxta.exception.ConfiguratorException;
import net.jxta.ext.configuration.AbstractConfigurator;
import net.jxta.ext.configuration.Configurator;
import net.jxta.ext.configuration.Profile;
import net.jxta.ext.network.GroupEvent;
import net.jxta.ext.network.Network;
import net.jxta.ext.network.NetworkEvent;
import net.jxta.ext.network.NetworkException;
import net.jxta.ext.network.NetworkListener;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.peergroup.PeerGroup;
import net.jxta.rendezvous.RendezvousEvent;

public class MyNetwork {

    private static final String HOME = System.getProperty("JXTA_HOME", System.getProperty("user.dir") +
            File.separator + ".mynet");
    private static final String PROFILE_RESOURCE = "/net/jxta/ext/example/presence/resources/edge.xml";
    private static final String CONFIG_NAME = "proto";
    private static final String CONFIG_USER = "usr";
    private static final String CONFIG_PASSWORD = "pwd";
    private static final String LISTENER_LOCK = new String(MyNetwork.class.getName() + ":listener lock");

    private Network network = null;
    private NetworkListener listener = null;

    public MyNetwork() {
        this(null);
    }

    public MyNetwork(final NetworkListener listener) {
        this.listener = listener;
    }

    public Network getNetwork() {
        return network;
    }

    public void start() {
        if (network == null) {
            try { // new URI(HOME), Profile.get(getClass().getResource(PROFILE_RESOURCE).toURI())
            	// "file:///C:/Users/pierre/workspace/meerkat/example/.mynet"
            	URI myURI= new URI("file:///C:/Users/pierre/workspace/meerkat/example/.mynet");
            	Profile myProfile= Profile.get(getClass().getResource(PROFILE_RESOURCE).toURI());
                network = new Network(new AbstractConfigurator( myURI, myProfile ) {
                    public PlatformConfig createPlatformConfig(Configurator c)
                            throws ConfiguratorException {
                        c.setName(CONFIG_NAME);
                        c.setSecurity(CONFIG_USER, CONFIG_PASSWORD);

                        return c.getPlatformConfig();
                    }
                }, getNetworkListener());
            } catch (Exception use) {
                use.printStackTrace();
            }
        }

        if (network != null) {
            try {
                network.start();
            } catch (NetworkException ne) {
                ne.printStackTrace();
            }
        }
    }

    public void stop() {
        if (network != null) {
            network.stop();

            network = null;
        }
    }

    private NetworkListener getNetworkListener() {
        if (listener == null) {
            synchronized (LISTENER_LOCK) {
                listener = new NetworkListener() {
                    public void notify(NetworkEvent ne) {
                        StringBuffer msg = new StringBuffer();

                        msg.append("NetworkEvent: ");

                        PeerGroup pg = ne.getPeerGroup();

                        msg.append(pg.getPeerGroupName() + " ");

                        EventObject cause = ne.getCause();

                        if (cause != null) {
                            msg.append(cause.getClass().getName() + " ");

                            if (cause instanceof RendezvousEvent) {
                                RendezvousEvent re = (RendezvousEvent) cause;
                                String p = re.getPeer();
                                String pid = re.getPeerID().toString();
                                int t = re.getType();

                                pg = ne.getPeerGroup();

                                msg.append(pg.getPeerGroupName() + " " + p + " " + pid + " " + t);
                            } else if (cause instanceof GroupEvent) {
                                GroupEvent ge = (GroupEvent) cause;
                                int t = ge.getType();

                                pg = ge.getPeerGroup();

                                msg.append(pg.getPeerGroupName() + " " + t);
                            }
                        }

                        System.out.println(msg);
                    }
                };
            }
        }

        return listener;
    }
}
