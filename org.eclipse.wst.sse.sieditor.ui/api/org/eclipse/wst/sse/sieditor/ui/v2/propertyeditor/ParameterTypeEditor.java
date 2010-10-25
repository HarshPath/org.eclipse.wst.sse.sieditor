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
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ParameterTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ParameterTypeEditorTypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ParameterTypeEditor extends TypePropertyEditor {

    private final ParameterTypeEditorTypeCommitter typeCommitter;

    public ParameterTypeEditor(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
        super(controller, typeDisplayer);
        typeDialogDisplayText = Messages.ParameterTypeEditor_type_dialog_text_set_parameter_type;
        showComplexTypes = true;
        typeCommitter = createParameterTypeCommitter();
    }

    /**
     * Utility method
     * 
     * @return
     */
    protected ParameterTypeEditorTypeCommitter createParameterTypeCommitter() {
        return new ParameterTypeEditorTypeCommitter(this.getSiFormPageController());
    }

    /**
     * utility method
     * 
     * @return
     */
    public SIFormPageController getSiFormPageController() {
        return ((SIFormPageController) getFormPageController());
    }

    @Override
    public ParameterTypeEditorTypeCommitter getTypeCommitter() {
        return typeCommitter;
    }

    @Override
    protected IType getType() {
        final Object modelObject = getInput().getModelObject();
        if (modelObject instanceof IParameter) {
            return ((IParameter) modelObject).getType();
        }
        return null;
    }

    @Override
    protected boolean canNavigateToType() {
        IType selectedType = getSelectedType();
        return selectedType != null && !(EmfXsdUtils.getSchemaForSchemaNS().equals(selectedType.getNamespace()))
                && !(UnresolvedType.instance().equals(selectedType));
    }

    @Override
    public ITypeDialogStrategy createNewTypeDialogStrategy() {
        final ParameterTypeDialogStrategy strategy = new ParameterTypeDialogStrategy();
        strategy.setInput(getInput().getModelObject());
        strategy.setController(getSiFormPageController());
        return strategy;
    }

    @Override
    public void setInput(final ITreeNode input) {
        super.setInput(input);
        getTypeCommitter().setInput(input);
    }
}
