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
package org.eclipse.wst.sse.sieditor.model;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.command.Command;

import org.eclipse.wst.sse.sieditor.command.common.TextCommandWrapper;
import org.eclipse.wst.sse.sieditor.core.common.ICommandStackListener;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

public class SIECommandStackListener implements ICommandStackListener {

    private final IEnvironment env;
    private final IModelRoot result;
    private final XMLModelNotifierWrapper modelNotifier;

    public SIECommandStackListener(final IEnvironment env, final IModelRoot result, final XMLModelNotifierWrapper modelNotifier) {
        this.env = env;
        this.result = result;
        this.modelNotifier = modelNotifier;
    }

    public void commandToBeExecuted(final Command command) {
        final TextCommandWrapper textCommand = new TextCommandWrapper(result, result.getModelObject(), command, modelNotifier);
        try {
            env.execute(textCommand);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}