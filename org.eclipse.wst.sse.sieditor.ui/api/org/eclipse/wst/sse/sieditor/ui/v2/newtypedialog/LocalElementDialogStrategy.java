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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

public class LocalElementDialogStrategy extends AbstractTypeDialogStrategy {

    @Override
    public ISchema getSchema() {
        return findParentSchema();
    }

    public String getDefaultName(final String type) {
        final ISchema schema = findParentSchema();
        if (schema != null) {
            return getDefaultName(schema, type);
        }
        return UIConstants.EMPTY_STRING;
    }

    public boolean isDuplicateName(final String name, final String type) {
        final ISchema parent = findParentSchema();
        if (parent != null) {
            return !isGlobalElementNameNotDuplicate(parent, name, type);
        }
        throw new IllegalStateException("input has no ISchema in it's hierarchy tree"); //$NON-NLS-1$
    }

    protected ISchema findParentSchema() {
        IModelObject parent = input.getParent();
        while (parent != null && !(parent instanceof ISchema)) {
            parent = parent.getParent();
        }
        return (ISchema) parent;
    }
}
