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
package org.eclipse.wst.sse.sieditor.test.model.commands.common;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.CompositeTextOperationWrapper;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.IModelReconciler;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public class CompositeTextOperationWrapperTest {

    private CompositeTextOperationWrapperExpose compositeOperationWrapper;
    private IModelReconciler modelReconciler;

    @Test
    public void reconcileOnUndo() throws Exception {
        final IModelRoot modelRoot = setUpModelRoot();
        final AbstractNotificationOperation operationMock = createWrapperOperation(modelRoot);

        // calledInPostUndoRedo[0] - basicPatchEmfModel
        // calledInPostUndoRedo[1] - fullPatchEmfModel
        // calledInPostUndoRedo[2] - doReconcile
        final boolean calledInPostUndoRedo[] = { false, false, false };
        // calledPreAndPostOfUndoRedo[0] - preUndoRedoOfCompositeCommand
        // calledPreAndPostOfUndoRedo[1] - postUndoRedoOfCompositeCommand
        final boolean calledPreAndPostOfUndoRedoCompositeCommand[] = { false, false };
        compositeOperationWrapper = new CompositeTextOperationWrapperExpose(operationMock) {
            @Override
            public void preUndoRedoOfCompositeCommand() {
                assertFalse("postUndoRedoOfCompositeCommand was called before preUndoRedoOfCompositeCommand",
                        calledPreAndPostOfUndoRedoCompositeCommand[1]);
                calledPreAndPostOfUndoRedoCompositeCommand[0] = true;
            }

            @Override
            protected void postUndoRedoOfCompositeCommand() {
                assertTrue("postUndoRedoOfCompositeCommand was called after preUndoRedoOfCompositeCommand",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                calledPreAndPostOfUndoRedoCompositeCommand[1] = true;
                super.postUndoRedoOfCompositeCommand();
            }

            @Override
            protected void fullPatchEmfModel(final IModelRoot modelRoot) {
                assertTrue("preUndoRedoOfCompositeCommand was not called before fullPatchEmfModel",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                calledInPostUndoRedo[1] = true;
                assertFalse("doReconcile was not called before fullPatchEmfModel", calledInPostUndoRedo[2]);
                assertSame("operation model root should be passed for patching", modelRoot, operationMock.getModelRoot());
            }

            @Override
            public void doReconcile() {
                assertTrue("preUndoRedoOfCompositeCommand was not called before doReconcile",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                assertTrue("fullPatchEmfModel was not called before doReconcile", calledInPostUndoRedo[1]);
                calledInPostUndoRedo[2] = true;
            }
        };
        assertEquals(Boolean.TRUE, compositeOperationWrapper.getOptions().get(Transaction.OPTION_NO_VALIDATION));
        compositeOperationWrapper.doUndo(null, null);

        assertTrue("preUndoRedoOfCompositeCommand was not called", calledPreAndPostOfUndoRedoCompositeCommand[0]);
        assertTrue("fullPatchEmfModel was not called", calledInPostUndoRedo[1]);
        assertTrue("postUndoRedoOfCompositeCommand was not called", calledPreAndPostOfUndoRedoCompositeCommand[1]);
    }

    @Test
    public void reconcileOnRedo() throws Exception {
        final IModelRoot modelRoot = setUpModelRoot();
        final AbstractNotificationOperation operationMock = createWrapperOperation(modelRoot);

        // calledInPostUndoRedo[0] - basicPatchEmfModel
        // calledInPostUndoRedo[1] - fullPatchEmfModel
        // calledInPostUndoRedo[2] - doReconcile
        final boolean calledInPostUndoRedo[] = { false, false, false };
        // calledPreAndPostOfUndoRedo[0] - preUndoRedoOfCompositeCommand
        // calledPreAndPostOfUndoRedo[1] - postUndoRedoOfCompositeCommand
        final boolean calledPreAndPostOfUndoRedoCompositeCommand[] = { false, false };
        compositeOperationWrapper = new CompositeTextOperationWrapperExpose(operationMock) {
            @Override
            public void preUndoRedoOfCompositeCommand() {
                assertFalse("postUndoRedoOfCompositeCommand was called before preUndoRedoOfCompositeCommand",
                        calledPreAndPostOfUndoRedoCompositeCommand[1]);
                calledPreAndPostOfUndoRedoCompositeCommand[0] = true;
            }

            @Override
            protected void postUndoRedoOfCompositeCommand() {
                assertTrue("postUndoRedoOfCompositeCommand was called after preUndoRedoOfCompositeCommand",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                calledPreAndPostOfUndoRedoCompositeCommand[1] = true;
                super.postUndoRedoOfCompositeCommand();
            }

            @Override
            protected void fullPatchEmfModel(final IModelRoot modelRoot) {
                assertTrue("preUndoRedoOfCompositeCommand was not called before fullPatchEmfModel",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                calledInPostUndoRedo[1] = true;
                assertFalse("doReconcile was not called before fullPatchEmfModel", calledInPostUndoRedo[2]);
                assertSame("operation model root should be passed for patching", modelRoot, operationMock.getModelRoot());
            }

            @Override
            public void doReconcile() {
                assertTrue("preUndoRedoOfCompositeCommand was not called before doReconcile",
                        calledPreAndPostOfUndoRedoCompositeCommand[0]);
                assertTrue("fullPatchEmfModel was not called before doReconcile", calledInPostUndoRedo[1]);
                calledInPostUndoRedo[2] = true;
            }

        };
        assertEquals(Boolean.TRUE, compositeOperationWrapper.getOptions().get(Transaction.OPTION_NO_VALIDATION));
        compositeOperationWrapper.doRedo(null, null);

        assertTrue("preUndoRedoOfCompositeCommand was not called", calledPreAndPostOfUndoRedoCompositeCommand[0]);
        assertTrue("fullPatchEmfModel was not called", calledInPostUndoRedo[1]);
        assertTrue("postUndoRedoOfCompositeCommand was not called", calledPreAndPostOfUndoRedoCompositeCommand[1]);
    }

    @Test
    public void reconcileOnExecute() throws Exception {
        final IModelRoot modelRoot = createMock(IModelRoot.class);
        final IEnvironment environment = createMock(IEnvironment.class);

        expect(modelRoot.getEnv()).andReturn(environment).anyTimes();
        expect(environment.getEditingDomain()).andReturn(null).anyTimes();
        environment.finalizeWrapperCommandExecution();
        expect(environment.getCompositeTextOperationWrapper()).andReturn(null);
        expect(modelRoot.getModelObject()).andReturn(null).anyTimes();
        replay(modelRoot, environment);

        final AbstractNotificationOperation operationMock = createWrapperOperation(modelRoot);

        final boolean called[] = { false, false, false };
        compositeOperationWrapper = new CompositeTextOperationWrapperExpose(operationMock) {
            @Override
            public void preUndoRedoOfCompositeCommand() {
                assertFalse("basicPatchModel was called before preUndoRedoOfCompositeCommand", called[1]);
                assertFalse("postUndoRedoOfCompositeCommand was called before preUndoRedoOfCompositeCommand", called[2]);
                called[0] = true;
            }

            @Override
            protected void basicPatchEmfModel(final IModelRoot modelRoot) {
                assertTrue("preUndoRedoOfCompositeCommand was called before basicPatchModel", called[0]);
                assertFalse("postUndoRedoOfCompositeCommand was called before basicPatchModel", called[2]);
                called[1] = true;
            }

            @Override
            public void doReconcile() {
                assertTrue("postUndoRedoOfCompositeCommand was called before preUndoRedoOfCompositeCommand", called[0]);
                assertTrue("postUndoRedoOfCompositeCommand was called before basicPatchModel", called[1]);
                called[2] = true;
            }
        };

        compositeOperationWrapper.doExecute(null, null);
        assertEquals(Boolean.TRUE, compositeOperationWrapper.getOptions().get(Transaction.OPTION_NO_VALIDATION));

        assertTrue("preUndoRedoOfCompositeCommand was not called", called[0]);
        assertTrue("basicPatchModel was not called", called[1]);
        assertTrue("postUndoRedoOfCompositeCommand was not called", called[2]);

        verify(environment);
    }

    @Test
    public void postUndoRedoOfCompositeCommand() {
        final IModelReconcileRegistry modelReconcileRegistry = createNiceMock(IModelReconcileRegistry.class);
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final XMLModelNotifier modelNotifier = createMock(XMLModelNotifier.class);

        final IEnvironment environment = createMock(IEnvironment.class);

        expect(modelRoot.getEnv()).andReturn(environment).anyTimes();
        expect(environment.getEditingDomain()).andReturn(null).anyTimes();
        expect(modelRoot.getModelObject()).andReturn(null).anyTimes();

        modelReconciler = createMock(IModelReconciler.class);

        expect(modelReconciler.needsToReconcileModel(modelReconcileRegistry)).andReturn(true);
        modelReconciler.reconcileModel(modelRoot, modelReconcileRegistry);
        modelReconciler.modelReconciled(modelReconcileRegistry,modelNotifier);
        modelNotifier.endChanging();

        replay(modelReconciler, modelNotifier, modelRoot, environment);

        final AbstractNotificationOperation operationMock = createWrapperOperation(modelRoot);

        compositeOperationWrapper = new CompositeTextOperationWrapperExpose(operationMock) {

            @Override
            protected IModelReconciler modelReconciler() {
                return modelReconciler;
            }

            @Override
            protected IModelReconcileRegistry getModelReconcileRegistry() {
                return modelReconcileRegistry;
            }

            @Override
            protected IModelRoot getDocumentModelRoot() {
                return modelRoot;
            }

            @Override
            protected XMLModelNotifier modelNotifier() {
                return modelNotifier;
            }

            @Override
            protected void basicPatchEmfModel(final IModelRoot modelRoot) {
                // TODO Auto-generated method stub
            }

        };
        compositeOperationWrapper.doReconcile();
        assertEquals(Boolean.TRUE, compositeOperationWrapper.getOptions().get(Transaction.OPTION_NO_VALIDATION));

        verify(modelReconciler, modelNotifier);
    }

    // =========================================================
    // helpers
    // =========================================================

    private IModelRoot setUpModelRoot() {
        final IWsdlModelRoot modelRoot = createMock(IWsdlModelRoot.class);
        final IEnvironment environment = createMock(IEnvironment.class);
        final IDescription description = createMock(IDescription.class);

        final IModelRoot documentModelRoot = createMock(IModelRoot.class);

        expect(modelRoot.getEnv()).andReturn(environment).anyTimes();
        expect(modelRoot.getRoot()).andReturn(documentModelRoot);
        expect(environment.getEditingDomain()).andReturn(null).anyTimes();
        expect(modelRoot.getModelObject()).andReturn(description).anyTimes();
        replay(modelRoot, environment);
        return modelRoot;
    }

    private AbstractNotificationOperation createWrapperOperation(final IModelRoot modelRoot) {
        final AbstractNotificationOperation operationMock = new AbstractNotificationOperation(modelRoot, modelRoot
                .getModelObject(), "label") {
            @Override
            public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                return null;
            }

            @Override
            public IModelRoot getModelRoot() {
                return modelRoot;
            }

            @Override
            protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                return null;
            }

        };
        return operationMock;
    }

    // =========================================================
    // expose
    // =========================================================

    private class CompositeTextOperationWrapperExpose extends CompositeTextOperationWrapper {

        public CompositeTextOperationWrapperExpose(final AbstractNotificationOperation operation) {
            super(operation);
        }

        @Override
        public IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            return super.doExecute(monitor, info);
        }

        @Override
        protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            return super.doUndo(monitor, info);
        }

        @Override
        protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            return super.doRedo(monitor, info);
        }

        @Override
        public void doReconcile() {
            super.doReconcile();
        }

        @Override
        public void preUndoRedoOfCompositeCommand() {
            super.preUndoRedoOfCompositeCommand();
        }

    }

}
