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
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AbstractTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AttributeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.GlobalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.LocalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ElementTypeEditorTypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

public class ElementTypeEditor extends TypePropertyEditor {

    private ElementNodeDetailsController detailsController;
    private final ElementTypeEditorTypeCommitter typeCommitter;

    public ElementTypeEditor(final ElementNodeDetailsController detailsController, final ITypeDisplayer typeDisplayer) {
        super((AbstractFormPageController) detailsController.getFormPageController(), typeDisplayer);
        typeDialogDisplayText = Messages.ElementTypeEditor_type_dialog_text_set_element_type;
        showComplexTypes = true;
        typeCommitter = new ElementTypeEditorTypeCommitter(null);
        setDetailsController(detailsController);
    }

    @Override
    protected IType getType() {
        if (detailsController != null) {
            return detailsController.getType();
        }
        return null;
    }

    public void setDetailsController(final ElementNodeDetailsController detailsController) {
        this.detailsController = detailsController;
        typeCommitter.setDetailsController(detailsController);
    }

    @Override
    public boolean isStale() {
        return super.isStale();// getType() != selectedType;
    }

    @Override
    public ITypeDialogStrategy createNewTypeDialogStrategy() {
    	ITreeNode input = detailsController.getInput();
        IModelObject modelObject = input == null ? null : input.getModelObject();
        AbstractTypeDialogStrategy strategy = null;
        
        if (modelObject instanceof IStructureType && ((IStructureType) modelObject).isElement()) {
            strategy = new GlobalElementDialogStrategy();
        } else if (modelObject instanceof IElement) {
        	if (((IElement) modelObject).isAttribute()) {
        		strategy = new AttributeDialogStrategy();
        	} else {
        		strategy = new LocalElementDialogStrategy();
        	}
        }
        
        if (strategy != null) {
    		strategy.setInput((IModelObject) modelObject);
    		strategy.setController(getFormPageController());
        }
        
        return strategy;
    }
    
    @Override
    public ITypeCommitter getTypeCommitter() {
        return typeCommitter;
    }
}
