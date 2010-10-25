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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class SITreeViewerEditorActivationStrategy extends ColumnViewerEditorActivationStrategy {
    protected static final int LOWERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING = 1000;
    protected static final int UPPERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING = 4000;
    protected ITreeNode lastSelectedElement;
    protected boolean isModifiable = false;
    protected long timeOfLastClick = 0;

    protected KeyListener keyboardActivationListener;
    protected TreeViewer viewer;

    public SITreeViewerEditorActivationStrategy(TreeViewer viewer) {
        super(viewer);
        this.viewer = viewer;

    }

    @Override
    protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
        // if "F2" button is pressed, the eventType is SWT.ALPHA and the
        // function must return the default implementation
        if (isF2KeyPressedEvent(event)) {
            return super.isEditorActivationEvent(event);
        }
        
        int count = getMouseEvent(event).count;
        ITreeNode currentSelection = (ITreeNode) ((IStructuredSelection) viewer.getSelection()).getFirstElement();

        long currentTimeInMillSec = System.currentTimeMillis();

        boolean isInTimeRangeForEditing = (currentTimeInMillSec - timeOfLastClick > LOWERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING)
                && (currentTimeInMillSec - timeOfLastClick < UPPERBOUND_OF_TIME_RANGE_FOR_CELL_EDITING);

        if (isInTimeRangeForEditing && currentSelection != null && (currentSelection.equals(lastSelectedElement)) && count < 2) {
            isModifiable = true;
        } else {
            isModifiable = false;
        }
        timeOfLastClick = currentTimeInMillSec;
        lastSelectedElement = currentSelection;

        return isModifiable;
    }

    @Override
    public void setEnableEditorActivationWithKeyboard(boolean enable) {
        if (enable) {
            if (keyboardActivationListener == null) {
                keyboardActivationListener = new KeyListener() {

                    public void keyPressed(KeyEvent e) {
                        if (e.keyCode == SWT.F2) {
                            Object firstElement = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                            viewer.editElement(firstElement, 0);
                        }
                    }

                    public void keyReleased(KeyEvent e) {

                    }

                };
                viewer.getControl().addKeyListener(keyboardActivationListener);
            }
        } else {
            if (keyboardActivationListener != null) {
                viewer.getControl().removeKeyListener(keyboardActivationListener);
                keyboardActivationListener = null;
            }
        }
    }

    public KeyListener getKeyListener() {
        return keyboardActivationListener;
    }

    protected MouseEvent getMouseEvent(ColumnViewerEditorActivationEvent event) {
        return ((MouseEvent) event.sourceEvent);
    }

  
    protected boolean isF2KeyPressedEvent(ColumnViewerEditorActivationEvent event) {
        if (event == null)
            return false;
        return event.eventType == SWT.ALPHA;

    }

}
