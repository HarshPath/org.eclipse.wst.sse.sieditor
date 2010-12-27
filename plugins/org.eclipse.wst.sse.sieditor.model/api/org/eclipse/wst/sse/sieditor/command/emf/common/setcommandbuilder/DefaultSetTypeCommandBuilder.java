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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeBaseTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * this is the default implementation of the {@link ISetTypeCommandBuilder}
 * interface
 * 
 */
public class DefaultSetTypeCommandBuilder implements ISetTypeCommandBuilder {

    private final IModelObject modelObject;

    public DefaultSetTypeCommandBuilder(final IModelObject modelObject) {

        this.modelObject = modelObject;
    }

    public AbstractNotificationOperation createSetTypeCommand(final IType newType) {
        if (modelObject instanceof IElement) {
            return new SetElementTypeCommand(((IElement) modelObject).getModelRoot(), (IElement) modelObject, newType);
        }
        if (modelObject instanceof IStructureType && ((IStructureType) modelObject).isElement()) {
            return new SetStructureTypeCommand(((IStructureType) modelObject).getModelRoot(), (IStructureType) modelObject,
                    newType);
        }
        if (modelObject instanceof IStructureType && !((IStructureType) modelObject).isElement()) {
            return new SetStructureTypeBaseTypeCompositeCommand(((IStructureType) modelObject).getModelRoot(), (IStructureType) modelObject,
                    newType);
        }
        if (modelObject instanceof ISimpleType) {
            return new SetBaseTypeCommand(((ISimpleType) modelObject).getModelRoot(), (ISimpleType) modelObject, newType);
        }
        if (modelObject instanceof IParameter) {
            return new SetParameterTypeCommand((IParameter) modelObject, newType);
        }
        throw new IllegalStateException("given modelObject is not recognized: " + modelObject); //$NON-NLS-1$
    }

}
