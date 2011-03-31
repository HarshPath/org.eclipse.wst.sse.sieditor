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

import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public abstract class AbstractQuickfixTest {

    protected static IXSDModelRoot root;
    protected static final String XSD_FILE = "InvalidSchema.xsd";
    protected static IProject project;
    protected static int i = 1;

    protected DataTypesEditor editor = null;

    @Test
    public abstract void testQuickFix() throws Exception;

    private IFile file;

    private static DataTypesEditor openEditor(IEditorInput input, String editorId) throws PartInitException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            IEditorPart editor = page.openEditor(input, editorId);
            return (DataTypesEditor) editor;
        }
        return null;
    }

    @Before
    public void setUpFile() throws Exception {
        // moved from @BeforeClass-----
        project = ResourcesPlugin.getWorkspace().getRoot().getProject("invalid_schema");
        if (!project.exists()) {
            project.create(null);
        }
        project.open(null);
        // ------
        file = ResourceUtils.copyFileIntoTestProject(XSD_FILE, null, project, "Copied" + i++ + ".xsd");
        FileEditorInput input = new FileEditorInput(file);
        editor = openEditor(input, DataTypesEditor.EDITOR_ID);

        root = (IXSDModelRoot) editor.getModelRoot();
        ISchema schema = root.getSchema();
        assertNull(schema.getComponent().getSchemaForSchema());
    }

    @After
    public void tearDownEditor() throws CoreException {
        if (editor != null) {
            editor.close(false);
        }
        if (file != null) {
            file.delete(true, null);
        }

        // moved from @AfterClass----
        if (project != null) {
            project.delete(true, null);
        }

        ThreadUtils.waitOutOfUI(100);
        // ---------
    }

    public AbstractQuickfixTest() {
        super();
    }
}