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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.FaultName;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.junit.Assert;
import org.junit.Test;


public class TestFaultName extends BasicConstraintTest{

	
	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("fault", "fault", BindingFault.class).getSeverity());
	}
	
	@Test
	public void testNullFaultName() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(null, "fault", BindingFault.class).getSeverity());
	}	
	
	@Test
	public void testDifferentNames() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation("fault", "another_fault", BindingFault.class).getSeverity());
	}
	
	@Test
	public void testNoContainer() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("fault", "fault", null).getSeverity());
	}
	
	@Test
	public void testDifferentContainer() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("fault", "fault", SOAPFault.class).getSeverity());
	}	
	
	private IStatus invokeValidation(String soapFaultName, String bindingFaultName, Class<? extends WSDLElement> parentClass) throws Exception {
		SOAPFault fault = EasyMockUtils.createNiceMock(SOAPFault.class);
		EasyMock.expect(fault.getName()).andStubReturn(soapFaultName);
		
		if (parentClass != null) {
			WSDLElement parent = EasyMockUtils.createNiceMock(parentClass);
			EasyMock.expect(fault.getContainer()).andStubReturn(parent);
			if (parentClass == BindingFault.class) {
				EasyMock.expect(((BindingFault) parent).getName()).andStubReturn(bindingFaultName);
			}
		}
		
		IValidationContext validationContext = createValidationContext(true, fault);
		FaultName constraint = new FaultName();
		return constraint.validate(validationContext);
	}
}
