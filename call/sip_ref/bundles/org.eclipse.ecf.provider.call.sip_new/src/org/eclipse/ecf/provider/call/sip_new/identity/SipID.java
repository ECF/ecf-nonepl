package org.eclipse.ecf.provider.call.sip_new.identity;

import javax.sip.address.SipURI;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class SipID extends StringID{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8785005416978046990L;

	protected SipID(Namespace n, String s) {
		super(n, s);		
	
	}
	
	public SipID(SipURI sipUri) {
		super(IDFactory.getDefault().getNamespaceByName(
				SIPNamespace.NAME), sipUri.toString());
		
	}

	public SipID(String s) {
		this(IDFactory.getDefault().getNamespaceByName(
				SIPNamespace.NAME), s);
		
	}
	
	public String toString(){
		StringBuffer sb=new StringBuffer();
		sb.append(SIPNamespace.SCHEME).append(Namespace.SCHEME_SEPARATOR).append(toExternalForm());
		return sb.toString();
	}
	
	
	
	
}
