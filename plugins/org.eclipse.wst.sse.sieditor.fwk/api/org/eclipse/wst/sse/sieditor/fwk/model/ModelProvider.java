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
package org.eclipse.wst.sse.sieditor.fwk.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.sieditor.core.editorfwk.ModelHandler;
import org.eclipse.wst.sse.sieditor.mm.IModelProvider;
import org.eclipse.wst.sse.sieditor.model.api.IModelExtension;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.AbstractModelObject;
import org.eclipse.wst.sse.sieditor.model.write.api.IWritable;

public class ModelProvider implements IModelProvider, IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
        // GFB_POC: Commented this class will no longer be required
        if (!(adaptableObject instanceof IFileEditorInput)) {
        	return null;
        }
        
        if(getAdapterList()[0].equals(adapterType) ||
           getAdapterList()[1].equals(adapterType)) {
            final IFile f = ((IFileEditorInput) adaptableObject).getFile();
            if (f != null) {
                return ModelHandler.retrieveModelObject(URI.createFileURI(f.getLocation().toFile().getAbsolutePath()));
            }
        }
        return null;
    }
	
	public Class[] getAdapterList() {
        return new Class[] { IWsdlModelRoot.class, IXSDModelRoot.class };
    }

    public boolean supports(IModelObject source) {
        return source instanceof AbstractModelObject;
    }

    /**
     *  @deprecated The write API is deprecated. Use commands instead.
     */
    public IWritable getWriteObject(IModelObject object) {
        if (supports(object) && object instanceof IWritable)
            return (IWritable) object;
        return null;
    }

    public <T extends IModelExtension> T getModleExtension(IModelObject source, Class<T> adapter) {
        // Still Not used
        return null;
    }
    
}