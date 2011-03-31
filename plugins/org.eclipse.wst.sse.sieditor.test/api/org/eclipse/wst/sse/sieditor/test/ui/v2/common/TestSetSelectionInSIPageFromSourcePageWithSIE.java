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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestSetSelectionInSIPageFromSourcePageWithSIE extends SIEditorBaseTest {
    private static final String PORT_TYPE_FOR_SELECTION = "<wsdl:portType name=\"Service\">"; //$NON-NLS-1$
    private static final String PART_FOR_SELECTION = "<wsdl:part element=\"tns:Element3\" name=\"ServerVersion\" />"; //$NON-NLS-1$
    private static final String ELEMENT_FOR_SELECTION_IN_DTPAGE = "<xsd:element name=\"in\" type=\"xsd:string\" />"; //$NON-NLS-1$

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLWithRPCandDOcumentStyleBindings.wsdl", //$NON-NLS-1$
                "WSDLWithRPCandDOcumentStyleBindings.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$
    }

    @Test
    public void testPageChangeFromSourceToSIPageWithSelectedPortType() throws Exception {
        editor.pageChange(2);// change selected page to source page

        StructuredTextViewer textViewer = editor.getSourcePage().getTextViewer();
        String textContent = textViewer.getDocument().get();
        int selectionStartIndex = textContent.indexOf(PORT_TYPE_FOR_SELECTION);

        // portType with name Service is  going to be selected
        editor.getSourcePage().selectAndReveal(selectionStartIndex + 1, PORT_TYPE_FOR_SELECTION.length() - 2);

        editor.pageChange(0);// change selected page to SI page

        ServiceIntefaceEditorPage serviceEditorPage = (ServiceIntefaceEditorPage) editor.getPages().get(0);

        IStructuredSelection selection = (IStructuredSelection) serviceEditorPage.getTreeViewer().getSelection();

        ITreeNode selectedNode = (ITreeNode) selection.getFirstElement();

        EObject emfCorrespondingComponent = selectedNode.getModelObject().getComponent();

        assertEquals("Selected node in the UI is not PortType", emfCorrespondingComponent instanceof PortType, true);
        assertEquals("The PortType is not the expected one", ((PortType) emfCorrespondingComponent).getElement().getAttribute(
                "name").equals("Service"), true); //$NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testPageChnageFromSurceToSIPageWithSelectedPart() throws Exception {
        editor.pageChange(2);// change selected page to source page

        StructuredTextViewer textViewer = editor.getSourcePage().getTextViewer();
        String textContent = textViewer.getDocument().get();
        int selectionStartIndex = textContent.indexOf(PART_FOR_SELECTION);

        // part with name "ServiceVersion" is going to be selected
        editor.getSourcePage().selectAndReveal(selectionStartIndex + 1, PART_FOR_SELECTION.length() - 2);

        editor.pageChange(0);// change selected page to SI page

        ServiceIntefaceEditorPage serviceEditorPage = (ServiceIntefaceEditorPage) editor.getPages().get(0);

        IStructuredSelection selection = (IStructuredSelection) serviceEditorPage.getTreeViewer().getSelection();

        ITreeNode selectedNode = (ITreeNode) selection.getFirstElement();

        EObject emfCorrespondingComponent = selectedNode.getModelObject().getComponent();

        assertEquals("Selected node in the UI is not a parameter", emfCorrespondingComponent instanceof Part, true);
        assertEquals("The Part is not the expected one", ((Part) emfCorrespondingComponent).getElement().getAttribute("name") //$NON-NLS-2$
                .equals("ServerVersion"), true); //$NON-NLS-1$

    }

    @Test
    public void testPageChnageFromSurceToDTPageWithSelectedElement() throws Exception {
        editor.pageChange(2);// change selected page to source page

        StructuredTextViewer textViewer = editor.getSourcePage().getTextViewer();
        String textContent = textViewer.getDocument().get();
        int selectionStartIndex = textContent.indexOf(ELEMENT_FOR_SELECTION_IN_DTPAGE);

        // element with name "in" is going to be selected
        editor.getSourcePage().selectAndReveal(selectionStartIndex + 1, ELEMENT_FOR_SELECTION_IN_DTPAGE.length() - 2);

        editor.pageChange(1);// change selected page to DT page

        DataTypesEditorPage serviceEditorPage = (DataTypesEditorPage) editor.getPages().get(1);

        IStructuredSelection selection = (IStructuredSelection) serviceEditorPage.getTreeViewer().getSelection();

        ITreeNode selectedNode = (ITreeNode) selection.getFirstElement();

        EObject emfCorrespondingComponent = selectedNode.getModelObject().getComponent();

        assertEquals("Selected node in the UI is not a XSDParticle",
                emfCorrespondingComponent instanceof XSDParticle, true);
        assertEquals(
                "The XSDParticle is not the expected one", ((XSDParticle) emfCorrespondingComponent).getElement().getAttribute("name") //$NON-NLS-2$
                        .equals("in"), true); //$NON-NLS-1$

    }
}
