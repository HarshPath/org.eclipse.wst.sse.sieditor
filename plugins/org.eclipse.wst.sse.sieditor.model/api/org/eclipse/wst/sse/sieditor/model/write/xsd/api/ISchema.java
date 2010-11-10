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

import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.write.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * @deprecated The write API is deprecated. Use commands instead.
 * 
 * 
 */
public interface ISchema extends INamespacedObject {

    ISimpleType addSimpleType(String name) throws DuplicateException, IllegalInputException, ExecutionException;

    IStructureType addStructureType(String name, boolean element) throws DuplicateException, IllegalInputException, ExecutionException;

    void removeType(IType type) throws ExecutionException;

    IType copyType(IType type, String newName) throws DuplicateException, ExecutionException;

    IType createGlobalTypeFromAnonymous(org.eclipse.wst.sse.sieditor.model.xsd.api.IElement element, String newName)
            throws DuplicateException, ExecutionException;

}
