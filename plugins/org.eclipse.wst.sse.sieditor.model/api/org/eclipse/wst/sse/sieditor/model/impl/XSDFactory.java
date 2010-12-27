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
package org.eclipse.wst.sse.sieditor.model.impl;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

public class XSDFactory extends AbstractModelObjectFactory {
	
	private static final XSDFactory instance = new XSDFactory();
	
	/**
	 * Convenience method. Class can not be Singleton because is used via extension point.
	 * @return already created single instance.
	 */
	public static final XSDFactory getInstance() {
		return instance;
	}
	
    public IXSDModelRoot createModelObject(Object rootObjectList, boolean isReadOnly) {
    	IXSDModelRoot xsdModelRoot = null;
        if (rootObjectList instanceof List) {
                Object rootObject = ((List) rootObjectList).get(0);
                if (rootObject instanceof XSDSchema) {
                        xsdModelRoot = getXSDModelRoot((XSDSchema) rootObject, null);
                }
        }
        return xsdModelRoot;
    }
    
    /**
     * Returned object is pooled, so next call of the method with the same definition and
     * adaptable will return the previously created XSDModelRoot instance.
     * @param xsdSchema
     * @return
     */
    public IXSDModelRoot createXSDModelRoot(XSDSchema xsdSchema) {
    	return createXSDModelRoot(xsdSchema, null);
    }
    
    /**
     * Attach specific adaptable to created XSDModelRoot. This method will return different instance of XSDModelRoot than
     * createXSDModelRoot(XSDSchema xsdSchema). Returned object is pooled, so next call of the method with the same definition and
     * adaptable will return the previously created XSDModelRoot instance.
     * @param xsdSchema
     * @param adaptable adaptable which will be used in getAdapter() method of XSDModelRoot
     * @return
     */
    public IXSDModelRoot createXSDModelRoot(XSDSchema xsdSchema, IAdaptable adaptable) {
    	return getXSDModelRoot(xsdSchema, adaptable);
    }
        
        
}
