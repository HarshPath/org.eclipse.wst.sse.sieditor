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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ParameterTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ParameterTypeEditorTypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * 
 */
public class ParameterTypeEditorTest {

    private ParameterTypeEditorTypeCommitter typeCommitter;
    private ITreeNode input;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        input = createMock(ITreeNode.class);
        typeCommitter = createMock(ParameterTypeEditorTypeCommitter.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private class ParameterTypeEditorExposer extends ParameterTypeEditor {
        public ParameterTypeEditorExposer(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
        }

        @Override
        protected ParameterTypeEditorTypeCommitter createParameterTypeCommitter() {
            return typeCommitter;
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor#createNewTypeDialogStrategy()}
     * .
     */
    @Test
    public final void testCreateNewTypeDialogStrategy() {
        typeCommitter.setInput(input);
        replay(typeCommitter);
        final ParameterTypeEditorExposer typeEditor = new ParameterTypeEditorExposer(createMock(SIFormPageController.class), createMock(ITypeDisplayer.class));
        typeEditor.setInput(input);
        verify(typeCommitter);
        final ITypeDialogStrategy strategy = typeEditor.createNewTypeDialogStrategy();
        assertTrue(strategy instanceof ParameterTypeDialogStrategy);
    }

}
