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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveContainer;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.BaseComponentResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdComplexTypeDefinitionExtensionResolver;

public class UnresolvedXsdContentsValidation extends AbstractConstraint {

    @Override
    protected IStatus doValidate(final IValidationContext ctx) {
        final List<XSDSchema> allSchemas = getAllContainedSchemas(((XSDConcreteComponent) ctx.getTarget()).getSchema());

        final List<IStatus> statusList = new LinkedList<IStatus>();
        for (final XSDSchema schema : allSchemas) {
            final ObjectsForResolveContainer container = ObjectsForResolveUtils.instance().findObjectsForResolve(schema,
                    allSchemas);

            validateElementReferences(ctx, container.getElementsForReferenceResolve(), statusList);
            validateElementTypeDefinitions(ctx, container.getElementsForTypeResolve(), statusList);
            validateAttributeTypeDefinitions(ctx, container.getAttributesForTypeResolve(), statusList);
            validateAttributeReferences(ctx, container.getAttributesForReferenceResolve(), statusList);
            validateComplexTypeDefinitions(ctx, container.getComplexTypesForExtensionResolve(), statusList,
                    XSDConstants.EXTENSION_ELEMENT_TAG);
            validateComplexTypeDefinitions(ctx, container.getComplexTypesForRestrictionResolve(), statusList,
                    XSDConstants.RESTRICTION_ELEMENT_TAG);
        }
        return createStatus(ctx, statusList);
    }

    @SuppressWarnings("unchecked")
    private List<XSDSchema> getAllContainedSchemas(final XSDSchema schema) {
        final List<XSDSchema> allSchemas = new LinkedList<XSDSchema>();
        if (schema != null && schema.eContainer() == null) {
            allSchemas.add(schema);
        } else if (schema != null && schema.eContainer() instanceof XSDSchemaExtensibilityElement) {
            final Types eTypes = ((XSDSchemaExtensibilityElement) schema.eContainer()).getEnclosingDefinition().getETypes();
            if (eTypes != null) {
                allSchemas.addAll(eTypes.getSchemas());
            }
        }
        return allSchemas;
    }

    private void validateElementReferences(final IValidationContext ctx,
            final List<XSDNamedComponent> elementsForReferenceResolve, final List<IStatus> statusList) {
        for (final XSDNamedComponent xsdElementDeclaration : elementsForReferenceResolve) {
            final XSDElementDeclaration resolvedElementDeclaration = ((XSDElementDeclaration) xsdElementDeclaration)
                    .getResolvedElementDeclaration();
            if (resolvedElementDeclaration.eContainer() == null) {
                continue;
            }
            final Set<EObject> resultLocus = new HashSet<EObject>();
            resultLocus.add(XSDPackage.Literals.XSD_ELEMENT_DECLARATION__ELEMENT_DECLARATION_REFERENCE);
            statusList.add(ConstraintStatus.createStatus(ctx, xsdElementDeclaration, resultLocus, MessageFormat.format(
                    Messages.UnresolvedXsdContentsValidation_element_reference_X_Y_is_unresolved_msg, resolvedElementDeclaration
                            .getTargetNamespace(), resolvedElementDeclaration.getName()), IStatus.ERROR));
        }
    }

    private void validateElementTypeDefinitions(final IValidationContext ctx,
            final List<XSDNamedComponent> elementsForTypeResolve, final List<IStatus> statusList) {
        final EReference locusFeature = XSDPackage.Literals.XSD_ELEMENT_DECLARATION__TYPE_DEFINITION;

        for (final XSDNamedComponent namedComponent : elementsForTypeResolve) {
            final XSDTypeDefinition typeDefinition = getTypeDefinition(namedComponent);

            final String typeDefinitionName = BaseComponentResolver.getResolveComponentLocalName(namedComponent.getElement(),
                    typeDefinition);
            if (typeDefinitionName == null) {
                continue;
            }
            final Set<EObject> resultLocus = new HashSet<EObject>();
            resultLocus.add(locusFeature);
            statusList.add(ConstraintStatus.createStatus(ctx, namedComponent, resultLocus, MessageFormat.format(
                    Messages.UnresolvedXsdContentsValidation_type_reference_X_Y_is_unresolved_validation_msg, typeDefinition
                            .getTargetNamespace(), typeDefinitionName), IStatus.ERROR));
        }
    }

    private void validateAttributeTypeDefinitions(final IValidationContext ctx,
            final List<XSDNamedComponent> attributesForTypeResolve, final List<IStatus> statusList) {
        final EReference locusFeature = XSDPackage.Literals.XSD_ATTRIBUTE_DECLARATION__TYPE_DEFINITION;

        for (final XSDNamedComponent namedComponent : attributesForTypeResolve) {
            final XSDTypeDefinition typeDefinition = getTypeDefinition(namedComponent);

            final String typeDefinitionName = BaseComponentResolver.getResolveComponentLocalName(namedComponent.getElement(),
                    typeDefinition);
            if (typeDefinitionName == null) {
                continue;
            }
            final Set<EObject> resultLocus = new HashSet<EObject>();
            resultLocus.add(locusFeature);
            statusList.add(ConstraintStatus.createStatus(ctx, namedComponent, resultLocus, MessageFormat.format(
                    Messages.UnresolvedXsdContentsValidation_type_reference_X_Y_is_unresolved_validation_msg, typeDefinition
                            .getTargetNamespace(), typeDefinitionName), IStatus.ERROR));
        }
    }

    private XSDTypeDefinition getTypeDefinition(final XSDNamedComponent namedComponent) {
        if (namedComponent instanceof XSDAttributeDeclaration) {
            return ((XSDAttributeDeclaration) namedComponent).getTypeDefinition();
        } else if (namedComponent instanceof XSDElementDeclaration) {
            return ((XSDElementDeclaration) namedComponent).getTypeDefinition();
        }
        return null;
    }

    private void validateAttributeReferences(final IValidationContext ctx,
            final List<XSDNamedComponent> attributesForReferenceResolve, final List<IStatus> statusList) {

        for (final XSDNamedComponent xsdAttributeDeclaration : attributesForReferenceResolve) {
            final XSDAttributeDeclaration resolvedAttributeDeclaration = ((XSDAttributeDeclaration) xsdAttributeDeclaration)
                    .getResolvedAttributeDeclaration();
            if (resolvedAttributeDeclaration.eContainer() == null) {
                continue;
            }
            final Set<EObject> resultLocus = new HashSet<EObject>();
            resultLocus.add(XSDPackage.Literals.XSD_ATTRIBUTE_DECLARATION__ATTRIBUTE_DECLARATION_REFERENCE);
            statusList.add(ConstraintStatus.createStatus(ctx, xsdAttributeDeclaration, resultLocus, MessageFormat.format(
                    Messages.UnresolvedXsdContentsValidation_attributes_reference_X_Y_is_unresolved_msg,
                    resolvedAttributeDeclaration.getTargetNamespace(), resolvedAttributeDeclaration.getName()), IStatus.ERROR));
        }
    }

    private void validateComplexTypeDefinitions(final IValidationContext ctx,
            final List<XSDNamedComponent> complexTypesForResolve, final List<IStatus> statusList, final String derivationMethod) {

        for (final XSDNamedComponent xsdComplexTypeDefinition : complexTypesForResolve) {
            final XSDTypeDefinition baseTypeDefinition = ((XSDComplexTypeDefinition) xsdComplexTypeDefinition)
                    .getBaseTypeDefinition();
            final String typeDefinitionName = XsdComplexTypeDefinitionExtensionResolver.getResolveComponentLocalName(
                    ((XSDComplexTypeDefinition) xsdComplexTypeDefinition), derivationMethod);
            if (typeDefinitionName == null) {
                continue;
            }

            final Set<EObject> resultLocus = new HashSet<EObject>();
            resultLocus.add(XSDPackage.Literals.XSD_COMPLEX_TYPE_DEFINITION__BASE_TYPE_DEFINITION);
            statusList.add(ConstraintStatus.createStatus(ctx, xsdComplexTypeDefinition, resultLocus, MessageFormat.format(
                    Messages.UnresolvedXsdContentsValidation_type_reference_X_Y_is_unresolved_validation_msg, baseTypeDefinition
                            .getTargetNamespace(), typeDefinitionName), IStatus.ERROR));
        }
    }

    @Override
    protected boolean shouldExecute(final IValidationContext ctx) {
        if (isBatchValidation(ctx)) {
            return false;
        }
        return ctx.getTarget() instanceof XSDConcreteComponent;
    }

}
