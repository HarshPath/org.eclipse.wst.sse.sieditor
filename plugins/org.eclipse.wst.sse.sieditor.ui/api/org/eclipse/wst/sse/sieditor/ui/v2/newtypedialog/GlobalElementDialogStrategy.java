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

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class GlobalElementDialogStrategy extends AbstractTypeDialogStrategy {

    protected IStructureType getElement() {
        return (IStructureType) input;
    }

    @Override
    public boolean isElementEnabled() {
        return false;
    }

    public String getDefaultName(final String type) {
        final ISchema schema = getElement().getParent();
        return getDefaultName(schema, type);

    }

    public boolean isDuplicateName(final String name, final String type) {
        final ISchema schema = getElement().getParent();
        return !isGlobalElementNameNotDuplicate(schema, name, type);
    }

    @Override
    public ISchema getSchema() {
        return getElement().getParent();
    }

    @Override
    public String getDialogTitle() {
        return Messages.GlobalElementDialogStrategy_dlt_title;
    }
}
