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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;

public class NamespaceNode extends AbstractTreeNode implements INamespaceNode {

    public static final String NO_NS_STRING = Messages.NamespaceNode_no_target_namespace_value;
    
    public NamespaceNode(final IModelObject schema, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
        super(schema, parent, nodeMapper);
    }
    
    public NamespaceNode(final IModelObject schema, final TreeNodeMapper nodeMapper) {
        this(schema, null, nodeMapper);
    }

    
    public String getDisplayName() {
        return getNamespaceDisplayText((ISchema) getModelObject());
    }

    public Image getImage() {
        return isReadOnly() ? getImageRegistry().get(Activator.NODE_NAMESPACE_GRAY) :
                        getImageRegistry().get(Activator.NODE_NAMESPACE);
    }

    public Object[] getChildren() {
        final Collection<IType> types = ((ISchema) getModelObject()).getAllContainedTypes();
        if (!types.isEmpty()) {
            final Collection<ITreeNode> typeNodes = new ArrayList<ITreeNode>();
            for (final IType type : types) {
                typeNodes.add(getTypeNode(type));
            }
            return typeNodes.toArray();
        }
        return UIConstants.EMPTY_ARRAY;
    }

    protected IDataTypesTreeNode createStructureTypeNode(final IType type, final TreeNodeMapper nodeMapper, final ITreeNode parentNode) {
        return new StructureTypeNode(type, parentNode, nodeMapper);
    }

    protected IDataTypesTreeNode createSimpleTypeNode(final IType type, final ITreeNode parentNode) {
        return new SimpleTypeNode(type, parentNode);
    }

    private ITreeNode getTypeNode(final IType type) {
        final TreeNodeMapper nodeMapper = getNodeMapper();
        
        final List<ITreeNode> treeNodes = getNodeMapper().getTreeNode(type, getCategories(), this);
        ITreeNode treeNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
        if (treeNode == null) {
            if (type instanceof ISimpleType) {
                treeNode = createSimpleTypeNode(type, this);
            } else {
                treeNode = createStructureTypeNode(type, nodeMapper, this);
            }
            nodeMapper.addToNodeMap(type, treeNode);
        }
        return treeNode;
    }
    
	public static String getNamespaceDisplayText(final String namespace) {
	    if (namespace == null || UIConstants.EMPTY_STRING.equals(namespace.trim())) {
        	return NO_NS_STRING;
            }
		return namespace;
	}

    public static String getNamespaceDisplayText(final ISchema importedSchema) {
        final ISchema importingSchema = (ISchema) importedSchema.getModelRoot().getModelObject();
        final XSDSchema importingXsdSchema = importingSchema.getComponent();
        final XSDSchema importedXsdSchema = importedSchema.getComponent();
        String importDisplayNamespace = importedSchema.getNamespace();

        // in case that importedSchema is from <import>
        // do get NS from <import>, but not from TNS of importedSchema
        if(!importingXsdSchema.equals(importedXsdSchema)) {
            final Collection<XSDImport> imports = EmfXsdUtils.filterComponents(importingXsdSchema.getContents(), XSDImport.class);
            for (final XSDImport xsdImport : imports) {
                if (importedXsdSchema.equals(xsdImport.getResolvedSchema())) {
                    final String importNS = xsdImport.getNamespace();
                    importDisplayNamespace = importNS;
                    break;
                }
            }
        }
        return getNamespaceDisplayText(importDisplayNamespace);

    }
    
}
