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
package org.eclipse.wst.sse.sieditor.core.common;

import java.util.List;

import org.eclipse.xsd.XSDSchema;

public interface IModelReconcileRegistry {

    public void clearRegistry();

    public void setNeedsReconciling(boolean needsReconciling);

    public boolean needsReconciling();

    public void addChangedSchema(XSDSchema changedSchema);

    public List<XSDSchema> getChangedSchemas();

}
