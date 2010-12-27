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

import org.eclipse.wst.sse.sieditor.model.api.IExtensibleObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * Operation parameter
 */
public interface IParameter extends INamedObject, IExtensibleObject {
	
    IType getType();

    boolean canEdit();

    /**
     * 
     * @return INPUT if this is an input parameter, OUTPUT or FAULT
     */
    byte getParameterType();

    final byte INPUT = 0;
    final byte OUTPUT = 1;
    final byte FAULT = 2;
}