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
import static org.easymock.EasyMock.isA;
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
import org.eclipse.wst.sse.sieditor.ui.v2.sections.NamespaceDetailsSection;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class NamespaceDetailsSectionTest {

    private static final String OTHER_TEXT = "otherText"; //$NON-NLS-1$

    private static final String SOME_NAME_SPACE = "someNameSpace"; //$NON-NLS-1$

    private Image errorImage;

    private TestNamespaceDetailsSection section;

    private Shell shell;

    private IDataTypesFormPageController controller;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        errorImage = new Image(display, 10, 10);

        controller = createMock(IDataTypesFormPageController.class);
        final FormToolkit toolkit = new FormToolkit(display);
        final IManagedForm managedForm = createMock(IManagedForm.class);

        section = new TestNamespaceDetailsSection(controller, toolkit, managedForm);
        shell = new Shell(display);
        section.createContents(shell);

    }

    class TestNamespaceDetailsSection extends NamespaceDetailsSection {

        private int dirtyStateChangedCallCount = 0;

        public TestNamespaceDetailsSection(final IFormPageController controller, final FormToolkit toolkit,
                final IManagedForm managedForm) {
            super(controller, toolkit, managedForm);
        }

        public int getDirtyStateChangedCallCount() {
            return dirtyStateChangedCallCount;
        }

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
        assertEquals(Messages.NamespaceDetailsSection_section_title, sectionControl.getText());
        assertTrue(sectionControl.isExpanded());

        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        assertEquals(clientComposite, sectionControl.getClient());

        final Control[] children = clientComposite.getChildren();
        assertEquals(2, children.length);
        assertEquals(Label.class, children[0].getClass());
        assertEquals(Messages.NamespaceDetailsSection_namespace_label, ((Label) children[0]).getText());

        assertEquals(Text.class, children[1].getClass());
        final Text text = (Text) children[1];
        assertEquals(UIConstants.EMPTY_STRING, text.getText());

        assertTrue(clientComposite.getListeners(SWT.Paint).length > 0);
    }

    @Test
    public void testModifyListener() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[1];
        final Listener[] listeners = text.getListeners(SWT.Modify);
        assertEquals(1, listeners.length);

        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);

        final ISchema schema = selectionChangedMockCall(false);
        text.setText(SOME_NAME_SPACE);
        assertFalse(section.isDirty());
        assertEquals(0, section.getDirtyStateChangedCallCount());

        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());
        verify(schema, controller);
    }

    @Test
    public void testFocusListener() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[1];
        Listener[] listeners = text.getListeners(SWT.FocusIn);
        assertTrue(listeners.length > 0);
        listeners = text.getListeners(SWT.FocusOut);
        assertTrue(listeners.length > 0);

        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(controller.isPartOfEdittedDocument(isA(IModelObject.class))).andReturn(Boolean.valueOf(true));
        replay(controller);
        final ISchema schema = selectionChangedMockCall(false);

        text.setText(OTHER_TEXT);
        assertTrue(section.isDirty());
        assertEquals(1, section.getDirtyStateChangedCallCount());

        reset(controller);
        controller.renameNamespace(schema, OTHER_TEXT);
        replay(controller);

        final Event event = new Event();
        event.widget = text;
        event.detail = SWT.FocusOut;
        text.notifyListeners(SWT.FocusOut, event);

        assertFalse(section.isDirty());
        assertEquals(2, section.getDirtyStateChangedCallCount());
        verify(schema, controller);
    }

    @Test
    public void testKeyListener() {
        final Control[] children = getClientCompositeChildren();
        final Text text = (Text) children[1];
        Listener[] listeners = text.getListeners(SWT.KeyDown);
        assertEquals(1, listeners.length);
        final Listener keyDownListeners = listeners[0];
        listeners = text.getListeners(SWT.KeyUp);
        assertEquals(1, listeners.length);
        assertEquals(keyDownListeners, listeners[0]);
    }

    @Test
    public void testRefresh() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(controller.isPartOfEdittedDocument(isA(IModelObject.class))).andReturn(Boolean.valueOf(true));
        replay(controller);
        final ISchema schema = selectionChangedMockCall(false);
        verify(schema);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        final Control[] children = getClientCompositeChildren();

        final Text text = (Text) children[1];
        assertEquals(SOME_NAME_SPACE, text.getText());

        assertTrue(text.getEditable());

        assertFalse(section.isDirty());
    }

    @Test
    public void testRefreshReadOnly() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
        expect(controller.isPartOfEdittedDocument(isA(IModelObject.class))).andReturn(Boolean.valueOf(true));
        replay(controller);
        ISchema schema = selectionChangedMockCall(false);
        verify(schema);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        Control[] children = getClientCompositeChildren();

        Text text = (Text) children[1];
        assertEquals(SOME_NAME_SPACE, text.getText());
        assertFalse(text.getEditable());
        assertFalse(section.isDirty());

        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        replay(controller);

        schema = selectionChangedMockCall(true);
        verify(schema);
        assertEquals(0, section.getDirtyStateChangedCallCount());

        children = getClientCompositeChildren();

        text = (Text) children[1];
        assertEquals(SOME_NAME_SPACE, text.getText());
        assertFalse(text.getEditable());
        assertFalse(section.isDirty());
    }

    @Test
    public void testIsStale() {
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(controller.isPartOfEdittedDocument(isA(IModelObject.class))).andReturn(Boolean.valueOf(true));
        replay(controller);
        assertFalse(section.isStale());

        final ISchema schema = selectionChangedMockCall(false);
        assertFalse(section.isStale());
        verify(schema);

        reset(schema);
        final XSDSchema xsdSchema = createNiceMock(XSDSchema.class);
        replay(xsdSchema);
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        
        expect(schema.getNamespace()).andReturn(OTHER_TEXT).times(1);
        replay(schema);
        assertTrue(section.isStale());
        verify(schema);
    }

    private ISchema selectionChangedMockCall(final boolean isReadOnlyNode) {
        final ISchema schema = createMock(ISchema.class);

        final IModelRoot modelRoot = createMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(schema).anyTimes();
        replay(modelRoot);

        final XSDSchema xsdSchema = createNiceMock(XSDSchema.class);
        replay(xsdSchema);

        final IFormPart formPart = createMock(IFormPart.class);
        expect(schema.getNamespace()).andReturn(SOME_NAME_SPACE).anyTimes();
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);

        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(schema).anyTimes();
        expect(treeNode.isReadOnly()).andReturn(isReadOnlyNode).anyTimes();
        replay(treeNode);

        final IProblemDecorator problemDecorator = createMock(IProblemDecorator.class);
        section.setProblemDecorator(problemDecorator);
        problemDecorator.setModelObject(schema);
        EasyMock.expectLastCall().atLeastOnce();
        replay(problemDecorator);

        final StructuredSelection selection = new StructuredSelection(treeNode);
        section.selectionChanged(formPart, selection);

        verify(problemDecorator);

        return schema;
    }

    private Control[] getClientCompositeChildren() {
        final Section sectionControl = (Section) shell.getChildren()[0];
        final Composite clientComposite = (Composite) sectionControl.getChildren()[2];
        final Control[] children = clientComposite.getChildren();
        return children;
    }

    // @Test
    // public void testInitialize() {
    // fail("Not yet implemented");
    // }
}
