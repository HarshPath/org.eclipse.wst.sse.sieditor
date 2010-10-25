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
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource;

import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Element;

/**
 * This is the model source interface. Implementors are responsible for the
 * components search of DOM elements. Implementors should also implement equals
 * + hashCode methods.
 * 
 * 
 * 
 */
public interface IConcreteComponentSource {

    /**
     * @return the EMF object corresponding to the given DOM element.
     */
    public EObject getConcreteComponentFor(final Element element);

    public int hashCode();

    public boolean equals(final Object obj);

}
