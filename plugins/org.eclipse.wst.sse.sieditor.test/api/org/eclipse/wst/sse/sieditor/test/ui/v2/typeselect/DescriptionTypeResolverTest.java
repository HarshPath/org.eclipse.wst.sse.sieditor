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
package org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.DescriptionTypeResolver;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeResolver;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeResolverFactory;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * Test for the {@link DescriptionTypeResolver} class
 *
 *
 */
public class DescriptionTypeResolverTest extends SIEditorBaseTest {
    private static final String IMPORTED_TYPE_NAME = "Address";
    private static final String IMPORTED_SCHEMA_NAMESPACE = "http://www.example.com/";
    private static final String IMPORTED_TYPE_NAME_NEGATIVE = "NotExistingType";

    private ITypeResolver typeResolver;
    private IDescription description;
    private IFile importedFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix2/PurchaseOrderConfirmation.wsdl", Document_FOLDER_NAME,
                this.getProject(), "TypeResolversTest.wsdl");
        refreshProjectNFile(file);

        importedFile = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME,
                this.getProject(), "imported schema.xsd");
        refreshProjectNFile(importedFile);

        IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(file);
        description = wsdlModelRoot.getDescription();
        typeResolver = TypeResolverFactory.getInstance().createTypeResolver(description);
    }

    @Test
    public void testGetLocalSchemas() {
        List<ISchema> containedSchemas = description.getContainedSchemas();
        XSDSchema[] schemas = new XSDSchema[containedSchemas.size()];
        for (int i = 0; i < schemas.length; i++) {
            schemas[i] = containedSchemas.get(i).getComponent();
        }

        XSDSchema[] localSchemas = typeResolver.getLocalSchemas();

        Assert.assertTrue(Arrays.equals(schemas, localSchemas));

    }
    
    @Test
    public void testResolveLocalType() {
        // pick a lucky type
        List<ISchema> containedSchemas = description.getContainedSchemas();
        Collection<IType> types = containedSchemas.get(0).getAllContainedTypes();
        IType type = types.iterator().next();
        XSDNamedComponent xsdType = type.getComponent();

        // now resolve it
        IType resolvedType = typeResolver.resolveType(xsdType);

        Assert.assertEquals(type, resolvedType);
    }
    
    @Test
    public void testResolveType() {
        final IType resolvedType = typeResolver.resolveType(IMPORTED_TYPE_NAME, IMPORTED_SCHEMA_NAMESPACE, importedFile);

        assertNotNull(resolvedType);
        assertNotSame(UnresolvedType.instance(), resolvedType);
        assertTrue(resolvedType instanceof IStructureType);
        assertEquals(IMPORTED_TYPE_NAME, resolvedType.getName());
        assertEquals(IMPORTED_SCHEMA_NAMESPACE, resolvedType.getNamespace());
    }

    @Test
    public void testResolveTypeNegative() throws Exception {
        final IType resolvedType = typeResolver.resolveType(IMPORTED_TYPE_NAME_NEGATIVE, IMPORTED_SCHEMA_NAMESPACE, importedFile);

        assertNotNull(resolvedType);
        assertEquals(UnresolvedType.instance(), resolvedType);
    }
}