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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;

public class DependencyColumnEditingSupport extends EditingSupport {

    private final CellEditor cellEditor;
    private final TableViewer viewer;

    private final IWizardPage wizardPage;

    public DependencyColumnEditingSupport(final TableViewer viewer, final IWizardPage wizardPage) {
        super(viewer);
        this.viewer = viewer;
        this.cellEditor = new TextCellEditor(viewer.getTable());
        this.wizardPage = wizardPage;
    }

    @Override
    protected CellEditor getCellEditor(final Object element) {
        return cellEditor;
    }

    @Override
    protected final void setValue(final Object element, final Object value) {
        ((SchemaNode) element).setFilename((String) value);
        viewer.refresh();
        wizardPage.getWizard().getContainer().updateButtons();
    }

    @Override
    protected boolean canEdit(final Object element) {
        return true;
    }

    @Override
    protected Object getValue(final Object element) {
        return ((SchemaNode) element).getFilename();
    }

}
