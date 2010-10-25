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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.pages;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.table.SchemaFilesTableViewer;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.utils.ExtractSchemaWizardConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.utils.SchemaFilenameGenerator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.utils.WizardPageValidationUtils;

public class SchemaDependenciesWizardPage extends WizardPage {

    private Set<SchemaNode> dependenciesSet;

    private boolean pageInitialized = false;

    public SchemaDependenciesWizardPage() {
        super("Dependencies Page"); //$NON-NLS-1$
    }

    public void init(final SchemaNode schemaNode, final Set<SchemaNode> dependenciesSet) {
        this.dependenciesSet = dependenciesSet;
        final String[] filenames = filenameGenerator().getFilenames(schemaNode.getPath(),
                ExtractSchemaWizardConstants.DEFAULT_DEPENDENT_SCHEMA_FILENAME_TEMPLATE, dependenciesSet.size());
        int i = 0;
        for (final SchemaNode node : dependenciesSet) {
            node.setFilename(filenames[i++]);
        }
        this.pageInitialized = true;
    }

    @Override
    public void createControl(final Composite parent) {
        setTitle(Messages.SchemaDependenciesWizardPage_page_title);
        setDescription(Messages.SchemaDependenciesWizardPage_page_description);
        createContents(parent);
    }

    private void createContents(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        setControlLayout(container);
        setControl(container);
        createDependenciesTable(container);
    }

    private void setControlLayout(final Composite container) {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginTop = 0;
        layout.marginBottom = 5;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
    }

    private void createDependenciesTable(final Composite container) {
        new SchemaFilesTableViewer(container, dependenciesSet, this);
    }

    @Override
    public boolean isPageComplete() {
        if (!pageInitialized) {
            return false;
        }

        IStatus validationStatus = pageValidationUtils().validateSchemaDependencyNodes(dependenciesSet);
        if (!validationStatus.isOK()) {
            setErrorMessage(validationStatus.getMessage());
            return false;
        }
        final Set<SchemaNode> nodes = new HashSet<SchemaNode>(dependenciesSet);
        validationStatus = pageValidationUtils().validateSchemaFilenames(nodes);
        if (!validationStatus.isOK()) {
            setErrorMessage(validationStatus.getMessage());
            return false;
        }
        
        setErrorMessage(null);
        return true;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected WizardPageValidationUtils pageValidationUtils() {
        return WizardPageValidationUtils.instance();
    }

    protected SchemaFilenameGenerator filenameGenerator() {
        return SchemaFilenameGenerator.instance();
    }
}
