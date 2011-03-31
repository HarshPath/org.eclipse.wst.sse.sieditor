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
package org.eclipse.wst.sse.sieditor.test.v2.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.eclipse.wst.wsdl.Operation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

/**
 * Tests service interface editor I/O operations
 * 
 *
 * 
 */
public class MultipageEditorTest extends SIEditorBaseTest {

    private static final String TEST_MOVE_XSD = "test_move.xsd";

    private static final String SERVICE_NAME = "CustomerSimpleByNameAndAddressQueryResponse_In";
    private static final String TEST_MOVE_WSDL = "test_move.wsdl";
    public static final String TEST_WSDL_NAME = "test.wsdl";
    public static final String TEST_XSD_NAME = "test.xsd";
    private static final byte SIE = 0;
    private static final byte DTE = 1;

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
    public void testServiceInterfaceEditorCreate() throws Exception {
        final SIETestEditorInput input = prepareEditorInput(false, true, TEST_WSDL_NAME);
        editor = openEditor(input, ServiceInterfaceEditor.EDITOR_ID);
        editor.doSave(new NullProgressMonitor());
        final SIETestStorage storage = (SIETestStorage) input.getStorage();
        assertEquals(storage.getWsdlAsString(), storage.getChangedWsdlAsString());
        assertTrue(editor instanceof ServiceInterfaceEditor);
    }

    @Test
    public void testDataTypeEditorCreate() throws Exception {
        final SIETestEditorInput input = prepareEditorInput(false, true, TEST_XSD_NAME);
        editor = openEditor(input, DataTypesEditor.EDITOR_ID);
        editor.doSave(new NullProgressMonitor());
        final SIETestStorage storage = (SIETestStorage) input.getStorage();
        assertEquals(storage.getWsdlAsString(), storage.getChangedWsdlAsString());
        assertTrue(editor instanceof DataTypesEditor);
    }

    @Test
    public void testRenameWSDL() throws Exception {
        final IPath newPath = new Path("another/folder/myNewWSDLFile.wsdl");
        getProject().getFolder("another").create(true, true, null);
        getProject().getFolder("another/folder").create(true, true, null);

        testRename(TEST_WSDL_NAME, newPath);
    }

    @Test
    public void testMoveWSDL() throws Exception {
        final IPath newPath = new Path("move/" + TEST_MOVE_WSDL);
        final IFolder folder = getProject().getFolder("move");
        if (!folder.exists())
            folder.create(true, true, null);

        final IWsdlModelRoot model = (IWsdlModelRoot) testRename(TEST_MOVE_WSDL, newPath);
        final IDescription description = model.getDescription();
        final List<IServiceInterface> servInterf = description.getInterface(SERVICE_NAME);
        assertFalse(servInterf.isEmpty());
        
        final String newServName = "Renamed";
        // rename portType
        final RenameServiceInterfaceCommand rename = new RenameServiceInterfaceCommand(model, servInterf.get(0), newServName);
        assertTrue(model.getEnv().execute(rename).isOK());
        assertEquals(newServName, servInterf.get(0).getComponent().getQName().getLocalPart());
        // rename operation
        final List<IServiceInterface> newServ = description.getInterface(newServName);
        assertFalse(newServ.isEmpty());
        final List<IOperation> operations = newServ.get(0).getOperation("CustomerSimpleByNameAndAddressQueryResponse_In");
        assertFalse(operations.isEmpty());

        final String newOpName = "NewOperationName";

        final RenameOperationCommand renameOp = new RenameOperationCommand(model, operations.get(0), newOpName);
        assertTrue(model.getEnv().execute(renameOp).isOK());
        assertEquals(newOpName, ((Operation) operations.get(0).getComponent()).getName());
    }

    @Test
    public void testRenameXSD() throws Exception {
        final IPath newPath = new Path("yet/another/folder/myNewXSDFile.xsd");
        getProject().getFolder("yet").create(true, true, null);
        getProject().getFolder("yet/another").create(true, true, null);
        getProject().getFolder("yet/another/folder").create(true, true, null);

        testRename(TEST_XSD_NAME, newPath);
    }

    @Test
    public void testMoveXSD() throws Exception {
        final IPath newPath = new Path("move/" + TEST_MOVE_XSD);
        final IFolder folder = getProject().getFolder("move");
        if (!folder.exists())
            folder.create(true, true, null);
        final IXSDModelRoot model = (IXSDModelRoot) testRename(TEST_MOVE_XSD, newPath, DTE);
        final IType type = model.getSchema().getType(true, "book");
        assertNotNull(type);

        assertTrue(type instanceof StructureType);
        final String newElementName = "newName";
        final RenameStructureTypeCommand cmd = new RenameStructureTypeCommand(model, (StructureType) type, newElementName);
        assertTrue(model.getEnv().execute(cmd).isOK());
        assertEquals(newElementName, type.getComponent().getName());

    }
    
    @Test 
    public void testSetInputFiresTitleChanged() throws CoreException, IOException {
    	FileEditorInput input = prepareFileEditorInput(TEST_MOVE_WSDL);
    	editor = openEditor(input, ServiceInterfaceEditor.EDITOR_ID);
    	final boolean titlePropertyUpdated[] = {false};
        IPropertyListener propertyChangeListener = new IPropertyListener() {
        	public void propertyChanged(Object source, int propId) {
        		titlePropertyUpdated[0] = propId == IWorkbenchPart.PROP_TITLE;
			}
		};
		editor.addPropertyListener(propertyChangeListener);

		editor.setInput(input);

        assertTrue("Fire of event IWorkbenchPart.PROP_TITLE on set input is missing." + 
        		" This event updates the new input's path in eclipse title bar.", titlePropertyUpdated[0]);
    }

    private IModelRoot testRename(String fileName, IPath newPath) throws Exception {
        return testRename(fileName, newPath, SIE);
    }

    private IModelRoot testRename(final String fileName, final IPath newPath, final byte editorType) throws Exception {
        FileEditorInput input = prepareFileEditorInput(fileName);
        if (editorType == DTE) {
            editor = openEditor(input, DataTypesEditor.EDITOR_ID);
        } else if (editorType == SIE) {
            editor = openEditor(input, ServiceInterfaceEditor.EDITOR_ID);
        } else {
            fail("could not find editor");
        }        

        input.getFile().move(newPath, true, null);
        ThreadUtils.waitOutOfUI(100);

        Assert.assertEquals(newPath.lastSegment(), editor.getPartName());

        final IModelRoot model = ((AbstractEditorPage) editor.getSelectedPage()).getModel();
        EObject rootObject = null;
        if (model instanceof IWsdlModelRoot) {
            rootObject = ((IWsdlModelRoot) model).getDescription().getComponent();
        } else {
            rootObject = ((IXSDModelRoot) model).getSchema().getComponent();
        }

        
        input = (FileEditorInput) editor.getEditorInput();
        Assert.assertEquals(URI.createPlatformResourceURI(input.getStorage().getFullPath().toString(), false), rootObject
                .eResource().getURI());
        
        assertUndoRedoHandlersAreInSyncWithUndoContext();
        
        return model;
    }

    private void assertUndoRedoHandlersAreInSyncWithUndoContext() {
    	final IActionBars actionBars = editor.getEditorSite().getActionBars();
    	final IModelRoot model = ((AbstractEditorPage) editor.getSelectedPage()).getModel();
    	final CommandStack commandStack = model.getEnv().getEditingDomain().getCommandStack();

        IUndoContext newUndoContext = ((WorkspaceCommandStackImpl) commandStack).getDefaultUndoContext();
        
    	UndoActionHandler undoActionHandler = (UndoActionHandler)actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
    	RedoActionHandler redoActionHandler = (RedoActionHandler)actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
		
    	assertEquals(newUndoContext, undoActionHandler.getAdapter(IUndoContext.class));
    	assertEquals(newUndoContext, redoActionHandler.getAdapter(IUndoContext.class));
	}

    private SIETestEditorInput prepareEditorInput(final boolean readOnly, final boolean exists, final String fileName) throws IOException {
        final String testWsdlContents = readTestWsdlContents(fileName);

        final SIETestStorage storage = new SIETestStorage();
        storage.setWsdlAsString(testWsdlContents);
        storage.setCharset("UTF-8");
        storage.setFullPath(new Path("someproject/" + fileName));
        storage.setName(fileName);
        storage.setReadOnly(readOnly);

        final SIETestEditorInput editorInput = new SIETestEditorInput();
        editorInput.setStorage(storage);
        editorInput.setExists(exists);
        editorInput.setToolTipText(fileName);

        return editorInput;
    }

    private FileEditorInput prepareFileEditorInput(final String fileName) throws CoreException, IOException {
        final String testWsdlContents = readTestWsdlContents(fileName);

        final IFile file = getProject().getFile(fileName);
        if (file.exists()) {
            file.delete(true, null);
        }
        file.create(new ByteArrayInputStream(testWsdlContents.getBytes()), true, null);
        final FileEditorInput editorInput = new FileEditorInput(file);
        return editorInput;
    }

    private String readTestWsdlContents(final String fileName) throws IOException {
        String wsdlContents = "";
        final InputStream resourceStream = MultipageEditorTest.class.getResourceAsStream(fileName);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int ch = -1; (ch = resourceStream.read()) >= 0;) {
            baos.write(ch);
        }
        wsdlContents = new String(baos.toByteArray());
        return wsdlContents;
    }

}
