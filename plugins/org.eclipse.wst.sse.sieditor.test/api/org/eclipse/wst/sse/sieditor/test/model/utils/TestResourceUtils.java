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
package org.eclipse.wst.sse.sieditor.test.model.utils;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;

public class TestResourceUtils extends SIEditorBaseTest {
	
	@Test 
	public void testCheckContentType() throws IOException, CoreException {
		final IFile xsdFile = org.eclipse.wst.sse.sieditor.test.util.ResourceUtils.copyFileIntoTestProject(
				"pub/xsd/example.xsd", 
				Document_FOLDER_NAME, this.getProject(), "example.xsd");
		
		final IFile wsdlFile = org.eclipse.wst.sse.sieditor.test.util.ResourceUtils.copyFileIntoTestProject(
				"pub/self/mix2/PurchaseOrderConfirmation.wsdl", 
				Document_FOLDER_NAME, this.getProject(), "PurchaseOrderConfirmation.wsdl");
		
		assertTrue(ResourceUtils.checkContentType(xsdFile, ResourceUtils.XSD_CONTENT_ID));
		assertFalse(ResourceUtils.checkContentType(wsdlFile, ResourceUtils.XSD_CONTENT_ID));
		
		assertTrue(ResourceUtils.checkContentType(wsdlFile, ResourceUtils.WSDL_CONTENT_ID));
		assertFalse(ResourceUtils.checkContentType(wsdlFile, ResourceUtils.XSD_CONTENT_ID));
	}
	
}
