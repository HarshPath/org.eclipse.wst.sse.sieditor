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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.BodyEncodingStyle;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.junit.Assert;
import org.junit.Test;


public class TestBodyEncodingStyle extends BasicConstraintTest{

	@Test
	public void testEncodedOK() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("encoded", true));
	}
	
	@Test
	public void testEncodedErr() throws Exception {
		Assert.assertEquals(IStatus.ERROR, invokeValidation("encoded", false));
	}
	
	@Test
	public void testNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation("literal", false));
	}
	
	@Test
	public void testNull() throws Exception {
		Assert.assertEquals(IStatus.OK, invokeValidation(null, false));
	}
	
	@SuppressWarnings("unchecked")
	private int invokeValidation(String use, boolean hasEncodingStyles) throws Exception {
		SOAPBody body = EasyMockUtils.createNiceMock(SOAPBody.class);
		EasyMock.expect(body.getUse()).andStubReturn(use);
		
		List encodingStyles = new ArrayList();
		EasyMock.expect(body.getEncodingStyles()).andStubReturn(encodingStyles);
		if (hasEncodingStyles) {
			encodingStyles.add(0);
		}
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyEncodingStyle constraint = new BodyEncodingStyle();
		
		return constraint.validate(validationContext).getSeverity();
	}
}
