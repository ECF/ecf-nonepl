/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.mgmt.ds;

public interface IReferenceInfo {

    String getName();

    String getServiceName();

    long[] getIds();

    boolean isSatisfied();

    boolean isOptional();

    boolean isMultiple();

    boolean isStatic();

    String getTarget();

    String getBindMethodName();

    String getUnbindMethodName();

}
