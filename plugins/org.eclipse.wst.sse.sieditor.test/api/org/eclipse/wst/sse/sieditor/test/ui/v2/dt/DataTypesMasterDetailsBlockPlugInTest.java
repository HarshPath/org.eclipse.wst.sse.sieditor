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
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

/**
 *
 *
 */
public class DataTypesMasterDetailsBlockPlugInTest extends SIEditorBaseTest{
    
    private static class TestDataTypesMasterDetailsBlock extends DataTypesMasterDetailsBlock{

        public TestDataTypesMasterDetailsBlock(DataTypesEditorPage parent) {super(parent);}
        
        public static boolean updateButtonsStateCalled = false;
        
        @Override
        protected void updateButtonsState(IStructuredSelection selection) {
            updateButtonsStateCalled = true;
            super.updateButtonsState(selection);
        }
        
        public static boolean removePressedCalled = false;
        @Override
        protected void removePressed() {
            removePressedCalled = true;
            super.removePressed();
        }
    }
    
    /**
     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)}.
     */
    @Test
    public final void testCreateMasterPartIManagedFormComposite() {
        IXSDModelRoot xsdModelRoot = null;
        try {
            xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd"); //$NON-NLS-1$//$NON-NLS-2$
        } catch (IOException e) {
            fail("failed to create XSD model root"); //$NON-NLS-1$
        } catch (CoreException e) {
            fail("failed to create XSD model root"); //$NON-NLS-1$
        }
        assertNotNull(xsdModelRoot);

        FormToolkit formToolkit = new FormToolkit(Display.getCurrent());
        IManagedForm managedForm = createNiceMock(IManagedForm.class);
        
        DataTypesEditorPage dtPage = createMock(DataTypesEditorPage.class);
        expect(dtPage.getModel()).andReturn(xsdModelRoot);
        
        expect(managedForm.getToolkit()).andReturn(formToolkit).anyTimes();
        expect(managedForm.getForm()).andReturn(new ScrolledForm(new Shell())).anyTimes();
        managedForm.addPart(isA(SectionPart.class));
        managedForm.addPart(isA(DetailsPart.class));
        
        replay(dtPage);
        replay(managedForm);
        
        DataTypesFormPageController dtfpController = new DataTypesFormPageController(xsdModelRoot, false);

        TestDataTypesMasterDetailsBlock mdb = new TestDataTypesMasterDetailsBlock(dtPage);
        mdb.setController(dtfpController);
        mdb.createContent(managedForm);

        TreeViewer treeViewer = mdb.getTreeViewer();

        //test selecting and the call to update buttons
        TestDataTypesMasterDetailsBlock.updateButtonsStateCalled = false;
        treeViewer.setSelection(new StructuredSelection(treeViewer.getTree().getItems()[0].getData()));
        assertTrue(TestDataTypesMasterDetailsBlock.updateButtonsStateCalled);
        assertNotNull(treeViewer.getSelection());
        assertEquals(1, ((StructuredSelection) treeViewer.getSelection()).size());
        
        //test press of delete
        TestDataTypesMasterDetailsBlock.removePressedCalled = false;
        pressKeyOnWidget(treeViewer.getTree(), SWT.DEL);
        assertTrue(TestDataTypesMasterDetailsBlock.removePressedCalled);
        //refresh the tree viewer because the form page 
        //(which should be the listener for model changes) is mocked
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(treeViewer.getTree().getItems()[0].getData()));
        
        //no j-unit tests available for direct editign of the tree nodes.
        
        verify(managedForm);
        verify(dtPage);
    }

    private void pressKeyOnWidget(Widget widget, int character) {
        Event keyEvent = new Event();
        keyEvent.keyCode = character;
        keyEvent.type = SWT.KeyUp;
        keyEvent.widget = widget;
        widget.notifyListeners(SWT.KeyDown, keyEvent);
    }

//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#addGlobalElementPressed()}.
//     */
//    @Test
//    public final void testAddGlobalElementPressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#addSimpleTypePressed()}.
//     */
//    @Test
//    public final void testAddSimpleTypePressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#removePressed()}.
//     */
//    @Test
//    public final void testRemovePressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#addComplexTypePressed()}.
//     */
//    @Test
//    public final void testAddComplexTypePressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#addNsPressed()}.
//     */
//    @Test
//    public final void testAddNsPressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#addAttributePressed()}.
//     */
//    @Test
//    public final void testAddAttributePressed() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#componentChanged(org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent)}.
//     */
//    @Test
//    public final void testComponentChanged() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    /**
//     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock#showType(org.eclipse.wst.sse.sieditor.model.xsd.api.IType)}.
//     */
//    @Test
//    public final void testShowType() {
//        fail("Not yet implemented"); // TODO
//    }

}
