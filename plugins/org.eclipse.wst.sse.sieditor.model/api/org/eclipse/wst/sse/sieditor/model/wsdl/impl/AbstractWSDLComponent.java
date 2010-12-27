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
package org.eclipse.wst.sse.sieditor.model.wsdl.impl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.wst.wsdl.WSDLElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.AbstractExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.write.api.IWritable;

/**
 * Generic base class for the WSDL {@link IModelObject} classes
 * 
 */
public abstract class AbstractWSDLComponent extends AbstractExtensibleObject implements IWritable {
    protected final WSDLElement component;
	
    private final IModelObject parent;
    /**
     * A wsdl component is the Description, Service Interface, Operation etc..
     * @param component the component the current is wrapping
     * @param modelRoot the wsld model root this element belongs to
     * @param parent the parent wsdl Component of the given.<br> If the wsdl Component is a Description - it's parent is null (it hasn't got any)
     */
    public AbstractWSDLComponent(final WSDLElement component, final IWsdlModelRoot modelRoot, final IModelObject parent) {
        super(modelRoot);
        this.component = component;
        this.parent = parent;
	}
    
    @Override
    public IWsdlModelRoot getModelRoot() {
		return (IWsdlModelRoot) super.getModelRoot();
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.wst.sse.sieditor.model.wsdl.impl.IWsdlComponent#getComponent()
     */
    public WSDLElement getComponent() {
        return component;
    }

    public IModelObject getParent(){
        return parent;
    }
    
    public String getDocumentation() {
        final Element domElement = component.getDocumentationElement();
	
		if(null == domElement)
			return ""; //$NON-NLS-1$
		
		final Node textNode = domElement.getFirstChild();
		if (textNode != null)
		{
			String docValue = textNode.getNodeValue();
			if (docValue != null)
			{
				return docValue;
			}
		}
		return ""; //$NON-NLS-1$
	}
	
    public Element setDocumentation(final String value) throws ExecutionException, CommandException {
        final SetDocumentationCommand command;

        command = new SetDocumentationCommand(getModelRoot(), this, component.getElement(), value);
        getModelRoot().getEnv().execute(command);

			return command.getDocumentationElement();
	}
	
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        return getComponent().equals(((AbstractWSDLComponent) obj).getComponent());
    }

    @Override
    public int hashCode() {
        return getComponent().hashCode();
    }
}
