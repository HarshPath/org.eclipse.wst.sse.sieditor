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
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * this is the interface of the set type command builders. implementors are
 * responsible for the creation of the set type command
 * 
 */
public interface ISetTypeCommandBuilder {

    /**
     * create new set type command instance
     * 
     * @param newType
     *            - the new type to be set
     * @return the created set type command instance
     */
    public AbstractNotificationOperation createSetTypeCommand(final IType newType);

}
