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
package org.eclipse.wst.sse.sieditor.test.model;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.AbstractWSDLComponent;


public class AbstractModelObjectFactoryTest extends SIEditorBaseTest{

	@Test 
	public void testNotSameXSDModelRootInstancesWhenLoadResources() throws IOException, CoreException {
		IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
		IXSDModelRoot xsdModelRoot2 = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
		
		assertNotSame(xsdModelRoot, xsdModelRoot2);
	}
	
	@Test 
	public void testSameXSDModelRootInstancesFromPool() throws IOException, CoreException, URISyntaxException {
		IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
		
		XSDSchema xsdSchema = xsdModelRoot.getSchema().getComponent();
		IXSDModelRoot xsdModelRoot2 = XSDFactory.getInstance().createXSDModelRoot(xsdSchema);
		
		assertSame(xsdModelRoot, xsdModelRoot2);
	}
	
	@Test 
	public void testNotSameWSDLModelRootInstancesWhenLoadResources() throws IOException, CoreException {
		IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl", "ChangePurchaseOrder_WSD.wsdl");
		IWsdlModelRoot wsdlModelRoot2 = getWSDLModelRoot("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl", "ChangePurchaseOrder_WSD.wsdl");
		
		assertNotSame(wsdlModelRoot, wsdlModelRoot2);
	}
	
	@Test 
	public void testSameWSDLModelRootInstancesFromPool() throws IOException, CoreException, URISyntaxException {
		IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl", "ChangePurchaseOrder_WSD.wsdl");
		
		Definition definition = (Definition)((AbstractWSDLComponent)wsdlModelRoot.getDescription()).getComponent();
		IWsdlModelRoot wsdlModelRoot2 = WSDLFactory.getInstance().createWSDLModelRoot(definition);
		
		assertSame(wsdlModelRoot, wsdlModelRoot2);
	}
	
}
