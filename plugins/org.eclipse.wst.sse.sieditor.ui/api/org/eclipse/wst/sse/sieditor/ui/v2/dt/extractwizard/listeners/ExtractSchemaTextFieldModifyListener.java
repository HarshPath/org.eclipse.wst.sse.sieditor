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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.listeners;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.ExtractSchemaWizardPage;

public class ExtractSchemaTextFieldModifyListener implements ModifyListener {

    private final Text saveLocationText;
    private final ExtractSchemaWizardPage wizardPage;

    private final SchemaNode schemaNode;

    public ExtractSchemaTextFieldModifyListener(final ExtractSchemaWizardPage wizardPage,
            final SchemaNode schemaNode) {
        this.saveLocationText = wizardPage.getSaveLocationText();
        this.wizardPage = wizardPage;
        this.schemaNode = schemaNode;
    }

    public void modifyText(final ModifyEvent e) {
        final String text = saveLocationText.getText();

        if (text == null) {
            return;
        }

        final IPath path = new Path(text);

        final IPath directoryPath = path.removeLastSegments(1);
        schemaNode.setPath(directoryPath);
        schemaNode.updateImportsPaths();
        
        schemaNode.setFilename(path.lastSegment());
        updateButtonsState();
    }

    protected void updateButtonsState() {
        wizardPage.getWizard().getContainer().updateButtons();
    }

}
