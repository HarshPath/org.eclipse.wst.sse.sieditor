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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class DeleteSetCommand extends CompositeCommand {

    private static Set<IModelObject> objects = null;

    public DeleteSetCommand(IModelRoot root, Collection<IModelObject> parents, Collection<IModelObject> objectsToRemove) {
        super(root, setUpObjectsForRefresh(parents, objectsToRemove), Messages.DeleteSetCommand_Delete_lbl);

        if (objects == null) {
            objects = findRootItems(objectsToRemove);
        }

        DeleteCommandsFinder finder = new DeleteCommandsFinder(root, objects);

        setOperations(finder.getDeleteCommands());
        setTransactionPolicy(TransactionPolicy.MULTI);
    }

    private static Collection<IModelObject> setUpObjectsForRefresh(Collection<IModelObject> parents,
            Collection<IModelObject> objectsToRemove) {
        objects = findRootItems(objectsToRemove);
        Collection<IModelObject> objectForRefresh = new ArrayList<IModelObject>(objects);
        objectForRefresh.addAll(parents);
        return objectForRefresh;
    }

    protected static Set<IModelObject> findRootItems(Collection<IModelObject> items) {
        Set<IModelObject> rootItems = new HashSet<IModelObject>();
        rootItems.addAll(items);
        for (IModelObject item : items) {
            if (containsParent(rootItems, item)) {
                rootItems.remove(item);
            }

        }
        return rootItems;
    }

    private static boolean containsParent(Set<IModelObject> rootItems, IModelObject item) {

        IModelObject parent = item.getParent();

        if (parent == null) {
            return false;
        }
        if (rootItems.contains(parent) || containsParent(rootItems, parent)) {
            return true;
        }
        return false;

    }

}
