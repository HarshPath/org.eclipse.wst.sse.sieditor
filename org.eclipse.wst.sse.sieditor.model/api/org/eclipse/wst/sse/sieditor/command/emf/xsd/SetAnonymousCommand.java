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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDFactory;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.NamedNodeMap;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for making an element Anonymous
 * 
 * 
 * 
 */
public class SetAnonymousCommand extends AbstractXSDNotificationOperation {
    private static final String ATTRIBUTE_NAME = "ref"; //$NON-NLS-1$
    private final Schema _schema;
    private AbstractType _type;
    private final boolean _isSimple;

    public SetAnonymousCommand(final IXSDModelRoot root, final IElement element, final AbstractType type, final boolean isSimple) {
        super(root, element, Messages.SetAnonymousCommand_set_anonymous_command_label);
        this._type = type;
        this._schema = ((Element) element).getSchema();
        this._isSimple = isSimple;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final XSDConcreteComponent _component =  (XSDConcreteComponent) modelObject.getComponent();
        final XSDFactory factory = getXSDFactory();

        if (_component instanceof XSDAttributeDeclaration) {
            if (null == _type) {
                // For Attribute Declarations irrespective of isSimple is false
                // it
                // will always be SimpleTypeDefinition
                final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) _component;
                XSDSimpleTypeDefinition type = attribute.getAnonymousTypeDefinition();
                if (null == type) {
                    // attribute.setTypeDefinition(null);
                    ((EObject) attribute).eUnset(XSDPackage.eINSTANCE.getXSDAttributeDeclaration_TypeDefinition());
                    type = factory.createXSDSimpleTypeDefinition();
                    attribute.setAnonymousTypeDefinition(type);
                }
                _type = new SimpleType(getModelRoot(), _schema, modelObject, type);
            }
        } else if (_component instanceof XSDParticle) {
            XSDElementDeclaration element = (XSDElementDeclaration) ((XSDParticle) _component).getContent();
            XSDTypeDefinition type = element.getAnonymousTypeDefinition();
            if (_isSimple && (null == _type || _type instanceof IStructureType)) {
                if (null == type || type instanceof XSDComplexTypeDefinition) {
                    // Un-set element declaration reference
                    ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
                    // If Element Declaration Reference then set name for
                    // element
                    if (element.isElementDeclarationReference()) {
                        final XSDElementDeclaration referredElement = element.getResolvedElementDeclaration();
                        if (null != referredElement.eContainer())
                            element.setName(referredElement.getName());
                        final NamedNodeMap attributes = element.getElement().getAttributes();
                        if (attributes.getNamedItem(ATTRIBUTE_NAME) != null) {
                            attributes.removeNamedItem(ATTRIBUTE_NAME);
                        }
                    }
                    // Create a simple type and set type
                    type = factory.createXSDSimpleTypeDefinition();
                    element.setAnonymousTypeDefinition(type);
                    _type = new SimpleType(getModelRoot(), _schema, modelObject, (XSDSimpleTypeDefinition) type);
                } else {
                    _type = new SimpleType(getModelRoot(), _schema, modelObject,(XSDSimpleTypeDefinition) type);
                }

            } else {
                if (null == _type || _type instanceof ISimpleType) {
                    if (null == type || type instanceof XSDSimpleTypeDefinition) {
                        // Un-set element declaration reference
                        ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
                        // If Element Declaration Reference then set name for
                        // element
                        if (element.isElementDeclarationReference()) {
                            final XSDElementDeclaration referredElement = element.getResolvedElementDeclaration();
                            if (null != referredElement.getContainer()) {
                                XSDParticle particle = (XSDParticle) element.getContainer();
                                final XSDModelGroup modelGroup = (XSDModelGroup) particle.getContainer();

                                final int minOccurs = particle.getMinOccurs();
                                final int maxOccurs = particle.getMaxOccurs();
                                final boolean isNillable = element.isNillable();

                                final int ndx = modelGroup.getContents().indexOf(particle);
                                modelGroup.getContents().remove(ndx);

                                particle = XSDFactory.eINSTANCE.createXSDParticle();
                                particle.setMinOccurs(minOccurs);
                                particle.setMaxOccurs(maxOccurs);

                                type = (XSDTypeDefinition) referredElement.getType().cloneConcreteComponent(true, false);
                                
                                element = XSDFactory.eINSTANCE.createXSDElementDeclaration();
                                element.setName(referredElement.getName());
                                element.setNillable(isNillable);
                                element.setAnonymousTypeDefinition(type);

                                particle.setContent(element);
                                modelGroup.getContents().add(ndx, particle);

                                // element.setName(referredElement.getName());
                                // ((EObject)
                                // element).eUnset(XSDPackage.eINSTANCE
                                // .getXSDElementDeclaration_ElementDeclarationReference());

                                _type = new StructureType(getModelRoot(), _schema, modelObject, type);
                            }
                        } else {
                            type = factory.createXSDComplexTypeDefinition();
                            element.setAnonymousTypeDefinition(type);

                            _type = new StructureType(getModelRoot(), _schema,modelObject, type);
                        }
                    } else if (null != type) {
                        _type = new StructureType(getModelRoot(), _schema, modelObject, type);
                    }
                }
            }
        }
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
    	if (modelObject == null || _schema == null) {
    		return false;
    	}
    	if (!_isSimple && modelObject.getComponent() instanceof XSDAttributeDeclaration) {
    		return false;
    	}
    	
    	return true;
        
    }

    public AbstractType getType() {
        return _type;
    }

}
