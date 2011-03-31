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
package org.eclipse.wst.sse.sieditor.test.v2.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.fwk.Activator;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class NonUIThreadOpenEditorTest extends SIEditorBaseTest{

    public static final String TEST_WSDL_NAME = "PurchaseOrderConfirmation.wsdl"; //$NON-NLS-1$
    public static final String TEST_XSD_NAME = "example.xsd"; //$NON-NLS-1$
    
    @Before
    public void setUp() throws Exception {
        Display.getDefault();
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        // Flush threads that wait UI thread for execution
        // Otherwise exceptions on closing editor will happen
        ThreadUtils.waitOutOfUI(100);
    }

    @Test
    /**
     * Tests that refreshing editor input from a non UI thread would not result in any crashes 
     */
    public final void testSetInputServiceInterfaceEditorFromNonUIThread() throws Exception{
        startEditorFromNonUITrhead(TEST_WSDL_NAME , ServiceInterfaceEditor.EDITOR_ID);
    }

    @Test
    /**
     * Tests that refreshing editor input from a non UI thread would not result in any crashes 
     */
    public final void testSetInputDataTypesEditorFromNonUIThread() throws Exception{
        startEditorFromNonUITrhead(TEST_XSD_NAME , DataTypesEditor.EDITOR_ID);
    }

    private void startEditorFromNonUITrhead(final String fileName, final String editorID) throws Exception {
        //close all opened editors
        final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = null;
        // set up test
        IFile file = null;
        try {
            file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, this.getProject());
            refreshProjectNFile(file);
            FileEditorInput input = new FileEditorInput(file);
            final IEditorPart openEditor =  IDE.openEditor(activePage, input, editorID);
            editor = openEditor;
            // the non UI job
            Job openJob = new Job("openJob") { //$NON-NLS-1$
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        ((AbstractEditorWithSourcePage) openEditor).revertContentsToSavedVersion();
                        return Status.OK_STATUS;
                    } catch (Exception e) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
                    }
                }
            };
            // start the job
            openJob.schedule();
            // check the result
            while (openJob.getResult() == null) {
                openJob.join();
            }
            if (!openJob.getResult().isOK()) {
                fail(openJob.getResult().getMessage());
            }
        } finally {
            if (editor instanceof FormEditor) {
                ((FormEditor)editor).close(false);
            }
        }
    }
}
