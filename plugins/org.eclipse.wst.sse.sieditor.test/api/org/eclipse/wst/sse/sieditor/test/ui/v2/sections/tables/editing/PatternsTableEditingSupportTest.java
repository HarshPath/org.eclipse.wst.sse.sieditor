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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections.tables.editing;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.editing.PatternsTableEditingSupport;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;

public class PatternsTableEditingSupportTest {

    private static final String TEST_NON_EMPTY_VALUE = "ala-bala";
    private ElementNodeDetailsController detailsController;
    private IFacet patternFacet;

    private TableViewer viewer;

    private IConstraintsController constraintsController;

    @Before
    public void setUp() {
        patternFacet = createMock(IFacet.class);
        constraintsController = createMock(IConstraintsController.class);
        detailsController = createMock(ElementNodeDetailsController.class);
        viewer = new TableViewer(new Table(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE));
    }

    @Test
    public void focusLost_EmptyString() {
        expect(detailsController.getConstraintsController())
                .andReturn(constraintsController);
        constraintsController.deletePattern(patternFacet);
        replay(constraintsController);
        replay(detailsController);

        new PatternsTableEditingSupport(viewer, detailsController) {
            @Override
            public org.eclipse.jface.viewers.CellEditor getCellEditor(final Object element) {
                return super.getCellEditor(element);
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                super.setValue(element, value);
            }
        }.setValue(patternFacet, "");

        verify(constraintsController);

    }

    @Test
    public void focusLost_NonEmptyString() {
        expect(detailsController.getConstraintsController())
                .andReturn(constraintsController);
        constraintsController.setPattern(patternFacet, TEST_NON_EMPTY_VALUE);
        replay(constraintsController);
        replay(detailsController);

        new PatternsTableEditingSupport(viewer, detailsController) {
            @Override
            public org.eclipse.jface.viewers.CellEditor getCellEditor(final Object element) {
                return super.getCellEditor(element);
            }

            @Override
            protected void setValue(final Object element, final Object value) {
                super.setValue(element, value);
            }
        }.setValue(patternFacet, TEST_NON_EMPTY_VALUE);
    }

}
