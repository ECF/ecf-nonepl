package org.eclipse.ecf.provider.call.sip_new.container;

import java.io.Serializable;

import org.eclipse.ecf.core.security.Callback;

public class ProxyServerCallBack implements Callback, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5595490934046608684L;
	
	
	private String prompt;

	private String defaultProxy;

	private String inputProxy;

	/**
	 * Construct a <code>NameCallback</code> with a prompt.
	 * 
	 * @param prompt
	 *            the prompt used to request the name.
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>prompt</code> is null.
	 */
	public ProxyServerCallBack(String prompt) {
		if (prompt == null)
			throw new IllegalArgumentException("Prompt cannot be null");
		this.prompt = prompt;
	}

	/**
	 * Construct a <code>NameCallback</code> with a prompt and default name.
	 * 
	 * <p>
	 * 
	 * @param prompt
	 *            the prompt used to request the information.
	 *            <p>
	 * 
	 * @param defaultName
	 *            the name to be used as the default name displayed with the
	 *            prompt.
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>prompt</code> is null.
	 */
	public ProxyServerCallBack(String prompt, String defaultName) {
		if (prompt == null)
			throw new IllegalArgumentException("Prompt cannot be null");
		this.prompt = prompt;
		this.defaultProxy = defaultName;
	}

	/**
	 * Get the prompt.
	 * 
	 * <p>
	 * 
	 * @return the prompt.
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * Get the default name.
	 * 
	 * <p>
	 * 
	 * @return the default name, or null if this <code>NameCallback</code> was
	 *         not instantiated with a <code>defaultName</code>.
	 */
	public String getDefaultProxy() {
		return defaultProxy;
	}

	/**
	 * Set the retrieved name.
	 * 
	 * <p>
	 * 
	 * @param name
	 *            the retrieved name (which may be null).
	 * 
	 * @see #getName
	 */
	public void setProxy(String proxy) {
		this.inputProxy = proxy;
	}

	/**
	 * Get the retrieved name.
	 * 
	 * <p>
	 * 
	 * @return the retrieved name (which may be null)
	 * 
	 * @see #setName
	 */
	public String getProxy() {
		return inputProxy;
	}


}
