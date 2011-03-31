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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class DataTypesEditorPageTest {

    private Composite parent;
    
    private Shell shell;

    class TestFormEditor extends FormEditor {
        
        public TestFormEditor(IEditorSite site, IEditorInput input) throws PartInitException {
            init(site, input);
            createPageContainer(parent);
        }
        
        @Override
        public boolean isSaveAsAllowed() {
            return false;
        }
        
        @Override
        public void doSaveAs() {
        }
        
        @Override
        public void doSave(IProgressMonitor monitor) {
        }
        
        @Override
        protected void addPages() {
        }
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        Display display = Display.getDefault();
        shell = new Shell(display);
        shell.open();        
        parent = new Composite(shell, SWT.NONE);

    }

    @After
    public void tearDown() throws Exception {
        shell.close();
    }
    
    @Test
    public void testCreateFormContent() {
                                
        IEditorSite site = createMock(IEditorSite.class);
        IEditorInput input = createMock(IEditorInput.class);        
        IWsdlModelRoot modelRoot = createNiceMock(IWsdlModelRoot.class);
        
        replay(modelRoot);
        
        try {
            TestFormEditor editor = new TestFormEditor(site, input);
            DataTypesEditorPage page = createPage(editor);
            page.setModel(modelRoot, false, false);
            page.init(editor.getEditorSite(), editor.getEditorInput());
            page.createPartControl(parent);
            
        } catch (PartInitException e) {
            fail(e.getMessage());
        }
    }
    /*
    @Test
    public void testInit() {
        fail("Not yet implemented");
    }

    @Test
    public void testDispose() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsDirty() {
        fail("Not yet implemented");
    }

    @Test
    public void testInitIEditorSiteIEditorInput() {
        fail("Not yet implemented");
    }

    @Test
    public void testComponentChanged() {
        fail("Not yet implemented");
    }

    @Test
    public void testDoSaveIProgressMonitor() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetModel() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetAndRefreshModel() {
        fail("Not yet implemented");
    }

    @Test
    public void testNotifyEvent() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetErrorMessage() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetDirty() {
        fail("Not yet implemented");
    }

    @Test
    public void testShowType() {
        fail("Not yet implemented");
    }
    */
    protected DataTypesEditorPage createPage(TestFormEditor editor) {
        DataTypesEditorPage page = new DataTypesEditorPage(editor) {
            protected void createMasterDetailsBlock(IManagedForm managedForm) {
                
            }
            @Override
            protected void createContextMenu() {
            }
        };
        return page;
    }
    
}
