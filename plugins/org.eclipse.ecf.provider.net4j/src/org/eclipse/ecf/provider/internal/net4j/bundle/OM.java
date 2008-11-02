/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper (Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.ecf.provider.internal.net4j.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.core.runtime.IAdapterManager;

import org.osgi.util.tracker.ServiceTracker;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.ecf.provider.net4j"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static IAdapterManager getAdapterManager()
  {
    if (Activator.adapterManagerTracker != null)
    {
      return (IAdapterManager)Activator.adapterManagerTracker.getService();
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    static ServiceTracker adapterManagerTracker;

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      super.doStart();
      adapterManagerTracker = new ServiceTracker(bundleContext, IAdapterManager.class.getName(), null);
      adapterManagerTracker.open();
    }

    @Override
    protected void doStop() throws Exception
    {
      adapterManagerTracker.close();
      adapterManagerTracker = null;
      super.doStop();
    }
  }
}
