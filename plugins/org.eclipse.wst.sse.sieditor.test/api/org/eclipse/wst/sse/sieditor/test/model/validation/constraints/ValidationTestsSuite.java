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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints;

import org.eclipse.wst.sse.sieditor.test.model.validation.EditingDomainListenerTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.EditorTitleValidationTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.EsmXsdModelAdapterTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.FaulltValidationTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ImportsValidationTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ParentsValidationTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.TestValidationOnImportedWsdl;
import org.eclipse.wst.sse.sieditor.test.model.validation.ValidationResultLocusesTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ValidationServiceInvalidMessageReferenceTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ValidationServiceTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ValidationServiceUndoRedoInSITabTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.ValidationStatusRegistryDuplicatedMessagesTest;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestAddressBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestAddressLocationURI;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestBindingVerb;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestHTTPElementLocation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestInputChildren;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestInputChildrenOperation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestOperationBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.http.TestOperationLocationURI;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestBindingTransport;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestBodyBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestBodyEncodingStyle;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestBodyParts;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestBodyPartsEncodedUse;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestFaultName;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestHeaderBinding;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestHeaderEncodedUseEncodingStyle;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestHeaderEncodedUseNamespaceURI;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestHeaderMessage;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestHeaderPart;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestSOAPElementLocation;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability.TestWSIOperationCompliant;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability.TestWSIOperationCompliantWithBigWSDL;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability.TestWSIPortTypesCompliant;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability.TestWSISchemaCompliant;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability.TestWSIXSDAttributeDeclarationCompliant;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.xsd.ValidXSDEntityTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( { 
    TestKeyAttribute.class,
    UniqeWsdlElementNamesValidationTest.class,
    TestPortAddress.class, 
    TestRequiredAttribute.class, 
    TestNcName.class,
    TestAddressBinding.class, 
        TestAddressLocationURI.class, 
        TestBindingVerb.class, 
        TestHTTPElementLocation.class,
        TestInputChildren.class, 
        TestInputChildrenOperation.class, 
        TestOperationBinding.class, 
        TestOperationLocationURI.class,
        org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestAddressBinding.class,
        org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestAddressLocationURI.class,
        TestBindingTransport.class, 
        TestBodyBinding.class, 
        TestBodyEncodingStyle.class, 
        TestBodyParts.class,
        TestBodyPartsEncodedUse.class, 
        TestFaultName.class, 
        TestHeaderBinding.class, 
        TestHeaderEncodedUseEncodingStyle.class,
        TestHeaderEncodedUseNamespaceURI.class, 
        TestHeaderMessage.class, 
        TestHeaderPart.class,
        org.eclipse.wst.sse.sieditor.test.model.validation.constraints.soap.TestOperationBinding.class,
        TestSOAPElementLocation.class, 
        EsmXsdModelAdapterTest.class, 
        ValidXSDEntityTest.class, 
        EditorTitleValidationTest.class,
        ValidationServiceTest.class,
        ValidationServiceUndoRedoInSITabTest.class,
        ValidationServiceInvalidMessageReferenceTest.class,
        FaulltValidationTest.class, 
        ValidationResultLocusesTest.class, 
        ImportsValidationTest.class,
        ParentsValidationTest.class,
        TestWSIOperationCompliant.class,
        TestWSIPortTypesCompliant.class,
        TestWSIOperationCompliantWithBigWSDL.class,
        TestWSISchemaCompliant.class,
        TestWSIXSDAttributeDeclarationCompliant.class,
        
        EditingDomainListenerTest.class,
        ValidationStatusRegistryDuplicatedMessagesTest.class,
        TestValidationOnImportedWsdl.class

})
public class ValidationTestsSuite {

}
