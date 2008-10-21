package org.eclipse.ecf.provider.filetransfer.httpcore;

import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientHandler;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;

public class HttpCoreClientHandler implements NHttpClientHandler {

    private final AsyncNHttpClientHandler asyncHandler;

    public HttpCoreClientHandler(
            final HttpProcessor httpProcessor, 
            final NHttpRequestExecutionHandler execHandler,
            final ConnectionReuseStrategy connStrategy,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        this.asyncHandler = new AsyncNHttpClientHandler(
                httpProcessor,
                execHandler,
                connStrategy,
                allocator,
                params);
    }
    

    public void setEventListener(final EventListener eventListener) {
        this.asyncHandler.setEventListener(eventListener);
    }

    public void connected(final NHttpClientConnection conn, final Object attachment) {
        this.asyncHandler.connected(conn, attachment);
    }

    public void closed(final NHttpClientConnection conn) {
        this.asyncHandler.closed(conn);
    }

    public void requestReady(final NHttpClientConnection conn) {
        this.asyncHandler.requestReady(conn);
    }

    public void inputReady(final NHttpClientConnection conn, final ContentDecoder decoder) {
        this.asyncHandler.inputReady(conn, decoder);
    }

    public void outputReady(final NHttpClientConnection conn, final ContentEncoder encoder) {
        this.asyncHandler.outputReady(conn, encoder);
    }

    public void responseReceived(final NHttpClientConnection conn) {
        this.asyncHandler.responseReceived(conn);
    }

    public void exception(final NHttpClientConnection conn, final HttpException httpex) {
        this.asyncHandler.exception(conn, httpex);
    }

    public void exception(final NHttpClientConnection conn, final IOException ioex) {
        this.asyncHandler.exception(conn, ioex);
    }

    public void timeout(final NHttpClientConnection conn) {
        this.asyncHandler.timeout(conn);
    }
    
}
