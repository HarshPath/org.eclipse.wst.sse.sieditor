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

import org.eclipse.wst.sse.sieditor.test.core.editorfwk.ModelHandlerTest;
import org.eclipse.wst.sse.sieditor.test.model.AbstractModelObjectFactoryTest;
import org.eclipse.wst.sse.sieditor.test.model.EnsureSchemaCommandRepatchTest;
import org.eclipse.wst.sse.sieditor.test.model.ModelTestsSuite;
import org.eclipse.wst.sse.sieditor.test.model.TestWSTmodelInSyncWithDOM;
import org.eclipse.wst.sse.sieditor.test.model.factory.DataTypesModelRootFactoryTest;
import org.eclipse.wst.sse.sieditor.test.model.factory.ServiceInterfaceModelRootFactoryTest;
import org.eclipse.wst.sse.sieditor.test.model.utils.EmfXsdUtilsTest;
import org.eclipse.wst.sse.sieditor.test.model.utils.TestEmfWSDLUtils;
import org.eclipse.wst.sse.sieditor.test.model.utils.TestResourceUtils;
import org.eclipse.wst.sse.sieditor.test.model.validation.constraints.ValidationTestsSuite;
import org.eclipse.wst.sse.sieditor.test.model.xsd.ResolveImportedAndIncludedSchemasDefinitionsCommandsExecutionTest;
import org.eclipse.wst.sse.sieditor.test.model.xsd.ResolveReferredSchemasCommandsExecutionTest;
import org.eclipse.wst.sse.sieditor.test.model.xsd.XSDModelRootTest;
import org.eclipse.wst.sse.sieditor.test.ui.TestXMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.test.ui.XMLModelNotifierWrapperSingleTransactionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



/**
 * 
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
        ValidationTestsSuite.class,
	ModelTestsSuite.class,
	ModelCommandsTestSuite.class,
	XSDModelRootTest.class,
	TestSuiteForMVP.class,
	AbstractModelObjectFactoryTest.class,
	ModelHandlerTest.class,
	EmfXsdUtilsTest.class,
	TestResourceUtils.class,
	TestXMLModelNotifierWrapper.class,
	XMLModelNotifierWrapperSingleTransactionTest.class,
	TestWSTmodelInSyncWithDOM.class,
	ResolveReferredSchemasCommandsExecutionTest.class,
        ResolveImportedAndIncludedSchemasDefinitionsCommandsExecutionTest.class, 
        EnsureSchemaCommandRepatchTest.class,
        TestEmfWSDLUtils.class,
        DataTypesModelRootFactoryTest.class,
        ServiceInterfaceModelRootFactoryTest.class
        
})
public class AllTestsSuiteStandalone {
}
