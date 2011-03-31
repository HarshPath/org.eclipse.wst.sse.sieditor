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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common.propertyEditor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.FaultTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.FaultTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class FaultTypeEditorTest {

    private static class FaultTypeEditorExposer extends FaultTypeEditor {
        public FaultTypeEditorExposer(SIFormPageController controller, ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
        }

        @Override
        public ITypeDialogStrategy createNewTypeDialogStrategy() {
            return super.createNewTypeDialogStrategy();
        }

        @Override
        public IType getType() {
            return super.getType();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor#createNewTypeDialogStrategy()}
     * .
     */
    @Test
    public final void testCreateNewTypeDialogStrategy() {
        FaultTypeEditorExposer typeEditor = new FaultTypeEditorExposer(
                createMock(SIFormPageController.class),
                org.easymock.EasyMock.createMock(ITypeDisplayer.class));
        typeEditor.setInput(org.easymock.EasyMock.createMock(ITreeNode.class));
        ITypeDialogStrategy strategy = typeEditor.createNewTypeDialogStrategy();
        assertTrue(strategy instanceof FaultTypeDialogStrategy);
    }
    
    @Test
    public void testGetTypeForEmptyParameter() {
        SIFormPageController formPageController = createMock(SIFormPageController.class);
        ITypeDisplayer typeDisplayer = createMock(ITypeDisplayer.class);
        
        IFault fault = createMock(IFault.class);
        expect(fault.getParameters()).andReturn(Collections.EMPTY_LIST).anyTimes();
        replay(fault);
        
        ITreeNode selectedTreeNode = createMock(ITreeNode.class);
        expect(selectedTreeNode.getModelObject()).andReturn(fault).anyTimes();
        replay(selectedTreeNode);
        
        FaultTypeEditorExposer typeEditor = new FaultTypeEditorExposer(formPageController, typeDisplayer);
        typeEditor.setInput(selectedTreeNode);
        
        assertNull(typeEditor.getType());
    }
}
