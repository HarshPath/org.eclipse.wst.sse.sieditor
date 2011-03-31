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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base.SetElementTypeAbstractCommandTest;

public class SetElementCustomSimpleTypeCommandTest extends SetElementTypeAbstractCommandTest {
    private static final String NEW_ELEMENT_NAME = "customSimpleTypeElement";
    private static final String SIMPLE_TYPE_NAME = "CustomElementsSimpleType";

    @Override
    protected AbstractType getNewType() throws ExecutionException {
    	IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final AddSimpleTypeCommand cmd = new AddSimpleTypeCommand(xsdModelRoot, schema, SIMPLE_TYPE_NAME) {
            @Override
            public boolean canRedo() {
                return false;
            }
            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(cmd);

        return (AbstractType) cmd.getSimpleType();
    }

    @Override
    protected String getNewElementName() {
        return NEW_ELEMENT_NAME;
    }
}
