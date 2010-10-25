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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * interface for the type committers. implementors are responsible for the
 * creation/setting of new types
 * 
 * 
 * 
 */
public interface ITypeCommitter {

    public void commitType(IType type);

    public void commitName(IType type, String typeName);

}
