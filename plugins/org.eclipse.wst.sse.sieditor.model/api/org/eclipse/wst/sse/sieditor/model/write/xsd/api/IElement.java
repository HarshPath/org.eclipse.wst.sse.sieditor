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
package org.eclipse.wst.sse.sieditor.model.write.xsd.api;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.wst.sse.sieditor.model.write.api.INamedObject;

/**
 * 
 * @deprecated The write API is deprecated. Use commands instead.
 *
 */
public interface IElement extends INamedObject {

	@Deprecated
	void setType(org.eclipse.wst.sse.sieditor.model.xsd.api.IType type) throws ExecutionException;

	@Deprecated
	org.eclipse.wst.sse.sieditor.model.xsd.api.IType setAnonymousType(boolean isSimple) throws ExecutionException;

	@Deprecated
	void setMinOccurs(int value) throws ExecutionException;

	@Deprecated
	void setMaxOccurs(int value) throws ExecutionException;

	@Deprecated
	void setNillable(boolean nillable) throws ExecutionException;

}
