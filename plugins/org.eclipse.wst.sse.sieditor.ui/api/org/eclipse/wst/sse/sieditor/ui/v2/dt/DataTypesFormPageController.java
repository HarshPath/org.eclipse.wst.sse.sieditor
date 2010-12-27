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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDPackage;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.InlineNamespaceCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddEnumFacetToElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetToElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.FacetsCommandFactory;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.InlineStructureTypeContentsCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeGlobalTypeAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameNamedComponent;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementNillableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementOccurences;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetGlobalElementNillableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeBaseTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ICondition;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.ExtractNamespaceWizard;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.utils.ExtractSchemaWizardConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.SIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class DataTypesFormPageController extends AbstractFormPageController implements IDataTypesFormPageController {

    public static final String ELEMENT_DEFAULT_NAME = "Element"; //$NON-NLS-1$
    public static final String FAULT_ELEMENT_DEFAULT_NAME = "FaultElement"; //$NON-NLS-1$
    public static final String STRUCTURE_TYPE_DEFAULT_NAME = "StructureType"; //$NON-NLS-1$
    public static final String SIMPLE_TYPE_DEFAULT_NAME = "SimpleType"; //$NON-NLS-1$
    public static final String ATTRIBUTE_DEFAULT_NAME = "Attribute"; //$NON-NLS-1$
    private static final String ANY_TYPE = "anyType"; //$NON-NLS-1$

    /**
     * Presenter would hold the View, and model instances along with the event
     * broker. Adds itself as above listener to model and view (which ever is
     * applicable)
     * 
     * @param view
     * @param model
     * @param eventBroker
     * @param readOnly
     */
    public DataTypesFormPageController(final IModelRoot model, final boolean readOnly) {
        super(model, readOnly);
    }

    @Override
    public boolean isResourceReadOnly() {
        return super.isResourceReadOnly() || (model instanceof IXSDModelRoot && isStandaloneDTEReadOnly());
    }

    private boolean isStandaloneDTEReadOnly() {
        return EmfXsdUtils.isSchemaElementMissing(((IXSDModelRoot) model).getSchema())
                || EmfXsdUtils.isSchemaForSchemaMissing(getModelObject());
    }

    /**
     * After a delete action, the node related to that element and its child
     * nodes need to be removed This methods removes such nodes from the tree
     * node mapper, which contains all nodes present in the tree.
     * 
     * @param treeNode
     */
    public void removeNodeAndItsChildrenFromMap(final ITreeNode treeNode) {
        if (treeNode.hasChildren()) {
            for (final Object childNode : treeNode.getChildren()) {
                if (childNode instanceof ITreeNode)
                    removeNodeAndItsChildrenFromMap((ITreeNode) childNode);
            }
        }
        getTreeNodeMapper().removeNodeFromMap(treeNode);
    }

    /**
     * Returns the next tree node after a delete operation on a given node. 3
     * different cases are handled a. First, it returns the tree node which is
     * after this tree node. b. When there are no nodes after this tree node
     * (when the last tree node is deleted), it returns the previous tree node
     * c. If the deleted node is the only child, then the parent node is
     * selected.
     * 
     * @param selectedTreeNode
     * @return the closest relative node to the given, null if such does'nt
     *         exist
     */
    @Override
    protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
        if (null == selectedTreeNode) {
            return null;
        }

        Object[] siblingTreeNodes = null;

        if (null == selectedTreeNode.getParent()) {
            if (selectedTreeNode instanceof INamespaceNode) {
                if (model instanceof IWsdlModelRoot) {
                    final List<ISchema> containedSchemas = ((IWsdlModelRoot) model).getDescription().getContainedSchemas();
                    final ArrayList<ITreeNode> siblings = new ArrayList<ITreeNode>();
                    for (final ISchema schema : containedSchemas) {
                        siblings.add(getTreeNodeMapper().getTreeNode(schema));
                    }
                    siblingTreeNodes = siblings.toArray();
                }
                if (model instanceof IXSDModelRoot) {
                    return null;
                }
            } else {
                throw new InvalidParameterException(
                        "the tree node given is a Parentless tree node, not an instace of INamespaceNode"); //$NON-NLS-1$
            }
        } else {
            siblingTreeNodes = selectedTreeNode.getParent().getChildren();
        }
        return getNextSiblingTreeNode(selectedTreeNode, siblingTreeNodes); // returning
        // the
        // next
        // node
    }

    @Override
    public void editDocumentation(final ITreeNode treeNode, final String text) {
        final IModelObject selectionModelObject = treeNode.getModelObject();
        if (selectionModelObject == null) {
            throw new IllegalArgumentException("Model object of the selected tree node is null"); //$NON-NLS-1$
        }

        if (!isEditAllowed(selectionModelObject)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, treeNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_edit_documentation, status);
            return;
        }
        final IStatus status = executeCommand(new SetDocumentationCommand(model, selectionModelObject, text));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_edit_documentation, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_edit_documentation_of_element_X, treeNode.getDisplayName()),
                    status);
        }
    }

    /**
     * Method to retrieve the model root , specially in case the model is not an
     * IXSDModelRoot.<br>
     * In that case a new model root for a schema is created and all the
     * listeners for the model are added
     * 
     * @param schema
     *            the {@link ISchema} for which the model root is being
     *            determined
     * @return the {@link IXSDModelRoot} root for the
     */
    protected IXSDModelRoot getXSDModelRoot(final ISchema schema) {
        return (IXSDModelRoot) schema.getModelRoot();
    }

    /**
     * Generates Name for a new Simple Type
     */
    public String getNewElementName(final IModelObject modelObject) {
        return getNewElementDefaultName(modelObject);
    }

    public static String getNewFaultElementDefaultName(final IModelObject modelObject) {
        return NameGenerator.getNewFaultElementDefaultName(modelObject);
    }

    public static String getNewElementDefaultName(final IModelObject modelObject) {
        return NameGenerator.getNewElementDefaultName(modelObject);
    }

    protected String getNewAttributeName(final IStructureType structureType) {
        return NameGenerator.getNewAttributeDefaultName(structureType);
    }

    /**
     * Method called in order to add an xsd Element relative to the selected
     * node's model object.<br>
     * The Element could be added as a global element in a {@link Schema}
     * (namespace), as an element in a {@link StructureType}, or<br>
     * as an element in an anonymous type. In the last case - the selected
     * element must not be an attribute
     * 
     * @param selectionModelObject
     *            the model object relative which the addition will be executed
     */
    public void handleAddElementAction(final ITreeNode selectedTreeNode) {
        final IModelObject selectionModelObject = selectedTreeNode.getModelObject();
        final IModelObject newElement = addNewElement(selectionModelObject, null);
        if (newElement != null) {
            fireTreeNodeSelectionEvent(newElement);
            fireTreeNodeExpandEvent(newElement);
            fireTreeNodeEditEvent(newElement);
        }
    }

    /**
     * Method adding a new element in the specified model {@link IModelObject}
     * with the given 'new name'<br>
     * If the new Name is null a default new name will be generated for the new
     * element.
     * 
     * @param selectionModelObject
     *            the parent object
     * @param newName
     *            the new name or null for a default generated one
     * @return
     */
    public IModelObject addNewElement(final IModelObject selectionModelObject, String newName) {
        final ITreeNode selectedTreeNode = getTreeNodeMapper().getTreeNode(selectionModelObject);
        if (selectionModelObject == null) {
            throw new IllegalArgumentException("Tree node selection is null."); //$NON-NLS-1$
        }

        if (!isEditAllowed(selectionModelObject)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, selectedTreeNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_element, status);
            return null;
        }
        AbstractNotificationOperation command = null;

        if (selectionModelObject instanceof ISchema) {
            final ISchema schema = (ISchema) selectionModelObject;
            final IXSDModelRoot modelRoot = getXSDModelRoot(schema);
            newName = newName == null ? getNewElementName(schema) : newName;
            command = new AddStructureTypeCommand(modelRoot, schema,
                    Messages.DataTypesFormPageController_add_global_type_command_label, newName, true, (AbstractType) Schema
                            .getDefaultSimpleType());
        }
        if (selectionModelObject instanceof IStructureType) {
            final IStructureType strType = (IStructureType) selectionModelObject;
            final XSDConcreteComponent component = strType.getComponent();
            if ((component instanceof XSDElementDeclaration) && (!strType.getType().isAnonymous())) {
                // simple type element in schema
                return addNewElement(strType.getParent(), newName);
            } else {
                newName = newName == null ? getNewElementName(selectionModelObject) : newName;
                command = new AddElementCommand(selectionModelObject.getModelRoot(), (IStructureType) selectionModelObject,
                        newName);
            }
        }
        if (selectionModelObject instanceof IElement) {
            final IElement element = (IElement) selectionModelObject;

            IType type = element.getType();

            final IModelObject parent = element.getParent();
            if ((parent instanceof IStructureType)) {
                type = (IStructureType) parent;
            }

            if (type instanceof IStructureType) {
                newName = newName == null ? getNewElementName(type) : newName;
                command = new AddElementCommand(element.getModelRoot(), (IStructureType) type, newName);
            }
        }
        if (selectionModelObject instanceof ISimpleType) {
            final ISimpleType simpleType = (ISimpleType) selectionModelObject;
            return addNewElement(simpleType.getParent(), newName);
        }
        if (command == null) {
            throw new IllegalStateException("model object to which elements can be added are structure types schemas or elements"); //$NON-NLS-1$
        }
        try {
            final IStatus status = model.getEnv().execute(command);
            if (StatusUtils.canContinue(status)) {
                if (selectedTreeNode == null && model instanceof IXSDModelRoot) {
                    // Called in order to reload all the root nodes in the DT
                    // tree.
                    // TODO Can be optimised by calling getElements to the
                    // DTTreeContentProvider
                    final ISIEvent refreshEvent = new SIEvent(ISIEvent.ID_REFRESH_INPUT, null);
                    for (final ISIEventListener eventListener : eventListeners) {
                        eventListener.notifyEvent(refreshEvent);
                    }
                } else {
                    selectedTreeNode.getChildren();
                }
                IModelObject newElement = null;
                if (command instanceof AddElementCommand) {
                    newElement = ((AddElementCommand) command).getElement();
                } else if (command instanceof AddStructureTypeCommand) {
                    newElement = ((AddStructureTypeCommand) command).getStructureType();
                }
                return newElement;
            }
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_element,
                    Messages.DataTypesFormPageController_msg_can_not_add_new_element, status);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR,
                    "Can not add element to tree node " + selectedTreeNode.getDisplayName(), e); //$NON-NLS-1$
        }
        return null;
    }

    /*
     * Generates Name for a new Simple Type
     */
    public String getNewSimpleTypeName(final ISchema schema) {
        return getNewSimpleTypeDefaultName(schema);
    }

    /**
     * Default implementation for the this{@link #getNewSimpleTypeName(ISchema)}
     * method
     * 
     * @param schema
     * @return
     */
    public static String getNewSimpleTypeDefaultName(final ISchema schema) {
        return NameGenerator.generateName(SIMPLE_TYPE_DEFAULT_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return schema.getAllTypes(in) == null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.listeners.IDataTypeViewListener#
     * handleAddSimpleTypeAction(java.lang.String,
     * org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema)
     */
    public void handleAddSimpleTypeAction(final ITreeNode selectedNode) {
        ISchema schema;
        if (model instanceof IXSDModelRoot) {
            schema = ((IXSDModelRoot) model).getSchema();
        } else {
            schema = getSchema(selectedNode);
        }
        final IType newSimpleType = addNewSimpleType(schema, null);
        if (newSimpleType != null) {
            fireTreeNodeSelectionEvent(newSimpleType);
            fireTreeNodeEditEvent(newSimpleType);
        }
    }

    public IType addNewSimpleType(final ISchema schema, String newName) {
        final ITreeNode selectedNode = getTreeNodeMapper().getTreeNode(schema);
        if (schema == null) {
            throw new IllegalArgumentException();
        }
        if (!isEditAllowed(schema)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, selectedNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_simple_type, status);

            return null;
        }
        IXSDModelRoot xsdRoot;
        if (model instanceof IXSDModelRoot) {
            xsdRoot = (IXSDModelRoot) model;
        } else {
            xsdRoot = getXSDModelRoot(schema);
        }
        if (xsdRoot == null) {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_find_XSD_root_for_schema_X, schema.getLocation()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_simple_type, status);
            return null;
        }
        newName = newName == null ? getNewSimpleTypeName(schema) : newName;
        final AddSimpleTypeCommand command = new AddSimpleTypeCommand(xsdRoot, schema, newName);
        try {
            final IStatus status = xsdRoot.getEnv().execute(command);
            if (StatusUtils.canContinue(status)) {
                if (model instanceof IWsdlModelRoot) {
                    final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(schema);
                    treeNode.getChildren();
                }
                final SimpleType simpleType = command.getSimpleType();
                return simpleType;
            }
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_simple_type,
                    Messages.DataTypesFormPageController_msg_can_not_add_simple_type, status);
        } catch (final ExecutionException e) {
            fireShowErrorMsgEvent(Messages.DataTypesFormPageController_error_msg_addition_simple_type_failed);
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Addition of Simple type failed", e); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Generates Name for a new Structure Type
     */
    public String getNewStructureTypeName(final ISchema schema) {
        return getNewStructureTypeDefaultName(schema);
    }

    /**
     * Default implementation for the this
     * {@link #getNewStructureTypeName(ISchema)} method.
     * 
     * @param schema
     * @return
     */
    public static String getNewStructureTypeDefaultName(final ISchema schema) {
        return NameGenerator.generateName(STRUCTURE_TYPE_DEFAULT_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return schema.getAllTypes(in) == null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.listeners.IDataTypeViewListener#
     * handleAddStructureTypeAction(java.lang.String,
     * org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema)
     */
    public void handleAddStructureTypeAction(final ITreeNode selectedElement) {
        ISchema schema;
        if (model instanceof IXSDModelRoot) {
            schema = ((IXSDModelRoot) model).getSchema();
        } else {
            schema = getSchema(selectedElement);
        }
        final IStructureType newStructureType = addNewStructureType(schema, null);
        if (newStructureType != null) {
            fireTreeNodeSelectionEvent(newStructureType);
            fireTreeNodeExpandEvent(newStructureType);
            fireTreeNodeEditEvent(newStructureType);
        }

    }

    public IStructureType addNewStructureType(final ISchema schema, String newName) {
        final ITreeNode selectedElement = getTreeNodeMapper().getTreeNode(schema);
        if (schema == null) {
            throw new IllegalArgumentException();
        }
        if (!isEditAllowed(schema)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, selectedElement.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_structure_type, status);

            return null;
        }

        final IXSDModelRoot xsdRoot;
        if (model instanceof IXSDModelRoot) {
            xsdRoot = (IXSDModelRoot) model;
        } else {
            xsdRoot = getXSDModelRoot(schema);
        }
        if (xsdRoot == null) {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_find_XSD_root_for_schema_X, schema.getLocation()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_structure_type, status);
            return null;
        }
        newName = newName == null ? getNewStructureTypeName(schema) : newName;
        final AddStructureTypeCommand addStructureCommand = new AddStructureTypeCommand(xsdRoot, schema,
                Messages.DataTypesFormPageController_add_structure_type_command_label, newName, false, null);
        final AbstractCompositeNotificationOperation compositeNotificationOperation = new AbstractCompositeNotificationOperation(
                xsdRoot, schema, addStructureCommand.getLabel()) {

            {
                setTransactionPolicy(AbstractCompositeNotificationOperation.TransactionPolicy.MULTI);
            }

            @Override
            public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
                if (subOperations.isEmpty()) {
                    return addStructureCommand;
                } else if (subOperations.size() == 1) {
                    if (model instanceof IWsdlModelRoot) {
                        final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(schema);
                        treeNode.getChildren();
                    }

                    final StructureType structureType = addStructureCommand.getStructureType();

                    // add new element in the structure so that it is not empty
                    return new AddElementCommand(xsdRoot, structureType, getNewElementName(structureType));
                }
                return null;
            }
        };

        final IStatus status = executeCommand(compositeNotificationOperation);
        if (!status.isOK()) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_structure_type,
                    Messages.DataTypesFormPageController_msg_can_not_add_structure_type, status);
        }
        return addStructureCommand.getStructureType();
    }

    /**
     * Retrieves the schema the given tree node belongs to. If it does not
     * (which should not be the use case ) returns null
     * 
     * @param node
     *            the node which's Schema is being retrieved
     * @return the schema or null
     */

    protected ISchema getSchema(final ITreeNode node) {
        ISchema schema = null;
        if (node instanceof INamespaceNode) {
            final INamespaceNode namespaceNode = (INamespaceNode) node;
            schema = (ISchema) namespaceNode.getModelObject();
        } else if (node instanceof ISimpleTypeNode) {
            final ISimpleTypeNode simpleTypeNode = (ISimpleTypeNode) node;
            final INamespaceNode parent = (INamespaceNode) simpleTypeNode.getParent();
            schema = (ISchema) parent.getModelObject();
        } else if (node instanceof IStructureTypeNode) {
            final IStructureTypeNode structureTypeNode = (IStructureTypeNode) node;
            final INamespaceNode parent = (INamespaceNode) structureTypeNode.getParent();
            schema = (ISchema) parent.getModelObject();
        } else if (node instanceof IElementNode) {
            final IElementNode elementNode = (IElementNode) node;
            ITreeNode parent = elementNode.getParent();
            while (!(parent instanceof INamespaceNode)) {
                parent = parent.getParent();
            }
            schema = (ISchema) parent.getModelObject();
        }
        return schema;
    }

    public void handleAddAttributeAction(final ITreeNode selectedElement) {
        ISchema schema;
        if (model instanceof IXSDModelRoot) {
            schema = ((IXSDModelRoot) model).getSchema();
        } else {
            schema = getSchema(selectedElement);
        }
        if (schema == null) {
            throw new IllegalArgumentException();
        }
        if (!isEditAllowed(schema)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, selectedElement.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_attribute, status);

            return;
        }

        IXSDModelRoot xsdRoot;
        if (model instanceof IXSDModelRoot) {
            xsdRoot = (IXSDModelRoot) model;
        } else {
            xsdRoot = getXSDModelRoot(schema);
        }
        if (xsdRoot == null) {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_find_XSD_root_for_schema_X, schema.getLocation()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_attribute, status);
            return;
        }

        final IModelObject modelObject = selectedElement.getModelObject();
        IStructureType structureType = null;
        if (modelObject instanceof IStructureType) {
            structureType = (IStructureType) selectedElement.getModelObject();
        } else if (modelObject instanceof IElement) {
            final IModelObject type = ((IElement) modelObject).getParent();
            if (type instanceof IStructureType) {
                structureType = (IStructureType) type;
            } else if (type instanceof ISimpleType) {
                final IModelObject parent = modelObject.getParent();
                if (parent instanceof IStructureType) {
                    structureType = (IStructureType) parent;
                }
            }
        }
        if (structureType != null) {
            final AddAttributeCommand command = new AddAttributeCommand(xsdRoot, structureType,
                    getNewAttributeName(structureType));
            try {
                final IStatus status = xsdRoot.getEnv().execute(command);
                if (StatusUtils.canContinue(status)) {
                    if (model instanceof IWsdlModelRoot) {
                        final ITreeNode treeNode = getTreeNodeMapper().getTreeNode(schema);
                        treeNode.getChildren();
                    }
                    final IElement attribute = command.getAttribute();
                    fireTreeNodeSelectionEvent(attribute);
                    fireTreeNodeEditEvent(attribute);
                } else {
                    StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_attribute,
                            Messages.DataTypesFormPageController_msg_failure_add_attribute, status);
                }
            } catch (final ExecutionException e) {
                final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                        Messages.DataTypesFormPageController_msg_error_while_executing_add_attribute_command, e);
                Logger.log(status);
                StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_attribute, status);
            }
        }
    }

    /**
     * 
     * Method called in order to remove the model objects represented by the
     * tree nodes
     * 
     * @param nodes
     *            tree nodes to be deleted
     */

    public void handleRemoveAction(final List<ITreeNode> nodes) {

        if (nodes == null || nodes.isEmpty()) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
                    Messages.DataTypesFormPageController_msg_target_element_can_not_be_deleted);
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_element, status);
            return;
        }

        final Set<IModelObject> parents = new HashSet<IModelObject>();
        ITreeNode nodeToSelect = null;
        final ArrayList<IModelObject> objectsToRemove = new ArrayList<IModelObject>();
        for (final ITreeNode node : nodes) {

            final IModelObject modelObject = node.getModelObject();
            if (!isEditAllowed(modelObject)) {
                return;
            }
            // select the first "next" node which will not be deleted
            if (nodeToSelect == null) {
                final ITreeNode nextTreeNode = getNextTreeNode(node);
                if (!nodes.contains(nextTreeNode)) {
                    nodeToSelect = nextTreeNode;
                }
            }
            objectsToRemove.add(modelObject);

            parents.add(modelObject.getParent());
        }
        final DeleteSetCommand deleteCommand = new DeleteSetCommand(model, parents, objectsToRemove);

        if (!deleteCommand.canExecute()) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
                    Messages.DataTypesFormPageController_msg_target_element_can_not_be_deleted);
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_element, status);
            return;

        }
        try {
            final IStatus status = model.getEnv().execute(deleteCommand);
            if (StatusUtils.canContinue(status)) {
                fireTreeNodeSelectionEvent(nodeToSelect);
                for (final ITreeNode treeNodeToRemove : nodes) {
                    removeNodeAndItsChildrenFromMap(treeNodeToRemove);
                }
            } else {
                StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_element,
                        Messages.DataTypesFormPageController_msg_can_not_delete_element, status);
            }
        } catch (final ExecutionException e) {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.DataTypesFormPageController_msg_failure_executing_delete_element, e);
            Logger.log(status);
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_element,
                    Messages.DataTypesFormPageController_msg_can_not_delete_element, status);
        }
    }

    /**
     * Renames {@link INamedObject} to the given new name. This method can be
     * used instead of the specialised <code>renameSmth</code> methods.
     * 
     * Checks if model object is part of Edited Document
     * 
     * @param namedObject
     * @param newName
     */
    public void rename(final INamedObject namedObject, final String newName) {
        if (!isPartOfEdittedDocument(namedObject)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, namedObject.getName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_rename_element, status);
            return;
        }
        if (namedObject instanceof IElement) {
            renameElement((IElement) namedObject, newName);
        } else if (namedObject instanceof IType) {
            renameType((IType) namedObject, newName);
        }
    }

    public void renameElement(final IElement element, final String newName) {
        if (!isEditAllowedWithDialog(element)) {
            return;
        }

        final IStatus status = executeCommand(new RenameElementCommand(model, element, newName));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_rename_element, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_rename_element_X, element.getName()), status);
        }
    }

    /**
     * Renames the namespace of the given {@link ISchema}
     * 
     * @param schema
     *            {@link ISchema}
     * @param newNamespace
     */
    public void renameNamespace(final ISchema schema, final String newNamespace) {
        if (schema == null || newNamespace == null) {
            throw new IllegalArgumentException(
                    "shcema for which the namespace is being set or the namespace content it self is null!"); //$NON-NLS-1$
        }
        if (schema.getNamespace() != null && schema.getNamespace().trim().equals(newNamespace.trim())) {
            // nothing to change by setting new namespace.
            return;
        }
        if (!isEditAllowed(schema)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_edition_schema_X_not_allowed, schema.getLocation()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_edit_schema, status);
            return;
        }

        final IStatus status = executeCommand(new SetNamespaceCommand(model, schema, newNamespace));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_schema_namespace, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_namespace_schema_X, schema.getLocation()),
                    status);
        }
    }

    /**
     * Renames the {@link IType}'s name to the given name
     * 
     * @param category
     * @param type
     *            {@link IType}
     * @param newName
     */
    public void renameType(final IType type, final String newName) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }

        RenameNamedComponent cmd = null;
        if (type instanceof IStructureType) {
            cmd = new RenameStructureTypeCommand(model, (IStructureType) type, newName);
        } else {
            cmd = new RenameNamedComponent(model, type, newName);
        }

        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_rename_type, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_rename_type_X, type.getName()), status);
        }
    }

    /**
     * Sets the nillable property of {@link IElement}
     * 
     * @param category
     * @param element
     *            {@link IElement}
     * @param nillable
     */
    public void setNillable(final String category, final IElement element, final boolean nillable) {
        if (element == null) {
            throw new IllegalArgumentException("element is null - set what nillable or not?"); //$NON-NLS-1$
        }
        if (!isEditAllowedWithDialog(element)) {
            return;
        }
        final IStatus status = executeCommand(new SetElementNillableCommand(model, element, nillable));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_element_nillable, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_nillable_element_X, element.getName()), status);
        }
    }

    /**
     * Sets the given cardinality to the given {@link IElement}
     * 
     * @param category
     * @param element
     *            {@link IElement}
     * @param cardinality
     */
    public void setCardinality(final String category, final IElement element, final int minOccurs, final int maxOccurs) {
        if (element == null) {
            throw new IllegalArgumentException("element is null - set cardinality to what"); //$NON-NLS-1$
        }
        if (!isEditAllowedWithDialog(element)) {
            return;
        }

        if (element.getMinOccurs() != minOccurs || element.getMaxOccurs() != maxOccurs) {
            final IStatus status = executeCommand(new SetElementOccurences(model, element, minOccurs, maxOccurs));
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_min_occurs, MessageFormat.format(
                        Messages.DataTypesFormPageController_msg_failure_set_min_occurs_element_X, element.getName()), status);
            }
        }

    }

    /**
     * Sets the {@link IType} for the local {@link IElement}
     * 
     * @param type
     *            {@link IType}
     * @param category
     * @param element
     *            {@link IElement}
     */
    public void setTypeForElement(final IType type, final String category, final IElement element) {
        if (element == null) {
            throw new IllegalArgumentException("element is null - set type to what"); //$NON-NLS-1$
        }
        if (!isEditAllowedWithDialog(element)) {
            return;
        }

        final IStatus status = executeCommand(new SetElementTypeCommand(model, element, type));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_elements_type, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypeFacet(final ISimpleType type, final String value, final int facetId) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddFacetCommand(facetId, model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteFacetCommand(facetId, model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_set_length_facet, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_length_facet_type_X, type.getName()), status);
        }
    }

    public void deleteSimpleTypeFacet(final ISimpleType type, final IFacet facet) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createDeleteFacetCommand(model, type, facet));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils
                    .showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_pattern_facet, MessageFormat.format(
                            Messages.DataTypesFormPageController_msg_failure_delete_pattern_facet_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypeLengthFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddLengthFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteLengthFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_set_length_facet, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_length_facet_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypeMinLengthFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMinLengthFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMinLengthFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils
                    .showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_min_length_facet, MessageFormat.format(
                            Messages.DataTypesFormPageController_msg_failure_set_min_length_facet_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypeMaxLengthFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMaxLengthFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMaxLengthFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_max_length_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_max_length_facet_to_type_X, type.getName()),
                    status);
        }
    }

    public void setSimpleTypeMinExclusiveFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMinExclusiveFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMinExclusiveFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_min_exclusive_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_min_exclusive_facet_type_X, type.getName()),
                    status);
        }
    }

    public void setSimpleTypeMaxExclusiveFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMaxExclusiveFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMaxExclusiveFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_max_exclusive_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_max_eclusive_facet_type_X, type.getName()),
                    status);

        }
    }

    public void setSimpleTypeMinInclusiveFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMinInclusiveFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMinInclusiveFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_min_inclusive_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_min_inclusive_facet_type_X, type.getName()),
                    status);
        }
    }

    public void setSimpleTypeMaxInclusiveFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddMaxInclusiveFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteMaxInclusiveFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_max_inclusive_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_max_inclusive_facet_type_X, type.getName()),
                    status);
        }
    }

    public void setSimpleTypeWhitespaceFacet(final ISimpleType type, final Whitespace whitespace) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        if (whitespace != null) {
            final IStatus status = executeCommand(FacetsCommandFactory.createAddWhiteSpaceFacetCommand(model, type, whitespace));
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_whitespace_facet, MessageFormat
                        .format(Messages.DataTypesFormPageController_msg_failure_add_whitespace_facet_type_X, type.getName()),
                        status);
            }
        } else {
            final IStatus status = executeCommand(FacetsCommandFactory.createDeleteWhiteSpaceFacetCommand(model, type, null));
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_whitespace_facet,
                        MessageFormat.format(Messages.DataTypesFormPageController_msg_failure_delete_whitespace_facet_type_X,
                                type.getName()), status);
            }
        }
    }

    public void setSimpleTypeTotalDigitsFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddTotalDigitsFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteTotalDigitsFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_total_digits_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_total_digits_facet_type_X, type.getName()),
                    status);
        }
    }

    public void setSimpleTypeFractionDigitsFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        AbstractNotificationOperation cmd = null;
        if (value != null) {
            cmd = FacetsCommandFactory.createAddFractionDigitsFacetCommand(model, type, value);
        } else {
            cmd = FacetsCommandFactory.createDeleteFractionDigitsFacetCommand(model, type);
        }
        final IStatus status = executeCommand(cmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_fraction_digits_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_fraction_digits_facet_type_X, type.getName()),
                    status);
        }
    }

    public void addSimpleTypePatternFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createAddPatternFacetCommand(model, type, value));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_pattern_facet, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_add_pattern_facet_type_X, type.getName()), status);
        }
    }

    public void deleteSimpleTypePatternFacet(final ISimpleType type, final IFacet facet) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createDeletePatternFacetCommand(model, type, facet));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils
                    .showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_pattern_facet, MessageFormat.format(
                            Messages.DataTypesFormPageController_msg_failure_delete_pattern_facet_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypePatternFacet(final ISimpleType type, final IFacet facet, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createSetPatternFacetCommand(model, type, facet, value));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_pattern_facet, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_pattern_facet_type_X, type.getName()), status);
        }
    }

    public void setSimpleTypeEnumFacet(final ISimpleType type, final IFacet facet, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createSetEnumerationFacetCommand(model, type, facet, value));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_enumeration_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_enumeration_facet_type_X, type.getName()),
                    status);
        }
    }

    public void addSimpleTypeEnumFacet(final ISimpleType type, final String value) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createAddEnumerationFacetCommand(model, type, value));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_add_enumeration_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_enumeration_facet_type_X, type.getName()),
                    status);
        }
    }

    public void deleteSimpleTypeEnumFacet(final ISimpleType type, final IFacet facet) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(FacetsCommandFactory.createDeleteEnumerationFacetCommand(model, type, facet));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_enumeration_facet, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_delete_enumeration_facet_type_X, type.getName()),
                    status);
        }
    }

    @Override
    public void setStructureTypeContent(final IStructureType structure, final IType type) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(new SetStructureTypeBaseTypeCompositeCommand(model, structure, type));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_2, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_type_of_structure_X, structure.getName()), status);
        }
    }

    @Override
    public void setStructureType(final IStructureType structure, final IType type) {
        if (!isEditAllowedWithDialog(type)) {
            return;
        }
        final IStatus status = executeCommand(new SetStructureTypeCommand(model, structure, type));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_structure_type, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_type_of_structure_X, structure.getName()), status);
        }
    }

    public void setGlobalElementNillable(final IStructureType structure, final boolean nillable) {
        if (!isEditAllowedWithDialog(structure)) {
            return;
        }
        final IStatus status = executeCommand(new SetGlobalElementNillableCommand(model, structure, nillable));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_element_nillable, MessageFormat
                    .format(Messages.DataTypesFormPageController_msg_failure_set_global_element_X_nillable, structure.getName()),
                    status);
        }
    }

    public void setSimpleTypeBaseType(final ISimpleType type, final ISimpleType baseType) {
        final IStatus status = executeCommand(new SetBaseTypeCommand(model, type, baseType));
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_set_base_type, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_failure_set_base_type_X, type.getName()), status);
        }
    }

    protected IStatus executeCommand(final AbstractNotificationOperation cmd) {
        try {
            final IStatus status = model.getEnv().execute(cmd);
            return status;
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not execute command " + cmd.getClass(), e); //$NON-NLS-1$
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_can_not_execute_command_X, cmd.getClass()));
        }
    }

    @Override
    protected IModelObject getModelObject() {
        return model.getModelObject();
    }

    @Override
    protected String getEditorID() {
        return DataTypesEditor.EDITOR_ID;
    }

    private boolean isRemoveItemEnabled(final IDataTypesTreeNode node) {
        final ITreeNode parentNode = node.getParent();
        return node != null
                && (parentNode == null || !parentNode.isReadOnly())
                && (node instanceof ISimpleTypeNode || node instanceof IStructureTypeNode || node instanceof IElementNode || node instanceof INamespaceNode)
                && !isResourceReadOnly() && !isModelRootSchema(node.getModelObject());
    }

    /**
     * Method called to determine if the passed treeNodes are deletable via this
     * controller. This method checks if the model is set as editable, if the
     * nodes are one of the managed by the controller types and if any of the
     * preEdit listeners deny the delete (via the super.isDeleteAllowed)
     * 
     * @param nodes
     *            the nodes for which the check is performed
     * @return true if the nodes can be deleted, false if not.
     */
    public boolean isRemoveItemsEnabled(final List<IDataTypesTreeNode> nodes) {
        if (nodes == null) {
            return false;
        }
        if (nodes.size() == 0) {
            return false;
        }
        for (final IDataTypesTreeNode node : nodes) {
            final boolean canRemove = isRemoveItemEnabled(node);
            if (!canRemove) {
                return false;
            }
        }
        return true;
    }

    public boolean isRenameItemEnabled(final IDataTypesTreeNode node) {
        return !isResourceReadOnly()
                && node != null
                && !node.isReadOnly()
                && (node instanceof INamespaceNode || node instanceof IElementNode || node instanceof ISimpleTypeNode || node instanceof IStructureTypeNode)
                && (node.getModelObject() instanceof INamedObject || node.getModelObject() instanceof INamespacedObject);
    }

    public boolean isAddElementEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode == null) {
            return false;
        }
        if (((selectedNode.getCategories() & ITreeNode.CATEGORY_REFERENCE) == ITreeNode.CATEGORY_REFERENCE)
                && !selectedNode.getParent().isReadOnly()) {
            return true;
        }
        if (selectedNode.isReadOnly() || isResourceReadOnly()) {
            return false;
        }

        final IModelObject modelObject = selectedNode.getModelObject();

        // disable element creation for namespaces and simple types - for them
        // add global element action is enabled
        if (modelObject instanceof ISchema || modelObject instanceof ISimpleType) {
            return false;
        }

        IStructureType structureType = null;
        if (modelObject instanceof IElement) {
            structureType = getGlobalStructureType(modelObject);
        } else if (modelObject instanceof IStructureType) {
            structureType = (IStructureType) modelObject;
        }

        if (structureType != null) {
            if (structureType.isComplexTypeSimpleContent()) {
                return false;
            }
            if (structureType.isElement() && structureType.getType() != null
                    && (!structureType.getType().isAnonymous() || structureType.getType() instanceof ISimpleType)) {
                return false;
            }
        }

        return true;
    }

    private IStructureType getGlobalStructureType(final IModelObject modelObject) {
        if (modelObject == null) {
            return null;
        }
        if (modelObject.getParent() instanceof IStructureType) {
            final IStructureType structureType = (IStructureType) modelObject.getParent();
            final IModelObject parentOfParent = ((AbstractType) structureType).getDirectParent();
            if (parentOfParent instanceof ISchema) {
                return (IStructureType) modelObject.getParent();
            }
            return getGlobalStructureType(parentOfParent);
        }
        return getGlobalStructureType(modelObject.getParent());
    }

    public boolean isAddSimpleTypeEnabled(final IDataTypesTreeNode selectedNode) {
        return isAddTypeEnabled(selectedNode);
    }

    public boolean isAddStructureEnabled(final IDataTypesTreeNode selectedNode) {
        return isAddTypeEnabled(selectedNode);
    }

    protected boolean isAddTypeEnabled(final IDataTypesTreeNode selectedNode) {
        IModelObject modelObject;
        if (selectedNode == null || selectedNode instanceof ImportedTypesNode) {
            if (model instanceof IXSDModelRoot) {
                modelObject = ((IXSDModelRoot) model).getSchema();
            } else {
                return false;
            }
            return modelObject != null && !isResourceReadOnly();
        }
        modelObject = selectedNode.getModelObject();
        final boolean isReference = (selectedNode.getCategories() & ITreeNode.CATEGORY_REFERENCE) == ITreeNode.CATEGORY_REFERENCE;
        return isReference || !selectedNode.isReadOnly() && !isResourceReadOnly();
    }

    public boolean isAddAttributeEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode == null) {
            return false;
        }
        if (((selectedNode.getCategories() & ITreeNode.CATEGORY_REFERENCE) == ITreeNode.CATEGORY_REFERENCE)
                && !selectedNode.getParent().isReadOnly()) {
            return true;
        }
        final IModelObject modelObject = selectedNode.getModelObject();

        if (selectedNode.isReadOnly() || isResourceReadOnly()) {
            return false;
        }

        if (modelObject instanceof IStructureType) {
            final IStructureType structure = (IStructureType) modelObject;
            // global element
            if (structure.isElement() && !(structure.getType() instanceof IStructureType && structure.getType().isAnonymous())) {
                return false;
            }
            return true;
        } else if (modelObject instanceof IElement) {
            // inner element - all allowed, add in parent element
            return true;
        }

        return false;
    }

    /**
     * Used in case the model is {@link IXSDModelRoot}. Checks if the object is
     * the model root's {@link Schema}
     * 
     * @param schema
     *            the object for which the check is performed
     * @return true if the parameter equals the controller's XSDModelRoot's
     *         schema
     */
    boolean isModelRootSchema(final Object schema) {
        return schema instanceof ISchema && model instanceof IXSDModelRoot && ((IXSDModelRoot) model).getSchema().equals(schema);
    }

    public void handleAddGlobalElementAction(final IDataTypesTreeNode selectedTreeNode) {
        final NamespaceNode nsNode = new NamespaceNode(getModelObject(), treeNodeMapper);
        handleAddElementAction(nsNode);
    }

    public boolean isAddGlobalElementEnabled(final IDataTypesTreeNode selectedTreeNode) {
        return isAddTypeEnabled(selectedTreeNode);
    }

    public boolean isCopyEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode == null) {
            return false;
        }
        final IModelObject modelObject = selectedNode.getModelObject();

        return modelObject != null && modelObject instanceof IType;
    }

    public void setElementFacet(final IElement element, final String value, final int facetId) {
        setElementFacet((INamedObject) element, value, facetId);
    }

    public void setGlobalElementFacet(final IStructureType structureInput, final String value, final int facetId) {
        if (!isEditAllowedWithDialog(structureInput)) {
            return;
        }

        setElementFacet(structureInput, value, facetId);
    }

    private void setElementFacet(final INamedObject input, final String value, final int facetId) {
        AbstractNotificationOperation addFacetCmd;
        if (XSDPackage.XSD_ENUMERATION_FACET == facetId) {

            addFacetCmd = new AddEnumFacetToElementCommand(model, input, value);

        } else {
            addFacetCmd = new AddFacetToElementCommand(model, input, facetId, value);
        }
        final IStatus status = executeCommand(addFacetCmd);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_SET_FACET_TTL, MessageFormat.format(
                    Messages.DataTypesFormPageController_CANNOT_SET_FACET_TO_ANONYMOUS_TYPE, input.getName()), status);
        }
    }

    WeakReference<IType> copiedTypeRef = null;

    public void handleCopyTypeAction(final ITreeNode selectedTreeNode) {
        final IModelObject selectionModelObject = selectedTreeNode.getModelObject();
        if (selectionModelObject == null || !(selectionModelObject instanceof IType)) {
            throw new IllegalArgumentException("Tree node selection must be IType but was:" + selectionModelObject); //$NON-NLS-1$
        }

        copiedTypeRef = new WeakReference<IType>((IType) selectionModelObject);

    }

    public void handlePasteTypeAction(final ITreeNode selectedTreeNode) {
        final IModelObject targetModelObject = selectedTreeNode.getModelObject();
        if (targetModelObject == null) {
            throw new IllegalArgumentException("Tree node selection is null."); //$NON-NLS-1$
        }

        if (!isEditAllowed(targetModelObject)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, selectedTreeNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_0, status);
            return;
        }

        final IType sourceType = copiedTypeRef == null ? null : copiedTypeRef.get();
        if (sourceType == null) {
            copiedTypeRef = null;
            return;
        }

        ISchema targetSchema = null;
        if (targetModelObject instanceof ISchema) {
            targetSchema = (ISchema) targetModelObject;
        }

        IStatus status = Status.OK_STATUS;
        IType copiedType = null;
        for (final IType type : targetSchema.getAllContainedTypes()) {
            if (type.getName().equals(sourceType.getName())) {
                status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                        Messages.DataTypesFormPageController_msg_type_with_name_X_already_exists, sourceType.getName()));
                copiedType = type;
            }
        }

        if (status.isOK()) {
            final CopyTypeCommand copyCommand = new CopyTypeCommand(targetModelObject.getModelRoot(), targetModelObject
                    .getParent(), sourceType.getComponent(), targetSchema, sourceType.getName());

            status = executeCommand(copyCommand);
            copiedType = copyCommand.getCopiedType();
        }
        StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_1, status);

        if (copiedType != null) {
            selectedTreeNode.getChildren();
            fireTreeNodeSelectionEvent(copiedType);
        }

        copiedTypeRef = null;
    }

    public boolean isPasteEnabled(final IDataTypesTreeNode selectedNode) {
        final IModelObject sourceModelObject = copiedTypeRef == null ? null : copiedTypeRef.get();

        if (selectedNode == null || sourceModelObject == null) {
            return false;
        }
        if (sourceModelObject.getComponent().eResource() == null) {
            return false;
        }
        final IModelObject targetModelObject = selectedNode.getModelObject();

        if (selectedNode.isReadOnly() || isResourceReadOnly()) {
            return false;
        }

        return targetModelObject instanceof ISchema;
    }

    public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
        if (!(treeNode instanceof IDataTypesTreeNode)) {
            throw new IllegalArgumentException("this controller works only with Data Type Tree Nodes"); //$NON-NLS-1$
        } else if (treeNode.isReadOnly()) {
            throw new IllegalArgumentException(
                    "thisTreeNode is readOnly (probably from imported schema.\n is isRenameItemEnabled() method called before this call?"); //$NON-NLS-1$
        }

        if ((treeNode.getModelObject() instanceof INamedObject)) {
            final INamedObject modelObject = (INamedObject) treeNode.getModelObject();
            if (modelObject.getName() != null && modelObject.getName().trim().equals(newName.trim())) {
                // nothing to change with editing.
                return;
            }
            rename(modelObject, newName);
        } else if (treeNode.getModelObject() instanceof INamespacedObject) {
            final INamespacedObject modelObject = (INamespacedObject) treeNode.getModelObject();
            if (modelObject.getNamespace() != null && modelObject.getNamespace().trim().equals(newName.trim())) {
                // nothing to change with editting.
                return;
            }
            if (modelObject instanceof ISchema) {
                renameNamespace((ISchema) modelObject, newName);
            }
        }
    }

    /**
     * Returns true if the model object is eligible for edit and if not -
     * informs the user for this.
     * 
     * @param element
     * @return
     */
    protected boolean isEditAllowedWithDialog(final INamedObject element) {
        if (!isEditAllowed(element)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.DataTypesFormPageController_msg_editing_element_X_is_not_allowed, element.getName()));
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_rename_element, status);
            return false;
        }
        return true;
    }

    @Override
    public boolean isConvertToAnonymousTypeWithTypeContentsEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode == null || selectedNode.isReadOnly()) {
            return false;
        }
        final IModelObject selectedModelObject = selectedNode.getModelObject();
        if (!(selectedModelObject instanceof IStructureType)) {
            return false;
        }
        final IStructureType selectedStructureType = (IStructureType) selectedModelObject;

        if (!selectedStructureType.isElement() || selectedStructureType.isAnonymous()) {
            return false;
        }

        final IType typeDefinitionOfSelectedStructureType = selectedStructureType.getType();
        if (!(typeDefinitionOfSelectedStructureType.getComponent() instanceof XSDComplexTypeDefinition)) {
            return false;
        }
        final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) typeDefinitionOfSelectedStructureType
                .getComponent();
        return complexType.getBaseType() != null && ANY_TYPE.equals(complexType.getBaseType().getName());
    }

    @Override
    public void handleConvertToAnonymousTypeWithTypeContentsAction(final ITreeNode firstElement) {
        final IStructureType structureType = (IStructureType) firstElement.getModelObject();
        final InlineStructureTypeContentsCommand inlineStructureTypeCommand = new InlineStructureTypeContentsCommand(
                (IXSDModelRoot) firstElement.getModelObject().getModelRoot(), structureType);
        final IStatus status = executeCommand(inlineStructureTypeCommand);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_inline_structure_type_dlg_title,
                    Messages.DataTypesFormPageController_inline_structure_type_dlg_message, status);
        }
        fireTreeNodeExpandEvent(structureType);
    }

    @Override
    public boolean isConvertToGlobalTypeEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode != null && selectedNode.getModelObject() instanceof IStructureType) {
            final IStructureType structureType = (IStructureType) selectedNode.getModelObject();
            return !selectedNode.isReadOnly() && structureType.isElement() && structureType.isAnonymous()
                    && !structureType.getAllElements().isEmpty();
        }
        return false;
    }

    @Override
    public boolean isConvertToAnonymousTypeEnabled(final IDataTypesTreeNode selectedNode) {
        if (selectedNode != null && selectedNode.getModelObject() instanceof IStructureType) {
            final IStructureType structureType = (IStructureType) selectedNode.getModelObject();
            return !selectedNode.isReadOnly() && structureType.isElement() && !structureType.isAnonymous();
        }
        return false;
    }

    @Override
    public void handleConvertToGlobalAction(final ITreeNode firstElement) {
        final IStructureType structureType = (IStructureType) firstElement.getModelObject();
        final MakeAnonymousTypeGlobalCommand setAnonymousCommand = new MakeAnonymousTypeGlobalCommand(
                (IXSDModelRoot) firstElement.getModelObject().getModelRoot(), structureType);
        executeSetElementAnonymousCommand(structureType.getName(), true, setAnonymousCommand,
                Messages.DataTypesFormPageController_convert_to_global_type_dlg_title);
        fireTreeNodeExpandEvent(setAnonymousCommand.getExtractedStructureType());
    }

    @Override
    public void handleConvertToAnonymousTypeAction(final ITreeNode firstElement) {
        final IStructureType structureType = (IStructureType) firstElement.getModelObject();
        final AbstractNotificationOperation setAnonymousCommand = new MakeGlobalTypeAnonymousCommand((IXSDModelRoot) firstElement
                .getModelObject().getModelRoot(), structureType, false);
        executeSetElementAnonymousCommand(structureType.getName(), true, setAnonymousCommand,
                Messages.DataTypesFormPageController_set_element_anonymous_dialog_title);
        fireTreeNodeExpandEvent(firstElement);
    }

    protected void executeSetElementAnonymousCommand(final String elementName, final boolean anonymous,
            final AbstractNotificationOperation setAnonymousCommand, final String dialogTitle) {
        final IStatus status = executeCommand(setAnonymousCommand);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(dialogTitle, MessageFormat.format(
                    Messages.DataTypesFormPageController_cannot_set_anonymous_X_for_element_Y_error_msg, anonymous, elementName),
                    status);
        }
    }

    @Override
    public boolean isMakeAnInlineNamespaceEnabled(final IDataTypesTreeNode selectedNode) {
        return (selectedNode != null && selectedNode.getModelObject() instanceof ISchema)
                && ((selectedNode.getCategories() & ITreeNode.CATEGORY_IMPORTED) == ITreeNode.CATEGORY_IMPORTED)
                && selectedNode.getModelObject().getModelRoot().getRoot() instanceof IWsdlModelRoot;
    }

    @Override
    public void handleMakeAnInlineNamespace(final ITreeNode selectedNode) {
        final ISchema schemaToInline = (ISchema) selectedNode.getModelObject();

        final InlineNamespaceCompositeCommand inlineNamespaceCompositeCommand = new InlineNamespaceCompositeCommand(
                (IWsdlModelRoot) schemaToInline.getModelRoot().getRoot(), schemaToInline);
        final IStatus status = executeCommand(inlineNamespaceCompositeCommand);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_inline_schema_failed, MessageFormat.format(
                    Messages.DataTypesFormPageController_inline_failed_error_msg, schemaToInline.getNamespace()), status);
        }
        fireTreeNodeExpandEvent(inlineNamespaceCompositeCommand.getNewInlinedSchema());
    }

    @Override
    public boolean isExtractNamespaceEnabled(final IDataTypesTreeNode selectedNode) {
        return (selectedNode != null && selectedNode.getModelObject() instanceof ISchema && !selectedNode.isReadOnly());
    }

    @Override
    public void handleExtractNamespace(final ITreeNode node) {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final ExtractNamespaceWizard wizard = new ExtractNamespaceWizard();
        final IStatus status = initWizard(node, wizard);
        if (!status.isOK()) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_initialization_error_dlg_title, status);
            return;
        }

        final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.setHelpAvailable(false);
        dialog.setPageSize(ExtractSchemaWizardConstants.EXTRACT_WIZARD_DIALOG_WIDTH,
                ExtractSchemaWizardConstants.EXTRACT_WIZARD_DIALOG_HEIGHT);
        wizard.setWizardDialog(dialog);
        openWizardDialog(dialog);
    }

    // =========================================================
    // helpers
    // =========================================================

    protected IStatus initWizard(final ITreeNode node, final ExtractNamespaceWizard wizard) {
        return wizard.init((ISchema) node.getModelObject());
    }

    protected ISchema getElementSchema(final IElement element) {
        return ((Element) element).getSchema();
    }

    protected void openWizardDialog(final WizardDialog dialog) {
        dialog.open();
    }
}
