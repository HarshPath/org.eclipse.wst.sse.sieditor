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

import javax.wsdl.Definition;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Import;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class Imports extends AbstractConstraint {

    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        if (!(target instanceof Import)) {
            throw new IllegalArgumentException(target.eClass().getName());
        }
        Import importedDef = (Import) target;
        String namespaceURI = importedDef.getNamespaceURI();
        Definition definition = importedDef.getDefinition();
        Collection<EObject> resultLocus = new ArrayList<EObject>();
        if (definition == null) {
            return ConstraintStatus.createStatus(ctx, target, resultLocus,
                    Messages.Imports_Missing_File_Err,
                    importedDef.getLocationURI());
        }
        String targetNamespace = definition.getTargetNamespace();
        if (targetNamespace != null && !targetNamespace.equals(namespaceURI)) {
            return ConstraintStatus.createStatus(ctx, target, resultLocus,
                    Messages.Imports_Wrong_Namespace_Err, namespaceURI,
                    targetNamespace);

        }
        return ConstraintStatus.createSuccessStatus(ctx, target, resultLocus);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        boolean result = true;

        if (isBatchValidation(ctx)) {
            result = target instanceof Import;
        }

        if (!result) {
            ctx.skipCurrentConstraintFor(target);
        }
        return result;
    }

}
