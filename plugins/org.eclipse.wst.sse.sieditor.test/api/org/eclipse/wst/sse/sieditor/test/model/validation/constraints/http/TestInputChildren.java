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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.InputChildren;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlEncoded;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlReplacement;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestInputChildren extends BasicConstraintTest{

	@Test
	public void testUrlEncodedOK() throws Exception{
		HTTPUrlEncoded element = createChild(HTTPUrlEncoded.class);
		IValidationContext validationContext = createValidationContext(true, element);
		InputChildren constraint = new InputChildren();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testUrlReplacementOK() throws Exception{
		HTTPUrlReplacement element = createChild(HTTPUrlReplacement.class);
		IValidationContext validationContext = createValidationContext(true, element);
		InputChildren constraint = new InputChildren();
		
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUrlEncodedErr() throws Exception{
		HTTPUrlEncoded element = createChild(HTTPUrlEncoded.class, HTTPUrlReplacement.class);
		IValidationContext validationContext = createValidationContext(true, element);
		InputChildren constraint = new InputChildren();
		
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUrlReplacementErr() throws Exception{
		HTTPUrlReplacement element = createChild(HTTPUrlReplacement.class, HTTPUrlEncoded.class);
		IValidationContext validationContext = createValidationContext(true, element);
		InputChildren constraint = new InputChildren();
		
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}
	
	public static <T extends ExtensibilityElement> T createChild(Class<T> clazz, Class<? extends ExtensibilityElement>...extensibilityElementsClasses) {
		BindingInput input = EasyMockUtils.createNiceMock(BindingInput.class);
		T element = EasyMockUtils.createNiceMock(clazz);
		
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getLocalName()).andStubReturn(clazz.getName());
		EasyMock.expect(element.getElement()).andStubReturn(domElement);
		
		SimpleEList<ExtensibilityElement> list = new SimpleEList<ExtensibilityElement>();
		list.add(element);
		if (extensibilityElementsClasses != null) {
			for (Class<? extends ExtensibilityElement> aClass : extensibilityElementsClasses) {
				list.add(EasyMockUtils.createNiceMock(aClass));
			}
		}
		EasyMock.expect(input.getEExtensibilityElements()).andStubReturn(list);
		EasyMock.expect(element.eContainer()).andStubReturn(input);
		
		return element;
	}
}
