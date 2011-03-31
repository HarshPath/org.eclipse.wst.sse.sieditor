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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

/*
 * This test case checks that in this certain situation no IllegalStateExvception is thrown.
 * 
 */
public class TestGetReferencedServices extends SIEditorBaseTest {

    private IFile rootFile;
    private AbstractEditorWithSourcePage editor = null;
    @Before
    public void setUp() throws Exception {
        super.setUp();


            ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

            IFile file = ResourceUtils.copyFileIntoTestProject("pub/import/transaction/types.xsd", null, this.getProject(),
                    "types.xsd");
            refreshProjectNFile(file);
            
            IFolder wsdlsFolder = getProject().getFolder("wsdls");
            if(!wsdlsFolder.exists()) {
            	wsdlsFolder.create(false, true, null);
            }
            file = ResourceUtils.copyFileIntoTestProject("pub/import/transaction/wsdls/NewWSDLFile.wsdl", "wsdls", this
                    .getProject(), "NewWSDLFile.wsdl");
            refreshProjectNFile(file);

            rootFile = ResourceUtils.copyFileIntoTestProject("pub/import/transaction/wsdls/stockquoteservice.wsdl", "wsdls", this
                    .getProject(), "stockquoteservice.wsdl");
            refreshProjectNFile(file);


    }

    @Override
    public void tearDown() throws Exception {
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        ThreadUtils.waitOutOfUI(10);

    }

    @Test
    public void testImportUnresolvedServices() throws PartInitException {

        // test no java.lang.IllegalStateException: Cannot modify resource set
        // without a write transaction is thrown
        editor = (AbstractEditorWithSourcePage)openEditor(rootFile);
        assertTrue(editor instanceof ServiceInterfaceEditor);
        


    }

    private IEditorPart openEditor(IFile iFile) throws PartInitException {
        IEditorInput input = new FileEditorInput(iFile);
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            IEditorPart editor = IDE.openEditor(page, input, ServiceInterfaceEditor.EDITOR_ID);
            return editor;
        }
        return null;
    }

}
