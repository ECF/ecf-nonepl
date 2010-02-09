package com.example.android.sharedobjectservice;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;

public interface ISharedObjectContainerService {

	public ISharedObjectContainer createClientContainer() throws ContainerCreateException;

	public ISharedObjectContainer createClientContainer(String clientId) throws ContainerCreateException;
}
