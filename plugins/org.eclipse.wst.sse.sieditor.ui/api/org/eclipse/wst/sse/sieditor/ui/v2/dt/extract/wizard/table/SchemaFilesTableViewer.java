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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.table;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extract.wizard.utils.ExtractSchemaWizardConstants;

public class SchemaFilesTableViewer extends TableViewer {

    private final Set<SchemaNode> schemas;
    private final IWizardPage wizardPage;

    public SchemaFilesTableViewer(final Composite parent, final Set<SchemaNode> schemas, final IWizardPage wizardPage) {
        super(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        this.schemas = schemas;
        this.wizardPage = wizardPage;

        this.createDependenciesTable(parent);
    }

    private void createDependenciesTable(final Composite container) {
        final Table table = getTable();

        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        createNamespaceColumn(this);
        createFileColumn(this);

        setContentProvider(new DependentSchemasContentProvider());
        setLabelProvider(new DependentSchemasLabelProvider());

        setInput(schemas);
    }

    private void createNamespaceColumn(final TableViewer viewer) {
        final TableViewerColumn namespaceViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        namespaceViewerColumn.getColumn().setText(Messages.SchemaDependenciesWizardPage_dependent_xml_schemas_column_header);
        namespaceViewerColumn.getColumn().setWidth(ExtractSchemaWizardConstants.DEPENDENCIES_TABLE_NAMESPACE_COLUMN_WIDTH);
    }

    private void createFileColumn(final TableViewer viewer) {
        final TableViewerColumn fileViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        fileViewerColumn.getColumn().setText(Messages.SchemaDependenciesWizardPage_files_column_header);
        fileViewerColumn.getColumn().setWidth(ExtractSchemaWizardConstants.DEPENDENCIES_TABLE_FILES_COLUMN_WIDTH);
        fileViewerColumn.setEditingSupport(new DependencyColumnEditingSupport(viewer, wizardPage));
    }

    // =========================================================
    // content provider
    // =========================================================

    @SuppressWarnings("unchecked")
    protected class DependentSchemasContentProvider implements IStructuredContentProvider {
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof Set) {
                final Set<SchemaNode> set = (Set<SchemaNode>) inputElement;
                return set.toArray(new Object[set.size()]);
            }
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    // =========================================================
    // label provider
    // =========================================================

    protected class DependentSchemasLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;// Activator.getDefault().getImage(FieldDecorationRegistry.DEC_ERROR);
        }

        @Override
        public String getColumnText(final Object element, final int columnIndex) {
            final SchemaNode node = (SchemaNode) element;
            switch (columnIndex) {
            case 0:
                return node.getNamespace();
            case 1:
                return node.getFilename();
            }
            return null;
        }
    }

}
