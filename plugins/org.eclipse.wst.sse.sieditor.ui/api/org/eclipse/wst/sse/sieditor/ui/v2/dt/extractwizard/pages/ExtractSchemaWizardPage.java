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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SaveAsDialog;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.listeners.ExtractSchemaTextFieldModifyListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.utils.ExtractSchemaWizardConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.utils.SchemaFilenameGenerator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.utils.WizardPageValidationUtils;

public class ExtractSchemaWizardPage extends WizardPage {

    private SchemaNode schemaNode;

    private Set<SchemaNode> dependenciesSet;

    private Text saveLocationText;

    private Button browseButton;

    private Button keepInlinedNamespacesCheckBox;

    private boolean pageInitialized = false;

    public ExtractSchemaWizardPage() {
        super("Extract XML Schema Page"); //$NON-NLS-1$
    }

    public void init(final SchemaNode schemaNode, final Set<SchemaNode> dependenciesSet) {
        this.schemaNode = schemaNode;
        this.dependenciesSet = dependenciesSet;

        final String fullLocation = createDefaultSaveAsLocation();
        final Path fullPath = new Path(fullLocation);
        schemaNode.setPath(fullPath.removeLastSegments(1));
        schemaNode.updateImportsPaths();
        schemaNode.setFilename(fullPath.lastSegment());

        this.pageInitialized = true;
    }

    @Override
    public void createControl(final Composite parent) {
        setTitle(Messages.ExtractSchemaWizardPage_page_title);
        setPageDescription(ExtractSchemaWizardConstants.KEEP_NAMESPACE_DEFAULT_SELECTED);

        createContents(parent);
        initContents(parent);
    }

    private void createContents(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);
        setControlLayout(container);
        setControl(container);

        createSaveAsLabel(container);
        createSaveLocationText(container);
        createBrowseButton(container);
        createUseKeepInlinedNamespacesCheckbox(container);
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

    private void createSaveAsLabel(final Composite container) {
        final Label saveAsLabel = new Label(container, SWT.NONE);
        saveAsLabel.setText(Messages.ExtractSchemaWizardPage_save_as_label);
        GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(saveAsLabel);
    }

    private void createSaveLocationText(final Composite container) {
        saveLocationText = new Text(container, SWT.BORDER);
        saveLocationText.setEditable(false);
        saveLocationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    @Override
    public boolean canFlipToNextPage() {
        return !dependenciesSet.isEmpty() && isPageComplete();
    }

    private void createBrowseButton(final Composite container) {
        browseButton = new Button(container, SWT.PUSH);
        browseButton.setText(Messages.ExtractSchemaWizardPage_browse_button);
    }

    private void createUseKeepInlinedNamespacesCheckbox(final Composite container) {
        // dummy label to indent the checkbox one cell to the right
        new Label(container, SWT.NONE);
        keepInlinedNamespacesCheckBox = new Button(container, SWT.CHECK);
        keepInlinedNamespacesCheckBox
                .setText(Messages.ExtractSchemaWizardPage_keep_inlined_namespaces_in_current_wsdl_checkbox_label);
        GridDataFactory.swtDefaults().span(2, 1).grab(true, false).applyTo(keepInlinedNamespacesCheckBox);
        keepInlinedNamespacesCheckBox.setSelection(ExtractSchemaWizardConstants.KEEP_NAMESPACE_DEFAULT_SELECTED);
    }

    private void initContents(final Composite parent) {
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final SaveAsDialog saveAsDialog = new SaveAsDialog(parent.getShell());
                saveAsDialog.setOriginalFile(schemaNode.getIFile());
                saveAsDialog.open();
                final IPath result = saveAsDialog.getResult();
                if (result != null) {
                    saveLocationText.setText(result.toString());
                }
            }
        });

        saveLocationText.setText(schemaNode.getFullPath().toString());
        saveLocationText.addModifyListener(new ExtractSchemaTextFieldModifyListener(this, schemaNode));

        keepInlinedNamespacesCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setPageDescription(keepInlinedNamespacesCheckBox.getSelection());
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    @Override
    public boolean isPageComplete() {
        if (!pageInitialized) {
            return false;
        }
        final IStatus validationStatus = pageValidationUtils().validateSchemaDependencyNode(schemaNode, false);
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

    public Text getSaveLocationText() {
        return saveLocationText;
    }

    protected String createDefaultSaveAsLocation() {
        final String platformLocation = ((IDescription) schemaNode.getSchema().getRoot()).getComponent().getLocation();

        final String pathLocation = platformLocation.substring(ResourceUtils.PLATFORM_RESOURCE_PREFIX.length(), platformLocation
                .lastIndexOf('/') + 1);

        final String filename = filenameGenerator().getFilename(new Path(pathLocation));
        return pathLocation + filename;
    }

    protected WizardPageValidationUtils pageValidationUtils() {
        return WizardPageValidationUtils.instance();
    }

    public Button getKeepInlinedNamespacesCheckBox() {
        return keepInlinedNamespacesCheckBox;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected SchemaFilenameGenerator filenameGenerator() {
        return SchemaFilenameGenerator.instance();
    }

    private void setPageDescription(final boolean keepNamespaces) {
        String description = MessageFormat.format(Messages.ExtractSchemaWizardPage_page_description, schemaNode.getNamespace());

        if (keepNamespaces) {
//            description += UIConstants.SPACE + "The document will not be changed after the extract.";
        } else {
            description += UIConstants.SPACE
                    + Messages.ExtractSchemaWizardPage_references_will_be_replaced_with_imports_description;
        }

        setDescription(description);
    }

}
