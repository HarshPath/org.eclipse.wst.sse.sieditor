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
package org.eclipse.wst.sse.sieditor.model.write.api;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.CommandException;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

/**
 * Generic inteface all the writable {@link IModelObject} interfaces
 * @deprecated The write API is deprecated. Use commands instead.
 */
public interface IWritable {
	
	/**
	 * Sets Description of the Object
	 * @param description - {@link String}
	 * @throws ExecutionException 
	 * @throws CommandException 
	 */
	Element setDocumentation(String description) throws ExecutionException, CommandException;
}
