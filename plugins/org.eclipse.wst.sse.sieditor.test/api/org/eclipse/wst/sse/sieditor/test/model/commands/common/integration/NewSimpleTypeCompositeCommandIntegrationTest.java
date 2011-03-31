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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.integration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewSimpleTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.DefaultSetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class NewSimpleTypeCompositeCommandIntegrationTest extends AbstractCommandTest {

    private static final String ELEMENT_BASE_TYPE = "anyType";
    private Schema schema;
    private IStructureType structureType;
    private NewSimpleTypeCompositeCommand command;

    // ===========================================================
    // constants
    // ==========================================================

    private static final String TARGET_NAMESPACE = "http://www.example.org/CommonEntities/";
    private static final String NEW_ELEMENT_NAME = "newelementname" + System.currentTimeMillis();
    private static final String ELEMENT_TYPE_NAME = "Element1";

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        structureType = (IStructureType) schema.getType(true, ELEMENT_TYPE_NAME);

        assertNull(structureType.getType().getName());
        assertEquals(ELEMENT_BASE_TYPE, structureType.getBaseType().getName());

        command = new NewSimpleTypeCompositeCommand(modelRoot, structureType, "", schema, NEW_ELEMENT_NAME,
                new DefaultSetTypeCommandBuilder(structureType));
        return command;
    }

    @Override
    protected String getWsdlFilename() {
        return "CommonEntities.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return Constants.DATA_PUBLIC_SIMPLE;
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(NEW_ELEMENT_NAME, structureType.getType().getName());
        final ISimpleType createdType = (ISimpleType) modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(false,
                NEW_ELEMENT_NAME);
        assertNotNull(createdType);
        assertEquals(operationHistory.getUndoHistory(undoContext).length, 1);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNull(structureType.getType().getName());
        assertEquals(ELEMENT_BASE_TYPE, structureType.getBaseType().getName());
        final ISimpleType createdType = (ISimpleType) modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(false,
                NEW_ELEMENT_NAME);
        assertNull(createdType);
    }

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix/CopyTypeTestExternal.xsd", Document_FOLDER_NAME,
                this.getProject(), "CopyTypeTestExternal.xsd");
        refreshProjectNFile(file);

        return super.getModelRoot();
    }

}
