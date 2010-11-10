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

import java.text.MessageFormat;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;

public abstract class AbstractTypeDialogStrategy implements ITypeDialogStrategy {

    protected IFormPageController controller;
    protected IModelObject input;

    protected static String getDefaultName(final ISchema schema, final String type) {
        if (NewTypeDialog.RADIO_SELECTION_FAULT_ELEMENT.equals(type)) {
            return DataTypesFormPageController.getNewFaultElementDefaultName(schema);
        }
        if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(type)) {
            return DataTypesFormPageController.getNewElementDefaultName(schema);
        }

        if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(type)) {
            return DataTypesFormPageController.getNewSimpleTypeDefaultName(schema);
        }
        if (NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(type)) {
            return DataTypesFormPageController.getNewStructureTypeDefaultName(schema);
        }
        return UIConstants.EMPTY_STRING;
    }

    protected static boolean isGlobalElementNameNotDuplicate(final ISchema schema, final String name, final String elementType) {
        final IType[] allTypes = schema.getAllTypes(name);
        if (allTypes == null) {
            return true;
        }
        for (final IType currentType : allTypes) {
            if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(elementType)
                    || NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(elementType)) {
                if (currentType instanceof ISimpleType) {
                    return false;
                }
                if (currentType instanceof IStructureType && !((IStructureType) currentType).isElement()) {
                    return false;
                }
            }
            if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(elementType)) {
                if (currentType instanceof IStructureType && ((IStructureType) currentType).isElement()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setInput(final IModelObject input) {
        this.input = input;
    }

    @Override
    public String getDialogTitle() {
        return Messages.AbstractTypeDialogStrategy_default_new_type_dialog_title;
    }

    @Override
    public boolean isElementEnabled() {
        return true;
    }

    @Override
    public boolean isSimpleTypeEnabled() {
        return true;
    }

    @Override
    public boolean isStructureTypeEnabled() {
        return true;
    }

    public void setController(final IFormPageController controller) {
        this.controller = controller;
    }

    protected IDataTypesFormPageController getDTController() {
        return (IDataTypesFormPageController) controller;
    }

    /**
     * Default implementation for global types
     */
    public String getDuplicateNameErrorMessage(final String type) {
        String elementType = UIConstants.EMPTY_STRING;
        if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(type)) {
            elementType = Messages.AbstractTypeDialogStrategy_msg_error_element;
        } else if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(type)) {
            elementType = Messages.AbstractTypeDialogStrategy_msg_error_simple_type;
        } else if (NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(type)) {
            elementType = Messages.AbstractTypeDialogStrategy_msg_error_structure_type;
        } else {
            return Messages.AbstractTypeDialogStrategy_msg_error_duplicate_element;
        }
        return MessageFormat.format(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_X, elementType);
    }
}
