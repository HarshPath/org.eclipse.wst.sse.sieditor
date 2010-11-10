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
package org.eclipse.wst.sse.sieditor.ui.v2.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;


public class WSDLContentProvider implements ITreeContentProvider {
    
        private final SIFormPageController nodeMapperContainer;

        public WSDLContentProvider(final SIFormPageController nodeMapperContainer) {
            this.nodeMapperContainer = nodeMapperContainer;
            // TODO Auto-generated constructor stub
        }

	public Object[] getChildren(final Object parentElement) {
		if(parentElement instanceof ITreeNode)
			return ((ITreeNode) parentElement).getChildren();
		return null;
	}

    public Object getParent(final Object element) {
        if(element instanceof ParameterNode ||  element instanceof FaultNode) {
            if (!nodeMapperContainer.isShowCategoryNodes()) {
                final ITreeNode parent = ((ITreeNode) element).getParent();
                return parent != null ? parent.getParent() : null;
            }
        }
        if(element instanceof ITreeNode)
            return ((ITreeNode) element).getParent();
        return null;
    }

    public boolean hasChildren(final Object element) {
		if(element instanceof ITreeNode)
			return ((ITreeNode) element).hasChildren();
		return false;
	}

	public Object[] getElements(final Object inputElement) {
		
		//Returns the first elements which are the Service Interfaces
		if(inputElement instanceof IWsdlModelRoot)
		{			
			return nodeMapperContainer.getAllServiceInterfaceNodes(((IWsdlModelRoot)inputElement).getDescription());
		}

		return null;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// TODO Auto-generated method stub
		
	}

	
	
}
