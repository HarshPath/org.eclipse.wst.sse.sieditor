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
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementDefaultValueCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class SetElementDefaultValueCommandTest extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://www.example.org/NewWSDLFile/";
    private static final String GLOBAL_ELEMENT_NAME = "Element1";
    private static final String ELEMENT_INT_DEF_VALUE = "1";
    
    private IElement elementInt;
    
    protected String getWsdlFilename() {
        return "NewWSDLFile.wsdl";
    }

    protected String getWsdlFoldername() {
        return "pub/simple/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
    	XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) ((XSDParticle)elementInt.getComponent()).getContent();
    	assertEquals("Default value not set to element.", ELEMENT_INT_DEF_VALUE, elementDeclaration.getValue().toString());
    	
    	String defaultAttributeValue = elementDeclaration.getElement().getAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
    	assertEquals("Default value not set to DOM attribute.",ELEMENT_INT_DEF_VALUE, defaultAttributeValue);
    	
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
    	XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) ((XSDParticle)elementInt.getComponent()).getContent();
    	assertEquals("Default value not unset to element.", null, elementDeclaration.getValue());
    	
    	String defaultAttributeValue = elementDeclaration.getElement().getAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
    	assertEquals("Default value not unset to DOM attribute.", null, defaultAttributeValue);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        StructureType element1 = (StructureType) schemas[0].getType(true, GLOBAL_ELEMENT_NAME);
        elementInt = element1.getElements("ElementInt").iterator().next();
        
        return new SetElementDefaultValueCommand(modelRoot, elementInt, ELEMENT_INT_DEF_VALUE);
    }
}
