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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerEditorActivationStrategy;
import org.junit.Test;


public class TestSITreeViewerEditorActivationStrategy {

    @Test
    public void testEnableAndDisableEditorActivationWithKeyboard() {
        Display display = Display.getDefault();
        Composite composite = new Composite(new Shell(display), SWT.NONE);
        TreeViewer viewer = new TreeViewer(composite);

        SITreeViewerEditorActivationStrategy strategy = new SITreeViewerEditorActivationStrategy(viewer);

        int numUpListeners = viewer.getControl().getListeners(SWT.KeyUp).length;
        int numDownListeners = viewer.getControl().getListeners(SWT.KeyDown).length;

        assertNull(strategy.getKeyListener());

        strategy.setEnableEditorActivationWithKeyboard(true);

        assertNotNull(strategy.getKeyListener());
        assertEquals(numUpListeners + 1, viewer.getControl().getListeners(SWT.KeyUp).length);
        assertEquals(numDownListeners + 1, viewer.getControl().getListeners(SWT.KeyDown).length);

        strategy.setEnableEditorActivationWithKeyboard(false);

        assertNull(strategy.getKeyListener());
        assertEquals(numUpListeners, viewer.getControl().getListeners(SWT.KeyUp).length);
        assertEquals(numDownListeners, viewer.getControl().getListeners(SWT.KeyDown).length);
    }

    @Test
    public void testKeyboardListenerF2KeyEditElement() {
        final Object firstElement = new Object();

        final IStructuredSelection selection = createMock(IStructuredSelection.class);
        expect(selection.getFirstElement()).andReturn(firstElement).anyTimes();
        replay(selection);

        Display display = Display.getDefault();
        Composite composite = new Composite(new Shell(display), SWT.NONE);
        final boolean[] editElement_Called = { false };
        TreeViewer viewer = new TreeViewer(composite) {
            @Override
            public ISelection getSelection() {
                return selection;
            }

            @Override
            public void editElement(Object element, int column) {
                editElement_Called[0] = true;
                assertEquals(firstElement, element);
            }

        };

        SITreeViewerEditorActivationStrategy strategy = new SITreeViewerEditorActivationStrategy(viewer);

        assertNull(strategy.getKeyListener());

        strategy.setEnableEditorActivationWithKeyboard(true);

        assertNotNull(strategy.getKeyListener());

        KeyListener keyListener = strategy.getKeyListener();
        Event e = new Event();
        e.widget = composite;
        KeyEvent event = new KeyEvent(e);
        event.keyCode = SWT.F2;
        event.widget = composite;
        keyListener.keyPressed(event);

        assertTrue(editElement_Called[0]);
    }


   
}
