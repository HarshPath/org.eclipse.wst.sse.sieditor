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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

@SuppressWarnings("nls")
public class RemoveMixedSetCommandTest extends AbstractCommandTest {

    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private Schema schema;
    private final String structType = "NewStructure";
    private final String simpleType = "NewSimple";
    private final String attribute = "NewAttribute";
    private final String serviceInterf = "NewServiceInterface";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertNull(schema.getType(false, structType));
        assertNull(schema.getType(false, simpleType));
        assertTrue(modelRoot.getDescription().getInterface(serviceInterf).isEmpty());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNotNull(schema.getType(false, structType));
        assertNotNull(schema.getType(false, simpleType));
        assertFalse(modelRoot.getDescription().getInterface(serviceInterf).isEmpty());
        assertNotNull(modelRoot.getDescription().getInterface(serviceInterf).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);

        schema = (Schema) schemas[0];

        final Collection<IModelObject> addNewObjects = addNewObjects(modelRoot, schema);

        assertNotNull(schema.getType(false, structType));
        assertNotNull(schema.getType(false, simpleType));
        assertFalse(modelRoot.getDescription().getInterface(serviceInterf).isEmpty());
        assertNotNull(modelRoot.getDescription().getInterface(serviceInterf).get(0));
        return new DeleteSetCommand(modelRoot, (Collection) Arrays.asList(schema), addNewObjects);
    }

    protected Collection<IModelObject> addNewObjects(final IWsdlModelRoot modelRoot, final ISchema schema) throws ExecutionException {

        final Collection<IModelObject> addedObjects = new ArrayList<IModelObject>();

        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());

        final AddStructureTypeCommand addTypecmd = new AddStructureTypeCommand(xsdModelRoot, schema, structType, false, null) {

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(addTypecmd);
        final StructureType structureType = addTypecmd.getStructureType();
        addedObjects.add(structureType);

        final AddSimpleTypeCommand addSimple = new AddSimpleTypeCommand(xsdModelRoot, schema, simpleType) {

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        modelRoot.getEnv().execute(addSimple);
        addedObjects.add(addSimple.getSimpleType());

        final AddAttributeCommand addAttribute = new AddAttributeCommand(xsdModelRoot, structureType, attribute) {

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(addAttribute);
        addedObjects.add(addAttribute.getAttribute());

        final AddServiceInterfaceCommand addService = new AddServiceInterfaceCommand(modelRoot, modelRoot.getDescription(),
                serviceInterf) {

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        modelRoot.getEnv().execute(addService);
        addedObjects.add(addService.getInterface());

        return addedObjects;

    }

    @Test
    public void testCannotExecuteCommand() {
        final IEnvironment envMock = createMock(IEnvironment.class);
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        final IModelRoot modelRootMock = createMock(IWsdlModelRoot.class);
        final IModelObject parentMock = createMock(IModelObject.class);

        expect(modelObjectMock.getParent()).andReturn(null).anyTimes();

        expect(modelRootMock.getEnv()).andReturn(envMock).anyTimes();
        expect(envMock.getEditingDomain()).andReturn(null).anyTimes();
        replay(modelRootMock, envMock, modelObjectMock);

        final DeleteSetCommand command = new DeleteSetCommand(modelRootMock, Arrays.asList(parentMock), Arrays.asList(modelObjectMock));
        Assert.assertFalse(command.canExecute());

    }
}
