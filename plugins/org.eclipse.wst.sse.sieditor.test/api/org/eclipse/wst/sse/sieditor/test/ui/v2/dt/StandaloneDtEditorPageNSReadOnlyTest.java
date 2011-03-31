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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StandaloneDtEditorPage;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.MockInterface;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Test;


import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

/**
 *
 *
 */
public class StandaloneDtEditorPageNSReadOnlyTest extends SIEditorBaseTest{

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private static class StandaloneDtEditorPageExposer extends StandaloneDtEditorPage{

        public StandaloneDtEditorPageExposer(FormEditor editor) {
            super(editor);
        }
        
        public Text getNamespaceText(){
            return namespaceTextControl;
        }
    }
    
    private IXSDModelRoot modelRoot;
    private FormEditor formEditor;

    @Test
    public final void testMakeNamespaceReadOnly() throws IOException, CoreException {
        setUpFormEditorAndSIPage(false);
        StandaloneDtEditorPageExposer page = (StandaloneDtEditorPageExposer) formEditor.getSelectedPage();
        Text namespaceText = page.getNamespaceText();
        assertTrue(namespaceText.getEditable());
        page.setModel(modelRoot, true, true);
        assertFalse(namespaceText.getEditable());
    }

    private void setUpFormEditorAndSIPage(final boolean readOnly) throws IOException, CoreException {
        modelRoot = getXSDModelRoot("pub/self/mix/example.xsd", "example.xsd");

        formEditor = new FormEditor() {
            StandaloneDtEditorPageExposer page = new StandaloneDtEditorPageExposer(this);
            @Override
            public boolean isSaveAsAllowed() {return false;}
            @Override
            public void doSaveAs() {}
            @Override
            public void doSave(IProgressMonitor monitor) {}
            @Override
            protected void addPages() {
                try {
                    page.setModel(modelRoot, readOnly, false);
                    addPage(page);
                } catch (PartInitException e) {
                    fail(e.getMessage());
                }
            }
        };
        IEditorSite editorSite = createNiceMock(IEditorSite.class);
        expect(editorSite.getService(IPartService.class)).andReturn(createMock(MockInterface.class)).anyTimes();
        expect(editorSite.getKeyBindingService()).andReturn(createMock(MockInterface.class)).anyTimes();
        IHandlerService handlerService = createNiceMock(IHandlerService.class);
        expect(editorSite.getService(IHandlerService.class)).andReturn(handlerService).anyTimes();
        replay(editorSite,handlerService);
        try {
            formEditor.init(editorSite, createMock(IEditorInput.class));
        } catch (PartInitException e) {
            fail(e.getMessage());
        }
        Shell shell = new Shell();
        formEditor.createPartControl(shell);
    }

}
