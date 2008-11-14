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
 *  $Id: MyServices.java,v 1.1 2008/11/14 07:31:12 phperret Exp $
 */

package net.jxta.ext.example.presence;

import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;

import net.jxta.ext.service.Constants;
import net.jxta.ext.service.PeerletContext;
import net.jxta.ext.service.Server;
import net.jxta.ext.service.presence.Attention;
import net.jxta.ext.service.presence.AttentionDispatcher;
import net.jxta.ext.service.protocol.http.HTTPServer;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.eclipse.ecf.provider.jxta.Activator;

public class MyServices
        implements Attention, PeerletContext {

    private MyNetwork network = null;
    private String status = net.jxta.ext.example.presence.Constants.STATUS_PENDING;
    private URI attention = null;
    private MulticastSocket multicast = null;
    private Activator activator=null;
    
    public MyServices(final MyNetwork network) {
        this.network = network;
        init();
    }

    public MulticastSocket getMulticastSocket() {
        return multicast;
    }

    public boolean isConnected() {
        return network.getNetwork().isConnected() && multicast != null;
    }

    public String getId() {
    	return activator.getId();
    }

    public String getStatusId() {
        return status;
    }

    public void setStatus(final String statusId) {
        setStatus(getId(), statusId);
    }

    public void setStatus(final String id, final String statusId) {
        if (id.equals(getId())) {
            status = statusId;

            if (isConnected()) {
                Thread t = new Thread(new AttentionDispatcher(this, multicast, statusId),
                        getClass().getName() + ":attention");

                t.setDaemon(true);
                t.start();
            }
        }
    }

    public void attention(final String id, final String statusId) {
        if (! activator.getId().equals(id)) {
        	activator.setStatus(id, statusId);
        }
    }

    public Context getNamingContext() {
        return null;
    }

    private void init() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                System.out.println("connecting ...");

                while (network.getNetwork() == null ||
                        (network.getNetwork() != null && ! network.getNetwork().isConnected())) {
                    try {
                        Thread.sleep(net.jxta.ext.example.presence.Constants.NETWORK_CONNECT_WAIT);
                    } catch (InterruptedException ie) {
                        // ignore
                    }
                }

                registerServices();
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void registerServices() {
        PeerGroup pg = network.getNetwork().getNetPeerGroup();
        HTTPServer hs = new HTTPServer();
        Server s = hs.createServer(activator.getId() + Constants.Protocol.P2MP.getProtocol(), pg,
                createPipeAdvertisement(pg, Constants.Pipe.P2MP_NAME.getPipe()));

        s.addPeerlet("/attention.*", "net.jxta.ext.service.peerlet.AttentionPeerlet", this);

        hs.start();

        // todo: make determinant
        try {
            Thread.sleep(net.jxta.ext.example.presence.Constants.SERVICE_WAIT);
        } catch (InterruptedException ie) {
            // ignore
        }

        try {
            attention = s.getURI();
        } catch (URISyntaxException use) {
            // ignore
        }

        multicast = s.getMulticastSocket();

//        setStatus(STATUS_ACTIVE);
    }

    private PipeAdvertisement createPipeAdvertisement(final PeerGroup pg, final String name) {
        return createPipeAdvertisement(pg, name, name);
    }

    private PipeAdvertisement createPipeAdvertisement(final PeerGroup pg, final String id, final String name) {
        PipeAdvertisement pa = (PipeAdvertisement)net.jxta.document.AdvertisementFactory.
                newAdvertisement(PipeAdvertisement.getAdvertisementType());

        pa.setPipeID(IDFactory.newPipeID(pg.getPeerGroupID(), id.getBytes()));
        pa.setName(name);
        pa.setType(PipeService.PropagateType);

        return pa;
    }
}
