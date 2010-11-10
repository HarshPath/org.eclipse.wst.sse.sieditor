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


import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ImportedSchemaNode extends NamespaceNode implements IImportedSchemaNode {

    private boolean tnsDiffersImportNS = false;
    
    public ImportedSchemaNode(IModelObject schema, ITreeNode parent, TreeNodeMapper nodeMapper) {
        super(schema, parent, nodeMapper);
    }
        
    @Override
    public String getDisplayName() {

        String importedSchemalocation = ((ISchema) getModelObject()).getLocation();
        importedSchemalocation = importedSchemalocation == null ? "" : importedSchemalocation; //$NON-NLS-1$
        
        StringBuilder displayName = new StringBuilder(getImportNamespace());
        if (displayName.length() > 0) {
            displayName.append(UIConstants.SPACE);
        }
        displayName.append(UIConstants.OPEN_BRACKET);
        displayName.append(importedSchemalocation);
        displayName.append(UIConstants.CLOSE_BRACKET);
        return displayName.toString();
    }
    
    private String getImportNamespace() {
        ISchema importedSchema = (ISchema) getModelObject();
        String namespaceDisplayText = super.getNamespaceDisplayText(importedSchema);
        tnsDiffersImportNS = !namespaceDisplayText.equals(importedSchema.getNamespace());
        
        return namespaceDisplayText;
    }

    @Override
    public Object[] getChildren() {
        if(tnsDiffersImportNS) {
            return new Object[0];
        }
        return super.getChildren();
    }
}
