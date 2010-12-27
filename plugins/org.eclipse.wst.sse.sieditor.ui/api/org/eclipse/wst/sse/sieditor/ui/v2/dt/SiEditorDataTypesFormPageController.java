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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.ICondition;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class SiEditorDataTypesFormPageController extends DataTypesFormPageController implements
        ISiEditorDataTypesFormPageController {

    public SiEditorDataTypesFormPageController(final IWsdlModelRoot model, final boolean readOnly) {
        super(model, readOnly);
    }

    private static final String NEW_NAMESPACE_NAME = "http://namespace"; //$NON-NLS-1$

    protected String getNewNamespaceName() {
        if (!(model instanceof IWsdlModelRoot)) {
            throw new IllegalStateException("this method should not be called if editor is the standalone XSD editor"); //$NON-NLS-1$
        }
        final IWsdlModelRoot root = (IWsdlModelRoot) model;
        return NameGenerator.generateName(NEW_NAMESPACE_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return root.getDescription().getSchema(in).length == 0;
            }
        });
    }

    /**
     * called in order to execute a new namespace addition
     */
    public void handleAddNewNamespaceAction() {
        final ISchema newSchema = addNewNamespace(null);
        if (newSchema != null) {
            fireTreeNodeSelectionEvent(newSchema);
            fireTreeNodeEditEvent(newSchema);
        }
    }

    public ISchema addNewNamespace(String newName) {
        if (!isEditAllowed(null)) {
            return null;
        }
        if (!(model instanceof IWsdlModelRoot)) {
            throw new IllegalStateException("this method should not be called if editor is the standalone XSD editor"); //$NON-NLS-1$
        }
        final IWsdlModelRoot root = (IWsdlModelRoot) model;
        newName = newName == null ? getNewNamespaceName() : newName;
        final AddNewSchemaCommand command = new AddNewSchemaCommand(root, newName);
        try {
            final IStatus status = root.getEnv().execute(command);
            if (StatusUtils.canContinue(status)) {
                return command.getNewSchema();
            }
            StatusUtils.showStatusDialog(Messages.SiEditorDataTypesFormPageController_dlg_title_add_namespace, MessageFormat
                    .format(Messages.SiEditorDataTypesFormPageController_msg_failure_add_xsd_schema_to_wsdl_X, root
                            .getDescription().getLocation()), status);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Addition of a new schema failed", e); //$NON-NLS-1$
            fireShowErrorMsgEvent(Messages.SiEditorDataTypesFormPageController_error_msg_addition_of_new_schema_failed);
        }
        return null;
    }

    /**
     * Called in order to determine if the action adding namespace (another
     * <schema> to the wsdl's types is enabled
     * 
     * @param selectedNode
     * 
     * @return
     */
    public boolean isAddNamespaceEnabled(final ITreeNode selectedNode) {
        List<ITreeNode> selectedNodeList = new ArrayList<ITreeNode>();
        if(selectedNode != null) {
        	selectedNodeList.add(selectedNode);
        }
        
		if (!areAllItemsPartOfEditedDocument(selectedNodeList)) {
            return false;
        }
        final IModelObject description = getModelObject();
        return description instanceof IDescription && !isResourceReadOnly();
    }

    @Override
    public void handleAddGlobalElementAction(IDataTypesTreeNode selectedTreeNode) {
        while (!(selectedTreeNode.getModelObject() instanceof ISchema) && (selectedTreeNode.getParent() != null)) {
            selectedTreeNode = (IDataTypesTreeNode) selectedTreeNode.getParent();
        }

        if (selectedTreeNode.getModelObject() instanceof ISchema) {
            final NamespaceNode nsNode = new NamespaceNode(selectedTreeNode.getModelObject(), treeNodeMapper);
            handleAddElementAction(nsNode);
        }
    }
}
