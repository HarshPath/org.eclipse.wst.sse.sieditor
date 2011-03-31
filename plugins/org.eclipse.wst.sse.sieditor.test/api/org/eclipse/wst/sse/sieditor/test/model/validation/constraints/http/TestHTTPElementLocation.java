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

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.HTTPElementLocation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.eclipse.wst.wsdl.binding.http.HTTPOperation;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlEncoded;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlReplacement;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestHTTPElementLocation extends BasicConstraintTest{

	@Test
	public void testPortOK() throws Exception {
		WSDLElement target = createTarget(HTTPAddress.class, Port.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testPortErr() throws Exception {
		WSDLElement target = createTarget(HTTPBinding.class, Port.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testBindingOK() throws Exception {
		WSDLElement target = createTarget(HTTPBinding.class, Binding.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testBindingErr() throws Exception {
		WSDLElement target = createTarget(HTTPAddress.class, Binding.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testBindingOperationOK() throws Exception {
		WSDLElement target = createTarget(HTTPOperation.class, BindingOperation.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testBindingOperationErr() throws Exception {
		WSDLElement target = createTarget(HTTPBinding.class, BindingOperation.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testBindingInputOK() throws Exception {
		WSDLElement target = createTarget(HTTPUrlEncoded.class, BindingInput.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
		
		tearDown();
		
		target = createTarget(HTTPUrlReplacement.class, BindingInput.class);
		validationContext = createValidationContext(true, target);
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testBindingInputErr() throws Exception {
		WSDLElement target = createTarget(HTTPAddress.class, BindingInput.class);
		
		IValidationContext validationContext = createValidationContext(true, target);
		HTTPElementLocation constraint = new HTTPElementLocation();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}		
	
	public <T extends WSDLElement> T createTarget(Class<T> targetClass, Class<? extends WSDLElement> parentClass) {
		T target = EasyMockUtils.createNiceMock(targetClass);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getLocalName()).andStubReturn(targetClass.getSimpleName());
		EasyMock.expect(target.getElement()).andStubReturn(domElement);
		WSDLElement parent = EasyMockUtils.createNiceMock(parentClass);
		EasyMock.expect(target.getContainer()).andStubReturn(parent);
		
		return target;
	}
}
