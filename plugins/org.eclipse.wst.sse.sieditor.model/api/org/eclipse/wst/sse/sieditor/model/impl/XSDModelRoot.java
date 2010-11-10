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
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class XSDModelRoot extends AbstractModelRoot implements IXSDModelRoot {
    private ISchema schema = null;
    private IAdaptable adaptable = null;

    /**
     * To create an instance use:
     * org.eclipse.wst.sse.sieditor.model.impl.XSDFactory
     * .getInstance().createXSDModelRoot(XSDSchema);
     * 
     * @param xsdSchema
     */
    XSDModelRoot(final XSDSchema xsdSchema) {
        this(xsdSchema, null);
    }

    /**
     * To create an instance use:
     * org.eclipse.wst.sse.sieditor.model.impl.XSDFactory
     * .getInstance().createXSDModelRoot(XSDSchema);
     * 
     * @param xsdSchema
     */
    XSDModelRoot(final XSDSchema xsdSchema, IAdaptable adaptable) {
        super(xsdSchema.eResource());
        Nil.checkNil(xsdSchema, "xsdSchema"); //$NON-NLS-1$
        URI uri;
        try {
            uri = URIHelper.createEncodedURI(xsdSchema.getSchemaLocation());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Cannot create XSD model due to issues with locating the document. See nested exception for details", e); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(
                    "Cannot create XSD model due to issues with locating the document. See nested exception for details", e); //$NON-NLS-1$
        }
        
        if(adaptable != null) {
        	schema = (ISchema)adaptable.getAdapter(ISchema.class);
		}
        
        if(schema == null) {
        	schema = new Schema(xsdSchema, this, null, uri);
        }

        if (adaptable == null) {
            this.adaptable = new IAdaptable() {
                public Object getAdapter(Class adapter) {
                    if (adapter.equals(IXSDModelRoot.class)) {
                        return XSDModelRoot.this;
                    }
                    return null;
                }
                
            };
        } else {
            this.adaptable = adaptable;
        }
    }

    public Object getAdapter(Class adapter) {
        return adaptable.getAdapter(adapter);
    }

    public ISchema getSchema() {
        return schema;
    }
    
    public ISchema getModelObject() {
    	return getSchema();
    }
    
    @Override
    public void notifyListeners(IModelChangeEvent event) {
        super.notifyListeners(event);
        IModelObject parent = schema.getParent();
        if (parent != null) {
            IModelRoot modelRoot = parent.getModelRoot();
            if (modelRoot != this) {
                modelRoot.notifyListeners(event);
            }
        }
    }
       
}
