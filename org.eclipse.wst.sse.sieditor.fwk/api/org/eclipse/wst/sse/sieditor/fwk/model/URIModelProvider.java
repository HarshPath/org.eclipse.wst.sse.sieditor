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
package org.eclipse.wst.sse.sieditor.fwk.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorInput;

import org.eclipse.wst.sse.sieditor.core.editorfwk.IModelObject;
import org.eclipse.wst.sse.sieditor.mm.IModelProvider;
import org.eclipse.wst.sse.sieditor.model.api.IModelExtension;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.AbstractModelObject;
import org.eclipse.wst.sse.sieditor.model.write.api.IWritable;

/**
 * 
 * Modified by GFB This class is the model provider for WSDL Editor input.
 * 
 */

public class URIModelProvider implements IAdapterFactory, IModelProvider {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (!(adaptableObject instanceof IEditorInput)) {
			return null;
		}
		
        if (getAdapterList()[0].equals(adapterType) ||
        		getAdapterList()[1].equals(adapterType)) {
        	
            IModelObject modelObject = (IModelObject) ((IEditorInput) adaptableObject).getAdapter(adapterType);
            return modelObject.getAdapter(adapterType);
        }
        return null;
    }
	
	public Class[] getAdapterList() {
        return new Class[] { IWsdlModelRoot.class, IXSDModelRoot.class };
    }

    public boolean supports(org.eclipse.wst.sse.sieditor.model.api.IModelObject source) {
        return source instanceof AbstractModelObject;
    }

    /**
     *  @deprecated The write API is deprecated. Use commands instead.
     */
    public IWritable getWriteObject(org.eclipse.wst.sse.sieditor.model.api.IModelObject object) {
        if (supports(object) && object instanceof IWritable)
            return (IWritable) object;
        return null;
    }

    public <T extends IModelExtension> T getModleExtension(org.eclipse.wst.sse.sieditor.model.api.IModelObject source, Class<T> adapter) {
        // Still Not used
        return null;
    }
    
}