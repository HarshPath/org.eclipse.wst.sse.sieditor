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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.StructureDetailsSection;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.xsd.XSDNamedComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class StructureDetailsSectionTest {

    private static final String OTHER_TEXT = "other"; //$NON-NLS-1$

    private static final String SOME_NAME = "someName"; //$NON-NLS-1$

    private Image errorImage;

    private TestStructureDetailsSection section;

    private Shell shell;

    private IDataTypesFormPageController controller;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        errorImage = new Image(display, 10, 10);

        controller = EasyMock.createNiceMock(IDataTypesFormPageController.class);
        final FormToolkit toolkit = new FormToolkit(display);
        final IManagedForm managedForm = createMock(IManagedForm.class);

        section = new TestStructureDetailsSection(controller, toolkit, managedForm);
        shell = new Shell(display);
        section.createContents(shell);
    }

    class TestStructureDetailsSection extends StructureDetailsSection {

        private int dirtyStateChangedCallCount = 0;

        public TestStructureDetailsSection(final IFormPageController controller, final FormToolkit toolkit,
                final IManagedForm managedForm) {
            super(controller, toolkit, managedForm);
        }

        public int getDirtyStateChangedCallCount() {
            return dirtyStateChangedCallCount;
        }

        @Override
        protected ISharedImages getSharedImages() {
            final ISharedImages sharedImages = createMock(ISharedImages.class);
            expect(sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK)).andReturn(errorImage).once();
            replay(sharedImages);
            return sharedImages;
        }

        protected void dirtyStateChanged() {
            dirtyStateChangedCallCount++;
        }
    };

    @After
    public void tearDown() throws Exception {
        errorImage = null;
        section = null;
        shell = null;
        controller = null;
    }

    @Test
    public void testCreateContents() {
        final Section sectionControl = (Section) shell.getChildren()[0];
        assertEquals(Messages.StructureDetailsSection_section_title, sectionControl.getText());

        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        assertEquals(clientComposite, sectionControl.getClient());

        final Control[] children = clientComposite.getChildren();
        assertEquals(2, children.length);

        assertEquals(Label.class, children[0].getClass());
        assertEquals(Messages.StructureDetailsSection_name_label, ((Label) children[0]).getText());

        assertEquals(Text.class, children[1].getClass());
        final Text text = (Text) children[1];
        assertEquals(UIConstants.EMPTY_STRING, text.getText());

        assertTrue(clientComposite.getListeners(SWT.Paint).length > 0);
    }

    @Test
    public void testRefresh() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        final IStructureType type = selectionChangedMockCall(false);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        final Control[] children = getClientCompositeChildren();

        final Text text = (Text) children[1];
        assertEquals(SOME_NAME, text.getText());

        assertFalse(section.isDirty());
        verify(type);
    }

    @Test
    public void testRefreshReadOnly() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
        replay(controller);
        IStructureType type = selectionChangedMockCall(false);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        Control[] children = getClientCompositeChildren();
        Text text = (Text) children[1];
        assertEquals(SOME_NAME, text.getText());

        assertFalse(text.getEditable());
        assertFalse(section.isDirty());
        verify(type);

        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        type = selectionChangedMockCall(true);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        children = getClientCompositeChildren();
        text = (Text) children[1];
        assertEquals(SOME_NAME, text.getText());

        assertFalse(text.getEditable());
        assertFalse(section.isDirty());
        verify(type);
    }

    @Test
    public void testModifyListenerOnNameText() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        setUpAndTestModifyListener(1, 4, 2, SOME_NAME);
    }

    private void setUpAndTestModifyListener(final int controlIndex, final int getNameCallTimes, final int getNamespaceCallTimes,
            final String setSameText) {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[controlIndex];
        final Listener[] listeners = text.getListeners(SWT.Modify);
        assertEquals(1, listeners.length);

        final IStructureType type = selectionChangedMockCall(false);
        // set the same text as in the model object
        text.setText(setSameText);
        assertFalse(section.isDirty());
        assertEquals(0, section.getDirtyStateChangedCallCount());

        // set different text
        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());
        verify(type);
    }

    @Test
    public void testFocusListenerOnNameText() {
        setUpAndTestFocusListener(1, 4, 2);
    }

    private void setUpAndTestFocusListener(final int controlIndex, final int getNameCallTimes, final int getNamespaceCallTimes) {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[controlIndex];
        Listener[] listeners = text.getListeners(SWT.FocusIn);
        assertTrue(listeners.length > 0);
        listeners = text.getListeners(SWT.FocusOut);
        assertTrue(listeners.length > 0);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        final IStructureType type = selectionChangedMockCall(false);
        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());

        reset(controller);
        controller.rename(type, OTHER_TEXT);
        replay(controller);

        final Event event = new Event();
        event.widget = text;
        event.detail = SWT.FocusOut;
        text.notifyListeners(SWT.FocusOut, event);

        assertFalse(section.isDirty());
        assertEquals(2, section.getDirtyStateChangedCallCount());
        verify(type, controller);
    }

    @Test
    public void testKeyListenerOnNameText() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[1];
        Listener[] listeners = text.getListeners(SWT.KeyDown);
        assertEquals(1, listeners.length);
        final Listener keyDownListeners = listeners[0];
        listeners = text.getListeners(SWT.KeyUp);
        assertEquals(1, listeners.length);
        assertEquals(keyDownListeners, listeners[0]);
    }

    // @Test
    // public void testInitialize() {
    // fail("Not yet implemented");
    // }

    @Test
    public void testIsDirty() {
        assertFalse(section.isDirty());
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);
        IStructureType type = selectionChangedMockCall(false);

        // change name
        final Control[] children = getClientCompositeChildren();
        ((Text) children[1]).setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        verify(type);

        type = selectionChangedMockCall(false);
        assertFalse(section.isDirty());
        verify(type);
    }

    @Test
    public void testIsStale() {
        assertFalse(section.isStale());

        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);

        final IStructureType type = selectionChangedMockCall(false);
        assertFalse(section.isStale());
        verify(type);

        reset(type);
        EasymockModelUtils.addComponentAndContainerCalls(type, XSDNamedComponent.class);
        expect(type.getName()).andReturn(OTHER_TEXT).atLeastOnce();
        replay(type);
        assertTrue(section.isStale());
        verify(type);
    }

    private IStructureType selectionChangedMockCall(final boolean readOnlyNode) {
        final IFormPart formPart = createMock(IFormPart.class);
        final IStructureType type = createMock(IStructureType.class);
        expect(type.getName()).andReturn(SOME_NAME).anyTimes();
        EasymockModelUtils.addComponentAndContainerCalls(type, XSDNamedComponent.class);
        replay(type);

        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(type).anyTimes();
        expect(treeNode.isReadOnly()).andReturn(readOnlyNode).anyTimes();
        replay(treeNode);

        final IProblemDecorator problemDecorator = createMock(IProblemDecorator.class);
        section.setProblemDecorator(problemDecorator);
        problemDecorator.setModelObject(type);
        problemDecorator.updateDecorations();
        replay(problemDecorator);

        final StructuredSelection selection = new StructuredSelection(treeNode);
        section.selectionChanged(formPart, selection);

        verify(problemDecorator);

        return type;
    }

    private Control[] getClientCompositeChildren() {
        final Section sectionControl = (Section) shell.getChildren()[0];
        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        final Control[] children = clientComposite.getChildren();
        return children;
    }
}
