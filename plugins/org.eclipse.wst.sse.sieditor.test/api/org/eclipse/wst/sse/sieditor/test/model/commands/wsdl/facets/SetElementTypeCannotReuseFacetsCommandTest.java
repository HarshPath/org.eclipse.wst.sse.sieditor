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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.facets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * This test covers the following test case: <br>
 * <ol>
 * <li>We have global element GlobalElement</li>
 * <li>GlobalElement has child element ChildElement</li>
 * <li>ChildElement is of anonymous type and has some restriction over the int
 * base type</li>
 * <li>We are changing the type of ChildElement to string</li>
 * <li>ChildElement should no longer be of anonymous type, but should be of type
 * string</li>
 * </ol>
 * 
 *
 * 
 */
public class SetElementTypeCannotReuseFacetsCommandTest extends AbstractCommandTest {

    public static final String TARGET_NAMESPACE = "set_type_command_test_ns";
    public static final String GLOBAL_ELEMENT_NAME = "GlobalElement";
    public static final String CHILD_ELEMENT_NAME = "ChildElement";

    private IStructureType globalElement;
    private IElement childElement;

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IWsdlModelRoot modelRoot) {
        assertFalse(childElement.getType().isAnonymous());
        assertEquals("string", childElement.getType().getName());
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IWsdlModelRoot modelRoot) {
        assertTrue(childElement.getType().isAnonymous());
        assertNull(childElement.getType().getName());
        assertEquals("int", childElement.getType().getBaseType().getName());
    }

    @Override
    protected AbstractNotificationOperation getOperation(IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());

        globalElement = (IStructureType) schema.getType(true, GLOBAL_ELEMENT_NAME);
        childElement = globalElement.getElements(CHILD_ELEMENT_NAME).iterator().next();

        assertTrue(childElement.getType().isAnonymous());
        assertEquals("int", childElement.getType().getBaseType().getName());
        assertNull(childElement.getType().getName());

        return new SetElementTypeCommand(schema.getModelRoot(), childElement, Schema.getDefaultSimpleType());
    }

    @Override
    protected String getWsdlFilename() {
        return "SetSimpleTypeFacetsCommandTestWSDLFile.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/facets/";
    }

}
