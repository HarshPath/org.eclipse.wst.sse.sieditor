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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class WSDLFactory extends AbstractModelObjectFactory {
	
	private static final WSDLFactory instance = new WSDLFactory();
	
	/**
	 * Convenience method. Class can not be Singleton because is used via extension point.
	 * @return already created single instance.
	 */
	public static final WSDLFactory getInstance() {
		return instance;
	}
	
    public IWsdlModelRoot createModelObject(Object rootObjectList, boolean isReadOnly) {
    	IWsdlModelRoot wsdlModelRoot = null;
        if (rootObjectList instanceof List) {
                Object rootObject = ((List) rootObjectList).get(0);
                if (rootObject instanceof Definition) {
                        wsdlModelRoot = getWsdlModelRoot((Definition) rootObject, null);
                }
        }
        return wsdlModelRoot;
    }
    
    /**
     * Note that returned object is pooled, so next call of the method with the same definition will return 
     * the previously created WSDLModelRoot instance.
     * @param definition
     * @return
     */
    public IWsdlModelRoot createWSDLModelRoot(Definition definition) {
    	return createWSDLModelRoot(definition, null);
    }
    
    /**
     * Attach specific adaptable to created WSDLModelRoot. This method will return different instance of WSDLModelRoot than
     * createWSDLModelRoot(Definition definition). Returned object is pooled, so next call of the method with the same definition and
     * adaptable will return the previously created WSDLModelRoot instance.
     * @param definition
     * @param adaptable adaptable which will be used in getAdapter() method of WSDLModelRoot
     * @return
     */
    public IWsdlModelRoot createWSDLModelRoot(Definition definition, IAdaptable adaptable) {
    	return getWsdlModelRoot(definition, adaptable);
    }
}
