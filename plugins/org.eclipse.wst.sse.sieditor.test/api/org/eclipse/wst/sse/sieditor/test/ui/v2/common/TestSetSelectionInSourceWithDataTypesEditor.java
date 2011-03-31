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
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class TestSetSelectionInSourceWithDataTypesEditor extends SIEditorBaseTest {
    private static final int Expected_Cursor_Position_After_Swtich_To_SourcePage_352 = 352;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IXSDModelRoot modelRoot = (IXSDModelRoot) getModelRoot("validation/imports/Schema1.xsd", //$NON-NLS-1$
                "Schema1.xsd", DataTypesEditor.EDITOR_ID); //$NON-NLS-1$

    }

    @Test
    public void testPageChnageFromServiceInterfacePageToSource() throws Exception {

        List pages = editor.getPages();

        AbstractEditorPage serviceEditorPage = (AbstractEditorPage) pages.get(0);

        TreeViewer treeViewer = serviceEditorPage.getTreeViewer();

        final TreeItem[] node = { (TreeItem) treeViewer.getTree().getItem(0) };

        StructuredSelection selection = new StructuredSelection((StructureTypeNode) node[0].getData());

        treeViewer.setSelection(selection);

        editor.pageChange(1);

        assertEquals("Expected cursor position after swtich to SourcePage" //$NON-NLS-1$
                + Expected_Cursor_Position_After_Swtich_To_SourcePage_352,
                Expected_Cursor_Position_After_Swtich_To_SourcePage_352, ((org.eclipse.jface.text.TextSelection) editor
                        .getSourcePage().getTextViewer().getSelection()).getOffset());

    }
}
