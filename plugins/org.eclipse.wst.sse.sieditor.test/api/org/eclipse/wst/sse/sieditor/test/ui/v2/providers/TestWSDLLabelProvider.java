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
package org.eclipse.wst.sse.sieditor.test.ui.v2.providers;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.DTTreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class TestWSDLLabelProvider {

    @Test
    public void testConstructTextForNotNullType() {
        final WSDLLabelProvider provider = new WSDLLabelProvider();

        final IType type = createNiceMock(IType.class);
        expect(type.getName()).andReturn("TypeName").atLeastOnce();
        replay(type);

        final IParameter parameter = createNiceMock(IParameter.class);
        expect(parameter.getType()).andReturn(type).atLeastOnce();
        expect(parameter.getName()).andReturn("ParameterName").atLeastOnce();
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);

        final String text = provider.getText(node);

        assertEquals("ParameterName : TypeName", text);

        verify(parameter);
        verify(type);
    }

    @Test
    public void testConstructTextForNotNullTypeAndDecorateDotElement() {
        final WSDLLabelProvider provider = new WSDLLabelProvider();

        final IType type = createNiceMock(IType.class);
        expect(type.getName()).andReturn("TypeName.element").atLeastOnce();
        replay(type);

        final IParameter parameter = createNiceMock(IParameter.class);
        expect(parameter.getType()).andReturn(type).atLeastOnce();
        expect(parameter.getName()).andReturn("ParameterName").atLeastOnce();
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);

        final String text = provider.getText(node);

        assertEquals("ParameterName : TypeName [Element]", text);

        verify(parameter);
        verify(type);
    }

    @Test
    public void testConstructTextForNullType() {
        final WSDLLabelProvider provider = new WSDLLabelProvider();

        final IParameter parameter = createNiceMock(IParameter.class);
        expect(parameter.getName()).andReturn("ParameterName").atLeastOnce();
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);

        final String text = provider.getText(node);

        assertEquals(MessageFormat.format("ParameterName : {0}", new Object[] { Messages.AbstractEditorLabelProvider_0 }), text);

        verify(parameter);
    }

    @Test
    public void testForegroundIsRedForNullType() {
        final WSDLLabelProvider provider = new WSDLLabelProvider();

        final IParameter parameter = createNiceMock(IParameter.class);
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);
        final Color colorRed = provider.getForeground(node);

        assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_BLACK), colorRed);
        verify(parameter);
    }

    @Test
    public void testForegroundIsBlackForNotNullType() {
        final WSDLLabelProvider provider = new WSDLLabelProvider();

        final IType type = createNiceMock(IType.class);
        replay(type);

        final IParameter parameter = createNiceMock(IParameter.class);
        expect(parameter.getType()).andReturn(type);
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);
        final Color colorRed = provider.getForeground(node);

        assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_BLACK), colorRed);
    }

    @Test
    public void testConstructionOfToolTipText() {
        final String validationMessage = "Validation Status Message";
        final IValidationStatus status = createNiceMock(IValidationStatus.class);
        expect(status.getMessage()).andReturn(validationMessage);
        replay(status);

        final List<IValidationStatus> statuses = new ArrayList<IValidationStatus>();
        statuses.add(status);

        final IValidationStatusProvider validationProvider = createNiceMock(IValidationStatusProvider.class);
        expect(validationProvider.getStatus((IModelObject) anyObject())).andReturn(statuses);
        replay(validationProvider);

        final WSDLLabelProvider provider = new WSDLLabelProvider() {
            @Override
            protected IValidationStatusProvider getValidationStatusProvider(final Object modelObject) {
                return validationProvider;
            }
        };

        final IParameter parameter = createNiceMock(IParameter.class);
        replay(parameter);

        final ParameterNode node = new ParameterNode(null, parameter, null);
        final String toolTip = provider.getToolTipText(node);

        assertEquals(validationMessage, toolTip);
        verify(parameter);
    }

    @Test
    public void testGetTypeDisplayName() {
        final IStructureType wrapperType = createMock(IStructureType.class);
        final IElement element = createMock(IElement.class);
        final IType wrappedType = createMock(IType.class);

        final Collection<IElement> elements = new ArrayList<IElement>();
        elements.add(element);

        expect(wrapperType.getName()).andReturn("wrapperName").anyTimes();
        expect(wrapperType.isAnonymous()).andReturn(false).anyTimes();
        replay(wrappedType, wrapperType, element);

        final String text = DTTreeNodeLabelsProviderFactory.instance().getLabelsProvider(wrapperType).getDisplayName();
        Assert.assertEquals("wrapperName", text);

        verify(wrappedType, wrapperType, element);
    }

}
