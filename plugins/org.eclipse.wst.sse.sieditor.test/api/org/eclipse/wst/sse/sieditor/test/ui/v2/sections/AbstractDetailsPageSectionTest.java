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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.AbstractDetailsPageSection;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public class AbstractDetailsPageSectionTest {

    private Shell shell;

    private IFormPageController controller;

    private FormToolkit toolkit;

    private IManagedForm managedForm;

    private TestAbstractDetailsPageSection section;

    @Before
    public void setUp() throws Exception {
        controller = createMock(IFormPageController.class);
        final Display display = Display.getDefault();
        toolkit = new FormToolkit(display);
        managedForm = createMock(IManagedForm.class);
        shell = new Shell(display);
        section = new TestAbstractDetailsPageSection(controller, toolkit, managedForm);
    }

    @After
    public void tearDown() throws Exception {
        controller = null;
        toolkit = null;
        managedForm = null;
        shell = null;
        section = null;
    }

    @Test
    public void testCreateSection() {
        section.createContents(shell);

        final Control[] children = shell.getChildren();
        assertNotNull(children);
        assertEquals(1, children.length);
        assertEquals(Section.class, children[0].getClass());
    }

    @Test
    public void testSetCompositeLayout() {
        section.createContents(shell);

        final Section sectionControl = (Section) shell.getChildren()[0];

        final Control[] sectionChildren = sectionControl.getChildren();
        assertNotNull(sectionChildren);
        assertEquals(3, sectionChildren.length);

        assertTrue(sectionChildren[2] instanceof Composite);
        final Composite clientComposite = (Composite) sectionChildren[2];
        assertEquals(clientComposite, sectionControl.getClient());

        assertEquals(GridLayout.class, clientComposite.getLayout().getClass());
    }

    @Test
    public void testGetToolkit() {
        assertEquals(toolkit, section.getToolkit());
    }

    @Test
    public void testGetManagedForm() {
        assertEquals(managedForm, section.getManagedForm());
    }

    @Test
    public void testGetController() {
        assertEquals(controller, section.getController());
    }

    @Test
    public void testGetModelObject() {
        final IFormPart formPart = createMock(IFormPart.class);
        StructuredSelection selection = new StructuredSelection();
        section.selectionChanged(formPart, selection);
        assertNull(section.getModelObject());

        final ITreeNode treeNode = createMock(ITreeNode.class);
        final IModelObject modelObject = createMock(IModelObject.class);
        EasymockModelUtils.addComponentAndContainerCalls(modelObject, EObject.class);
        expect(treeNode.getModelObject()).andReturn(modelObject).anyTimes();
        replay(treeNode, modelObject);
        selection = new StructuredSelection(treeNode);
        section.selectionChanged(formPart, selection);
        assertEquals(modelObject, section.getModelObject());
    }

    // @Test
    // public void testRefresh() {
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testDispose() {
    // fail("Not yet implemented");
    // }

    @Test
    public void testInitialize() {
        section.initialize(managedForm);

    }

    @Test
    public void testIsDirty() {
        assertFalse(section.isDirty());
    }

    @Test
    public void testIsStale() {
        assertFalse(section.isStale());
    }

    @Test
    public void testSetFormInput() {
        assertFalse(section.setFormInput(null));
    }

    @Test
    public void testSelectionChanged() {
        final IFormPart formPart = createMock(IFormPart.class);
        final ISelection notAStructuredSelection = createMock(ISelection.class);
        section.selectionChanged(formPart, notAStructuredSelection);
        assertNull(section.getNode());
        assertEquals(0, section.getRefreshCallCount());

        StructuredSelection selection = new StructuredSelection(new Object());
        section.selectionChanged(formPart, selection);
        assertNull(section.getNode());
        assertEquals(0, section.getRefreshCallCount());

        IModelObject modelObject = createMock(IModelObject.class);
        EasymockModelUtils.addComponentAndContainerCalls(modelObject, EObject.class);
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(modelObject).anyTimes();
        replay(modelObject, treeNode);
        
        selection = new StructuredSelection(treeNode);

        section.selectionChanged(formPart, selection);
        assertEquals(treeNode, section.getNode());
        assertEquals(1, section.getRefreshCallCount());
    }

    @Test
    public void testGetControl() {
        section.createContents(shell);
        assertNotNull(section.getControl());
        assertEquals(Section.class, section.getControl().getClass());
    }

    @Test
    public void testSetVisible() {
        section.createContents(shell);
        final Section control = section.getControl();
        final GridData layoutData = (GridData) control.getLayoutData();
        layoutData.exclude = true;
        assertTrue(control.getVisible());

        section.setVisible(true);
        assertTrue(control.getVisible());

        section.setVisible(false);
        assertFalse(control.isVisible());

        section.setVisible(false);
        assertFalse(control.isVisible());

        section.setVisible(true);
        assertTrue(control.getVisible());
    }

    class TestAbstractDetailsPageSection extends AbstractDetailsPageSection {

        private int refreshCallCount = 0;

        public TestAbstractDetailsPageSection(final IFormPageController controller, final FormToolkit toolkit, final IManagedForm managedForm) {
            super(controller, toolkit, managedForm);
        }

        @Override
        public void createContents(final Composite parent) {
            final FormToolkit toolkit = getToolkit();
            final Section section = createSection(parent, "title"); //$NON-NLS-1$
            section.setLayoutData(new GridData());
            final Composite clientComposite = toolkit.createComposite(section);
            section.setClient(clientComposite);
            setCompositeLayout(clientComposite);
        }

        @Override
        public void refresh() {
            refreshCallCount++;
        }

        @Override
        public FormToolkit getToolkit() {
            return super.getToolkit();
        }

        @Override
        public IManagedForm getManagedForm() {
            return super.getManagedForm();
        }

        @Override
        public IFormPageController getController() {
            return super.getController();
        }

        @Override
        public ITreeNode getNode() {
            return super.getNode();
        }

        @Override
        public IModelObject getModelObject() {
            return super.getModelObject();
        }

        @Override
        public Section getControl() {
            return super.getControl();
        }

        @Override
        public void setVisible(final boolean visible) {
            super.setVisible(visible);
        }

        public int getRefreshCallCount() {
            return refreshCallCount;
        }
    }
}
