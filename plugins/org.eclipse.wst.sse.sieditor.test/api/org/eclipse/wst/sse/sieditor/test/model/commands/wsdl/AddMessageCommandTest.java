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

import java.util.Collection;
import java.util.Map;

import javax.wsdl.Message;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddMessageCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public class AddMessageCommandTest extends AbstractCommandTest {

    @Override
    protected IWsdlModelRoot getModelRoot() throws Exception {
        ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/services/xsd/stockquote.xsd", Document_FOLDER_NAME
                + "/xsd", this.getProject(), "stockquote.xsd");
        refreshProjectNFile(file);

        // file =
        // ResourceUtils.copyFileIntoTestProject("pub/import/services/wsdl/stockquote.wsdl",
        // Document_FOLDER_NAME + "/wsdl",
        // this.getProject(), "stockquote.wsdl");
        // refreshProjectNFile(file);

        return super.getModelRoot();
    }

    @Override
    protected String getWsdlFilename() {
        return "stockquote.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/import/services/wsdl/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();
        final QName messageQname = new QName("http://example.com/stockquote/definitions", "MyMessageName", "");

        final Message message = definition.getMessage(messageQname);
        assertNotNull(message);

        final Map partsMap = message.getParts();
        final Collection parts = partsMap.values();
        assertNotNull(parts);
        assertEquals(1, parts.size());
        final Part part = (Part) parts.iterator().next();
        assertNull(part.getTypeDefinition());
        assertNull(part.getTypeName());
        assertNull(part.getElementDeclaration());
        assertNull(part.getElementName());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();
        final QName messageQname = new QName("http://example.com/stockquote/definitions", "MyMessageName", "");

        final Message message = definition.getMessage(messageQname);
        assertNull(message);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();

        final QName messageQname = new QName("http://example.com/stockquote/definitions", "MyMessageName", "");
        assertNull(definition.getMessage(messageQname));

        return new AddMessageCommand(description, null, "MyMessageName", "MyPartName");
    }

}
