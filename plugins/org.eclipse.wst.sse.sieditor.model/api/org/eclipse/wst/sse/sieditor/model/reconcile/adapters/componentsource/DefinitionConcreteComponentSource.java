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
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ui.internal.util.WSDLEditorUtil;
import org.w3c.dom.Element;

/**
 * This is the definition component source implementation of the
 * {@link IConcreteComponentSource} interface. The equality is based on the
 * provided definition.
 * 
 */
public class DefinitionConcreteComponentSource implements IConcreteComponentSource {

    private final Definition definition;

    public DefinitionConcreteComponentSource(final Definition definition) {
        this.definition = definition;
    }

    @Override
    public EObject getConcreteComponentFor(final Element element) {
        return (EObject) WSDLEditorUtil.getInstance().findModelObjectForElement(definition, element);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DefinitionConcreteComponentSource) {
            return definition.equals(((DefinitionConcreteComponentSource) obj).definition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

}
