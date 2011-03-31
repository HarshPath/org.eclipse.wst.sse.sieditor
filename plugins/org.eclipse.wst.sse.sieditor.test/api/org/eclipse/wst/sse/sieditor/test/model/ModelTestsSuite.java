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
package org.eclipse.wst.sse.sieditor.test.model;

import org.eclipse.wst.sse.sieditor.test.model.wsdl.impl.TestDescription;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Read API's Tests suite
 * 
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { WSDLReadAPITest.class, WsdlMultipleComponentsTest.class, InvalidWSDLTest.class, ReferencedDocumentsTest.class,
        XSDReadAPITest.class, XSDMultipleComponentsTest.class, InvalidXSDTest.class, XSDReferencedDocumentsTest.class,
        WSDLWriteTest.class, XSDWriteTest.class, SetAPIWSDLWriteTest.class, SetAPIXSDWriteTest.class, TestDescription.class,
        ReadOfDuplicateSITest.class, ActionsSelectionTest.class, UriEncodingsOnImportTest.class, RemoveTargetNamespaceTest.class })
public class ModelTestsSuite {

}
