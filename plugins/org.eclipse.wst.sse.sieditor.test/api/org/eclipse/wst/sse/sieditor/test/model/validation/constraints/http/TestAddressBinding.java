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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.http.AddressBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.junit.Assert;
import org.junit.Test;


public class TestAddressBinding extends BasicConstraintTest{
	
	@Test
	public void testOK() throws Exception {
		HTTPAddress address = createHTTPAddress(true, true, true);
		
		IValidationContext validationContext = createValidationContext(true, address);
		AddressBinding constraint = new AddressBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testNoPort() throws Exception {
		HTTPAddress address = createHTTPAddress(false, false, false);
		
		IValidationContext validationContext = createValidationContext(true, address);
		AddressBinding constraint = new AddressBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}	
	
	@Test
	public void testNoBinding() throws Exception {
		HTTPAddress address = createHTTPAddress(true, false, false);
		
		IValidationContext validationContext = createValidationContext(true, address);
		AddressBinding constraint = new AddressBinding();
		Assert.assertEquals(true, constraint.validate(validationContext).isOK());
	}
	
	@Test
	public void testNoHTTPBinding() throws Exception {
		HTTPAddress address = createHTTPAddress(true, true, false);
		
		IValidationContext validationContext = createValidationContext(true, address);
		AddressBinding constraint = new AddressBinding();
		Assert.assertEquals(IStatus.ERROR, constraint.validate(validationContext).getSeverity());
	}	

	private HTTPAddress createHTTPAddress(boolean hasPort, boolean hasBinding, boolean hasHttpBinding) {
		
		HTTPAddress address = EasyMockUtils.createNiceMock(HTTPAddress.class);
		if (hasPort) {
			Port port = EasyMockUtils.createNiceMock(Port.class);
			EasyMock.expect(address.getContainer()).andStubReturn(port);
			EasyMock.expect(port.getName()).andStubReturn("myPort");
			
			if (hasBinding) {
				Binding binding = EasyMockUtils.createNiceMock(Binding.class);
				EasyMock.expect(port.getEBinding()).andStubReturn(binding);
				EasyMock.expect(binding.getQName()).andStubReturn(new QName("myBinding"));
				
				SimpleEList<ExtensibilityElement> elements = new SimpleEList<ExtensibilityElement>();
				EasyMock.expect(binding.getExtensibilityElements()).andStubReturn(elements);
				
				if (hasHttpBinding) {
					elements.add(EasyMockUtils.createNiceMock(HTTPBinding.class));
				}
				
			}
		}
		
		return address;
	}
}
