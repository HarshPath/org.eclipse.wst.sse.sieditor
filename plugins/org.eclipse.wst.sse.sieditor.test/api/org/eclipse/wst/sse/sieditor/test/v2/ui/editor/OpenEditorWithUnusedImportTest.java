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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

/**
 * Tests service interface editor I/O operations
 * 
 *
 * 
 */
public class OpenEditorWithUnusedImportTest extends SIEditorBaseTest {

    private static final String WSDL_NAME = "pub/dirty/DirtyEditorUnusedImport.wsdl";
    private static final String XSD_IMPORT = "pub/dirty/UnusedImportForSIE.xsd";
    AbstractEditorWithSourcePage editor = null;

    @Override
    @After
    public void tearDown() throws Exception {
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);
    }

    @Test
    public void testServiceInterfaceEditorCreate() throws Exception {
        final SIETestEditorInput input = prepareEditorInput(false, true, WSDL_NAME);
        editor = openEditor(input, ServiceInterfaceEditor.EDITOR_ID);
        assertFalse(editor.isDirty());

    }

    protected SIETestEditorInput prepareEditorInput(final boolean readOnly, final boolean exists, final String fileName) throws IOException,
            CoreException {
        final IFile file = ResourceUtils.copyFileIntoTestProject(WSDL_NAME, getProject());
        ResourceUtils.copyFileIntoTestProject(XSD_IMPORT, getProject());

        BufferedReader reader = null;
        StringBuffer wsdlContents = null;
        try {
            reader = new BufferedReader(new FileReader(file.getLocation().toFile()));

            wsdlContents = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            wsdlContents.append(line);
        }
        } finally {

            if (reader != null)
                reader.close();
        }
        if (wsdlContents == null) {
            fail("could not read wsdl content");
        }
        final SIETestStorage storage = new SIETestStorage();
        storage.setWsdlAsString(wsdlContents.toString());
        storage.setCharset("UTF-8");
        storage.setFullPath(new Path("someproject/" + fileName));
        storage.setName(fileName);
        storage.setReadOnly(readOnly);

        final SIETestEditorInput editorInput = new SIETestEditorInput();
        editorInput.setStorage(storage);
        editorInput.setExists(exists);
        editorInput.setToolTipText(fileName);

        return editorInput;
    }

}
