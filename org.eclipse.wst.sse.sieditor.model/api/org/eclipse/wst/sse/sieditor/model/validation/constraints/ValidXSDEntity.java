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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

public class ValidXSDEntity extends AbstractConstraint {

    private Set<EClass> featureClasses = new HashSet<EClass>();

    private IStatus convertDiagnosticsToStatus(final IValidationContext ctx, final List<XSDDiagnostic> diagnostics,
            XSDSchema schema) {
        final List<IStatus> statusList = new ArrayList<IStatus>();
        statusList.add(ConstraintStatus.createSuccessStatus(ctx, schema, null));
        if (diagnostics.size() == 0) {
            final EObject target = ctx.getTarget();
            if (target instanceof XSDDiagnostic) {
                final XSDDiagnostic diagnostic = (XSDDiagnostic) target;
                diagnostic.getPrimaryComponent().getDiagnostics().add(diagnostic);
            }
            statusList.add(ConstraintStatus.createSuccessStatus(ctx, target, null));
        }

        for (XSDDiagnostic diagnostic : diagnostics) {
            int severity = IStatus.INFO;
            switch (diagnostic.getSeverity()) {
            case FATAL_LITERAL:
            case ERROR_LITERAL:
                severity = IStatus.ERROR;
                break;
            case WARNING_LITERAL:
                severity = IStatus.WARNING;
            }

            Collection<EObject> resultLocus = new ArrayList<EObject>();

            // add the feature to the result locus
            // IValidationContext#getFeature() does not work the way it is
            // supposed to
            EStructuralFeature feature = findFailedConstraintTrigger(diagnostic);

            if (feature != null) {
                resultLocus.add(feature);
            }

            // add other components to result locus
            resultLocus.addAll(diagnostic.getComponents());

            diagnostic.getPrimaryComponent().getDiagnostics().add(diagnostic);
            statusList.add(ConstraintStatus.createStatus(ctx, diagnostic, resultLocus, diagnostic.getMessage(), severity,
                    diagnostic.getSubstitutions()));
        }

        return createStatus(ctx, statusList);
    }

    public ValidXSDEntity() {
        super();
        featureClasses.add(XSDPackage.Literals.XSD_NAMED_COMPONENT);
        featureClasses.add(XSDPackage.Literals.XSD_TOTAL_DIGITS_FACET);
        featureClasses.add(XSDPackage.Literals.XSD_MIN_LENGTH_FACET);
        featureClasses.add(XSDPackage.Literals.XSD_MAX_LENGTH_FACET);
        featureClasses.add(XSDPackage.Literals.XSD_LENGTH_FACET);
        featureClasses.add(XSDPackage.Literals.XSD_ENUMERATION_FACET);
        featureClasses.add(XSDPackage.Literals.XSD_WHITE_SPACE_FACET);

    }

    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        final XSDSchema schema = ((XSDConcreteComponent) ctx.getTarget()).getSchema();
        // without this check the XSD API throws NPEs everywhere
        if (EmfXsdUtils.isSchemaElementMissing(schema)) {
            return ConstraintStatus.createStatus(ctx, schema, ctx.getResultLocus(),
                    Messages.ValidXSDEntity_Missing_Schema_Element0, IStatus.ERROR);
        }
        if (EmfXsdUtils.isSchemaForSchemaMissing(schema)) {
            return ConstraintStatus.createStatus(ctx, schema, ctx.getResultLocus(), Messages.EmfXsdUtils_MissingSchemaForSchema1,
                    IStatus.ERROR);
        }
        schema.clearDiagnostics();
        schema.validate();

        return convertDiagnosticsToStatus(ctx, schema.getAllDiagnostics(), schema);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        if (isBatchValidation(ctx)) {
            return (target instanceof XSDSchema);
        }

        return !(target instanceof XSDDiagnostic);
    }

    /**
     * Tries to find the particular feature which triggered the constraint
     */
    public EStructuralFeature findFailedConstraintTrigger(XSDDiagnostic diagnostic) {

        // First check if the constraint failed in an attribute and try to find
        // the feature which corresponds to this attribute
        Node node = diagnostic.getNode();
        EStructuralFeature feature = null;
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            String nodeName = node.getNodeName();
            feature = diagnostic.getContainer().eClass().getEStructuralFeature(nodeName);
            if (feature != null) {
                EClass eClass = feature.getEContainingClass();
                // this is done because for example xsd min exclusive and xsd
                // min exclusive have both the same feature class
                // - XSD_MIN_FACET and cannot be distinguished
                if (featureClasses.contains(eClass)) {
                    return feature;
                }
            }

        }
        // Now try to find the feature by examining the primary component in
        // which the constraint failed
        XSDConcreteComponent component = diagnostic.getPrimaryComponent();

        if (component instanceof XSDMinExclusiveFacet) {
            return XSDPackage.Literals.XSD_MIN_FACET__EXCLUSIVE;

        } else if (component instanceof XSDMinInclusiveFacet) {
            return XSDPackage.Literals.XSD_MIN_FACET__INCLUSIVE;

        } else if (component instanceof XSDMaxInclusiveFacet) {
            return XSDPackage.Literals.XSD_MAX_FACET__INCLUSIVE;

        } else if (component instanceof XSDMaxExclusiveFacet) {
            return XSDPackage.Literals.XSD_MAX_FACET__EXCLUSIVE;

        }
        // if the component was none of the above check again if the constrained
        // failed in an attribute; if it was not an attribute
        // we can assume the following 2 cases to be true
        if (feature != null) {
            return feature;
        }
        // if the constraint failed in the simple type definition and not in one
        // of its attributes it must be in its base type
        else if (component instanceof XSDSimpleTypeDefinition) {
            return XSDPackage.Literals.XSD_TYPE_DEFINITION__BASE_TYPE;
        }
        // if the constraint failed in the element or attribute
        // declaration and not in one of its attributes it must be in its type;
        // Use the XSD_TYPE_DEFINITION__ANNOTATION constant
        // to mark it since there is no constant combining simple and
        // complex types
        else if (component instanceof XSDElementDeclaration || component instanceof XSDAttributeDeclaration) {
            return XSDPackage.Literals.XSD_TYPE_DEFINITION__ANNOTATION;

        }

        return null;
    }
}
