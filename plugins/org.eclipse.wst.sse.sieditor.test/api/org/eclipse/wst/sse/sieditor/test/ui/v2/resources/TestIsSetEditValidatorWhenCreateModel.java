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
package org.eclipse.wst.sse.sieditor.test.ui.v2.resources;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EnvironmentFactory;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.preedit.EditValidator;

public class TestIsSetEditValidatorWhenCreateModel extends SIEditorBaseTest {
    private IWsdlModelRoot modelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLWithRPCandDOcumentStyleBindings.wsdl", //$NON-NLS-1$
                "WSDLWithRPCandDOcumentStyleBindings.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$
    }

    @Test
    public void testToManyParatsInTheMessageWhenDocumentStyleIsSpecified() throws Exception {
        assertEquals(EditValidator.class, (((EnvironmentFactory.EnvironmentImpl) (modelRoot.getEnv())).getEditValidator())
                .getClass());
    }
}
