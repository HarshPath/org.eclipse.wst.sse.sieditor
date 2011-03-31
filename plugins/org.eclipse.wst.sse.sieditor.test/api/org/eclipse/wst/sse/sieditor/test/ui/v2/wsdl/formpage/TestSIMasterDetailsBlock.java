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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.formpage;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.SIMasterDetailsBlock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 *
 * 
 */
public class TestSIMasterDetailsBlock {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testRefreshTreeViewerIsCalledOnComponentChangedEvent() {
        final boolean refreshTreeViewer_Called[] = {false};
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        final TreeViewer treeViewerMock = new TreeViewer(new Composite(shell, SWT.None));

        final SIMasterDetailsBlock masterDetailsBlock = new SIMasterDetailsBlock() {
            {
                this.treeViewer = treeViewerMock;
            }
            
            @Override
            protected void updateButtonsState(IStructuredSelection selection) {
                // do nothing            }
            }
            @Override
            public void refreshTreeViewer() {
                refreshTreeViewer_Called[0] = true; 
            }
            
        };
     
        IDescription description = createNiceMock(IDescription.class);
        final IModelChangeEvent event = EasyMock.createNiceMock(IModelChangeEvent.class);
        expect(event.getObject()).andReturn(description).anyTimes();
        replay(event);
        
        masterDetailsBlock.componentChanged(event);

        assertTrue("Expected treeviewer refresh call on componentChanged event.", 
                refreshTreeViewer_Called[0]);
    }

}
