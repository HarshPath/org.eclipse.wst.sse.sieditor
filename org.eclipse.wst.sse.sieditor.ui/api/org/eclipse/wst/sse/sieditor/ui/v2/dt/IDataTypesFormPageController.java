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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public interface IDataTypesFormPageController extends IFormPageController {

    TreeNodeMapper getTreeNodeMapper();

    boolean isResourceReadOnly();

    /**
     * After a delete action, the node related to that element and its child
     * nodes need to be removed This methods removes such nodes from the tree
     * node mapper, which contains all nodes present in the tree.
     * 
     * @param treeNode
     */
    public void removeNodeAndItsChildrenFromMap(final ITreeNode treeNode);

    /**
     * Method adding a new element in the specified model {@link IModelObject}
     * with the given 'newName'<br>
     * If the 'newName' is null a default new name will be generated for the new
     * element.
     * 
     * @param selectionModelObject
     *            the parent object
     * @param newName
     *            the new name or null for a default generated one
     */
    public IModelObject addNewElement(IModelObject selectionModelObject, String newName);

    /**
     * Method adding a new simpleType in the specified {@link ISchema} with the
     * given 'newName'<br>
     * If 'newName' is null a default new name will be generated for the new
     * element.
     * 
     * @param schema
     *            the parent schema
     * @param newName
     *            the new name or null for a default generated one
     */
    public IType addNewSimpleType(ISchema schema, String newName);

    /**
     * Method adding a new anonymous {@link IStructureType} and an string base
     * type element in it <br>
     * inside the specified {@link ISchema} with the given 'newName'<br>
     * If 'newName' is null a default new name will be generated for the new
     * element.
     * 
     * @param schema
     *            the parent schema
     * @param newName
     *            the new name or null for a default generated one
     */
    IType addNewStructureType(ISchema schema, String name);

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
    public void handleAddElementAction(ITreeNode selectedTreeNode);

    public void handleAddSimpleTypeAction(ITreeNode selectedNode);

    public void handleAddStructureTypeAction(ITreeNode selectedElement);

    public void handleAddAttributeAction(ITreeNode selectedElement);

    /**
     * Method called in order to remove the model objects represented by the
     * tree nodes
     * 
     * @param items
     *            to be removed
     */
    public void handleRemoveAction(List<ITreeNode> items);

    /**
     * Renames {@link INamedObject} to the given new name. This method can be
     * used instead of the specialised <code>renameSmth</code> methods.
     * 
     * @param namedObject
     * @param newName
     */
    public void rename(INamedObject namedObject, String newName);

    /**
     * Renames the {@link IElement}'s name to the given name
     * 
     * @param category
     * @param element
     *            {@link IElement}
     * @param newName
     */
    public void renameElement(IElement element, String newName);

    /**
     * Renames the namespace of the given {@link ISchema}
     * 
     * @param schema
     *            {@link ISchema}
     * @param newNamespace
     */
    public void renameNamespace(ISchema schema, String newNamespace);

    /**
     * Renames the {@link IType}'s name to the given name
     * 
     * @param category
     * @param type
     *            {@link IType}
     * @param newName
     */
    public void renameType(IType type, String newName);

    /**
     * Sets the nillable property of {@link IElement}
     * 
     * @param category
     * @param element
     *            {@link IElement}
     * @param nillable
     */
    public void setNillable(String category, IElement element, boolean nillable);

    /**
     * Sets the given cardinality to the given {@link IElement}
     * 
     * @param category
     * @param element
     *            {@link IElement}
     * @param cardinality
     */
    public void setCardinality(String category, IElement element, int minOccurs, int maxOccurs);

    /**
     * Sets the {@link IType} for the local {@link IElement}
     * 
     * @param type
     *            {@link IType}
     * @param category
     * @param element
     *            {@link IElement}
     */
    public void setTypeForElement(IType type, String category, IElement element);

    public void setSimpleTypeLengthFacet(ISimpleType type, String value);

    public void setSimpleTypeMinLengthFacet(ISimpleType type, String value);

    public void setSimpleTypeMaxLengthFacet(ISimpleType type, String value);

    public void setSimpleTypeMinExclusiveFacet(ISimpleType type, String value);

    public void setSimpleTypeMaxExclusiveFacet(ISimpleType type, String value);

    public void setSimpleTypeMinInclusiveFacet(ISimpleType type, String value);

    public void setSimpleTypeMaxInclusiveFacet(ISimpleType type, String value);

    public void setSimpleTypeWhitespaceFacet(ISimpleType type, Whitespace whitespace);

    public void setSimpleTypeTotalDigitsFacet(ISimpleType type, String value);

    public void setSimpleTypeFractionDigitsFacet(ISimpleType type, String value);

    public void addSimpleTypePatternFacet(ISimpleType type, String value);

    public void deleteSimpleTypePatternFacet(ISimpleType type, IFacet facet);

    public void setSimpleTypePatternFacet(ISimpleType type, IFacet facet, String value);

    public void setSimpleTypeEnumFacet(ISimpleType type, IFacet facet, String value);

    public void addSimpleTypeEnumFacet(ISimpleType type, String value);

    public void deleteSimpleTypeEnumFacet(ISimpleType type, IFacet facet);

    public void setStructureType(IStructureType structure, IType type);

    public void setGlobalElementNillable(IStructureType structure, boolean nillable);

    public void setSimpleTypeBaseType(ISimpleType type, ISimpleType baseType);

    public void setSimpleTypeFacet(ISimpleType type, String value, int facetId);

    public void deleteSimpleTypeFacet(ISimpleType type, IFacet facet);

    public void setElementFacet(final IElement element, final String value, int facetId);

    public void setGlobalElementFacet(IStructureType structureInput, String value, int facetId);

    /**
     * Method called to determine if the passed treeNode is deletable via this
     * controller. This method checks if the model is set as editable, if the
     * node is one of the managed by the controller types and if any of the
     * preEdit listeners deny the delete (via the super.isDeleteAllowed)
     * 
     * @param node
     *            the node for which the check is performed
     * @return true if the node could be deleted, false if not.
     */
    public boolean isRemoveItemsEnabled(List<IDataTypesTreeNode> nodes);

    public boolean isRenameItemEnabled(IDataTypesTreeNode node);

    public boolean isAddElementEnabled(IDataTypesTreeNode selectedNode);

    public boolean isAddSimpleTypeEnabled(IDataTypesTreeNode selectedNode);

    public boolean isAddStructureEnabled(IDataTypesTreeNode selectedNode);

    public boolean isAddAttributeEnabled(IDataTypesTreeNode selectedNode);

    public void handleAddGlobalElementAction(IDataTypesTreeNode selectedTreeNode);

    public boolean isAddGlobalElementEnabled(IDataTypesTreeNode selectedTreeNode);

    public boolean isCopyEnabled(IDataTypesTreeNode treeNode);

    public void handleCopyTypeAction(ITreeNode firstElement);

    public void handlePasteTypeAction(ITreeNode firstElement);

    public boolean isPasteEnabled(IDataTypesTreeNode treeNode);

    public boolean isConvertToGlobalTypeEnabled(IDataTypesTreeNode selectedNode);

    public void handleConvertToGlobalAction(ITreeNode firstElement);

    public boolean isConvertToAnonymousTypeEnabled(IDataTypesTreeNode selectedNode);

    public void handleConvertToAnonymousTypeAction(ITreeNode firstElement);

    public void handleConvertToAnonymousTypeWithTypeContentsAction(ITreeNode firstElement);

    public boolean isConvertToAnonymousTypeWithTypeContentsEnabled(IDataTypesTreeNode firstElement);

    public void handleExtractNamespace(ITreeNode firstElement);

    public boolean isExtractNamespaceEnabled(IDataTypesTreeNode firstElement);

}
