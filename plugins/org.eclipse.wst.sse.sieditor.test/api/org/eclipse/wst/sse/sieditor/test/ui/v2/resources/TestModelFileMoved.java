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
package org.eclipse.wst.sse.sieditor.test.ui.v2.resources;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StandaloneDtEditorPage;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class TestModelFileMoved extends SIEditorBaseTest{
	
	private AbstractEditorWithSourcePage editor;
	
    @After
    public void tearDown() throws Exception {
        if (editor != null) {
            editor.close(false);
        }
        super.tearDown();
        // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);

    }

    @SuppressWarnings("unchecked")
	@Test
	public void testStandaloneDTEditorNamespaceChanged() throws Exception {
		final IFile file = ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd", Document_FOLDER_NAME, this.getProject(),
				"example.xsd");
		refreshProjectNFile(file);

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		FileEditorInput eInput = new FileEditorInput(file);
		IWorkbenchPage workbenchActivePage = window.getActivePage();

		editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);
		assertFalse(editor.isDirty());

		final IXSDModelRoot modelRoot = (IXSDModelRoot) editor.getModelRoot();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					IFile newFile = file.getParent().getFile(new Path("example_moved.xsd"));
					file.move(newFile.getFullPath(), true, null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		ModalContext.run(runnable, true, new NullProgressMonitor(), PlatformUI.getWorkbench().getDisplay());

		IXSDModelRoot newModelRoot = (IXSDModelRoot) editor.getModelRoot();
		assertNotSame(newModelRoot, modelRoot);

		StandaloneDtEditorPage page = null;
		List pages = editor.getPages();
		for (int ndx = 0; ndx < pages.size(); ndx++) {
			if (pages.get(ndx) instanceof StandaloneDtEditorPage) {
				page = (StandaloneDtEditorPage) pages.get(ndx);
				break;
			}
		}

		assertNotNull(page);

		Field nsField = StandaloneDtEditorPage.class.getDeclaredField("namespaceTextControl");
		nsField.setAccessible(true);

		Text ns = (Text) nsField.get(page);

		ns.setText("newtargetnamespace");
		assertTrue(page.isDirty());
		
		Listener[] listeners = ns.getListeners(SWT.FocusOut);
		Event event = new Event();
		event.type = SWT.FocusOut;
		event.widget = ns;
		for (Listener listener : listeners) {
			listener.handleEvent(event);
		}

		assertEquals("newtargetnamespace", newModelRoot.getSchema().getNamespace());
	}

}
