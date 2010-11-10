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

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ISiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ParameterTypeDialogStrategy extends AbstractTypeDialogStrategy {

    @Override
    public ISchema getSchema() {
        final ISchema[] schemas = getTNSSchema();
        if (schemas == null || schemas.length == 0) {
            return null;
        }
        return schemas[0];
    }

    /**
     * Returns the schemas with namespace matching the target namespace of the
     * description
     * 
     * @return
     */
    protected ISchema[] getTNSSchema() {
        final IDescription description = getDescription();
        return description.getSchema(description.getNamespace());
    }

    private IDescription getDescription() {
        IDescription description;
        description = ((IWsdlModelRoot) input.getModelRoot()).getDescription();
        return description;
    }

    public String getDefaultName(final String type) {
        final ISchema[] tnsSchemas = getTNSSchema();

        if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(type)) {
            if (tnsSchemas == null || tnsSchemas.length == 0) {
                return DataTypesFormPageController.ELEMENT_DEFAULT_NAME;
            }

            return getDefaultName(tnsSchemas[0], NewTypeDialog.RADIO_SELECTION_ELEMENT);
        }

        if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(type)) {
            if (tnsSchemas == null || tnsSchemas.length == 0) {
                return DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME;
            }
            return getDefaultName(tnsSchemas[0], NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE);
        }

        if (NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(type)) {
            if (tnsSchemas == null || tnsSchemas.length == 0) {
                return DataTypesFormPageController.STRUCTURE_TYPE_DEFAULT_NAME;
            }
            return getDefaultName(tnsSchemas[0], NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE);
        }
        return UIConstants.EMPTY_STRING;
    }

    public boolean isDuplicateName(final String name, final String type) {
        final ISchema[] tnsSchemas = getTNSSchema();
        if (tnsSchemas == null || tnsSchemas.length == 0) {
            return false;
        }
        return !isGlobalElementNameNotDuplicate(tnsSchemas[0], name, type);
    }

    @Override
    protected IDataTypesFormPageController getDTController() {
        return ((SIFormPageController) controller).getDtController();
    }

    protected ISiEditorDataTypesFormPageController getSIDTController() {
        return (ISiEditorDataTypesFormPageController) ((SIFormPageController) controller).getDtController();
    }

    @Override
    public String getDialogTitle() {
        return Messages.ParameterTypeDialogStrategy_window_title_new_parameter_type_dialog;
    }
}
