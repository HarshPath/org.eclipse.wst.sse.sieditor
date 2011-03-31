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
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

@SuppressWarnings("nls")
public class WSDLReadAPITest extends SIEditorBaseTest {

    protected static final String WSDL_FILE_NAME = "ChangePurchaseOrder_WSD.wsdl";
    protected static final int NUMBER_OF_INTERFACES_IN_FILE = 1;
    protected static final String INTERFACE_NAME = "PurchaseOrderChangeRequestConfirmation_In";
    protected static final int NUMBER_OF_CONTAINED_SCHEMAS = 2;
    protected static final int NUMBER_OF_VISIBLE_SCHEMAS = 3;
    protected static final int NUMBER_OF_REFERENCED_SERVICES = 0;
    protected static final String DESCRIPTION_LOCATION = "/WSDLReadAPITests/data/ChangePurchaseOrder_WSD.wsdl";
    protected static final String DESCRIPTION_NAMESPACE = "http://sap.com/xi/APPL/SE/Global";
    protected static final int NUMBER_OF_OPERATIONS = 1;
    protected static final String TESTED_OPERATION = "PurchaseOrderChangeRequestConfirmation_In";
    protected static final OperationType OPERATION_TYPE = OperationType.REQUEST_RESPONSE;
    protected static final int NUMBER_OF_INPUT_PARAMETERS = 1;
    protected static final String TESTED_INPUT_PARAMETER = "PurchaseOrderChangeRequest_sync";
    protected static final int NUMBER_OF_OUTPUT_PARAMETERS = 1;
    protected static final String TESTED_OUTPUT_PARAMETER = "PurchaseOrderChangeConfirmation_sync";
    protected static final int NUMBER_OF_FAULTS = 1;
    protected static final String TESTED_FAULT = "StandardMessageFault";
    protected static final int NUMBER_OF_PARAMETERS_IN_FAULT = 1;
    protected static final String TESTED_PARAMETER_IN_FAULT = "StandardMessageFault";
    protected static final String[] SCHEMA_NAMESPACES = new String[] { "http://sap.com/xi/APPL/SE/Global",
            "http://sap.com/xi/SAPGlobal20/Global" };
    protected static final String SCHEMA_FOR_SCHEMANS = "http://www.w3.org/2001/XMLSchema";

    protected String getProjectName() {
        return "WSDLReadAPITests";
    }

    @Test
    public void test_ModelAccess() throws Exception {
        final IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);
        assertNotNull("Model could not be acquired", root);
        assertNotNull("Description could not be acquired", root.getDescription());
    }

    @Test
    public void test_Description() throws Exception {
        IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

        IDescription description = root.getDescription();

        String nameSpace = description.getNamespace();
        assertEquals("http://sap.com/xi/APPL/SE/Global", nameSpace);

        Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertTrue(interfaces.size() + " interfaces returned instead of " + NUMBER_OF_INTERFACES_IN_FILE,
                NUMBER_OF_INTERFACES_IN_FILE == interfaces.size()); // @Test-0001
        interfaces = description.getAllInterfaces();
        assertTrue(interfaces.size() + " interfaces returned instead of " + NUMBER_OF_INTERFACES_IN_FILE,
                NUMBER_OF_INTERFACES_IN_FILE == interfaces.size()); // @Test-0001

        List<IServiceInterface> sIs = description.getInterface(INTERFACE_NAME);
        assertFalse(sIs.isEmpty());
        IServiceInterface service1 = sIs.get(0);
        assertNotNull("Null returned for getInterface", service1); // @Test-0002

        sIs = description.getInterface("foo");
        assertTrue("Interface exists with name foo",sIs.isEmpty());
        
        Collection<ISchema> schemas = description.getContainedSchemas();
        assertTrue(schemas.size() + " Contained schemas returned instead of " + NUMBER_OF_CONTAINED_SCHEMAS,
                NUMBER_OF_CONTAINED_SCHEMAS == schemas.size()); // @Test-0003

        Collection<ISchema> visibleSchemas = description.getAllVisibleSchemas();
        assertTrue(visibleSchemas.size() + " Visible Schemas returned instead of " + NUMBER_OF_VISIBLE_SCHEMAS,
                NUMBER_OF_VISIBLE_SCHEMAS == visibleSchemas.size()); // @Test-0004

        boolean exists = false;
        for(ISchema schema : visibleSchemas) {
        	if(SCHEMA_FOR_SCHEMANS.equals(schema.getNamespace())) {
        		exists = true;
        		break;
        	}
        }
        assertTrue("Return Visible schema is not schema for schema", exists);

        for (String schemans : SCHEMA_NAMESPACES) {
            ISchema[] schemaArray = description.getSchema(schemans);
            assertTrue("No schema returned for namespace '" + schemans + "'", 1 == schemaArray.length); // @Test-0005
            assertEquals("Wrong Schema returned for namespace '" + schemans + "'", schemans, schemaArray[0].getNamespace());
        }

        ISchema[] schemaArray = description.getSchema("foo");
        assertTrue("Schemas exists for namespace 'foo'", 0 == schemaArray.length); // @Test-0005

        Collection<IDescription> referencedServices = description.getReferencedServices();
        assertTrue("Referenced services exist", NUMBER_OF_REFERENCED_SERVICES == referencedServices.size()); // @Test-0006

        String location = description.getLocation();
        assertTrue(location.endsWith(DESCRIPTION_LOCATION)); // @Test-007

        String namespace = description.getNamespace();
        assertEquals("'" + namespace + "' returned as namespace instead of '" + DESCRIPTION_NAMESPACE + "'",
                DESCRIPTION_NAMESPACE, namespace); // @Test-0008
        assertTrue(description.getParent() == null);
        assertTrue(description.getRoot() == null);
    }

    @Test
    public void test_ServiceInterface() throws Exception {
        IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

        IDescription description = root.getDescription();
        List<IServiceInterface> interfaces = description.getInterface(INTERFACE_NAME);
        assertFalse(interfaces.isEmpty());
        IServiceInterface service = interfaces.get(0);
        assertNotNull("No interface returned for name " + INTERFACE_NAME + " is  null", service);
        assertTrue("Wrong interface returned for name " + INTERFACE_NAME, INTERFACE_NAME.equals(service.getName()));

        Collection<IOperation> operations = service.getAllOperations();
        assertEquals(operations.size() + " operations returned instead of " + NUMBER_OF_OPERATIONS, NUMBER_OF_OPERATIONS,
                operations.size()); // @Test-0009
        operations = service.getAllOperations();
        assertEquals(operations.size() + " operations returned instead of " + NUMBER_OF_OPERATIONS, NUMBER_OF_OPERATIONS,
                operations.size()); // @Test-0009

        List<IOperation> ops = service.getOperation(TESTED_OPERATION);
        assertFalse(ops.isEmpty());
        IOperation operation = ops.get(0);
        assertNotNull("No operation returned for name " + TESTED_OPERATION, operation); // @Test-0010
        assertTrue("Wrong Operaiton returned for name " + TESTED_OPERATION, TESTED_OPERATION.equals(operation.getName()));

        assertTrue(description == service.getParent());
        assertTrue(description == service.getRoot());
    }

    @Test
    public void test_Operation() throws Exception {
        IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

        IDescription description = root.getDescription();
        IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
        IOperation operation = service.getOperation(TESTED_OPERATION).get(0);

        OperationType operationType = operation.getOperationStyle();
        assertTrue("Returned operation is not Request-Response", OPERATION_TYPE == operationType); // @Test-0012

        Collection<IParameter> inputParameters = operation.getAllInputParameters();
        assertEquals(inputParameters.size() + " in parameters returned instead of " + NUMBER_OF_INPUT_PARAMETERS,
                NUMBER_OF_INPUT_PARAMETERS, inputParameters.size()); // @Test-0013
        inputParameters = operation.getAllInputParameters();
        assertEquals(inputParameters.size() + " in parameters returned instead of " + NUMBER_OF_INPUT_PARAMETERS,
                NUMBER_OF_INPUT_PARAMETERS, inputParameters.size()); // @Test-0013

        List<IParameter> params = operation.getInputParameter(TESTED_INPUT_PARAMETER);
        assertFalse(params.isEmpty());
        IParameter inputParameter = params.get(0);
        assertNotNull("No input parameter returned for name " + TESTED_INPUT_PARAMETER, inputParameter); // @Test-0014
        assertTrue("Wrong in parameter returned for name " + TESTED_INPUT_PARAMETER, TESTED_INPUT_PARAMETER.equals(inputParameter
                .getName()));
        assertTrue(operation == inputParameter.getParent());
        assertTrue(description == inputParameter.getRoot());

        Collection<IParameter> outputParameters = operation.getAllOutputParameters();
        assertEquals(outputParameters.size() + " out parameters returned instead of " + NUMBER_OF_OUTPUT_PARAMETERS,
                NUMBER_OF_OUTPUT_PARAMETERS, outputParameters.size()); // @Test-0015
        outputParameters = operation.getAllOutputParameters();
        assertEquals(outputParameters.size() + " out parameters returned instead of " + NUMBER_OF_OUTPUT_PARAMETERS,
                NUMBER_OF_OUTPUT_PARAMETERS, outputParameters.size()); // @Test-0015

        params = operation.getOutputParameter(TESTED_OUTPUT_PARAMETER);
        assertFalse(params.isEmpty());
        IParameter outputParameter = params.get(0);
        assertNotNull("No out parameter returned for name " + TESTED_OUTPUT_PARAMETER, outputParameter); // @Test-0016
        assertTrue("Wrong out parameter returned for name " + TESTED_OUTPUT_PARAMETER, TESTED_OUTPUT_PARAMETER
                .equals(outputParameter.getName()));
        assertTrue(operation == outputParameter.getParent());
        assertTrue(description == outputParameter.getRoot());

        Collection<IFault> faults = operation.getAllFaults();
        assertEquals(faults.size() + " faults returned instead of " + NUMBER_OF_FAULTS, NUMBER_OF_FAULTS, faults.size()); // @Test-0017
        faults = operation.getAllFaults();
        assertEquals(faults.size() + " faults returned instead of " + NUMBER_OF_FAULTS, NUMBER_OF_FAULTS, faults.size()); // @Test-0017

        List<IFault> flts = operation.getFault(TESTED_FAULT);
        assertFalse(flts.isEmpty());
        IFault fault = flts.get(0);
        assertNotNull("No Fault returned for name " + TESTED_FAULT, fault); // @Test-0018
        assertTrue("Wrong fault returned for name " + TESTED_FAULT, TESTED_FAULT.equals(fault.getName()));
        assertTrue(operation == fault.getParent());
        assertTrue(description == fault.getRoot());

        assertTrue(service == operation.getParent());
        assertTrue(description == operation.getRoot());
    }

    @Test
    public void test_OperationParameter() throws Exception {
        IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);
        IDescription description = root.getDescription();

        IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
        IOperation operation = service.getOperation(TESTED_OPERATION).get(0);
        IParameter parameter = operation.getInputParameter(TESTED_INPUT_PARAMETER).get(0);

        String name = parameter.getName();
        assertNotNull("Null returned for name", name);
        assertTrue("Wrong name returned for parameter " + TESTED_INPUT_PARAMETER, TESTED_INPUT_PARAMETER.equals(name));

        IType type = parameter.getType();
        assertNotNull("Null returned for type" + type); // @Test-0019
        assertTrue("Wrong type returned for parameter", "PurchaseOrderChangeRequest_sync".equals(type.getName()));
        assertTrue("Type must be StructureType", type instanceof StructureType);

        parameter = operation.getOutputParameter(TESTED_OUTPUT_PARAMETER).get(0);

        name = parameter.getName();
        assertNotNull("Null returned for name", name);
        assertTrue("Wrong name returned for parameter " + TESTED_OUTPUT_PARAMETER, TESTED_OUTPUT_PARAMETER.equals(name));

        type = parameter.getType();
        assertNotNull("Null returned for type" + type); // @Test-0019
        assertTrue("Wrong type returned for parameter", "PurchaseOrderChangeConfirmation_sync".equals(type.getName()));
        assertTrue("Type must be StructureType", type instanceof StructureType);
        assertTrue("Returned Type is not an Element", ((StructureType) type).isElement());
    }

    @Test
    public void test_OperationFault() throws Exception {
        IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);
        IDescription description = root.getDescription();
        IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
        IOperation operation = service.getOperation(TESTED_OPERATION).get(0);

        IFault fault = operation.getFault(TESTED_FAULT).get(0);
        final String name = fault.getName();
        assertNotNull("Null returned for name", name);
        assertEquals("Wrong fault name returned ", TESTED_FAULT, name);

        Collection<IParameter> parameters = fault.getParameters();
        assertEquals(parameters.size() + " Fault parameters returned instead of " + NUMBER_OF_PARAMETERS_IN_FAULT,
                NUMBER_OF_PARAMETERS_IN_FAULT, parameters.size()); // @Test-0020
        IParameter parameter = fault.getParameter(TESTED_PARAMETER_IN_FAULT).get(0);
        parameters = fault.getParameters();
        assertEquals(parameters.size() + " Fault parameters returned instead of " + NUMBER_OF_PARAMETERS_IN_FAULT,
                NUMBER_OF_PARAMETERS_IN_FAULT, parameters.size()); // @Test-0020
        assertTrue(operation == fault.getParent());
        assertTrue(description == fault.getRoot());

        assertNotNull("No parameter returned for name " + TESTED_PARAMETER_IN_FAULT, parameter); // @Test-0021
        assertEquals("Wrong parameter returned for name " + TESTED_PARAMETER_IN_FAULT, TESTED_PARAMETER_IN_FAULT, parameter
                .getName());
    }

    private IWsdlModelRoot getModelRoot(String fileName) throws Exception {
        final String fullPath = Constants.DATA_PUBLIC_SELF_MIX2 + fileName;
        final IWsdlModelRoot modelRoot = getWSDLModelRoot(fullPath, fileName);
        assertNotNull("Could not Acquire Model Root for " + fullPath, modelRoot);
        return modelRoot;
    }

    public void dispose() throws Exception {

        super.dispose();
    }
}