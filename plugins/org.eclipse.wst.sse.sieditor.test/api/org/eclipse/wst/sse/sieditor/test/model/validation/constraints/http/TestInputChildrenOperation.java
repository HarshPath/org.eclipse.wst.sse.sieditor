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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.InputChildrenOperation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.http.HTTPOperation;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlEncoded;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestInputChildrenOperation extends BasicConstraintTest{
	

	@Test
	public void testHasHTTPOperation() throws Exception{
		WSDLElement child = createChild(HTTPUrlEncoded.class, true, true, true);
		
		IValidationContext validationContext = createValidationContext(true, child);
		InputChildrenOperation constraint = new InputChildrenOperation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testNoHTTPOperation() throws Exception{
		WSDLElement child = createChild(HTTPUrlEncoded.class, true, true, false);
		
		IValidationContext validationContext = createValidationContext(true, child);
		InputChildrenOperation constraint = new InputChildrenOperation();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@Test
	public void testNoBindingInput() throws Exception{
		WSDLElement child = createChild(HTTPUrlEncoded.class, false, false, false);
		
		IValidationContext validationContext = createValidationContext(true, child);
		InputChildrenOperation constraint = new InputChildrenOperation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	@Test
	public void testNoBindingOperation() throws Exception{
		WSDLElement child = createChild(HTTPUrlEncoded.class, true, false, false);
		
		IValidationContext validationContext = createValidationContext(true, child);
		InputChildrenOperation constraint = new InputChildrenOperation();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	private static WSDLElement createChild(Class<? extends WSDLElement> childClass, boolean hasBindingInput, boolean hasBindingOperation, boolean hasHTTPOperation) {
		WSDLElement child = EasyMockUtils.createNiceMock(childClass);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getLocalName()).andStubReturn(childClass.getSimpleName());
		EasyMock.expect(child.getElement()).andStubReturn(domElement);
		
		if (hasBindingInput) {
			BindingInput bindingInput = EasyMockUtils.createNiceMock(BindingInput.class);
			EasyMock.expect(child.getContainer()).andStubReturn(bindingInput);
			if (hasBindingOperation) {
				BindingOperation bindingOperation = EasyMockUtils.createNiceMock(BindingOperation.class);
				EasyMock.expect(bindingInput.getContainer()).andStubReturn(bindingOperation);
				SimpleEList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
				EasyMock.expect(bindingOperation.getEExtensibilityElements()).andStubReturn(extensibilityElements);
				if (hasHTTPOperation) {
					extensibilityElements.add(EasyMockUtils.createNiceMock(HTTPOperation.class));
				}
			}
		}
		
		return child;
	}

}
