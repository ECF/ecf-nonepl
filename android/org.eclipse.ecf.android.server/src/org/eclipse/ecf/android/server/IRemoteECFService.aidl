
package org.eclipse.ecf.android.server;

import org.eclipse.ecf.android.server.IRemoteECFServiceCallback;

/**
 * Remote service
 * (running in another process).
 */
interface IRemoteECFService  {
    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void registerCallback(IRemoteECFServiceCallback cb);
    
    /**
     * Remove a previously registered callback interface.
     */
    void unregisterCallback(IRemoteECFServiceCallback cb);
    
    void connect();
    
    boolean start();
    
    
}
