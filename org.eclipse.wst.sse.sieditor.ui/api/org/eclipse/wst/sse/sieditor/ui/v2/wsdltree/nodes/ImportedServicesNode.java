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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ImportedServicesNode extends AbstractTreeNode {

    private SIFormPageController siFormPageController = null;

    public ImportedServicesNode(IDescription description, SIFormPageController siFormPageController) {
        super(description, null, siFormPageController.getTreeNodeMapper(), CATEGORY_STATIC_ROOT);
        this.siFormPageController = siFormPageController;
    }

    public Object[] getChildren() {

        IDescription description = (IDescription) getModelObject();
        Collection<IDescription> referencedServices = description.getReferencedServices();

        List<ImportedServiceNode> nodes = new ArrayList<ImportedServiceNode>();
        for (IDescription referredDescription : referencedServices) {
            ImportedServiceNode descriptionNode = null;
            if (getNodeMapper().getTreeNode(referredDescription) instanceof ImportedServiceNode) {
                descriptionNode = (ImportedServiceNode) getNodeMapper().getTreeNode(referredDescription);
            } else {
                descriptionNode = new ImportedServiceNode(referredDescription, this, siFormPageController);
                getNodeMapper().addToNodeMap(referredDescription, descriptionNode);
            }
            nodes.add(descriptionNode);
        }
        return nodes.toArray();
    }

    public String getDisplayName() {
        return Messages.ImportedServicesNode_0;
    }

    public Image getImage() {
        return getImageRegistry().get(Activator.NODE_IMPORTED_TYPES);
    }
}
