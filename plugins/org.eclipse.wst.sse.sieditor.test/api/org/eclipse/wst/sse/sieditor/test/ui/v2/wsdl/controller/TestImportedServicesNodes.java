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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServiceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestImportedServicesNodes extends SIEditorBaseTest {

    private static IWsdlModelRoot wsdlModelRoot = null;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        if (wsdlModelRoot == null) {
            ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

            IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/services/wsdl/stockquote.wsdl", Document_FOLDER_NAME
                    + "/wsdl", this.getProject(), "stockquote.wsdl");
            refreshProjectNFile(file);

            file = ResourceUtils.copyFileIntoTestProject("pub/import/services/wsdl/NewWSDLFile.wsdl", Document_FOLDER_NAME
                    + "/wsdl", this.getProject(), "NewWSDLFile.wsdl");
            refreshProjectNFile(file);

            file = ResourceUtils.copyFileIntoTestProject("pub/import/services/xsd/stockquote.xsd", Document_FOLDER_NAME + "/xsd",
                    this.getProject(), "stockquote.xsd");
            refreshProjectNFile(file);

            wsdlModelRoot = getWSDLModelRoot("pub/import/services/stockquoteservice.wsdl", "stockquoteservice.wsdl");
        }
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testImportedServicesNodes() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());

        ITreeNode importedServicesNode = null;
        ITreeNode imported_Stockquote_ServiceNode = null;
        ITreeNode imported_NewWSDLFile_ServiceNode = null;

        for (final Object node : allServiceInterfaceNodes) {
            if (node instanceof ImportedServicesNode) {
                importedServicesNode = (ITreeNode) node;
            }
        }

        assertNotNull(importedServicesNode);
        assertTrue(importedServicesNode.isImportedNode());
        assertTrue(importedServicesNode.isReadOnly());

        // 1 child "stockquote.wsdl"
        // 2 child "NewWSDLFile.wsdl" - should be displayed even in case that
        // nothing is used from it
        final Object[] importedServicesChildren = importedServicesNode.getChildren();
        assertEquals(2, importedServicesChildren.length);

        imported_Stockquote_ServiceNode = (ITreeNode) importedServicesChildren[0];
        imported_NewWSDLFile_ServiceNode = (ITreeNode) importedServicesChildren[1];

        assertNotNull(imported_Stockquote_ServiceNode);
        assertTrue(imported_Stockquote_ServiceNode.isImportedNode());
        assertTrue(imported_Stockquote_ServiceNode.isReadOnly());
        assertEquals(1, imported_Stockquote_ServiceNode.getChildren().length);

        String importedServiceNodeName = imported_Stockquote_ServiceNode.getDisplayName();
        IDescription importedDescription = (IDescription) imported_Stockquote_ServiceNode.getModelObject();
        String expectedName = "http://example.com/stockquote/definitions (" + importedDescription.getLocation() + ")";
        assertEquals(expectedName, importedServiceNodeName);

        importedServiceNodeName = imported_NewWSDLFile_ServiceNode.getDisplayName();
        importedDescription = (IDescription) imported_NewWSDLFile_ServiceNode.getModelObject();
        expectedName = "http://www.example.org/NewWSDLFile/ (" + importedDescription.getLocation() + ")";
        assertEquals(expectedName, importedServiceNodeName);
    }

    @Test
    public void testOpenInNewEditorForImportedService() throws Exception {
        final SIFormPageControllerExpose controller = new SIFormPageControllerExpose(wsdlModelRoot);

        final IWorkbenchPage activePage = createMock(IWorkbenchPage.class);
        expect(activePage.openEditor((IEditorInput) anyObject(), eq(ServiceInterfaceEditor.EDITOR_ID))).andReturn(null);
        replay(activePage);

        final IWorkbenchWindow window = createMock(IWorkbenchWindow.class);
        expect(window.getActivePage()).andReturn(activePage).anyTimes();
        replay(window);

        controller.window = window;

        final ImportedServiceNode importedServiceNode = getImportedServiceNode(controller);

        controller.openInNewEditor(importedServiceNode);

        // Open editor is expected to happen here
        // with setup input arguments (see above)
        verify(activePage);

        // Warning message dialog is expected to be show
        assertTrue("Warning dialog is wxpected.", controller.showWarningMessageCalls > 0);
    }

    @Test
    public void testOpenInNewDataTypeEditorForImportedType() throws Exception {
        final SIFormPageControllerExpose controller = new SIFormPageControllerExpose(wsdlModelRoot);

        final IWorkbenchPage activePage = createMock(IWorkbenchPage.class);
        expect(activePage.openEditor((IEditorInput) anyObject(), eq(DataTypesEditor.EDITOR_ID))).andReturn(null);
        replay(activePage);

        final IWorkbenchWindow window = createMock(IWorkbenchWindow.class);
        expect(window.getActivePage()).andReturn(activePage).anyTimes();
        replay(window);

        controller.window = window;

        final ImportedServiceNode importedServiceNode = getImportedServiceNode(controller);

        // Get type of input param of imported service
        final IDescription importedDescription = (IDescription) importedServiceNode.getModelObject();
        final IServiceInterface importedInterface = importedDescription.getInterface("StockQuotePortType").get(0);
        final IOperation operation = importedInterface.getOperation("GetLastTradePrice").get(0);
        final IParameter inputParameter = operation.getInputParameter("body").get(0);

        controller.openInNewEditor(inputParameter.getType());

        // Open editor is expected to happen here
        // with setup input arguments (see above)
        verify(activePage);

        // Warning message dialog is expected to be show
        assertTrue("Warning dialog is wxpected.", controller.showWarningMessageCalls > 0);
    }

    private ImportedServiceNode getImportedServiceNode(final SIFormPageController controller) {
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());

        ITreeNode importedServicesNode = null;
        ImportedServiceNode importedServiceNode = null;

        for (final Object node : allServiceInterfaceNodes) {
            if (node instanceof ImportedServicesNode) {
                importedServicesNode = (ITreeNode) node;
            }
        }

        for (final Object node : importedServicesNode.getChildren()) {
            if (node instanceof ImportedServiceNode) {
                importedServiceNode = (ImportedServiceNode) node;
                break;
            }
        }
        return importedServiceNode;
    }

    private class SIFormPageControllerExpose extends SIFormPageController {

        public IWorkbenchWindow window;

        public int showWarningMessageCalls = 0;

        public SIFormPageControllerExpose(final IWsdlModelRoot model) {
            super(model, false, false);
        }

        @Override
        public IWorkbenchWindow getActiveWorkbenchWindow() {
            return window;
        }

        @Override
        protected void showWarningMessage(final IWorkbenchWindow window, final IPreferenceStore preferenceStore) {
            showWarningMessageCalls++;
        }

    }

}
