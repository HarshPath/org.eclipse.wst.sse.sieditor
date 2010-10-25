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
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

/**
 * This is the XML Schema component source implementation of the
 * {@link IConcreteComponentSource} interface. The equality is based on the
 * provided schema.
 * 
 * 
 * 
 */
public class XsdSchemaConcreteComponentSource implements IConcreteComponentSource {

    private final XSDSchema schema;

    public XsdSchemaConcreteComponentSource(final XSDSchema schema) {
        this.schema = schema;
    }

    @Override
    public EObject getConcreteComponentFor(final Element element) {
        return schema.getCorrespondingComponent(element);
    }

    @Override
    public int hashCode() {
        return schema.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof XsdSchemaConcreteComponentSource) {
            return schema.equals(((XsdSchemaConcreteComponentSource) obj).schema);
        }
        return false;
    }

}
