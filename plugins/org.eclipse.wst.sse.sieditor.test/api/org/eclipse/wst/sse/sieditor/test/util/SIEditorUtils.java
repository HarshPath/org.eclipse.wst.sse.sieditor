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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.test.util;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;

import org.eclipse.wst.sse.sieditor.model.api.INamedObject;

@SuppressWarnings("nls")
public class SIEditorUtils {

    public static void validateNamedComponents(final Collection<? extends INamedObject> components, final Collection<String> names) {

        ArrayList<String> namesList = new ArrayList<String>(names);

        for (INamedObject component : components) {
            namesList.remove(component.getName());
        }

        Assert.assertTrue(namesList.toString() + " not found in the list of components", namesList.size() == 0);

    }

    public static <T extends INamedObject> Collection<T> findNamedObjects(Collection<T> components, String name) {
        final Collection<T> result = new ArrayList<T>();
        for (T component : components) {
            if (name.equals(component.getName()))
                result.add(component);
        }
        return result;
    }

    public static <T extends INamedObject> T findNamedObject(Collection<T> components, String name) {
        for (T component : components) {
            if (name.equals(component.getName()))
                return component;
        }
        return null;
    }

}
