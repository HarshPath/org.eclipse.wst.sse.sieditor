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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.BindingVerb;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.junit.Test;


public class TestBindingVerb extends BasicConstraintTest{

	@Test
	public void testGetOK() throws Exception{
		HTTPBinding binding = createBinding("GET");
		IValidationContext validationContext = createValidationContext(true, binding);
		
		BindingVerb constraint = new BindingVerb();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testPostOK() throws Exception{
		HTTPBinding binding = createBinding("POST");
		IValidationContext validationContext = createValidationContext(true, binding);
		
		BindingVerb constraint = new BindingVerb();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testInvalidValue() throws Exception{
		HTTPBinding binding = createBinding("SOMETHING");
		IValidationContext validationContext = createValidationContext(true, binding);
		
		BindingVerb constraint = new BindingVerb();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testNullValue() throws Exception{
		HTTPBinding binding = createBinding(null);
		IValidationContext validationContext = createValidationContext(true, binding);
		
		BindingVerb constraint = new BindingVerb();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	private HTTPBinding createBinding(String verb) {
		HTTPBinding binding = EasyMockUtils.createNiceMock(HTTPBinding.class);
		EasyMock.expect(binding.getVerb()).andStubReturn(verb);
		
		return binding;
	}
}
