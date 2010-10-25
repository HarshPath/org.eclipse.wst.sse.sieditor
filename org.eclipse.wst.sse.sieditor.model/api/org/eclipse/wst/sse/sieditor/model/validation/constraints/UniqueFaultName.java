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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class UniqueFaultName extends AbstractConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        Operation operation = null;

        if (target instanceof Operation) {
            operation = (Operation) target;
        } else {
            operation = (Operation) ((Fault) target).getContainer();
        }

        Set<String> allNames = new HashSet<String>();

        EList<Fault> faults = operation.getEFaults();
        List<IStatus> statusList = new ArrayList<IStatus>(faults.size());
        // the location of the error will be the name of the fault
        Collection<EObject> locus = new ArrayList<EObject>();
        locus.add(WSDLPackage.Literals.FAULT.getEStructuralFeature(WSDLPackage.FAULT__NAME));

        final List<String> duplicateNames = new ArrayList<String>();

        for (Fault fault : faults) {
            String name = fault.getName();
            IStatus status = null;
            if (allNames.contains(name)) {
                duplicateNames.add(name);
                status = ConstraintStatus.createStatus(ctx, fault, locus, Messages.UniqueFaultName_0, name, operation.getName());
            } else {
                allNames.add(name);
                status = ConstraintStatus.createSuccessStatus(ctx, fault, null);
            }
            statusList.add(status);
        }

        // adds the first elements to the status list
        for (int i = 0; i < faults.size(); i++) {
            final Fault fault = faults.get(i);
            final String name = fault.getName();
            if (duplicateNames.contains(name)) {
                duplicateNames.remove(name);
                ConstraintStatus errorStatus = ConstraintStatus.createStatus(ctx, fault, locus, Messages.UniqueFaultName_0, name,
                        operation.getName());
                statusList.set(i, errorStatus);
            }
        }

        return createStatus(ctx, statusList);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        boolean result = false;

        if (isBatchValidation(ctx)) {
            result = target instanceof Operation;
        } else {
            result = target instanceof Fault || (target instanceof Operation && "eFaults".equals(ctx.getFeature().getName())); //$NON-NLS-1$
        }

        if (!result) {
            ctx.skipCurrentConstraintFor(target);
        }
        return result;
    }

}
