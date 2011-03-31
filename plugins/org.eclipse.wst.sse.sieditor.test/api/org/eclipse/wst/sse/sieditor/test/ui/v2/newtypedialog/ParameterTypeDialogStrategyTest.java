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
import static org.junit.Assert.assertSame;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ISiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ParameterTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class ParameterTypeDialogStrategyTest {

    private static final String HTTP_SAMPLE_NAMESPACE = "http://sampleNamespace";
    private static final String NEW_SIMPLE_TYPE_GENERATED_NAME = "SimpleType1";
    private static final String NEW_STRUCTURE_TYPE_GENERATED_NAME = "StryctyreType1";
    private static final String NEW_ELEMENT_GENERATED_NAME = "Element1";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private IParameter parameter;
    private ISchema schemaMock;
    private IWsdlModelRoot wsdlModelRootMock;
    private IDescription description;
    private SIFormPageController siController;
    private MergedInterface dtController;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private static class ParameterTypeDialogStrategyExposer extends ParameterTypeDialogStrategy {

        public boolean abstractIsGlobalElementNameNotDuplicateExpose(final ISchema schema, final String name, final String type) {
            return isGlobalElementNameNotDuplicate(schema, name, type);
        }

        @Override
        public IDataTypesFormPageController getDTController() {
            return super.getDTController();
        }

        @Override
        public ISiEditorDataTypesFormPageController getSIDTController() {
            return super.getSIDTController();
        }

        @Override
        public ISchema[] getTNSSchema() {
            return super.getTNSSchema();
        }
    }

    @Test
    public final void testGetDialogTitle() {
        assertEquals(Messages.ParameterTypeDialogStrategy_window_title_new_parameter_type_dialog,
                new ParameterTypeDialogStrategy().getDialogTitle());
    }

    private static interface MergedInterface extends ISiEditorDataTypesFormPageController, IDataTypesFormPageController {

    }

    private ParameterTypeDialogStrategyExposer createAndInitStrategy() {
        final ParameterTypeDialogStrategyExposer strategy = new ParameterTypeDialogStrategyExposer();
        // the parameter the type property editor is displaying
        parameter = createNiceMock(IParameter.class);
        // mock of the model root
        wsdlModelRootMock = createMock(IWsdlModelRoot.class);
        expect(parameter.getModelRoot()).andReturn(wsdlModelRootMock).atLeastOnce();
        // mocks for the model objects
        description = createMock(IDescription.class);
        schemaMock = createMock(ISchema.class);
        // mocks for the two controllers
        siController = createMock(SIFormPageController.class);
        dtController = createMock(MergedInterface.class);

        replay(parameter);
        strategy.setInput(parameter);
        return strategy;
    }

    @Test
    public final void testGetDTController() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        // adjust the connection between the two controllers
        expect(siController.getDtController()).andReturn(dtController).atLeastOnce();
        replay(siController);
        // set the controller to the strategy
        strategy.setController(siController);
        // test method functionality
        assertEquals(dtController, strategy.getDTController());
        assertEquals(dtController, strategy.getSIDTController());

        verify(siController);
    }

    @Test
    public final void testCreateTypeCreatingSchema() {

        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        // expect the description to be asked for the namespeace and than for
        // contained schemas
        // with the same namespace - simulate return of no schemas
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(null).atLeastOnce();

        replay(description, wsdlModelRootMock, dtController);

        assertSame(null, strategy.getSchema());

        verify(parameter, description, wsdlModelRootMock, dtController);
    }

    @Test
    public final void testCreateTypeExistingSchemas() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        // adjust connection between model root and description
        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        // expect the description to be asked of it's namespace
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();
        // expect the description to be asked for all schemas with the target
        // namespace namespace
        final ISchema[] schemas = new ISchema[] { createMock(ISchema.class), createMock(ISchema.class) };
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(schemas).atLeastOnce();

        replay(description, wsdlModelRootMock);
        // test for a new element
        
        assertEquals(schemas[0], strategy.getSchema());

        verify(parameter, description, wsdlModelRootMock);
    }

    @Test
    public final void testGetTNSSchema() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        // adjust connection between description and model root
        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        // expect get target namespace to be called and all schemas wit the same
        // namespace to be retrieved
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(new ISchema[] { schemaMock });
        replay(description, wsdlModelRootMock);

        final ISchema[] schemas = strategy.getTNSSchema();

        assertEquals(schemaMock, schemas[0]);

        verify(description, wsdlModelRootMock);
    }

    @Test
    public final void testGetDefaultNameNoTnsNamespaces() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        // set connection between description and model root
        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        // expect get target namespace to be called and all schemas wit the same
        // namespace to be retrieved (return no namespaces)
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(null).atLeastOnce();
        replay(description, wsdlModelRootMock);

        // assert behaviour

        assertEquals(DataTypesFormPageController.ELEMENT_DEFAULT_NAME, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT));

        assertEquals(DataTypesFormPageController.STRUCTURE_TYPE_DEFAULT_NAME, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));

        assertEquals(DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));

        verify(description, wsdlModelRootMock);
    }

    @Test
    public final void testGenerateDefaultNameWithTnsNamespace() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();
        // set connectio
        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();

        final ISchema[] tnsSchemas = new ISchema[] { schemaMock };
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(tnsSchemas).atLeastOnce();

        expect(schemaMock.getAllTypes(DataTypesFormPageController.ELEMENT_DEFAULT_NAME + 1)).andReturn(
                new IType[] { createMock(IStructureType.class) });
        expect(schemaMock.getAllTypes(DataTypesFormPageController.ELEMENT_DEFAULT_NAME + 2)).andReturn(null);

        replay(description, wsdlModelRootMock, schemaMock);

        assertEquals(DataTypesFormPageController.ELEMENT_DEFAULT_NAME + 2, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT));

        verify(schemaMock);
        reset(schemaMock);

        expect(schemaMock.getAllTypes(DataTypesFormPageController.STRUCTURE_TYPE_DEFAULT_NAME + 1)).andReturn(
                new IType[] { createMock(IStructureType.class) });
        expect(schemaMock.getAllTypes(DataTypesFormPageController.STRUCTURE_TYPE_DEFAULT_NAME + 2)).andReturn(null);

        replay(schemaMock);

        assertEquals(DataTypesFormPageController.STRUCTURE_TYPE_DEFAULT_NAME + 2, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));

        verify(schemaMock);
        reset(schemaMock);

        expect(schemaMock.getAllTypes(DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME + 1)).andReturn(
                new IType[] { createMock(ISimpleType.class) });
        expect(schemaMock.getAllTypes(DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME + 2)).andReturn(null);

        replay(schemaMock);

        assertEquals(DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME + 2, strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));

        verify(description, wsdlModelRootMock, schemaMock);
    }

    @Test
    public final void testIsDuplicateName() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();

        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();

        final ISchema[] tnsSchemas = new ISchema[] { schemaMock };
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(tnsSchemas).atLeastOnce();

        final IStructureType type1 = createMock(IStructureType.class);
        final IStructureType type2 = createMock(IStructureType.class);
        final ISimpleType type3 = createMock(ISimpleType.class);

        final IType[] types = new IType[] { type1, type2, type3 };

        expect(type1.isElement()).andReturn(true).anyTimes();
        expect(type2.isElement()).andReturn(false).anyTimes();

        expect(schemaMock.getAllTypes(NEW_ELEMENT_GENERATED_NAME)).andReturn(types).atLeastOnce();
        expect(schemaMock.getAllTypes(NEW_STRUCTURE_TYPE_GENERATED_NAME)).andReturn(types).atLeastOnce();
        expect(schemaMock.getAllTypes(NEW_SIMPLE_TYPE_GENERATED_NAME)).andReturn(types).atLeastOnce();

        replay(wsdlModelRootMock, description, schemaMock, type1, type2);

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_ELEMENT_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT), strategy.isDuplicateName(NEW_ELEMENT_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT));

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_STRUCTURE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE), strategy.isDuplicateName(NEW_STRUCTURE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_SIMPLE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE), strategy.isDuplicateName(NEW_SIMPLE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));

        verify(wsdlModelRootMock, description, parameter, schemaMock, type1, type2);
    }

    @Test
    public final void testIsDuplicateNameNoSchema() {
        final ParameterTypeDialogStrategyExposer strategy = createAndInitStrategy();

        expect(wsdlModelRootMock.getDescription()).andReturn(description).atLeastOnce();
        expect(description.getNamespace()).andReturn(HTTP_SAMPLE_NAMESPACE).atLeastOnce();
        expect(description.getSchema(HTTP_SAMPLE_NAMESPACE)).andReturn(null).atLeastOnce();

        replay(wsdlModelRootMock, description);

        assertEquals(Boolean.valueOf(false), strategy.isDuplicateName(NEW_ELEMENT_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT));

        assertEquals(Boolean.valueOf(false), strategy.isDuplicateName(NEW_STRUCTURE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));

        assertEquals(Boolean.valueOf(false), strategy.isDuplicateName(NEW_SIMPLE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));
        verify(wsdlModelRootMock, description);
    }
}
