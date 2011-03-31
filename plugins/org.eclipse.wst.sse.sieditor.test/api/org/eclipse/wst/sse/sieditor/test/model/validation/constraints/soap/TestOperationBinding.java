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
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.OperationBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPOperation;
import org.junit.Assert;
import org.junit.Test;


public class TestOperationBinding extends BasicConstraintTest{
	
	@Test
	public void testSOAPBindingOK() throws Exception{
		SOAPOperation SOAPOperation = createSOAPOperation(true);
		
		IValidationContext validationContext = createValidationContext(true, SOAPOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testSOAPBindingMissing() throws Exception{
		SOAPOperation SOAPOperation = createSOAPOperation(false);
		
		IValidationContext validationContext = createValidationContext(true, SOAPOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testSOAPOperationAtWrongPlace() throws Exception{
		
		Operation operation = EasyMockUtils.createNiceMock(Operation.class);
		SOAPOperation SOAPOperation = EasyMockUtils.createNiceMock(SOAPOperation.class);
		
		EasyMock.expect(SOAPOperation.getContainer()).andStubReturn(operation);
		EasyMock.expect(operation.getContainer()).andStubReturn(EasyMockUtils.createNiceMock(Definition.class));
		
		IValidationContext validationContext = createValidationContext(true, SOAPOperation);
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testGetBindingOK() throws Exception{
		SOAPOperation SOAPOperation = createSOAPOperation(true);
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNotNull(constraint.getBinding(SOAPOperation));
	}
	
	@Test
	public void testGetBindingNoOperationBinding() throws Exception{
		BindingOperation operation = EasyMockUtils.createNiceMock(BindingOperation.class);
		SOAPOperation SOAPOperation = EasyMockUtils.createNiceMock(SOAPOperation.class);
		
		EasyMock.expect(SOAPOperation.getContainer()).andStubReturn(operation);
		
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNull(constraint.getBinding(SOAPOperation));
	}
	
	@Test
	public void testGetBindingNoOperation() throws Exception{
		SOAPOperation SOAPOperation = EasyMockUtils.createNiceMock(SOAPOperation.class);
		
		EasyMockUtils.replay();
		OperationBinding constraint = new OperationBinding();
		
		Assert.assertNull(constraint.getBinding(SOAPOperation));
	}
	
	private SOAPOperation createSOAPOperation(boolean hasSOAPBinding) {
		Binding binding = EasyMockUtils.createNiceMock(Binding.class);
		SimpleEList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		if (hasSOAPBinding) {
			extensibilityElements.add(EasyMockUtils.createNiceMock(SOAPBinding.class));
		}
		EasyMock.expect(binding.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		EasyMock.expect(binding.getQName()).andStubReturn(new QName("myBinding"));
		
		BindingOperation operation = EasyMockUtils.createNiceMock(BindingOperation.class);
		SOAPOperation SOAPOperation = EasyMockUtils.createNiceMock(SOAPOperation.class);
		
		EasyMock.expect(SOAPOperation.getContainer()).andStubReturn(operation);
		EasyMock.expect(operation.getContainer()).andStubReturn(binding);
		
		return SOAPOperation;
	}

}
