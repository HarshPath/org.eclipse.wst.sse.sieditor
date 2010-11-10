/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.common;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

/**
 * XSD Schema specific AbstractNotificationCommand
 * 
 * @see AbstractNotificationCommand
 * 
 */
public abstract class AbstractXSDNotificationOperation extends AbstractNotificationOperation {

    /**
     * 
     * @param root
     *            - Model root for getting the listeners
     * @param object
     *            - Changed model object
     */
    public AbstractXSDNotificationOperation(final IXSDModelRoot root, final IModelObject object, String operationLabel) {
        super(root, object, operationLabel);
    }

    public IXSDModelRoot getModelRoot() {
        return (IXSDModelRoot) super.getModelRoot();
    }

}