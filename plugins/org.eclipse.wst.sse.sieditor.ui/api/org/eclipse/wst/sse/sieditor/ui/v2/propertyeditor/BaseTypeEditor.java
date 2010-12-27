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
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

public class BaseTypeEditor extends TypePropertyEditor {

    protected ITypeCommitter typeCommitter;
    protected final ElementNodeDetailsController detailsController;

    public BaseTypeEditor(final IFormPageController controller, final ITypeDisplayer typeDisplayer,
            final ElementNodeDetailsController detailsController) {
        super(controller, typeDisplayer);
        this.detailsController = detailsController;
        typeDialogDisplayText = Messages.ElementDetailsSection_type_dialog_text_set_base_type;
        showComplexTypes = false;
        typeCommitter = new BaseTypeEditorTypeCommitter();
    }

    @Override
    protected IType getType() {
        return detailsController.getBaseType();
    }

    @Override
    public ITypeCommitter getTypeCommitter() {
        return typeCommitter;
    }

    @Override
    public ITypeDialogStrategy createNewTypeDialogStrategy() {
        final SimpleTypeDialogStrategy strategy = new SimpleTypeDialogStrategy();
        IModelObject inputObject = getInput().getModelObject();

        if (inputObject instanceof IStructureType && ((IStructureType) inputObject).isElement()) {
            // should be always true until setting a base complex type is
            // implemented
            inputObject = ((IStructureType) inputObject).getType();
        }
        strategy.setInput(inputObject);
        strategy.setController(getFormPageController());
        return strategy;
    }

    private class BaseTypeEditorTypeCommitter implements ITypeCommitter {
        @Override
        public void commitType(final IType type) {
            detailsController.setBaseType(type);
        }

        @Override
        public void commitName(final IType type, final String typeName) {
            detailsController.setBaseType(type);
        }

    }

}
