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
package org.eclipse.wst.sse.sieditor.test.model.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class TestEmfWSDLUtils extends SIEditorBaseTest {

    private static final String WSDL_FILE_NAME = "PurchaseOrderConfirmation.wsdl";
    private static final String TNS_ATTRIBUT_NAME = "targetNamespace";
    private static final String TNS_VALUE_HTTP_SAP_COM_XI_PURCHASING = "http://sap.com/xi/Purchasing";

    @Test
    public void testcheckTargetNamespaceAttribute() throws IOException, CoreException, ExecutionException {
        final IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + WSDL_FILE_NAME, "One.wsdl");

        final AddNewSchemaCommand command = new AddNewSchemaCommand(wsdlModelRoot, TNS_VALUE_HTTP_SAP_COM_XI_PURCHASING);

        command.execute(new NullProgressMonitor(), null);

        final ISchema iSchema = command.getNewSchema();
        final Element schemaElement = iSchema.getComponent().getElement();

        assertEquals(TNS_VALUE_HTTP_SAP_COM_XI_PURCHASING, schemaElement.getAttribute(TNS_ATTRIBUT_NAME));
        assertEquals(TNS_VALUE_HTTP_SAP_COM_XI_PURCHASING, iSchema.getNamespace());

        schemaElement.removeAttribute(TNS_ATTRIBUT_NAME);

        final Set<Node> hashSet = new HashSet<Node>();
        hashSet.add(schemaElement);
        final AbstractNotificationOperation patchCommand = new AbstractNotificationOperation(wsdlModelRoot, iSchema, "testCommand") {

            @Override
            public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                EmfModelPatcher.instance().patchEMFModelAfterDomChange(wsdlModelRoot, hashSet);
                return Status.OK_STATUS;
            }

        };
        patchCommand.execute(new NullProgressMonitor(), null);

        assertEquals(UIConstants.EMPTY_STRING, schemaElement.getAttribute(TNS_ATTRIBUT_NAME));
        assertNotSame(TNS_VALUE_HTTP_SAP_COM_XI_PURCHASING, iSchema.getNamespace());
    }
}
