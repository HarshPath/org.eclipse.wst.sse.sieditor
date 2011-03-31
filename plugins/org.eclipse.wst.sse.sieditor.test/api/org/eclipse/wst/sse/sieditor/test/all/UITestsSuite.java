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
package org.eclipse.wst.sse.sieditor.test.all;

import org.eclipse.wst.sse.sieditor.test.core.common.TestDisposableInstances;
import org.eclipse.wst.sse.sieditor.test.model.commands.TestUndoRedoInSourceAndEMF;
import org.eclipse.wst.sse.sieditor.test.ui.TestAbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.test.ui.TestServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.test.ui.preferences.TestServiceInterfaceEditorPreferencePage;
import org.eclipse.wst.sse.sieditor.test.ui.readonly.CommonDocumentationSectionReadOnlyTest;
import org.eclipse.wst.sse.sieditor.test.ui.readonly.ElementNodeDetailsControllerReadOnlyTest;
import org.eclipse.wst.sse.sieditor.test.ui.readonly.SIFormPageControllerReadOnlyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.AbstractEditorWithSourcePageUndoHandlersDisposeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.DataTypesLabelProviderPlugInTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.EditorTitleMessagesManagerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.ProblemDecoratorTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestSetSelectionInDTPageFromSourcePageWithDTE;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestSetSelectionInSIPageFromSourcePageWithSIE;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestSetSelectionInSourceWithDataTypesEditor;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestSetSelectionInSourceWithSIEditor;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestTypePropertyEditorHyperLinkSelection;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.TestTypeSearchDialog;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.propertyEditor.ElementTypeEditorTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.propertyEditor.FaultTypeEditorTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.common.propertyEditor.ParameterTypeEditorTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.AbstractFormPageControllerPluginTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.AbstractFormPageControllerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.CreateSearchListProviderTests;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DTTreeContextMenuListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesContentProviderTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesDetailsPageProviderTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesEditorPageTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesFormPageControllerJUnitTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesFormPageControllerPlugInTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesLabelProviderTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesMasterDetailsBlockPlugInTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.DataTypesMasterDetailsBlockTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.NamespaceDetailsPageTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SetNewTypeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SiEditorDataTypesFormPageControllerPlugInTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SiEditorDataTypesFormPageControllerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SimpleTypeFacetsUtilsFacetsVisibleTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SimpleTypeFacetsUtilsPatternsTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.SimpleTypeFacetsUtilsProjectTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.StandaloneDtEditorPageNSReadOnlyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.StructureNodeDetailsPageTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.TestElementNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.TestTypeNullNameAttribute;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.extract.ExtractNamespaceWizardTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.extract.ExtractSchemaTextFieldModifyListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.ElementNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.ImportedSchemaNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.ImportedTypesNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.NamespaceNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.SimpleTypeNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl.StructureTypeNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.labels.DTTreeNodeLabelsProviderFactoryTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.labels.DataTypesTreeNodesLabelsPovider_BrokenElements_Test;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.labels.DataTypesTreeNodesLabelsPovider_ValidElements_Test;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.TypePropertyEditorTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.selectionlisteners.AbstractTypePropertyEditorEventListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.selectionlisteners.TypePropertyEditorBrowseButtonSelectionListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.selectionlisteners.TypePropertyEditorNewButtonSelectionListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.selectionlisteners.TypePropertyEditorTypeComboEventListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.typecommitters.ElementTypeEditorTypeCommitterTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.typecommitters.ParameterTypeEditorTypeCommitterTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.factory.TestTreeNodeMapper;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.AbstractTypeDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.FaultTypeDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.GlobalElementDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.LocalElementDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.NewTypeDialogTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.ParameterTypeDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog.SimpleTypeDialogStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.nodes.impl.AbstractTreeNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.providers.TestWSDLContentProvider;
import org.eclipse.wst.sse.sieditor.test.ui.v2.providers.TestWSDLDetailsPageProvider;
import org.eclipse.wst.sse.sieditor.test.ui.v2.providers.TestWSDLLabelProvider;
import org.eclipse.wst.sse.sieditor.test.ui.v2.resources.TestCanEditResource;
import org.eclipse.wst.sse.sieditor.test.ui.v2.resources.TestIsSetEditValidatorWhenCreateModel;
import org.eclipse.wst.sse.sieditor.test.ui.v2.resources.TestModelFileMoved;
import org.eclipse.wst.sse.sieditor.test.ui.v2.resources.TestModelRevertToSaved;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.AbstractDetailsPageSectionTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.DocumentationSectionTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.ElementDetailsSectionRefreshFromRefToNonRefReLayoutTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.NamespaceDetailsSectionTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.StructureDetailsSectionTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.TestElementConstraintsController;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.TestElementDetailsSection;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.TestSimpleTypeConstraintsSection;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.TestTypePropertyEditor;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.AttributeStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.ElementNodeDetailsControllerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.ElementOfAnonymousTypeStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.ElementOfGlobalTypeStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.ElementRefToGlobalElementStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements.GlobalElementStrategyTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.tables.editing.EnumsTableEditingSupportTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.sections.tables.editing.PatternsTableEditingSupportTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect.DescriptionTypeResolverTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect.SIEditorSearchListProviderTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect.SchemaTypeResolverTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect.TypeSelectionDialogDelegateTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.AbstractMasterDetailsBlockTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.ServiceIntefaceEditorPageTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.TreeViewerSelectionListenerTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller.TestGetReferencedServices;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller.TestImportedSchemaNodes;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller.TestImportedServicesNodes;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller.TestSIFormPageController;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.detailspages.TestOperationDetailsPage;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.detailspages.TestParameterDetailsPage;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.detailspages.TestServiceInterfaceDetailsPage;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.formpage.TestSIMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.TestIsEditorActivationInTreeViewerEditorActivationStrategy;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.TestSITreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.TestSITreeViewerCellModifier;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.TestSITreeViewerEditorActivationStrategy;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.actionenablement.TestSIEActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.FaultNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.ImportedServiceNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.OperationCategoryNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.OperationNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.ParameterNodeTest;
import org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.labels.SITreeNodeLabelsProviderFactoryTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.MissingSchemaElementQuickfixTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.MissingSchemaForSchemaQuickFixTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.MultipageEditorTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.NonUIThreadOpenEditorTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.OpenEditorWithUnusedImportTest;
import org.eclipse.wst.sse.sieditor.test.v2.ui.editor.SIEditorRefresNullTNS;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses( { 
    MissingSchemaForSchemaQuickFixTest.class, 
    MultipageEditorTest.class, 
    OpenEditorWithUnusedImportTest.class,
    NonUIThreadOpenEditorTest.class, 
    DataTypesDetailsPageProviderTest.class,
    DataTypesContentProviderTest.class, 
    DataTypesEditorPageTest.class,
    DataTypesLabelProviderTest.class, 
    DataTypesMasterDetailsBlockTest.class, 
    DataTypesMasterDetailsBlockPlugInTest.class,
    DataTypesFormPageControllerJUnitTest.class, 
    DataTypesFormPageControllerPlugInTest.class,
    SiEditorDataTypesFormPageControllerTest.class, 
    SiEditorDataTypesFormPageControllerPlugInTest.class,
    ServiceIntefaceEditorPageTest.class, 
    StandaloneDtEditorPageNSReadOnlyTest.class, 
    DTTreeContextMenuListenerTest.class,
    AbstractFormPageControllerTest.class, 
    ElementNodeTest.class, 
    ImportedSchemaNodeTest.class, 
    ImportedTypesNodeTest.class,
    NamespaceNodeTest.class, 
    SimpleTypeNodeTest.class, 
    StructureTypeNodeTest.class, 
    DataTypesTreeNodesLabelsPovider_ValidElements_Test.class,
    DataTypesTreeNodesLabelsPovider_BrokenElements_Test.class,
    DTTreeNodeLabelsProviderFactoryTest.class,
    AbstractTreeNodeTest.class,
    NamespaceDetailsSectionTest.class, 
    NamespaceDetailsPageTest.class, 
    StructureNodeDetailsPageTest.class,
    StructureDetailsSectionTest.class, 
    DocumentationSectionTest.class, 
    AbstractDetailsPageSectionTest.class,
    AbstractTypePropertyEditorEventListenerTest.class,
    TypePropertyEditorBrowseButtonSelectionListenerTest.class,
    TypePropertyEditorNewButtonSelectionListenerTest.class,
    TypePropertyEditorTypeComboEventListenerTest.class,
    ElementTypeEditorTypeCommitterTest.class,
    ParameterTypeEditorTypeCommitterTest.class,
    TypePropertyEditorTest.class, 
    CommonDocumentationSectionReadOnlyTest.class, 
    SimpleTypeFacetsUtilsFacetsVisibleTest.class,
    SimpleTypeFacetsUtilsPatternsTest.class,
    SimpleTypeFacetsUtilsProjectTest.class, 
    ImportedServiceNodeTest.class,
    FaultNodeTest.class,
    OperationCategoryNodeTest.class, 
    OperationNodeTest.class,
    ParameterNodeTest.class, 
    SITreeNodeLabelsProviderFactoryTest.class,
    TestWSDLContentProvider.class, 
    TestWSDLDetailsPageProvider.class, 
    TestWSDLLabelProvider.class,
    TestSIFormPageController.class, 
    SIFormPageControllerReadOnlyTest.class, 
    ElementNodeDetailsControllerReadOnlyTest.class, 
    TestSITreeContextMenuListener.class, 
    TestSITreeViewerCellModifier.class,
    TestSITreeViewerEditorActivationStrategy.class, 
    TestOperationDetailsPage.class, 
    TestParameterDetailsPage.class,
    TestServiceInterfaceDetailsPage.class, 
    TestTypeSearchDialog.class, 
    TestElementDetailsSection.class,
    EnumsTableEditingSupportTest.class,
    PatternsTableEditingSupportTest.class,
    TestSimpleTypeConstraintsSection.class, 
    TestTypePropertyEditor.class, 
    NewTypeDialogTest.class,
    LocalElementDialogStrategyTest.class, 
    FaultTypeDialogStrategyTest.class, 
    GlobalElementDialogStrategyTest.class,
    ParameterTypeDialogStrategyTest.class, 
    SimpleTypeDialogStrategyTest.class, 
    AttributeStrategyTest.class,
    ElementNodeDetailsControllerTest.class, 
    ElementOfGlobalTypeStrategyTest.class, 
    AbstractTypeDialogStrategyTest.class,
    ElementRefToGlobalElementStrategyTest.class, 
    GlobalElementStrategyTest.class, 
    ElementOfAnonymousTypeStrategyTest.class,
    TestElementNodeDetailsPage.class, 
    TestAbstractDetailsPage.class, 
    DescriptionTypeResolverTest.class,
    SchemaTypeResolverTest.class, 
    AbstractEditorWithSourcePageUndoHandlersDisposeTest.class, 
    TestModelRevertToSaved.class,
    TestCanEditResource.class, 
    ElementTypeEditorTest.class, 
    ParameterTypeEditorTest.class, 
    FaultTypeEditorTest.class,
    SIEditorSearchListProviderTest.class, 
    ProblemDecoratorTest.class, 
    TestTypeNullNameAttribute.class, 
    DataTypesLabelProviderPlugInTest.class,
    TestServiceInterfaceEditorPreferencePage.class, 
    TestImportedServicesNodes.class,
    AbstractFormPageControllerPluginTest.class, 
    TestImportedSchemaNodes.class, 
    CreateSearchListProviderTests.class,
    TestElementConstraintsController.class, 
    TestGetReferencedServices.class, 
    TypeSelectionDialogDelegateTest.class,
    SIEditorRefresNullTNS.class,
    TestUndoRedoInSourceAndEMF.class,
    SetNewTypeTest.class,
    AbstractMasterDetailsBlockTest.class,
    TestTreeNodeMapper.class,
    MissingSchemaElementQuickfixTest.class,
    TestModelFileMoved.class,
    TreeViewerSelectionListenerTest.class,
    TestSIMasterDetailsBlock.class,
    TestIsEditorActivationInTreeViewerEditorActivationStrategy.class,
    ElementDetailsSectionRefreshFromRefToNonRefReLayoutTest.class,
    TestSIEActionEnablementForSelectionManager.class,
    TestServiceInterfaceEditor.class,
    
    EditorTitleMessagesManagerTest.class,
    TestIsSetEditValidatorWhenCreateModel.class,
    TestDisposableInstances.class,
    TestSetSelectionInSourceWithDataTypesEditor.class,
    TestSetSelectionInSourceWithSIEditor.class,
    
    ExtractNamespaceWizardTest.class,
    ExtractSchemaTextFieldModifyListenerTest.class,
    TestSetSelectionInDTPageFromSourcePageWithDTE.class,
    TestSetSelectionInSIPageFromSourcePageWithSIE.class,
    TestTypePropertyEditorHyperLinkSelection.class
})
public class UITestsSuite {

}
