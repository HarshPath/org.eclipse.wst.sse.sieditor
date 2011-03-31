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
package org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.FaultTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class FaultTypeDialogStrategyTest {
    private static final String HTTP_SAMPLE_NAMESPACE = "http://sampleNamespace";

    @Test
    public void testIsElementEnabled() {
        Assert.assertTrue(new FaultTypeDialogStrategy().isElementEnabled());
    }

    @Test
    public void testIsStructureTypeEnabled() {
        Assert.assertFalse(new FaultTypeDialogStrategy().isStructureTypeEnabled());
    }

    @Test
    public void testIsSimpleTypeEnabled() {
        Assert.assertFalse(new FaultTypeDialogStrategy().isSimpleTypeEnabled());
    }

    @Test
    public final void testGenerateDefaultName() {
        IDescription description = createMock(IDescription.class);
        ISchema schemaMock = createMock(ISchema.class);
        IWsdlModelRoot wsdlModelRootMock = createMock(IWsdlModelRoot.class);
        IParameter parameter = createNiceMock(IParameter.class);

        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();

        ISchema[] tnsSchemas = new ISchema[] { schemaMock };
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(tnsSchemas).atLeastOnce();

        expect(schemaMock.getAllTypes(DataTypesFormPageController.FAULT_ELEMENT_DEFAULT_NAME + 1)).andReturn(
                new IType[] { createMock(IStructureType.class) });
        expect(schemaMock.getAllTypes(DataTypesFormPageController.FAULT_ELEMENT_DEFAULT_NAME + 2)).andReturn(null);

        expect(parameter.getModelRoot()).andReturn(wsdlModelRootMock).atLeastOnce();

        replay(description, wsdlModelRootMock, schemaMock, parameter);

        FaultTypeDialogStrategy faultTypeDialogStrategy = new FaultTypeDialogStrategy();
        faultTypeDialogStrategy.setInput(parameter);
        assertEquals(DataTypesFormPageController.FAULT_ELEMENT_DEFAULT_NAME + 2, faultTypeDialogStrategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_FAULT_ELEMENT));

        verify(schemaMock);
        reset(schemaMock);
    }
}
