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
package org.eclipse.wst.sse.sieditor.model.validation.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.internal.impl.DefinitionImpl;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class DefinitionMessages extends AbstractConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext ctx) {

        Part part = (Part) ctx.getTarget();
        QName elementName = part.getElementName();
        QName typeName = part.getTypeName();
        Map extAtts = part.getExtensionAttributes();

        Collection<EObject> resultLocus = new ArrayList<EObject>();
        resultLocus.add(WSDLPackage.Literals.PART__TYPE_DEFINITION);
        // TODO:This will have to be extended as parts can have extensibility
        // elements
        // ensure the part has a type or an element defined
        if (elementName == null && typeName == null && (extAtts == null || extAtts.isEmpty())) {
            return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_0, part.getName());
            /*
             * String[] args = { p.getName()};
             * valInfo.addError(messagegenerator.
             * getString(_PART_NO_ELEMENT_OR_TYPE, QUOTE + args[0] + QUOTE), p,
             * _PART_NO_ELEMENT_OR_TYPE, args);
             */
        } else if (elementName != null && typeName != null) {
            return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_1, part.getName());
            // here the part has both the element and type defined and it can
            // only have one defined
            /*
             * String[] args = {p.getName()};
             * valInfo.addError(messagegenerator.getString
             * (_PART_BOTH_ELEMENT_AND_TYPE, QUOTE + args[0] + QUOTE), p,
             * _PART_BOTH_ELEMENT_AND_TYPE, args);
             */
        } else if (elementName != null && part.getElementDeclaration() != null
                && part.getElementDeclaration().getContainer() == null) {
            XSDElementDeclaration elementDeclaration = part.getElementDeclaration();
            Definition definition = part.getEnclosingDefinition();

            if (!((DefinitionImpl) definition).resolveSchema(elementDeclaration.getTargetNamespace()).isEmpty()
                    || !definition.getNamespaces().containsValue(elementDeclaration.getTargetNamespace())) {

                return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_2, part.getName(),
                        elementName.getLocalPart());
            }
            /*
             * if (!checkPartConstituent(elementName.getNamespaceURI(),
             * elementName.getLocalPart(), ELEMENT, p, valInfo)) { String[] args
             * = {p.getName(), elementName.getLocalPart()}; valInfo.addError(
             * messagegenerator.getString( _PART_INVALID_ELEMENT, QUOTE +
             * args[0] + QUOTE, QUOTE + args[1] + QUOTE), p,
             * _PART_INVALID_ELEMENT, args); }
             */
        } else if (typeName != null && part.getTypeDefinition() != null && part.getTypeDefinition().getContainer() == null) {
            XSDTypeDefinition typeDefinition = part.getTypeDefinition();
            DefinitionImpl definition = (DefinitionImpl) part.getEnclosingDefinition();
            if (!definition.resolveSchema(typeDefinition.getTargetNamespace()).isEmpty()
                    || !definition.getNamespaces().containsValue(typeDefinition.getTargetNamespace())) {

                // check that the type itself is defined properly
                return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_3, part.getName(),
                        typeName.getLocalPart());
            }
            /*
             * if (!checkPartConstituent(typeName.getNamespaceURI(),
             * typeName.getLocalPart(), TYPE, p, valInfo)) { String[] args =
             * {p.getName(), typeName.getLocalPart() }; valInfo.addError(
             * messagegenerator.getString( _PART_INVALID_TYPE, QUOTE + args[0] +
             * QUOTE, QUOTE + args[1] + QUOTE), p, _PART_INVALID_TYPE, args); }
             */
        }

        return ctx.createSuccessStatus();
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

}
