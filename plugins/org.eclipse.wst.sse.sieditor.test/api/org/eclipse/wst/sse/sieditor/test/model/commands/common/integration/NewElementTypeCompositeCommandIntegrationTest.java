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
import org.eclipse.wst.sse.sieditor.command.emf.common.NewElementTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.DefaultSetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class NewElementTypeCompositeCommandIntegrationTest extends AbstractCommandTest {

    private Schema schema;
    private IStructureType structureType;
    private IElement element;
    private NewElementTypeCompositeCommand command;

    // ===========================================================
    // constants
    // ==========================================================

    private static final String TARGET_NAMESPACE = "http://www.example.org/CommonEntities/";
    private static final String NEW_ELEMENT_NAME = "newelementname" + System.currentTimeMillis();
    private static final String STRUCTURE_TYPE_ELEMENT_NAME = "StructureType1";

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        structureType = (IStructureType) schema.getType(false, STRUCTURE_TYPE_ELEMENT_NAME);
        element = structureType.getAllElements().iterator().next();

        command = new NewElementTypeCompositeCommand(modelRoot, element, "", schema, NEW_ELEMENT_NAME,
                new DefaultSetTypeCommandBuilder(element));
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
        assertEquals(1, structureType.getElements(NEW_ELEMENT_NAME).size());
        assertNotNull(modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(true, NEW_ELEMENT_NAME));
        assertEquals(operationHistory.getUndoHistory(undoContext).length, 1);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, structureType.getElements(NEW_ELEMENT_NAME).size());
        assertNull(modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(true, NEW_ELEMENT_NAME));
    }

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix/CopyTypeTestExternal.xsd", Document_FOLDER_NAME,
                this.getProject(), "CopyTypeTestExternal.xsd");
        refreshProjectNFile(file);

        return super.getModelRoot();
    }

}
