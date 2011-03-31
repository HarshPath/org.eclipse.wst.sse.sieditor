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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.AddressLocationURI;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.binding.soap.SOAPAddress;
import org.junit.Assert;
import org.junit.Test;


public class TestAddressLocationURI extends BasicConstraintTest{

	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(true, invokeConstraint("mylocation").isOK());
	}
	
	@Test
	public void testNull() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeConstraint(null).getSeverity());
	}
	
	@Test
	public void testEmpty() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeConstraint("").getSeverity());
	}
	
	@Test
	public void testWhitespaces() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeConstraint(" 	").getSeverity());
	}
	
	private IStatus invokeConstraint(String locationURI) throws Exception{
		SOAPAddress address = EasyMockUtils.createNiceMock(SOAPAddress.class);
		EasyMock.expect(address.getLocationURI()).andStubReturn(locationURI);
		
		IValidationContext validationContext = createValidationContext(true, address);
		AddressLocationURI constraint = new AddressLocationURI();
		return constraint.validate(validationContext);
	}
}
