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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public interface ITypeDisplayer {
    /**
     * Called for the subclass to display the given Type in a containing UI Control.<br/> 
     * The successor may choose not to display the type if it doesn't have it in a structure (e.g. a tree)
     * @param type the type to be displayed
     */
    public abstract void showType(IType type);

}