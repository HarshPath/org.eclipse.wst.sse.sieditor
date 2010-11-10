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
package org.eclipse.wst.sse.sieditor.model.impl;

import org.eclipse.core.runtime.PlatformObject;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * 
 * 
 *
 */
public abstract class AbstractModelObject extends PlatformObject implements IModelObject {
	
	private final IModelRoot _modelRoot;

	protected AbstractModelObject(final IModelRoot modelRoot) {
		Nil.checkNil(modelRoot, "modelRoot"); //$NON-NLS-1$
		this._modelRoot = modelRoot;
	}
	
	protected AbstractModelObject() {
		_modelRoot = null;
	}
	
	public IModelRoot getModelRoot() {
		return _modelRoot;
	}
	
	
	public IModelObject getDirectParent() {
	
	    return getParent();
	}

        public IModelObject getRoot(){
            IModelObject parent = getParent();
            IModelObject result= parent;
            while(parent != null){
                result = parent;
                parent = parent.getParent();
            }
            return result;
	}
	
	protected IModelChangeEvent createModelChangeEvent(final IModelObject object){
		return new ModelChangeEvent(object);
	}

}