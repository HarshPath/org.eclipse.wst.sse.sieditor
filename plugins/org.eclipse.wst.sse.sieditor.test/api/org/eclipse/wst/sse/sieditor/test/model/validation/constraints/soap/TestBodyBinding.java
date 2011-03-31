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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.BodyBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.ExtensibleElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.junit.Assert;
import org.junit.Test;


public class TestBodyBinding extends BasicConstraintTest{
	@Test
	public void testWithBindingInputOK() throws Exception {
		SOAPBody body = createSOAPBody(true, false, true, true, true);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testWithBindingOutputOK() throws Exception {
		SOAPBody body = createSOAPBody(false, true, true, true, true);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	@Test
	public void testNoInputOutput() throws Exception {
		SOAPBody body = createSOAPBody(false, false, true, true, true);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	@Test
	public void testNoOperation() throws Exception {
		SOAPBody body = createSOAPBody(true, false, false, true, true);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testNoBinding() throws Exception {
		SOAPBody body = createSOAPBody(true, true, true, false, true);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	@Test
	public void testNoSOAPBinding() throws Exception {
		SOAPBody body = createSOAPBody(true, true, true, true, false);
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyBinding constraint = new BodyBinding();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}	

	private SOAPBody createSOAPBody(boolean hasInput, boolean hasOutput, boolean hasBindingOperation, boolean hasBinding, boolean hasSOAPBinding) {
		
		SOAPBody body = EasyMockUtils.createNiceMock(SOAPBody.class);
		if (hasInput || hasOutput) {
			ExtensibleElement inout = null;
			if (hasInput) {
				inout = EasyMockUtils.createNiceMock(BindingInput.class);
			} else {
				inout = EasyMockUtils.createNiceMock(BindingInput.class);
			}
			EasyMock.expect(body.getContainer()).andStubReturn(inout);
			
			if (hasBindingOperation) {
				BindingOperation bindingOperation = EasyMockUtils.createNiceMock(BindingOperation.class);
				EasyMock.expect(inout.getContainer()).andStubReturn(bindingOperation);
				
				if (hasBinding) {
					Binding binding = EasyMockUtils.createNiceMock(Binding.class);
					EasyMock.expect(bindingOperation.getContainer()).andStubReturn(binding);
					EasyMock.expect(binding.getQName()).andStubReturn(new QName("myBinding"));
					
					SimpleEList<ExtensibilityElement> elements = new SimpleEList<ExtensibilityElement>();
					EasyMock.expect(binding.getEExtensibilityElements()).andStubReturn(elements);
					
					if (hasSOAPBinding) {
						elements.add(EasyMockUtils.createNiceMock(SOAPBinding.class));
					}
						
				}
			}
		}
			
		return body;
	}
}
