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
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.wst.wsdl.internal.impl.DefinitionImpl;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

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

        if (elementName == null && typeName == null && (extAtts == null || extAtts.isEmpty())) {
            return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_0, part.getName());
        }

        if (elementName != null && typeName != null) {
            return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_1, part.getName());
        }

        if (elementName != null) {
            if (EmfXsdUtils.isSchemaForSchemaNS(elementName.getNamespaceURI())) {
                return ctx.createSuccessStatus();
            }

            XSDElementDeclaration elementDeclaration = part.getElementDeclaration();
            if (elementDeclaration == null || elementDeclaration.getElement() == null) {
                return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_2, part.getName(),
                        elementName.getLocalPart());
            }
            if (elementDeclaration.getType() != null && !EmfWsdlUtils.couldBeVisibleType(elementDeclaration)) {
                DefinitionImpl definition = (DefinitionImpl) part.getEnclosingDefinition();

                if (!definition.resolveSchema(elementDeclaration.getTargetNamespace()).isEmpty()
                        || !definition.getNamespaces().containsValue(elementDeclaration.getTargetNamespace())) {

                    return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_2, part.getName(),
                            elementName.getLocalPart());
                }
            }
        }

        if (typeName != null) {
            if (EmfXsdUtils.isSchemaForSchemaNS(typeName.getNamespaceURI())) {
                return ctx.createSuccessStatus();
            }

            XSDTypeDefinition typeDefinition = part.getTypeDefinition();
            if (typeDefinition == null || typeDefinition.getElement() == null) {
                return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_3, part.getName(),
                        typeName.getLocalPart());
            }
            if (typeDefinition.getBaseType() != null && (!EmfWsdlUtils.couldBeVisibleType(typeDefinition))) {
                DefinitionImpl definition = (DefinitionImpl) part.getEnclosingDefinition();
                if (!definition.resolveSchema(typeDefinition.getTargetNamespace()).isEmpty()
                        || !definition.getNamespaces().containsValue(typeDefinition.getTargetNamespace())) {

                    return ConstraintStatus.createStatus(ctx, part, resultLocus, Messages.DefinitionMessages_3, part.getName(),
                            typeName.getLocalPart());
                }
            }

        }

        return ctx.createSuccessStatus();
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

}
