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
package org.eclipse.wst.sse.sieditor.mm;

import org.eclipse.wst.sse.sieditor.model.api.IModelExtension;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.write.api.IWritable;

/**
 * 
 *
 */
public interface IModelProvider {
	
	IWritable getWriteObject(IModelObject object);
	
	<T extends IModelExtension> T getModleExtension(IModelObject source, Class<T> adapter);
	
	boolean supports(IModelObject source);
	
}