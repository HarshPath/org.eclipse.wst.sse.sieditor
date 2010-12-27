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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedXsdTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class DataTypesContentProvider implements ITreeContentProvider {

    private final IDataTypesFormPageController controller;

    public DataTypesContentProvider(final IDataTypesFormPageController controller) {
        this.controller = controller;
    }

    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof IDataTypesTreeNode) {
            return ((IDataTypesTreeNode) parentElement).getChildren();
        }
        return UIConstants.EMPTY_ARRAY;
    }

    public Object getParent(final Object element) {
        if (element instanceof IDataTypesTreeNode) {
            return ((IDataTypesTreeNode) element).getParent();
        }
        return null;
    }

    public boolean hasChildren(final Object element) {
        if (element instanceof IDataTypesTreeNode) {
            return ((IDataTypesTreeNode) element).hasChildren();
        }
        return false;
    }

    public Object[] getElements(final Object inputElement) {

        final List<Object> nodes = new ArrayList<Object>();
        final TreeNodeMapper treeNodeMapper = getController().getTreeNodeMapper();

        if (inputElement instanceof IWsdlModelRoot) {
            final IWsdlModelRoot modelRoot = (IWsdlModelRoot) inputElement;
            final IDescription description = modelRoot.getDescription();
            final Collection<ISchema> schemas = description.getContainedSchemas();

            if (schemas != null) {
                for (final ISchema iSchema : schemas) {
                    final List<ITreeNode> treeNodes = treeNodeMapper.getTreeNode(iSchema, ITreeNode.CATEGORY_MAIN, null);
                    ITreeNode namespaceNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
                    if (null == namespaceNode &&
                    // do not show namespace nodes for WS-I imports.
                            EmfWsdlUtils.getWsiImports(iSchema.getComponent()).isEmpty()) {
                        namespaceNode = new NamespaceNode(iSchema, treeNodeMapper);
                        treeNodeMapper.addToNodeMap(iSchema, namespaceNode);
                    }
                    if (namespaceNode != null) {
                        nodes.add(namespaceNode);
                    }
                }
            }
            final List<ITreeNode> treeNodes = treeNodeMapper.getTreeNode(description, ITreeNode.CATEGORY_STATIC_ROOT, null);
            ITreeNode importedTypesNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
            if (importedTypesNode == null) {
                importedTypesNode = new ImportedTypesNode(description, treeNodeMapper);
                treeNodeMapper.addToNodeMap(description, importedTypesNode);
            }
            if (importedTypesNode.hasChildren()) {
                nodes.add(importedTypesNode);
            }
        } else if (inputElement instanceof IXSDModelRoot) {
            final IXSDModelRoot modelRoot = (IXSDModelRoot) inputElement;
            final ISchema schema = modelRoot.getSchema();

            List<ITreeNode> treeNodes = treeNodeMapper.getTreeNode(schema, ITreeNode.CATEGORY_MAIN, null);
            ITreeNode namespaceNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
            if (namespaceNode == null) {
                // do not add the namespace node itself but its children
                // directly
                namespaceNode = new NamespaceNode(schema, treeNodeMapper);
                treeNodeMapper.addToNodeMap(schema, namespaceNode);
            }
            nodes.addAll(Arrays.asList(namespaceNode.getChildren()));

            treeNodes = treeNodeMapper.getTreeNode(schema, ITreeNode.CATEGORY_IMPORTED, null);
            final ITreeNode treeNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
            ImportedXsdTypesNode importedTypesNode;
            if (treeNode instanceof ImportedXsdTypesNode) {
                importedTypesNode = (ImportedXsdTypesNode) treeNode;
            } else {
                importedTypesNode = new ImportedXsdTypesNode(schema, treeNodeMapper);
            }
            if (importedTypesNode.hasChildren()) {
                nodes.add(importedTypesNode);
                treeNodeMapper.addToNodeMap(schema, importedTypesNode);
            }
        }

        return nodes.toArray();
    }

    public void dispose() {
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

    public IDataTypesFormPageController getController() {
        return controller;
    }
}
