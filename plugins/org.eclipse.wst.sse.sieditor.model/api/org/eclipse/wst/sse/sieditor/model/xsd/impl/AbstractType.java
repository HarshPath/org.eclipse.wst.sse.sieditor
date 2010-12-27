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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * 
 */
public abstract class AbstractType extends AbstractXSDComponent implements IType,
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.IType {

    private final XSDNamedComponent _component;

    private final ISchema _schema;

    private final IModelObject _parent;

    protected AbstractType(final IXSDModelRoot modelRoot, final XSDNamedComponent component, final ISchema parent) {
        this(modelRoot, component, parent, parent);

    }

    protected AbstractType(final IXSDModelRoot modelRoot, final XSDNamedComponent component, final ISchema schema,
            final IModelObject parent) {
        super(modelRoot);
        Nil.checkNil(schema, "schema"); //$NON-NLS-1$
        Nil.checkNil(component, "component"); //$NON-NLS-1$
        this._schema = schema;
        this._component = component;
        this._parent = parent;
    }

    public boolean isAnonymous() {
        if (_component instanceof XSDTypeDefinition) {
            return EmfXsdUtils.isAnonymous((XSDTypeDefinition) _component);

        } else if (_component instanceof XSDElementDeclaration) {
            return EmfXsdUtils.isAnonymous((XSDElementDeclaration) _component);
        }
        return false;
    }

    public String getName() {
        return _component.getName();
    }

    public ISchema getParent() {
        return _schema;
    }

    @Override
    public IModelObject getDirectParent() {
        return _parent;
    }

    public String getNamespace() {
        return _component.getTargetNamespace();
    }

    public void setAnonymous(final boolean anonymousType) {
        if (_component instanceof XSDComplexTypeDefinition)
            ((XSDComplexTypeDefinition) _component).setAbstract(anonymousType);
    }

    @Override
    public XSDNamedComponent getComponent() {
        return _component;
    }
}