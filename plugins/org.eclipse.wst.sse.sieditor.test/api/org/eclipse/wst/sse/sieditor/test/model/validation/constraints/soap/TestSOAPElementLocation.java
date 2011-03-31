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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.SOAPElementLocation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.BindingOutput;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPAddress;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPOperation;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestSOAPElementLocation extends BasicConstraintTest{

	@Test
	public void testPortOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPAddress.class, Port.class).getSeverity());
	}
	
	@Test
	public void testPortErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPBinding.class, Port.class).getSeverity());
	}
	
	@Test
	public void testBindingOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPBinding.class, Binding.class).getSeverity());
	}
	
	@Test
	public void testBindingErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPAddress.class, Binding.class).getSeverity());
	}
	
	@Test
	public void testBindingOperationOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPOperation.class, BindingOperation.class).getSeverity());
	}
	
	@Test
	public void testBindingOperationErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPBinding.class, BindingOperation.class).getSeverity());
	}
	
	@Test
	public void testBindingInputOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPBody.class, BindingInput.class).getSeverity());
		tearDown();
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, BindingInput.class).getSeverity());
		tearDown();
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, BindingInput.class).getSeverity());
	}
	
	@Test
	public void testBindingOutputOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPBody.class, BindingOutput.class).getSeverity());
		tearDown();
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, BindingOutput.class).getSeverity());
		tearDown();
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, BindingOutput.class).getSeverity());
	}
	
	@Test
	public void testBindingInputErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPFault.class, BindingInput.class).getSeverity());
	}
	
	@Test
	public void testBindingOutputErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPFault.class, BindingOutput.class).getSeverity());
	}	
	
	@Test
	public void testBindingFaultOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPFault.class, BindingFault.class).getSeverity());
	}
	
	@Test
	public void testBindingFaultErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, BindingFault.class).getSeverity());
	}	
	
	private <T extends WSDLElement> IStatus invokeValidation(Class<T> targetClass, Class<? extends WSDLElement> parentClass) throws Exception {
		T target = EasyMockUtils.createNiceMock(targetClass);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getLocalName()).andStubReturn(targetClass.getSimpleName());
		EasyMock.expect(target.getElement()).andStubReturn(domElement);
		WSDLElement parent = EasyMockUtils.createNiceMock(parentClass);
		EasyMock.expect(target.getContainer()).andStubReturn(parent);
		
		IValidationContext validationContext = createValidationContext(true, target);
		SOAPElementLocation constraint = new SOAPElementLocation();
		return constraint.validate(validationContext);
	}
	
}
