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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.BindingTransport;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.junit.Assert;
import org.junit.Test;


public class TestBindingTransport extends BasicConstraintTest{
	
	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("transportURL"));
	}
	
	@Test
	public void testNull() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(null));
	}
	
	@Test
	public void testEmpty() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(""));
	}
	
	@Test
	public void testWhitespaces() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(" 	"));
	}
	
	private int invokeValidation(String transport) throws Exception {
		SOAPBinding binding = EasyMockUtils.createNiceMock(SOAPBinding.class);
		EasyMock.expect(binding.getTransportURI()).andStubReturn(transport);
		
		IValidationContext validationContext = createValidationContext(true, binding);
		BindingTransport constraint = new BindingTransport();
		return constraint.validate(validationContext).getSeverity();
	}

}
