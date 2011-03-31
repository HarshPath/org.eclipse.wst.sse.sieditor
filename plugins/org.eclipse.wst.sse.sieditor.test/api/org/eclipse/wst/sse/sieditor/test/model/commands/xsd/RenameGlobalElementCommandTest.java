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

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base.RenameNamedComponentAbstractTest;

public class RenameGlobalElementCommandTest extends RenameNamedComponentAbstractTest {
    private static final String INITIAL_TYPE_NAME = "myComplexTypeToBeRenamed";
    private static final String NEW_TYPE_NAME = "myComplexTypeAlreadyBeRenamed";

    @Override
    protected IType addNewType(final IWsdlModelRoot modelRoot, final Schema schema) throws ExecutionException {
    	final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final AddStructureTypeCommand addTypecmd = new AddStructureTypeCommand(xsdModelRoot, schema, INITIAL_TYPE_NAME, true, null) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(addTypecmd);

        return addTypecmd.getStructureType();
    }

    @Override
    protected String getNewName() {
        return NEW_TYPE_NAME;
    }
}
