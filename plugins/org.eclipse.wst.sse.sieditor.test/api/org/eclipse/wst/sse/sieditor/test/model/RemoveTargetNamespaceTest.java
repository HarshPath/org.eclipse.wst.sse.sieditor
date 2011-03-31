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
package org.eclipse.wst.sse.sieditor.test.model;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class RemoveTargetNamespaceTest extends SIEditorBaseTest {

    @Test
    public void testXsdCase() throws ExecutionException {
        IXSDModelRoot xsdModelRoot = null;
        try {
            xsdModelRoot = getXSDModelRoot("pub/xsd/example2.xsd", "test with space.xsd");
        } catch (Exception e) {
            fail(e.toString());
        }

        SetNamespaceCommand setNamespaceCommand = new SetNamespaceCommand(xsdModelRoot, xsdModelRoot.getSchema(), "");
        xsdModelRoot.getEnv().execute(setNamespaceCommand);
        assertNull(xsdModelRoot.getSchema().getComponent().getTargetNamespace());

    }

    @Test
    public void testWsdlCase() throws Exception {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("tns/wsdl/wsdlWithoutTns.wsdl", "wsdlWithoutTns.wsdl");
        AddNewSchemaCommand addNewSchemaCommand = new AddNewSchemaCommand(wsdlModelRoot, "dummy");
        IEnvironment env = wsdlModelRoot.getEnv();
        env.execute(addNewSchemaCommand);
        ISchema newSchema = addNewSchemaCommand.getNewSchema();
        SetNamespaceCommand setNamespaceCommand = new SetNamespaceCommand(wsdlModelRoot, newSchema, "");
        env.execute(setNamespaceCommand);
        // fails because the model is not loaded from a real opened editor
        // TODO write a command test for undo/redo on this command
        // assertTrue(env.getOperationHistory().undo(env.getUndoContext(), new
        // NullProgressMonitor(), null).isOK());
        // assertEquals("dummy", newSchema.getNamespace());
    }
}
