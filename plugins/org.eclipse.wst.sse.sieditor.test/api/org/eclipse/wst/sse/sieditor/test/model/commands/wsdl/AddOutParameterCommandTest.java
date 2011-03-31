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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOutParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class AddOutParameterCommandTest extends AbstractCommandTest {
    private static final String MY_PARAMETER_NAME = "myParameter";
    private int initialNumberOfParts;
    
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

    @SuppressWarnings("unchecked")
	@Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        assertEquals(initialNumberOfParts + 1, operation.getAllOutputParameters().size());
        assertFalse(operation.getOutputParameter(MY_PARAMETER_NAME).isEmpty());
        assertNotNull(operation.getOutputParameter(MY_PARAMETER_NAME).get(0));

        final List<Part> parts = operation.getComponent().getEOutput().getEMessage().getEParts();
        assertEquals(initialNumberOfParts + 1, parts.size());

        boolean newPartExists = false;
        Part newPart = null;
        for (final Part part : parts) {
            if (MY_PARAMETER_NAME.equals(part.getName())) {
                newPartExists = true;
                newPart = part;
                break;
            }
        }

        assertTrue(newPartExists);
        final XSDElementDeclaration elementDeclaration = newPart.getElementDeclaration();
        final XSDTypeDefinition typeDefinition = newPart.getTypeDefinition();
        assertNotNull(elementDeclaration);
        assertNull(typeDefinition);        
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        assertEquals(initialNumberOfParts, operation.getAllOutputParameters().size());
        assertTrue(operation.getFault(MY_PARAMETER_NAME).isEmpty());

        final List<Part> parts = operation.getComponent().getEOutput().getEMessage().getEParts();
        assertEquals(initialNumberOfParts, parts.size());

        boolean newPartExists = false;
        for (final Part part : parts) {
            if (MY_PARAMETER_NAME.equals(part.getName())) {
                newPartExists = true;
                break;
            }
        }

        assertFalse(newPartExists);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        initialNumberOfParts = operation.getAllOutputParameters().size();
        
        return new AddOutParameterCommand(modelRoot, operation, MY_PARAMETER_NAME);
    }

}
