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

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.NamedNodeMap;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * Command for setting type for Local ElementDeclarations. It also does all the
 * additional cleaning up need for setting the type definintion in case the
 * element has anonymous type or if it refers some global element etc.
 * 
 * 
 * 
 */
public class SetElementTypeCommand extends AbstractCompositeNotificationOperation {

    private IType _type;
    private final IElement element;

    public SetElementTypeCommand(IElement element, IType newType) {
        this(element.getModelRoot(), element, newType);

    }

    public SetElementTypeCommand(IModelRoot root, IElement element, IType newType) {
        super(root, element, Messages.SetElementTypeCommand_set_element_type_command_label);
        this.element = element;
        this._type = newType;
        setTransactionPolicy(TransactionPolicy.MULTI);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        if (subOperations.isEmpty()) {
            IXSDModelRoot elementModelRoot = element.getModelRoot();
            return new org.eclipse.wst.sse.sieditor.command.emf.xsd.ImportSchemaCommand(elementModelRoot, elementModelRoot
                    .getSchema(), (AbstractType) _type);
        } else if (subOperations.size() == 1) {
            return new SetElementTypeCommandWithoutImport(root, element, _type);
        }

        return null;
    }

    private class SetElementTypeCommandWithoutImport extends AbstractNotificationOperation {
        private IType _type;
        private final IElement element;

        public SetElementTypeCommandWithoutImport(final IModelRoot root, final IElement element, final IType type) {
            super(root, element, Messages.SetElementTypeCommand_set_element_type_command_label);
            this.element = element;
            this._type = type;
        }

        @Override
        public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IType elementType = element.getType();

            // Check if facets can be reused and in such case execute
            // SetBaseTypeCommand
            IStatus iStatus = SetBaseTypeCommand
                    .setBaseTypeIfFacetsCanBeReused(elementType, _type, monitor, info, getModelRoot());
            if (iStatus != null) {
                // SetBaseTypeCommand is executed
                return iStatus;
            }

            final Schema schema = ((Element) element).getSchema();

            IType resolvedType = schema.resolveType((AbstractType) _type);
            if (resolvedType instanceof UnresolvedType) {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                        Messages.SetElementTypeCommand_msg_can_not_resolve_type_X, _type.getName()));
            }

            _type = resolvedType;

            final XSDNamedComponent typeToBeSet = _type.getComponent();
            final XSDConcreteComponent elementWithNewType = (XSDConcreteComponent) modelObject.getComponent();
            if (typeToBeSet instanceof XSDTypeDefinition) {
                final XSDTypeDefinition xsdType = (XSDTypeDefinition) typeToBeSet;
                if (elementWithNewType instanceof XSDAttributeDeclaration) {
                    if (xsdType instanceof XSDSimpleTypeDefinition) {
                        final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) elementWithNewType;
                        if (null != _type) {
                            // unset already set type
                            ((EObject) attribute).eUnset(XSDPackage.eINSTANCE
                                    .getXSDAttributeDeclaration_AnonymousTypeDefinition());
                            ((EObject) attribute).eUnset(XSDPackage.eINSTANCE.getXSDAttributeDeclaration_TypeDefinition());
                        }
                        if (attribute.isAttributeDeclarationReference()) {
                            // if attribute was referring to a global attribute
                            // then
                            // set the name for
                            // the attribute
                            String name = attribute.getResolvedAttributeDeclaration().getName();
                            final NamedNodeMap attributes = attribute.getElement().getAttributes();
                            if (attributes.getNamedItem(EmfXsdUtils.ATTRIBUTE_REF) != null) {
                                attributes.removeNamedItem(EmfXsdUtils.ATTRIBUTE_REF);
                            }
                            if (null != name && null == attribute.getName())
                                attribute.setName(name);
                        }
                        // now attribute is a condition for setting the type
                        // definition
                        attribute.setTypeDefinition((XSDSimpleTypeDefinition) xsdType);
                    } else {
                        // DOIT throw User Exception
                    }
                } else if (elementWithNewType instanceof XSDParticle) {
                    final XSDParticleContent content = ((XSDParticle) elementWithNewType).getContent();
                    if (content instanceof XSDElementDeclaration) {
                        final XSDElementDeclaration element = (XSDElementDeclaration) content;
                        if (null != _type) {
                            // unset already referred type defintion
                            ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_AnonymousTypeDefinition());
                            ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
                        }
                        if (element.isElementDeclarationReference()) {
                            // if attribute was referring to a global element
                            // then
                            // set the name
                            // for the element
                            String name = element.getResolvedElementDeclaration().getName();
                            final NamedNodeMap attributes = element.getElement().getAttributes();
                            if (attributes.getNamedItem(EmfXsdUtils.ATTRIBUTE_REF) != null) {
                                attributes.removeNamedItem(EmfXsdUtils.ATTRIBUTE_REF);
                            }
                            // ((EObject)element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_ElementDeclarationReference());
                            if (null != name && null == element.getName())
                                element.setName(name);
                        }
                        // set type after all the initial cleanup
                        element.setTypeDefinition(xsdType);
                    } else {
                        throw new RuntimeException("Component is not an instanceof XSDElementDeclaration"); //$NON-NLS-1$
                    }
                }
            } else if (typeToBeSet instanceof XSDElementDeclaration && elementWithNewType instanceof XSDParticle) {
                // set ref value for the element
                final XSDParticleContent content = ((XSDParticle) elementWithNewType).getContent();
                if (content instanceof XSDElementDeclaration) {
                    final XSDElementDeclaration element = (XSDElementDeclaration) content;
                    if (null != _type) {
                        // unset name, anonymous type and already assigned type
                        // defintion for the element
                        // before setting resolved element declaration
                        ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_AnonymousTypeDefinition());

                        // do unset "type" before call of
                        // "element.setResolvedElementDeclaration((XSDElementDeclaration) typeToBeSet);"
                        // because after that it has no effect
                        ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());

                        ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDNamedComponent_Name());
                    }
                    element.setResolvedElementDeclaration((XSDElementDeclaration) typeToBeSet);
                    // this call has to be done once again, because
                    // "element.setResolvedElementDeclaration((XSDElementDeclaration) typeToBeSet);"
                    // do set "type="anyType""
                    ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
                }
            }
            return Status.OK_STATUS;
        }

        @Override
        public boolean canExecute() {
            return modelObject != null && _type != null && !(_type instanceof UnresolvedType) && _type instanceof AbstractType
                    && modelObject.getComponent() != null;
        }
    }
}