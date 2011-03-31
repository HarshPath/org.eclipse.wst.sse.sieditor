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
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class DataTypesTreeNodesLabelsPovider_ValidElements_Test extends SIEditorBaseTest {

    private IXSDModelRoot modelRootForAllValidLabels;

    private ISimpleType simpleTypeWithBaseType;
    private ISimpleType simpleTypeWithAnonymousBaseType;
    private ISimpleType simpleTypeWithSimpleType;
    private ISimpleType simpleTypeWithAnonymousSimpleType;

    private IStructureType classicComplexType;
    private IStructureType anonymousGlobalElement;
    private IStructureType globalElementWithBaseType;
    private IStructureType globalElementWithAnonymousBaseType;
    private IStructureType globalElementWithComplexContent;
    private IStructureType globalElementWithSimpleContent;
    private IStructureType globalElementWithComplexType;
    private IStructureType globalElementWithSimpleType;
    private IStructureType globalElementWithAnonymousSimpleType;

    private IElement elementReference;
    private IElement attributeWithBaseType;
    private IElement attributeWithAnonymousBaseType;

    private final DTTreeNodeLabelsProviderFactory factory = DTTreeNodeLabelsProviderFactory.instance();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (modelRootForAllValidLabels == null) {
            final IFile fileForAllValidLabels = ResourceUtils.copyFileIntoTestProject("pub/csns/labels/labels_all.xsd",
                    Document_FOLDER_NAME, this.getProject(), "labels_all.xsd");
            ResourceUtils.copyFileIntoTestProject("pub/csns/labels/labels_to_import.xsd", Document_FOLDER_NAME,
                    this.getProject(), "labels_to_import.xsd");
            getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            modelRootForAllValidLabels = getXSDModelRoot(fileForAllValidLabels);
        }

        final ISchema schema = modelRootForAllValidLabels.getSchema();
        simpleTypeWithBaseType = (ISimpleType) schema.getType(false, "SimpleTypeWithBaseType");
        simpleTypeWithAnonymousBaseType = (ISimpleType) schema.getType(false, "SimpleTypeWithAnonymousBaseType");
        simpleTypeWithSimpleType = (ISimpleType) schema.getType(false, "SimpleTypeWithSimpleType");
        simpleTypeWithAnonymousSimpleType = (ISimpleType) schema.getType(false, "SimpleTypeWithAnonymousSimpleType");

        classicComplexType = (IStructureType) schema.getType(false, "ClassicComplexType");
        anonymousGlobalElement = (IStructureType) schema.getType(true, "ClassicGlobalElement");
        globalElementWithBaseType = (IStructureType) schema.getType(true, "GlobalElementWithBaseType");
        globalElementWithAnonymousBaseType = (IStructureType) schema.getType(true, "GlobalElementWithAnonymousBaseType");
        globalElementWithComplexContent = (IStructureType) schema.getType(true, "GlobalElementWithComplexContent");
        globalElementWithSimpleContent = (IStructureType) schema.getType(true, "GlobalElementWithSimpleContent");
        globalElementWithComplexType = (IStructureType) schema.getType(true, "GlobalElementWithComplexType");
        globalElementWithSimpleType = (IStructureType) schema.getType(true, "GlobalElementWithSimpleType");
        globalElementWithAnonymousSimpleType = (IStructureType) schema.getType(true, "GlobalElementWithAnonymousSimpleType");

        elementReference = (IElement) globalElementWithComplexContent.getElements("ClassicGlobalElement").toArray()[0];
        attributeWithBaseType = (IElement) globalElementWithComplexContent.getElements("Attribute1").toArray()[0];
        attributeWithAnonymousBaseType = (IElement) globalElementWithComplexContent.getElements("Attribute2").toArray()[0];
    }

    @Test
    public void testCheckLabels() {
        assertEquals("SimpleTypeWithBaseType", factory.getLabelsProvider(simpleTypeWithBaseType).getTreeDisplayText());
        assertEquals("string", factory.getLabelsProvider(simpleTypeWithBaseType).getTypeDisplayText());

        assertEquals("SimpleTypeWithAnonymousBaseType", factory.getLabelsProvider(simpleTypeWithAnonymousBaseType)
                .getTreeDisplayText());
        assertEquals("string", factory.getLabelsProvider(simpleTypeWithAnonymousBaseType).getTypeDisplayText());

        assertEquals("SimpleTypeWithSimpleType", factory.getLabelsProvider(simpleTypeWithSimpleType).getTreeDisplayText());
        assertEquals("SimpleTypeWithBaseType", factory.getLabelsProvider(simpleTypeWithSimpleType).getTypeDisplayText());

        assertEquals("SimpleTypeWithAnonymousSimpleType", factory.getLabelsProvider(simpleTypeWithAnonymousSimpleType)
                .getTreeDisplayText());
        assertEquals("SimpleTypeWithBaseType", factory.getLabelsProvider(simpleTypeWithAnonymousSimpleType).getTypeDisplayText());

        assertEquals("ClassicComplexType", factory.getLabelsProvider(classicComplexType).getTreeDisplayText());
        assertEquals("anyType", factory.getLabelsProvider(classicComplexType).getTypeDisplayText());

        assertEquals("ClassicGlobalElement : anonymous", factory.getLabelsProvider(anonymousGlobalElement).getTreeDisplayText());
        assertEquals("anonymous", factory.getLabelsProvider(anonymousGlobalElement).getTypeDisplayText());

        assertEquals("GlobalElementWithBaseType : float", factory.getLabelsProvider(globalElementWithBaseType)
                .getTreeDisplayText());
        assertEquals("float", factory.getLabelsProvider(globalElementWithBaseType).getTypeDisplayText());

        assertEquals("GlobalElementWithAnonymousBaseType : float", factory.getLabelsProvider(globalElementWithAnonymousBaseType)
                .getTreeDisplayText());
        assertEquals("float", factory.getLabelsProvider(globalElementWithAnonymousBaseType).getTypeDisplayText());

        assertEquals("GlobalElementWithSimpleType : SimpleTypeWithAnonymousBaseType", factory.getLabelsProvider(
                globalElementWithSimpleType).getTreeDisplayText());
        assertEquals("SimpleTypeWithAnonymousBaseType", factory.getLabelsProvider(globalElementWithSimpleType)
                .getTypeDisplayText());

        assertEquals("GlobalElementWithComplexType : ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexType)
                .getTreeDisplayText());
        assertEquals("ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexType).getTypeDisplayText());

        assertEquals("GlobalElementWithAnonymousSimpleType : SimpleTypeWithAnonymousBaseType", factory.getLabelsProvider(
                globalElementWithAnonymousSimpleType).getTreeDisplayText());
        assertEquals("SimpleTypeWithAnonymousBaseType", factory.getLabelsProvider(globalElementWithAnonymousSimpleType)
                .getTypeDisplayText());

        assertEquals("GlobalElementWithComplexContent : ClassicComplexType", factory.getLabelsProvider(
                globalElementWithComplexContent).getTreeDisplayText());
        assertEquals("ClassicComplexType", factory.getLabelsProvider(globalElementWithComplexContent).getTypeDisplayText());

        assertEquals("GlobalElementWithSimpleContent : SimpleTypeWithBaseType", factory.getLabelsProvider(
                globalElementWithSimpleContent).getTreeDisplayText());
        assertEquals("SimpleTypeWithBaseType", factory.getLabelsProvider(globalElementWithSimpleContent).getTypeDisplayText());

        assertEquals("ClassicGlobalElement : anonymous", factory.getLabelsProvider(elementReference).getTreeDisplayText());
        assertEquals("anonymous", factory.getLabelsProvider(elementReference).getTypeDisplayText());

        assertEquals("Attribute1 : string", factory.getLabelsProvider(attributeWithBaseType).getTreeDisplayText());
        assertEquals("string", factory.getLabelsProvider(attributeWithBaseType).getTypeDisplayText());

        assertEquals("Attribute2 : string", factory.getLabelsProvider(attributeWithAnonymousBaseType).getTreeDisplayText());
        assertEquals("string", factory.getLabelsProvider(attributeWithAnonymousBaseType).getTypeDisplayText());
    }

}
