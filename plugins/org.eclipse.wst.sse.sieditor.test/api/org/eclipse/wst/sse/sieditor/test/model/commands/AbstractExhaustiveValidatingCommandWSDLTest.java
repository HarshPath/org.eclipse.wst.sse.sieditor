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
package org.eclipse.wst.sse.sieditor.test.model.commands;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.SIEditorTestsPlugin;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

/**
 * EXTEND this class and write a static parameters method, annotated like this
 * example
 * 
 * @Parameters public static Collection<Object[]> getEnterpriseWSDLFiles()
 *             throws IOException, URISyntaxException { return
 *             generateParameterList(ENTERPRISE_WS_RELATIVE_PATH); }
 */

@RunWith(Parameterized.class)
public abstract class AbstractExhaustiveValidatingCommandWSDLTest extends AbstractCommandTest {

    protected static final String ENTERPRISE_WS_RELATIVE_PATH = Document_FOLDER_NAME + Path.SEPARATOR + "enterprise_ws"; //$NON-NLS-1$
    private static final String ABSTRACT_EXHAUSTIVE_VALIDATIONG_MODEL_TEST = "AbstractExhaustiveValidationgModelTest"; //$NON-NLS-1$

    private File currentFile;
    protected IFile currentIFile;
    protected AbstractEditorWithSourcePage openedEditor;
    protected IWsdlModelRoot modelRoot;
    private IValidationService validationService;

    /**
     * Use this method by the yours annotated with @Parameters to generate the
     * list of files the tests will be run with. by the given Path
     * 
     * @param path
     * @return list of Files - to be parameters
     * @throws URISyntaxException
     * @throws IOException
     */
    protected static Collection<Object[]> generateParameterList(final String path) throws URISyntaxException, IOException {
        final URL ewsDirURL = FileLocator.find(SIEditorTestsPlugin.getDefault().getBundle(), new Path(path), null);
        assertNotNull("path to enterprise wsdls not found", ewsDirURL); //$NON-NLS-1$
        final File ewsDir = new File(FileLocator.toFileURL(ewsDirURL).toURI());
        assertTrue("enterprise wsdls dir not found or is not a dir", ewsDir.isDirectory()); //$NON-NLS-1$

        List<File> files = getFilesFromFolderRecursively(ewsDir);

        if (files.size() > getListSizeLimitation()) {
            files = files.subList(0, getListSizeLimitation());
        }

        final List<Object[]> wrapperList = new ArrayList<Object[]>();
        for (final File file : files) {
            wrapperList.add(new Object[] { file });
        }

        return wrapperList;
    }

    /**
     * Gathers all the files from a directory. Traverses each and every
     * sub-directory and fetches its files as well. Directories are not added to
     * the list.
     * 
     * @param dir
     *            directory to descend into
     * @return a <code>java.util.List</code> of <code>java.io.File</code>. If
     *         the directory does not contain anything, an empty list is
     *         returned
     */
    public static List<File> getFilesFromFolderRecursively(final File dir) {
        final List<File> files = new ArrayList<File>();

        gatherFolderFilesRecursively(dir, files);

        return files;
    }

    private static void gatherFolderFilesRecursively(final File dir, final List<File> files) {
        final File[] childFiles = dir.listFiles();
        if (childFiles == null) {
            return;
        }

        for (final File childFile : childFiles) {
            if (childFile.isDirectory()) {
                gatherFolderFilesRecursively(childFile, files);
            } else {
                files.add(childFile);
            }
        }
    }

    public AbstractExhaustiveValidatingCommandWSDLTest(final Object wsdlFile) throws IOException, CoreException {
        currentFile = (File) wsdlFile;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        currentIFile = ResourceUtils.copyFileIntoTestProject(currentFile, null, this.getProject(), currentFile.getName());
        openedEditor = openEditor(currentIFile, getEditorID());
        assertTrue(openedEditor.getModelRoot() instanceof IWsdlModelRoot);
        modelRoot = (IWsdlModelRoot) openedEditor.getModelRoot();
        validationService = openedEditor.getValidationService();
        assertNotNull(validationService);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (openedEditor != null) {
            validationService = null;
            if (!org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false)) {
                openedEditor.close(false);
            }
            // getProject().delete(true, new NullProgressMonitor());
            openedEditor = null;
            currentIFile = null;
            currentFile = null;
            ThreadUtils.waitOutOfUI(10);
        }
        super.tearDown();
    }

    private AbstractEditorWithSourcePage openEditor(final IFile iFile, final String editorId) throws PartInitException {
        final IEditorInput input = new FileEditorInput(iFile);
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            final IWorkbenchPage page = window.getActivePage();
            final IEditorPart editor = IDE.openEditor(page, input, editorId);
            return (AbstractEditorWithSourcePage) editor;
        }
        return null;
    }

    // methods configuring the work of super classes
    @Override
    protected String getProjectName() {
        return ABSTRACT_EXHAUSTIVE_VALIDATIONG_MODEL_TEST;
    }

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        return modelRoot;
    }

    @Override
    protected void setupEnvironment(final IModelRoot modelRoot) {
        // Do nothing - we open the real editor - the environment should be set
        // by itself
    }

    // methods intended to be optionally overridden to configure work of this
    // test

    /**
     * Override to provide a different editor ID to be opened
     * 
     * @return
     */
    protected String getEditorID() {
        return ServiceInterfaceEditor.EDITOR_ID;
    }

    /**
     * Limit the count of parameters for which test is bein run
     * 
     * @return
     */
    protected static int getListSizeLimitation() {
        return 150;
    }

    // -----

    @Override
    protected void executeOperation(final AbstractEMFOperation operation, final IWsdlModelRoot modelRoot)
            throws Throwable {
        // verify that the document is valid before and after command execution
        verifyValid();
        super.executeOperation(operation, modelRoot);
        openedEditor.doSave(new NullProgressMonitor());
        verifyValid();

    }

    /**
     * Override to add more assertions to the post unDo state. Default
     * implementations call {@link #verifyValid()} Don't forget to call it if
     * such behaviour is expected
     */
    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        openedEditor.doSave(new NullProgressMonitor());
        verifyValid();
    }

    /**
     * Override to add more assertions to the post reDo state. Default
     * implementations call {@link #verifyValid()} Don't forget to call it if
     * such behaviour is expected
     */
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        openedEditor.doSave(new NullProgressMonitor());
        verifyValid();
    }

    /**
     * Default implementation calls validate for the root model object and
     * asserts that it's valid validation status
     */
    protected void verifyValid() {
        final Collection<EObject> sources = new ArrayList<EObject>(2);
        sources.add(modelRoot.getModelObject().getComponent());
        validationService.validateAll(sources);
        final IValidationStatusProvider valStatusProvider = validationService.getValidationStatusProvider();
        assertTrue(
                "validation errors :" + valStatusProvider.getStatus(modelRoot.getModelObject()).size() + "\n editing file:" + currentIFile.getName(),//$NON-NLS-1$ //$NON-NLS-2$
                validationService.getValidationStatusProvider().isValid(modelRoot.getModelObject()));
    }
}
