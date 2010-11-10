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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.command.common.SaveCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.SIECommandStackListener;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.ModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.reconcile.ModelReconciler;
import org.eclipse.wst.sse.sieditor.model.utils.CommandStackWrapper;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.impl.MarkerUtils;
import org.eclipse.wst.sse.sieditor.model.validation.impl.XSDDiagnosticValidationStatus;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.preedit.EditValidator;
import org.eclipse.wst.sse.sieditor.ui.providers.SurrogateSelectionProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.PageChangedSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ThreadUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.resources.ResourceChangeHandler;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.ServiceIntefaceEditorPage;
import org.eclipse.wst.sse.sieditor.ui.view.impl.SISourceEditorPart;

/**
 * Base class to provide common infrastructure for the Service Interface editor
 * and Data Types editor. It provides a source page and subclasses are
 * responsible to override
 * {@link AbstractEditorWithSourcePage#addExtraPages(IStorageEditorInput)}
 * 
 * @see ServiceInterfaceEditor
 * @see DataTypesEditor
 */
public abstract class AbstractEditorWithSourcePage extends FormEditor implements IGotoMarker {

    public static final String READ_ONLY_TAG = "readonly"; //$NON-NLS-1$

    private boolean saving;

    protected ValidationService validationService;

    protected IEnvironment env;

    protected IModelRoot commonModel;

    protected boolean isReadOnly = true;

    protected IUndoContext undoContext;

    protected IOperationHistory operationHistory;

    /**
     * Default Null implementation of the model notifier. Used in case that an
     * editor does not use {@link #createModel(IDocumentProvider)} to create a
     * model (e.g. ESR editor).
     */
    protected XMLModelNotifierWrapper modelNotifier = new XMLModelNotifierWrapper(null, null);

    private SISourceEditorPart sourcePage;

    private UndoActionHandler undoActionHandler;

    private RedoActionHandler redoActionHandler;

    private EditorActivationListener activationListener;

    private SIECommandStackListener commandStackListener;

    private IStructuredModel structuredModel;

    private PageChangedSelectionManager pageChangedSelectionManager;

    public AbstractEditorWithSourcePage() {
        super();
        initUndoRedoSupport();
    }

    protected void initUndoRedoSupport() {
        final IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
        operationHistory = operationSupport.getOperationHistory();
    }

    public IModelRoot getModelRoot() {
        return commonModel;
    }

    public XMLModelNotifierWrapper getModelNotifier() {
        return modelNotifier;
    }

    public List getPages() {
        return pages;
    }

    /**
     * Indicates if the editor is currently being saved. This flag is used by
     * <code>ResourceChangeHandler</code> to check if on file change event the
     * editor should be refreshed
     * 
     * @see org.eclipse.wst.sse.sieditor.ui.v2.resources.ResourceChangeHandler
     */
    public boolean isSaving() {
        return saving;
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        if (isReadOnly) {
            throw new RuntimeException("Cannot save a readonly model"); //$NON-NLS-1$
        }

        saving = true;

        ServiceIntefaceEditorPage siPage = null;
        DataTypesEditorPage dtPage = null;
        SISourceEditorPart sPage = null;

        for (final Object page : pages) {
            if (page instanceof SISourceEditorPart) {
                sPage = (SISourceEditorPart) page;
            } else if (page instanceof ServiceIntefaceEditorPage) {
                siPage = (ServiceIntefaceEditorPage) page;
            } else if (page instanceof DataTypesEditorPage) {
                dtPage = (DataTypesEditorPage) page;
            }
        }

        try {
            reloadModelOnSourcePageChange();
            final SaveCommand saveCommand = new SaveCommand(commonModel.getEnv().getEditingDomain(), sPage);
            saveCommand.execute(monitor);
            if (siPage != null) {
                siPage.setDirty(false);
            }
            dtPage.setDirty(false);
            fireDirtyPropertyChange();
        } catch (final Exception e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to save editor " + sPage.getClass(), e); //$NON-NLS-1$
        } finally {
            saving = false;
        }

        validate();
    }

    protected abstract void addExtraPages(IStorageEditorInput in) throws PartInitException;

    protected void updateValidator() {
        if (this.getValidationService() != null && commonModel != null) {
            getValidationService().update(commonModel.getEnv().getEditingDomain().getResourceSet(), commonModel);
        }
    }

    protected abstract IModelRoot createModelRoot(final Document document);

    protected void reloadModelWithSchemaCheck() {
        final IModelRoot root = getModelRoot();
        if (root instanceof IWsdlModelRoot) {
            final IDescription object = ((IWsdlModelRoot) root).getDescription();
            final List<ISchema> schemas = object.getAllVisibleSchemas();
            for (final ISchema schema : schemas) {
                // do not reload
                if (EmfXsdUtils.isSchemaForSchemaMissing(schema)) {
                    return;
                }
            }
        }

        if (!EmfXsdUtils.isSchemaForSchemaMissing(root.getModelObject())) {
            reloadModelFromDOM();
            ModelReconciler.instance().reconcileModel(getModelRoot(), env.getModelReconcileRegistry());
        }
    }

    protected abstract void reloadModelFromDOM();

    protected IModelRoot createModel(final IDocumentProvider documentProvider) {
        final IDocument textDocument = documentProvider.getDocument(getEditorInput());
        structuredModel = StructuredModelManager.getModelManager().getExistingModelForRead(textDocument);

        Document document = null;
        IModelRoot modelRoot;
        try {
            if (structuredModel instanceof IDOMModel) {
                document = ((IDOMModel) structuredModel).getDocument();
            }
            modelRoot = createModelRoot(document);
        } finally {
            if (structuredModel != null)
                structuredModel.releaseFromRead();
        }
        env = modelRoot.getEnv();
        env.setEditValidator(new EditValidator(this));

        final CommandStackWrapper commandStackWrapper = (CommandStackWrapper) env.getEditingDomain().getCommandStack();
        commandStackWrapper.registerTo(structuredModel);

        undoContext = commandStackWrapper.getDefaultUndoContext();

        if (undoActionHandler != null && redoActionHandler != null) {
            undoActionHandler.setContext(undoContext);
            redoActionHandler.setContext(undoContext);
        }

        env.setOperationHistory(operationHistory);
        env.setUndoContext(undoContext);

        final XMLModelNotifier originalModelNotifier = ((IDOMModel) structuredModel).getModelNotifier();
        modelNotifier = new XMLModelNotifierWrapper(originalModelNotifier, modelRoot);
        modelNotifier.registerTo(structuredModel);
        env.addDisposable(modelNotifier);

        ((IDOMModel) structuredModel).setModelNotifier(modelNotifier);

        commandStackListener = new SIECommandStackListener(env, modelRoot, modelNotifier);
        env.addCommandStackListener(commandStackListener);

        return modelRoot;
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Upon init the editor is registered with an external resource change
     * listener in case the editor is loaded with file input
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        super.init(site, input);

        if (getEditorInput() instanceof IFileEditorInput) {
            ResourceChangeHandler.getInstance().registerEditor(this);
            activationListener = new EditorActivationListener(this, this);
        }
    }

    /**
     * Upon dispose the editor is deregistered from an external resource change
     * listener in case the editor is loaded with file input
     */
    @Override
    public void dispose() {
        if (undoActionHandler != null) {
            undoActionHandler.dispose();
        }
        if (redoActionHandler != null) {
            redoActionHandler.dispose();
        }
        if (commonModel != null) {
            commonModel.getEnv().removeCommandStackListener(commandStackListener);
            commonModel.getEnv().dispose();
        }

        if (getEditorInput() instanceof IFileEditorInput) {
            ResourceChangeHandler.getInstance().deregisterEditor(this);
            activationListener.doDispose();
        }

        super.dispose();
    }

    @Override
    public void setInput(final IEditorInput input) {
        super.setInputWithNotify(input);
        // need to fire, in order to change the path to the edited file in
        // eclipse's title bar
        firePropertyChange(PROP_TITLE);
        if (input instanceof IStorageEditorInput) {
            updateModelRootURI(input);
            niceSetPartName(((IStorageEditorInput) input).getName());
        }
    }

    @Override
    protected void addPages() {
        if (!(getEditorInput() instanceof IStorageEditorInput)) {
            throw new RuntimeException(
                    "Only editor inputs of type IStorageEditorInput are supported. You probably tried to open a file located outside of the workspace."); //$NON-NLS-1$
        }

        final IStorageEditorInput in = (IStorageEditorInput) getEditorInput();
        isReadOnly = false;
        niceSetPartName(in.getName());

        try {
            sourcePage = new SISourceEditorPart();
            pageChangedSelectionManager = new PageChangedSelectionManager(sourcePage);

            addPage(sourcePage, in);
            sourcePage.initPart(in, this);

            final IDocumentProvider documentProvider = sourcePage.getDocumentProvider();
            commonModel = createModel(documentProvider);
            addExtraPages(in);

            setPageText(pages.size() - 1, sourcePage.getPageText());
            getSite().setSelectionProvider(new SurrogateSelectionProvider());
            setActivePage(0);

        } catch (final CoreException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add pages to SISource editor.", e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }

        validate();
    }

    public void reloadModel(final IStorageEditorInput newEditorInput) {

        Display.getDefault().asyncExec((new Runnable() {

            public void run() {
                // Clear undo/redo history
                final int oldUndoLimit = operationHistory.getLimit(undoContext);
                operationHistory.setLimit(undoContext, 0);
                operationHistory.setLimit(undoContext, oldUndoLimit);
                undoActionHandler.update();
                redoActionHandler.update();

                setInput(newEditorInput);
                sourcePage.setInput(newEditorInput);
                sourcePage.update();
                commonModel = createModel(sourcePage.getDocumentProvider());
                updateValidator();
                for (final Object page : pages) {
                    if (page instanceof AbstractEditorPage) {
                        ((AbstractEditorPage) page).setModel(commonModel, isReadOnly, true);
                    }
                }

                validate();

            }

        }));

    }

    protected void niceSetPartName(final String partName) {
        if (partName != null && !UIConstants.EMPTY_STRING.equalsIgnoreCase(partName.trim()) && !partName.contains(READ_ONLY_TAG)) {
            if (isReadOnly) {
                setPartName(partName + UIConstants.EMPTY_STRING + READ_ONLY_TAG);
            } else {
                setPartName(partName);
            }
        } else {
            setPartName(getPartName());
        }
    }

    public boolean revertContentsToSavedVersion() {
        if (null == commonModel) {
            return false;
        }

        try {
            revertModelToSaved();
            for (final Object page : pages) {
                if (page instanceof AbstractEditorPage) {
                    ((AbstractEditorPage) page).setModel(commonModel, isReadOnly, true);
                }
            }
        } finally {
            modelNotifier.getChangedNodes().clear();
        }
        return true;
    }

    private void revertModelToSaved() {

        final Runnable executeReload = new Runnable() {
            public void run() {
                // this command should not notify SIE Model
                final AbstractEMFOperation reloadCommand = new AbstractEMFOperation(commonModel.getEnv().getEditingDomain(),
                        Messages.AbstractEditorWithSourcePage_0) {
                    @Override
                    protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                        // revert model to saved
                        sourcePage.doRevertToSaved();
                        modelNotifier.getChangedNodes().clear();
                        reloadModelWithSchemaCheck();

                        // Clear undo/redo history
                        final int oldUndoLimit = operationHistory.getLimit(undoContext);
                        operationHistory.setLimit(undoContext, 0);
                        operationHistory.setLimit(undoContext, oldUndoLimit);
                        undoActionHandler.update();
                        redoActionHandler.update();
                        return Status.OK_STATUS;
                    }
                };

                final Map<Object, Object> options = new HashMap<Object, Object>();
                options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
                options.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);
                reloadCommand.setOptions(options);

                try {
                    final IStatus status = reloadCommand.execute(null, null);
                    if (!StatusUtils.canContinue(status)) {
                        Logger.log(status);
                        StatusUtils.showStatusDialog(Messages.AbstractEditorWithSourcePage_1, MessageFormat.format(
                                Messages.AbstractEditorWithSourcePage_2, getPartName()), status);
                    }
                } catch (final ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        ThreadUtils.displaySyncExec(executeReload);

    }

    protected void validate() {

    }

    public void gotoMarker(final IMarker marker) {
    }

    public void fireDirtyPropertyChange() {
        firePropertyChange(PROP_DIRTY);
    }

    @Override
    public void pageChange(final int newPageIndex) {
        final int currentPageIndex = getCurrentPage();

        reloadModelOnSourcePageChange();

        super.pageChange(newPageIndex);

        if (pageChangedSelectionManager != null) {// because of ESR
            pageChangedSelectionManager.performSelection(newPageIndex, currentPageIndex, getPages(), getModelRoot());
        }
    }

    private boolean reloadModelOnSourcePageChange() {
        if (isDirty() && getCurrentPage() > -1 && getEditor(getCurrentPage()) instanceof SISourceEditorPart) {

            final AbstractEMFOperation reloadModelCommand = new AbstractEMFOperation(commonModel.getEnv().getEditingDomain(),
                    Messages.AbstractEditorWithSourcePage_3) {

                @Override
                protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

                    reloadModelWithSchemaCheck();
                    commonModel.notifyListeners(new ModelChangeEvent(commonModel.getModelObject()));
                    return Status.OK_STATUS;
                }
            };

            try {
                final Map<Object, Object> options = new HashMap<Object, Object>();
                options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
                reloadModelCommand.setOptions(options);

                final IStatus status = reloadModelCommand.execute(null, null);
                if (!StatusUtils.canContinue(status)) {
                    Logger.log(status);
                    StatusUtils.showStatusDialog(Messages.AbstractEditorWithSourcePage_4, MessageFormat.format(
                            Messages.AbstractEditorWithSourcePage_5, getPartName()), status);
                }
            } catch (final ExecutionException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    protected void validate(final Collection<? extends IModelObject> validatedEntitites) {
        final Set<String> locationUris = new HashSet<String>();
        final IWorkspaceRunnable vr = new IWorkspaceRunnable() {

            public void run(final IProgressMonitor monitor) throws CoreException {
                final Set<IModelObject> validatedObjects = new HashSet<IModelObject>();

                final class BatchValidationOperation extends AbstractEMFOperation {
                    public BatchValidationOperation() {
                        super(commonModel.getEnv().getEditingDomain(), "Batch Validation"); //$NON-NLS-1$

                        // tell ValidationService#EditingDomainListener that no
                        // live validation needs to be started
                        final Map<Object, Object> options = new HashMap<Object, Object>(getOptions());
                        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
                        options.put(ValidationService.OPTION_BATCH_VALIDTION, Boolean.TRUE);
                        setOptions(options);
                    }

                    @Override
                    public boolean canRedo() {
                        return false;
                    }

                    @Override
                    public boolean canUndo() {
                        return false;
                    }

                    @Override
                    protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

                        for (final IModelObject modelObject : validatedEntitites) {
                            locationUris.add(modelObject.getComponent().eResource().getURI().toString());

                        }
                        validatedObjects.addAll(validationService.validateAll(validatedEntitites));
                        return Status.OK_STATUS;
                    }
                }

                try {
                    new DefaultOperationHistory().execute(new BatchValidationOperation(), new NullProgressMonitor(), null);
                } catch (final ExecutionException e) {
                    Logger.logWarning(e.getMessage(), e);
                    return;
                }

                final List<IValidationStatus> validationStatusList = new ArrayList<IValidationStatus>(validatedObjects.size() * 2);
                for (final IModelObject current : validatedObjects) {
                    final List<IValidationStatus> statuses = validationService.getValidationStatusProvider().getStatus(current);
                    for (final IValidationStatus currentStatus : statuses) {
                        if (currentStatus instanceof XSDDiagnosticValidationStatus) {
                            final int currentSeverity = currentStatus.getSeverity();
                            final EObject currentTarget = currentStatus.getConstraintStatusTarget();
                            final String currentId = currentStatus.getId();
                            for (final Iterator<IValidationStatus> iter = validationStatusList.iterator(); iter.hasNext();) {
                                final IValidationStatus existingStatus = iter.next();
                                if (existingStatus instanceof XSDDiagnosticValidationStatus) {
                                    if (currentSeverity == existingStatus.getSeverity()
                                            && currentTarget == existingStatus.getConstraintStatusTarget()
                                            && currentId.equals(existingStatus.getId())) {
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    }

                    validationStatusList.addAll(statuses);
                }
                MarkerUtils.updateMarkers(validationStatusList, locationUris);
            }
        };

        try {
            ResourcesPlugin.getWorkspace().run(vr, null);
        } catch (final CoreException e) {
            Logger.log(e.getStatus());
        }
    }

    public void setGlobalActionHandlers() {
        final IEditorSite site = getEditorSite();

        if (undoActionHandler == null) {
            undoActionHandler = new UndoActionHandler(site, undoContext);
        }

        if (redoActionHandler == null) {
            redoActionHandler = new RedoActionHandler(site, undoContext);
        }

        final IActionBars actionBars;

        actionBars = site.getActionBars();
        actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoActionHandler);
        actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoActionHandler);
        actionBars.updateActionBars();
    }

    private void updateModelRootURI(final IEditorInput input) {
        if (!(input instanceof IFileEditorInput) || commonModel == null) {
            return;
        }

        final EObject rootModelObject = commonModel.getModelObject().getComponent();
        if (rootModelObject != null && rootModelObject.eResource() != null) {
            try {
                final IFileEditorInput fileInput = (IFileEditorInput) input;
                final URI newURI = URI.createPlatformResourceURI(fileInput.getStorage().getFullPath().toString(), false);
                rootModelObject.eResource().setURI(newURI);
            } catch (final CoreException ex) {
                Logger.logError("Can't update resource URI", ex); //$NON-NLS-1$
            }
        }
    }

    public boolean validateEditorInputState() {
        if (sourcePage == null) {
            return true;
        }
        return sourcePage.validateEditorInputState();
    }

    // =========================================================
    // test helpers
    // =========================================================

    public SISourceEditorPart getSourcePage() {
        return sourcePage;
    }

    public IUndoContext getUndoContext() {
        return undoContext;
    }

    public IOperationHistory getOperationHistory() {
        return operationHistory;
    }

    public ValidationService getValidationService() {
        return validationService;
    }

    public IStructuredModel getStructuredModel() {
        return structuredModel;
    }
}
