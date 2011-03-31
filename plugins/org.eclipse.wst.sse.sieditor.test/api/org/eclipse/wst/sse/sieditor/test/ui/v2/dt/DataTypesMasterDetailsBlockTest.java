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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeViewerCellModifier;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ISiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class DataTypesMasterDetailsBlockTest {

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private int addNewGlobalElementCounter = 0;
    private int addNewNamespaceCounter = 0;
    private int addNewElementCounter = 0;
    private int addNewSimpleTypeCounter = 0;
    private int addNewStructureTypeCounter = 0;
    private int addNewAttributeCounter = 0;
    private int removeCounter = 0;
    private SectionPart masterSectionPart;

    private class DTEdtorControllerMock extends DataTypesFormPageController {
        // used to confirm that the argument of some methods matches the given
        private Iterator<IModelObject> modelObjectMatch;
        protected Boolean isEditDeleteAllowed;
        protected int isResourceReadOnlyCounter;

        public DTEdtorControllerMock() {
            super(null, false);
        }

        @Override
        protected IModelObject getModelObject() {
            return null;
        }

        @Override
        public void handleAddElementAction(final ITreeNode selectedElement) {
            addNewElementCounter++;
        }

        @Override
        public void handleAddSimpleTypeAction(final ITreeNode selecetedNode) {
            addNewSimpleTypeCounter++;
        }

        @Override
        public void handleAddStructureTypeAction(final ITreeNode selectedElement) {
            addNewStructureTypeCounter++;
        }

        @Override
        public void handleAddAttributeAction(final ITreeNode selectedElement) {
            addNewAttributeCounter++;
        }

        @Override
        public void handleRemoveAction(final List<ITreeNode> removedTreeNodes) {
            removeCounter++;
        }

        public void setModelObjectForCompare(final Iterator<IModelObject> modelObjectMatch) {
            this.modelObjectMatch = modelObjectMatch;
        }

        @Override
        public boolean isPartOfEdittedDocument(final IModelObject modelObject) {
            if (modelObjectMatch != null && modelObjectMatch.hasNext()) {
                assertEquals(modelObjectMatch.next(), modelObject);
            } else {
                assertNull(modelObject);
            }
            return true;
        }

        @Override
        public void handleAddGlobalElementAction(final IDataTypesTreeNode treeNode) {
            addNewGlobalElementCounter++;
        }

        @Override
        protected boolean isEditAllowed(final Object editedObject) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            if (modelObjectMatch != null) {
                assertEquals(modelObjectMatch.next(), editedObject);
            } else {
                assertNull(editedObject);
            }
            return true;
        }

        @Override
        protected boolean isDeleteAllowed(final Object editedObject) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            if (modelObjectMatch != null && modelObjectMatch.hasNext()) {
                assertEquals(editedObject, modelObjectMatch);
            } else {
                assertNull(editedObject);
            }
            return true;
        }

        public void setEditDeleteAllowed(final Boolean isEditDeleteAllowed) {
            this.isEditDeleteAllowed = isEditDeleteAllowed;
        }

        @Override
        public boolean isResourceReadOnly() {
            isResourceReadOnlyCounter++;
            return super.isResourceReadOnly();
        }

        public int getIsResourceReadOnlyCounter() {
            return isResourceReadOnlyCounter;
        }

        public void setIsResourceReadOnlyCounter(final int isResourceReadOnlyCounter) {
            this.isResourceReadOnlyCounter = isResourceReadOnlyCounter;
        }

        public void setResourceReadOnly(final boolean readOnly) {
            this.readOnly = readOnly;
        }
    }

    private class SiEditorControllerMock extends DTEdtorControllerMock implements ISiEditorDataTypesFormPageController {
        @Override
        public void handleAddNewNamespaceAction() {
            addNewNamespaceCounter++;
        }

        @Override
        public boolean isAddNamespaceEnabled(ITreeNode selectedNode) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            return true;
        }

        @Override
        public ISchema addNewNamespace(final String newName) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    private class TreeViewerMock extends TreeViewer {
        public TreeViewerMock(final Composite parent) {
            super(parent);
        }

        @Override
        public ISelection getSelection() {
            return new StructuredSelection(createMock(IDataTypesTreeNode.class));
        }
    }

    private static class TestDataTypesMasterDetailsBlock extends DataTypesMasterDetailsBlock {

        public static boolean cointainsButtons = true;

        public static void setCointainsButtons(final boolean cointainsButtons) {
            TestDataTypesMasterDetailsBlock.cointainsButtons = cointainsButtons;
        }

        public TestDataTypesMasterDetailsBlock(final DataTypesEditorPage page) {
            super(page);
        }

        public static boolean createButtonsInvoked = false;

        @Override
        public void createButtons(final FormToolkit toolkit, final Composite buttonsComposite) {
            createButtonsInvoked = true;
            if (cointainsButtons) {
                super.createButtons(toolkit, buttonsComposite);
                return;
            }
            return;
        }

        public void setTreeViewer(final TreeViewer viewer) {
            this.treeViewer = viewer;
        }

        @Override
        public void updateButtonsState(final IStructuredSelection selection) {
            super.updateButtonsState(selection);
        }

        @Override
        protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
            super.createMasterPart(managedForm, parent);
        }

        public SectionPart getMasterSectionPart() {
            return masterSectionPart;
        }

        public static boolean createContentProviderInvoked = false;

        @Override
        protected DataTypesContentProvider createContentProvider() {
            createContentProviderInvoked = true;
            return super.createContentProvider();
        }

        public static boolean createLabelProviderInvoked = false;

        @Override
        protected DataTypesLabelProvider createLabelProvider() {
            createLabelProviderInvoked = true;
            return super.createLabelProvider();
        }
    };

    @Test
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)}
     * .
     */
    public final void testCreateMasterPartIManagedFormCompositeStructure() {
        final TestDataTypesMasterDetailsBlock mdb = new TestDataTypesMasterDetailsBlock(createMock(DataTypesEditorPage.class));
        TestDataTypesMasterDetailsBlock.setCointainsButtons(false);
        final Composite parent = new Composite(new Shell(Display.getDefault()), SWT.NONE);
        final IManagedForm managedForm = createMock(IManagedForm.class);
        final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
        expect(managedForm.getToolkit()).andReturn(toolkit).atLeastOnce();
        managedForm.addPart(isA(SectionPart.class));
        replay(managedForm);

        TestDataTypesMasterDetailsBlock.createButtonsInvoked = false;
        TestDataTypesMasterDetailsBlock.createContentProviderInvoked = false;
        TestDataTypesMasterDetailsBlock.createLabelProviderInvoked = false;

        mdb.createMasterPart(managedForm, parent);

        verify(managedForm);
        // checks if the master section is constructed propperly
        masterSectionPart = mdb.getMasterSectionPart();
        assertNotNull(masterSectionPart);
        final Section section = masterSectionPart.getSection();
        assertNotNull(section);
        assertEquals(Messages.DataTypesMasterDetailsBlock_master_section_title_data_types, section.getText());

        assertTrue(TestDataTypesMasterDetailsBlock.createButtonsInvoked);
        assertTrue(TestDataTypesMasterDetailsBlock.createContentProviderInvoked);
        assertTrue(TestDataTypesMasterDetailsBlock.createLabelProviderInvoked);

        final TreeViewer treeViewer = mdb.getTreeViewer();

        assertEquals(1, treeViewer.getCellEditors().length);
        assertTrue(treeViewer.getCellEditors()[0] instanceof TextCellEditor);
        assertTrue(treeViewer.getCellModifier() instanceof DTTreeViewerCellModifier);
        assertTrue(treeViewer.getColumnViewerEditor() instanceof TreeViewerEditor);
        assertArrayEquals(new String[] { UIConstants.EMPTY_STRING }, treeViewer.getColumnProperties());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#createButtons(org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.swt.widgets.Composite)}
     * .
     */
    @Test
    public final void testCreateButtonsInSIEditor() {

        final TestDataTypesMasterDetailsBlock mdBlock = new TestDataTypesMasterDetailsBlock(null);
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        final Composite buttonsComposite = new Composite(shell, SWT.None);
        final FormToolkit formToolkit = new FormToolkit(display);
        mdBlock.setTreeViewer(new TreeViewerMock(new Composite(shell, SWT.None)));
        mdBlock.setController(new SiEditorControllerMock());

        TestDataTypesMasterDetailsBlock.cointainsButtons = true;

        mdBlock.createButtons(formToolkit, buttonsComposite);
        final Control[] children = buttonsComposite.getChildren();
        assertEquals(9, children.length);
        assertTrue(buttonsComposite.getLayout() instanceof RowLayout);
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_namespace_button.equals(((Button) children[0]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_AddGlobalElement.equals(((Button) children[1]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_simple_type_button.equals(((Button) children[2]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_complex_type_button.equals(((Button) children[3]).getText()));
        assertTrue(UIConstants.EMPTY_STRING.equals(((Button) children[4]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_element_button.equals(((Button) children[5]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_attribute_button.equals(((Button) children[6]).getText()));
        assertTrue(UIConstants.EMPTY_STRING.equals(((Button) children[7]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_remove_button.equals(((Button) children[8]).getText()));

        ((Button) children[0]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewNamespaceCounter == 1);
        ((Button) children[1]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewGlobalElementCounter == 1);
        ((Button) children[2]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewSimpleTypeCounter == 1);
        ((Button) children[3]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewStructureTypeCounter == 1);

        assertFalse(((Button) children[4]).isVisible());
        ((Button) children[5]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewElementCounter == 1);
        ((Button) children[6]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewAttributeCounter == 1);

        assertFalse(((Button) children[7]).isVisible());
        ((Button) children[8]).notifyListeners(SWT.Selection, null);
        assertTrue(removeCounter == 1);

        final Listener listener = children[0].getListeners(SWT.Selection)[0];
        assertNotNull(listener);
        for (int i = 0; i < children.length; i++) {
            final Control control = children[i];
            final Listener[] listeners = control.getListeners(SWT.Selection);
            if (i != 7 && i != 4) {
                assertEquals(1, listeners.length);
            } else {
                assertEquals(0, listeners.length);
            }
        }
    }

    @Test
    public final void testCreateButtonsStandalone() {
        // there should be no AddNewNamespace button
        final TestDataTypesMasterDetailsBlock mdBlock = new TestDataTypesMasterDetailsBlock(null);
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        final Composite buttonsComposite = new Composite(shell, SWT.None);
        final FormToolkit formToolkit = new FormToolkit(display);
        mdBlock.setTreeViewer(new TreeViewerMock(new Composite(shell, SWT.None)));
        final IModelRoot modelRoot = EasyMock.createNiceMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(null).anyTimes();
        EasyMock.replay(modelRoot);
        mdBlock.setController(new DataTypesFormPageController(modelRoot, false));

        TestDataTypesMasterDetailsBlock.cointainsButtons = true;

        mdBlock.createButtons(formToolkit, buttonsComposite);

        final Control[] children = buttonsComposite.getChildren();
        assertTrue(8 == children.length);
        assertTrue(Messages.DataTypesMasterDetailsBlock_AddGlobalElement.equals(((Button) children[0]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_simple_type_button.equals(((Button) children[1]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_complex_type_button.equals(((Button) children[2]).getText()));
        assertTrue(UIConstants.EMPTY_STRING.equals(((Button) children[3]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_element_button.equals(((Button) children[4]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_add_attribute_button.equals(((Button) children[5]).getText()));
        assertTrue(UIConstants.EMPTY_STRING.equals(((Button) children[6]).getText()));
        assertTrue(Messages.DataTypesMasterDetailsBlock_remove_button.equals(((Button) children[7]).getText()));

        mdBlock.setController(new DTEdtorControllerMock());

        ((Button) children[0]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewGlobalElementCounter == 1);
        ((Button) children[1]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewSimpleTypeCounter == 1);
        ((Button) children[2]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewStructureTypeCounter == 1);

        assertFalse(((Button) children[3]).isVisible());
        ((Button) children[4]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewElementCounter == 1);
        ((Button) children[5]).notifyListeners(SWT.Selection, null);
        assertTrue(addNewAttributeCounter == 1);

        assertFalse(((Button) children[6]).isVisible());
        ((Button) children[7]).notifyListeners(SWT.Selection, null);
        assertTrue(removeCounter == 1);

        final Listener listener = children[0].getListeners(SWT.Selection)[0];
        assertNotNull(listener);
        for (int i = 0; i < children.length; i++) {
            final Control control = children[i];
            final Listener[] listeners = control.getListeners(SWT.Selection);
            if (i != 6 && i != 3) {
                assertEquals(1, listeners.length);
            } else {
                assertEquals(0, listeners.length);
            }
        }
        assertTrue(buttonsComposite.getLayout() instanceof RowLayout);
    }

    @Test
    public void testRefreshTreeViewerIsCalledOnComponentChangedEvent() {
        final boolean refreshTreeViewer_Called[] = {false};
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        final TreeViewerMock treeViewerMock = new TreeViewerMock(new Composite(shell, SWT.None));

        final DataTypesMasterDetailsBlock masterDetailsBlock = new DataTypesMasterDetailsBlock(null) {
            {
                this.treeViewer = treeViewerMock;
            }
            
            @Override
            protected void updateButtonsState(IStructuredSelection selection) {
                // do nothing            }
            }
            @Override
            public void refreshTreeViewer() {
                refreshTreeViewer_Called[0] = true; 
            }
            
        };
     
        final IModelChangeEvent event = EasyMock.createNiceMock(IModelChangeEvent.class);
        masterDetailsBlock.componentChanged(event);

        assertTrue("Expected treeviewer refresh call on componentChanged event.", 
                refreshTreeViewer_Called[0]);
    }

}
