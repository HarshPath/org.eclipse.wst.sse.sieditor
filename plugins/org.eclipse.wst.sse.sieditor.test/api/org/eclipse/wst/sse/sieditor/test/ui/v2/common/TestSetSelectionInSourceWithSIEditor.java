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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestSetSelectionInSourceWithSIEditor extends SIEditorBaseTest {
    private static final int Expected_Cursor_Position_After_Switch_From_DTPage_402 = 402;
    private static final String EXPECTED_TEXT_TO_BE_REVEALED = "<wsdl:portType name=\"Service\">"; //$NON-NLS-1$

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLWithRPCandDOcumentStyleBindings.wsdl", //$NON-NLS-1$
                "WSDLWithRPCandDOcumentStyleBindings.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$
    }

    @Test
    public void testPageChnageFromServiceInterfacePageToSource() throws Exception {

        List pages = editor.getPages();

        AbstractEditorPage serviceEditorPage = (AbstractEditorPage) pages.get(0);

        TreeViewer treeViewer = serviceEditorPage.getTreeViewer();

        final TreeItem[] node = { (TreeItem) treeViewer.getTree().getItem(1) };

        StructuredSelection selection = new StructuredSelection((ServiceInterfaceNode) node[0].getData());

        treeViewer.setSelection(selection);

        editor.pageChange(2);

        StructuredTextViewer textViewer = editor.getSourcePage().getTextViewer();
        String textContent = textViewer.getDocument().get();
        int offset = ((org.eclipse.jface.text.TextSelection) textViewer.getSelection()).getOffset();
        char[] realSelection = new char[EXPECTED_TEXT_TO_BE_REVEALED.length()];
        textContent.getChars(offset, offset + EXPECTED_TEXT_TO_BE_REVEALED.length(), realSelection, 0);
        String result = new String(realSelection);

        assertEquals("Expect reveal text: " + EXPECTED_TEXT_TO_BE_REVEALED,//$NON-NLS-1$
                EXPECTED_TEXT_TO_BE_REVEALED, result);

    }

    @Test
    public void testPageChnageFromDataTypesPageToSource() throws Exception {

        // explicit switch to "DataTypes" page
        editor.pageChange(1);

        List pages = editor.getPages();

        AbstractEditorPage dataTypesPage = (AbstractEditorPage) pages.get(1);

        TreeViewer treeViewer = dataTypesPage.getTreeViewer();

        final TreeItem[] node = { (TreeItem) treeViewer.getTree().getItem(0) };

        StructuredSelection selection = new StructuredSelection((ITreeNode) node[0].getData());

        treeViewer.setSelection(selection);

        editor.pageChange(2);

        assertEquals("Expected curesor position after switch from DTPage " //$NON-NLS-1$
                + Expected_Cursor_Position_After_Switch_From_DTPage_402, Expected_Cursor_Position_After_Switch_From_DTPage_402,
                ((org.eclipse.jface.text.TextSelection) editor.getSourcePage().getTextViewer().getSelection()).getOffset());

    }
}
