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
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class UniqueMessagePartName extends AbstractConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        Message message = null;

        if (target instanceof Message) {
            message = (Message) target;
        } else {
            Part part = (Part) target;
            message = (Message) part.getContainer();
        }

        Set<String> allNames = new HashSet<String>();
        EList<Part> parts = message.getEParts();
        List<IStatus> statusList = new ArrayList<IStatus>(parts.size());

        IStatus status = null;
        String name = null;
        // the location of the error will be the name of the message part
        Collection<EObject> locus = new ArrayList<EObject>();
        locus.add(WSDLPackage.Literals.PART__NAME);

        final List<String> duplicateNames = new ArrayList<String>();

        for (Part part : parts) {
            name = part.getName();
            if (allNames.contains(name)) {
                duplicateNames.add(name);
                status = ConstraintStatus.createStatus(ctx, part, locus, Messages.DuplicateUnique, name, message.getElement()
                        .getLocalName(), part.getElement().getLocalName());
            } else {
                allNames.add(name);
                status = ConstraintStatus.createSuccessStatus(ctx, part, null);
            }
            statusList.add(status);
        }

        // adds the first elements to the status list
        for (int i = 0; i < parts.size(); i++) {
            final Part part = parts.get(i);
            final String partName = part.getName();
            if (duplicateNames.contains(partName)) {
                duplicateNames.remove(partName);
                IStatus errorStatus = ConstraintStatus.createStatus(ctx, part, locus, Messages.DuplicateUnique, partName, message
                        .getElement().getLocalName(), part.getElement().getLocalName());

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
            result = target instanceof Message;
        } else {
            result = target instanceof Part;
        }

        if (!result) {
            ctx.skipCurrentConstraintFor(target);
        }
        return result;
    }

}
