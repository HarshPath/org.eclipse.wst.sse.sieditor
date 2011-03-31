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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.HeaderMessage;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestHeaderMessage extends BasicConstraintTest{
	
	@Test
	public void testNoMessage() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, false, null).getSeverity());
	}
	
	@Test
	public void testNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, true, null).getSeverity());
	}
	
	@Test
	public void testNoMessageNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, false, null).getSeverity());
	}
	
	@Test
	public void testNoMessageNotNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeader.class, false, "mymessage").getSeverity());
	}
	
	@Test
	public void testHeaderFaultNoMessageNotNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, false, "mymessage").getSeverity());
	}
	
	@Test
	public void testHeaderFaultNoMessage() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, false, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, true, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultNoMessageNullMessageAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, false, null).getSeverity());
	}
	
	

	
	private IStatus invokeValidation(Class<? extends SOAPHeaderBase> headerClass, boolean hasMessage, String messageAttributeValue) throws Exception {
		SOAPHeaderBase header = EasyMockUtils.createNiceMock(headerClass);
		
		if (hasMessage) {
			EasyMock.expect(header.getEMessage()).andStubReturn(EasyMockUtils.createNiceMock(Message.class));
		}
		
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getAttribute("message")).andStubReturn(messageAttributeValue);
		EasyMock.expect(header.getElement()).andStubReturn(domElement);
		
		IValidationContext validationContext = createValidationContext(true, header);
		HeaderMessage constraint = new HeaderMessage();
		return constraint.validate(validationContext);
	}
}
