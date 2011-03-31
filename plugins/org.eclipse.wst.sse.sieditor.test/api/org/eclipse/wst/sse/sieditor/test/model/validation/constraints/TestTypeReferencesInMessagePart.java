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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTypeReferencesInMessagePart extends SIEditorBaseTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/unresolvedPartTestCase.wsdl", //$NON-NLS-1$
                "unresolvedPartTestCase.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                    }
                });
    }

    @Test
    public void testToManyParatsInTheMessageWhenDocumentStyleIsSpecified() throws Exception {
        assertThereAreValidationErrorsPresent(4);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
