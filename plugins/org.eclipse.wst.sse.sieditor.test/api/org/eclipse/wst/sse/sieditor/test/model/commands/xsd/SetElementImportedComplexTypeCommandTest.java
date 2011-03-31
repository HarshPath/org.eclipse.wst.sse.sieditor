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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base.SetElementTypeAbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeResolverFactory;

public class SetElementImportedComplexTypeCommandTest extends SetElementTypeAbstractCommandTest {
    private static final String IMPORTED_TYPE_NAME = "Address";
    private static final String IMPORTED_SCHEMA_NAMESPACE = "http://www.example.com/";
    private static final String NEW_ELEMENT_NAME = "importedComplexTypeElement";
    private IFile importedFile;

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IWsdlModelRoot modelRoot) {
        assertNotNull(newType);
        assertNotSame(UnresolvedType.instance(), newType);
        assertTrue(newType instanceof IStructureType);

        final IType elementType = element.getType();

        assertNotNull(elementType);
        assertNotSame(UnresolvedType.instance(), elementType);
        assertTrue(elementType instanceof IStructureType);

        super.assertPostRedoState(redoStatus, modelRoot);
    }

    @Override
    protected AbstractType getNewType() throws ExecutionException {
        final IType resolvedType;

        resolvedType = TypeResolverFactory.getInstance().createTypeResolver(modelRoot.getDescription()).resolveType(
                IMPORTED_TYPE_NAME, IMPORTED_SCHEMA_NAMESPACE, importedFile);

        return (AbstractType) resolvedType;
    }

    @Override
    protected String getNewElementName() {
        return NEW_ELEMENT_NAME;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        importedFile = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "imported_schema.xsd");

        refreshProjectNFile(importedFile);
    }
}
