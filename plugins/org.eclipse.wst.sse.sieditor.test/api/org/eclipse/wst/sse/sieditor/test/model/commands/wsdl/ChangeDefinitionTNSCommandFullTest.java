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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractExhaustiveValidatingCommandWSDLTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.junit.runners.Parameterized.Parameters;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

/**
 * This test is not included in the test suite because it is a kind of a
 * parameterized integration test which opens, saves and closes editors.
 * Consequences: - IT IS REALY SLOW - shouldn't be executed daily on prediction
 * with other tests - DEPENDS ON ALOT OF OUTSIDE FACTORS - || - . It's purpose
 * was to help in test driven development, and to help determine usecases of
 * this command. Parts of it could be reused in integration tests.
 * 
 *
 * 
 */
public class ChangeDefinitionTNSCommandFullTest extends AbstractExhaustiveValidatingCommandWSDLTest {

    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
    protected String initialDocumentContent;
    protected String modifiedDocumentContent;

    @Parameters
    public static Collection<Object[]> getEnterpriseWSDLFiles() throws IOException, URISyntaxException {
        return generateParameterList(Document_FOLDER_NAME + Path.SEPARATOR + "tns" + Path.SEPARATOR + "wsdl"); //$NON-NLS-1$
    }

    public ChangeDefinitionTNSCommandFullTest(final Object wsdlFile) throws IOException, CoreException {
        super(wsdlFile);
    }

    private String originalNamespace;
    private String newNamespace;

    @Override
    protected void executeOperation(final AbstractEMFOperation operation, final IWsdlModelRoot modelRoot) throws Throwable {
        // hook up here to get the content of the doc
        initialDocumentContent = ResourceUtils.getContents(currentIFile, ENCODING);
        // assert document is initialy correct.
        verifyMessageReferences();

        super.executeOperation(operation, modelRoot);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostRedoState(redoStatus, modelRoot);
        try {
            if (modifiedDocumentContent == null) {
                modifiedDocumentContent = ResourceUtils.getContents(currentIFile, ENCODING);
            }
            verifyMessageReferences();
            assertEquals(newNamespace, ((Description) modelRoot.getDescription()).getNamespace());
            assertEquals(modifiedDocumentContent, ResourceUtils.getContents(currentIFile, ENCODING));
        } catch (final Exception e) {
            fail(e.toString());
        }
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostUndoState(undoStatus, modelRoot);
        verifyMessageReferences();
        assertEquals(originalNamespace, ((Description) modelRoot.getDescription()).getNamespace());
        try {
            assertEquals(initialDocumentContent, ResourceUtils.getContents(currentIFile, ENCODING));
        } catch (final Exception e) {
            fail(e.toString());
        }
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final Description descr = (Description) modelRoot.getDescription();
        originalNamespace = descr.getNamespace();
        newNamespace = "http://www.emu.org/myNamespace"; //$NON-NLS-1$
        return new ChangeDefinitionTNSCompositeCommand(modelRoot, descr, newNamespace);
    }

    protected void verifyMessageReferences() {

        // WRITTEN ON PURPOSE !
        // REPORTED CSN : 0120031469 0001085874 2010. remove return when csn is
        // fixed.
        if (true)
            return;

        Definition definition = null;
        try {
            definition = getModelRoot().getDescription().getComponent();
        } catch (final Exception e) {
            fail(e.toString());
        }

        final EList<PortType> ePortTypes = definition.getEPortTypes();
        for (final PortType portType : ePortTypes) {
            final EList<Operation> eOperations = portType.getEOperations();
            for (final Operation wsdlOperation : eOperations) {
                final org.eclipse.wst.wsdl.Input input = wsdlOperation.getEInput();
                assertNotNull(input.getEMessage());
            }

        }

        final EList<Message> eMessages = definition.getEMessages();
        final List<Part> parts = new ArrayList<Part>();
        for (final Message message : eMessages) {
            parts.addAll(message.getEParts());
            for (final Part part : parts) {
                if (part.getElementName() != null) {
                    assertEquals(part.getElementName(), new QName(part.getElementDeclaration().getTargetNamespace(), part
                            .getElementDeclaration().getName()));
                } else {
                    assertEquals(part.getTypeName(), new QName(part.getTypeDefinition().getTargetNamespace(), part
                            .getTypeDefinition().getName()));
                }
            }
        }
    }
}
