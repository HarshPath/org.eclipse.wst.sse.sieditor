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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.List;
import java.util.Set;

import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * This is the interface for all EMF patchers from DOM changes. Implementors are
 * responsible for the patching of the model root and updating the child nodes -
 * both in definition and XML Schema.
 */
public interface IEmfModelPatcher {

    /**
     * Updates the model root based on its type - definition or XML schema
     */
    public void patchEMFModelAfterDomChange(IModelRoot modelRoot, Set<Node> changedNodes);

    public void updateChangedNodes(final Set<Node> changedNodes, final Definition definition, final List<ISchema> containedSchemas);

    public void updateChangedNodes(final Set<Node> changedNodes, final XSDSchema xsdSchema);

}
