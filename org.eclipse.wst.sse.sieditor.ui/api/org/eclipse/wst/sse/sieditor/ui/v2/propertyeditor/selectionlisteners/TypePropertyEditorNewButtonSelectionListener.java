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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

/**
 * This is the selection listener for the {@link TypePropertyEditor} "New..."
 * button. This listener is responsible for the creation of the
 * {@link NewTypeDialog} and for the creation of the selected type itself
 * 
 * 
 * 
 */
public class TypePropertyEditorNewButtonSelectionListener extends AbstractTypePropertyEditorEventListener {

    public TypePropertyEditorNewButtonSelectionListener(final TypePropertyEditor propertyEditor) {
        super(propertyEditor);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        final ITypeDialogStrategy strategy = getPropertyEditor().createNewTypeDialogStrategy();
        final NewTypeDialog newTypeDialog = createNewTypeDialog(strategy);
        if (newTypeDialog.createAndOpen().isOK()) {
            newType(newTypeDialog.getNewTypeType(), newTypeDialog.getNewTypeName(), strategy);
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    /**
     * utility method. calls the given property editor
     * {@link AbstractFormPageController} new type command
     * 
     * @param newTypeType
     *            - the type of the new type to add:
     *            {@link NewTypeDialog#RADIO_SELECTION_ELEMENT},
     *            {@link NewTypeDialog#RADIO_SELECTION_STRUCTURE_TYPE}, etc.
     * @param newTypeName
     */
    protected void newType(final String newTypeType, final String newTypeName, final ITypeDialogStrategy strategy) {
        final ISchema schema = strategy.getSchema();

        if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(newTypeType)) {
            getPropertyEditor().getFormPageController().newElementType(newTypeName, schema, getPropertyEditor());
        } else if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(newTypeType)) {
            getPropertyEditor().getFormPageController().newSimpleType(newTypeName, schema, getPropertyEditor());
        } else if (NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(newTypeType)) {
            getPropertyEditor().getFormPageController().newStructureType(newTypeName, schema, getPropertyEditor());
        }
    }

    // ===========================================================
    // helpers
    // ===========================================================

    protected UIUtils utils() {
        return UIUtils.instance();
    }

    /**
     * utility method, for testing purposes only
     * 
     * @param strategy
     *            - the strategy to create the dialog with
     * @return the created new type dialog
     */
    protected NewTypeDialog createNewTypeDialog(final ITypeDialogStrategy strategy) {
        return new NewTypeDialog(Display.getCurrent().getActiveShell(), strategy);
    }

}
