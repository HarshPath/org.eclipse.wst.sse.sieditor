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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IDocSectionContainer;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.FaultTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;

public class FaultDetailsPage extends ParameterDetailsPage implements IDocSectionContainer {
    public FaultDetailsPage(SIFormPageController controller, ITypeDisplayer typeDisplayer) {
        super(controller, typeDisplayer);

        this.typeEditor = new FaultTypeEditor(controller, typeDisplayer);
    }

    @Override
    protected void createSections(Composite parent) {

        super.createSections(parent);

        getProblemDecorator().bind(WSDLPackage.Literals.FAULT.getEStructuralFeature(WSDLPackage.FAULT__NAME), nameControl);
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection sSelection = (IStructuredSelection) selection;
            if (sSelection.size() == 1) {
                Object firstElement = sSelection.getFirstElement();
                if (firstElement instanceof FaultNode) {
                    treeNode = (FaultNode) firstElement;
                    input = (IFault) treeNode.getModelObject();
                    typeEditor.setInput(treeNode);
                    getProblemDecorator().setModelObject(input);
                }
            }
        } else {
            input = null;
            typeEditor.setInput(null);
        }
        updateWidgets();
    }
}
