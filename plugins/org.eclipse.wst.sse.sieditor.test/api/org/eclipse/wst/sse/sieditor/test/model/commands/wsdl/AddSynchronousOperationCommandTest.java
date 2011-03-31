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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

public class AddSynchronousOperationCommandTest extends AbstractCommandTest {
    private static final String ADDED_OPERATION_NAME = "addedOperation";

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/services/xsd/stockquote.xsd", Document_FOLDER_NAME
                + "/xsd", this.getProject(), "stockquote.xsd");
        refreshProjectNFile(file);

        return super.getModelRoot();
    }
    
    @Override
    protected String getWsdlFilename() {
        return "stockquote.wsdl";
    }
    
    @Override
    protected String getWsdlFoldername() {
        return "pub/import/services/wsdl/";
    }
    
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final ServiceInterface intf = (ServiceInterface) description.getAllInterfaces().iterator().next();
        final List<IOperation> ops = intf.getOperation(ADDED_OPERATION_NAME);
        assertFalse(ops.isEmpty());
        final IOperation operation = ops.get(0);
        assertNotNull(operation);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertTrue(operation.getAllInputParameters().size() > 0);
        assertTrue(operation.getAllOutputParameters().size() > 0);
        
        final IParameter inParameter = operation.getAllInputParameters().iterator().next();
        Part part = (Part) inParameter.getComponent();
        XSDElementDeclaration elementDeclaration = part.getElementDeclaration();
        XSDTypeDefinition typeDefinition = part.getTypeDefinition();
        assertNotNull(elementDeclaration);
        assertNull(typeDefinition);        

        final IParameter outParameter = operation.getAllOutputParameters().iterator().next();
        part = (Part) outParameter.getComponent();
        elementDeclaration = part.getElementDeclaration();
        typeDefinition = part.getTypeDefinition();
        assertNotNull(elementDeclaration);
        assertNull(typeDefinition);        
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final ServiceInterface intf = (ServiceInterface) description.getAllInterfaces().iterator().next();
        assertTrue(intf.getOperation(ADDED_OPERATION_NAME).isEmpty());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final ServiceInterface intf = (ServiceInterface) description.getAllInterfaces().iterator().next();
        
        return new AddOperationCommand(modelRoot, intf, ADDED_OPERATION_NAME, OperationType.REQUEST_RESPONSE);
    }

}
