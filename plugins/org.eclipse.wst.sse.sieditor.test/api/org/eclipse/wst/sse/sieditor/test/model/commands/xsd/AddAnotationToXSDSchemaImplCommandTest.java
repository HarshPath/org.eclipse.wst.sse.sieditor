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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAnotationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class AddAnotationToXSDSchemaImplCommandTest extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing"; //$NON-NLS-1$
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final XSDSchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getComponent();
        final EList<XSDAnnotation> annotations = schema.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.size() == 1);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final XSDSchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getComponent();
        final EList<XSDAnnotation> annotations = schema.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.size() == 0);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        
        final EList<XSDAnnotation> annotations = schema.getComponent().getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.size() == 0);
        
        return new AddAnotationCommand(schema.getComponent(), xsdModelRoot, schema);
    }

}
