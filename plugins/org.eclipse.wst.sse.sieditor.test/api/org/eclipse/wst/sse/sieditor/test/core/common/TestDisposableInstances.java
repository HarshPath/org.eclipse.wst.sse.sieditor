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
package org.eclipse.wst.sse.sieditor.test.core.common;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AnnotationsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AttributesReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.ElementsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.TransactionalWSDLModelStateListener;
import org.eclipse.wst.sse.sieditor.model.utils.CommandStackWrapper;
import org.eclipse.wst.sse.sieditor.model.utils.EnvironmentFactory;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestDisposableInstances extends SIEditorBaseTest {
	
	private final static Class EXPECTED_DISPOSABLES_IN_REGISTRY[] = {
		AnnotationsReconcileAdapter.class,
		AttributesReconcileAdapter.class,
		ElementsReconcileAdapter.class,
		CommandStackWrapper.class,
		TransactionalWSDLModelStateListener.class,
		ValidationService.class,
		XMLModelNotifierWrapper.class};

	@Test
	public void testDisposedAfterEditorIsClosed() throws PartInitException, IOException, CoreException {
		IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) getModelRoot("pub/simple/NewWSDLFile.wsdl", "NewWSDLFile.wsdl", ServiceInterfaceEditor.EDITOR_ID);
		EnvironmentFactory.EnvironmentImpl env = (EnvironmentFactory.EnvironmentImpl)wsdlModelRoot.getEnv();
		
		CommandStack currentCommandStack = editor.getStructuredModel().getUndoManager().getCommandStack();
		assertEquals(CommandStackWrapper.class, currentCommandStack.getClass());
		
		// All disposables should exist in the registry
		boolean disposableInstanceFound = false;
		for(Class expectedDisposable : EXPECTED_DISPOSABLES_IN_REGISTRY) {
			disposableInstanceFound = false;
			for(IDisposable disposable : env.getRegistryOfDisposables()) {
				if(expectedDisposable.equals(disposable.getClass())) {
					disposableInstanceFound = true;
					break;
				}
			}
			assertTrue("Missing disposable instance from env. registry. Missing instance of class:" + expectedDisposable, 
					disposableInstanceFound);
		}
		
		// Register a test disposable to ensure its method is called
		final boolean disposeCalled[] = {false};
		env.addDisposable(new IDisposable() {
			
			@Override
			public void doDispose() {
				disposeCalled[0] = true;
			}
		});
		
		// get undo manager before closing the editor
		IStructuredModel structuredModel = editor.getStructuredModel();
		IStructuredTextUndoManager undoManager = structuredModel.getUndoManager();
		
		// close the editor - here all disposables should be disposed
		editor.close(false);
		ThreadUtils.waitOutOfUI(10);
		
		assertTrue("Closing editor does not dispose instances in environment registry of disposables.",
				disposeCalled[0]);
		
		currentCommandStack = undoManager.getCommandStack();
		assertEquals(BasicCommandStack.class, currentCommandStack.getClass());
	}
}
