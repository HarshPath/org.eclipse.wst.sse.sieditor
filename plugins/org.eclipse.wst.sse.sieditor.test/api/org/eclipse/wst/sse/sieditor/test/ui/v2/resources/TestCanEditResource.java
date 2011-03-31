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

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.HiddenErrorStatus;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.preedit.EditValidator;

public class TestCanEditResource extends SIEditorBaseTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        StatusUtils.isUnderJunitExecution = true;
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        StatusUtils.isUnderJunitExecution = false;

        ResourceUtils.resetInstance();
    }

    @Test
    public void testAbstractNotificationOperationCallsCanEditFromIEditValidator() throws Exception {
        final IFile file = org.eclipse.wst.sse.sieditor.test.util.ResourceUtils.copyFileIntoTestProject("pub/xsd/example.xsd",
                Document_FOLDER_NAME, this.getProject(), "example.xsd");

        refreshProjectNFile(file);

        final IXSDModelRoot modelRoot = this.getXSDModelRoot(file);

        final IWorkspace mockWorkspace = createMock(IWorkspace.class);
        expect(mockWorkspace.validateEdit(aryEq(new IFile[] { file }), eq(IWorkspace.VALIDATE_PROMPT))).andReturn(
                Status.OK_STATUS);
        expect(mockWorkspace.isTreeLocked()).andReturn(false).anyTimes();
        replay(mockWorkspace);

        final AbstractEditorWithSourcePageTest editor = new AbstractEditorWithSourcePageTest(modelRoot, true);

        final EditValidatorTest validator = new EditValidatorTest(editor, mockWorkspace);

        final AbstractNotificationOperationForTest anyCommand = new AbstractNotificationOperationForTest(modelRoot, modelRoot
                .getSchema(), "Test command");

        modelRoot.getEnv().setEditValidator(validator);

        modelRoot.getEnv().execute(anyCommand);

        assertTrue(anyCommand.run);

        verify(mockWorkspace);
    }

    @Test
    public void testCanEditOnReadOnlyResourceWithErrorStatus() throws Exception {
        final IFile file = createMock(IFile.class);
        expect(file.isReadOnly()).andReturn(true);
        replay(file);

        final IWorkspace mockWorkspace = createMock(IWorkspace.class);
        expect(mockWorkspace.validateEdit(aryEq(new IFile[] { file }), eq(IWorkspace.VALIDATE_PROMPT))).andReturn(
                Status.OK_STATUS);
        replay(mockWorkspace);

        final AbstractEditorWithSourcePageTest editor = new AbstractEditorWithSourcePageTest(true);
        final EditValidatorTest validator = new EditValidatorTest(null, mockWorkspace);

        assertEquals(HiddenErrorStatus.class, validator.canEditTest(file).getClass());

        verify(mockWorkspace);
        verify(file);
    }

    @Test
    public void testCanEditDirivedResourceAssertThatCanNot() throws Exception {
        final IFile file = createMock(IFile.class);
        expect(file.isReadOnly()).andReturn(false);
        replay(file);

        final IWorkspace mockWorkspace = createMock(IWorkspace.class);
        expect(mockWorkspace.validateEdit(aryEq(new IFile[] { file }), eq(IWorkspace.VALIDATE_PROMPT))).andReturn(
                Status.OK_STATUS);
        expect(mockWorkspace.isTreeLocked()).andReturn(false).anyTimes();
        replay(mockWorkspace);

        final AbstractEditorWithSourcePageTest editor = new AbstractEditorWithSourcePageTest(false);
        final EditValidatorTest validator = new EditValidatorTest(editor, mockWorkspace);

        assertEquals(HiddenErrorStatus.class, validator.canEditTest(file).getClass());

        verify(mockWorkspace);
        verify(file);
    }

    @Test
    public void testCanEditDirivedResourceAssertThatCan() throws Exception {
        final IFile file = createMock(IFile.class);
        expect(file.isReadOnly()).andReturn(false);
        replay(file);

        final IWorkspace mockWorkspace = createMock(IWorkspace.class);
        expect(mockWorkspace.validateEdit(aryEq(new IFile[] { file }), eq(IWorkspace.VALIDATE_PROMPT))).andReturn(
                Status.OK_STATUS);
        expect(mockWorkspace.isTreeLocked()).andReturn(false).anyTimes();
        replay(mockWorkspace);

        final AbstractEditorWithSourcePageTest editor = new AbstractEditorWithSourcePageTest(true);
        final EditValidatorTest validator = new EditValidatorTest(editor, mockWorkspace);

        assertEquals(Status.OK_STATUS.getClass(), validator.canEditTest(file).getClass());

        verify(mockWorkspace);
        verify(file);
    }

    @Test
    public void testCanEditDirivedResourceAssertThatCanWithWorkspaceTreeLocked() throws Exception {
        final IFile file = createMock(IFile.class);
        expect(file.isReadOnly()).andReturn(false);
        replay(file);

        final IWorkspace mockWorkspace = createMock(IWorkspace.class);
        expect(mockWorkspace.validateEdit(aryEq(new IFile[] { file }), eq(IWorkspace.VALIDATE_PROMPT))).andReturn(
                Status.OK_STATUS);
        expect(mockWorkspace.isTreeLocked()).andReturn(true).anyTimes();
        replay(mockWorkspace);

        final AbstractEditorWithSourcePageTest editor = new AbstractEditorWithSourcePageTest(true);
        final EditValidatorTest validator = new EditValidatorTest(editor, mockWorkspace);

        assertEquals(Status.OK_STATUS.getClass(), validator.canEditTest(file).getClass());

        verify(mockWorkspace);
        verify(file);
    }

    private class AbstractNotificationOperationForTest extends AbstractNotificationOperation {

        public AbstractNotificationOperationForTest(final IModelRoot root, final IModelObject modelObject,
                final String operationLabel) {
            super(root, modelObject, operationLabel);
        }

        public boolean run = false;

        @Override
        public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            run = true;
            return Status.OK_STATUS;
        }

    }

    private class EditValidatorTest extends EditValidator {
        private final IWorkspace mockWorkspace;

        public EditValidatorTest(final AbstractEditorWithSourcePage editor, final IWorkspace mockWorkspace) {
            super(editor);
            this.mockWorkspace = mockWorkspace;
        }

        public IStatus canEditTest(final IFile file) {
            return super.canEdit(file);
        }

        @Override
        public IWorkspace getWorkspace() {
            return mockWorkspace;
        }

    }

    private class AbstractEditorWithSourcePageTest extends AbstractEditorWithSourcePage {
        boolean resultFromValidateEditorInput;

        public AbstractEditorWithSourcePageTest(final boolean resultFromValidateEditorInput) {
            this.resultFromValidateEditorInput = resultFromValidateEditorInput;
        }

        public AbstractEditorWithSourcePageTest(final IModelRoot modelRoot, final boolean resultFromValidateEditorInput) {
            this.resultFromValidateEditorInput = resultFromValidateEditorInput;
            super.commonModel = modelRoot;
        }

        @Override
        public boolean validateEditorInputState() {
            // called
            return resultFromValidateEditorInput;
        }

        @Override
        protected void addExtraPages(final IStorageEditorInput in) throws PartInitException {
            // TODO Auto-generated method stub

        }

        @Override
        protected IModelRoot createModelRoot(final Document document) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void validate() {
            // TODO Auto-generated method stub

        }

    }
}
