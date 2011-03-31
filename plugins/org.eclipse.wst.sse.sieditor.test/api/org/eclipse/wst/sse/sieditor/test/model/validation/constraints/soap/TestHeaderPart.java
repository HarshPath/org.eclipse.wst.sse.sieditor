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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.HeaderPart;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestHeaderPart extends BasicConstraintTest{
	
	@Test
	public void testNoMessage() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, false, false, null).getSeverity());
	}
	
	@Test
	public void testWithPart() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, true, true, null).getSeverity());
	}
	
	@Test
	public void testNullPartAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, true, false, null).getSeverity());
	}
	
	@Test
	public void testNotNullPartAttribute() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeader.class, true, false, "mypart").getSeverity());
	}
	
	@Test
	public void testHeaderFaultNoMessage() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, false, false, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultWithPart() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, true, true, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultNullPartAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, true, false, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultNotNullPartAttribute() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, true, false, "mypart").getSeverity());
	}	

	private IStatus invokeValidation(Class<? extends SOAPHeaderBase> headerClass, boolean hasMessage, boolean hasPart, String partAttributeValue) throws Exception {
		SOAPHeaderBase header = EasyMockUtils.createNiceMock(headerClass);
		
		if (hasMessage) {
			Message message = EasyMockUtils.createNiceMock(Message.class);
			EasyMock.expect(message.getQName()).andStubReturn(new QName("mymessage"));
			EasyMock.expect(header.getEMessage()).andStubReturn(message);
			
			if (hasPart) {
				EasyMock.expect(header.getEPart()).andStubReturn(EasyMockUtils.createNiceMock(Part.class));
			}
		}
		
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getAttribute("part")).andStubReturn(partAttributeValue);
		EasyMock.expect(header.getElement()).andStubReturn(domElement);
		
		IValidationContext validationContext = createValidationContext(true, header);
		HeaderPart constraint = new HeaderPart();
		return constraint.validate(validationContext);
	}
}
