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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.labels;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.DTTreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class DataTypesTreeNodesLabelsPovider_BrokenElements_Test extends SIEditorBaseTest {

    private IXSDModelRoot modelRoot;

    private IStructureType globalElementWithComplexContent;

    private IStructureType globalElementWithComplexType;
    private IStructureType globalElementWithSimpleType;
    private IStructureType complexTypeComplexContent;

    private IElement elementReference;

    private final DTTreeNodeLabelsProviderFactory factory = DTTreeNodeLabelsProviderFactory.instance();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (modelRoot == null) {
            final IFile fileForAllValidLabels = ResourceUtils.copyFileIntoTestProject("pub/csns/labels/labels_with_errors.xsd",
                    Document_FOLDER_NAME, this.getProject(), "labels_with_errors.xsd");
            getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            modelRoot = getXSDModelRoot(fileForAllValidLabels);
        }

        final ISchema schema = modelRoot.getSchema();

        globalElementWithComplexContent = (IStructureType) schema.getType(true, "GlobalElementComplexContent");
        complexTypeComplexContent = (IStructureType) schema.getType(false, "ComplexTypeComplexContent");

        globalElementWithComplexType = (IStructureType) schema.getType(true, "Element4");
        globalElementWithSimpleType = (IStructureType) schema.getType(true, "Element5");
        elementReference = (IElement) complexTypeComplexContent.getElements("GlobalElementSimpleContent").toArray()[0];
    }

    @Test
    public void testCheckLabels() {
        assertEquals("Element5 : SimpleType3", factory.getLabelsProvider(globalElementWithSimpleType).getTreeDisplayText());
        assertEquals("SimpleType3", factory.getLabelsProvider(globalElementWithSimpleType).getTypeDisplayText());

        assertEquals("Element4 : ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexType)
                .getTreeDisplayText());
        assertEquals("ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexType).getTypeDisplayText());

        assertEquals("GlobalElementComplexContent : ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexContent)
                .getTreeDisplayText());
        assertEquals("ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexContent).getTypeDisplayText());

        assertEquals("GlobalElementSimpleContent : <Unresolved Type>", factory.getLabelsProvider(elementReference)
                .getTreeDisplayText());
        assertEquals("<Unresolved Type>", factory.getLabelsProvider(elementReference).getTypeDisplayText());
    }
}
