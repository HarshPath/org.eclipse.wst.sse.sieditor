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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;


import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ImportedSchemaNode extends NamespaceNode implements IImportedSchemaNode {

    private final boolean tnsDiffersImportNS = false;
    
    public ImportedSchemaNode(final IModelObject schema, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
        super(schema, parent, nodeMapper);
    }
        
    @Override
    public Object[] getChildren() {
        if(tnsDiffersImportNS) {
            return new Object[0];
        }
        return super.getChildren();
    }
}
