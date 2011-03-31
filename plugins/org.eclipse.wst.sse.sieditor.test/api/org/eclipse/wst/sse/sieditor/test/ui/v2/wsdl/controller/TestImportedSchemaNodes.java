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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class TestImportedSchemaNodes extends SIEditorBaseTest {

    private static IXSDModelRoot xsdModelRoot = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        if (xsdModelRoot == null) {
            ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

            IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/simple/as2_schema3.xsd", Document_FOLDER_NAME, this
                    .getProject(), "as2_schema3.xsd");
            refreshProjectNFile(file);

            xsdModelRoot = getXSDModelRoot("pub/import/simple/as2_schema2.xsd", "as2_schema2.xsd");
        }
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testOpenInNewEditorForImportedSchema() throws Exception {
        DataTypesFormPageControllerExpose controller = new DataTypesFormPageControllerExpose(xsdModelRoot);
        DataTypesContentProvider contentProvider = new DataTypesContentProvider(controller);

        IWorkbenchPage activePage = createMock(IWorkbenchPage.class);
        expect(activePage.openEditor((IEditorInput) anyObject(), eq(DataTypesEditor.EDITOR_ID))).andReturn(null);
        replay(activePage);

        IWorkbenchWindow window = createMock(IWorkbenchWindow.class);
        expect(window.getActivePage()).andReturn(activePage).anyTimes();
        replay(window);

        controller.window = window;

        ImportedSchemaNode importedSchemaNode = getImportedServiceNode(contentProvider);

        controller.openInNewEditor(importedSchemaNode);

        // Open editor is expected to happen here
        // with setup input arguments (see above)
        verify(activePage);

        // Warning message dialog is expected to be show
        assertTrue("Warning dialog is wxpected.", controller.showWarningMessageCalls > 0);
    }

    @Test
    public void testImportedTypesNodeDoesNotCollapseOnTreeRefresh() throws ExecutionException {
        DataTypesFormPageControllerExpose controller = new DataTypesFormPageControllerExpose(xsdModelRoot);
        DataTypesContentProvider contentProvider = new DataTypesContentProvider(controller);

        final boolean refresh_Called[] = { false };

        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        final TreeViewer treeViewerMock = new TreeViewer(new Composite(shell, SWT.None)) {
            @Override
            public void refresh() {
                super.refresh();
                refresh_Called[0] = true;

            }
        };
        treeViewerMock.setContentProvider(contentProvider);
        treeViewerMock.setInput(xsdModelRoot);

        treeViewerMock.expandAll();

        ArrayList<Object> expandedElementsBefore = new ArrayList<Object>(Arrays.asList(treeViewerMock.getExpandedElements()));

        AddSimpleTypeCommand addSimpleTypeCommand = new AddSimpleTypeCommand(xsdModelRoot, xsdModelRoot.getSchema(),
                "mySimpleType");
        IStatus executeStatus = xsdModelRoot.getEnv().execute(addSimpleTypeCommand);
        
        assertEquals("Add Simpletype command execution failed.", 
                IStatus.OK, 
                executeStatus.getSeverity());

        AbstractMasterDetailsBlock masterDetailsBlock = new AbstractMasterDetailsBlock() {
            {
                this.treeViewer = treeViewerMock;
            }

            @Override
            protected void createButtons(FormToolkit toolkit, Composite buttonsComposite) {
            }

            @Override
            protected IDetailsPageProvider createDetailsPageProvider() {
                return null;
            }

            @Override
            protected Button getRemoveButton() {
                return null;
            }

            @Override
            protected void removePressed() {
            }

            @Override
            public void componentChanged(IModelChangeEvent event) {
            }

            @Override
            protected void updateButtonsState(IStructuredSelection structSelection) {
                // TODO Auto-generated method stub
                
            }
        };
        masterDetailsBlock.refreshTreeViewer();
        ArrayList<Object> expandedElementsAfter = new ArrayList<Object>(Arrays.asList(treeViewerMock.getExpandedElements()));
        //here we remove the new added item, because we want to check the expanded state only of the old elements, not the new one 
        expandedElementsAfter.remove(0);
        assertTrue("Expected tree viewer refresh call.", refresh_Called[0]);
        assertArrayEquals("It is expected that tree viewer remembers its state after refresh", expandedElementsBefore.toArray(),
             expandedElementsAfter.toArray()  );
    }

    private ImportedSchemaNode getImportedServiceNode(DataTypesContentProvider contentProvider) {
        Object[] allNodes = contentProvider.getElements(xsdModelRoot);

        ITreeNode importedTypesNode = null;
        ImportedSchemaNode importedSchemaNode = null;

        for (Object node : allNodes) {
            if (node instanceof ImportedTypesNode) {
                importedTypesNode = (ITreeNode) node;
            }
        }

        for (Object node : importedTypesNode.getChildren()) {
            if (node instanceof ImportedSchemaNode) {
                importedSchemaNode = (ImportedSchemaNode) node;
                break;
            }
        }
        return importedSchemaNode;
    }

    private class DataTypesFormPageControllerExpose extends DataTypesFormPageController {

        public IWorkbenchWindow window;

        public int showWarningMessageCalls = 0;

        public DataTypesFormPageControllerExpose(IXSDModelRoot model) {
            super(model, false);
        }

        @Override
        public IWorkbenchWindow getActiveWorkbenchWindow() {
            return window;
        }

        @Override
        protected void showWarningMessage(IWorkbenchWindow window, IPreferenceStore preferenceStore) {
            showWarningMessageCalls++;
        }

    }

}
