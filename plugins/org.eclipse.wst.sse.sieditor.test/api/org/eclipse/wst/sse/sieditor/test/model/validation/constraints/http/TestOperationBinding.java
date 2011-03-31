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
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.OperationBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.eclipse.wst.wsdl.binding.http.HTTPOperation;
import org.junit.Assert;
import org.junit.Test;


public class TestOperationBinding extends BasicConstraintTest{
	
	@Test
	public void testHTTPBindingOK() throws Exception{
		HTTPOperation httpOperation = createHTTPOperation(true);
		
		IValidationContext validationContext = createValidationContext(true, httpOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testHTTPBindingMissing() throws Exception{
		HTTPOperation httpOperation = createHTTPOperation(false);
		
		IValidationContext validationContext = createValidationContext(true, httpOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testHTTPOperationAtWrongPlace() throws Exception{
		
		Operation operation = EasyMockUtils.createNiceMock(Operation.class);
		HTTPOperation httpOperation = EasyMockUtils.createNiceMock(HTTPOperation.class);
		
		EasyMock.expect(httpOperation.getContainer()).andStubReturn(operation);
		EasyMock.expect(operation.getContainer()).andStubReturn(EasyMockUtils.createNiceMock(Definition.class));
		
		IValidationContext validationContext = createValidationContext(true, httpOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testGetBindingOK() throws Exception{
		HTTPOperation httpOperation = createHTTPOperation(true);
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNotNull(constraint.getBinding(httpOperation));
	}
	
	@Test
	public void testGetBindingNoOperationBinding() throws Exception{
		BindingOperation operation = EasyMockUtils.createNiceMock(BindingOperation.class);
		HTTPOperation httpOperation = EasyMockUtils.createNiceMock(HTTPOperation.class);
		
		EasyMock.expect(httpOperation.getContainer()).andStubReturn(operation);
		
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNull(constraint.getBinding(httpOperation));
	}
	
	@Test
	public void testGetBindingNoOperation() throws Exception{
		HTTPOperation httpOperation = EasyMockUtils.createNiceMock(HTTPOperation.class);
		
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNull(constraint.getBinding(httpOperation));
	}
	
	private HTTPOperation createHTTPOperation(boolean hasHTTPBinding) {
		Binding binding = EasyMockUtils.createNiceMock(Binding.class);
		SimpleEList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		if (hasHTTPBinding) {
			extensibilityElements.add(EasyMockUtils.createNiceMock(HTTPBinding.class));
		}
		EasyMock.expect(binding.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		EasyMock.expect(binding.getQName()).andStubReturn(new QName("myBinding"));
		
		BindingOperation operation = EasyMockUtils.createNiceMock(BindingOperation.class);
		HTTPOperation httpOperation = EasyMockUtils.createNiceMock(HTTPOperation.class);
		
		EasyMock.expect(httpOperation.getContainer()).andStubReturn(operation);
		EasyMock.expect(operation.getContainer()).andStubReturn(binding);
		
		return httpOperation;
	}

}
