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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.internal.forms.widgets.TitleRegion;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;

public class EditorTitleValidationTest extends AbstractCommandTest {

    private static final String INITIAL_SERVICE_NAME = "SimpleTypeFacetsWSDLFile";

    private static final String NEW_SERVICE_NAME = "name!";

    @Override
    protected String getWsdlFilename() {
        return "SetSimpleTypeFacetsCommandTestWSDLFile.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface serviceInterface = modelRoot.getDescription().getAllInterfaces().iterator().next();
        assertEquals(NEW_SERVICE_NAME, serviceInterface.getName());

        final List pages = editor.getPages();
        assertEquals(3, pages.size());
        int editorPages = 0;

        for (final Object pageObject : pages) {
            if (pageObject instanceof AbstractEditorPage) {
                final AbstractEditorPage page = (AbstractEditorPage) pageObject;

                editorPages++;

                final String tooltip = ((TitleRegion) page.getManagedForm().getForm().getForm().getHead().getChildren()[0])
                        .getChildren()[0].getToolTipText();
                assertNotNull(tooltip);
                
                assertTrue(tooltip.indexOf("Errors (") != -1);
                assertTrue(tooltip.indexOf("Warnings (") != -1);
                
                assertEquals(tooltip, ((TitleRegion) page.getManagedForm().getForm().getForm().getHead().getChildren()[0])
                        .getChildren()[1].getToolTipText());

                final Control control = page.getManagedForm().getForm().getForm().getHead().getChildren()[1];
                assertEquals(tooltip, control.getToolTipText());

                assertTrue(control instanceof CLabel);

                final CLabel label = (CLabel) control;
                assertEquals("8 errors, 2 warnings detected", label.getText());
            }
        }
        assertEquals(2, editorPages);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface serviceInterface = modelRoot.getDescription().getAllInterfaces().iterator().next();
        assertEquals(INITIAL_SERVICE_NAME, serviceInterface.getName());

        final List pages = editor.getPages();
        assertEquals(3, pages.size());
        int editorPages = 0;

        for (final Object pageObject : pages) {
            if (pageObject instanceof AbstractEditorPage) {
                final AbstractEditorPage page = (AbstractEditorPage) pageObject;

                editorPages++;

                final String tooltip = ((TitleRegion) page.getManagedForm().getForm().getForm().getHead().getChildren()[0])
                        .getChildren()[0].getToolTipText();
                assertNotNull(tooltip);
                
                assertTrue(tooltip.indexOf("Errors (") != -1);
                assertTrue(tooltip.indexOf("Warnings (") != -1);
                
                assertEquals(tooltip, ((TitleRegion) page.getManagedForm().getForm().getForm().getHead().getChildren()[0])
                        .getChildren()[1].getToolTipText());

                final Control control = page.getManagedForm().getForm().getForm().getHead().getChildren()[1];
                assertEquals(tooltip, control.getToolTipText());

                assertTrue(control instanceof CLabel);

                final CLabel label = (CLabel) control;
                assertEquals("7 errors, 2 warnings detected", label.getText());
            }
        }
        assertEquals(2, editorPages);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IServiceInterface serviceInterface = modelRoot.getDescription().getAllInterfaces().iterator().next();
        return new RenameServiceInterfaceCommand(modelRoot, serviceInterface, NEW_SERVICE_NAME);
    }

}
