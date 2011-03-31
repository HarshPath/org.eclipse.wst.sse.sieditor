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

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.junit.Before;
import org.junit.Test;


public class TestSITreeContextMenuListener {

    private boolean[] enablementCalled;

    @Before
    public void setUp() {
        enablementCalled = new boolean[] { false };
    }

    @Test
    public void testMenuAboutToShow_NoSelection() {
        final List selectionList = new LinkedList();
        testAboutToShow(selectionList);
        assertTrue(enablementCalled[0]);
    }

    @Test
    public void defaultEnablement_WithSelection() {
        final List selectionList = new LinkedList();
        selectionList.add(new Object());
        testAboutToShow(selectionList);
        assertTrue(enablementCalled[0]);
    }

    private void testAboutToShow(final List selectionList) {
        final IStructuredSelection treeSelection = createNiceMock(IStructuredSelection.class);
        expect(treeSelection.toList()).andReturn(selectionList);
        replay(treeSelection);

        final SIFormPageController mockController = createNiceMock(SIFormPageController.class);
        mockController.addNewServiceInterface();
        mockController.addNewOperation((ITreeNode) anyObject());
        mockController.addNewParameter((ITreeNode) anyObject(), eq(OperationCategory.INPUT));
        mockController.addNewParameter((ITreeNode) anyObject(), eq(OperationCategory.OUTPUT));
        mockController.addNewFault((ITreeNode) anyObject());
        mockController.deleteItemsTriggered(Arrays.asList((ITreeNode) anyObject()));
        replay(mockController);

        final boolean[] editElement_Called = { false };
        final Display display = Display.getDefault();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
        final TreeViewer mockTreeViewer = new TreeViewer(composite) {
            @Override
            public void editElement(final Object element, final int column) {
                editElement_Called[0] = true;
            }

            @Override
            public ISelection getSelection() {
                return treeSelection;
            }
        };

        final IMenuManager menu = new MenuManager();

        final SITreeContextMenuListener listener = new SITreeContextMenuListener(mockController, mockTreeViewer) {

            @Override
            protected void updateActionsState(final IStructuredSelection selection) {
                enablementCalled[0] = true;
            }
        };
        listener.menuAboutToShow(menu);

        assertTrue(containsID(menu.getItems(), ContextMenuConstants.GROUP_ADD_ITEMS));
        assertTrue(containsID(menu.getItems(), ContextMenuConstants.GROUP_EDIT));

        final List<IAction> actions = getActions(menu.getItems());
        assertEquals(7, actions.size());
        for (final IAction action : actions) {
            action.run();
        }

        verify(mockController);
        // assertTrue(editElement_Called[0]);
    }

    private List<IAction> getActions(final IContributionItem[] items) {
        final List<IAction> actions = new ArrayList<IAction>();

        for (final IContributionItem item : items) {
            if (item instanceof ActionContributionItem) {
                actions.add(((ActionContributionItem) item).getAction());
            } else if (item instanceof MenuManager) {
                actions.addAll(getActions(((MenuManager) item).getItems()));
            }
        }
        return actions;
    }

    private boolean containsID(final IContributionItem[] items, final String id) {
        for (final IContributionItem item : items) {
            if (id.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

}
