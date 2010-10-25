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
package org.eclipse.wst.sse.sieditor.model.wsdl.api;

import java.util.Collection;
import java.util.List;

import org.eclipse.wst.wsdl.Fault;

import org.eclipse.wst.sse.sieditor.model.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;

/**
 * 
 * 
 *
 */
public interface IFault extends INamedObject, IExtensibleObject {
	
	List<IParameter> getParameter(final String name);

	Collection<IParameter> getParameters();
	
	Fault getComponent();
}
