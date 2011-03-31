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
package org.eclipse.wst.sse.sieditor.test.model.commands;

import java.util.List;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestRenamePartCommandWithMultiReferredPart extends SIEditorBaseTest {
    private IDescription modelDescription;
    private IParameter multiReferredParameter;
    private IOperation theSecondOperation;
    private String oldNameOfThePartInTheSecondOperation;
    private IWsdlModelRoot modelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = (IWsdlModelRoot) getModelRoot("pub/csns/renameMultiReferredPart.wsdl", //$NON-NLS-1$
                "renameMultiReferredPart.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        final IOperation newOperation = modelDescription.getInterface("NewWSDLFile").get(0) //$NON-NLS-1$
                .getOperation("NewOperation").get(0); //$NON-NLS-1$

        theSecondOperation = modelDescription.getInterface("NewWSDLFile").get(0) //$NON-NLS-1$
                .getOperation("NewOperation1").get(0); //$NON-NLS-1$

        oldNameOfThePartInTheSecondOperation = ((List<IParameter>) theSecondOperation.getAllInputParameters()).get(1).getName();

        multiReferredParameter = ((List<IParameter>) newOperation.getAllInputParameters()).get(1);

        assertEquals(multiReferredParameter.getName(), oldNameOfThePartInTheSecondOperation);
        modelRoot.getEnv().execute(new RenameParameterCommand(modelRoot, multiReferredParameter, "newNameOfTheParameter")); //$NON-NLS-1$

    }

    @Test
    public void testThatNamesOfTheParametersInTheUITreeAreTheSameWhenReferToSameMessagePart() throws Exception {
        String theNewNameOfMultiReferredPart = multiReferredParameter.getName();

        assertNotSame(theNewNameOfMultiReferredPart, oldNameOfThePartInTheSecondOperation);

        IParameter parameterInTheSecondOperationWhichReferToTheSamePart = ((List<IParameter>) theSecondOperation
                .getAllInputParameters()).get(1);

        assertEquals(parameterInTheSecondOperationWhichReferToTheSamePart.getName(), theNewNameOfMultiReferredPart);
    }
}
