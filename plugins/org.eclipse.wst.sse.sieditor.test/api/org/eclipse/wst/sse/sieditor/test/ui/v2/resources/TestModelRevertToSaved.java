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
package org.eclipse.wst.sse.sieditor.test.ui.v2.resources;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

@SuppressWarnings("nls")
public class TestModelRevertToSaved extends SIEditorBaseTest {

    AbstractEditorWithSourcePage editor = null;

    @BeforeClass
    public static void setUpBefore() {
        StatusUtils.isUnderJunitExecution = true;
    }

    @AfterClass
    public static void tearDownAfter() {
        StatusUtils.isUnderJunitExecution = false;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

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
    public void testDTEUnderlyingFileContentsChangedWhenEditorIsSaved() throws Throwable {
        final String TEST_SIMPLETYPE_NAME = "TestSimpleType";
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        final String oldContents = ResourceUtils.getContents(file, "UTF-8");

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
        assertFalse(editor.isDirty());

        final IXSDModelRoot modelRoot = (IXSDModelRoot) editor.getModelRoot();
        IType testType[] = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNull(testType);

        final AddSimpleTypeCommand addSimpleType = new AddSimpleTypeCommand(modelRoot, modelRoot.getSchema(), TEST_SIMPLETYPE_NAME);
        modelRoot.getEnv().execute(addSimpleType);

        testType = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNotNull(testType[0]);

        // assert initial editor state
        assertTrue(editor.isDirty());
        editor.doSave(null);
        assertFalse(editor.isDirty());

        testType = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNotNull(testType[0]);

        // return the old file's contents
        final long refreshDialogCalls = StatusUtils.getShowDialogWithResult_calls_calls();
        // set file contents from not UI thread, so any resource change
        // listeners can react

        final IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    ResourceUtils.setContents(file, oldContents);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ModalContext.run(runnable, true, new NullProgressMonitor(), PlatformUI.getWorkbench().getDisplay());
        // wait for the refresh editors job
        //waitForResourceChangeListener();
        // Assert the initial editor's state
        // dialog is not shown because the editor was not dirty
        assertEquals(refreshDialogCalls, StatusUtils.getShowDialogWithResult_calls_calls());
        assertFalse(editor.isDirty());
        testType = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNull(testType);

    }

    @Test
    public void testDTEUnderlyingFileContentsChangedWhenEditorIsNotSaved() throws Throwable {
        final String TEST_SIMPLETYPE_NAME = "TestSimpleType";
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        final String oldContents = ResourceUtils.getContents(file, "UTF-8");

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();
        final IWorkbenchPart initialActivePart = window.getActivePage().getActivePart();

        editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
        show(editor);
        assertFalse(editor.isDirty());

        final IXSDModelRoot modelRoot = (IXSDModelRoot) editor.getModelRoot();
        IType testType[] = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNull(testType);

        final AddSimpleTypeCommand addSimpleType = new AddSimpleTypeCommand(modelRoot, modelRoot.getSchema(), TEST_SIMPLETYPE_NAME);
        modelRoot.getEnv().execute(addSimpleType);

        testType = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNotNull(testType[0]);

        // assert initial editor state
        assertTrue(editor.isDirty());

        // return the old file's contents
        final long refreshDialogCalls = StatusUtils.getShowDialogWithResult_calls_calls();
        // set file contents from not UI thread, so any resource change
        // listeners can react

        workbenchActivePage.activate(initialActivePart);
        
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    ResourceUtils.setContents(file, oldContents);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ModalContext.run(runnable, true, new NullProgressMonitor(), PlatformUI.getWorkbench().getDisplay());
        // wait for the refresh editors job
        //waitForResourceChangeListener();
        //switch back to the DTE page
        window.getActivePage().activate(editor.getEditorSite().getPart());
        // Assert that the user is asked before reload editor contents
        assertTrue(StatusUtils.getShowDialogWithResult_calls_calls() > refreshDialogCalls);
        assertFalse(editor.isDirty());
        testType = modelRoot.getSchema().getAllTypes(TEST_SIMPLETYPE_NAME);
        assertNull(testType);

    }

    @Test
    public void testSIEUnderlyingFileContentsChangedWhenEditorIsSaved() throws Throwable {
        final String TEST_INTERFACE_NAME = "TestInterface";
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/simple/NewWSDLFile.wsdl", Document_FOLDER_NAME, this
                .getProject(), "NewWSDLFile.wsdl");
        refreshProjectNFile(file);

        final String oldContents = ResourceUtils.getContents(file, "UTF-8");

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (ServiceInterfaceEditor) workbenchActivePage.openEditor(eInput, ServiceInterfaceEditor.EDITOR_ID);
        assertFalse(editor.isDirty());

        final IWsdlModelRoot modelRoot = ((ServiceInterfaceEditor) editor).getModelRoot();

        List<IServiceInterface> sIs = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertTrue(sIs.isEmpty());

        final AddServiceInterfaceCommand addInterface = new AddServiceInterfaceCommand(modelRoot, modelRoot.getDescription(),
                TEST_INTERFACE_NAME);
        modelRoot.getEnv().execute(addInterface);

        sIs = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertFalse(sIs.isEmpty());
        IServiceInterface testInterface = sIs.get(0);
        assertNotNull(testInterface);

        // assert initial editor state
        assertTrue(editor.isDirty());
        editor.doSave(null);
        assertFalse(editor.isDirty());

        sIs = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertFalse(sIs.isEmpty());
        testInterface = sIs.get(0);
        assertNotNull(testInterface);

        // return the old file's contents
        final long refreshDialogCalls = StatusUtils.getShowDialogWithResult_calls_calls();
        // set file contents from not UI thread, so any resource change
        // listeners can react

        final IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    ResourceUtils.setContents(file, oldContents);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ModalContext.run(runnable, true, new NullProgressMonitor(), PlatformUI.getWorkbench().getDisplay());
        // wait for the refresh editors job
        //waitForResourceChangeListener();
        // Assert the initial editor's state
        // dialog is not shown because the editor was not dirty
        assertEquals(refreshDialogCalls, StatusUtils.getShowDialogWithResult_calls_calls());
        assertFalse(editor.isDirty());
        final List<IServiceInterface> interfaces = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertTrue(interfaces.isEmpty());

    }

    @Test
    public void testSIEUnderlyingFileContentsChangedWhenEditorIsNotSaved() throws Throwable {
        final String TEST_INTERFACE_NAME = "TestInterface";
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/simple/NewWSDLFile.wsdl", Document_FOLDER_NAME, this
                .getProject(), "NewWSDLFile.wsdl");
        refreshProjectNFile(file);

        final String oldContents = ResourceUtils.getContents(file, "UTF-8");

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final FileEditorInput eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();
        final IWorkbenchPart initialActivePart = window.getActivePage().getActivePart();

        editor = (ServiceInterfaceEditor) workbenchActivePage.openEditor(eInput, ServiceInterfaceEditor.EDITOR_ID);
        show(editor);
        workbenchActivePage.activate(editor.getEditorSite().getPart());
        
        assertFalse(editor.isDirty());

        final IWsdlModelRoot modelRoot = ((ServiceInterfaceEditor) editor).getModelRoot();
        List<IServiceInterface> testInterfaces = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertTrue(testInterfaces.isEmpty());

        final AddServiceInterfaceCommand addInterface = new AddServiceInterfaceCommand(modelRoot, modelRoot.getDescription(),
                TEST_INTERFACE_NAME);
        modelRoot.getEnv().execute(addInterface);

        testInterfaces = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertFalse(testInterfaces.isEmpty());
        final IServiceInterface testInterface = testInterfaces.get(0);
        assertNotNull(testInterface);

        // assert initial editor state
        assertTrue(editor.isDirty());

        //activate some other page
        workbenchActivePage.activate(initialActivePart);
        
        // return the old file's contents
        final long refreshDialogCalls = StatusUtils.getShowDialogWithResult_calls_calls();
        // set file contents from not UI thread, so any resource change
        // listeners can react

        final IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    ResourceUtils.setContents(file, oldContents);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ModalContext.run(runnable, true, new NullProgressMonitor(), PlatformUI.getWorkbench().getDisplay());
        
        //switch back to the SIE page
        window.getActivePage().activate(editor.getEditorSite().getPart());
        
        // Assert that the user is asked before reload editor contents
        assertTrue(StatusUtils.getShowDialogWithResult_calls_calls() > refreshDialogCalls);

        final boolean isDirty = editor.isDirty();
        if (isDirty) {
            // DEBUG PURPOSE - it is random failure
            Logger.logError("ERROR testSIEUnderlyingFileContentsChangedWhenEditorIsNotSaved dirty pages: ");
            for (final Object page : (editor).getPages()) {
                if (page instanceof IFormPage) {
                    final IFormPage fpage = (IFormPage) page;
                    if (fpage.isDirty()) {
                        Logger.logError("Dirty page: " + (page.getClass()));
                    }
                } else {
                    Logger.logError("Page which is not IFormPage: " + (page.getClass()));
                }
            }
        }

        assertFalse(isDirty);
        final List<IServiceInterface> interfaces = modelRoot.getDescription().getInterface(TEST_INTERFACE_NAME);
        assertTrue(interfaces.isEmpty());

    }
    
    /**
     * Without setVisible & setSize, IPartListeners won't be notified upon part activation
     * @param editor
     */
    private void show(AbstractEditorWithSourcePage editor) {
        Control control = ((PartSite) editor.getEditorSite().getPart().getSite()).getPane().getControl();
        List<Control> controls = new ArrayList<Control>();
        controls.add(control);
        Control parent = null;
        
        while ((parent = control.getParent()) != null) {
        	controls.add(0, parent);
        	control = parent;
        }
        
        for (Control ctrl : controls) {
        	ctrl.setVisible(true);
        	ctrl.setSize(10, 10);
        }
    }

    /**
     * Waits for the refresh editors job which is started in the resource change
     * listener {@link ResourceChangeHandler}
     */
    /*
    private void waitForResourceChangeListener() {
        try {
            Job.getJobManager().join(ResourceChangeHandler.RefreshEditorsJob.REFRESH_EDITORS_FAMILY, new NullProgressMonitor());
        } catch (final InterruptedException e) {
            e.printStackTrace(System.err);
            final Job[] jobs = Job.getJobManager().find(ResourceChangeHandler.RefreshEditorsJob.REFRESH_EDITORS_FAMILY);
            if (jobs.length == 0) {
                return;
            }
            for (final Job job : jobs) {
                job.wakeUp();
            }
            waitForResourceChangeListener();
        }
    }
*/
}
