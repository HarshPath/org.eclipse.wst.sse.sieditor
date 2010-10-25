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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.wst.sse.sieditor.core.common.NotImplemented;
import org.eclipse.wst.sse.sieditor.model.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelExtension;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * 
 * 
 *
 */
public abstract class AbstractExtensibleObject extends AbstractModelObject implements IExtensibleObject,
	org.eclipse.wst.sse.sieditor.model.write.api.IExtensibleObject {
	
	private final HashSet<IModelExtension> _extensions;
	
	protected AbstractExtensibleObject(final IModelRoot modelRoot) {
		super(modelRoot);
		this._extensions = new HashSet<IModelExtension>(1);
	}
	
	protected AbstractExtensibleObject(){
		this._extensions = null;
	}

	public Collection<IModelExtension> getExtensions() {
		return Collections.unmodifiableCollection(_extensions);
	}

	public void addExtension(IModelExtension extension) {
		throw new NotImplemented("addExtension is not yet supported");		 //$NON-NLS-1$
	}

	public void removeExtension(IModelExtension extension) {
		throw new NotImplemented("removeExtension is not yet supported");		 //$NON-NLS-1$
	}

}