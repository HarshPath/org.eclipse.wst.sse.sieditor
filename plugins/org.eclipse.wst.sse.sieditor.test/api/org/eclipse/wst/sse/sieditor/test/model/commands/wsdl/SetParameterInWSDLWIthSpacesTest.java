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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetParameterInWSDLWIthSpacesTest extends SIEditorBaseTest {
    private String filePathWsdl = "pub/wsdls space/wsdl space.wsdl";

    private String folderName = "wsdl space";
    private String projectName = "parameter test";
    private IWsdlModelRoot modelRoot;

    @Override
    protected String getProjectName() {
        return projectName;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = getWSDLModelRoot(filePathWsdl, "wsdl space.wsdl", folderName);
    }

    @Test
    public void testParameterTypeChange() throws ExecutionException {

        IDescription description = modelRoot.getDescription();
        IServiceInterface interfaceObj = description.getInterface("ServiceInterface1").get(0);
        IOperation operation = interfaceObj.getOperation("NewOperation1").get(0);
        IParameter parameter = operation.getInputParameter("Parameter").get(0);
        assertNotNull(parameter);
        ISchema[] schema = description.getSchema("http://www.w3.org/2001/XMLSchema");
        IType string = schema[0].getType(false, "string");

        SetParameterTypeCommand command = new SetParameterTypeCommand(parameter, string);
        Assert.assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(command));

        assertEquals(string, parameter.getType());
    }
}
