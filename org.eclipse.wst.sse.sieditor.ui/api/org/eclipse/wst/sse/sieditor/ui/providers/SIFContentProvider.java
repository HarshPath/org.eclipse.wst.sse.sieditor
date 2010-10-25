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
package org.eclipse.wst.sse.sieditor.ui.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SIFContentProvider implements ITreeContentProvider{

	
	

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof IWorkspace) {
        // check if closed projects should be shown
        IProject[] allProjects = ((IWorkspace) parentElement).getRoot()
                .getProjects();
        ArrayList accessibleProjects = new ArrayList();
        for (int i = 0; i < allProjects.length; i++) {
            if (allProjects[i].isOpen()) {
            	//Adding all open projects in that root to Accessible projects
                accessibleProjects.add(allProjects[i]);
            }
        }
        return accessibleProjects.toArray();
    } else if (parentElement instanceof IContainer) {
    	//List all the sub-folders of the given folder
        IContainer container = (IContainer) parentElement;
        if (container.isAccessible()) {
            try {
                List children = new ArrayList();
                IResource[] members = container.members();
                for (int i = 0; i < members.length; i++) {
                    if (members[i].getType() != IResource.FILE) {
                        children.add(members[i]);
                    }
                }
                return children.toArray();
            } catch (CoreException e) {
            	// $JL-EXC$
                // this should never happen because we call #isAccessible before invoking #members
            	throw new RuntimeException(e);
            }
        }
    }
    return new Object[0];

	
	}


	public Object getParent(Object element) {
		 if (element instanceof IResource) {
				return ((IResource) element).getParent();
			}
	        return null;
	}


	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}


	public Object[] getElements(Object inputElement) { 
		return getChildren(inputElement);
		}
	

	public void dispose() {
		
	}


	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

}
