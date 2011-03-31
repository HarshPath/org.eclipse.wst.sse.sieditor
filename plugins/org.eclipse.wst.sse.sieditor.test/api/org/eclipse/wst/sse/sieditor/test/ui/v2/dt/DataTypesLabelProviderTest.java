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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public class DataTypesLabelProviderTest {

//    private static final String TYPE_NAME = "TypeName"; //$NON-NLS-1$
//
//    private static final String BASE_TYPE_NAME = "baseTypeName"; //$NON-NLS-1$

    private static final String NODE_NAME = "nodeName"; //$NON-NLS-1$

//    private static final String ANONYMOUS_LABEL = Messages.DataTypesLabelProvider_anonymous_type_label;

    @Test
    public void testGetTextDataTypesTreeNode() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
        replay(node);

        // simple IDataTypesTreeNode
        final DataTypesLabelProvider provider = new DataTypesLabelProvider();
        assertEquals(NODE_NAME, provider.getText(node));
        verify(node);

        assertNull(provider.getText(new Object()));
    }

//    @Test
//    public void testGetTextElementNode() {
//        // IElementNode with null Type
//
//        final IElement element = createMock(IElement.class);
//        expect(element.getType()).andReturn(null).once();
//        final IElementNode node = new ElementNode(element, null, null);
//        replay(element);
//
//        final DataTypesLabelProvider provider = new DataTypesLabelProvider();
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE
//                + Messages.AbstractEditorLabelProvider_0, provider.getText(node));
//        verify( element);
//
//        // IElementNode with anonymous Structure Type != null and null Base Type
//        reset(node, element);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(element).once();
//
//        IType type = createMock(IStructureType.class);
//        final XSDElementDeclaration xsdElementDeclaration = createMock(XSDElementDeclaration.class);
//        expect(type.isAnonymous()).andReturn(true).once();
//        expect(type.getName()).andReturn(NODE_NAME).anyTimes();
//        expect(type.getComponent()).andReturn(xsdElementDeclaration).once();
//
//        expect(element.getType()).andReturn(type).anyTimes();
//        replay(node, element, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + NODE_NAME, provider.getText(node));
//        verify(node, element, type);
//
//        // IElementNode with anonymous Simple Type != null and null Base Type
//        reset(node, element);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(element).once();
//
//        type = createMock(ISimpleType.class);
//        expect(type.isAnonymous()).andReturn(true).once();
//        expect(type.getBaseType()).andReturn(null).once();
//        expect(type.getName()).andReturn(TYPE_NAME);
//
//        expect(element.getType()).andReturn(type).once();
//        replay(node, element, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + ANONYMOUS_LABEL, provider
//                .getText(node));
//        verify(node, element, type);
//
//        // IElementNode with anonymous Simple Type != null and Base Type != null
//        reset(node, element, type);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(element).once();
//        expect(type.isAnonymous()).andReturn(true).once();
//
//        final IType baseType = createMock(IType.class);
//        expect(baseType.getName()).andReturn(BASE_TYPE_NAME).anyTimes();
//        expect(baseType.isAnonymous()).andReturn(true).anyTimes();
//        expect(baseType.getBaseType()).andReturn(null);
//        expect(type.getBaseType()).andReturn(baseType).anyTimes();
//        expect(type.getName()).andReturn(null);
//
//        expect(element.getType()).andReturn(type).once();
//        replay(node, element, type, baseType);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + BASE_TYPE_NAME + UIConstants.SPACE
//                + UIConstants.OPEN_BRACKET + ANONYMOUS_LABEL + UIConstants.CLOSE_BRACKET, provider.getText(node));
//        verify(node, element, type, baseType);
//
//        // IElementNode with non-anonymous Type != null
//        reset(node, element, type);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(element).once();
//
//        expect(type.isAnonymous()).andReturn(false).once();
//        expect(type.getName()).andReturn(TYPE_NAME).once();
//        expect(element.getType()).andReturn(type).once();
//        replay(node, element, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + TYPE_NAME, provider.getText(node));
//        verify(node, element, type);
//    }

//    @Test
//    public void testGetTextStructureTypeNode() {
//        // IStructureTypeNode, isElement - true, with null Type
//        final IStructureTypeNode node = createMock(IStructureTypeNode.class);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        final IStructureType structure = createMock(IStructureType.class);
//        expect(node.getModelObject()).andReturn(structure).once();
//        expect(structure.isElement()).andReturn(true).once();
//        expect(structure.getType()).andReturn(null).once();
//        replay(node, structure);
//
//        final DataTypesLabelProvider provider = new DataTypesLabelProvider();
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE
//                + Messages.AbstractEditorLabelProvider_0, provider.getText(node));
//        verify(node, structure);
//
//        // IStructureTypeNode, isElement - true, with anonymous
//        // Structure Type != null
//        reset(node, structure);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(structure).once();
//        expect(structure.isElement()).andReturn(true).once();
//
//        IType type = createMock(IStructureType.class);
//        final XSDElementDeclaration xsdElementDeclaration = createMock(XSDElementDeclaration.class);
//        expect(type.isAnonymous()).andReturn(true).once();
//        expect(type.getName()).andReturn(NODE_NAME).once();
//        expect(type.getComponent()).andReturn(xsdElementDeclaration).once();
//        expect(structure.getType()).andReturn(type).once();
//        replay(node, structure, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + NODE_NAME, provider.getText(node));
//        verify(node, structure, type);
//
//        // IStructureTypeNode, isElement - true, with anonymous
//        // Simple Type != null and null Base Type
//        reset(node, structure);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(structure).once();
//        expect(structure.isElement()).andReturn(true).once();
//
//        type = createMock(ISimpleType.class);
//        expect(type.isAnonymous()).andReturn(true).once();
//        expect(type.getBaseType()).andReturn(null).once();
//
//        expect(structure.getType()).andReturn(type).once();
//        replay(node, structure, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + ANONYMOUS_LABEL, provider
//                .getText(node));
//        verify(node, structure, type);
//
//        // IStructureTypeNode, isElement - true, with anonymous
//        // Simple Type != null and Base Type != null
//        reset(node, structure, type);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(structure).once();
//
//        expect(structure.isElement()).andReturn(true).once();
//        expect(type.isAnonymous()).andReturn(true).once();
//
//        final IType baseType = createMock(IType.class);
//        expect(baseType.getName()).andReturn(BASE_TYPE_NAME).times(2);
//        expect(type.getBaseType()).andReturn(baseType).once();
//
//        expect(structure.getType()).andReturn(type).once();
//        replay(node, structure, type, baseType);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + BASE_TYPE_NAME + UIConstants.SPACE
//                + UIConstants.OPEN_BRACKET + ANONYMOUS_LABEL + UIConstants.CLOSE_BRACKET, provider.getText(node));
//        verify(node, structure, type, baseType);
//
//        // IStructureTypeNode , isElement - true, with non-anonymous Type !=
//        // null
//        reset(node, structure, type);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(structure).once();
//
//        expect(type.isAnonymous()).andReturn(false).once();
//        expect(type.getName()).andReturn(TYPE_NAME).once();
//        expect(structure.getType()).andReturn(type).once();
//        expect(structure.isElement()).andReturn(true).once();
//        replay(node, structure, type);
//
//        assertEquals(NODE_NAME + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + TYPE_NAME, provider.getText(node));
//        verify(node, structure, type);
//
//        // IStructureTypeNode , isElement - false
//        reset(node, structure);
//        expect(node.getTreeDisplayText()).andReturn(NODE_NAME).once();
//        expect(node.getModelObject()).andReturn(structure).once();
//        expect(structure.isElement()).andReturn(false).once();
//        replay(node, structure);
//
//        assertEquals(NODE_NAME, provider.getText(node));
//        verify(node, structure, type);
//    }

    @Test
    public void testGetImage() {
        final Display display = Display.getDefault();
        final Image image = new Image(display, 10, 10);

        final IDataTypesTreeNode node = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject modelObject = createNiceMock(IModelObject.class);
        expect(node.getModelObject()).andReturn(modelObject).anyTimes();

        expect(node.getImage()).andReturn(image).once();

        final IValidationStatusProvider validation = createNiceMock(IValidationStatusProvider.class);
        final IValidationService validationService = createNiceMock(IValidationService.class);

        expect(validation.getStatusMarker((IModelObject) anyObject())).andReturn(IStatus.OK);
        replay(modelObject, node, validation, validationService);

        final DataTypesLabelProvider provider = new DataTypesLabelProvider() {
            @Override
            protected IValidationStatusProvider getValidationStatusProvider(final Object modelObject) {
                return validation;
            };
        };
        assertEquals(image, provider.getImage(node));
        verify(node, modelObject);

        assertNull(provider.getImage(new Object()));
    }

}
