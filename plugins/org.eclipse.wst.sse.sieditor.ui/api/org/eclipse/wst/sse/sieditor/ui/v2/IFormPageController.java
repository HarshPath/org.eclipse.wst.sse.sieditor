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

import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServiceNode;

public interface IFormPageController {

    public void editDocumentation(ITreeNode treeNode, String text);

    public boolean isResourceReadOnly();

    /**
     * opens the type dialog, in the default implementation with the most
     * reduced list of items only simple types
     * 
     * @deprecated use this{@link #openTypesDialog(String, IModelObject)}
     *             instead
     * @return the selectedd type
     */
    @Deprecated
    public IType openTypesDialog();

    public IType openTypesDialog(String displayText, IModelObject selectedModelObject, boolean showComplexTypes);

    public void editItemNameTriggered(ITreeNode treeNode, String newName);

    /**
     * Checks if the given model object is representing an element belonging to
     * the opened for editing document. (True if the model object does not
     * belong to an imported document)
     * 
     * @param modelObject
     *            the object for which the check is performed
     * @return true if the object belongs to this document, false otherwise <br>
     * <br>
     * <br>
     *         don't mind the double 't'
     */
    public boolean isPartOfEdittedDocument(IModelObject object);

    /**
     * @see isPartOfEdittedDocument(IModelObject object)
     * @param items
     * @return true if all of the items is part of an edited document, false
     *         otherwise
     */
    public boolean areAllItemsPartOfEditedDocument(List<? extends ITreeNode> items);

    /**
     * Opens a new editor in case that 'node' is or is child of
     * (ImportedServiceNode || ImportedSchemaNode)
     * 
     * @param node
     *            is or is child of ImportedServiceNode
     * @see ImportedServiceNode
     * @see ImportedSchemaNode
     */
    public void openInNewEditor(ITreeNode node);

    public boolean isOpenInNewEditorEnabled(ITreeNode iTreeNode);

    public void openInNewEditor(IType type);

    public String[] getCommonTypesDropDownList();

    public void newStructureType(String name, ISchema schema, TypePropertyEditor propertyEditor);

    public void newSimpleType(String name, ISchema schema, TypePropertyEditor propertyEditor);

    public void newElementType(String name, ISchema schema, TypePropertyEditor propertyEditor);

}
