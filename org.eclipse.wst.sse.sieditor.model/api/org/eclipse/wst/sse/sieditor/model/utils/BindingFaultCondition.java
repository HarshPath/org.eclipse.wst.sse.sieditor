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
package org.eclipse.wst.sse.sieditor.model.utils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.Fault;

public class BindingFaultCondition extends EObjectCondition {

    private Fault fault;

    public BindingFaultCondition(Fault fault) {
        this.fault = fault;
    }

    @Override
    public boolean isSatisfied(EObject eObject) {

        return fault != null && (eObject instanceof BindingFault) && fault.equals(((BindingFault) eObject).getFault());
    }

}
