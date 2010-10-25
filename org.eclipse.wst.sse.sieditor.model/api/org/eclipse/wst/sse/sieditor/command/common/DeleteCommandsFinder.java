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
package org.eclipse.wst.sse.sieditor.command.common;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RemoveSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RemoveTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class DeleteCommandsFinder {

    private Collection<IModelObject> items;
    private IModelRoot model;
    // private Set<IModelObject> parents = new HashSet<IModelObject>();
    private LinkedList<AbstractNotificationOperation> deleteCommands = new LinkedList<AbstractNotificationOperation>();

    public DeleteCommandsFinder(IModelRoot model, Collection<IModelObject> items) {
        this.items = items;
        this.model = model;

        createDeleteCommands();
    }

    public LinkedList<AbstractNotificationOperation> getDeleteCommands() {
        return deleteCommands;
    }

    private void createDeleteCommands() {

        for (IModelObject item : items) {

            AbstractNotificationOperation cmd = null;
            if (model instanceof IWsdlModelRoot) {
                if (item instanceof IServiceInterface) {
                    IDescription description = ((IWsdlModelRoot) model).getDescription();
                    cmd = new DeleteServiceInterfaceCommand((IWsdlModelRoot) model, description, (IServiceInterface) item);

                } else if (item instanceof IOperation) {
                    IOperation operation = (IOperation) item;
                    cmd = new DeleteOperationCommand((IWsdlModelRoot) model, (IServiceInterface) operation.getParent(), operation);

                } else if (item instanceof IFault) {

                    IFault fault = (IFault) item;
                    IOperation operation = (IOperation) fault.getParent();
                    cmd = new DeleteFaultCommand((IWsdlModelRoot) model, (IOperation) operation, fault);

                } else if (item instanceof IParameter) {

                    IParameter parameter = (IParameter) item;
                    IOperation operation = (IOperation) item.getParent();

                    if (parameter.getParameterType() == IParameter.INPUT) {
                        cmd = new DeleteInParameterCommand((IWsdlModelRoot) model, operation, parameter);

                    } else {
                        cmd = new DeleteOutParameterCommand((IWsdlModelRoot) model, operation, parameter);
                    }
                } else if (item instanceof ISchema) {

                    cmd = new RemoveSchemaCommand((IWsdlModelRoot) model, (ISchema) item);

                }
            }
            if (item instanceof IType) {
                IType type = (IType) item;
                cmd = new RemoveTypeCommand(model, type.getParent(), type);
            }

            else if (item instanceof IElement && ((IElement) item).getParent() instanceof IStructureType) {
                IElement element = (IElement) item;
                cmd = new DeleteElementCommand(model, (IStructureType) element.getParent(), element);
            }

            deleteCommands.add(cmd);
        }

    }
}
