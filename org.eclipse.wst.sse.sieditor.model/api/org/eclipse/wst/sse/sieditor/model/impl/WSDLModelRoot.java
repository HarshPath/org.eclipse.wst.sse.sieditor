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
package org.eclipse.wst.sse.sieditor.model.impl;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

public class WSDLModelRoot extends AbstractModelRoot implements IWsdlModelRoot {
    
    private IDescription description;
    private final Definition definition;
    private IAdaptable adaptable;

    /**
     * To create an instance use:
     * org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory
     * .getInstance().createWSDLModelRoot(Definition);
     */
    WSDLModelRoot(final Definition definition) {
        this(definition, null);
    }

    /**
     * To create an instance use:
     * org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory
     * .getInstance().createWSDLModelRoot(Definition);
     */
    WSDLModelRoot(final Definition definition, final IAdaptable adaptable) {
        super(definition.eResource());
        Nil.checkNil(definition, "definition"); //$NON-NLS-1$
        this.definition = definition;
        this.adaptable = adaptable;

        if (adaptable == null) {
            this.adaptable = new IAdaptable() {
                public Object getAdapter(final Class adapter) {
                    if (adapter.equals(IWsdlModelRoot.class)) {
                        return WSDLModelRoot.this;
                    }
                    return null;
                }
            };
        }
    }

    public IDescription getDescription() {
        if (null == description) {
            synchronized (this) {
                if (null != description) {
                    return description;
                }

                try {
                    description = new Description(this, definition, URIHelper.createEncodedURI(definition.eResource().getURI()));
                } catch (final UnsupportedEncodingException e) {
                    throw new IllegalStateException(
                            "Cannot create WSDL model due to issues with locating the document. See nested exception for details", e); //$NON-NLS-1$
                } catch (final URISyntaxException e) {
                    throw new IllegalStateException(
                            "Cannot create WSDL model due to issues with locating the document. See nested exception for details", e); //$NON-NLS-1$
                }
            }
        }
        return description;
    }
    
    public IDescription getModelObject() {
    	return getDescription();
    }

    public Object getAdapter(final Class adapter) {
        return adaptable.getAdapter(adapter);
    }
    
}
