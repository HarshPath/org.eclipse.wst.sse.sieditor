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

import org.eclipse.wst.sse.sieditor.test.model.commands.TestRenamePartCommandWithMultiReferredPart;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.CompositeTextOperationWrapperNotifiesTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.CompositeTextOperationWrapperTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.NewElementTypeCompositeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.NewSimpleTypeCompositeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.NewStructureTypeCompositeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.TextCommandWrapperTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.integration.NewElementTypeCompositeCommandIntegrationTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.integration.NewSimpleTypeCompositeCommandIntegrationTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.integration.NewStructureTypeCompositeCommandIntegrationTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.setcommandbuilder.DefaultSetTypeCommandBuilderTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.common.settargetnamespace.SetTargetNamespaceTestsSuite;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddAsynchronousOperationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddFaultCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddGlobalElementAndRenameToExistingOneCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddInParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddMessageCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddMessageNoNSCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddNewSchemaBadDocCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddNewSchemaCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddNewSchemaEmptyDocCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddOutParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddParameterTypeCommandNullTNSDocTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddServiceInterfaceAndOperationCommandBadDocTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddServiceInterfaceAndOperationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddServiceInterfaceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.AddSynchronousOperationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeAsynchronousOperationTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeDefinitionTNSCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeDefinitionTNSEmptyDocCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeDefinitionTNSNullTNSCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeGlobalElementTypeCommandChainTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeSchemaImportsLocationCommandTest1;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeSchemaImportsLocationCommandTest2;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeSynchronousOperationTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeSynchronousOperationTypeCommandWithFaultsTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeToAsynchronousOperationTypeNoInputTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ChangeToAsynchronousOperationTypeNoMessageTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.CloneNamespaceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteFaultCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteFaultParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteInParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteOutParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteServiceInterfaceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.DeleteServiceInterfaceWithOperationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.EnsureWsdlDefinitionCommandJiberishContentTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.EnsureWsdlDefinitionCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ExtractDependentXmlSchemaTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ExtractRootXmlSchemaTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ImportSchemaCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ImportSchemaCommandUndoRedoTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.InlineNamespaceCompositeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.InlineNamespaceCompositeCommandTestWithAnnotations;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.ReconcilerCalledAfterInliningTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RemoveSchemaCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RemoveXsdImportToImportedTypesCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RemoveXsdImportToReferedElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RenameOperationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RenameParameterCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RenameParameterInRpcStyleCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.RenameServiceInterfaceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetDocumentationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetFaultTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetParameterInWSDLWIthSpacesTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetParameterTypeCommandChangeFromExistingElementToTypeTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetParameterTypeCommandChangeFromMissingElementToTypeTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetParameterTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetParameterTypeImportFromNullTNSSchemaCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetUnwrappedParameterTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.SetWrappedParameterTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract.ExtractNamespaceRunnableWithImportTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract.ExtractNamespaceRunnableWithoutImportTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract.SchemaDependenciesUtilsTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract.XmlSchemaExtractorTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.facets.SetElementTypeCannotReuseFacetsCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.facets.SetElementTypeReuseFacetsCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAnotationToXSDAttributeDeclarationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAnotationToXSDAttributeDeclarationSimpleCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAnotationToXSDElementDeclarationCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAnotationToXSDSchemaImplCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAttributeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddAttributeToComplexTypeSimpleContentTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddComplexTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddEnumFacetToElementTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddEnumerationFacetCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddFacetCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddFacetToElementTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddGlobalElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddGlobalElementToSchemaWithNullTargetNamespaceTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddSchemaForSchemaXSDCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.AddSimpleTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.CopyElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.CopyElementCommandTestSimpleContent;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.CopyTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.DeleteFacetCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.EnsureSchemaElementCommandJiberishContentTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.EnsureSchemaElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.ImportSchemaCommandXSDTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.InlineStructureTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.MakeTypeAnonymousCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.MakeTypeGlobalCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.MakeTypeResolvableCommandElementDeclTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.MakeTypeResolvableCommandSimpleTypeTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.MakeTypeResolvableCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.OperationsOnRemovedObjectTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.RemoveGlobalElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.RemoveMixedSetCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.RenameComplexTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.RenameLocalElementCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.RenameSimpleTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetAttributeDefaultValueCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementComplexAnonymousTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementComplexTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementCustomSimpleTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementDefaultValueCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementImportedComplexTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementImportedPrimitiveTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementMaxOccursCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementMinOccursCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementNillableCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementPrimitiveTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementReferenceAnonymousTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementReferenceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementSimpleAnonymousTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetElementTypeCommandXSDTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetNamespaceCommandAfterNoNamespaceExistsTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetNamespaceCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.SetStructureTypeCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.ToggleBetweenMakeTypeAnonymousAndMakeTypeGlobalCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.xsd.setstructurebasetype.SetStructureTypeBaseTypeTestsSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( {
    SetElementReferenceAnonymousTypeCommandTest.class,
    SetElementSimpleAnonymousTypeCommandTest.class,
    SetElementComplexAnonymousTypeCommandTest.class,
    SetElementReferenceCommandTest.class,
    SetElementComplexTypeCommandTest.class,
    SetElementCustomSimpleTypeCommandTest.class,
    SetElementPrimitiveTypeCommandTest.class,
    SetElementMaxOccursCommandTest.class,
    SetElementMinOccursCommandTest.class,
    SetElementNillableCommandTest.class,
    SetNamespaceCommandTest.class,
    SetNamespaceCommandAfterNoNamespaceExistsTest.class,
    RenameLocalElementCommandTest.class,
    RenameSimpleTypeCommandTest.class,
    RenameComplexTypeCommandTest.class,
    RemoveGlobalElementCommandTest.class,
    RemoveMixedSetCommandTest.class,
    // ResolveImportedSchemaCommandTest.class,
    DeleteFacetCommandTest.class,
    AddFacetCommandTest.class,
    AddEnumerationFacetCommandTest.class,
    AddFacetToElementTest.class,
    AddEnumFacetToElementTest.class,
    AddComplexTypeCommandTest.class,
    AddGlobalElementCommandTest.class,
    AddGlobalElementToSchemaWithNullTargetNamespaceTest.class,
    /*
     * AddAnotationToXSDTypeDefinitionCommandTest. class , - EXCLUDED UNTIL
     * THE FEATURE IS FIXED
     */ 
    AddAnotationToXSDElementDeclarationCommandTest.class,
    AddAnotationToXSDAttributeDeclarationCommandTest.class,
    AddAnotationToXSDAttributeDeclarationSimpleCommandTest.class,
    AddAnotationToXSDSchemaImplCommandTest.class,
    AddSimpleTypeCommandTest.class,
    AddElementCommandTest.class,
    AddAttributeCommandTest.class,
    AddAttributeToComplexTypeSimpleContentTest.class,
    RemoveSchemaCommandTest.class,
    RemoveXsdImportToReferedElementCommandTest.class,
    RemoveXsdImportToImportedTypesCommandTest.class,
    DeleteServiceInterfaceCommandTest.class,
    DeleteServiceInterfaceWithOperationCommandTest.class,
    DeleteOutParameterCommandTest.class,
    DeleteInParameterCommandTest.class,
    DeleteFaultParameterCommandTest.class,
    DeleteFaultCommandTest.class,
    ChangeAsynchronousOperationTypeCommandTest.class,
    ChangeSynchronousOperationTypeCommandTest.class,
    ChangeSynchronousOperationTypeCommandWithFaultsTest.class,
    ChangeToAsynchronousOperationTypeNoInputTest.class,
    ChangeToAsynchronousOperationTypeNoMessageTest.class,
    AddServiceInterfaceCommandTest.class,
    AddAsynchronousOperationCommandTest.class,
    AddSynchronousOperationCommandTest.class,
    AddNewSchemaCommandTest.class,
    AddInParameterCommandTest.class,
    AddOutParameterCommandTest.class,
    SetWrappedParameterTypeCommandTest.class,
    SetUnwrappedParameterTypeCommandTest.class,
    AddFaultCommandTest.class,
    RenameParameterCommandTest.class,
    ChangeDefinitionTNSCommandTest.class,
    RenameServiceInterfaceCommandTest.class,
    RenameOperationCommandTest.class,
    SetDocumentationCommandTest.class,
    CopyElementCommandTest.class,
    CopyElementCommandTestSimpleContent.class,
    CopyTypeCommandTest.class,
    MakeTypeResolvableCommandTest.class,
    MakeTypeResolvableCommandSimpleTypeTest.class,
    MakeTypeResolvableCommandElementDeclTest.class,
    SetStructureTypeCommandTest.class,
    SetElementImportedComplexTypeCommandTest.class,
    SetElementImportedPrimitiveTypeCommandTest.class,
    SetParameterTypeCommandTest.class,
    SetParameterTypeCommandChangeFromExistingElementToTypeTest.class,
    SetParameterTypeCommandChangeFromMissingElementToTypeTest.class,
    SetParameterInWSDLWIthSpacesTest.class,
    AddMessageCommandTest.class,
    RenameParameterInRpcStyleCommandTest.class,
    ImportSchemaCommandTest.class,
    ChangeDefinitionTNSNullTNSCommandTest.class,
    ImportSchemaCommandUndoRedoTest.class,
    SetParameterTypeImportFromNullTNSSchemaCommandTest.class,
    AddParameterTypeCommandNullTNSDocTest.class,
    SetFaultTypeCommandTest.class,
    DefaultSetTypeCommandBuilderTest.class,
    NewElementTypeCompositeCommandTest.class,
    NewSimpleTypeCompositeCommandTest.class,
    NewStructureTypeCompositeCommandTest.class,
    NewElementTypeCompositeCommandIntegrationTest.class,
    NewSimpleTypeCompositeCommandIntegrationTest.class,
    NewStructureTypeCompositeCommandIntegrationTest.class,
    TextCommandWrapperTest.class,
    AddServiceInterfaceAndOperationCommandBadDocTest.class,
    AddServiceInterfaceAndOperationCommandTest.class,
    EnsureWsdlDefinitionCommandJiberishContentTest.class,
    EnsureWsdlDefinitionCommandTest.class,
    ChangeDefinitionTNSEmptyDocCommandTest.class,
    AddNewSchemaEmptyDocCommandTest.class,
    AddNewSchemaBadDocCommandTest.class,
    AddSchemaForSchemaXSDCommandTest.class,
    EnsureSchemaElementCommandTest.class,
    EnsureSchemaElementCommandJiberishContentTest.class,
    ImportSchemaCommandXSDTest.class,
    SetElementTypeCommandXSDTest.class,
    OperationsOnRemovedObjectTest.class,
    AddMessageNoNSCommandTest.class,
    SetElementTypeCannotReuseFacetsCommandTest.class,
    SetElementTypeReuseFacetsCommandTest.class,
    TestRenamePartCommandWithMultiReferredPart.class,
    MakeTypeGlobalCommandTest.class,
    MakeTypeAnonymousCommandTest.class,
    InlineStructureTypeCommandTest.class,
    ToggleBetweenMakeTypeAnonymousAndMakeTypeGlobalCommandTest.class,
    SetTargetNamespaceTestsSuite.class,
    CompositeTextOperationWrapperTest.class,
    AddGlobalElementAndRenameToExistingOneCommandTest.class,
    ExtractNamespaceRunnableWithoutImportTest.class,
    ExtractNamespaceRunnableWithImportTest.class,
    SchemaDependenciesUtilsTest.class,
    XmlSchemaExtractorTest.class,
    ChangeSchemaImportsLocationCommandTest1.class,
    ChangeSchemaImportsLocationCommandTest2.class,
    ExtractDependentXmlSchemaTest.class,
    ExtractRootXmlSchemaTest.class,
    CloneNamespaceCommandTest.class,
    InlineNamespaceCompositeCommandTest.class,
    InlineNamespaceCompositeCommandTestWithAnnotations.class,
    SetStructureTypeBaseTypeTestsSuite.class,
    CompositeTextOperationWrapperNotifiesTest.class,
    SetAttributeDefaultValueCommandTest.class,
    SetElementDefaultValueCommandTest.class,
    ReconcilerCalledAfterInliningTest.class,
    ChangeGlobalElementTypeCommandChainTest.class
    })
public class ModelCommandsTestSuite {
    public ModelCommandsTestSuite() {
    }
}
