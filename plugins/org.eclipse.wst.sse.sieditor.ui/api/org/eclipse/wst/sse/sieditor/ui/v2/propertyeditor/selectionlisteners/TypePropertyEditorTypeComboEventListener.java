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
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;

/**
 * This is the type combo box selection listener of the
 * {@link TypePropertyEditor}. This selection listener should be called when the
 * type combo selection has been changed
 * 
 */
public class TypePropertyEditorTypeComboEventListener extends AbstractTypePropertyEditorEventListener implements FocusListener {

    public TypePropertyEditorTypeComboEventListener(final TypePropertyEditor propertyEditor) {
        super(propertyEditor);
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
    }

    @Override
    public void focusGained(final FocusEvent e) {
    }

    @Override
    public void focusLost(final FocusEvent e) {
        final CCombo typeCombo = getPropertyEditor().getTypeCombo();

        if (typeCombo.getSelectionIndex() == -1) {
            return;
        }

        final String newSelection = typeCombo.getItem(typeCombo.getSelectionIndex());
        if (Messages.TypePropertyEditor_browse_button.equals(newSelection)) {
            selectType(typeCombo);
        } else {
            setType(newSelection);
        }
    }

    /**
     * Utility method. it processes the selection of a new type, because no
     * specific type was selected - e.g. the "browse..." type was selected
     * 
     * @param typeCombo
     */
    private void selectType(final CCombo typeCombo) {
        updateUI(typeCombo);
        chooseType();
    }

    /**
     * utility method, updates the comboBox to show the selected type, not the
     * "browse..." text
     * 
     * @param typeCombo
     */
    private void updateUI(final CCombo typeCombo) {
        final IType selectedType = getPropertyEditor().getSelectedType();
        final ITreeNode input = getPropertyEditor().getInput();

        final String selectedTypeName = getSelectedTypeName(selectedType, input);
        typeCombo.setText(selectedTypeName.trim());
    }

    /**
     * utility helper method. for testing purposes.
     * 
     * @param selectedType
     * @param input
     * @return the selected type name
     */
    protected String getSelectedTypeName(final IType selectedType, final ITreeNode input) {
        if (input == null) {
            return null;
        }
        return input.getDisplayName();
    }

}
