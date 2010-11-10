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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;

public class ModelReconcileRegistry implements IModelReconcileRegistry {

    private boolean needsReconciling;
    private final List<XSDSchema> changedSchemas;

    public ModelReconcileRegistry() {
        this.needsReconciling = false;
        this.changedSchemas = new LinkedList<XSDSchema>();
    }

    @Override
    public void clearRegistry() {
        this.needsReconciling = false;
        this.changedSchemas.clear();
    }

    @Override
    public void addChangedSchema(final XSDSchema changedSchema) {
        if (changedSchema == null) {
            return;
        }
        if (!changedSchemas.contains(changedSchema)) {
            this.changedSchemas.add(changedSchema);
        }
    }

    @Override
    public List<XSDSchema> getChangedSchemas() {
        return Collections.unmodifiableList(changedSchemas);
    }

    @Override
    public boolean needsReconciling() {
        return needsReconciling;
    }

    @Override
    public void setNeedsReconciling(final boolean needsReconciling) {
        this.needsReconciling = needsReconciling;
    }

}
