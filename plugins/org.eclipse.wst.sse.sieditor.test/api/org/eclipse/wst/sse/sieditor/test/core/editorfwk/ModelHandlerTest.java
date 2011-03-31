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
package org.eclipse.wst.sse.sieditor.test.core.editorfwk;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.editorfwk.IModelObject;
import org.eclipse.wst.sse.sieditor.core.editorfwk.ModelHandler;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ModelHandlerTest extends SIEditorBaseTest {

    @Test
    public void testLoadXSDfromFile() throws IOException, CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        StringBuffer sb = new StringBuffer();
        BufferedInputStream bis = new BufferedInputStream(file.getContents());
        int readedByte;
        while ((readedByte = bis.read()) > 0) {
            sb.append((char) readedByte);
        }

        String fileContent = sb.toString();
        IModelObject modelObject = ModelHandler.retrieveModelObject(fileContent, file.getLocationURI().toString());

        assertTrue(modelObject instanceof IXSDModelRoot);
    }

    @Test
    public void testLoadWSDLfromFile() throws IOException, CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl",
                Document_FOLDER_NAME, this.getProject(), "ChangePurchaseOrder_WSD.wsdl");
        refreshProjectNFile(file);

        StringBuffer sb = new StringBuffer();
        BufferedInputStream bis = new BufferedInputStream(file.getContents());
        int readedByte;
        while ((readedByte = bis.read()) > 0) {
            sb.append((char) readedByte);
        }

        String fileContent = sb.toString();
        IModelObject modelObject = ModelHandler.retrieveModelObject(fileContent, file.getLocationURI().toString());

        assertTrue(modelObject instanceof IWsdlModelRoot);
    }

    @Test
    public void testOperationHistoryNotClearedAfterRetrivingModelObject() throws IOException, CoreException, ExecutionException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl",
                "ChangePurchaseOrder_WSD.wsdl");
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        IDescription description = wsdlModelRoot.getDescription();
        ISchema schema = description.getSchema("http://sap.com/xi/SAPGlobal20/Global")[0];
        SetNamespaceCommand cmd = new SetNamespaceCommand(wsdlModelRoot, schema, "testChange");
        IEnvironment env = wsdlModelRoot.getEnv();
        assertEquals(Status.OK_STATUS, env.execute(cmd));
        assertEquals(1, env.getOperationHistory().getUndoHistory(env.getUndoContext()).length);
        assertTrue(env.getOperationHistory().canUndo(env.getUndoContext()));

        ModelHandler.retrieveModelObject(env, URI.createFileURI(file.getLocation().toOSString()), false);
        assertEquals(1, env.getOperationHistory().getUndoHistory(env.getUndoContext()).length);
        assertTrue(env.getOperationHistory().canUndo(env.getUndoContext()));
    }
}
