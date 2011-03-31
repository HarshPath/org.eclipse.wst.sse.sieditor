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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerEditorActivationStrategy;
import org.junit.Before;
import org.junit.Test;


public class TestIsEditorActivationInTreeViewerEditorActivationStrategy {
    private static Event event;
    private static MouseEvent mouseEvent;
    private static SITreeViewerEditorActivationStrategyTest strategy;
    protected static final int LOWERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING = 1000;
    protected static final int UPPERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING = 4100;

    @Before
    public void setUp() {
        final ITreeNode firstElement = createNiceMock(ITreeNode.class);
        replay(firstElement);

        final IStructuredSelection selection = createMock(IStructuredSelection.class);
        expect(selection.getFirstElement()).andReturn(firstElement).anyTimes();
        replay(selection);

        Display display = Display.getDefault();
        Composite composite = new Composite(new Shell(display), SWT.NONE);

        TreeViewer viewer = new TreeViewer(composite) {
            @Override
            public ISelection getSelection() {
                return selection;
            }

        };

        event = new Event();
        event.widget = composite;

        mouseEvent = new MouseEvent(event);

        strategy = new SITreeViewerEditorActivationStrategyTest(viewer);
        strategy.mouseEvent = mouseEvent;
    }

    class SITreeViewerEditorActivationStrategyTest extends SITreeViewerEditorActivationStrategy {
        public MouseEvent mouseEvent = null;

        public SITreeViewerEditorActivationStrategyTest(TreeViewer viewer) {
            super(viewer);
        }

        @Override
        protected MouseEvent getMouseEvent(ColumnViewerEditorActivationEvent event) {
            return mouseEvent;
        }

        @Override
        public boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
            return super.isEditorActivationEvent(event);
        }

    }

    @Test
    public void testIsEditorActivationStrategyWithSimpleDoubleClickEvent() {

        mouseEvent.count = 2;

        assertTrue("The current selection must be not modifiable", !strategy.isEditorActivationEvent(null));
    }

    @Test
    public void testIsEditorActivationStrategyWithSingleClickInTimeRangeForEditing() throws InterruptedException {

        mouseEvent.count = 1;

        strategy.isEditorActivationEvent(null);

        Thread.sleep(UPPERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING/2);

        assertTrue("The current selection must be modifiable", strategy.isEditorActivationEvent(null));
    }
    
    @Test
    public void testIsEditorActivationStrategyWithBigTimeRangeDoubleClick() throws InterruptedException {

        mouseEvent.count = 1;

        strategy.isEditorActivationEvent(null);

        Thread.sleep(UPPERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING);

        assertTrue("The current selection must be not modifiable", !strategy.isEditorActivationEvent(null));
    }
    

    @Test
    public void testIsEditorActivationStrategyWithSmallTimeRangeDoubleClick() throws InterruptedException {

        mouseEvent.count = 1;

        strategy.isEditorActivationEvent(null);

        Thread.sleep(LOWERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING/2);

        assertTrue("The current selection must be not modifiable", !strategy.isEditorActivationEvent(null));
    }
}
