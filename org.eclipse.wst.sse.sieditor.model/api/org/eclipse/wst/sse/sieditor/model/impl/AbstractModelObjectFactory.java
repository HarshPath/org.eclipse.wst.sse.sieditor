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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.core.editorfwk.ExtensibleObjectFactory;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

abstract class AbstractModelObjectFactory implements ExtensibleObjectFactory {
	
	private final static HashMap<ModelRootObjectKey, WeakReference<IXSDModelRoot>> xsdModelRootPool = new HashMap<ModelRootObjectKey, WeakReference<IXSDModelRoot>>();
	
	private final static HashMap<ModelRootObjectKey, WeakReference<IWsdlModelRoot>> wsdlModelRootPool = new HashMap<ModelRootObjectKey, WeakReference<IWsdlModelRoot>>();
	
	IXSDModelRoot getXSDModelRoot(XSDSchema xsdSchema, IAdaptable adaptable) {
		if(adaptable != null) {
        	ISchema schema = (ISchema)adaptable.getAdapter(ISchema.class);
        	xsdSchema = schema == null ? xsdSchema : schema.getComponent();
		}
		
		clearEmptyKeys(xsdModelRootPool.keySet());
        Resource schemaResource = xsdSchema.eResource();
        final ModelRootObjectKey key = new ModelRootObjectKey(schemaResource, xsdSchema, adaptable);
		WeakReference<IXSDModelRoot> weakXSDModelRoot = xsdModelRootPool.get(key);
		IXSDModelRoot xsdModelRoot = weakXSDModelRoot == null ? null : weakXSDModelRoot.get();
        if(xsdModelRoot == null) {
        	xsdModelRoot = new XSDModelRoot(xsdSchema, adaptable);
        	xsdModelRootPool.put(key, new WeakReference<IXSDModelRoot>(xsdModelRoot));
        }
        return xsdModelRoot;
	}
	
	IWsdlModelRoot getWsdlModelRoot(Definition definition, IAdaptable adaptable) {
		clearEmptyKeys(wsdlModelRootPool.keySet());
        Resource definitionResource = definition.eResource();
        final ModelRootObjectKey key = new ModelRootObjectKey(definitionResource, definition, adaptable);
		WeakReference<IWsdlModelRoot> weakWsdlModelRoot = wsdlModelRootPool.get(key);
		IWsdlModelRoot wsdlModelRoot = weakWsdlModelRoot == null ? null : weakWsdlModelRoot.get();
        if(wsdlModelRoot == null) {
        	wsdlModelRoot = new WSDLModelRoot(definition, adaptable);
        	wsdlModelRootPool.put(key, new WeakReference<IWsdlModelRoot>(wsdlModelRoot));
        }
        return wsdlModelRoot;
	}
	
	private void clearEmptyKeys(Set<ModelRootObjectKey> keys) {
		Iterator<ModelRootObjectKey> iterator = keys.iterator();
		while(iterator.hasNext()) {
			ModelRootObjectKey key = iterator.next();
			if(key.weakResource.get() == null || key.xsdObject.get() == null) {
				iterator.remove();
			}
		}
	}
	
	private static class ModelRootObjectKey {
		
		private WeakReference<Resource> weakResource = null;
		
		private WeakReference<EObject> xsdObject = null;
		
		private WeakReference<IAdaptable> weakAdaptable = null;
		
		ModelRootObjectKey(Resource eResource, EObject xsdObject, IAdaptable adaptable) {
			weakResource = new WeakReference<Resource>(eResource);
			this.xsdObject = new WeakReference<EObject>(xsdObject);
			this.weakAdaptable = new WeakReference<IAdaptable>(adaptable);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((weakAdaptable.get() == null) ? 0 : weakAdaptable.get().hashCode());
			result = prime
					* result
					+ ((xsdObject.get() == null) ? 0 : xsdObject.get()
							.hashCode());
			result = prime * result
					+ ((weakResource.get() == null) ? 0 : weakResource.get().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModelRootObjectKey other = (ModelRootObjectKey) obj;
			if (weakAdaptable.get() == null) {
				if (other.weakAdaptable.get() != null)
					return false;
			} else if (!weakAdaptable.equals(other.weakAdaptable.get()))
				return false;
			if (xsdObject.get() == null) {
				if (other.xsdObject.get() != null)
					return false;
			} else if (!xsdObject.get().equals(other.xsdObject.get()))
				return false;
			if (weakResource.get() == null) {
				if (other.weakResource.get() != null)
					return false;
			} else if (!weakResource.get().equals(other.weakResource.get()))
				return false;
			return true;
		}
	}
	
	
}
