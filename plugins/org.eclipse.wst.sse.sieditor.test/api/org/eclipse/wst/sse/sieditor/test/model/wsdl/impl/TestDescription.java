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
package org.eclipse.wst.sse.sieditor.test.model.wsdl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class TestDescription extends SIEditorBaseTest {
	
	private static final String FILES[] = new String[] {
		"pub/import/Importing.wsdl",
		"pub/import/Imported.wsdl",
		"pub/import/Imported2.wsdl",
		"pub/import/example.xsd",
		"pub/import/example2.xsd",
		"pub/import/Imported_example2.xsd",
		"pub/import/Imported_example3_1.xsd",
		"pub/import/Imported_example3.xsd",
		"pub/import/Imported_example4.xsd",
		"pub/import/po.xsd",
		"pub/import/po_2.xsd",
		"pub/import/noTargetNamespace.xsd",
		"pub/import/example5.xsd"
		};
	
	private IWsdlModelRoot wsdlModelRoot = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        for(int i=1; i < FILES.length; i++) {
	        String fileName = FILES[i].substring(FILES[i].lastIndexOf('/')+1);
			IFile file = ResourceUtils.copyFileIntoTestProject(FILES[i], Document_FOLDER_NAME, this.getProject(), fileName);
	        refreshProjectNFile(file);
        }
        wsdlModelRoot = getWSDLModelRoot(FILES[0], "Importing.wsdl");
    }

	@Test
	public void testImportedSchemasVisability() {
		IDescription description = wsdlModelRoot.getDescription();
		List<ISchema> allVisibleSchemas = description.getAllVisibleSchemas();
		List<ISchema> containedSchemas = description.getContainedSchemas();

		assertTrue("allVisibleSchemas does not contain all containedSchemas", 
				allVisibleSchemas.containsAll(containedSchemas));
		
		assertEquals(9, allVisibleSchemas.size());
		assertEquals(7, containedSchemas.size());
		
		List<ISchema> allVisibleSchemasWhithoutContainedSchemas = new ArrayList<ISchema>(allVisibleSchemas);
		allVisibleSchemasWhithoutContainedSchemas.removeAll(containedSchemas);
		
		assertAllContainedSchemasAreInPlace(containedSchemas);
		assertAllVisibleSchemasAreInPlace(allVisibleSchemasWhithoutContainedSchemas);
		
		ISchema[] schemaWithoutTNS = description.getSchema(null);
		assertEquals(1, schemaWithoutTNS.length);
		
		List<ISchema> schemaWithoutTNS_List = new ArrayList<ISchema>();
		schemaWithoutTNS_List.add(schemaWithoutTNS[0]);
		assertSchemasWithoutTargetNamespacesAreInPlace(schemaWithoutTNS_List);
	}

	private void assertAllContainedSchemasAreInPlace(List<ISchema> containedSchemas) {
		Map<String, String> expectedLocationFileAndNamespace = new HashMap<String, String>();
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://www.example.org/Importing/");
		expectedLocationFileAndNamespace.put("Importing.wsdl", null);
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://www.example.com/example_noTNS");
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://OnlyProperlyImportedImport");
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://OnlyProperlyImportedImport2");		
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://www.example.org/schema1");
		expectedLocationFileAndNamespace.put("Importing.wsdl", "http://www.example.org/schema2");
		
		assertListContainsExpectedSchemas(containedSchemas, expectedLocationFileAndNamespace);
	}
	
	private void assertAllVisibleSchemasAreInPlace(List<ISchema> visibleSchemas) {
		Map<String, String> expectedLocationFileAndNamespace = new HashMap<String, String>();
		expectedLocationFileAndNamespace.put("po.xsd", "http://www.example.com/IPO");
		
		assertListContainsExpectedSchemas(visibleSchemas, expectedLocationFileAndNamespace);
	}
	
	private void assertSchemasWithoutTargetNamespacesAreInPlace(List<ISchema> schemaWithoutTNS) {
		Map<String, String> expectedLocationFileAndNamespace = new HashMap<String, String>();
		expectedLocationFileAndNamespace.put("Importing.wsdl", null);
		
		assertListContainsExpectedSchemas(schemaWithoutTNS, expectedLocationFileAndNamespace);
	}
	
	private void assertListContainsExpectedSchemas(List<ISchema> schemas, Map<String, String> expectedLocationFileAndNamespace) {
		boolean foundSchema = false;
		boolean equalNamespaces = false;
		for(String expectedFile : expectedLocationFileAndNamespace.keySet()) {
			String expectedNamespace = expectedLocationFileAndNamespace.get(expectedFile);
			foundSchema = false;
			for(ISchema schema : schemas) {
				String namespace = schema.getNamespace();
				if(EmfXsdUtils.isSchemaForSchemaNS(namespace)) {
					continue;
				}
				
				String location = schema.getLocation();
				equalNamespaces = namespace == null ? expectedNamespace == null : namespace.equals(expectedNamespace);
				if(location.endsWith(expectedFile) && equalNamespaces) {
					foundSchema = true;
					break;
				}
			}
			if(!foundSchema) {
				fail("Can not find schema with namespace: " + expectedNamespace + "; location ends with file: " + expectedFile);
			}
		}
	}
	
}
