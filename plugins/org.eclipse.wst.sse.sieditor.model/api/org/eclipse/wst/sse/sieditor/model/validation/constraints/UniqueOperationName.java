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
 *    Tsvetan Stoyanov - initial API and implementation.
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
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * WSDLDocument->parseOperations According to the WSDL 1.2 working draft
 * operation overloading is no longer allowed.
 * 
 * 
 * 
 */
public class UniqueOperationName extends AbstractConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        Object target = ctx.getTarget();
        PortType portType = null;

        if (target instanceof PortType) {
            portType = (PortType) target;
        } else if (target instanceof Operation) {
            Operation operation = (Operation) target;
            portType = (PortType) operation.getContainer();
        }

        Set<String> allNames = new HashSet<String>();
        EList<Operation> operations = portType.getEOperations();
        List<IStatus> statusList = new ArrayList<IStatus>(operations.size());
        // the location of the error is the name of the operation
        Collection<EObject> locus = new ArrayList<EObject>();
        locus.add(WSDLPackage.Literals.OPERATION__NAME);

        final List<String> duplicateNames = new ArrayList<String>();

        for (Operation operation : operations) {
            String name = operation.getName();
            IStatus status = null;
            if (allNames.contains(name)) {
                duplicateNames.add(name);
                status = ConstraintStatus.createStatus(ctx, operation, locus, Messages.UniqueOperationName_0, name, portType
                        .getQName().getLocalPart());
            } else {
                allNames.add(name);
                status = ConstraintStatus.createSuccessStatus(ctx, operation, null);
            }
            statusList.add(status);
        }

        // adds the first elements to the status list
        for (int i = 0; i < operations.size(); i++) {
            final Operation operation = operations.get(i);
            final String name = operation.getName();
            if (duplicateNames.contains(name)) {
                duplicateNames.remove(name);
                ConstraintStatus errorStatus = ConstraintStatus.createStatus(ctx, operation, locus,
                        Messages.UniqueOperationName_0, name, portType.getQName().getLocalPart());
                statusList.set(i, errorStatus);
            }
        }

        return createStatus(ctx, statusList);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        Object target = ctx.getTarget();
        if (isBatchValidation(ctx)) {
            return target instanceof PortType;
        }
        return target instanceof Operation || (target instanceof PortType && "eOperations".equals(ctx.getFeature().getName())); //$NON-NLS-1$
    }
}
