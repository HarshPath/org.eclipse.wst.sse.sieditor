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
package org.eclipse.wst.sse.sieditor.test.model.factory;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.factory.DataTypesModelRootFactory;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AnnotationsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AttributesReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.ElementsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class DataTypesModelRootFactoryTest extends AbstractModelRootFactoryTest {

    DataTypesModelRootFactoryExpose dataTypesModelRootFactory;

    @BeforeClass
    public static void setUpBefore() {
        StatusUtils.isUnderJunitExecution = true;
    }

    @AfterClass
    public static void tearDownAfter() {
        StatusUtils.isUnderJunitExecution = false;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dataTypesModelRootFactory = new DataTypesModelRootFactoryExpose();

        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
                "example.xsd");
        refreshProjectNFile(file);

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        eInput = new FileEditorInput(file);
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        dataTypesModelRootFactory.resetOriginalInstance();
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);
    }

    @Test
    public void testCreateCustomReconcileAdapters() throws Throwable {
        final List<IModelStateListener> customModelStateListeners = dataTypesModelRootFactory.customModelStateListeners;

        assertEquals(3, customModelStateListeners.size());

        assertEquals("AttributesReconcileAdapter is expected at " + 0
                + " position (position is considered) in customModelStateListeners.", AttributesReconcileAdapter.class,
                customModelStateListeners.get(0).getClass());
        assertEquals("ElementsReconcileAdapter is expected at " + 1
                + " position (position is considered) in customModelStateListeners.", ElementsReconcileAdapter.class,
                customModelStateListeners.get(1).getClass());
        assertEquals("AnnotationsReconcileAdapter is expected at " + 2
                + " position (position is considered) in customModelStateListeners.", AnnotationsReconcileAdapter.class,
                customModelStateListeners.get(2).getClass());
    }

    private class DataTypesModelRootFactoryExpose extends DataTypesModelRootFactory {

        private final DataTypesModelRootFactory originalInstance;

        public List<IModelStateListener> customModelStateListeners;

        public DataTypesModelRootFactoryExpose() {
            super();
            originalInstance = INSTANCE;
            INSTANCE = this;
        }

        public void resetOriginalInstance() {
            INSTANCE = originalInstance;
        }

        @Override
        protected List<IModelStateListener> getCustomModelStateListeners(final IModelRoot modelRoot) {
            customModelStateListeners = super.getCustomModelStateListeners(modelRoot);
            return customModelStateListeners;
        }

    }
}
