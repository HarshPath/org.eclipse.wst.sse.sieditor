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
package org.eclipse.wst.sse.sieditor.test.v2.ui.editor;

import java.io.IOException;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;

/**
 *
 * 
 */
public class SIEditorRefresNullTNS extends SIEditorBaseTest {

    private static final String EMPTY_STRING = "";
    private static final String TARGET_NAMESPACE_ATTR_NAME = "targetNamespace";

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor#reloadModelFromDOM()}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     * @throws ExecutionException
     */
    @Test
    public final void testReloadModelFromDOM() throws IOException, CoreException, ExecutionException {
        final IWsdlModelRoot model = getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_MIX2 + "PurchaseOrderConfirmation.wsdl",
                "experimentTNSwsdl.wsdl");
        assertEquals("http://sap.com/xi/Purchasing", model.getDescription().getNamespace());

        final Element definitionsElement = model.getDescription().getComponent().getElement();
        assertTrue(ElementAttributeUtils.hasAttributeValue(definitionsElement, TARGET_NAMESPACE_ATTR_NAME));
        definitionsElement.removeAttribute(TARGET_NAMESPACE_ATTR_NAME);
        assertFalse(ElementAttributeUtils.hasAttributeValue(definitionsElement, TARGET_NAMESPACE_ATTR_NAME));
        assertNotNull(model.getDescription().getNamespace());
        assertNotSame(EMPTY_STRING, model.getDescription().getNamespace());
        final AbstractWSDLNotificationOperation refreshCommand = new AbstractWSDLNotificationOperation(model, model
                .getDescription(), "test refresh") {
            @Override
            public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                EmfModelPatcher.instance().patchEMFModelAfterDomChange(model, new HashSet<Node>());
                return Status.OK_STATUS;
            }
        };
        model.getEnv().execute(refreshCommand);
        assertNull(model.getDescription().getNamespace());
    }
}
