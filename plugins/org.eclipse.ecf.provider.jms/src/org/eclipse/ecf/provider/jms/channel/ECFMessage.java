package org.eclipse.ecf.provider.jms.channel;

import java.io.Serializable;

import org.eclipse.ecf.core.identity.ID;

public interface ECFMessage extends Serializable {

	public ID getTargetID();
	public ID getSenderID();
	public String getSenderJMSID();
	public Serializable getData();
}
