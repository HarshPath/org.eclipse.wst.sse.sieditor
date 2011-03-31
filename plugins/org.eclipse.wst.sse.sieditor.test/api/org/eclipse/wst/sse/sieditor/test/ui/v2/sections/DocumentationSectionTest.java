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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.DocumentationSection;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class DocumentationSectionTest {

    private static final String SOME_DOCUMENTATION = "Some Documentation"; //$NON-NLS-1$

    private static final String OTHER_TEXT = "Other Documentation"; //$NON-NLS-1$

    private IFormPageController controller;

    private Shell shell;

    private TestDocumentationSection section;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();

        controller = createNiceMock(IFormPageController.class);
        final FormToolkit toolkit = new FormToolkit(display);
        final IManagedForm managedForm = createMock(IManagedForm.class);

        section = new TestDocumentationSection(controller, toolkit, managedForm);
        shell = new Shell(display);
        section.createContents(shell);
    }

    @After
    public void tearDown() throws Exception {
        section = null;
        shell = null;
        controller = null;
    }

    class TestDocumentationSection extends DocumentationSection {

        private int dirtyStateChangedCallCount = 0;

        public TestDocumentationSection(final IFormPageController controller, final FormToolkit toolkit,
                final IManagedForm managedForm) {
            super(controller, toolkit, managedForm);
        }

        public int getDirtyStateChangedCallCount() {
            return dirtyStateChangedCallCount;
        }

        @Override
        public ITreeNode getNode() {
            return super.getNode();
        }

        @Override
        protected void dirtyStateChanged() {
            dirtyStateChangedCallCount++;
        }
    };

    @Test
    public void testCreateContents() {
        final Section sectionControl = (Section) shell.getChildren()[0];
        assertEquals(Messages.DocumentationSection_section_title, sectionControl.getText());
        assertTrue(sectionControl.isExpanded());

        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        assertEquals(clientComposite, sectionControl.getClient());

        assertEquals(GridLayout.class, clientComposite.getLayout().getClass());
        assertEquals(1, ((GridLayout) clientComposite.getLayout()).numColumns);

        final Control[] children = clientComposite.getChildren();
        assertEquals(1, children.length);
        assertEquals(Text.class, children[0].getClass());

        final Text text = (Text) children[0];
        assertEquals(GridData.class, text.getLayoutData().getClass());
        final GridData gridData = (GridData) text.getLayoutData();
        assertEquals(100, gridData.minimumHeight);
        assertEquals(SWT.FILL, gridData.horizontalAlignment);
        assertEquals(SWT.FILL, gridData.verticalAlignment);
        assertTrue(gridData.grabExcessHorizontalSpace);
        assertTrue(gridData.grabExcessVerticalSpace);
    }

    @Test
    public void testModifyListener() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[0];
        final Listener[] listeners = text.getListeners(SWT.Modify);
        assertEquals(1, listeners.length);

        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(controller);
        final IModelObject modelObject = selectionChangedMockCall(false);
        text.setText(SOME_DOCUMENTATION);
        assertFalse(section.isDirty());
        assertEquals(0, section.getDirtyStateChangedCallCount());

        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());

        text.setText(SOME_DOCUMENTATION);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());
        verify(modelObject, controller);
    }

    @Test
    public void testFocusListener() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[0];
        Listener[] listeners = text.getListeners(SWT.FocusIn);
        assertTrue(listeners.length > 0);
        listeners = text.getListeners(SWT.FocusOut);
        assertTrue(listeners.length > 0);

        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);

        final IModelObject modelObject = selectionChangedMockCall(false);
        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());

        reset(controller);
        final ITreeNode node = section.getNode();
        reset(controller);
        controller.editDocumentation(node, OTHER_TEXT);
        replay(controller);

        final Event event = new Event();
        event.widget = text;
        event.detail = SWT.FocusOut;
        text.notifyListeners(SWT.FocusOut, event);

        assertFalse(section.isDirty());
        assertEquals(2, section.getDirtyStateChangedCallCount());
        verify(controller, modelObject);
    }

    @Test
    public void testRefresh() {
        assertFalse(section.isDirty());
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(controller);
        final IModelObject modelObject = selectionChangedMockCall(false);
        verify(modelObject, controller);

        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[0];
        assertEquals(SOME_DOCUMENTATION, text.getText());

        assertTrue(text.getEditable());

        assertFalse(section.isDirty());
    }

    @Test
    public void testRefreshReadOnly() {
        assertFalse(section.isDirty());
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
        replay(controller);
        IModelObject modelObject = selectionChangedMockCall(false);
        verify(modelObject, controller);

        Control[] children = getClientCompositeChildren();
        Text text = (Text) children[0];
        assertEquals(SOME_DOCUMENTATION, text.getText());

        assertFalse(text.getEditable());

        assertFalse(section.isDirty());

        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        modelObject = selectionChangedMockCall(true);
        verify(modelObject, controller);

        children = getClientCompositeChildren();
        text = (Text) children[0];
        assertEquals(SOME_DOCUMENTATION, text.getText());

        assertFalse(text.getEditable());

        assertFalse(section.isDirty());
    }

    @Test
    public void testRefreshWritableElementReference() {
        assertFalse(section.isDirty());
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        final IModelObject modelObject = selectionChangedMockCall(false, ITreeNode.CATEGORY_REFERENCE);
        verify(modelObject, controller);

        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[0];
        assertEquals(SOME_DOCUMENTATION, text.getText());

        assertTrue(text.getEditable());
    }

    @Test
    public void testIsStale() {
        assertFalse(section.isStale());
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(controller);
        final IModelObject modelObject = selectionChangedMockCall(false);
        assertFalse(section.isStale());
        verify(modelObject, controller);

        reset(modelObject);
        EasymockModelUtils.addComponentAndContainerCalls(modelObject, EObject.class);
        expect(modelObject.getDocumentation()).andReturn(OTHER_TEXT).times(1);
        replay(modelObject);
        assertTrue(section.isStale());
        verify(modelObject);
    }

    private IModelObject selectionChangedMockCall(final boolean readOnlyNode) {
        return selectionChangedMockCall(readOnlyNode, 0);
    }

    private IModelObject selectionChangedMockCall(final boolean readOnlyNode, final int nodeCategories) {
        final IFormPart formPart = createMock(IFormPart.class);
        final IModelObject modelObject = createMock(IModelObject.class);
        expect(modelObject.getDocumentation()).andReturn(SOME_DOCUMENTATION).anyTimes();

        final EObject container = createMock(EObject.class);
        expect(container.eContainer()).andReturn(container).anyTimes();
        replay(container);
        final EObject component = createMock(EObject.class);
        expect(component.eContainer()).andReturn(container).anyTimes();
        replay(component);

        expect(modelObject.getComponent()).andReturn(component).anyTimes();
        replay(modelObject);

        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(modelObject).anyTimes();
        expect(treeNode.isReadOnly()).andReturn(readOnlyNode).anyTimes();
        expect(treeNode.getCategories()).andStubReturn(nodeCategories);
        replay(treeNode);

        final IProblemDecorator problemDecorator = createMock(IProblemDecorator.class);
        section.setProblemDecorator(problemDecorator);
        problemDecorator.setModelObject(modelObject);
        // problemDecorator.updateDecorations();
        replay(problemDecorator);

        final StructuredSelection selection = new StructuredSelection(treeNode);
        section.selectionChanged(formPart, selection);

        verify(problemDecorator);

        return modelObject;
    }

    private Control[] getClientCompositeChildren() {
        final Section sectionControl = (Section) shell.getChildren()[0];
        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        final Control[] children = clientComposite.getChildren();
        return children;
    }
}
