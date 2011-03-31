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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class TestSetSelectionInDTPageFromSourcePageWithDTE extends SIEditorBaseTest {
    private static final String EXPECTED_SELECTED_ELEMENT = "<xsd:element minOccurs=\"1\" maxOccurs=\"1\" name=\"string\"";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IXSDModelRoot modelRoot = (IXSDModelRoot) getModelRoot("validation/MySchema.xsd", //$NON-NLS-1$
                "MySchema.xsd", DataTypesEditor.EDITOR_ID); //$NON-NLS-1$

    }

    @Test
    public void testPageChnageFromSourcePageToDTPage() throws Exception {

        editor.pageChange(1);// change selected page to source page

        StructuredTextViewer textViewer = editor.getSourcePage().getTextViewer();
        String textContent = textViewer.getDocument().get();
        int selectionStartIndex = textContent.indexOf(EXPECTED_SELECTED_ELEMENT);

        // element with name "string" is going to be selected
        editor.getSourcePage().selectAndReveal(selectionStartIndex + 1, EXPECTED_SELECTED_ELEMENT.length() - 2);

        editor.pageChange(0);// change selected page to DT page

        DataTypesEditorPage dataTypesPage = (DataTypesEditorPage) editor.getPages().get(0);

        IStructuredSelection selection = (IStructuredSelection) dataTypesPage.getTreeViewer().getSelection();

        ITreeNode selectedNode = (ITreeNode) selection.getFirstElement();

        EObject emfCorrespondingComponent = selectedNode.getModelObject().getComponent();

        assertEquals("Selected node in the UI is not Element", emfCorrespondingComponent instanceof XSDParticle, true);
        assertEquals("The XSDParticle is not the expected one", ((XSDParticle) emfCorrespondingComponent)
                .getElement().getAttribute("name").equals("string"), true); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
