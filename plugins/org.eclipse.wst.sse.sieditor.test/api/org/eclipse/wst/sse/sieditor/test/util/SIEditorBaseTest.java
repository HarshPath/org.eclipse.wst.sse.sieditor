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
package org.eclipse.wst.sse.sieditor.test.util;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ValidationListener;
import org.eclipse.wst.sse.sieditor.ui.v2.resources.ResourceChangeHandler;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ui.internal.text.WSDLModelAdapter;
import org.eclipse.wst.wsdl.ui.internal.util.WSDLAdapterFactoryHelper;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;

/**
 * 
 * 
 * 
 */
@SuppressWarnings("nls")
public abstract class SIEditorBaseTest extends ProjectBasedTest {
    /**
     * Name of folder which contains WSDL files
     */
    protected static final String Document_FOLDER_NAME = "data";

    protected static final String DATA_PUBLIC_SELF_MIX_REL_PATH = "pub/self/mix/";

    protected static final String DATA_PUBLIC_SELF_MIX2_REL_PATH = "pub/self/mix2/";

    /**
     * This field refers to the underlying dom model only if the
     * {@link #getWsdlModelRootViaDocumentProvider(String, String)} or
     * {@link #getXSDModelRootViaDocumentProvider(String, String)} method is
     * used to load the WSDL Model Root.
     */
    private IDOMModel domModel = null;

    public SIEditorBaseTest() {
        super();
        StatusUtils.isUnderJunitExecution = true;
    }

    @Override
    protected String getProjectName() {
        return "SIEditorBaseTest";
    }

    protected IDescription getWSDLModel(final String fileName, final String targetFileName) throws IOException, CoreException {
        final IWsdlModelRoot root = getWSDLModelRoot(fileName, targetFileName);
        return root.getDescription();
    }

    protected IWsdlModelRoot getWSDLModelRoot(final String fileName, final String targetFileName) throws IOException,
            CoreException {
        return getWSDLModelRoot(fileName, targetFileName, Document_FOLDER_NAME);
    }

    protected IXSDModelRoot getXSDModelRoot(final String fileName, final String targetFileName) throws IOException, CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject(fileName, Document_FOLDER_NAME, this.getProject(),
                targetFileName);
        return getXSDModelRoot(file);
    }

    protected IXSDModelRoot getXSDModelRoot(final IFile file) throws IOException, CoreException {
        refreshProjectNFile(file);
        final IXSDModelRoot modelRoot = ModelManager.getInstance().getXSDModelRoot(new FileEditorInput(file));
        setupEnvironment(modelRoot);
        return modelRoot;
    }

    protected IWsdlModelRoot getWSDLModelRoot(final String fileName, final String targetFileName, final String folderName)
            throws IOException, CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject(fileName, folderName, this.getProject(), targetFileName);
        refreshProjectNFile(file);
        final IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(file));
        setupEnvironment(modelRoot);
        return modelRoot;
    }

    protected IWsdlModelRoot getWSDLModelRoot(final IFile file) throws IOException, CoreException {
        refreshProjectNFile(file);
        final IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(file));
        setupEnvironment(modelRoot);
        return modelRoot;
    }

    protected IWsdlModelRoot getWSDLModelRootWithProjectTypes(final String inlineFileName, final String[] projectFileNames)
            throws IOException, CoreException {
        String targetFileName = null;
        for (final String projectFileName : projectFileNames) {
            targetFileName = projectFileName.substring(projectFileName.lastIndexOf('/') + 1);
            final IFile file = ResourceUtils.copyFileIntoTestProject(projectFileName, Document_FOLDER_NAME, this.getProject(),
                    targetFileName);
            refreshProjectNFile(file);
        }
        targetFileName = inlineFileName.substring(inlineFileName.lastIndexOf('/') + 1);
        final IFile inlineFile = ResourceUtils.copyFileIntoTestProject(inlineFileName, Document_FOLDER_NAME, this.getProject(),
                targetFileName);
        refreshProjectNFile(inlineFile);
        final IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(inlineFile));
        setupEnvironment(modelRoot);
        return modelRoot;
    }

    protected void refreshProjectNFile(final IFile file) throws IOException, CoreException {
        this.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        file.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }

    protected void setupEnvironment(final IModelRoot modelRoot) {
        final ObjectUndoContext ctx = new ObjectUndoContext(this);
        final IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
        operationHistory.setLimit(ctx, 100);

        final IEnvironment env = modelRoot.getEnv();
        env.setOperationHistory(operationHistory);
        env.setUndoContext(ctx);
    }

    // =========================================================
    // helpers - editor setup
    // =========================================================

    protected AbstractEditorWithSourcePage editor = null;

    protected IModelRoot getModelRoot(final String fullPath, final String wsdlFilename, final String editorId)
            throws IOException, CoreException, PartInitException {
        final IFile file = ResourceUtils.copyFileIntoTestProject(fullPath, Document_FOLDER_NAME, this.getProject(), wsdlFilename);
        refreshProjectNFile(file);
        final FileEditorInput fileEditorInput = new FileEditorInput(file);

        editor = openEditor(fileEditorInput, editorId);
        return editor.getModelRoot();
    }

    protected AbstractEditorWithSourcePage openEditor(final IEditorInput input, final String editorId) throws PartInitException {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            final IWorkbenchPage page = window.getActivePage();
            // assertNull("There is an already opened editor for input: " +
            // input.getName(), page.findEditor(input));
            final IEditorPart editor = IDE.openEditor(page, input, editorId);
            if (!(editor instanceof AbstractEditorWithSourcePage)) {
                fail("expected editor was not opened. Expected was " + AbstractEditorWithSourcePage.class.getName()
                        + ", but opened " + editor.getClass().getName());
            }
            return (AbstractEditorWithSourcePage) editor;
        }
        return null;
    }

    /**
     * Copies the given document to the test project and creates a DOM model
     * 
     * @param fileName
     *            the source file name
     * @param targetFileName
     *            the target file name
     * @return the created dom model
     * 
     * @throws IOException
     * @throws CoreException
     */
    protected IDOMModel getDomDocumentViaDocumentProvider(final String fileName, final String targetFileName) throws IOException,
            CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject(fileName, Document_FOLDER_NAME, this.getProject(),
                targetFileName);
        refreshProjectNFile(file);
        // get the DomDoc from the textProvider (just as the source page editor
        // does
        final FileEditorInput fileEditorInput = new FileEditorInput(file);
        final TextFileDocumentProvider provider = (TextFileDocumentProvider) DocumentProviderRegistry.getDefault()
                .getDocumentProvider(fileEditorInput);
        provider.connect(fileEditorInput);
        final IStructuredDocument jFaceDocument = (IStructuredDocument) provider.getDocument(fileEditorInput);
        final IStructuredModel structuredModel = StructuredModelManager.getModelManager().getModelForEdit(jFaceDocument);

        IDOMModel domModel = null;
        try {
            if (structuredModel instanceof IDOMModel) {

                domModel = (IDOMModel) structuredModel;
            } else {
                fail("model loaded for test is not a IDOMModel");
            }
        } finally {
            if (structuredModel != null)
                structuredModel.releaseFromEdit();
        }
        return domModel;
    }

    /**
     * Used to set the custom notifier, making the connection from the DOM model
     * to the EMF model.
     * 
     * @param model
     * @param modelRoot
     */
    protected void plugXMLModelNotifierWrapper(final IDOMModel model, final IModelRoot modelRoot) {
        final XMLModelNotifier originalModelNotifier = model.getModelNotifier();
        final XMLModelNotifierWrapper modelNotifier = new XMLModelNotifierWrapper(originalModelNotifier, modelRoot);
        model.setModelNotifier(modelNotifier);
    }

    /**
     * This field refers to the underlying dom model only if the
     * {@link #getWsdlModelRootViaDocumentProvider(String, String)} method is
     * used to load the WSDL Model Root.
     */
    public IDOMModel getDomModel() {
        return domModel;
    }

    /**
     * This method is used to load the wsdl model as similar as possible to the
     * one opened in the SIE on runtime.
     * 
     * @param fileName
     *            the original file pathname
     * @param targetFileName
     *            the name of the coppied file in the project
     * @return the newly created model root
     * @throws IOException
     * @throws CoreException
     */
    protected IWsdlModelRoot getWsdlModelRootViaDocumentProvider(final String fileName, final String targetFileName)
            throws IOException, CoreException {
        domModel = getDomDocumentViaDocumentProvider(fileName, targetFileName);
        final Document domDocument = domModel.getDocument();

        // adapt the domDocument to a wst.wsld model
        final WSDLModelAdapter modelAdapter = WSDLModelAdapter.lookupOrCreateModelAdapter(domDocument);
        final org.eclipse.wst.wsdl.ui.internal.asd.facade.IDescription model = (org.eclipse.wst.wsdl.ui.internal.asd.facade.IDescription) WSDLAdapterFactoryHelper
                .getInstance().adapt(modelAdapter.createDefinition(domDocument));
        final Definition commonModelDefinition = (Definition) ((Adapter) model).getTarget();
        // create our abstract API on top of the wst.wsdl model
        final IWsdlModelRoot modelRoot = WSDLFactory.getInstance().createWSDLModelRoot(commonModelDefinition);
        setupEnvironment(modelRoot);
        plugXMLModelNotifierWrapper(domModel, modelRoot);
        return modelRoot;
    }

    /**
     * This method is used to load the xsd model as similar as possible to the
     * one opened in the SIE on runtime.
     * 
     * @param fileName
     *            the original file pathname
     * @param targetFileName
     *            the name of the coppied file in the project
     * @return the newly created model root
     * @throws IOException
     * @throws CoreException
     */
    protected IXSDModelRoot getXSDModelRootViaDocumentProvider(final String fileName, final String targetFileName)
            throws IOException, CoreException {
        domModel = getDomDocumentViaDocumentProvider(fileName, targetFileName);
        final Document domDocument = domModel.getDocument();

        // adapt the domDocument to a wst.xsd model
        final XSDModelAdapter modelAdapter = XSDModelAdapter.lookupOrCreateModelAdapter(domDocument);
        final XSDSchema schema = modelAdapter.createSchema(domDocument);

        // update schemes, so that any QNames are properly resolved.
        final EList<Resource> resources = schema.eResource().getResourceSet().getResources();
        for (final Resource res : resources) {
            if (res instanceof XSDResourceImpl) {
                ((XSDResourceImpl) res).getSchema().update();
            }
        }
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema);
        setupEnvironment(xsdModelRoot);
        plugXMLModelNotifierWrapper(domModel, xsdModelRoot);
        return xsdModelRoot;
    }

    /**
     * Must be called if this{@link #getModelRoot(String, String, String)}
     * method is used! closes the opened editor thus dismisses all changes made
     * on model.
     */
    protected void disposeModel() {
        if (editor != null) {
            ResourceChangeHandler.getInstance().deregisterEditor(editor);
            disposeEditor();
            ThreadUtils.waitOutOfUI(10);
        }
    }

    private void disposeEditor() {
        if (editor != null) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
            // editor.close(false);
            editor = null;
        }
    }

    protected void assertThereAreNoValidationErrors() {
        assertThereAreValidationErrorsPresent(0);
    }

    protected void assertThereAreValidationErrorsPresent(final int expected) {
        final List<IValidationListener> validationListeners = editor.getValidationService().getValidationListeners();
        assertEquals("One validation listener " + ValidationListener.class + " is expected", 1, validationListeners.size());
        assertEquals(ValidationListener.class, validationListeners.get(0).getClass());

        final List<String> errorMessages = ((ValidationListener) validationListeners.get(0)).getErrorMessages();

        final StringBuffer errorsBuffer = new StringBuffer();
        for (final String error : errorMessages) {
            errorsBuffer.append("<" + error + ">; ");
        }
        if (expected == -1) {
            assertTrue("no errors found in the model", errorMessages.size() > 1);
        } else {
            assertEquals(errorMessages.size() + " errors found in the model: \n\"" + errorsBuffer.toString() + "\"", expected,
                    errorMessages.size());
        }
    }
}
