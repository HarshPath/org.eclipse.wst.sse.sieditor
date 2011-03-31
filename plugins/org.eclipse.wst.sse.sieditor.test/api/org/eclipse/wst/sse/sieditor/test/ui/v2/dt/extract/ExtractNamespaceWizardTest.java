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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.extract;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.ExtractNamespaceWizard;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.listeners.ExtractSchemaTextFieldModifyListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.ExtractSchemaWizardPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.SchemaDependenciesWizardPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.utils.ExtractSchemaWizardConstants;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class ExtractNamespaceWizardTest extends SIEditorBaseTest {

    private IWsdlModelRoot wsdlModelRoot;
    private WizardDialog dialog;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (wsdlModelRoot == null) {
            wsdlModelRoot = (IWsdlModelRoot) getModelRoot("pub/extract/NamespaceImportsWSDL.wsdl", "NamespaceImportsWSDL.wsdl",
                    ServiceInterfaceEditor.EDITOR_ID);

            ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                    "NamespaceImportsXSD.xsd");
            getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        }
    }

    @Test
    public void testCreateWizard_WithoutDependentSchemas() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.org/NewWSDLFile/")[0];

        final ExtractNamespaceWizardExpose wizard = createWizard(schema);

        final SchemaNode schemaNode = wizard.getSchemaNode();
        assertNotNull(schemaNode);
        final Set<SchemaNode> dependenciesSet = wizard.getDependenciesSet();
        assertNotNull(dependenciesSet);

        final ExtractSchemaWizardPage extractSchemaWizardPage = wizard.getExtractSchemaWizardPage();
        final SchemaDependenciesWizardPage schemaDependenciesWizardPage = wizard.getSchemaDependenciesWizardPage();

        assertPagesInitialState(extractSchemaWizardPage, schemaDependenciesWizardPage, schema);

        assertFalse(extractSchemaWizardPage.canFlipToNextPage());
        final Text saveLocationText = extractSchemaWizardPage.getSaveLocationText();
        assertEquals("/SIEditorBaseTest/data/ExtractedSchema1.xsd", saveLocationText.getText());

        final Listener[] listeners = saveLocationText.getListeners(SWT.Modify);
        assertEquals(1, listeners.length);
        assertTrue(listeners[0] instanceof TypedListener);
        assertTrue(((TypedListener) listeners[0]).getEventListener() instanceof ExtractSchemaTextFieldModifyListener);
    }

    @Test
    public void createWizard_WithDependentSchemas() throws Exception {
        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://namespace1")[0];

        final ExtractNamespaceWizardExpose wizard = createWizard(schema);

        final SchemaNode schemaNode = wizard.getSchemaNode();
        assertNotNull(schemaNode);
        final Set<SchemaNode> dependenciesSet = wizard.getDependenciesSet();
        assertNotNull(dependenciesSet);

        final ExtractSchemaWizardPage extractSchemaWizardPage = wizard.getExtractSchemaWizardPage();
        final SchemaDependenciesWizardPage schemaDependenciesWizardPage = wizard.getSchemaDependenciesWizardPage();

        assertPagesInitialState(extractSchemaWizardPage, schemaDependenciesWizardPage, schema);

        assertTrue(extractSchemaWizardPage.canFlipToNextPage());
        final Text saveLocationText = extractSchemaWizardPage.getSaveLocationText();
        assertEquals("/SIEditorBaseTest/data/ExtractedSchema1.xsd", saveLocationText.getText());

        final Listener[] listeners = saveLocationText.getListeners(SWT.Modify);
        assertEquals(1, listeners.length);
        assertTrue(listeners[0] instanceof TypedListener);
        assertTrue(((TypedListener) listeners[0]).getEventListener() instanceof ExtractSchemaTextFieldModifyListener);
    }

    private void assertPagesInitialState(final ExtractSchemaWizardPage extractSchemaWizardPage,
            final SchemaDependenciesWizardPage schemaDependenciesWizardPage, final ISchema schema) {
        assertNotNull(extractSchemaWizardPage);
        assertEquals(Messages.ExtractSchemaWizardPage_page_title, extractSchemaWizardPage.getTitle());
        assertEquals(MessageFormat.format(Messages.ExtractSchemaWizardPage_page_description, schema.getNamespace()),
                extractSchemaWizardPage.getDescription());
        assertTrue(extractSchemaWizardPage.isPageComplete());
        assertNotNull(schemaDependenciesWizardPage);
        assertEquals(Messages.SchemaDependenciesWizardPage_page_title, schemaDependenciesWizardPage.getTitle());
        assertEquals(MessageFormat.format(Messages.SchemaDependenciesWizardPage_page_description, schema.getNamespace()),
                schemaDependenciesWizardPage.getDescription());
        assertTrue(schemaDependenciesWizardPage.isPageComplete());
    }

    private ExtractNamespaceWizardExpose createWizard(final ISchema schema) {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final ExtractNamespaceWizardExpose wizard = new ExtractNamespaceWizardExpose();
        wizard.init(schema);

        dialog = new WizardDialog(window.getShell(), wizard);
        dialog.setPageSize(ExtractSchemaWizardConstants.EXTRACT_WIZARD_DIALOG_WIDTH,
                ExtractSchemaWizardConstants.EXTRACT_WIZARD_DIALOG_HEIGHT);
        wizard.setWizardDialog(dialog);
        wizard.addPages();
        wizard.createPageControls(window.getShell());
        return wizard;
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (dialog != null) {
            dialog.close();
        }
        super.tearDown();
    }

    // =========================================================
    // mock
    // =========================================================

    private class ExtractNamespaceWizardExpose extends ExtractNamespaceWizard {
        @Override
        public SchemaNode getSchemaNode() {
            return super.getSchemaNode();
        }

        @Override
        public Set<SchemaNode> getDependenciesSet() {
            return super.getDependenciesSet();
        }
    }

}
