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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

@SuppressWarnings("nls")
public class ReferencedDocumentsTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "ReferencedDocumentsTest";
    }

    @Test
    public void testGetReferencedDocuments() throws Exception {
        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported.wsdl",
                Document_FOLDER_NAME, this.getProject(), "Imported.wsdl");
        assertTrue(null != file && file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "po.xsd", Document_FOLDER_NAME, this
                .getProject(), "po.xsd");
        assertTrue(null != file && file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "example.xsd", Document_FOLDER_NAME,
                this.getProject(), "example.xsd");
        assertTrue(null != file && file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Importing.wsdl", Document_FOLDER_NAME,
                this.getProject(), "Importing.wsdl");
        assertTrue(null != file && file.exists());
        // check for referenced documents
        final IDescription description = getModelRoot("Importing.wsdl");
        assertNotNull(description);

        final Collection<IServiceInterface> interfaces = description.getAllInterfaces();
        assertEquals(2, interfaces.size());

        final Collection<IDescription> services = description.getReferencedServices();
        assertNotNull("Null returned for referenced documents", services);
        assertTrue("No referenced documents present", services.size() == 1);
        assertEquals("http://www.example.org/Imported/", services.iterator().next().getNamespace());

        final Collection<ISchema> schemas = description.getAllVisibleSchemas();
        assertNotNull("Null returned for referenced schemas", schemas);
        assertEquals(4, schemas.size());
        List<IServiceInterface> sIs = description.getInterface("PortTypeWithImportedMessageRef");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull(serviceInterface);
        serviceInterface.getOperation("NewOperation");

    }

    @Test
    public void testImportedType() throws Exception {
        final IDescription description = getModelRoot("Importing.wsdl");
        List<IServiceInterface> sIs = description.getInterface("PortType");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull(serviceInterface);

        IOperation operation = serviceInterface.getOperation("NewOperation").get(0);
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(3, parameters.size());

        IParameter parameter = operation.getInputParameter("containedType").get(0);
        IType type = parameter.getType();
        assertNotNull("Type for containedType is null", type);
        assertEquals("NewOperation", type.getName());

        parameter = operation.getInputParameter("importedType").get(0);
        type = parameter.getType();
        assertNotNull("Type for importedType is null", type);
        assertEquals("comment", type.getName());
        assertEquals("http://www.example.com/IPO", type.getNamespace());

        parameter = operation.getInputParameter("wsiImportedType").get(0);
        type = parameter.getType();
        assertNotNull("Type for wsiImportedType is null", type);
        assertEquals("comment", type.getName());
        assertEquals("http://www.example.com/", type.getNamespace());
    }

    @Test
    public void testImportedMessage() throws Exception {
        //copy files used in the test:
        ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV+"example.xsd", this.getProject());
        getModelRoot("Imported.wsdl");
    
        final IDescription description = getModelRoot("Importing.wsdl");
        List<IServiceInterface> sIs = description.getInterface("PortTypeWithImportedMessageRef");
        assertFalse(sIs.isEmpty());
        IServiceInterface serviceInterface = sIs.get(0);
        assertNotNull(serviceInterface);

        IOperation operation = serviceInterface.getOperation("NewOperation").get(0);
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(2, parameters.size());

        List<IParameter> params = operation.getInputParameter("importedParamWithContainedType");
        assertFalse(params.isEmpty());
        IParameter parameter = params.get(0);
        assertNotNull(parameter);
        assertEquals("importedParamWithContainedType", parameter.getName());
        IType type = parameter.getType();
        assertNotNull(type);
        assertEquals("ImportedOperation", type.getName());
        assertEquals("http://www.example.org/Imported/", type.getNamespace());

        params = operation.getInputParameter("importedParamWithWsiImportedType");
        assertFalse(params.isEmpty());
        parameter = params.get(0);
        assertNotNull(parameter);
        assertEquals("importedParamWithWsiImportedType", parameter.getName());
        type = parameter.getType();
        assertNotNull(type);
        assertEquals("purchaseOrder", type.getName());
        assertEquals("http://www.example.com/", type.getNamespace());
    }

    private IDescription getModelRoot(String fileName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, fileName).getDescription();
    }

}