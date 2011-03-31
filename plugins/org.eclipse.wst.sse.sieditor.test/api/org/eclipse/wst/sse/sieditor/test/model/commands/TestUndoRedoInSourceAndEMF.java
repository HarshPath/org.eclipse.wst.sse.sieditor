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
package org.eclipse.wst.sse.sieditor.test.model.commands;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.undo.StructuredTextCommandImpl;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.test.util.ThreadUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.CompositeTextOperationWrapper;
import org.eclipse.wst.sse.sieditor.core.common.ICommandStackListener;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class TestUndoRedoInSourceAndEMF extends SIEditorBaseTest {

    AbstractEditorWithSourcePage editor = null;

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
     // Flush threads that wait UI thread for execution
        ThreadUtils.waitOutOfUI(100);
    }

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
    public void testDTESourceTextCommandsAreWrappedInSIECommands() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix/po.xsd", Document_FOLDER_NAME, this.getProject(),
                "po.xsd");
        refreshProjectNFile(file);

        final FileEditorInput eInput = new FileEditorInput(file);
        
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IWorkbenchPage workbenchActivePage = window.getActivePage();

        editor = (DataTypesEditor) workbenchActivePage.openEditor(eInput, DataTypesEditor.EDITOR_ID);

        final IXSDModelRoot modelRoot = (IXSDModelRoot) editor.getModelRoot();

        final IEnvironment env = modelRoot.getEnv();
        final CommandStack commandStack = env.getEditingDomain().getCommandStack();
        
        assertTrue(commandStack instanceof WorkspaceCommandStackImpl);
        assertEquals(((WorkspaceCommandStackImpl)commandStack).getDefaultUndoContext(), env.getUndoContext());

        testExecuteOnCommandStack(env, (WorkspaceCommandStackImpl)commandStack);
        
        
    }

    private void testExecuteOnCommandStack(final IEnvironment env, final WorkspaceCommandStackImpl commandStack) {
        final boolean listenerCalled[] = {false};
        final ICommandStackListener testListener = new ICommandStackListener() {

            public void commandToBeExecuted(final Command command) {
                listenerCalled[0] = true;
            }
        };
        env.addCommandStackListener(testListener);

        final boolean operationHistoryListener_called[] = {false};
        final IOperationHistoryListener operationHistoryListener = new IOperationHistoryListener() {
            @Override
            public void historyNotification(final OperationHistoryEvent event) {
                operationHistoryListener_called[0] = true;
                assertTrue(event.getOperation() instanceof CompositeTextOperationWrapper); 
            }
        };
        commandStack.getOperationHistory().addOperationHistoryListener(operationHistoryListener);
        
        // Assert that command stack intercept StructuredTextCommandImpl commands
        final StructuredTextCommandImpl commandToIntercept = new StructuredTextCommandImpl() {};
        commandStack.execute(commandToIntercept);
        
        assertTrue(listenerCalled[0]);
        assertTrue(operationHistoryListener_called[0]);
        
        // Assert that command stack does NOT intercept other commands
        listenerCalled[0] = false;
        commandStack.getOperationHistory().removeOperationHistoryListener(operationHistoryListener);
        
        final boolean commandNotToIntercept_called[] = {false};
        final AbstractCommand commandNotToIntercept = new AbstractCommand() {
            
            @Override
            public void redo() {
            }
            
            @Override
            public void execute() {
                commandNotToIntercept_called[0] = true;                
            }

            @Override
            public boolean canExecute() {
                return true;
            }
        };

        commandStack.execute(commandNotToIntercept);
        
        assertFalse(listenerCalled[0]);
        assertTrue(commandNotToIntercept_called[0]);
    }



}
