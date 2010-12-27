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
package org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class SimpleTypeDialogStrategy extends AbstractTypeDialogStrategy {

    @Override
    public ISchema getSchema() {
        return getSimpleType().getParent();
    }

    public String getDefaultName(final String type) {
        final ISchema schema = getSimpleType().getParent();
        return getDefaultName(schema, NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE);
    }

    public boolean isDuplicateName(final String name, final String type) {
        final ISchema schema = getSimpleType().getParent();
        return !isGlobalElementNameNotDuplicate(schema, name, NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE);
    }

    @Override
    public boolean isElementEnabled() {
        return false;
    }

    @Override
    public boolean isStructureTypeEnabled() {
        return false;
    }

    @Override
    public String getDialogTitle() {
        return Messages.SimpleTypeDialogStrategy_window_title_new_base_type_dialog;
    }

    protected ISimpleType getSimpleType() {
        return (ISimpleType) input;
    }
}
