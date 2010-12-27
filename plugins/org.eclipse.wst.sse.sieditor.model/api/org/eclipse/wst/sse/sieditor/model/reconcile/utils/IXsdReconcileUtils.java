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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils;

import org.eclipse.xsd.XSDSchema;

/**
 * This is the interface for the XML Schema reconcile utility methods.
 * 
 */
public interface IXsdReconcileUtils {

    public static enum NamespaceResolverType {
        ELEMENT, SCHEMA
    }

    public void reconcileSchemaContents(XSDSchema schema, ObjectsForResolveContainer container);

}