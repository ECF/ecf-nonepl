/*
 * $HeadURL$
 * $Revision: 1.2 $
 * $Date: 2008/10/21 16:17:29 $
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.eclipse.ecf.provider.filetransfer.httpcore;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.BufferingHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.HttpRequestExecutionHandler;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

public class NHttpClient {

    public static void main(String[] args) throws Exception {
        HttpParams params = new BasicHttpParams();
        params
            .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
            .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000)
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
            .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
            .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
            .setParameter(CoreProtocolPNames.USER_AGENT, "HttpComponents/1.1");

        final ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(2, params);

        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        
        // We are going to use this object to synchronize between the 
        // I/O event and main threads
        //CountDownLatch requestCount = new CountDownLatch(3);
        CountDownLatch requestCount = new CountDownLatch(1);
        
        BufferingHttpClientHandler handler = new MyBufferingHttpClientHandler(
                httpproc,
                new MyHttpRequestExecutionHandler(requestCount),
                new DefaultConnectionReuseStrategy(),
                params);

        handler.setEventListener(new EventLogger());
        
        final IOEventDispatch ioEventDispatch = new DefaultClientIOEventDispatch(handler, params);
        
        Thread t = new Thread(new Runnable() {
         
            public void run() {
                try {
                    ioReactor.execute(ioEventDispatch);
                } catch (InterruptedIOException ex) {
                    System.err.println("Interrupted");
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                }
                System.out.println("Shutdown");
            }
            
        });
        t.start();

        SessionRequest[] reqs = new SessionRequest[1];
        reqs[0] = ioReactor.connect(
                new InetSocketAddress("ftp.osuosl.org", 80), 
                null, 
                new HttpHost("ftp.osuosl.org"),
                new MySessionRequestCallback(requestCount));
        // Block until all connections signal
        // completion of the request execution
        requestCount.await();
        
        System.out.println("Shutting down I/O reactor");
        
        ioReactor.shutdown();
        
        System.out.println("Done");
    }
    
    static class MyHttpRequestExecutionHandler implements HttpRequestExecutionHandler {

        private final static String REQUEST_SENT       = "request-sent";
        private final static String RESPONSE_RECEIVED  = "response-received";
        
        private final CountDownLatch requestCount;
        
        public MyHttpRequestExecutionHandler(final CountDownLatch requestCount) {
            super();
            this.requestCount = requestCount;
        }
        
        public void initalizeContext(final HttpContext context, final Object attachment) {
            HttpHost targetHost = (HttpHost) attachment;
            context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, targetHost);
        }
        
        public void finalizeContext(final HttpContext context) {
            Object flag = context.getAttribute(RESPONSE_RECEIVED);
            if (flag == null) {
                // Signal completion of the request execution
                requestCount.countDown();
            }
        }

        public HttpRequest submitRequest(final HttpContext context) {
            HttpHost targetHost = (HttpHost) context.getAttribute(
                    ExecutionContext.HTTP_TARGET_HOST);
            Object flag = context.getAttribute(REQUEST_SENT);
            if (flag == null) {
                // Stick some object into the context
                context.setAttribute(REQUEST_SENT, Boolean.TRUE);

                System.out.println("--------------");
                System.out.println("Sending request to " + targetHost);
                System.out.println("--------------");
                
                //http://ftp.osuosl.org/pub/eclipse/technology/ecf/org.eclipse.ecf.examples-1.0.3.v20070927-1821.zip      
                return new BasicHttpRequest("GET", "/pub/eclipse/technology/ecf/org.eclipse.ecf.examples-1.0.3.v20070927-1821.zip");
            } else {
                // No new request to submit
                return null;
            }
        }
       public void handleResponse(final HttpResponse response, final HttpContext context) {
    	   System.out.println("handleResponse");
            HttpEntity entity = response.getEntity();
            try {
                String content = EntityUtils.toString(entity);
                
                System.out.println("--------------");
                System.out.println(response.getStatusLine());
                System.out.println("--------------");
                System.out.println("Document length: " + entity.getContentLength());
                System.out.println("--------------");
                //System.out.println("Content: "+content);
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            }

            context.setAttribute(RESPONSE_RECEIVED, Boolean.TRUE);
            
            // Signal completion of the request execution
            requestCount.countDown();
        }
        
    }
    
    static class MySessionRequestCallback implements SessionRequestCallback {

        private final CountDownLatch requestCount;        
        
        public MySessionRequestCallback(final CountDownLatch requestCount) {
            super();
            this.requestCount = requestCount;
        }
        
        public void cancelled(final SessionRequest request) {
            this.requestCount.countDown();
        }

        public void completed(final SessionRequest request) {
        }

        public void failed(final SessionRequest request) {
            this.requestCount.countDown();
        }

        public void timeout(final SessionRequest request) {
            this.requestCount.countDown();
        }
        
    }
    
    static class EventLogger implements EventListener {

        public void connectionOpen(final NHttpConnection conn) {
            System.out.println("Connection open: " + conn);
        }

        public void connectionTimeout(final NHttpConnection conn) {
            System.out.println("Connection timed out: " + conn);
        }

        public void connectionClosed(final NHttpConnection conn) {
            System.out.println("Connection closed: " + conn);
        }

        public void fatalIOException(final IOException ex, final NHttpConnection conn) {
            System.err.println("I/O error: " + ex.getMessage());
        }

        public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
            System.err.println("HTTP error: " + ex.getMessage());
        }
        
    }

    static class MyBufferingHttpClientHandler extends BufferingHttpClientHandler {

		public MyBufferingHttpClientHandler(HttpProcessor httpProcessor,
				HttpRequestExecutionHandler execHandler,
				ConnectionReuseStrategy connStrategy,
				ByteBufferAllocator allocator, HttpParams params) {
			super(httpProcessor, execHandler, connStrategy, allocator, params);
		}

		public MyBufferingHttpClientHandler(BasicHttpProcessor httpproc,
				MyHttpRequestExecutionHandler myHttpRequestExecutionHandler,
				DefaultConnectionReuseStrategy defaultConnectionReuseStrategy,
				HttpParams params) {
			super(httpproc,myHttpRequestExecutionHandler,defaultConnectionReuseStrategy,params);
		}
    	public void inputReady(NHttpClientConnection conn,
    			ContentDecoder decoder) {
    		super.inputReady(conn, decoder);
    		HttpConnectionMetrics metrics = conn.getMetrics();
    		System.out.println("received bytes="+metrics.getReceivedBytesCount());
    	}

    	public void responseReceived(NHttpClientConnection conn) {
    		HttpResponse response = conn.getHttpResponse();
    		System.out.println("response received="+Arrays.asList(response.getAllHeaders()));
    		super.responseReceived(conn);
    	}
    	
    }
}
