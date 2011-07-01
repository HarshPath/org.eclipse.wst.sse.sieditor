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

import java.util.Collection;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesEditorPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings( { "nls", "deprecation" })
public class TestTypePropertyEditorHyperLinkSelection extends SIEditorBaseTest {

    private static final String DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA = "pub/csns/typePropertyEditorTestData/";
    private static final String XSD_SUFFIX = ".xsd";
    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;
    private final boolean[] handleLinkActivatedMethodIsCalled = { false };
    private final boolean[] selectedTypeExistInVisibleSchemas = { false };
    private final boolean[] updateMethodIsCalled = { false };
    private final boolean[] showTypeMethodIsCalled = { false };
    private static final String SHEMA_FILENAME_PREFIX = "as2_schema";
    private TypePropertyEditorTest typePropEditor;
    private DataTypesEditorPage dtPage;
    private AbstractEditorPage serviceInterfaceEditorPage;
    private SIFormPageController siController;
    private DataTypesFormPageController dtController;

    private class TypePropertyEditorTest extends TypePropertyEditor {

        public HyperLinkSelectionHandlerTest hyperLinkSelectionHandler;

        public TypePropertyEditorTest(final AbstractFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
            hyperLinkSelectionHandler = new HyperLinkSelectionHandlerTest();

        }

        class HyperLinkSelectionHandlerTest extends HyperLinkSelectionHandler {

            @Override
            protected void handleLinkActivated(final ITreeNode input, final IType selectedType) {
                handleLinkActivatedMethodIsCalled[0] = true;
                super.handleLinkActivated(input, selectedType);
                return;
            }

            @Override
            protected boolean isSelectedTypeExistInVisibleSchemas(final Collection<ISchema> allVisibleSchemas, final IType selectedType) {
                // TypePropertyEditorTest.this.setSelectedType(selectedType);
                selectedTypeExistInVisibleSchemas[0] = super.isSelectedTypeExistInVisibleSchemas(allVisibleSchemas, selectedType);
                return selectedTypeExistInVisibleSchemas[0];
            }
        }

        public void initTypeDisplayer(final ITypeDisplayer displayer) {
            super.setTypeDisplayer(displayer);
        }

        @Override
        public IType getSelectedType() {
            // because we don't want to open a new editor
            return null;
        }

        @Override
        public void update() {
            updateMethodIsCalled[0] = true;
        }

        @Override
        protected HyperlinkAdapter createHyperLinkSelectionHandler() {
            return hyperLinkSelectionHandler;
        }

        @Override
        public ITypeDialogStrategy createNewTypeDialogStrategy() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected IType getType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ITypeCommitter getTypeCommitter() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private class DataTypeEditorPageTest extends DataTypesEditorPage {

        public DataTypeEditorPageTest(final FormEditor editor) {
            super(editor);
        }

        @Override
        public void showType(final IType type) {
            showTypeMethodIsCalled[0] = true;
        }

    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ResourceUtils.copyFileIntoTestProject(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + SHEMA_FILENAME_PREFIX
                + XSD_SUFFIX, Document_FOLDER_NAME, this.getProject(), SHEMA_FILENAME_PREFIX + XSD_SUFFIX);
        ResourceUtils.copyFileIntoTestProject(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + SHEMA_FILENAME_PREFIX + "2"
                + XSD_SUFFIX, Document_FOLDER_NAME, this.getProject(), SHEMA_FILENAME_PREFIX + "2" + XSD_SUFFIX);
        ResourceUtils.copyFileIntoTestProject(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + SHEMA_FILENAME_PREFIX + "3"
                + XSD_SUFFIX, Document_FOLDER_NAME, this.getProject(), SHEMA_FILENAME_PREFIX + "3" + XSD_SUFFIX);
        ResourceUtils.copyFileIntoTestProject(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + "test_xsd_modeled_AS2.wsdl",
                Document_FOLDER_NAME, this.getProject(), "test_xsd_modeled_AS2.wsdl");

        modelRoot = (IWsdlModelRoot) getModelRoot("pub/csns/typePropertyEditorTestData/test_xsd_modeled_AS.wsdl", //$NON-NLS-1$
                "test_xsd_modeled_AS.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        dtPage = (DataTypesEditorPage) editor.getPages().get(1);

        typePropEditor = new TypePropertyEditorTest(dtPage.getController(), dtPage);
        typePropEditor.initTypeDisplayer(new DataTypeEditorPageTest(dtPage.getEditor()));

        serviceInterfaceEditorPage = (AbstractEditorPage) editor.getPages().get(0);
        siController = (SIFormPageController)serviceInterfaceEditorPage.getController();
        dtController = (DataTypesFormPageController)((AbstractEditorPage) editor.getPages().get(1)).getController();
    }

    @Test
    public void handleLinkActivated() throws Exception {
        final IOperation operation = modelDescription.getInterface("AS").get(0) //$NON-NLS-1$
                .getOperation("op1").get(0); //$NON-NLS-1$
        final IParameter internalType1 = operation.getInputParameter("internalType1").get(0);
        final IParameter importedSchemaType = operation.getInputParameter("importedSchemaType").get(0);
        final IParameter internalType2 = operation.getOutputParameter("internalType2").get(0);

        handleLinkActivatedForInternalType(internalType1, internalType2);

        handleLinkActivatedForImportedType(importedSchemaType);
        
        ISchema schemaAS = modelDescription.getSchema("http://www.sap.com/caf/demo.sap.com/test_xsd/modeled/AS")[0];
        IStructureType type = (IStructureType)schemaAS.getType(true, "ElementRefTo_NSElement1");
        IElement refElement = type.getElements("NS_Element1").iterator().next();
        
        handleLinkActivatedForInternalSchemaType(refElement);
    }
    
    private void handleLinkActivatedForInternalSchemaType(IElement refElement) {
        ITreeNode treeNodeForInternalType1 = dtController.getTreeNodeMapper().getTreeNode(refElement);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForInternalType1, refElement.getType());
        assertInCaseWithExistedTypeInVisibleSchemas();
    }

    private void handleLinkActivatedForInternalType(final IParameter internalType1, final IParameter internalType2) {
        final ITreeNode treeNodeForInternalType1 = siController.getTreeNodeMapper().getTreeNode(internalType1);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForInternalType1, internalType1.getType());
        assertInCaseWithExistedTypeInVisibleSchemas();

        final ITreeNode treeNodeForInternalType2 = siController.getTreeNodeMapper().getTreeNode(internalType2);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForInternalType2, internalType2.getType());
        assertInCaseWithExistedTypeInVisibleSchemas();
    }

    private void handleLinkActivatedForImportedType(final IParameter importedSchemaType) {
        final ITreeNode treeNodeForimportedSchemaType = siController.getTreeNodeMapper().getTreeNode(importedSchemaType);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForimportedSchemaType, importedSchemaType.getType());
        assertInCaseWithExistedTypeInVisibleSchemas();
    }

    private void assertInCaseWithExistedTypeInVisibleSchemas() {
        assertTrue("Link activated in not called, when HyperLinkSelectionHandler must be notified",
                handleLinkActivatedMethodIsCalled[0]);
        assertTrue("Selected Type exist and new editor must not be opened", selectedTypeExistInVisibleSchemas[0]);
        assertTrue("Updated types is not called, when navigation to type is being performed", updateMethodIsCalled[0]);
        assertTrue("showType method is not invoked, when HyperLinkSelectionHandler is notified", showTypeMethodIsCalled[0]);
        resetAssertFlags();
    }

    private void resetAssertFlags() {
        handleLinkActivatedMethodIsCalled[0] = false;
        selectedTypeExistInVisibleSchemas[0] = false;
        updateMethodIsCalled[0] = false;
        showTypeMethodIsCalled[0] = false;
    }

    @Test
    public void testTypeHyperLinkWithImportedMessage() throws Exception {
         final IOperation operation = modelDescription.getInterface("ServiceInterface1").get(0) //$NON-NLS-1$
                .getOperation("NewOperation1").get(0); //$NON-NLS-1$

        final IParameter importedWsdlType1 = operation.getInputParameter("importedWsdlType1").get(0);
        final IParameter importedWsdlFromSchemaType = operation.getInputParameter("importedWsdlFromSchemaType").get(0);
        final IParameter internalType3 = operation.getOutputParameter("internalType3").get(0);

        handleLinkActivatedForInternalType(internalType3);

        handleLinkActivatedWhenSelectedTypeIsNotVisible(importedWsdlType1, importedWsdlFromSchemaType);

    }

     private void handleLinkActivatedWhenSelectedTypeIsNotVisible(final IParameter importedWsdlType1,
            final IParameter importedWsdlFromSchemaType) {
        selectedTypeExistInVisibleSchemas[0] = true;
        final ITreeNode treeNodeForInternalType1 = siController.getTreeNodeMapper().getTreeNode(importedWsdlType1);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForInternalType1, importedWsdlType1.getType());
        assertThatEditorNewEditorMustBeOpened();

        selectedTypeExistInVisibleSchemas[0] = true;
        final ITreeNode treeNodeForInternalType2 = siController.getTreeNodeMapper().getTreeNode(importedWsdlFromSchemaType);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForInternalType2, importedWsdlFromSchemaType
                .getType());
        assertThatEditorNewEditorMustBeOpened();
    }

    private void handleLinkActivatedForInternalType(final IParameter internalType3) {
        final ITreeNode treeNodeForimportedSchemaType = siController.getTreeNodeMapper().getTreeNode(internalType3);
        typePropEditor.hyperLinkSelectionHandler.handleLinkActivated(treeNodeForimportedSchemaType, internalType3.getType());
        assertInCaseWithExistedTypeInVisibleSchemas();
    }

    private void assertThatEditorNewEditorMustBeOpened() {
        assertTrue("Link activated in not called, when HyperLinkSelectionHandler must be notified",
                handleLinkActivatedMethodIsCalled[0]);

        assertFalse("Selected Type does not exist in current document and new editor must be opened",
                selectedTypeExistInVisibleSchemas[0]);

        assertFalse("Updated types must not be called, when new editor is being opened", updateMethodIsCalled[0]);
        assertFalse("showType method must not be called, when new editor is being opened", showTypeMethodIsCalled[0]);
        resetAssertFlags();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }

}
