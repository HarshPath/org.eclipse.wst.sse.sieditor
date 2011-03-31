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
package org.eclipse.wst.sse.sieditor.test.ui.v2.providers;

import static org.junit.Assert.assertTrue;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLDetailsPageProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.FaultDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.OperationDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ParameterDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ServiceInterfaceDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.junit.Test;


public class TestWSDLDetailsPageProvider {
	
	@Test 
	public void testGetPageByKey_ServiceInterfaceNode() {
		WSDLDetailsPageProvider provider = new WSDLDetailsPageProvider(null, null);
		ServiceInterfaceNode node = new ServiceInterfaceNode(null, null, null);
		IDetailsPage page = provider.getPage(provider.getPageKey(node));

		assertTrue(page instanceof ServiceInterfaceDetailsPage);
	}	
	
	@Test 
	public void testGetPageByKey_OperationNode() {
		WSDLDetailsPageProvider provider = new WSDLDetailsPageProvider(null, null);
		OperationNode node = new OperationNode(null, null, null);
		IDetailsPage page = provider.getPage(provider.getPageKey(node));

		assertTrue(page instanceof OperationDetailsPage);
	}
	
	@Test 
	public void testGetPageByKey_ParameterNode() {
		WSDLDetailsPageProvider provider = new WSDLDetailsPageProvider(null, null);
		ParameterNode node = new ParameterNode(null, null, null);
		IDetailsPage page = provider.getPage(provider.getPageKey(node));

		assertTrue(page instanceof ParameterDetailsPage);
	}
	
	@Test 
	public void testGetPageByKey_FaultNode() {
		WSDLDetailsPageProvider provider = new WSDLDetailsPageProvider(null, null);
		FaultNode node = new FaultNode(null, null, null);
		IDetailsPage page = provider.getPage(provider.getPageKey(node));

		assertTrue(page instanceof FaultDetailsPage);
	}

}
