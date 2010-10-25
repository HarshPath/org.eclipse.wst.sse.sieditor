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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.AbstractExtensibleObject;

/**
 * 
 * 
 * 
 */
public abstract class AbstractXSDComponent extends AbstractExtensibleObject {
	
    public abstract XSDConcreteComponent getComponent();

    protected AbstractXSDComponent(final IXSDModelRoot modelRoot) {
        super(modelRoot);
    }

    @Override
    public IXSDModelRoot getModelRoot() {
		return (IXSDModelRoot) super.getModelRoot();
	}

    protected Element getFirstElement(final List<XSDAnnotation> annotations) {
        final XSDAnnotation annotation = annotations.size() > 0 ? annotations.get(0) : null;
        if (null == annotation)
            return null;
        final List<Element> userInfo = annotation.getUserInformation();
        if (null == userInfo)
            return null;
        final Element docuElement = userInfo.size() > 0 ? userInfo.get(0) : null;
        return docuElement;
    }

    protected String getDocumentation(final Element domElement) {
        if (null == domElement)
            return ""; //$NON-NLS-1$

        final Node child = domElement.getFirstChild();
        if (null == child || !(child instanceof Text))
            return ""; //$NON-NLS-1$
        return child.getNodeValue();
    }
    
    public Element setDocumentation(String description) throws ExecutionException{
    	SetDocumentationCommand cmd = new SetDocumentationCommand(getModelRoot(), this, description);
    	getModelRoot().getEnv().execute(cmd);
    	return cmd.getDocumentationElement();
    }
/*
    protected void setDocumentation(final XSDAnnotation annotation, final String value) throws ExecutionException {
        final SetDocumentationCommand command = new SetDocumentationCommand(getModelRoot(), this, annotation, value);
        getModelRoot().getEnv().execute(command);
    }
  */  
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

        return getComponent().equals(((AbstractXSDComponent)obj).getComponent());
    }

    @Override
    public int hashCode() {
        return getComponent().hashCode();
    }
}