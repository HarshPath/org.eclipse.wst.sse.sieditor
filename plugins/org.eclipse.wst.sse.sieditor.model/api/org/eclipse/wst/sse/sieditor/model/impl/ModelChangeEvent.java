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

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

/**
 * 
 * 
 *
 */
public class ModelChangeEvent implements IModelChangeEvent{

	private IModelObject _object;

	public ModelChangeEvent(final IModelObject object) {
		Nil.checkNil(object, "object"); //$NON-NLS-1$
		this._object = object;
	}
	
	public IModelObject getObject() {
		return _object;
	}
	
}
