package org.eclipse.ecf.provider.call.sip_new.container;

import java.io.IOException;

import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.NameCallback;
import org.eclipse.ecf.core.security.ObjectCallback;
import org.eclipse.ecf.core.security.PassphraseCallback;
import org.eclipse.ecf.core.security.PasswordCallback;
import org.eclipse.ecf.core.security.UnsupportedCallbackException;

public class SipConnectContextFactory {
	
	private SipConnectContextFactory() {
		super();
	}
	
	/**
	 * Create username and password connect context, where username is
	 * represented as a String and password as an Object.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return IConnectContext for accessing the username and password
	 */
	public static IConnectContext createSipConnectContext(final String initiatorName, final Object password, final String proxyServer) {
		return new IConnectContext() {
			public CallbackHandler getCallbackHandler() {
				return new CallbackHandler() {
					/**
					 * @param callbacks
					 * @throws IOException not thrown by this implementation.
					 * @throws UnsupportedCallbackException not thrown by this implementation.
					 */
					public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
						if (callbacks == null)
							return;
						for (int i = 0; i < callbacks.length; i++) {
							if (callbacks[i] instanceof NameCallback) {
								NameCallback ncb = (NameCallback) callbacks[i];
								ncb.setName(initiatorName);
							} else if (callbacks[i] instanceof ObjectCallback) {
								ObjectCallback ocb = (ObjectCallback) callbacks[i];
								ocb.setObject(password);
							} else if (callbacks[i] instanceof PasswordCallback && password instanceof String) {
								PasswordCallback pc = (PasswordCallback) callbacks[i];
								pc.setPassword((String) password);
							} else if (callbacks[i] instanceof PassphraseCallback && password instanceof String) {
								PassphraseCallback pc = (PassphraseCallback) callbacks[i];
								pc.setPassphrase((String) password);
							}else if(callbacks[i] instanceof ProxyServerCallBack){
								ProxyServerCallBack psc=(ProxyServerCallBack)callbacks[i];
								psc.setProxy(proxyServer);
							}
						}
					}
				};
			}
		};
	}

}
