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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.GlobalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

public class StructureTypeEditor extends BaseTypeEditor {

    public StructureTypeEditor(final IFormPageController controller, final ITypeDisplayer typeDisplayer,
            final ElementNodeDetailsController detailsController) {
        super(controller, typeDisplayer, detailsController);

        typeDialogDisplayText = Messages.ElementDetailsSection_type_dialog_text_set_base_type;
        showComplexTypes = true;

        typeCommitter = new ITypeCommitter() {
            @Override
            public void commitType(final IType type) {
                detailsController.setBaseType(type);
            }

            @Override
            public void commitName(final IType type, final String typeName) {
                commitType(type);
            }
        };
    }

    @Override
    public ITypeDialogStrategy createNewTypeDialogStrategy() {
        final GlobalElementDialogStrategy strategy = new GlobalElementDialogStrategy();
        final IModelObject inputObject = getInput().getModelObject();

        strategy.setInput(inputObject);
        strategy.setController(getFormPageController());
        return strategy;
    }

    @Override
    public Control createControl(final FormToolkit toolkit, final Composite parent) {
        final Control control = super.createControl(toolkit, parent);
        typeLink.setText(Messages.ElementDetailsSection_baseType);
        return control;
    }
    
//    @Override
//    protected boolean isNewTypeButtonEnabled() {
//        return false;
//    }

}
