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

import static org.eclipse.wst.sse.sieditor.test.util.SIEditorUtils.findNamedObject;
import static org.eclipse.wst.sse.sieditor.test.util.SIEditorUtils.validateNamedComponents;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

@SuppressWarnings("nls")
public class WsdlMultipleComponentsTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "WsdlNComponentsTest";
    }

    @Test
    public void testMultipleComponents() throws Exception {
        final String INTERFACE_NAME = "SecondPurchaseOrderConfirmation";
        final String OPERATION_NAME = "SecondPurchaseOrderConfirmationRequestResponse_In";
        final String FAULT_NAME = "exception01";
        final IDescription description = getModelRoot("MultipleComponents.wsdl");

        // Multiple Interfaces
        final Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertEquals(interfaces.size() + " interfaces returned instead of 3", 3, interfaces.size());
        validateNamedComponents(interfaces, Arrays.asList("PurchaseOrderConfirmation", "SecondPurchaseOrderConfirmation"));

        // Multiple Operations
        final IServiceInterface serviceInterface = findNamedObject(interfaces, INTERFACE_NAME);
        assertNotNull("No service interface found for name ", serviceInterface);

        final Collection<IOperation> operations = serviceInterface.getAllOperations();
        assertEquals(operations.size() + " operations returned instead of 2", 2, operations.size());
        validateNamedComponents(operations, Arrays.asList("PurchaseOrderConfirmationRequestResponse_In",
                "SecondPurchaseOrderConfirmationRequestResponse_In"));

        // Multiple Faults
        final IOperation operation = findNamedObject(operations, OPERATION_NAME);
        assertNotNull("No operation found for name ", operation);

        final Collection<IFault> faults = operation.getAllFaults();
        assertEquals(faults.size() + " faults returned instead of 2", 2, faults.size());
        validateNamedComponents(faults, Arrays.asList("exception00", "exception01"));

        // MultipleParameters
        final Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(parameters.size() + " parameters returned instead of 2", 2, parameters.size());
        validateNamedComponents(parameters, Arrays.asList("firstInParameter", "secondInParameter"));

        final Collection<IParameter> outparameters = operation.getAllOutputParameters();
        assertEquals(outparameters.size() + " out parameters returned instead of 2", 2, outparameters.size());
        validateNamedComponents(outparameters, Arrays.asList("firstOutParameter", "secondOutParameter"));

        // MultipleFaultParameters
        final IFault fault = findNamedObject(faults, FAULT_NAME);
        assertNotNull("No Fault found with name " + FAULT_NAME, fault);
        final Collection<IParameter> faultParameters = fault.getParameters();
        validateNamedComponents(faultParameters, Arrays.asList("exception00", "exception01", "exception02"));
    }

    @Test
    public void testNoPortTypes() throws Exception {
        // No Service Interfaces
        final IDescription description = getModelRoot("NoPortTypes.wsdl");

        final Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertNotNull("Null returned for no PortTypes in a file", interfaces);
        assertTrue(interfaces.size() == 0);

    }

    @Test
    public void testNoSchemas() throws Exception {
        // No Service Interfaces
        IDescription description = getModelRoot("NoTypes.wsdl");

        Collection<ISchema> schemas = description.getContainedSchemas();
        assertNotNull("Empty collection must be returned when there are no schemas", schemas);
        assertEquals(0, schemas.size());

        description = getModelRoot("NoSchemas.wsdl");
        schemas = description.getContainedSchemas();
        assertNotNull("Empty collection must be returned when there are no schemas", schemas);
        assertEquals(0, schemas.size());
    }

    @Test
    public void testNoComponents() throws Exception {
        final IDescription description = getModelRoot("NoComponents.wsdl");

        // No Operations
        final Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertNotNull(interfaces);
        assertTrue(interfaces.size() == 3);

        IServiceInterface serviceInterface = findNamedObject(interfaces, "PortTypeWithNoOperations");
        assertNotNull(serviceInterface);
        Collection<IOperation> operations = serviceInterface.getAllOperations();
        assertNotNull("Null returned when there are no operations", operations);
        assertTrue(operations.size() == 0);

        serviceInterface = findNamedObject(interfaces, "PortType");
        assertNotNull(serviceInterface);

        operations = serviceInterface.getAllOperations();
        assertTrue(operations.size() == 5);

        // NoParameters
        // No Faults
        IOperation operation = findNamedObject(operations, "OperationWithNoInParametersAndFaults");
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertNotNull("Empty Collecitons should be returned when there are no in parametes", parameters);
        assertTrue(parameters.size() == 0);
        Collection<IFault> faults = operation.getAllFaults();
        assertNotNull("Empty Collecitons must be returned when there are no faults", faults);
        assertTrue(faults.size() == 0);

        operation = findNamedObject(operations, "OperationWithNoOutParameters");
        parameters = operation.getAllOutputParameters();
        assertNotNull("Empty Collecitons must be returned when there are no out parametes", parameters);
        assertTrue(parameters.size() == 0);

        operation = findNamedObject(operations, "OperationWithNoFaults");
        faults = operation.getAllFaults();
        assertNotNull("Empty Collecitons must be returned when there are no faults", faults);
        assertTrue(faults.size() == 0);

        // No Fault Parameters
        operation = findNamedObject(operations, "OperationWithNoFaultParameters");
        faults = operation.getAllFaults();
        IFault fault = findNamedObject(faults, "fault");
        parameters = fault.getParameters();
        assertNotNull("Empty Collecitons must be returned when there are no fault parametes", parameters);
        assertTrue(parameters.size() == 0);

        operation = findNamedObject(operations, "OperationWithNoInputOutput");
        parameters = operation.getAllInputParameters();
        assertNotNull("Empty Collecitons must be returned when there are no in parametes", parameters);
        assertTrue(parameters.size() == 0);
        parameters = operation.getAllOutputParameters();
        assertNotNull("Empty Collecitons must be returned when there are no out parametes", parameters);
        assertTrue(parameters.size() == 0);

        // PortType with invalid operations
        serviceInterface = findNamedObject(interfaces, "PortTypeWithInvalidOperations");
        assertNotNull(serviceInterface);

        operations = serviceInterface.getAllOperations();
        assertTrue(operations.size() == 0);
    }

    @Test
    public void testDuplicateComponents() throws Exception {
        final IDescription description = getModelRoot("DuplicateComponents.wsdl");
        // Duplicate Interfaces

        final Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertNotNull(interfaces);
        assertTrue(interfaces.size() == 3);

        List<IServiceInterface> sIs = description.getInterface("DuplicatePortType");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = description.getInterface("DuplicatePortType").get(0);
        assertNotNull(serviceInterface);

        sIs =description.getInterface("PortType");
        assertFalse(sIs.isEmpty());
        serviceInterface =  sIs.get(0);
        assertNotNull(serviceInterface);

        // Duplicate Operations
        Collection<IOperation> operations = serviceInterface.getAllOperations();
        assertTrue(operations.size() == 3);

        List<IOperation> ops = serviceInterface.getOperation("DuplicateOperation");
        assertFalse(ops.isEmpty());
        IOperation operation = ops.get(0);
        assertNotNull(operation);
        ops = serviceInterface.getOperation("Operation");
        assertFalse(ops.isEmpty());
        operation = ops.get(0);
        assertNotNull(operation);

        // Duplicate Faults
        Collection<IFault> faults = operation.getAllFaults();
        assertTrue(faults.size() == 3);
        List<IFault> flts = operation.getFault("duplicatefault");
        assertFalse(flts.isEmpty());
        IFault fault = flts.get(0);
        assertNotNull(fault);
        flts = operation.getFault("fault");
        assertFalse(flts.isEmpty());
        fault = flts.get(0);
        assertNotNull(fault);

        // Duplicate Parameters
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertTrue(parameters.size() == 3);
        List<IParameter> prms = operation.getInputParameter("duplicateparameter");
        assertFalse(prms.isEmpty());
        IParameter parameter = prms.get(0);
        assertNotNull(parameter);
        prms = operation.getInputParameter("parameter");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull(parameter);
        parameters = operation.getAllOutputParameters();
        assertTrue(parameters.size() == 3);
        prms = operation.getOutputParameter("duplicateparameter");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull(parameter);
        prms = operation.getOutputParameter("parameter");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull(parameter);

        // Duplicate Fault Parameters
        parameters = fault.getParameters();
        assertTrue(parameters.size() == 3);
        prms = operation.getInputParameter("duplicateparameter");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull(parameter);
        prms = operation.getInputParameter("parameter");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull(parameter);
    }

    @Test
    public void testSharedMessages() throws Exception {
        final IDescription description = getModelRoot("SharedMessage.wsdl");

        List<IServiceInterface> sIs = description.getInterface("SharedMessage");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull(serviceInterface);

        List<IOperation> ops = serviceInterface.getOperation("Operation");
        assertFalse(ops.isEmpty());
        IOperation operation = ops.get(0);
        assertNotNull(operation);

        List<IFault> flts = operation.getFault("fault");
        assertFalse(flts.isEmpty());
        IFault fault = flts.get(0);
        assertNotNull(fault);

        // Duplicate Parameters
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertTrue(parameters.size() == 1);
        parameters = operation.getAllOutputParameters();
        assertTrue(parameters.size() == 1);

        // Duplicate Fault Parameters
        parameters = fault.getParameters();
        assertTrue(parameters.size() == 1);
    }

    @Test
    public void testOperationType() throws Exception {
        final IDescription description = getModelRoot("MultipleComponents.wsdl");
        List<IServiceInterface> sIs = description.getInterface("OperationTypes");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull("No interface returned for name OperationTypes", serviceInterface);
        Collection<IOperation> operations = serviceInterface.getAllOperations();
        assertEquals(4, operations.size());

        IOperation operation = serviceInterface.getOperation("SyncOperationWithFault").get(0);
        OperationType style = operation.getOperationStyle();
        assertNotNull("style is null for SyncOperationWithFault", style);
        assertTrue("style for SyncOperationWithFault is not REQUEST_RESPONSE", OperationType.REQUEST_RESPONSE == style);

        operation = serviceInterface.getOperation("SyncOperationWithNoFault").get(0);
        style = operation.getOperationStyle();
        assertNotNull("style is null for SyncOperationWithNoFault", style);
        assertTrue("style for SyncOperationWithNoFault is not REQUEST_RESPONSE", OperationType.REQUEST_RESPONSE == style);

        operation = serviceInterface.getOperation("ASyncOperationWithFault").get(0);
        style = operation.getOperationStyle();
        assertNotNull("style is null for ASyncOperationWithFault", style);
        assertTrue("style for ASyncOperationWithFault is not ASYNCHRONOUS", OperationType.ASYNCHRONOUS == style);

        operation = serviceInterface.getOperation("ASyncOperationWithNoFault").get(0);
        style = operation.getOperationStyle();
        assertNotNull("style is null for ASyncOperationWithNoFault", style);
        assertTrue("style for ASyncOperationWithNoFault is not ASYNCHRONOUS", OperationType.ASYNCHRONOUS == style);
    }

    private IDescription getModelRoot(String fileName) throws Exception {
        final String fullPath = Constants.DATA_PUBLIC_SELF_KESHAV + fileName;
        final IWsdlModelRoot modelRoot = getWSDLModelRoot(fullPath, fileName);
        assertNotNull("Could not Acquire Model Root for " + fullPath, modelRoot);
        return modelRoot.getDescription();
    }
}