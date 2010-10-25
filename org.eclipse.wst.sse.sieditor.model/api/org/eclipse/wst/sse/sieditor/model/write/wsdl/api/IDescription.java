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
package org.eclipse.wst.sse.sieditor.model.write.wsdl.api;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.wst.sse.sieditor.model.generic.DocumentSaveException;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.write.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.write.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * Represents the root of the ServiceInterface Objects
 * @deprecated The write API is deprecated. Use commands instead.
 * 
 *
 */
public interface IDescription extends INamespacedObject, IExtensibleObject{
	
	IServiceInterface addInterface(String name) throws DuplicateException, IllegalInputException, ExecutionException;
	
	//void removeInterface(String name) throws ExecutionException;
	
	void removeInterface(IServiceInterface serviceInterface) throws ExecutionException;
	
	ISchema addSchema(String namespace) throws IllegalInputException, ExecutionException;
	
	boolean removeSchema(String namespace) throws ExecutionException;
	
	boolean removeSchema(ISchema schema) throws ExecutionException;
	
	/**
	 * @deprecated Use SaveCommand instead
	 * @return
	 * @throws DocumentSaveException
	 */
	boolean save() throws DocumentSaveException;

	IType makeResolvable(IType type) throws ExecutionException;
}