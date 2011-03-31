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
import org.eclipse.wst.sse.sieditor.model.validation.constraints.soap.BodyParts;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.BasicConstraintTest;
import org.eclipse.wst.sse.sieditor.test.util.EasyMockUtils;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;


public class TestBodyParts extends BasicConstraintTest{
	
	@Test
	public void testOK() throws Exception {
		Assert.assertEquals(IStatus.OK, executeValidation(" p1 p3 p2 ", "p3", "p1", "p2").getSeverity());
	}
	
	@Test
	public void testError() throws Exception {
		IStatus status = executeValidation(" my_constraint another_constraint p1 ", "p3", "p1", "p2");
		Assert.assertEquals(true, status.isMultiStatus());
		Assert.assertEquals(2, status.getChildren().length);
	}
	
	@Test
	public void testNoPartsAttribute() throws Exception {
		Assert.assertEquals(IStatus.OK, executeValidation(null, "p3", "p1", "p2").getSeverity());
	}	
	
	private IStatus executeValidation(String partsAttributeValue, String...partNames) throws Exception {
		SOAPBody body = EasyMockUtils.createNiceMock(SOAPBody.class);
		Element domElement = EasyMockUtils.createNiceMock(Element.class);
		EasyMock.expect(domElement.getAttribute("parts")).andStubReturn(partsAttributeValue);
		EasyMock.expect(body.getElement()).andStubReturn(domElement);
		
		List<Part> parts = new ArrayList<Part>();
		EasyMock.expect(body.getParts()).andStubReturn(parts);
		
		if (partNames != null) {
			for (String partName: partNames) {
				Part part = EasyMockUtils.createNiceMock(Part.class);
				EasyMock.expect(part.getName()).andStubReturn(partName);
				parts.add(part);
			}
		}
		
		IValidationContext validationContext = createValidationContext(true, body);
		BodyParts constraint = new BodyParts();	
		return constraint.validate(validationContext);
	}

}
