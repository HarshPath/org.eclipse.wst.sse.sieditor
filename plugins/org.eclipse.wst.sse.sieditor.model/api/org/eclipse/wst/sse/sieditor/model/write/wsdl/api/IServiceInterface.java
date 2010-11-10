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

import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.write.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.write.api.IQNamedObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;

/**
 * @deprecated The write API is deprecated. Use commands instead.
 * 
 * 
 */
public interface IServiceInterface extends IQNamedObject, IExtensibleObject {

    IOperation addOperation(String name, OperationType operationType) throws DuplicateException, IllegalInputException,
            ExecutionException;

    void removeOperation(IOperation operation) throws ExecutionException;

    //NOT USED
    //void removeOperation(String operation) throws ExecutionException;

}
