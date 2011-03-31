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

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.HeaderEncodedUseEncodingStyle;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.sse.sieditor.test.util.SimpleEList;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.junit.Test;


public class TestHeaderEncodedUseEncodingStyle extends BasicConstraintTest{
	
	@Test
	public void testEncodedOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, "encoded", true).getSeverity());
	}
	
	@Test
	public void testEncodedWithoutEncodingStyles() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeader.class, "encoded", false).getSeverity());
	}
	
	@Test
	public void testNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, "literal", false).getSeverity());
	}
	
	@Test
	public void testNullEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeader.class, null, false).getSeverity());
	}	
	
	@Test
	public void testHeaderFaultEncodedOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, "encoded", true).getSeverity());
	}
	
	@Test
	public void testHeaderFaultEncodedWithoutEncodingStyles() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation(SOAPHeaderFault.class, "encoded", false).getSeverity());
	}
	
	@Test
	public void testHeaderFaultNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, "literal", false).getSeverity());
	}	
	
	@Test
	public void testHeaderFaultNullEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(SOAPHeaderFault.class, null, false).getSeverity());
	}	
	
	private IStatus invokeValidation(Class<? extends SOAPHeaderBase> headerClass, String use, boolean hasEncodingStyle) throws Exception {
		SOAPHeaderBase header = EasyMockUtils.createNiceMock(headerClass);
		EasyMock.expect(header.getUse()).andStubReturn(use);
		SimpleEList<Object> encodingStyles = new SimpleEList<Object>();
		EasyMock.expect(header.getEEncodingStyles()).andStubReturn(encodingStyles);
		if (hasEncodingStyle) {
			encodingStyles.add(1);
		}
		
		IValidationContext validationContext = createValidationContext(true, header);
		HeaderEncodedUseEncodingStyle constraint = new HeaderEncodedUseEncodingStyle();
		return constraint.validate(validationContext);
	}

}
