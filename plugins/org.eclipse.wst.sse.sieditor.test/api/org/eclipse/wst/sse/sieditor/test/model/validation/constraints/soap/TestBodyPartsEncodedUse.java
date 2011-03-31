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

import javax.xml.namespace.QName;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.BodyPartsEncodedUse;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.junit.Assert;
import org.junit.Test;


public class TestBodyPartsEncodedUse extends BasicConstraintTest{

	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(IStatus.OK, executeValidation("encoded", "t1", "t2", "t3").getSeverity());
	}
	
	@Test
	public void testMissingTypes() throws Exception {
		IStatus status = executeValidation("encoded", null, null, "t3");
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals(2, status.getChildren().length);
	}
	
	@Test
	public void testNotEncoded() throws Exception {
		Assert.assertEquals(IStatus.OK, executeValidation("literal", null, null, null).getSeverity());
	}	
	
	private IStatus executeValidation(String use, String...typeNames) throws Exception {
		SOAPBody body = EasyMockUtils.createNiceMock(SOAPBody.class);
		EasyMock.expect(body.getUse()).andStubReturn(use);
		
		List<Part> parts = new ArrayList<Part>();
		EasyMock.expect(body.getParts()).andStubReturn(parts);
		
		for (String typeName : typeNames) {
			Part part = EasyMockUtils.createNiceMock(Part.class);
			if (typeName != null) {
				EasyMock.expect(part.getTypeName()).andStubReturn(new QName(typeName));
			}
			parts.add(part);
		}
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyPartsEncodedUse constraint = new BodyPartsEncodedUse();
		
		return constraint.validate(validationContext);
	}
}
