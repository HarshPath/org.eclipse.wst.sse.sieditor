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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.HeaderBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.ExtensibleElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.junit.Assert;
import org.junit.Test;


public class TestHeaderBinding extends BasicConstraintTest{
	@Test
	public void testWithBindingInputOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(true, false, true, true, true).getSeverity());
	}
	
	@Test
	public void testWithBindingOutputOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(false, true, true, true, true).getSeverity());
	}	
	
	@Test
	public void testNoInputOutput() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(false, false, true, true, true).getSeverity());
	}	
	
	@Test
	public void testNoOperation() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(true, false, false, true, true).getSeverity());
	}
	
	@Test
	public void testNoBinding() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(true, true, true, false, true).getSeverity());
	}	
	
	@Test
	public void testNoSOAPBinding() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(true, true, true, true, false).getSeverity());
	}
	
	private IStatus invokeValidation(boolean hasInput, boolean hasOutput, boolean hasBindingOperation, boolean hasBinding, boolean hasSOAPBinding) throws Exception {
		SOAPHeader header = createSOAPHeader(hasInput, hasOutput, hasBindingOperation, hasBinding, hasSOAPBinding);
		
		IValidationContext validationContext = createValidationContext(true, header);
		HeaderBinding constraint = new HeaderBinding();
		return constraint.validate(validationContext);
	}

	private SOAPHeader createSOAPHeader(boolean hasInput, boolean hasOutput, boolean hasBindingOperation, boolean hasBinding, boolean hasSOAPBinding) {
		
		SOAPHeader header = EasyMockUtils.createNiceMock(SOAPHeader.class);
		if (hasInput || hasOutput) {
			ExtensibleElement inout = null;
			if (hasInput) {
				inout = EasyMockUtils.createNiceMock(BindingInput.class);
			} else {
				inout = EasyMockUtils.createNiceMock(BindingInput.class);
			}
			EasyMock.expect(header.getContainer()).andStubReturn(inout);
			
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
			
		return header;
	}
}
