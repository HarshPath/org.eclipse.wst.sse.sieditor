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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractXSDComponent;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

public class SetStructureTypeCommandTest extends AbstractCommandTest {
    @Override
    protected String getWsdlFilename() {
        return "CopyTypeTestImportedSchema.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/self/mix/";
    }

    private static final String TARGET_NAMESPACE = "http://www.example.org/CopyTypeTest/Imported/ns1";

    private Schema schema;
    private IStructureType type;
    private IType newType;
    private SetStructureTypeCommand command;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final XSDTypeDefinition typeDefinition = ((XSDElementDeclaration) ((AbstractXSDComponent) type).getComponent())
                .getTypeDefinition();
        final XSDTypeDefinition newTypeDefinition = (XSDTypeDefinition) ((AbstractXSDComponent) newType).getComponent();
        assertEquals(newTypeDefinition, typeDefinition);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final XSDTypeDefinition typeDefinition = ((XSDElementDeclaration) ((AbstractXSDComponent) type).getComponent())
                .getTypeDefinition();
        final XSDTypeDefinition newTypeDefinition = (XSDTypeDefinition) ((AbstractXSDComponent) newType).getComponent();
        assertFalse(newTypeDefinition.equals(typeDefinition));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        type = (IStructureType) schema.getType(true, "myEl");
        newType = schema.getType(false, "GlobalComplexType");
        command = new SetStructureTypeCommand(modelRoot, type, newType);

        return command;
    }

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix/CopyTypeTestExternal.xsd", Document_FOLDER_NAME, this
                .getProject(), "CopyTypeTestExternal.xsd");
        refreshProjectNFile(file);

        return super.getModelRoot();
    }
}
