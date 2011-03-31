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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.HeaderEncodedUseNamespaceURI;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.junit.Assert;
import org.junit.Test;


public class TestHeaderEncodedUseNamespaceURI extends BasicConstraintTest{

	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, "encoded", "myns").getSeverity());
	}
	
	@Test
	public void testNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, "literal", null).getSeverity());
	}	
	
	@Test
	public void testNullEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, null, null).getSeverity());
	}
	
	@Test
	public void testEncodedNoNamespace() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeader.class, "encoded", "  ").getSeverity());
	}
	
	@Test
	public void testEncodedNullNamespace() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeader.class, "encoded", null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, "encoded", "myns").getSeverity());
	}
	
	@Test
	public void testHeaderFaultNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, "literal", null).getSeverity());
	}	
	
	@Test
	public void testHeaderFaultNullEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, null, null).getSeverity());
	}
	
	@Test
	public void testHeaderFaultEncodedNoNamespace() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, "encoded", "  ").getSeverity());
	}
	
	@Test
	public void testHeaderFaultEncodedNullNamespace() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, "encoded", null).getSeverity());
	}	
	
	private IStatus invokeValidation(Class<? extends SOAPHeaderBase> headerClass, String use, String namespaceURI) throws Exception {
		SOAPHeaderBase header = EasyMockUtils.createNiceMock(headerClass);
		EasyMock.expect(header.getUse()).andStubReturn(use);
		EasyMock.expect(header.getNamespaceURI()).andStubReturn(namespaceURI);
		
		IValidationContext validationContext = createValidationContext(true, header);
		HeaderEncodedUseNamespaceURI constraint = new HeaderEncodedUseNamespaceURI();
		return constraint.validate(validationContext);
	}
}
