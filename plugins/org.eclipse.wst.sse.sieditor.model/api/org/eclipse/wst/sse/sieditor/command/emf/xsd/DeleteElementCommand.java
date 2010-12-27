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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for deleting a local ElementDeclaration or AttributeDeclaration
 * 
 * 
 */
public class DeleteElementCommand extends AbstractNotificationOperation {
    private final IElement _element;
	private final IStructureType _type;

    public DeleteElementCommand(final IModelRoot root, final IStructureType type, final IElement element) {
        super(root, type, Messages.DeleteElementCommand_delete_element_command_label);
		this._type = type;
        this._element = element;
    }

    public org.eclipse.core.runtime.IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor,
            org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
    	
    	XSDConcreteComponent component = _element.getComponent();
    	XSDModelGroup container = ((Element) _element).getContainer();
    	
    	if (component instanceof XSDParticle) {
    		container.getContents().remove(component);
    	} else {
            if (_element.getParent() instanceof StructureType) {
                XSDNamedComponent parentComponent = (XSDNamedComponent) _element.getParent().getComponent();
                if (parentComponent instanceof XSDElementDeclaration && component instanceof XSDAttributeDeclaration) {
                	parentComponent = ((XSDElementDeclaration) parentComponent).getType();
                }
                if (parentComponent instanceof XSDComplexTypeDefinition) {
                    for (XSDAttributeGroupContent attrContent : new ArrayList<XSDAttributeGroupContent>(
                            ((XSDComplexTypeDefinition) parentComponent).getAttributeContents())) {
                        if ((attrContent instanceof XSDAttributeUse)
                                && component.equals(((XSDAttributeUse) attrContent).getContent())) {
                            ((XSDComplexTypeDefinition) parentComponent).getAttributeContents().remove(attrContent);
                        }
                    }
                }
            }
    	}
    	
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        if (_element == null || _type == null) {
        	return false;
        }
        
        return _type.getAllElements().contains(_element);
    }

}
