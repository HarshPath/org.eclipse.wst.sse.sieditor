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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.validation.EsmXsdModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

/**
 *
 * 
 */
public class DataTypesLabelProviderPlugInTest extends SIEditorBaseTest {

    private static final int MAX_NUM_CHILDREN = 100;

    private static class DataTypesLabelProviderExposer extends DataTypesLabelProvider {

        IValidationStatusProvider service;

        @Override
        protected IValidationStatusProvider getValidationStatusProvider(Object modelObject) {
            return service;
        }

    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesLabelProvider#areChildrenValidCircular(Object[])}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public void areChildrenValidCircular() throws IOException, CoreException {
        // set up
        final IXSDModelRoot xsdModelRoot = getXSDModelRoot("validation/circularElementRef.xsd", "circular.xsd"); //$NON-NLS-1$//$NON-NLS-2$
        DataTypesFormPageController controller = new DataTypesFormPageController(xsdModelRoot, false);
        NamespaceNode namespaceNode = new NamespaceNode(xsdModelRoot.getSchema(), controller.getTreeNodeMapper());
        final ValidationService service = new ValidationService(xsdModelRoot.getSchema().getComponent().eResource()
                .getResourceSet(), xsdModelRoot);
        service.addModelAdapter(new EsmXsdModelAdapter());
        xsdModelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(xsdModelRoot.getEnv().getEditingDomain()) {

                    @Override
                    protected void doExecute() {
                        service.validate(xsdModelRoot.getSchema());

                    }
                });

        DataTypesLabelProviderExposer labelProvider = new DataTypesLabelProviderExposer();
        labelProvider.service = service.getValidationStatusProvider();
        // set up model objects
        Object[] schemaElements = namespaceNode.getChildren();
        // IElementNode invalidElement = (IElementNode) ((ITreeNode)
        // schemaElements[0]).getChildren()[3];
        // // assert you've got the right element
        //        assertEquals("AnonymousElement?", invalidElement.getDisplayName()); //$NON-NLS-1$

        // if you'd like to see the logic for the asserts - open the
        // xsd with the DTEditor. See the type hierarchy

        assertNotSame(((ITreeNode) schemaElements[0]).getImage(), labelProvider.getImage(schemaElements[0]));
        assertSame(((ITreeNode) schemaElements[1]).getImage(), labelProvider.getImage((ITreeNode) schemaElements[1]));

        ITreeNode element1 = (ITreeNode) schemaElements[0];
        assertEquals("Element1", element1.getDisplayName()); //$NON-NLS-1$
        assertSame(((ITreeNode) element1.getChildren()[0]).getImage(), labelProvider
                .getImage((ITreeNode) element1.getChildren()[0]));
        assertSame(((ITreeNode) element1.getChildren()[1]).getImage(), labelProvider
                .getImage((ITreeNode) element1.getChildren()[1]));
        assertNotSame(((ITreeNode) element1.getChildren()[3]).getImage(), labelProvider.getImage((ITreeNode) element1
                .getChildren()[3]));
        assertNotSame(((ITreeNode) element1.getChildren()[3]).getImage(), labelProvider.getImage((ITreeNode) element1
                .getChildren()[3]));
    }

    @Test
    public void testChildrenNodesAreFinite() throws IOException, CoreException {
        // set up
        IXSDModelRoot xsdModelRoot = getXSDModelRoot("validation/circularElementRef.xsd", "shcema.xsd"); //$NON-NLS-1$//$NON-NLS-2$
        DataTypesFormPageController controller = new DataTypesFormPageController(xsdModelRoot, false);
        NamespaceNode namespaceNode = new NamespaceNode(xsdModelRoot.getSchema(), controller.getTreeNodeMapper());

        // set up model objects
        for (Object childNode : namespaceNode.getChildren()) {
            int numberOfAllChildrenNodes = getNumberOfAllChildrenNodes((ITreeNode) childNode, 0);
            assertTrue("Infinite Loop have been found for node: " + ((ITreeNode) childNode).getDisplayName()
                    + " Open the file from this test, and press * on the problem node.",
                    MAX_NUM_CHILDREN > numberOfAllChildrenNodes);
        }
    }

    private int getNumberOfAllChildrenNodes(ITreeNode node, int foundChildren) {
        for (Object child : node.getChildren()) {
            foundChildren++;
            if (foundChildren >= MAX_NUM_CHILDREN) {
                return foundChildren;
            }
            foundChildren = getNumberOfAllChildrenNodes((ITreeNode) child, foundChildren);
        }
        return foundChildren;
    }

}
