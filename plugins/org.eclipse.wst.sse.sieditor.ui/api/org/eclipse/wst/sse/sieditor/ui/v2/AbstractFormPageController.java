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
package org.eclipse.wst.sse.sieditor.ui.v2;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.BaseNewTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewElementTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewElementTypeWithAddSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewSimpleTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewSimpleTypeWithAddSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewStructureTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewStructureTypeWithAddSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.DefaultSetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Facet;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.preedit.PreEditService;
import org.eclipse.wst.sse.sieditor.ui.preferences.ServiceInterfaceEditorPreferencePage;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.common.TypeSearchDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.SIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ISIComponentSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeResolver;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.SIEditorSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeResolverFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeSelectionDialogDelegate;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;

public abstract class AbstractFormPageController implements IFormPageController {

    protected IModelRoot model;

    protected boolean readOnly; // TODO - the multi page stores that data - get
    // it from there from now on?

    protected final TreeNodeMapper treeNodeMapper;

    protected ArrayList<ISIEventListener> eventListeners;

    /**
     * This class is providing the common functionality for a Service Interface
     * Form Page controller. <br>
     * - {@link TreeNodeMapper} instance<br>
     * - readOnly state persistence & getter<br>
     * - event listeners management and convenience methods for firing events<br>
     * - checks if an model object, treeNode or the model it self are able for
     * edit<br>
     * - provides the list and mapping between the names most common used
     * primitive types and them actually<br>
     * - used to open the {@link TypeSearchDialog}
     * 
     * @param model
     *            the model which the Controller will be operating upon
     * @param readOnly
     *            the flag determining if the model is enabled for edit or not
     */
    public AbstractFormPageController(final IModelRoot model, final boolean readOnly) {
        this.model = model;
        this.readOnly = readOnly;
        eventListeners = new ArrayList<ISIEventListener>();
        treeNodeMapper = new TreeNodeMapper();
    }

    /**
     * Called to determine if the editor is opened with a readOnly resource
     * 
     * @return the read only state of the editor page
     */
    public boolean isResourceReadOnly() {
        return readOnly;
    }

    /**
     * Method make the controller operate on a new model
     * 
     * @param model
     *            the model with which the controller will be operating
     * @param readOnly
     *            the mode of access to the model
     */
    public void setNewModel(final IModelRoot model, final boolean readOnly) {
        this.model = model;
        this.readOnly = readOnly;
        treeNodeMapper.clearAllNodesFromMap();
        final ISIEvent refreshEvent = new SIEvent(ISIEvent.ID_REFRESH_INPUT, null);
        // ISIEvent selectEvent = new SIEvent(ISIEvent.ID_SELECT_TREENODE, new
        // Object[0]);
        for (final ISIEventListener eventListener : eventListeners) {
            eventListener.notifyEvent(refreshEvent);
            // eventListener.notifyEvent(selectEvent);
        }
    }

    /**
     * 
     * @return The node mapper used to map Model Objects to Tree Nodes
     */
    public TreeNodeMapper getTreeNodeMapper() {
        return treeNodeMapper;
    }

    /**
     * Adds an FormPage event listener - Listens for errors in SI and errors and
     * updates of TreeNodes
     * 
     * @param listener
     *            the listener
     */
    public void addEventListener(final ISIEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Removes the listener from the list. If not existing in the first place -
     * nothing will happen
     * 
     * @param listener
     */
    public void removeEventListener(final ISIEventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Called to notify all registered listeners that the given ITreeNode object
     * should be selected
     * 
     * @param treeNode
     *            the tree node to be selected
     */
    protected void fireTreeNodeSelectionEvent(final ITreeNode treeNode) {
        final ISIEvent event = new SIEvent(ISIEvent.ID_SELECT_TREENODE, new Object[] { treeNode });
        for (final ISIEventListener eventListener : eventListeners) {
            eventListener.notifyEvent(event);
        }
    }

    protected void fireTreeNodeSelectionEvent(final IModelObject modelObject) {
        final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(modelObject);
        fireTreeNodeSelectionEvent(treeNode);
    }

    protected void fireTreeNodeExpandEvent(final IModelObject modelObject) {
        final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(modelObject);
        fireTreeNodeExpandEvent(treeNode);
    }

    protected void fireTreeNodeEditEvent(final IModelObject modelObject) {
        final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(modelObject);
        fireTreeNodeEditEvent(treeNode);
    }

    private void fireTreeNodeEditEvent(final ITreeNode treeNode) {
        fireEvent(ISIEvent.ID_EDIT_TREENODE, treeNode);
    }

    /**
     * notifies all registered listener that the given ITreeNode should be
     * expanded
     * 
     * @param treeNode
     */
    protected void fireTreeNodeExpandEvent(final ITreeNode treeNode) {
        fireEvent(ISIEvent.ID_TREE_NODE_EXPAND, treeNode);
    }

    public void fireShowErrorMsgEvent(final String message) {
        fireEvent(ISIEvent.ID_ERROR_MSG, message);
    }

    private void fireEvent(final int eventType, final Object parameter) {
        final ISIEvent event = new SIEvent(eventType, new Object[] { parameter });
        for (final ISIEventListener eventListener : eventListeners) {
            eventListener.notifyEvent(event);
        }
    }

    /**
     * Called in order to determine if the provided object is an editable one.<br>
     * The original implementation is not using the individual objects and
     * directly using the model root,<br>
     * due to the problem of incorrect object being passed and for dtr pre-edit
     * case for wsdl,<br>
     * object level change means a file level change.
     * 
     * @param editedObject
     *            the object for which the check will be done
     * @return true if edit is allowed, false otherwise.
     */
    protected boolean isEditAllowed(final Object editedObject) {
        // not using the individual objects and directly using the model root
        // due to the problem of incorrect object being passed
        // and for dtr pre-edit case for wsdl, object level change means a file
        // level change
        return !isResourceReadOnly() && PreEditService.getInstance().startEdit(model);
    }

    /**
     * Called in order to determine if the provided object can be deleted.<br>
     * The original implementation is not using the individual objects and
     * directly using the model root,<br>
     * due to the problem of incorrect object being passed and for dtr pre-edit
     * case for wsdl,<br>
     * object level change means a file level change.
     * 
     * @param editedObject
     *            the object for which the check will be done
     * @return true if delete is allowed, false otherwise.
     */
    protected boolean isDeleteAllowed(final Object editedObject) {
        // not using the individual objects and directly using the model root
        // due to the problem of incorrect object being passed
        // and for dtr pre-edit case for wsdl, object level change means a file
        // level change
        return !isResourceReadOnly() && PreEditService.getInstance().startDelete(model);
    }

    // TODO - refactor this one, it's going to be used?
    /**
     * Determines if the model object that the treeNode is representing is an
     * editable one
     * 
     * @return true if it is editable, false otherwise
     */
    public boolean canEdit(final ITreeNode node) {
        if (null == node || null == node.getModelObject()) { // TODO is this
            // check necessary
            return false;
        }
        return isEditAllowed(node.getModelObject());
    }

    /**
     * Used to retrieve a list of most often used type names
     * 
     * @return the list of type names
     */
    @Override
    public String[] getCommonTypesDropDownList() {
        final String[] primTypes = BuiltinTypesHelper.getInstance().getCommonlyUsedTypeNames();
        final String[] ret = new String[primTypes.length + 1];
        ret[0] = Messages.TypePropertyEditor_browse_button;
        for (int i = 0, j = 1; i < primTypes.length; i++, j++) {
            ret[j] = primTypes[i];
        }
        return ret;
    }

    public void editDocumentation(final ITreeNode treeNode, final String text) {
        // TODO do some checks only if necessary
        AbstractNotificationOperation command = null;

        final IModelObject modelObject = treeNode.getModelObject();
        if (modelObject instanceof Facet) {
            // do nothing
            return;
        }
        if (model instanceof IWsdlModelRoot) {
            command = new SetDocumentationCommand(model, modelObject, ((WSDLElement) modelObject.getComponent()).getElement(),
                    text);
        } else if (model instanceof IXSDModelRoot) {
            command = new org.eclipse.wst.sse.sieditor.command.emf.xsd.SetDocumentationCommand(model, modelObject, text);
        } else {
            Logger
                    .log(new Status(
                            IStatus.ERROR,
                            Activator.PLUGIN_ID,
                            "Failed to edit element documentation " + treeNode.getDisplayName() + " override set documentation to use the correct command")); //$NON-NLS-1$//$NON-NLS-2$
        }
        try {
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_documentation,
                        Messages.SIFormPageController_msg_failure_edit_documentation, status);
                return;
            }
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit element documentation " + treeNode.getDisplayName(), //$NON-NLS-1$
                    e);
        }
    }

    private IEditorInput editorInput;

    public void setEditorInput(final IEditorInput editorInput) {
        this.editorInput = editorInput;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController#openTypesDialog
     * ()
     */
    public IType openTypesDialog() {
        return openTypesDialog(Messages.AbstractFormPageController_type_wizard_display_text, null, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController#openTypesDialog
     * (java.lang.String, boolean)
     */
    public IType openTypesDialog(final String displayText, final IModelObject selectedModelObject, final boolean showComplexTypes) {

        // could be null
        final IFile editorFile = (IFile) editorInput.getAdapter(IFile.class);
        IType result = null;

        final IModelObject modelObject = getModelObject();
        final ITypeResolver typeResolver = TypeResolverFactory.getInstance().createTypeResolver(modelObject);
        final XSDSchema[] schemas = typeResolver.getLocalSchemas();
        final ITypeSelectionDialogDelegate dialog = createTypeSelectionDialog(editorFile, schemas, displayText,
                selectedModelObject, showComplexTypes);

        final boolean[] bool = { false };
        final AbstractEMFOperation op = new AbstractEMFOperation(model.getEnv().getEditingDomain(), "Load selected types") { //$NON-NLS-1$
            @Override
            protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                bool[0] = dialog.open();
                return Status.OK_STATUS;
            }
        };

        try {
            new DefaultOperationHistory().execute(op, null, null);
        } catch (final ExecutionException e) {
            Logger.logError(e.getMessage(), e);

            return UnresolvedType.instance();
        }

        if (bool[0]) {
            final Object selectedObject = dialog.getSelectedObject();
            if (selectedObject instanceof XSDNamedComponent) {
                result = typeResolver.resolveType((XSDNamedComponent) selectedObject);
            }

            if (result == null) {
                final String name = dialog.getSelectedTypeName();
                final String namespace = dialog.getSelectedTypeNamespace();
                final IFile file = dialog.getSelectedTypeFile();

                if (file != null) {
                    result = typeResolver.resolveType(name, namespace, file);
                } else {
                    throw new IllegalStateException("Cannot find XSD type " + new QName(namespace, name)); //$NON-NLS-1$
                }
            }

        }

        return result;

        // old logic
        // return
        // TypesDialogCreator.getInstance().openTypesDialog(getModelObject());
    }

    /*
     * For mocking
     */
    protected ITypeSelectionDialogDelegate createTypeSelectionDialog(final IFile editorFile, final XSDSchema[] schemas,
            final String displayText, final IModelObject selectedModelObject, final boolean showComplexTypes) {
        final TypeSelectionDialogDelegate typeSelectionDialog = new TypeSelectionDialogDelegate(editorFile, schemas);
        final Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        typeSelectionDialog.create(shell, displayText, createSearchListProvider(selectedModelObject, editorFile, schemas,
                showComplexTypes));
        return typeSelectionDialog;
    }

    /**
     * Template method used to create the list provider for the type search
     * dialog. The type search dialog could be created in a couple of different
     * fashions: Showing complex types or not, showing global elements or not,
     * and filterin out one type.
     * 
     * @param selectedModelObject
     *            the selected model object according to which the custom
     *            content of the list should be defined. In the Default
     *            implementation - the type to be filtered and the existance ot
     *            complex types and global elements in the list isdefined by
     *            this parameter
     * @param contextFile
     *            the file needed to create the provider
     * @param schemas
     *            the schemas included
     * @return the newly created provider
     */
    protected ISIComponentSearchListProvider createSearchListProvider(final IModelObject selectedModelObject,
            final IFile contextFile, final XSDSchema[] schemas, boolean showComplexTypes) {
        IType typeToFilter = null;
        boolean localShowComplexTypes = false;
        boolean showElements = false;
        boolean showSimpleTypes = true;

        if (selectedModelObject instanceof IFault) {
            showSimpleTypes = false;
            localShowComplexTypes = false;
            showElements = true;
        } else if (selectedModelObject instanceof IElement) {
            if (!((IElement) selectedModelObject).isAttribute()) {
                localShowComplexTypes = true;
                showElements = true;
            }
        } else if (selectedModelObject instanceof IType) {
            typeToFilter = (IType) selectedModelObject;

            if (typeToFilter instanceof StructureType) {
                localShowComplexTypes = true;

                if (!((StructureType) typeToFilter).isElement()) {
                    showElements = false;
                }
            }
        }
        
        // consider the PropertyEditor's opinion
        showComplexTypes &= localShowComplexTypes;

        final SIEditorSearchListProvider searchListProvider = new SIEditorSearchListProvider(contextFile, schemas, showElements,
                showComplexTypes, showSimpleTypes);
        searchListProvider.setTypeToFilter(typeToFilter);
        return searchListProvider;
    }

    protected abstract IModelObject getModelObject();

    protected abstract String getEditorID();

    abstract protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode);

    protected ITreeNode getNextSiblingTreeNode(final ITreeNode selectedTreeNode, final Object[] siblings) {
        int index = 0;
        // getting the index of the current node
        for (int i = 0; i < siblings.length; i++) {
            if (selectedTreeNode.equals(siblings[i])) {
                index = i;
                break;
            }
        }
        if (index == siblings.length - 1) {
            if (siblings.length == 1) { // returning the parent node
                return selectedTreeNode.getParent();
            }
            return (ITreeNode) siblings[index - 1]; // returning the
            // previous node
        }
        return (ITreeNode) siblings[index + 1]; // returning the next node
    }

    @Override
    public boolean areAllItemsPartOfEditedDocument(final List<? extends ITreeNode> items) {
        if (items == null) {
            return true;
        }

        if (items.size() == 1 && (items.get(0) instanceof ImportedTypesNode || items.get(0) instanceof ImportedServicesNode)) {
            return true;
        }

        for (final ITreeNode item : items) {
            if ((item.getCategories() & ITreeNode.CATEGORY_IMPORTED) == ITreeNode.CATEGORY_IMPORTED) {
                return false;
            }
        }
        return true;
    }

    public boolean isPartOfEdittedDocument(final IModelObject modelObject) {
        return EmfWsdlUtils.isModelObjectPartOfModelRoot(model, modelObject);
    }

    public void openInNewEditor(final ITreeNode node) {
        if (node == null) {
            return;
        }
        IPath rootPath = null;
        String importedLocation = null;

        final IModelObject modelObject = node.getModelObject();
        final IDescription referredDescription = EmfWsdlUtils.getReferredDescription(modelObject);
        final ISchema referedSchema = EmfWsdlUtils.getReferredSchema(modelObject);

        if (referredDescription != null) {
            final IDescription rootDescription = ((IWsdlModelRoot) referredDescription.getModelRoot()).getDescription();

            rootPath = new Path(rootDescription.getLocation());
            rootPath = rootPath.removeLastSegments(1);
            importedLocation = referredDescription.getLocation();
        } else if (referedSchema != null) {
            final ISchema rootSchema = ((IXSDModelRoot) referedSchema.getModelRoot()).getSchema();

            rootPath = new Path(rootSchema.getLocation());
            rootPath = rootPath.removeLastSegments(1);
            importedLocation = referedSchema.getLocation();
        }

        if (importedLocation != null) {
            final IPath path = new Path(rootPath.toOSString() + File.separator + importedLocation);
            openInNewEditor(path, getEditorID());
        }
    }

    public void openInNewEditor(final IType type) {
        if (type == null) {
            return;
        }
        // do not use type.getSchema(), because it has relative URI
        // Construct full path
        final ISchema importedSchema = type.getParent();

        String rootPathString = null;
        if (importedSchema.getParent() instanceof IDescription) {
            rootPathString = ((IWsdlModelRoot) importedSchema.getParent().getModelRoot()).getDescription().getLocation();
        } else {
            rootPathString = ((IXSDModelRoot) importedSchema.getModelRoot()).getSchema().getLocation();
        }

        IPath rootPath = new Path(rootPathString);
        rootPath = rootPath.removeLastSegments(1);
        final String importedLocation = importedSchema.getLocation();

        final IPath path = new Path(rootPath.toOSString() + File.separator + importedLocation);
        final String editorID = importedSchema.getParent() instanceof IDescription ? ServiceInterfaceEditor.EDITOR_ID
                : DataTypesEditor.EDITOR_ID;
        openInNewEditor(path, editorID);

    }

    public boolean isOpenInNewEditorEnabled(final ITreeNode iTreeNode) {
        final IModelObject modelObject = iTreeNode.getModelObject();
        if (modelObject == null) {
            return false;
        }
        return !EmfWsdlUtils.isModelObjectPartOfModelRoot(modelObject.getModelRoot(), modelObject);
    }

    private void openInNewEditor(final IPath path, final String editorID) {
        final IFile file = ResourceUtils.getWorkSpaceFile(path);
        boolean hasError = file == null;

        if (file != null) {
            try {
                final IWorkbenchWindow window = getActiveWorkbenchWindow();
                final FileEditorInput eInput = new FileEditorInput(file);
                final IWorkbenchPage workbenchActivePage = window.getActivePage();

                final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
                final String doNotShow = preferenceStore
                        .getString(ServiceInterfaceEditorPreferencePage.EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN);
                if (UIConstants.EMPTY_STRING.equals(doNotShow)) {
                    showWarningMessage(window, preferenceStore);
                }

                workbenchActivePage.openEditor(eInput, editorID);
            } catch (final PartInitException e) {
                Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not open editor for file: " //$NON-NLS-1$
                        + file.getFullPath().toString(), e);
                hasError = true;
            }
        }

        if (hasError) {
            final IStatus statusToShow = new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.AbstractFormPageController_4, path.toString()));
            StatusUtils.showStatusDialog(Messages.AbstractFormPageController_5, statusToShow);
        }
    }

    protected IWorkbenchWindow getActiveWorkbenchWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    protected void showWarningMessage(final IWorkbenchWindow window, final IPreferenceStore preferenceStore) {
        MessageDialogWithToggle.openWarning(window.getShell(), Messages.AbstractFormPageController_0,
                Messages.AbstractFormPageController_1, Messages.AbstractFormPageController_2, false, preferenceStore,
                ServiceInterfaceEditorPreferencePage.EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN);
    }

    // ===========================================================
    // new type command execution helpers
    // ===========================================================

    @Override
    public void newElementType(final String name, final ISchema schema, final TypePropertyEditor propertyEditor) {
        final ISetTypeCommandBuilder setTypeCommandBuilder = createNewTypeSetTypeCommandBuilder(propertyEditor);
        BaseNewTypeCompositeCommand command = null;
        if (schema == null) {
            command = new NewElementTypeWithAddSchemaCompositeCommand(getModelObject().getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_element_type, setTypeCommandBuilder, name);
        } else {
            command = new NewElementTypeCompositeCommand(schema.getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_element_type, schema, name, setTypeCommandBuilder);
        }
        executeNewTypeCommand(command, name);

        postExecuteNewTypeCommand(command.getType(), propertyEditor.getInput().getModelObject());
    }

    @Override
    public void newSimpleType(final String name, final ISchema schema, final TypePropertyEditor propertyEditor) {
        final ISetTypeCommandBuilder setTypeCommandBuilder = createNewTypeSetTypeCommandBuilder(propertyEditor);

        BaseNewTypeCompositeCommand command = null;
        if (schema == null) {
            command = new NewSimpleTypeWithAddSchemaCompositeCommand(getModelObject().getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_simple_type, setTypeCommandBuilder, name);
        } else {
            command = new NewSimpleTypeCompositeCommand(schema.getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_simple_type, schema, name, setTypeCommandBuilder);
        }
        executeNewTypeCommand(command, name);
        postExecuteNewTypeCommand(command.getType(), propertyEditor.getInput().getModelObject());
    }

    @Override
    public void newStructureType(final String name, final ISchema schema, final TypePropertyEditor propertyEditor) {
        final ISetTypeCommandBuilder setTypeCommandBuilder = createNewTypeSetTypeCommandBuilder(propertyEditor);
        BaseNewTypeCompositeCommand command = null;
        if (schema == null) {
            command = new NewStructureTypeWithAddSchemaCompositeCommand(getModelObject().getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_structure_type, setTypeCommandBuilder, name);
        } else {
            command = new NewStructureTypeCompositeCommand(schema.getModelRoot(), getModelObject(),
                    Messages.AbstractFormPageController_cmd_label_new_structure_type, schema, name, setTypeCommandBuilder);
        }
        executeNewTypeCommand(command, name);
        postExecuteNewTypeCommand(command.getType(), propertyEditor.getInput().getModelObject());
    }

    /**
     * Utility method. Notifies the listeners that the g
     * 
     * @param newModelObject
     */
    protected void postExecuteNewTypeCommand(final IModelObject newModelObject, final IModelObject sourceModelObject) {
        fireTreeNodeExpandEvent(newModelObject);

        if (sourceModelObject != null && newModelObject.getModelRoot() != sourceModelObject.getModelRoot()) {
            fireTreeNodeSelectionEvent(sourceModelObject);
        } else {
            fireTreeNodeSelectionEvent(newModelObject);
        }
    }

    /**
     * utility method. creates the set type command builder for the new element
     * type
     * 
     * @param propertyEditor
     *            - the {@link TypePropertyEditor} from which the method call is
     *            issued
     * @return the created set type command builder for the new element type
     */
    protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(final TypePropertyEditor propertyEditor) {
        if (propertyEditor.getInput().getModelObject().getModelRoot() instanceof IWsdlModelRoot) {
            final IModelObject modelObject = UIUtils.instance().getParameterFromUINode(propertyEditor.getInput());
            return new DefaultSetTypeCommandBuilder(modelObject);
        }
        if (propertyEditor.getInput().getModelObject().getModelRoot() instanceof IXSDModelRoot) {
            return new DefaultSetTypeCommandBuilder(propertyEditor.getInput().getModelObject());
        }
        return null;
    }

    /**
     * Utility method. Executes the {@link AbstractNewTypeCommand} command. Logs
     * any {@link ExecutionException}s. Displays error dialog if the execution
     * status was not {@link IStatus#OK}
     * 
     * @param command
     *            - the command to execute
     * @param typeName
     *            - the name of the new type (for display purposes only)
     */
    protected void executeNewTypeCommand(final BaseNewTypeCompositeCommand command, final String typeName) {
        try {
            final IStatus status = getModelObject().getModelRoot().getEnv().execute(command);
            if (!status.isOK()) {
                StatusUtils.showStatusDialog(Messages.AbstractFormPageController_dlg_title_new_type, MessageFormat.format(
                        Messages.AbstractFormPageController_dlg_err_msg_new_type, typeName), status);
            }
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to create new type", e); //$NON-NLS-1$
        }
    }

}
