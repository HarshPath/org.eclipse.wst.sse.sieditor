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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

/**
 * Command for setting MinOccurs and MaxOccurs for {@link XSDParticle}
 * 
 * 
 */
public class SetElementOccurences extends AbstractNotificationOperation {
    private final XSDConcreteComponent _particle;
    private final int _minOccurs;
    private final int _maxOccurs;
  

    public SetElementOccurences(final IModelRoot root, final IElement element, final int minOccurs, final int maxOccurs) {
        super(root, element, Messages.SetElementOccurences_set_element_occurences_label);
        this._particle = element.getComponent();
        this._minOccurs = minOccurs;
        this._maxOccurs = maxOccurs;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        // set min and max occurs for the particle
      
            ((XSDParticle) _particle).setMinOccurs(_minOccurs);
            ((XSDParticle) _particle).setMaxOccurs(_maxOccurs);
            
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return _particle != null && _particle instanceof XSDParticle;
    }

}
