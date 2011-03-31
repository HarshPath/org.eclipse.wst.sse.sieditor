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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.wsdl.Part;
import org.junit.Assert;
import org.junit.Before;
import org.w3c.dom.NamedNodeMap;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetParameterTypeCommandTest extends AbstractCommandTest {

    private IParameter parameter;
    private IType stringType;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        parameter = null;
        stringType = null;
    }

    @Override
    protected String getWsdlFilename() {
        return "FINB_IC_ISSUE_CNCRC.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface interfaceObj = description.getInterface("InternalControlIssueCancelRequestConfirmation_In")
                .get(0);
        final IOperation operation = interfaceObj.getOperation("InternalControlIssueCancelRequestConfirmation_In").get(0);
        parameter = operation.getInputParameter("parameters").get(0);
       
        final ISchema[] schema = description.getSchema("http://www.w3.org/2001/XMLSchema");
        stringType = schema[0].getType(false, "string");
        return new SetParameterTypeCommand(parameter, stringType);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(redoStatus.isOK());
        assertEquals(stringType, parameter.getType());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final Part part = (Part) parameter.getComponent();
        final NamedNodeMap partDomAttributes = part.getElement().getAttributes();
        Assert.assertTrue(partDomAttributes.getNamedItem("element") != null);
        Assert.assertTrue(partDomAttributes.getNamedItem("type") == null);
    }

}
