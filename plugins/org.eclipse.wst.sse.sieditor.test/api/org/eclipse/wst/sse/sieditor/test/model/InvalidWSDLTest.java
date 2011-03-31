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
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

@SuppressWarnings("nls")
public class InvalidWSDLTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "InvalidWSDLTest";
    }

    @Test
    public void testNoName() throws Exception {
        // Service Interface
        final IDescription description = getModelRoot("InvalidWSDL.wsdl");
        Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertTrue(interfaces.size() == 3);
        IServiceInterface[] serviceinterfaces = new IServiceInterface[3];
        interfaces.toArray(serviceinterfaces);
        assertEquals("", serviceinterfaces[0].getName());
        assertEquals("", serviceinterfaces[1].getName());
        List<IServiceInterface> sIs = description.getInterface("");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull("Null returned for service interface with name ''", serviceInterface);

        sIs = description.getInterface("PortType");
        assertFalse(sIs.isEmpty());
        serviceInterface = sIs.get(0);
        assertNotNull(serviceInterface);

        // Operation
        final Collection<IOperation> operations = serviceInterface.getAllOperations();
        assertTrue(operations.size() == 5);
        IOperation[] operationArray = new IOperation[5];
        operations.toArray(operationArray);
        assertEquals(null, operationArray[0].getName());
        assertEquals("", operationArray[1].getName());
        List<IOperation> ops = serviceInterface.getOperation("");
        assertFalse(ops.isEmpty());
        IOperation operation = ops.get(0);
        assertNotNull("Null returned for operation with name ''", operation);
        ops = serviceInterface.getOperation(null);
        assertFalse(ops.isEmpty());
        assertNotNull(ops.get(0));

        // In Parameters
        ops = serviceInterface.getOperation("Operation");
        assertFalse(ops.isEmpty());
        operation = ops.get(0);
        assertNotNull(operation);
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(4, parameters.size());
        IParameter[] parameterArray = new IParameter[4];
        parameters.toArray(parameterArray);
        assertEquals("", parameterArray[0].getName());
        assertEquals("", parameterArray[1].getName());
        assertEquals("parameterOne", parameterArray[2].getName());
        assertEquals("parameterTwo", parameterArray[3].getName());
        List<IParameter> params = operation.getInputParameter("");
        assertFalse(params.isEmpty());
        IParameter parameter = params.get(0);
        assertNotNull("In Paramter is null for name ''", parameter);
        assertTrue("In Paramter is not null for null", operation.getInputParameter(null).isEmpty());

        // Out Parameters
        parameters = operation.getAllOutputParameters();
        assertEquals(4, parameters.size());
        parameterArray = new IParameter[4];
        parameters.toArray(parameterArray);
        assertEquals("", parameterArray[0].getName());
        assertEquals("", parameterArray[1].getName());
        assertEquals("parameterOne", parameterArray[2].getName());
        assertEquals("parameterTwo", parameterArray[3].getName());
        params = operation.getOutputParameter("");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull("Out Paramter is null for name ''", parameter);
        assertTrue("Out Paramter is not null for null", operation.getOutputParameter(null).isEmpty());

        // Faults
        final Collection<IFault> faults = operation.getAllFaults();
        assertEquals(3, faults.size());
        IFault[] faultArray = new IFault[3];
        faults.toArray(faultArray);
        assertEquals(null, faultArray[0].getName());
        assertEquals("", faultArray[1].getName());
        assertEquals("fault", faultArray[2].getName());
        List<IFault> flts = operation.getFault("");
        assertFalse(flts.isEmpty());
        IFault fault = flts.get(0);
        assertNotNull("Fault is null for name ''", fault);
        flts = operation.getFault(null);
        assertFalse(flts.isEmpty());
        fault = flts.get(0);
        assertNotNull("Fault is null for null", fault);

        // Fault Parameters
        fault = faultArray[2];
        parameters = fault.getParameters();
        assertEquals(4, parameters.size());
        parameterArray = new IParameter[4];
        parameters.toArray(parameterArray);
        assertEquals("", parameterArray[0].getName());
        assertEquals("", parameterArray[1].getName());
        assertEquals("parameterOne", parameterArray[2].getName());
        assertEquals("parameterTwo", parameterArray[3].getName());
        List<IParameter> prms = fault.getParameter("");
        assertFalse(prms.isEmpty());
        parameter = prms.get(0);
        assertNotNull("Fault Paramter is null for name ''", parameter);
        assertTrue("Fault Parameter is not null for null", fault.getParameter(null).isEmpty());
    }

    @Test
    public void testNoParameterType() throws Exception {
        final IDescription description = getModelRoot("InvalidWSDL.wsdl");
        IServiceInterface serviceInterface = description.getInterface("PortType").get(0);

        IOperation operation = serviceInterface.getOperation("MissingParameterType").get(0);

        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(5, parameters.size());
        List<IParameter> params = operation.getInputParameter("missingTypeParameter");
        assertFalse(params.isEmpty());
        IParameter parameter = params.get(0);
        assertNotNull(parameter);
        IType type = parameter.getType();
        assertNull("Type returned is not null", type);
        params = operation.getInputParameter("missingTypeParameterOne");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = operation.getInputParameter("missingTypeParameterTwo");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = operation.getInputParameter("correctParameter");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull("Type returned is null", type);

        parameters = operation.getAllOutputParameters();
        assertEquals(5, parameters.size());
        params = operation.getOutputParameter("missingTypeParameter");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNull("Type returned is not null", type);
        params = operation.getOutputParameter("missingTypeParameterOne");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = operation.getOutputParameter("missingTypeParameterTwo");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = operation.getOutputParameter("correctParameter");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull("Type returned is null", type);

        IFault fault = operation.getFault("fault").get(0);

        parameters = fault.getParameters();
        assertEquals(5, parameters.size());
        params = fault.getParameter("missingTypeParameter");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNull(type);
        params = fault.getParameter("missingTypeParameterOne");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = fault.getParameter("missingTypeParameterTwo");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull(type);
        assertTrue(type instanceof UnresolvedType);
        params = fault.getParameter("correctParameter");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        type = parameter.getType();
        assertNotNull("Type returned is null", type);
    }

    @Test
    public void testNoMessage() throws Exception {
        final IDescription description = getModelRoot("InvalidWSDL.wsdl");
        IServiceInterface serviceInterface = description.getInterface("PortType").get(0);

        IOperation operation = serviceInterface.getOperation("MissingMessage").get(0);

        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(0, parameters.size());
        parameters = operation.getAllOutputParameters();
        assertEquals(0, parameters.size());

        IFault fault = operation.getFault("fault").get(0);
        parameters = fault.getParameters();
        assertEquals(0, parameters.size());
    }

    private IDescription getModelRoot(String fileName) throws Exception {
        final String fullPath = Constants.DATA_PUBLIC_SELF_KESHAV + fileName;
        final IWsdlModelRoot modelRoot = getWSDLModelRoot(fullPath, fileName);
        assertNotNull("Could not Acquire Model Root for " + fullPath, modelRoot);
        return modelRoot.getDescription();
    }

}