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

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandChainTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.junit.Before;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.InlineNamespaceCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;

public class ReconcilerCalledAfterInliningTest extends AbstractCommandChainTest {
    private static final String XSD1_NS = "http://www.example.org/NamespaceImportsXSD";
    private static final String FOLDER_NAME = "pub/csns/NonReconcilatingTest/";
    private static final String XSD_FILE_1 = "NamespaceImportsXSD.xsd";
    private static final String XSD_FILE_2 = "NamespaceIncludesXSD.xsd";
    private static final String WSDL_FILE = "NamespaceImportsWSDL.wsdl";
    private InlineNamespaceCompositeCommand inlineNSCommand;
    private SetElementTypeCommand setTypeCommand;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IFile xsd1 = ResourceUtils.copyFileIntoTestProject(FOLDER_NAME + XSD_FILE_1, Document_FOLDER_NAME, this
                .getProject(), XSD_FILE_1);
        final IFile xsd2 = ResourceUtils.copyFileIntoTestProject(FOLDER_NAME + XSD_FILE_2, Document_FOLDER_NAME, this
                .getProject(), XSD_FILE_2);
        refreshProjectNFile(xsd1);
        refreshProjectNFile(xsd2);
    }

    @Override
    protected String getWsdlFilename() {
        return WSDL_FILE;
    }

    @Override
    protected String getWsdlFoldername() {
        return FOLDER_NAME;
    }

    @Override
    protected AbstractNotificationOperation getNextOperation(final IWsdlModelRoot modelRoot) throws Exception {
        if (inlineNSCommand == null) {
            final ISchema containingSchema = findSingleSchema(modelRoot, "http://namespace1");
            final Collection<ISchema> allReferredSchemas = containingSchema.getAllReferredSchemas();
            ISchema schemaToInline = null;
            for (final ISchema iSchema : allReferredSchemas) {
                if (XSD1_NS.equals(iSchema.getNamespace())) {
                    schemaToInline = iSchema;
                    break;
                }
            }
            inlineNSCommand = new InlineNamespaceCompositeCommand(modelRoot, schemaToInline);
            return inlineNSCommand;
        }
        if (setTypeCommand == null) {
            final IElement attribute = getStructureType3(modelRoot).getElements("Attribute2").iterator().next();

            final ISchema schema2 = findSingleSchema(modelRoot, "http://namespace3");
            final IType typeToBeSet = schema2.getType(false, "SimpleType2");
            setTypeCommand = new SetElementTypeCommand(attribute, typeToBeSet);
            return setTypeCommand;
        }
        return null;
    }

    private ISchema findSingleSchema(final IWsdlModelRoot modelRoot, final String ns) {
        final ISchema schemas[] = modelRoot.getDescription().getSchema(ns);
        assertEquals("no schema with namespace " + ns, 1, schemas.length);
        assertNotNull(schemas[0]);
        return schemas[0];
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(redoStatus.isOK());
        final IElement firstElement = getStructureType3(modelRoot).getAllElements().iterator().next();
        final XSDElementDeclaration resolvedElementDeclaration = ((org.eclipse.xsd.XSDElementDeclaration) ((org.eclipse.xsd.XSDParticle) firstElement.getComponent())
                .getContent()).getResolvedElementDeclaration();
        assertNotNull(resolvedElementDeclaration);
        assertNotNull(resolvedElementDeclaration.getContainer());

    }

    @Override
    protected void assertPostExecuteState(final IStatus status, final IWsdlModelRoot modelRoot) {
        super.assertPostExecuteState(status, modelRoot);
        assertPostRedoState(status, modelRoot);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertPostRedoState(undoStatus, modelRoot);
    }

    @Override
    protected void assertPostOperationUndoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        assertPostRedoState(Status.OK_STATUS, modelRoot);
    }

    @Override
    protected void assertPostOperationRedoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        assertPostRedoState(Status.OK_STATUS, modelRoot);
    }

    // custom for this test. When ever it is first initiated :)
    private IStructureType getStructureType3(final IWsdlModelRoot modelRoot) {
        final ISchema schema = findSingleSchema(modelRoot, "http://namespace1");
        return (StructureType) schema.getType(false, "StructureType3");
    }

}
