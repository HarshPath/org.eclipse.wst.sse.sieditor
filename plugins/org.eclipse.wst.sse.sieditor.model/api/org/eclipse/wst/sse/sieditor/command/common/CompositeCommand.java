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
import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

public class CompositeCommand extends AbstractCompositeNotificationOperation {

    private LinkedList<AbstractNotificationOperation> operations;

    /**
     * 
     * @param root
     * @param objects
     *            elements to be deleted
     */
    public CompositeCommand(IModelRoot root, Collection<IModelObject> parents,
            LinkedList<AbstractNotificationOperation> operations, String label) {

        super(root, parents.toArray(new IModelObject[parents.size()]), label);
        this.operations = operations;

    }

    public CompositeCommand(IModelRoot root, Collection<IModelObject> parents, String label) {

        super(root, parents.toArray(new IModelObject[parents.size()]), label);
        this.operations = new LinkedList<AbstractNotificationOperation>();

    }

    public void setOperations(LinkedList<AbstractNotificationOperation> operations) {
        this.operations = operations;
    }

    @Override
    public boolean canExecute() {
        if (operations == null || operations.isEmpty()) {
            return false;
        }
        for (AbstractNotificationOperation operation : operations) {

            if (operation == null) {
                return false;
            }
            if (!operation.canExecute()) {
                return false;
            }

        }
        return true;
    }

    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        if (operations == null || operations.isEmpty()) {
            return null;
        }
        return operations.removeFirst();
    }

}
