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
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class AddFaultCommandTest extends AbstractCommandTest {
    private static final String MY_FAULT_NAME = "myFault";
    private int initialNumberOfFaults;
    
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
        assertEquals(initialNumberOfFaults + 1, operation.getAllFaults().size());
        assertFalse(operation.getFault(MY_FAULT_NAME).isEmpty());
        assertNotNull(operation.getFault(MY_FAULT_NAME).get(0));

        final List<Fault> faults = operation.getComponent().getEFaults();
        assertEquals(initialNumberOfFaults + 1, faults.size());

        boolean newFaultExists = false;
        Fault newFault = null;
        for (final Fault fault : faults) {
            if (MY_FAULT_NAME.equals(fault.getName())) {
                newFaultExists = true;
                newFault = fault;
            }
        }

        assertTrue(newFaultExists);
        final Part part = (Part)(newFault.getEMessage().getEParts().iterator().next());
        final XSDElementDeclaration elementDeclaration = part.getElementDeclaration();
        final XSDTypeDefinition typeDefinition = part.getTypeDefinition();
        assertNotNull(elementDeclaration);
        assertNull(typeDefinition);        
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        assertEquals(initialNumberOfFaults, operation.getAllFaults().size());
        assertTrue(operation.getFault(MY_FAULT_NAME).isEmpty());

        final List<Fault> faults = operation.getComponent().getEFaults();
        assertEquals(initialNumberOfFaults, faults.size());

        boolean newFaultExists = false;
        for (final Fault fault : faults) {
            if (MY_FAULT_NAME.equals(fault.getName())) {
                newFaultExists = true;
            }
        }

        assertFalse(newFaultExists);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        initialNumberOfFaults = operation.getAllFaults().size();

        final Definition definition = description.getComponent();
        final String xsdSchamaNamespacePrefix = definition.getPrefix(EmfXsdUtils.getSchemaForSchemaNS());
        assertNull(xsdSchamaNamespacePrefix);
        
        return new AddFaultCommand(modelRoot, operation, MY_FAULT_NAME);
    }

}
