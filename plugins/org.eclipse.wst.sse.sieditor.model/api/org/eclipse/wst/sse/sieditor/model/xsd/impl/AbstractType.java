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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * 
 * 
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
            final XSDTypeDefinition xsdTypeDefinition = (XSDTypeDefinition) _component;
            final XSDConcreteComponent container = xsdTypeDefinition.getContainer();
            return null == xsdTypeDefinition.getName() && !(container instanceof XSDSchema);

        } else if (_component instanceof XSDElementDeclaration) {
            final XSDTypeDefinition anonymousTypeDefinition = ((XSDElementDeclaration) _component).getAnonymousTypeDefinition();
            return isAnonymous(anonymousTypeDefinition);
        }
        return false;
    }

    /**
     * Utility method checking if the type definition is anonymous. Here we are
     * explicitly checking if the name of the base type of the definition is
     * equal to "anyType". Imagine we have the following element in the source: <br>
     * <br>
     * &lt;xsd:element name="NewOperation"> <br>
     * &nbsp;&nbsp;&lt;xsd:complexType> <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element minOccurs="1"
     * maxOccurs="1" name="Element1" type="tns:StructureType2"><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:element><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence><br>
     * &nbsp;&nbsp;&lt;/xsd:complexType> <br>
     * &lt;/xsd:element><br>
     * <br>
     * Here, the anonymousTypeDefinition is the first child of the element - the
     * &lt;complexType>...&lt;/complexType> element. The element is anonymous if
     * the complexType has no type. The EMF model reflects this by setting the
     * type of the complexType to "anyType". This is why we need to check it.
     * 
     * @param anonymousTypeDefinition
     * @return <code>true</code> if the structure type is anonymous.
     *         <code>false</code> otherwise.
     */
    private boolean isAnonymous(final XSDTypeDefinition anonymousTypeDefinition) {
        return (anonymousTypeDefinition != null && anonymousTypeDefinition.getBaseType() != null && "anyType" //$NON-NLS-1$
        .equals(anonymousTypeDefinition.getBaseType().getName()));
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