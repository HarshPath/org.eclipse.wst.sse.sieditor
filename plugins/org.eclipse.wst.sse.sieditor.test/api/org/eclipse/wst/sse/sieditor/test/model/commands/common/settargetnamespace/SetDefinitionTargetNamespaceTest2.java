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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.settargetnamespace;

/**
 * Test Case: Set target namespace of definition with default target namespace
 * prefix and schemas with existing prefixes.
 * 
 * 
 * 
 */
public class SetDefinitionTargetNamespaceTest2 extends
        AbstractSetDefinitionTNSCommandTest {

    @Override
    protected String getWsdlFilename() {
        return "DefinitionWithDefaultTNSPrefixWSDLAndSchemasWithExistingPrefixes.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/setnamespace/";
    }

}
