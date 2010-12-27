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

import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.FaultTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class FaultTypeEditor extends ParameterTypeEditor {

    public FaultTypeEditor(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
        super(controller, typeDisplayer);

    }

    @Override
    public Control createControl(final FormToolkit toolkit, final Composite parent) {
        final Control control = super.createControl(toolkit, parent);
        typeCombo.setEnabled(false);
        typeCombo.setEditable(false);
        return control;
    }

    @Override
    protected IType getType() {
        final Object modelObject = getInput().getModelObject();
        if (modelObject instanceof IFault) {
            final Iterator<IParameter> faultParamsIterator = ((IFault) modelObject).getParameters().iterator();
            if(!faultParamsIterator.hasNext()){
                    return null;
            }
            final IParameter faultParameter = faultParamsIterator.next();
            if (faultParameter != null) {
                return faultParameter.getType();
            }
        }
        return null;
    }

    @Override
    public void update() {
        super.update();
        typeCombo.setEnabled(false);
        typeCombo.setEditable(false);
    }

    @Override
    public ITypeDialogStrategy createNewTypeDialogStrategy() {
        final FaultTypeDialogStrategy strategy = new FaultTypeDialogStrategy();
        strategy.setInput(getInput().getModelObject());
        strategy.setController(getSiFormPageController());
        return strategy;
    }

}
