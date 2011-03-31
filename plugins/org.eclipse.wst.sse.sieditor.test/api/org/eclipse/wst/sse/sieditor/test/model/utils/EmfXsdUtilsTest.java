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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class EmfXsdUtilsTest extends SIEditorBaseTest {

    private static final String TARGET_NAMESPACE = "http://www.example.org/CopyTypeTest/Imported/ns1";

    @Test
    public void testcloneWithAnnotationElementDeclaration() throws IOException, CoreException, ExecutionException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        ISchema schema = xsdModelRoot.getSchema();
        final XSDSchema xsdSchema = schema.getComponent();

        final AbstractType type = (AbstractType) schema.getType(true, "purchaseOrder");
        final XSDNamedComponent clonedType[] = new XSDNamedComponent[1];

        AbstractNotificationOperation operation = new AbstractNotificationOperation(xsdModelRoot, type, "cloneWithAnnotation()") {

            @Override
            public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

                clonedType[0] = EmfXsdUtils.cloneWithAnnotation(type.getComponent(), xsdSchema);
                return Status.OK_STATUS;
            }

        };

        final IEnvironment env = xsdModelRoot.getEnv();
        env.execute(operation);

        assertNotNull(clonedType[0]);
        assertEquals(type.getComponent().getClass(), clonedType[0].getClass());
    }

    @Test
    public void testcloneWithAnnotationAttributeDeclaration() throws IOException, CoreException, ExecutionException {
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        ISchema schema = xsdModelRoot.getSchema();
        final XSDSchema xsdSchema = schema.getComponent();
        XSDAttributeDeclaration xsdAttr = null;

        for (XSDAttributeDeclaration attr : xsdSchema.getAttributeDeclarations()) {
            if ("globalAttribute".equals(attr.getName())) {
                xsdAttr = attr;
            }
        }

        assertNotNull("globalAttribute declaration not found in pub/xsd/example.xsd", xsdAttr);

        final XSDAttributeDeclaration finalAttr = xsdAttr;
        final XSDNamedComponent clonedAttr[] = new XSDNamedComponent[1];

        AbstractNotificationOperation operation = new AbstractNotificationOperation(xsdModelRoot, schema, "cloneWithAnnotation()") {

            @Override
            public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

                clonedAttr[0] = EmfXsdUtils.cloneWithAnnotation(finalAttr, xsdSchema);
                return Status.OK_STATUS;
            }

        };

        final IEnvironment env = xsdModelRoot.getEnv();
        env.execute(operation);

        assertNotNull(clonedAttr[0]);
        assertEquals(finalAttr.getClass(), clonedAttr[0].getClass());
    }

    @Test
    public void testcloneWithAnnotationModelGroupDefinition() throws IOException, CoreException, ExecutionException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/CopyTypeTestImportedSchema.wsdl",
                "CopyTypeTestImportedSchema.wsdl");
        final ISchema[] schemas = wsdlModelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        ISchema schema = schemas[0];
        final XSDSchema xsdSchema = schema.getComponent();
        XSDModelGroupDefinition xsdModelGroup = xsdSchema.getModelGroupDefinitions().iterator().next();

        final XSDModelGroupDefinition finalXsdModel = xsdModelGroup;
        final XSDNamedComponent clonedAttr[] = new XSDNamedComponent[1];

        AbstractNotificationOperation operation = new AbstractNotificationOperation(wsdlModelRoot, schema,
                "cloneWithAnnotation()") {

            @Override
            public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

                clonedAttr[0] = EmfXsdUtils.cloneWithAnnotation(finalXsdModel, xsdSchema);
                return Status.OK_STATUS;
            }

        };

        final IEnvironment env = wsdlModelRoot.getEnv();
        env.execute(operation);

        assertNotNull(clonedAttr[0]);
        assertEquals(finalXsdModel.getClass(), clonedAttr[0].getClass());
    }

    @Test
    public void testcloneWithAnnotationAttributeGroupDefinition() throws IOException, CoreException, ExecutionException {
        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot("pub/self/mix/CopyTypeTestImportedSchema.wsdl",
                "CopyTypeTestImportedSchema.wsdl");
        final ISchema[] schemas = wsdlModelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        ISchema schema = schemas[0];
        final XSDSchema xsdSchema = schema.getComponent();
        XSDAttributeGroupDefinition xsdModelGroup = xsdSchema.getAttributeGroupDefinitions().iterator().next();

        final XSDAttributeGroupDefinition finalXsdModel = xsdModelGroup;
        final XSDNamedComponent clonedAttr[] = new XSDNamedComponent[1];

        AbstractNotificationOperation operation = new AbstractNotificationOperation(wsdlModelRoot, schema,
                "cloneWithAnnotation()") {

            @Override
            public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

                clonedAttr[0] = EmfXsdUtils.cloneWithAnnotation(finalXsdModel, xsdSchema);
                return Status.OK_STATUS;
            }

        };

        final IEnvironment env = wsdlModelRoot.getEnv();
        env.execute(operation);

        assertNotNull(clonedAttr[0]);
        assertEquals(finalXsdModel.getClass(), clonedAttr[0].getClass());
    }
    
    @Test
    public final void testGetRootBaseType() {
        ISimpleType typeMock = createMock(ISimpleType.class);
        expect(typeMock.getNamespace()).andReturn(EmfXsdUtils.getSchemaForSchemaNS());
        replay(typeMock);
        assertEquals(typeMock,EmfXsdUtils.getRootBaseType(typeMock));
        verify(typeMock);
        
        //return the primitive base type of the curent's base type
        reset(typeMock);
        expect(typeMock.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS").atLeastOnce(); //$NON-NLS-1$
        
        ISimpleType typeMock2 = createMock(ISimpleType.class);
        
        expect(typeMock.getBaseType()).andReturn(typeMock2);
        
        expect(typeMock2.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS2").atLeastOnce(); //$NON-NLS-1$
        
        ISimpleType typeMock3 = createMock(ISimpleType.class);
        
        expect(typeMock2.getBaseType()).andReturn(typeMock3);
        
        expect(typeMock3.getNamespace()).andReturn(EmfXsdUtils.getSchemaForSchemaNS()).atLeastOnce();
        
        replay(typeMock,typeMock2,typeMock3);
        assertEquals(typeMock3,EmfXsdUtils.getRootBaseType(typeMock));
        verify(typeMock,typeMock2,typeMock3);
        
        //a invalid type with a null baase type
        reset(typeMock);
        expect(typeMock.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS").atLeastOnce(); //$NON-NLS-1$
        
        expect(typeMock.getBaseType()).andReturn(null);
        replay(typeMock);
        assertEquals(UnresolvedType.instance(),EmfXsdUtils.getRootBaseType(typeMock));
        verify(typeMock);
                
      //a recursive definiton
        reset(typeMock,typeMock2,typeMock3);
        expect(typeMock.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS").atLeastOnce(); //$NON-NLS-1$
        
        expect(typeMock.getBaseType()).andReturn(typeMock2);
        
        expect(typeMock2.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS2").atLeastOnce(); //$NON-NLS-1$
        
        expect(typeMock2.getBaseType()).andReturn(typeMock3);
        
        expect(typeMock3.getNamespace()).andReturn("NOT_A_SCHEMA_4_SCHEMA_TNS3").atLeastOnce(); //$NON-NLS-1$
        
        
        expect(typeMock3.getBaseType()).andReturn(typeMock);
        
        replay(typeMock,typeMock2,typeMock3);
        assertEquals(UnresolvedType.instance(),EmfXsdUtils.getRootBaseType(typeMock));
        verify(typeMock,typeMock2,typeMock3);   
    }

}
