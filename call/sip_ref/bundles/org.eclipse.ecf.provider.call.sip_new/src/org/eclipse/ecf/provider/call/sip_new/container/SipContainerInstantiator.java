package org.eclipse.ecf.provider.call.sip_new.container;

import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;

public class SipContainerInstantiator extends GenericContainerInstantiator{
//	public IContainer createInstance(ContainerTypeDescription description,
//			Object[] args) throws ContainerCreateException {
//		
//		SipLocalParticipant localParty=null;
//		if(args!=null){
//			 localParty = new SipLocalParticipant(
//					(SipUriID) new SipUriNamespace()
//							.createInstance(new Object[] { "<sip:"+((ID)args[0]).getName()+">" }),
//					(String)args[1], (String)args[2],(String)args[3]);
//		}else{
//			 localParty = new SipLocalParticipant(
//					(SipUriID) new SipUriNamespace()
//							.createInstance(new Object[] { "<sip:"+"2233369447@sip2sip.infp"+">" }),
//					"Harshana Martin", "abcd","proxy.sipthor.net");
//		}
//		return new SipContainer(localParty);
//	}

}
