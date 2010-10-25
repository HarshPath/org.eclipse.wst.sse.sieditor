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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.editing;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

/**
 * Editing support for the pattern constraint section of the xsd types/elements
 * 
 * 
 * 
 */
public class PatternsTableEditingSupport extends EditingSupport {

    protected final TextCellEditor cellEditor;
    private final ElementNodeDetailsController detailsController;

    /**
     * create new instance
     * 
     * @param viewer
     *            - the table viewer to add the editing support to
     * @param detailsController
     *            - the controller to use to set the new edited value
     */
    public PatternsTableEditingSupport(final TableViewer viewer, final ElementNodeDetailsController detailsController) {
        super(viewer);
        this.detailsController = detailsController;
        cellEditor = new TextCellEditor(viewer.getTable());

    }

    @Override
    protected boolean canEdit(final Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(final Object element) {
        return cellEditor;
    }


    @Override
    protected Object getValue(final Object element) {
        return ((IFacet) element).getValue();
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        if (((String) value).trim().isEmpty()) {
            getDetailsController().getConstraintsController().deletePattern((IFacet) element);
        } else {
            getDetailsController().getConstraintsController().setPattern((IFacet) element, (String) value);
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    protected ElementNodeDetailsController getDetailsController() {
        return detailsController;
    }

}