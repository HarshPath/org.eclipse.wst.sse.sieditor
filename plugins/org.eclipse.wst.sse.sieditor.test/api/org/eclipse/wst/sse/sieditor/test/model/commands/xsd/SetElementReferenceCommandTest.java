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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base.SetElementTypeAbstractCommandTest;

public class SetElementReferenceCommandTest extends SetElementTypeAbstractCommandTest {
    private static final String NEW_ELEMENT_NAME = "customElementReference";
    private static final String GLOBAL_ELEMENT_NAME = "CustomGlobalElement";

    @Override
    protected AbstractType getNewType() throws ExecutionException {
        IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final AddStructureTypeCommand cmd = new AddStructureTypeCommand(xsdModelRoot, schema, GLOBAL_ELEMENT_NAME, true,
                (AbstractType) Schema.getSchemaForSchema().getType(false, "date")) {
            @Override
            public boolean canRedo() {
                return false;
            }

            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(cmd);

        return (AbstractType) cmd.getStructureType();
    }

    @Override
    protected String getNewElementName() {
        return NEW_ELEMENT_NAME;
    }

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IWsdlModelRoot modelRoot) {
        super.assertPostRedoState(redoStatus, modelRoot);
        // checks if the dom model is properly updated

        XSDParticle component = (XSDParticle) element.getComponent();
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) component.getContent();
        Element element = xsdElementDeclaration.getElement();
        assertNotNull(element.getAttributeNode("ref"));
        assertNull(element.getAttributeNode("type"));
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IWsdlModelRoot modelRoot) {
        super.assertPostUndoState(undoStatus, modelRoot);
        // checks if the dom model is properly updated
        XSDParticle component = (XSDParticle) element.getComponent();
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) component.getContent();
        Element element = xsdElementDeclaration.getElement();
        assertNull(element.getAttributeNode("ref"));
        assertNotNull(element.getAttributeNode("type"));
    }
}