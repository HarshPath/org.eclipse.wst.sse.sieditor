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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

/**
 *
 * 
 */
public class ReadOfDuplicateSITest extends SIEditorBaseTest{

   /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description#getInterface(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test
    public void testGetInterface() throws URISyntaxException, IOException, CoreException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        final String SI_NAME = "DuplicateNameSI";
        
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface(SI_NAME); //$NON-NLS-1$
        
        assertEquals(2, interfaces.size());
        for (IServiceInterface sInterface : interfaces) {
            assertNotNull(sInterface);
            assertNotNull(sInterface.getComponent());
            assertEquals(SI_NAME,sInterface.getName());
        }
    }
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface#getOperation(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test 
    public void testGetOperation() throws IOException, CoreException{
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface("DuplicateNameSI"); //$NON-NLS-1$
        IServiceInterface sInterface = interfaces.get(0);
        
        final String OP_NAME = "NewOperation";
        List<IOperation> parameters = sInterface.getOperation(OP_NAME); //$NON-NLS-1$
     
        assertEquals(2, parameters.size());
        for (IOperation parameter : parameters) {
            assertNotNull(parameter);
            assertNotNull(parameter.getComponent());
            assertEquals(OP_NAME, parameter.getName());
        }
    }
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation#getInputParameter(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test
    public void testGetInputParameter() throws IOException, CoreException{
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface("DuplicateNameSI"); //$NON-NLS-1$
        IOperation sInterface = interfaces.get(0).getOperation("NewOperation").get(0);
        
        final String PARAM_NAME = "Parameter";
        List<IParameter> parameters = sInterface.getInputParameter(PARAM_NAME); //$NON-NLS-1$
     
        assertEquals(2, parameters.size());
        for (IParameter parameter : parameters) {
            assertNotNull(parameter);
            assertNotNull(parameter.getComponent());
            assertEquals(PARAM_NAME, parameter.getName());
            assertEquals(IParameter.INPUT,parameter.getParameterType());
        }
    }
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation#getOutputParameter(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test
    public void testGetOutputParameter() throws IOException, CoreException{
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface("DuplicateNameSI"); //$NON-NLS-1$
        IOperation sInterface = interfaces.get(0).getOperation("NewOperation").get(0);
        
        final String PARAM_NAME = "Parameter"; //$NON-NLS-1$
        List<IParameter> parameters = sInterface.getOutputParameter(PARAM_NAME);
     
        assertEquals(2, parameters.size());
        for (IParameter parameter : parameters) {
            assertNotNull(parameter);
            assertNotNull(parameter.getComponent());
            assertEquals(PARAM_NAME, parameter.getName());
            assertEquals(IParameter.OUTPUT,parameter.getParameterType());
        }
    }
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation#getFault(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test
    public void testGetFault() throws IOException, CoreException{
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface("DuplicateNameSI"); //$NON-NLS-1$
        IOperation sInterface = interfaces.get(0).getOperation("NewOperation").get(0);
        
        final String PARAM_NAME = "Fault1";//$NON-NLS-1$
        List<IFault> parameters = sInterface.getFault(PARAM_NAME); 
     
        assertEquals(2, parameters.size());
        for (IFault fault : parameters) {
            assertNotNull(fault);
            assertNotNull(fault.getComponent());
            assertEquals(PARAM_NAME, fault.getName());
        }
    }
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault#getParameter(java.lang.String)}
     * .
     * 
     * @throws URISyntaxException
     * @throws CoreException 
     * @throws IOException 
     */
    @Test
    public void testGetFaultParameter() throws URISyntaxException, IOException, CoreException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/DuplicateNameSI.wsdl", "DuplicateNameSI.wsdl");
        List<IServiceInterface> interfaces = wsdlModelRoot.getDescription().getInterface("DuplicateNameSI"); //$NON-NLS-1$
        IFault fault = interfaces.get(0).getAllOperations().iterator().next().getAllFaults().iterator().next();
        
        final String PARAM_NAME = "Parameter";
        List<IParameter> parameters = fault.getParameter(PARAM_NAME); //$NON-NLS-1$
     
        assertEquals(2, parameters.size());
        for (IParameter parameter : parameters) {
            assertNotNull(parameter);
            assertNotNull(parameter.getComponent());
            assertEquals(PARAM_NAME, parameter.getName());
            assertEquals(IParameter.FAULT, parameter.getParameterType());
        }
    }
}
