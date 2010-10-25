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
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.Service;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * Port name must be unique among all Ports
 * 
 * 
 * 
 */
public class UniquePortName extends AbstractConstraint {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        WSDLElement target = (WSDLElement) ctx.getTarget();

        Definition definition = target.getEnclosingDefinition();

        if (definition == null) {
            return ctx.createSuccessStatus();
        }

        Set<String> allNames = new HashSet<String>();
        EList<Service> services = definition.getEServices();
        List<IStatus> statusList = new ArrayList<IStatus>();
        IStatus status = null;
        String name = null;

        Collection<EObject> locus = new ArrayList<EObject>();
        locus.add(WSDLPackage.Literals.PORT__NAME);

        final List<IStatus> localList = new ArrayList<IStatus>();

        for (Service service : services) {
            final EList<Port> ports = service.getEPorts();

            localList.clear();

            final List<String> duplicateNames = new ArrayList<String>();

            for (Port port : ports) {
                name = port.getName();
                if (allNames.contains(name)) {
                    duplicateNames.add(name);
                    status = ConstraintStatus.createStatus(ctx, port, locus, Messages.UniquePortName_0, name);
                } else {
                    allNames.add(name);
                    status = ConstraintStatus.createSuccessStatus(ctx, port, null);
                }
                localList.add(status);
            }

            for (int i = 0; i < ports.size(); i++) {
                final Port port = ports.get(i);
                final String portName = port.getName();
                if (duplicateNames.contains(portName)) {
                    duplicateNames.remove(portName);
                    ConstraintStatus errorStatus = ConstraintStatus.createStatus(ctx, port, locus, Messages.UniquePortName_0,
                            portName);
                    localList.set(i, errorStatus);
                }
            }
            statusList.addAll(localList);
        }

        // adds the first elements to the status list


        return createStatus(ctx, statusList);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        Object target = ctx.getTarget();
        return (target instanceof Definition && isBatchValidation(ctx)) || (target instanceof Port && isLiveValidation(ctx));
    }

}
