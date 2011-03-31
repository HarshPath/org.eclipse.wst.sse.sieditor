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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.ElementDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

public class ElementDetailsSectionRefreshFromRefToNonRefReLayoutTest {

    private ElementNodeDetailsController controller;
    private FormToolkit toolkit;
    private IManagedForm managedForm;
    private ITypeDisplayer typeDisplayer;
    private Shell shell;

    @Before
    public void setUp() throws Exception {
        final DataTypesFormPageController dataTypesController = createNiceMock(DataTypesFormPageController.class);
        expect(dataTypesController.getCommonTypesDropDownList()).andReturn(new String[] { "primitive" }).atLeastOnce(); //$NON-NLS-1$

        expect(dataTypesController.isPartOfEdittedDocument((IModelObject) anyObject())).andReturn(true).anyTimes();
        replay(dataTypesController);

        controller = createNiceMock(ElementNodeDetailsController.class);
        expect(controller.getFormPageController()).andReturn(dataTypesController).atLeastOnce();

        final Display display = Display.getDefault();
        toolkit = new FormToolkit(display);
        managedForm = createMock(IManagedForm.class);
        typeDisplayer = createMock(ITypeDisplayer.class);
        shell = new Shell(display);
    }

    @Test
    public void testRefresh() {
        expect(controller.getName()).andReturn("name").atLeastOnce();
        expect(controller.isNillable()).andReturn(true).atLeastOnce();
        expect(controller.getCardinality()).andReturn(CardinalityType.ONE_TO_ONE).atLeastOnce();
        expect(controller.isNameEditable()).andReturn(true).atLeastOnce();
        expect(controller.isNillableEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityVisible()).andReturn(true).atLeastOnce();
        expect(controller.isNillableVisible()).andReturn(true).atLeastOnce();
        expect(controller.isTypeApplicable()).andReturn(true).atLeastOnce();
        expect(controller.isBaseTypeApplicable()).andReturn(true).atLeastOnce();
        replay(controller);

        final IElement refElement = EasymockModelUtils.createReferingIElementMockFromSameModel();
        final ITreeNode refNode = createNiceMock(ITreeNode.class);
        expect(refNode.getModelObject()).andReturn(refElement).anyTimes();
        expect(refNode.isReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();

        final IElement localElement = EasymockModelUtils.createIElementMockFromSameModel();
        final ITreeNode typeNode = createMock(ITreeNode.class);
        expect(typeNode.getModelObject()).andReturn(localElement).anyTimes();
        expect(typeNode.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();

        replay(typeDisplayer, refNode, typeNode, refElement, localElement);

        final boolean[] redrawCalled = { false };

        final ElementDetailsSection sectionModified = new ElementDetailsSection(controller, toolkit, managedForm, typeDisplayer) {
            @Override
            protected ITreeNode getNode() {
                return refNode;
            }

            @Override
            protected void redrawSection() {
                redrawCalled[0] = true;
            }

            @Override
            protected boolean isEditable() {
                return false;
            }

            @Override
            protected boolean isWritableElementReference() {
                return false;
            }
        };
        sectionModified.createContents(shell);
        sectionModified.selectionChanged(null, new StructuredSelection(new ITreeNode[] { refNode }));
        //no redraw on same element
        assertFalse(redrawCalled[0]);
        redrawCalled[0] = false;
        sectionModified.selectionChanged(null, new StructuredSelection(new ITreeNode[] { typeNode }));
        assertTrue(redrawCalled[0]);
    }


}
