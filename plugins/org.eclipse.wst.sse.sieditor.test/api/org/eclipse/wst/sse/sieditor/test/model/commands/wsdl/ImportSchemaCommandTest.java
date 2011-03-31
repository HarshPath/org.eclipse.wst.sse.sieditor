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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import java.util.List;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class ImportSchemaCommandTest extends SIEditorBaseTest{
	
	private static final String WSDL_FILE = "ActionAvailabilityCheck.wsdl";
	private static final String XSD_FILE = "Imported.xsd";
	private static final String WSDL_FILE_PATH = DATA_PUBLIC_SELF_MIX_REL_PATH + WSDL_FILE;
	private static final String XSD_FILE_PATH = DATA_PUBLIC_SELF_MIX_REL_PATH + XSD_FILE;
	

	@Test
	@SuppressWarnings("unchecked")
	public void testNewSchemaPrefix() throws Exception{
		IDescription description = getWSDLModel(WSDL_FILE_PATH, WSDL_FILE);
		IXSDModelRoot xsdModelRoot = getXSDModelRoot(XSD_FILE_PATH, XSD_FILE);
		IType type = xsdModelRoot.getSchema().getType(false, "Address");
		
		ImportSchemaCommand cmd = new ImportSchemaCommand(
				(IWsdlModelRoot) description.getModelRoot(),
				(Description) description,
				URIHelper.createEncodedURI(description.getLocation()),
				URIHelper.createEncodedURI(xsdModelRoot.getSchema().getComponent().eResource().getURI().toString()),
				(AbstractType) type, DocumentType.XSD_SHEMA);
		
		description.getModelRoot().getEnv().execute(cmd);
		Definition definition = (Definition) description.getComponent();
		List<XSDSchema> schemas = definition.getETypes().getSchemas();
		Assert.assertEquals(2, schemas.size());
		
		XSDSchema newSchema = schemas.get(1);
	}
	
	@Override
	protected String getProjectName() {
		return "CommandsTestingProject";
	}

}
