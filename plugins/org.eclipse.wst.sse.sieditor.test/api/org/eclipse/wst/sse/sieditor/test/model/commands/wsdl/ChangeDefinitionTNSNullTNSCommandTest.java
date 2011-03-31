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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;


public class ChangeDefinitionTNSNullTNSCommandTest extends AbstractCommandTest {
    private static final String ORIGINAL_NAMESPACE_VALUE = UIConstants.EMPTY_STRING;
    private String originalNamespace;
    private String newNamespace;

    @SuppressWarnings("unchecked")
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final Description description = (Description) modelRoot.getDescription();
        final Definition definitions = description.getComponent();
        assertEquals(newNamespace, description.getNamespace());
        final Map namespaces = definitions.getNamespaces();
        assertEquals(newNamespace, namespaces.get(UIConstants.EMPTY_STRING));
        final Set<Entry<String, String>> entrySet = namespaces.entrySet();
        int counter = 0;
        for (final Entry<String, String> entry : entrySet) {
            if(newNamespace.equals(entry.getValue())){
                final String key = entry.getKey();
                if (entry.getKey() != null && !key.equals(UIConstants.EMPTY_STRING)) {
                    assertEquals("counts the namespace is containd", 1, ++counter);
                }
            }
        }
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final Description description = (Description) modelRoot.getDescription();
        final Definition definitions = description.getComponent();
        assertEquals(originalNamespace, description.getNamespace());
        assertFalse(definitions.getNamespaces().containsValue(newNamespace));
        final Map namespaces = definitions.getNamespaces();
        assertEquals(ORIGINAL_NAMESPACE_VALUE, namespaces.get(UIConstants.EMPTY_STRING));
        final Set<Entry<String, String>> entrySet = namespaces.entrySet();
        int counter = 0;
        for (final Entry<String, String> entry : entrySet) {
            if (ORIGINAL_NAMESPACE_VALUE.equals(entry.getValue())) {
                assertEquals("counts the namespace is containd", 1, ++counter);
            }
        }
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final Description descr = (Description) modelRoot.getDescription();
        originalNamespace = descr.getNamespace();
        assertEquals(null, originalNamespace);
        newNamespace = "http://www.emu.org/myNamespace";
        return new ChangeDefinitionTNSCompositeCommand(modelRoot, descr, newNamespace);
    }
    
    @Override
    protected String getWsdlFilename() {
        return "PurchaseOrderConfirmationNoTNS.wsdl";
    }
}
