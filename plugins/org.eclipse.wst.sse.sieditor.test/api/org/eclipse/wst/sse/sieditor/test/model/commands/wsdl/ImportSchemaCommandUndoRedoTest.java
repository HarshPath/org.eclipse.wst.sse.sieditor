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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class ImportSchemaCommandUndoRedoTest extends AbstractCommandTest {
    private static final String IMPORTED_SCHEMA_NS = "http://www.example.com/Imported.xsd";
    private static final String XSD_FILE = "Imported.xsd";
    private static final String XSD_FILE_PATH = DATA_PUBLIC_SELF_MIX_REL_PATH + XSD_FILE;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final List<ISchema> allVisibleSchemas = description.getAllVisibleSchemas();
        assertEquals(4, allVisibleSchemas.size());
        assertNotNull(description.getSchema(IMPORTED_SCHEMA_NS));
        final List<ISchema> containedSchemas = description.getContainedSchemas();
        assertEquals(3, containedSchemas.size());
        int conainsNoTNSSchema = 0;
        for (final ISchema iSchema : containedSchemas) {
            if (iSchema.getNamespace() == null) {
                conainsNoTNSSchema++;
                break;
            }
        }
        assertEquals(1, conainsNoTNSSchema);
        // checks directly the emf model
        final Definition definition = description.getComponent();
        final Types types = definition.getETypes();
        final List<XSDSchema> schemas = types.getSchemas();
        assertEquals(3, schemas.size());
        assertEquals(1, types.getSchemas(null).size());
        final XSDSchema nullNSSchema = (XSDSchema) types.getSchemas(null).get(0);
        final EList<XSDSchemaContent> contents = nullNSSchema.getContents();
        assertEquals(1, contents.size());
        assertTrue(contents.get(0) instanceof XSDImport);
        final XSDImport schemaImport = (XSDImport) contents.get(0);
        final XSDSchema schema = schemaImport.getResolvedSchema();
        assertNotNull(schema);
        assertEquals(IMPORTED_SCHEMA_NS, schema.getTargetNamespace());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final List<ISchema> allVisibleSchemas = description.getAllVisibleSchemas();
        assertEquals(3, allVisibleSchemas.size());
        assertEquals(0, description.getSchema(IMPORTED_SCHEMA_NS).length);
        final List<ISchema> containedSchemas = description.getContainedSchemas();
        assertEquals(2, containedSchemas.size());
        int conainsNoTNSSchema = 0;
        for (final ISchema iSchema : containedSchemas) {
            if (iSchema.getNamespace() == null) {
                conainsNoTNSSchema++;
            }
        }
        assertEquals(0, conainsNoTNSSchema);

        // checks directly the emf model
        final Definition definition = description.getComponent();
        final Types types = definition.getETypes();
        final List<XSDSchema> schemas = types.getSchemas();
        assertEquals(2, schemas.size());
        assertEquals(0, types.getSchemas(null).size());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IXSDModelRoot xsdModelRoot = getXSDModelRoot(XSD_FILE_PATH, XSD_FILE);
        final IType type = xsdModelRoot.getSchema().getType(false, "Address");

        return new ImportSchemaCommand(modelRoot, (Description) description, URIHelper
                .createEncodedURI(description.getLocation()), URIHelper.createEncodedURI(xsdModelRoot.getSchema().getComponent()
                .eResource().getURI().toString()), (AbstractType) type, DocumentType.XSD_SHEMA);
    }

}
