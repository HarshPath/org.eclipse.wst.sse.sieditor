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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.PortAddress;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.soap.SOAPAddress;
import org.junit.Assert;
import org.junit.Test;


public class TestPortAddress extends BasicConstraintTest{

	@Test
	public void testIsAddress() {
		HTTPAddress httpAddress = EasyMockUtils.createNiceMock(HTTPAddress.class);
		SOAPAddress soapAddress = EasyMockUtils.createNiceMock(SOAPAddress.class);
		ExtensibilityElement extensibilityElement = EasyMockUtils.createNiceMock(ExtensibilityElement.class);
		
		EasyMockUtils.replay();
		
		PortAddress constraint = new PortAddress();
		Assert.assertEquals(true, constraint.isAddress(httpAddress));
		Assert.assertEquals(true, constraint.isAddress(soapAddress));
		Assert.assertFalse(constraint.isAddress(extensibilityElement));
	}
	
	@Test
	public void testSOAPAddressOK() throws Exception{
		SOAPAddress soapAddress = EasyMockUtils.createNiceMock(SOAPAddress.class);
		testAddressOK(soapAddress);
	}
	
	@Test
	public void testHTTPAddressOK() throws Exception{
		HTTPAddress httpAddress = EasyMockUtils.createNiceMock(HTTPAddress.class);
		testAddressOK(httpAddress);
	}
	
	@Test
	public void testNoExtensibilityElements() throws Exception{
		Port port = EasyMockUtils.createNiceMock(Port.class);
		EList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		EasyMock.expect(port.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		
		IValidationContext validationContext = createValidationContext(true, port);
		PortAddress constraint = new PortAddress();
		IStatus status = constraint.validate(validationContext);
		Assert.assertEquals(true, status.getSeverity() == IStatus.ERROR);
	}
	
	@Test
	public void testNoAddressElements() throws Exception{
		Port port = EasyMockUtils.createNiceMock(Port.class);
		EList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		for (int i = 0; i < 3; i++) {
			extensibilityElements.add(EasyMockUtils.createNiceMock(ExtensibilityElement.class));
		}
		EasyMock.expect(port.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		
		IValidationContext validationContext = createValidationContext(true, port);
		PortAddress constraint = new PortAddress();
		IStatus status = constraint.validate(validationContext);
		Assert.assertEquals(true, status.getSeverity() == IStatus.ERROR);
	}
	
	@Test
	public void testMultipleAddressElements() throws Exception{
		Port port = EasyMockUtils.createNiceMock(Port.class);
		EList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		
		extensibilityElements.add(EasyMockUtils.createNiceMock(ExtensibilityElement.class));
		extensibilityElements.add(EasyMockUtils.createNiceMock(HTTPAddress.class));
		extensibilityElements.add(EasyMockUtils.createNiceMock(SOAPAddress.class));

		EasyMock.expect(port.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		
		IValidationContext validationContext = createValidationContext(true, port);
		PortAddress constraint = new PortAddress();
		IStatus status = constraint.validate(validationContext);
		Assert.assertEquals(true, status.getSeverity() == IStatus.ERROR);
	}
	
	
	private void testAddressOK(ExtensibilityElement address) throws Exception {
		Port port = EasyMockUtils.createNiceMock(Port.class);
		EList<ExtensibilityElement> extensibilityElements = new SimpleEList<ExtensibilityElement>();
		extensibilityElements.add(address);
		EasyMock.expect(port.getEExtensibilityElements()).andStubReturn(extensibilityElements);
		
		IValidationContext validationContext = createValidationContext(true, port);
		PortAddress constraint = new PortAddress();
		IStatus status = constraint.validate(validationContext);
		Assert.assertEquals(true, status.isOK());		
	}
	
}
