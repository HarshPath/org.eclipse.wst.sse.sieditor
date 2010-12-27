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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.ExtractNamespaceRunnable;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaDependenciesUtils;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.SchemaLocationUtils;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.ExtractSchemaWizardPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.SchemaDependenciesWizardPage;

public class ExtractNamespaceWizard extends Wizard {

    private SchemaNode schemaNode;
    private Set<SchemaNode> dependenciesSet;

    private ExtractSchemaWizardPage extractSchemaWizardPage;
    private SchemaDependenciesWizardPage schemaDependenciesWizardPage;

    private IPath wsdlLocationPath;

    private WizardDialog wizardDialog;
    private boolean keepInlinedNamespaces;

    public IStatus init(final ISchema schema) {
        try {
            setWindowTitle(Messages.ExtractNamespaceWizard_wizard_title);
            setNeedsProgressMonitor(true);
            setHelpAvailable(false);

            wsdlLocationPath = locationUtils().getLocationRelativeToWorkspace(
                    new Path(((Description) schema.getRoot()).getRawLocation()));

            schemaNode = SchemaDependenciesUtils.instance().buildSchemaDependenciesTree(schema);
            dependenciesSet = SchemaDependenciesUtils.instance().getSchemaDependencies(schemaNode);
        } catch (final Exception e) {
            Logger.logError("Start of Extract Namespace wizard failed during the initialization", e.getMessage(), e); //$NON-NLS-1$
            return new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR,
                    Messages.ExtractNamespaceWizard_initialization_error_status_msg, e);

        }
        return Status.OK_STATUS;
    }

    @Override
    public void addPages() {
        extractSchemaWizardPage = new ExtractSchemaWizardPage();
        extractSchemaWizardPage.init(schemaNode, dependenciesSet);
        addPage(extractSchemaWizardPage);
        schemaDependenciesWizardPage = new SchemaDependenciesWizardPage();
        schemaDependenciesWizardPage.init(schemaNode, dependenciesSet);
        addPage(schemaDependenciesWizardPage);
    }

    @Override
    public boolean performFinish() {
        this.keepInlinedNamespaces = extractSchemaWizardPage.getKeepInlinedNamespacesCheckBox().getSelection();

        final ExtractNamespaceRunnable runnable = new ExtractNamespaceRunnable(schemaNode, dependenciesSet, keepInlinedNamespaces,
                wsdlLocationPath);
        try {
            wizardDialog.run(false, true, runnable);

        } catch (final InvocationTargetException e) {
            Logger.logError(MessageFormat.format("Extract of namespace \"{0}\" failed",//$NON-NLS-1$ 
                    schemaNode.getNamespace()), e);
            return false;

        } catch (final InterruptedException e) {
            // was cancelled by the user
            return false;

        } finally {
            final IStatus extractSchemaRunnableStatus = runnable.getStatus();
            if (extractSchemaRunnableStatus.getSeverity() == IStatus.ERROR) {
                StatusUtils.showStatusDialog(Messages.ExtractNamespaceWizard_error_status_dlg_title, MessageFormat.format(
                        Messages.ExtractNamespaceWizard_error_status_dlg_msg, schemaNode.getNamespace()), runnable.getStatus());
            }
        }
        return true;
    }

    // =========================================================
    // helpers
    // ========================================================

    public void setWizardDialog(final WizardDialog wizardDialog) {
        this.wizardDialog = wizardDialog;
    }

    public WizardDialog getWizardDialog() {
        return wizardDialog;
    }

    public SchemaDependenciesWizardPage getSchemaDependenciesWizardPage() {
        return schemaDependenciesWizardPage;
    }

    public ExtractSchemaWizardPage getExtractSchemaWizardPage() {
        return extractSchemaWizardPage;
    }

    protected SchemaNode getSchemaNode() {
        return schemaNode;
    }

    protected Set<SchemaNode> getDependenciesSet() {
        return dependenciesSet;
    }

    protected SchemaLocationUtils locationUtils() {
        return SchemaLocationUtils.instance();
    }

}
