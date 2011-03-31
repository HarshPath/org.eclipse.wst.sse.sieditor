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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.TypePropertyEditorTest;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.eclipse.wst.sse.sieditor.command.emf.common.BaseNewTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewElementTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewSimpleTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewStructureTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetNewTypeTest {

    final IModelObject modelObject = createMock(IModelObject.class);
    final IModelRoot modelRoot = createMock(IModelRoot.class);
    ISchema schema;
    ITreeNode treeNode;

    @Before
    public void setUp() {

        final IEnvironment env = createMock(IEnvironment.class);
        schema = createNiceMock(ISchema.class);
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelRoot.getEnv()).andReturn(env).anyTimes();
        expect(env.getEditingDomain()).andReturn(null);
        // expect(env.execute(isA(NewElementTypeCompositeCommand.class))).andReturn(null);
        treeNode = createMock(ITreeNode.class);
        replay(schema, modelRoot, modelObject/* , env */);
    }

    @After
    public void clear() {

        EasyMock.reset(modelObject, modelRoot, schema);

    }

    @Test
    public void newElementType() throws ExecutionException {
        final ISetTypeCommandBuilder commandBuilder = createMock(ISetTypeCommandBuilder.class);
        final boolean[] executed = { false, false, false };

        final AbstractFormPageController testFormPageController = new AbstractFormPageController(modelRoot, true) {
            @Override
            protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(
                    final org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor propertyEditor) {
                return commandBuilder;
            }

            @Override
            protected String getEditorID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected IModelObject getModelObject() {
                return modelObject;
            }

            @Override
            protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return null;
            }

            @Override
            public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            }

            @Override
            protected void executeNewTypeCommand(BaseNewTypeCompositeCommand command, final String typeName) {
                assertTrue(command instanceof NewElementTypeCompositeCommand);
                executed[0] = true;
            };

            @Override
            protected void fireTreeNodeSelectionEvent(IModelObject modelObject) {
                executed[2] = true;
            };

            @Override
            protected void fireTreeNodeExpandEvent(IModelObject modelObject) {
                executed[1] = true;
            }

        };
        TypePropertyEditor property = new TypePropertyEditor(testFormPageController, null) {

            @Override
            public ITypeCommitter getTypeCommitter() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected IType getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ITypeDialogStrategy createNewTypeDialogStrategy() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        property.setInput(treeNode);
        testFormPageController.newElementType("", schema, property); //$NON-NLS-1$
        assertTrue(executed[0]);
        assertTrue(executed[1]);
        assertTrue(executed[2]);
    }

    @Test
    public void newStructureType() {
        final ISetTypeCommandBuilder commandBuilder = createMock(ISetTypeCommandBuilder.class);
        final boolean[] executed = { false, false, false };

        final AbstractFormPageController testFormPageController = new AbstractFormPageController(modelRoot, true) {
            @Override
            protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(
                    final org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor propertyEditor) {
                return commandBuilder;
            }

            @Override
            protected String getEditorID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected IModelObject getModelObject() {
                return modelObject;
            }

            @Override
            protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return null;
            }

            @Override
            public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            }

            @Override
            protected void fireTreeNodeExpandEvent(IModelObject modelObject) {
                executed[1] = true;
            }

            @Override
            protected void fireTreeNodeSelectionEvent(IModelObject modelObject) {
                executed[2] = true;
            };

            @Override
            protected void executeNewTypeCommand(BaseNewTypeCompositeCommand command, final String typeName) {
                assertTrue(command instanceof NewStructureTypeCompositeCommand);
                executed[0] = true;
            }

        };
        TypePropertyEditor property = new TypePropertyEditor(testFormPageController, null) {

            @Override
            public ITypeCommitter getTypeCommitter() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected IType getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ITypeDialogStrategy createNewTypeDialogStrategy() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        property.setInput(treeNode);

        testFormPageController.newStructureType("", schema, property); //$NON-NLS-1$
        // assert the command was executed
        assertTrue(executed[0]);
        assertTrue(executed[1]);
        assertTrue(executed[2]);
    }

    @Test
    public void newSimpleType() {
        final ISetTypeCommandBuilder commandBuilder = createMock(ISetTypeCommandBuilder.class);
        final boolean[] executed = { false, false, false };

        final AbstractFormPageController testFormPageController = new AbstractFormPageController(modelRoot, true) {
            @Override
            protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(
                    final org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor propertyEditor) {
                return commandBuilder;
            }

            @Override
            protected String getEditorID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected IModelObject getModelObject() {
                return modelObject;
            }

            @Override
            protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return null;
            }

            @Override
            public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            }

            @Override
            protected void fireTreeNodeExpandEvent(IModelObject modelObject) {
                executed[1] = true;
            }

            @Override
            protected void fireTreeNodeSelectionEvent(IModelObject modelObject) {
                executed[2] = true;
            };

            @Override
            protected void executeNewTypeCommand(BaseNewTypeCompositeCommand command, final String typeName) {
                assertTrue(command instanceof NewSimpleTypeCompositeCommand);
                executed[0] = true;
            }

        };
        TypePropertyEditor property = new TypePropertyEditor(testFormPageController, null) {

            @Override
            public ITypeCommitter getTypeCommitter() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected IType getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ITypeDialogStrategy createNewTypeDialogStrategy() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        property.setInput(treeNode);

        testFormPageController.newSimpleType("", schema, property); //$NON-NLS-1$

        assertTrue(executed[0]);
        assertTrue(executed[1]);
        assertTrue(executed[2]);
    }

    @Test
    public void testFireSelectionEventFromPostExecuteWhitSameModelObjects() {
        final ISetTypeCommandBuilder commandBuilder = createMock(ISetTypeCommandBuilder.class);
        final boolean[] executed = { false, false, false, true };
        final IModelObject modelObject1 = createMock(IModelObject.class);

        class AbstractFormPageControllerTest extends AbstractFormPageController {
            public AbstractFormPageControllerTest() {
                super(modelRoot, true);
            }

            @Override
            protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(
                    final org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor propertyEditor) {
                return commandBuilder;
            }

            @Override
            protected String getEditorID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected IModelObject getModelObject() {
                return modelObject;
            }

            @Override
            protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return null;
            }

            @Override
            public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            }

            @Override
            protected void fireTreeNodeExpandEvent(IModelObject modelObject) {
                executed[1] = true;
            }

            @Override
            protected void fireTreeNodeSelectionEvent(IModelObject modelObject) {
                assertSame(modelObject1, modelObject);
                executed[0] = true;
            };

            @Override
            protected void fireTreeNodeSelectionEvent(ITreeNode treeNode) {
                executed[3] = false;
            }

            @Override
            protected void fireTreeNodeExpandEvent(ITreeNode treeNode) {

            }

            @Override
            protected void postExecuteNewTypeCommand(IModelObject modelObject, IModelObject sourceModelObject) {
                super.postExecuteNewTypeCommand(modelObject, sourceModelObject);
                executed[2] = true;
            };

            @Override
            protected void executeNewTypeCommand(BaseNewTypeCompositeCommand command, final String typeName) {

            }

        }

        AbstractFormPageControllerTest pageControler = new AbstractFormPageControllerTest();

        expect(modelObject1.getModelRoot()).andReturn(modelRoot).anyTimes();
        replay(modelObject1);

        pageControler.postExecuteNewTypeCommand(modelObject1, modelObject1);

        // assert the command was executed .newStructureType("", schema, null);
        assertTrue(executed[0]);
        assertTrue(executed[1]);
        assertTrue(executed[2]);
        assertTrue(executed[3]);
    }

    @Test
    public void testFireSelectionEventFromPostExecuteWithDifferentModelObjects() {
        final ISetTypeCommandBuilder commandBuilder = createMock(ISetTypeCommandBuilder.class);
        final boolean[] executed = { false, false };

        IModelObject modelObject1 = createMock(IModelObject.class);
        expect(modelObject1.getModelRoot()).andReturn(modelRoot).anyTimes();

        final IModelObject modelObject2 = createMock(IModelObject.class);
        IModelRoot modelRoot2 = createMock(IModelRoot.class);
        expect(modelObject2.getModelRoot()).andReturn(modelRoot2).anyTimes();

        replay(modelObject1, modelObject2);

        class AbstractFormPageControllerTest extends AbstractFormPageController {
            public AbstractFormPageControllerTest() {
                super(modelRoot, true);
            }

            @Override
            protected ISetTypeCommandBuilder createNewTypeSetTypeCommandBuilder(
                    final org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor propertyEditor) {
                return commandBuilder;
            }

            @Override
            protected String getEditorID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected IModelObject getModelObject() {
                return modelObject;
            }

            @Override
            protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return null;
            }

            @Override
            public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            }

            @Override
            protected void fireTreeNodeExpandEvent(IModelObject modelObject) {
            }

            @Override
            protected void fireTreeNodeSelectionEvent(IModelObject modelObjectForSelection) {
                executed[0] = true;
                assertSame(modelObject2, modelObjectForSelection);
                super.fireTreeNodeSelectionEvent(modelObjectForSelection);
            }

            @Override
            protected void fireTreeNodeSelectionEvent(ITreeNode treeNode) {
                executed[1] = true;
            }

            @Override
            protected void fireTreeNodeExpandEvent(ITreeNode treeNode) {

            }

            @Override
            public void postExecuteNewTypeCommand(IModelObject newModelObject, IModelObject sourceModelObject) {
                super.postExecuteNewTypeCommand(newModelObject, sourceModelObject);
            }

            @Override
            protected void executeNewTypeCommand(BaseNewTypeCompositeCommand command, final String typeName) {

            }

        }

        AbstractFormPageControllerTest pageControler = new AbstractFormPageControllerTest();

        pageControler.postExecuteNewTypeCommand(modelObject1, modelObject2);

        assertTrue(executed[0]);
        assertTrue(executed[1]);
    }
}
