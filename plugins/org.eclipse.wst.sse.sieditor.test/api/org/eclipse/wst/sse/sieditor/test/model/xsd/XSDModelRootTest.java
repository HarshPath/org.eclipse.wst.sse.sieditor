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
package org.eclipse.wst.sse.sieditor.test.model.xsd;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.impl.ModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class XSDModelRootTest extends XSDBaseProjectTest {

    @Test
    public void testLoadXSDFile() throws IOException, CoreException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        ISchema schema = xsdModelRoot.getSchema();
        XSDSchema xsdSchema = schema.getComponent();

        assertNotNull(xsdSchema);
    }

    @Test
    public void testCopyXSDTypeCommand() throws IOException, CoreException, ExecutionException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        ISchema schema = xsdModelRoot.getSchema();
        XSDSchema xsdSchema = schema.getComponent();

        EList<XSDTypeDefinition> typeDefinitions = xsdSchema.getTypeDefinitions();
        XSDNamedComponent typeToBeCopied = typeDefinitions.get(0);
        int typesNumber = schema.getAllContainedTypes().size();

        CopyTypeCommand copyCommand = new CopyTypeCommand(xsdModelRoot, schema, typeToBeCopied, (Schema) schema, "HelloName");
        xsdModelRoot.getEnv().execute(copyCommand);

        assertEquals(typesNumber + 1, schema.getAllContainedTypes().size());
    }

    @Test
    public void testSchemaCopyTypeCommand() throws IOException, CoreException, ExecutionException, DuplicateException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        ISchema schema = xsdModelRoot.getSchema();
        IType type = schema.getAllContainedTypes().iterator().next();
        int typesNumber = schema.getAllContainedTypes().size();

        assertNotNull(((Schema) schema).copyType(type, "HelloName"));

        assertEquals(typesNumber + 1, schema.getAllContainedTypes().size());

    }

    @Test
    public void testGetExistingISchemaFromAdaptable() throws IOException, CoreException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        XSDSchema xsdSchema = xsdModelRoot.getSchema().getComponent();

        IXSDModelRoot xsdModelRoot2 = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");

        IAdaptable adaptable = createNiceMock(IAdaptable.class);
        expect(adaptable.getAdapter(ISchema.class)).andReturn(xsdModelRoot.getSchema()).anyTimes();
        replay(adaptable);

        IAdaptable adaptable2 = createNiceMock(IAdaptable.class);
        expect(adaptable2.getAdapter(ISchema.class)).andReturn(xsdModelRoot2.getSchema()).anyTimes();
        replay(adaptable2);

        IXSDModelRoot xsdModelRootFromFactory = XSDFactory.getInstance().createXSDModelRoot(xsdSchema, adaptable);
        IXSDModelRoot xsdModelRootFromFactory2 = XSDFactory.getInstance().createXSDModelRoot(xsdSchema, adaptable2);

        assertNotSame(xsdModelRootFromFactory.getSchema(), xsdModelRootFromFactory2.getSchema());

        IAdaptable adaptable3 = createNiceMock(IAdaptable.class);
        expect(adaptable3.getAdapter(ISchema.class)).andReturn(xsdModelRoot.getSchema()).anyTimes();
        replay(adaptable3);

        IXSDModelRoot xsdModelRootFromFactory3 = XSDFactory.getInstance().createXSDModelRoot(xsdSchema, adaptable3);

        assertSame(xsdModelRootFromFactory.getSchema(), xsdModelRootFromFactory3.getSchema());
    }

    @Test
    public void testCopyTypeCommandWithReference() throws IOException, CoreException, ExecutionException, DuplicateException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/csns/csn_1038868_2010.wsdl", "csn_1038868_2010.wsdl");
        ISchema[] sourceSchema = wsdlModelRoot.getDescription().getSchema("http://sap.com/xi/SAPGlobal20/Global");
        assertEquals(1, sourceSchema.length);

        IType sourceType = sourceSchema[0].getType(true, "BatchByIDQuery_sync");
        assertNotNull(sourceType);

        ISchema[] targetSchema = wsdlModelRoot.getDescription().getSchema("http://namespace1");
        assertEquals(1, targetSchema.length);

        CopyTypeCommand copyCommand = new CopyTypeCommand(targetSchema[0].getModelRoot(), wsdlModelRoot.getDescription(),
                sourceType.getComponent(), targetSchema[0], sourceType.getName());
        IStatus status = wsdlModelRoot.getEnv().execute(copyCommand);
        assertTrue(status.toString(), status.isOK());

        IType targetType = targetSchema[0].getType(true, "BatchByIDQuery_sync");
        assertNotNull(targetType);

        boolean hasImportToSourceSchema = false;
        for (ISchema refSchema : targetSchema[0].getAllReferredSchemas()) {
            if ("http://sap.com/xi/APPL/SE/Global".equals(refSchema.getNamespace())) {
                hasImportToSourceSchema = true;
                break;
            }
        }

        assertTrue("Missing import directive in target schema.", hasImportToSourceSchema);
    }

    /**
     * Ensure that when the XSD model root listeners are notified, it's parent's
     * listeners will be notified as well
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public void testNotifyListeners() throws IOException, CoreException {

        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/import/Importing.wsdl", "testNotifyListenersSchema.wsdl");

        final boolean[] notified = new boolean[1];
        wsdlModelRoot.addChangeListener(new IChangeListener() {

            @Override
            public void componentChanged(IModelChangeEvent event) {
                notified[0] = true;
            }
        });

        IDescription description = wsdlModelRoot.getDescription();
        ISchema[] schemas = description.getSchema("http://www.example.org/Importing/");
        assertEquals(1, schemas.length);
        IModelRoot modelRoot = schemas[0].getModelRoot();

        IModelChangeEvent changeEvent = new ModelChangeEvent(schemas[0]);
        modelRoot.notifyListeners(changeEvent);

        Assert.assertTrue(notified[0]);
    }
}
