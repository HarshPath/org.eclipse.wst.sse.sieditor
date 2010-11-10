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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners;

import org.eclipse.swt.events.SelectionListener;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;

/**
 * This is the base {@link TypePropertyEditor} button selection listener.
 * Subclasses are responsible for the event execution handling
 * 
 * 
 * 
 */
public abstract class AbstractTypePropertyEditorEventListener implements SelectionListener {

    private final TypePropertyEditor propertyEditor;

    public AbstractTypePropertyEditorEventListener(final TypePropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    protected void setType(final IType type) {
        if (type != null) {
            // don't set the type name , the refresh should do it.
            // typeCombo.setText(selectedType.getName());

            // does not set the dirty flag to true, because the
            // change is immediate
            getPropertyEditor().setSelectedType(type);
            getPropertyEditor().getTypeCommitter().commitType(type);
        }
    }

    protected void setType(final String coreSimpleTypeName) {
        getPropertyEditor().setSelectedTypeName(coreSimpleTypeName);
        final IType selectedType = getSelectedType(coreSimpleTypeName);
        getPropertyEditor().setSelectedType(selectedType);
        if (getPropertyEditor().isStale()) {
            getPropertyEditor().getTypeCommitter().commitName(selectedType, getPropertyEditor().getSelectedTypeName());
        }
    }

    /**
     * utility method. opens the types dialog and sets the selection
     */
    protected void chooseType() {
        final IType dialogResult = openTypesDialog();
        setType(dialogResult);
    }

    protected TypePropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    protected IType openTypesDialog() {
        final AbstractFormPageController controller = getPropertyEditor().getFormPageController();
        final String typeDialogDisplayText = getPropertyEditor().getTypeDialogDisplayText();
        final ITreeNode editorInput = getPropertyEditor().getInput();
        final IModelObject modelObject = editorInput.getModelObject();
        final boolean showComplexTypes = getPropertyEditor().showComplexTypes();

        final IType dialogResult = controller.openTypesDialog(typeDialogDisplayText, modelObject, showComplexTypes);
        return dialogResult;
    }

    // ===========================================================
    // helpers
    // ===========================================================

    /**
     * utility method, for testing purposes
     * 
     * @param coreSimpleTypeName
     * @return
     */
    protected IType getSelectedType(final String coreSimpleTypeName) {
        return BuiltinTypesHelper.getInstance().getCommonBuiltinType(coreSimpleTypeName);
    }

}
