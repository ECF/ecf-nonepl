/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.jms.container;

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.IQueueEnqueue;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.generic.SOContext;

/**
 * @author slewis
 *
 */
public class JMSContainerContext extends SOContext {

    public JMSContainerContext(ID objID, ID homeID, SOContainer cont, Map props, IQueueEnqueue queue) {
        super(objID, homeID, cont, props, queue);
    }
    
}
