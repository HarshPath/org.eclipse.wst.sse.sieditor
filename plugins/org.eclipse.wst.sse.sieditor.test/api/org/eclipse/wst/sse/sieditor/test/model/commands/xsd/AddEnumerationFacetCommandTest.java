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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDPackage;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class AddEnumerationFacetCommandTest extends AbstractCommandTest {
	
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String NEW_TYPE_NAME = "myNewFacetedSimpleType";
    private static final String ENUM_FACET_VALUE = "EnumValue";    
	
    private SimpleType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
    	final IFacet[] enumerations = type.getEnumerations();
    	assertNotNull(enumerations);
    	assertEquals(1, enumerations.length);
        assertEquals(ENUM_FACET_VALUE, enumerations[0].getValue());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
    	final IFacet[] enumerations = type.getEnumerations();
    	assertTrue((enumerations==null) || (enumerations.length==0));
    }
    
    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schemas[0].getComponent());
        final AddSimpleTypeCommand cmd = new AddSimpleTypeCommand(xsdModelRoot, schemas[0], NEW_TYPE_NAME) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        modelRoot.getEnv().execute(cmd);
        type = cmd.getSimpleType();

        return new AddFacetCommand(modelRoot, type, getXSDPackage().getXSDEnumerationFacet(), null, ENUM_FACET_VALUE);
    }    
}
