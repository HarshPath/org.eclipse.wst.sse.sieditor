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
package org.eclipse.wst.sse.sieditor.test.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSTmodelInSyncWithDOM extends SIEditorBaseTest {

    AbstractEditorWithSourcePage editor = null;

    @Override
    @After
    public void tearDown() throws Exception {
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);
    }

    @Test
    public void testDOMusesSIEnotifyerForDTE() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
        assertFalse(editor.isDirty());

        final XMLModelNotifierWrapper modelNotifier = editor.getModelNotifier();

        final IXSDModelRoot modelRoot = (IXSDModelRoot) editor.getModelRoot();
        final XSDSchema xsdSchema = modelRoot.getSchema().getComponent();
        final Document document = xsdSchema.getDocument();
        final Element documentElement = document.getDocumentElement();

        modelNotifier.getChangedNodes().clear();
        modelNotifier.getCollectedNotifications().clear();

        final String attributeTNS = documentElement.getAttribute("targetNamespace");
        documentElement.setAttribute("targetNamespace", attributeTNS + "new");

        assertEquals(1, modelNotifier.getChangedNodes().size());
        // Assert that there are notifications collected from the changes to the
        // EMF model.
        // TEMPORARY COMMENT CHECK, UNTIL CONCRETE IMPLEMENTATION IS DESIGNED
        // assertTrue(0 != modelNotifier.getCollectedNotifications().size());
    }

    @Test
    public void testDOMusesSIEnotifyerForSIE() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/simple/NewWSDLFile.wsdl", Document_FOLDER_NAME, this
                .getProject(), "NewWSDLFile.wsdl");
        refreshProjectNFile(file);

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (ServiceInterfaceEditor) workbenchActivePage.openEditor(eInput, ServiceInterfaceEditor.EDITOR_ID);
        assertFalse(editor.isDirty());

        final XMLModelNotifierWrapper modelNotifier = editor.getModelNotifier();

        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) editor.getModelRoot();
        final ISchema schema = modelRoot.getDescription().getSchema("http://www.example.org/NewWSDLFile/")[0];
        final XSDSchema xsdSchema = schema.getComponent();
        final Document document = xsdSchema.getDocument();
        final Element documentElement = document.getDocumentElement();

        modelNotifier.getChangedNodes().clear();
        modelNotifier.getCollectedNotifications().clear();

        final String attributeTNS = documentElement.getAttribute("targetNamespace");
        documentElement.setAttribute("targetNamespace", attributeTNS + "new");

        assertEquals(1, modelNotifier.getChangedNodes().size());
        // Assert that there are notifications collected from the changes to the
        // EMF model.
        // TEMPORARY COMMENT CHECK, UNTIL CONCRETE IMPLEMENTATION IS DESIGNED
        // assertTrue(0 != modelNotifier.getCollectedNotifications().size());
    }

    @Test
    public void testRenamingXSDTypeDoesNotBreakTypeReferencers() throws Exception {
        final IXSDModelRoot modelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");

        final IType addressType = modelRoot.getSchema().getType(false, "Address");
        final IStructureType purchaseOrderType = (IStructureType) modelRoot.getSchema().getType(false, "PurchaseOrderType");
        final IElement shipTo = purchaseOrderType.getElements("shipTo").iterator().next();

        assertEquals(StructureType.class, shipTo.getType().getClass());

        // Change Address name
        final RenameStructureTypeCommand renameCommand = new RenameStructureTypeCommand(modelRoot, (IStructureType) shipTo
                .getType(), "Address22");
        modelRoot.getEnv().execute(renameCommand);

        assertEquals("Address22", addressType.getName());
        assertFalse(UnresolvedType.instance().equals(shipTo.getType()));
        assertEquals("Address22", shipTo.getType().getName());
    }

    @Test
    public void testRenamingXSDTypeDoesNotBreakWSDLPartReferencers() throws Exception {
        final IWsdlModelRoot modelRoot = getWSDLModelRoot("pub/simple/NewWSDLFile.wsdl", "NewWSDLFile.wsdl");

        final ISchema schema = modelRoot.getDescription().getSchema("http://www.example.org/NewWSDLFile/")[0];
        final IType newOperationType = schema.getType(true, "NewOperation");

        final IServiceInterface newWSDLFile_Interface = modelRoot.getDescription().getInterface("NewWSDLFile").get(0);
        final IOperation newOperation = newWSDLFile_Interface.getOperation("NewOperation").get(0);
        final IParameter inputParameter = newOperation.getInputParameter("parameters").get(0);

        assertEquals(StructureType.class, inputParameter.getType().getClass());

        // Change NewOperation type name
        final RenameStructureTypeCommand renameCommand = new RenameStructureTypeCommand(modelRoot,
                (IStructureType) newOperationType, "NewOperation22");
        modelRoot.getEnv().execute(renameCommand);

        assertEquals("NewOperation22", newOperationType.getName());
        assertFalse(UnresolvedType.instance().equals(inputParameter.getType()));
        assertEquals("NewOperation22", inputParameter.getType().getName());
    }

}
