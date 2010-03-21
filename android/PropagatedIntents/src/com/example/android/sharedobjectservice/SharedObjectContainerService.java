package com.example.android.sharedobjectservice;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.TCPClientSOContainer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SharedObjectContainerService extends Service {


	static class SharedObjectContainerServiceBinder extends Binder implements
			ISharedObjectContainerService {


		public ISharedObjectContainer createClientContainer()
				throws ContainerCreateException {
			return createClientContainer(IDFactory.getDefault().createGUID()
					.getName());
		}

		public ISharedObjectContainer createClientContainer(String clientId)
				throws ContainerCreateException {
			return new TCPClientSOContainer(new SOContainerConfig(IDFactory
					.getDefault().createStringID(clientId)));
		}


	}

	SharedObjectContainerServiceBinder binder = new SharedObjectContainerServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		if (intent.getComponent().getClassName().equals(
				SharedObjectContainerService.class.getName()))
			return binder;
		return null;
	}

}
