package org.eclipse.ecf.provider.call.sip_new.container;

public class Credential {
	
	private String initiatorName;
	private String password;
	private String proxyServer;
	
	public Credential(final String initiatorName,final String password,final String proxyServer) {
		this.initiatorName=initiatorName;
		this.password=password;
		this.proxyServer=proxyServer;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}
	
	

}
