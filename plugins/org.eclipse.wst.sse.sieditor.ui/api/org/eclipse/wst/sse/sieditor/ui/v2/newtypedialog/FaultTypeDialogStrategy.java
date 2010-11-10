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
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;

public class FaultTypeDialogStrategy extends ParameterTypeDialogStrategy {

    @Override
    public boolean isElementEnabled() {
        return true;
    }

    @Override
    public boolean isStructureTypeEnabled() {
        return false;
    }

    @Override
    public boolean isSimpleTypeEnabled() {
        return false;
    }

    @Override
    public String getDialogTitle() {
        return Messages.FaultTypeDialogStrategy_DialogTitle;
    }

    public String getDefaultName(String type) {
        final ISchema[] tnsSchemas = getTNSSchema();

        if (tnsSchemas == null || tnsSchemas.length == 0) {
            return DataTypesFormPageController.FAULT_ELEMENT_DEFAULT_NAME;
        }

        return getDefaultName(tnsSchemas[0], NewTypeDialog.RADIO_SELECTION_FAULT_ELEMENT);
    }
}
