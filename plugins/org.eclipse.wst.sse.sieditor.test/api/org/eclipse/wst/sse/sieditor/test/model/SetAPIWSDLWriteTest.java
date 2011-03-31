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
package org.eclipse.wst.sse.sieditor.test.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/*
 * Those tests should be removed after the wsdl write API gets removed.
 */
@SuppressWarnings("nls")
public class SetAPIWSDLWriteTest extends SIEditorBaseTest {

    private static final String SOURCE_FOLDER = "src/wsdl";

    protected String getProjectName() {
        return "SetAPIWSDLWriteTest";
    }

    @Test
    public void test_Description() throws Exception {
        // check for referenced documents
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "One.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        IDescription wDesc = (IDescription) mm.getWriteSupport(description);
        interfaceObj = wDesc.addInterface("newInterface");
        assertEquals("newInterface", interfaceObj.getName());
        assertEquals("http://sap.com/xi/Purchasing", interfaceObj.getNamespace());
        List<org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface> interfaces = description
                .getInterface("newInterface");
        assertFalse("Could not find service new SI", interfaces.isEmpty());
        assertNotNull("Could not find service new SI", interfaces.get(0));
        assertEquals(0, interfaceObj.getAllOperations().size());

        wDesc.removeInterface(interfaceObj);
        assertTrue("newInterface is not removed", description.getInterface("newInterface").isEmpty());

        interfaceObj = wDesc.addInterface("newInterface");
        assertEquals("newInterface", interfaceObj.getName());
        assertEquals("http://sap.com/xi/Purchasing", interfaceObj.getNamespace());
        assertFalse("Could not find service new SI", description.getInterface("newInterface").isEmpty());
        assertNotNull("Could not find service new SI", description.getInterface("newInterface").get(0));
        assertEquals(0, interfaceObj.getAllOperations().size());

        // Implement these methods
        Exception error = null;
        try {
            wDesc.addExtension(null);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);
        error = null;
        try {
            wDesc.removeExtension(null);
        } catch (Exception e) {
            error = e;
        }
        assertNotNull(error);

        wDesc.removeInterface(description.getInterface("PurchaseOrderConfirmation").get(0));
        assertTrue("PurchaseOrderConfirmation is not removed", description.getInterface("PurchaseOrderConfirmation").isEmpty());

        schema = wDesc.addSchema("http://newNs");
        assertNotNull("Schema http://newNs is not added", schema);
        assertEquals("http://newNs", schema.getNamespace());
        assertEquals(description, schema.getRoot());
        assertEquals(description, schema.getParent());
        assertTrue("Schema location is not WSDL Location", schema.getLocation().endsWith("One.wsdl"));
        assertNotNull("new Schema could not be obtained", description.getSchema("http://newNs"));
        assertEquals(0, schema.getAllContainedTypes().size());

        assertTrue(wDesc.removeSchema(schema));
        assertNotNull("new Schema is not deleted", description.getSchema("http://newNs"));

        schema = wDesc.addSchema("http://newNs");
        assertNotNull("Schema http://newNs is not added", schema);
        assertEquals("http://newNs", schema.getNamespace());
        assertEquals(description, schema.getRoot());
        assertEquals(description, schema.getParent());
        assertTrue("Schema location is not WSDL Location", schema.getLocation().endsWith("One.wsdl"));
        assertNotNull("new Schema could not be obtained", description.getSchema("http://newNs"));
        assertEquals(0, schema.getAllContainedTypes().size());

        wDesc.setDocumentation("Documentation");
        assertEquals("Documentation", description.getDocumentation());
        wDesc.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "Two.wsdl");
        description = modelRoot.getDescription();
        wDesc = (IDescription) mm.getWriteSupport(description);
        wDesc.addInterface("newInterface");
        wDesc.removeInterface(description.getInterface("PurchaseOrderConfirmation").get(0));
        wDesc.addSchema("http://newNs");
        assertTrue(wDesc.removeSchema("http://sap.com/xi/SRM/Basis/Global"));
        wDesc.setDocumentation("Documentation");
        wDesc.setDocumentation("NewDocumentation");
        wDesc.save();

        modelRoot = getModelRoot("Two.wsdl");
        description = modelRoot.getDescription();
        assertFalse("Could not find service new SI", description.getInterface("newInterface").isEmpty());
        assertNotNull(description.getInterface("newInterface").get(0));
        assertNotNull("new Schema is not deleted", description.getSchema("http://newNs"));
        assertEquals("Schema http://sap.com/xi/SRM/Basis/Global is not deleted", 0, description
                .getSchema("http://sap.com/xi/SRM/Basis/Global").length);
        assertTrue("PurchaseOrderConfirmation is not removed", description.getInterface("PurchaseOrderConfirmation").isEmpty());
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("Documentation.wsdl", "Documentation.wsdl");
        description = modelRoot.getDescription();
        assertEquals("Documentation", description.getDocumentation());
        wDesc = (IDescription) mm.getWriteSupport(description);
        wDesc.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", description.getDocumentation());
        wDesc.save();
        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("PrefixWSDL.wsd", "PrefixWSDL.wsdl");
        description = modelRoot.getDescription();
        wDesc = (IDescription) mm.getWriteSupport(description);
        wDesc.setDocumentation("Documentation");
        assertEquals("Documentation", description.getDocumentation());

        wDesc.setNamespace("http://example.org");
        assertEquals("http://example.org", description.getNamespace());
        wDesc.save();
        modelRoot = getModelRoot("PrefixWSDL.wsdl");
        description = modelRoot.getDescription();
        assertEquals("http://example.org", description.getNamespace());

        try {
            wDesc.setNamespace("http://example.org&%^^*&%*(");
            fail("Validation of description namespace doesn't work");
        } catch (IllegalInputException e) {

        }
    }

    @Test
    public void test_ServiceInterface() throws Exception {
        // check for referenced documents
        final String fileName = "Three.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        IDescription wDesc = (IDescription) mm.getWriteSupport(description);
        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);
        IServiceInterface wInterface = (IServiceInterface) mm.getWriteSupport(interfaceObj);
        operation = wInterface.addOperation("newReqResponseOperation", OperationType.REQUEST_RESPONSE);
        assertNotNull("New operation 'newReqResponseOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newReqResponseOperation", operation.getName());
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(2, interfaceObj.getAllOperations().size());

        operation = wInterface.addOperation("newAsyncOperation", OperationType.ASYNCHRONOUS);
        assertNotNull("New operation 'newAsyncOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newAsyncOperation", operation.getName());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(3, interfaceObj.getAllOperations().size());

        wInterface.setDocumentation("Documentation");
        assertEquals("Documentation", interfaceObj.getDocumentation());
        wInterface.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());

        interfaceObj = wDesc.addInterface("newInterface");
        wInterface = (IServiceInterface) mm.getWriteSupport(interfaceObj);
        operation = wInterface.addOperation("newReqResponseOperation", OperationType.REQUEST_RESPONSE);
        assertNotNull("New operation 'newReqResponseOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newReqResponseOperation", operation.getName());
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(1, interfaceObj.getAllOperations().size());

        operation = wInterface.addOperation("newAsyncOperation", OperationType.ASYNCHRONOUS);
        assertNotNull("New operation 'newAsyncOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newAsyncOperation", operation.getName());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(2, interfaceObj.getAllOperations().size());

        wInterface.setDocumentation("Documentation");
        assertEquals("Documentation", interfaceObj.getDocumentation());
        wInterface.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());

        wInterface.removeOperation(interfaceObj.getOperation("newReqResponseOperation").get(0));
        assertTrue(interfaceObj.getOperation("newReqResponseOperation").isEmpty());
        wInterface.removeOperation(operation);
        assertTrue(interfaceObj.getOperation("newAsyncOperation").isEmpty());

        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        wDesc = (IDescription) mm.getWriteSupport(description);
        List<org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface> interfaces = description.getInterface("PurchaseOrderConfirmation");
        assertFalse(interfaces.isEmpty());
        interfaceObj = interfaces.get(0);
        assertEquals("Documentation\n\t\t", interfaceObj.getDocumentation());
        wInterface = (IServiceInterface) mm.getWriteSupport(interfaceObj);
        wInterface.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());
        wInterface.setName("NewPurchaseOrderConfirmation");
        assertEquals("NewPurchaseOrderConfirmation", interfaceObj.getName());
        assertFalse("Could not find interface after renaming", description.getInterface("NewPurchaseOrderConfirmation").isEmpty());
        assertNotNull("Could not find interface after renaming", description.getInterface("NewPurchaseOrderConfirmation").get(0));
        wDesc.save();

        // Test if changes are saved
        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        interfaceObj = description.getInterface("NewPurchaseOrderConfirmation").get(0);
        assertEquals("NewPurchaseOrderConfirmation", interfaceObj.getName());
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());
        wInterface = (IServiceInterface) mm.getWriteSupport(interfaceObj);

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", fileName);
        description = modelRoot.getDescription();
        wDesc = (IDescription) mm.getWriteSupport(description);
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);
        wInterface = (IServiceInterface) mm.getWriteSupport(interfaceObj);
        operation = wInterface.addOperation("NewOperation", OperationType.REQUEST_RESPONSE);
        assertEquals(2, interfaceObj.getAllOperations().size());
        wInterface.removeOperation(operation);
        assertEquals(1, interfaceObj.getAllOperations().size());
        wInterface.removeOperation(interfaceObj.getOperation("PurchaseOrderConfirmationRequestResponse_In").get(0));
        assertEquals(0, interfaceObj.getAllOperations().size());
    }

    @Test
    public void test_Operation() throws Exception {
        // check for referenced documents
        final String fileName = "TestOperation.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("Operations.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter parameter;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("SyncOperation").get(0);
        IOperation wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test parameter addition and deletion
        assertEquals(1, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(2, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = wOperation.addOutputParameter("Parameter2");
        assertEquals(2, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("Documentation", operation.getDocumentation());
        wOperation.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", operation.getDocumentation());
        faultObj = wOperation.addFault("fault2");
        assertNotNull("New Fault is null", faultObj);
        assertEquals("fault2", faultObj.getName());

        operation = interfaceObj.getOperation("SyncOperationWithNoMessage").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = wOperation.addOutputParameter("Parameter2");
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        wOperation.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithMissingMessages").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = wOperation.addOutputParameter("Parameter2");
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("", operation.getDocumentation());
        wOperation.setDocumentation("Documentation");
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithNoMessage").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("", operation.getDocumentation());
        wOperation.setDocumentation("Documentation");
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithMissingMessage").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        wOperation.setName("NewASyncOperationWithMissingMessage");
        assertEquals("NewASyncOperationWithMissingMessage", operation.getName());
        assertFalse("Could not find operation after renaming", interfaceObj.getOperation("NewASyncOperationWithMissingMessage").isEmpty());
        assertNotNull("Could not find operation after renaming", interfaceObj.getOperation("NewASyncOperationWithMissingMessage").get(0));
        assertTrue("Old operation is not renamed", interfaceObj.getOperation("ASyncOperationWithMissingMessage").isEmpty());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput1").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test adding input parameter
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        parameter = wOperation.addInputParameter("Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput2").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        // Test adding output parameter
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        parameter = wOperation.addOutputParameter("Parameter1");
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());

        IDescription wDesc = (IDescription) mm.getWriteSupport(description);
        wDesc.save();

        modelRoot = getModelRoot(fileName);
        description = modelRoot.getDescription();

        // Test if changes are saved
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("SyncOperation").get(0);
        assertEquals(2, operation.getAllInputParameters().size());
        assertEquals(2, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithNoMessage").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithMissingMessages").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithNoMessage").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("NewASyncOperationWithMissingMessage").get(0);
        assertNotNull("Operation NewASyncOperationWithMissingMessage could not be found", operation);

        operation = interfaceObj.getOperation("OperationWithNoInputOutput1").get(0);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(1, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput2").get(0);
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllInputParameters().size());

        // Test Removal of parameters and faults
        operation = interfaceObj.getOperation("SyncOperation").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        assertEquals(2, operation.getAllInputParameters().size());
        parameter = wOperation.addInputParameter("Parameter4");
        assertEquals(3, operation.getAllInputParameters().size());
        wOperation.removeInputParameter(parameter);
        assertEquals(2, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getInputParameter("Parameter4").isEmpty());
        wOperation.removeInputParameter(operation.getInputParameter("Parameter1").get(0));
        assertEquals(1, operation.getAllInputParameters().size());

        assertEquals(2, operation.getAllOutputParameters().size());
        parameter = wOperation.addOutputParameter("Parameter4");
        assertEquals(3, operation.getAllOutputParameters().size());
        wOperation.removeOutputParameter(parameter);
        assertEquals(2, operation.getAllOutputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getOutputParameter("Parameter4").isEmpty());
        wOperation.removeOutputParameter(operation.getOutputParameter("Parameter2").get(0));
        assertEquals(1, operation.getAllOutputParameters().size());

        assertEquals(2, operation.getAllFaults().size());
        wOperation.removeFault(operation.getFault("fault2").get(0));
        assertTrue("fault2 is not deleted", operation.getFault("fault2").isEmpty());
        assertEquals(1, operation.getAllFaults().size());
        wOperation.removeFault(operation.getFault("fault1").get(0));
        assertTrue("fault1 is not deleted", operation.getFault("fault1").isEmpty());
        assertEquals(0, operation.getAllFaults().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        parameter = wOperation.addInputParameter("parameter1");
        assertEquals(2, operation.getAllInputParameters().size());
        wOperation.removeInputParameter(parameter);
        assertEquals(1, operation.getAllInputParameters().size());
        wOperation.removeInputParameter(operation.getInputParameter("parameters").get(0));
        assertEquals(0, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("SyncOperation1").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        wOperation.setOperationType(OperationType.ASYNCHRONOUS);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());

        operation = interfaceObj.getOperation("ASyncOperation1").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        wOperation.setOperationType(OperationType.REQUEST_RESPONSE);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());

        operation = interfaceObj.getOperation("SyncOperation2").get(0);
        wOperation = (IOperation) mm.getWriteSupport(operation);
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(4, parameters.size());

        wOperation.removeOutputParameter(operation.getOutputParameter("Parameter1").get(0));
        parameters = operation.getAllOutputParameters();
        assertEquals(3, parameters.size());
        wDesc = (IDescription) mm.getWriteSupport(description);
        wDesc.save();

        modelRoot = getModelRoot(fileName);
        description = modelRoot.getDescription();

        // Test if removal changes are saved
        interfaceObj = description.getInterface("Operations").get(0);

        operation = interfaceObj.getOperation("SyncOperation").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getInputParameter("Parameter4").isEmpty());
        assertTrue("Parameter1 is not deleted", operation.getInputParameter("Parameter1").isEmpty());

        assertEquals(1, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getOutputParameter("Parameter4").isEmpty());
        assertTrue("Parameter2 is not deleted", operation.getOutputParameter("Parameter2").isEmpty());
        assertEquals(0, operation.getAllFaults().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        assertEquals(0, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("SyncOperation1").get(0);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());

        operation = interfaceObj.getOperation("ASyncOperation1").get(0);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());

        operation = interfaceObj.getOperation("SyncOperation2").get(0);
        parameters = operation.getAllInputParameters();
        assertEquals(4, parameters.size());
        Iterator<IParameter> paramIter = parameters.iterator();
        assertEquals("Parameter1", paramIter.next().getName());
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());

        operation.getOutputParameter("Parameter1");
        parameters = operation.getAllOutputParameters();
        assertEquals(3, parameters.size());
        paramIter = parameters.iterator();
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());
    }

    @Test
    public void test_Parameter() throws Exception {
        final String fileName = "TestParameter.wsdl";
        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "po.xsd", Document_FOLDER_NAME,
                this.getProject(), "po.xsd");
        assertTrue("File po.xsd could not be obtained", file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "example.xsd", Document_FOLDER_NAME,
                this.getProject(), "example.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "NullNamespace.xsd",
                Document_FOLDER_NAME, this.getProject(), "NullNamespace.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "ParameterType.xsd",
                Document_FOLDER_NAME, this.getProject(), "ParameterType.xsd");
        assertTrue("File example.xsd could not be obtained", file.exists());
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("ParameterType.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        IParameter parameter;

        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("ServiceInterface").get(0);
        operation = interfaceObj.getOperation("Operation").get(0);
        parameter = operation.getInputParameter("parameters").get(0);
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter wParameter;

        wParameter = (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter) mm.getWriteSupport(parameter);
        wParameter.setName("Parameter1");
        assertEquals("Parameter1", parameter.getName());
        assertEquals("", parameter.getDocumentation());
        wParameter.setDocumentation("Documentation");
        assertEquals("Documentation", parameter.getDocumentation());

        ISchema inlineSchema = description.getSchema("http://sap.com/xi/Purchasing")[0];

        // Set Type and Element from same WSDL
        assertEquals("NewOperation", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "BusinessTransactionDocumentID"), false);
        assertEquals("BusinessTransactionDocumentID", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "LanguageCode"), false);
        assertEquals("LanguageCode", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "BusinessTransactionDocumentID"), false);
        wParameter.setType(inlineSchema.getType(true, "PurchaseOrderConfirmationRequest"), false);
        assertEquals("PurchaseOrderConfirmationRequest", parameter.getType().getName());
        inlineSchema = description.getSchema("http://www.example.org/ParameterType/")[0];
        wParameter.setType(inlineSchema.getType(true, "NewOperation"), false);
        assertEquals("NewOperation", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "complexType"), false);
        assertEquals("complexType", parameter.getType().getName());

        SchemaNamespaceCondition condition = new SchemaNamespaceCondition("");

        // Set Type and Element from imported XSD
        condition.setNamespace("http://www.example.com/");
        inlineSchema = getSchema(description.getAllVisibleSchemas(), condition);
        wParameter.setType(inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(true, "purchaseOrder"), false);
        assertEquals("purchaseOrder", parameter.getType().getName());
        // Set Type from included XSD
        condition.setNamespace("http://www.example.com/IPO");

        ISchema[] ipoSchemas = description.getSchema("http://www.example.com/IPO");
        assertEquals(3, ipoSchemas.length);
        inlineSchema = getSchema1(ipoSchemas[0].getAllReferredSchemas(), condition);
        if (inlineSchema == null || inlineSchema.getType(false, "Address") == null) {
            inlineSchema = getSchema1(ipoSchemas[1].getAllReferredSchemas(), condition);
        }
        if (inlineSchema == null || inlineSchema.getType(false, "Address") == null) {
            inlineSchema = getSchema1(ipoSchemas[2].getAllReferredSchemas(), condition);
        }

        wParameter.setType(inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(true, "purchaseOrder"), false);
        assertEquals("purchaseOrder", parameter.getType().getName());
        // Set Type form XSD With Null namespace
        inlineSchema = description.getSchema("http://www.example.com/IPO/Include/NullNamespace")[0];
        condition.setNamespace(null);
        inlineSchema = getSchema1(inlineSchema.getAllReferredSchemas(), condition);
        wParameter.setType(inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(true, "purchaseOrder"), false);
        assertEquals("purchaseOrder", parameter.getType().getName());
        // Set Type from XSD With Same Namespace
        condition.setNamespace("http://www.example.org/ParameterType/");
        inlineSchema = getSchema(description.getContainedSchemas(), condition);
        wParameter.setType(inlineSchema.getType(true, "NewOperation"), false);
        assertEquals("NewOperation", parameter.getType().getName());
        wParameter.setType(inlineSchema.getType(false, "complexType"), false);
        assertEquals("complexType", parameter.getType().getName());

        ResourceUtils.createFolderInProject(getProject(), "src");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported.xsd", SOURCE_FOLDER, this
                .getProject(), "Imported.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "SupplierInvoice.wsdl", SOURCE_FOLDER,
                this.getProject(), "SupplierInvoice.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestSingleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestSingleSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestMultipleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestMultipleSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestExternal.xsd",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestExternal.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestImportedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestImportedSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestIncludedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestIncludedSchema.wsdl");

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "ParameterTypeTwo.wsdl");
        description = modelRoot.getDescription();
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);
        operation = interfaceObj.getOperation("PurchaseOrderConfirmationRequestResponse_In").get(0);
        parameter = operation.getInputParameter("parameters").get(0);
        wParameter = (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter) mm.getWriteSupport(parameter);
    }

    @Test
    public void test_Fault() throws Exception {
        // check for referenced documents
        final String fileName = "TestOperationFault.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("Operations.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter parameter;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("TestFault").get(0);
        faultObj = operation.getFault("fault1").get(0);
        IFault wFaultObj = (IFault) mm.getWriteSupport(faultObj);
        assertEquals("fault1", faultObj.getName());
        wFaultObj.setName("newFault");
        assertEquals("newFault", faultObj.getName());
        assertEquals("", faultObj.getDocumentation());
        wFaultObj.setDocumentation("Documentation");
        assertEquals("Documentation", faultObj.getDocumentation());
        wFaultObj.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", faultObj.getDocumentation());
        assertEquals(1, faultObj.getParameters().size());
        parameter = wFaultObj.addParameter("Parameter1");
        parameter = wFaultObj.addParameter("Parameter2");
        assertEquals(3, faultObj.getParameters().size());
        wFaultObj.removeParameter(faultObj.getParameter("parameters").get(0));
        assertEquals(2, faultObj.getParameters().size());
        wFaultObj.removeParameter(parameter);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("fault2").get(0);
        wFaultObj = (IFault) mm.getWriteSupport(faultObj);
        assertEquals(4, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithMissingMessage").get(0);
        assertEquals(0, faultObj.getParameters().size());
        wFaultObj = (IFault) mm.getWriteSupport(faultObj);
        parameter = wFaultObj.addParameter("Parameter1");
        parameter = wFaultObj.addParameter("Parameter2");
        assertEquals(2, faultObj.getParameters().size());
        wFaultObj.removeParameter(parameter);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithNoMessage").get(0);
        assertEquals(0, faultObj.getParameters().size());
        wFaultObj = (IFault) mm.getWriteSupport(faultObj);
        parameter = wFaultObj.addParameter("Parameter1");
        parameter = wFaultObj.addParameter("Parameter2");
        assertEquals(2, faultObj.getParameters().size());
        wFaultObj.removeParameter(parameter);
        assertEquals(1, faultObj.getParameters().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        assertEquals(0, operation.getAllFaults().size());
        IOperation wOperation = (IOperation) mm.getWriteSupport(operation);
        faultObj = wOperation.addFault("newFault");
        wFaultObj = (IFault) mm.getWriteSupport(faultObj);
        wFaultObj.addParameter("Parameter1");
        assertEquals(2, faultObj.getParameters().size());

        IDescription wDescription = (IDescription) mm.getWriteSupport(description);
        wDescription.save();

        modelRoot = getModelRoot("TestOperationFault.wsdl");
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("TestFault").get(0);
        faultObj = operation.getFault("newFault").get(0);
        assertEquals(1, faultObj.getParameters().size());
        assertEquals("NewDocumentation", faultObj.getDocumentation());
        assertFalse("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").isEmpty());
        assertNotNull("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").get(0));

        faultObj = operation.getFault("fault2").get(0);
        assertEquals(4, faultObj.getParameters().size());
        Iterator<IParameter> paramIter = faultObj.getParameters().iterator();
        assertEquals("Parameter1", paramIter.next().getName());
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());

        faultObj = operation.getFault("faultWithMissingMessage").get(0);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithNoMessage").get(0);
        assertEquals(1, faultObj.getParameters().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        faultObj = operation.getFault("newFault").get(0);
        assertEquals(2, faultObj.getParameters().size());
        assertFalse("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").isEmpty());
        assertNotNull("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").get(0));

    }

    private ISchema getSchema(final Collection<ISchema> schemas, ICondition<ISchema> condition) {
        for (ISchema schema : schemas) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    private ISchema getSchema1(final Collection<ISchema> schemas, ICondition<ISchema> condition) {
        for (ISchema schema : schemas) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    interface ICondition<T> {
        boolean isSatisfied(T in);
    }

    private class SchemaNamespaceCondition implements ICondition<ISchema> {

        private String _namespace;

        public SchemaNamespaceCondition(final String namespace) {
            this._namespace = namespace;
        }

        public boolean isSatisfied(ISchema in) {
            if ((null == _namespace && null == in.getNamespace()) || (null != _namespace && _namespace.equals(in.getNamespace())))
                return true;
            return false;
        }

        public void setNamespace(final String namespace) {
            this._namespace = namespace;
        }

    }

    private IWsdlModelRoot copyAndGetModelRoot(final String fileName, final String targetName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, targetName);
    }

    private IWsdlModelRoot getModelRoot(final String fileName) throws Exception {
        final IFile file = (IFile) getProject().findMember(new Path(Document_FOLDER_NAME + IPath.SEPARATOR + fileName));
        assertTrue(file.exists());
        final IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(file));

        setupEnvironment(modelRoot);

        return modelRoot;
    }
}
