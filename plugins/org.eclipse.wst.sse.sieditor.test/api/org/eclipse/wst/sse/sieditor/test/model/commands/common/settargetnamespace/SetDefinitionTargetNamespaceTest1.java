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

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.junit.Before;


/**
 * Test Case: Set Target namespace of definition importing WSDL with the same
 * target namespace.
 * 
 * 
 * 
 */
public class SetDefinitionTargetNamespaceTest1 extends AbstractSetDefinitionTNSCommandTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ResourceUtils.copyFileIntoTestProject(
                "pub/csns/setnamespace/DefinitionWithDefaultTNSPrefixWSDLAndSchemasWithExistingPrefixes.wsdl",
                Document_FOLDER_NAME, this.getProject(), "DefinitionWithDefaultTNSPrefixWSDLAndSchemasWithExistingPrefixes.wsdl");
        getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
    }

    @Override
    protected String getWsdlFilename() {
        return "DefinitionImportingWSDLwithSameTNS.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/setnamespace/";
    }

}
