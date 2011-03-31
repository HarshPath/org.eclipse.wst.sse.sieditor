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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.junit.Assert;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetParameterTypeCommandWithNeededImportTest extends AbstractCommandTest {

    private IParameter parameter;
    private IDescription description;
    private IXSDModelRoot xsdModelRoot;
    private ValidationService validationService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        parameter = null;
        description = null;
        xsdModelRoot = null;
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
    protected IWsdlModelRoot getModelRoot() throws Exception {
        ResourceUtils.createFolderInProject(getProject(), Document_FOLDER_NAME);

        final IFile xsdFile = ResourceUtils.copyFileIntoTestProject("pub/import/services/xsd/stockquote.xsd",
                Document_FOLDER_NAME + "/xsd", this.getProject(), "sch ema.xsd");
        refreshProjectNFile(xsdFile);
        xsdModelRoot = ModelManager.getInstance().getXSDModelRoot(new FileEditorInput(xsdFile));

        return super.getModelRoot();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        description = modelRoot.getDescription();
        final IServiceInterface interfaceObj = description.getInterface("InternalControlIssueCancelRequestConfirmation_In")
                .get(0);
        final IOperation operation = interfaceObj.getOperation("InternalControlIssueCancelRequestConfirmation_In").get(0);
        parameter = operation.getInputParameter("parameters").get(0);

        final IType type = xsdModelRoot.getSchema().getType(false, "StructureType1");

        Assert.assertEquals(3, description.getAllVisibleSchemas().size());
        SetParameterTypeCommand setParameterTypeCommand = new SetParameterTypeCommand(parameter, type);
        return setParameterTypeCommand;
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        Assert.assertNotNull(parameter.getType());
        Assert.assertEquals(4, description.getAllVisibleSchemas().size());
        
        validationService = editor.getValidationService();
        List<ISchema> containedSchemas = description.getContainedSchemas();
 
        IValidationStatusProvider statusProvider = validationService.getValidationStatusProvider();
        int errorStatusCount = 0;
        for (ISchema schema : containedSchemas) {
            for (ISchema referredSchema : schema.getAllReferredSchemas()) {
                List<IValidationStatus> statuses = statusProvider.getStatus(referredSchema);
                for (IValidationStatus status : statuses) {
                    if (status.getSeverity() == IStatus.ERROR) {
                        ++errorStatusCount;
                    }
                }
            }
        }
        Assert.assertEquals(errorStatusCount, 0);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        Assert.assertEquals(3, description.getAllVisibleSchemas().size());
    }

}
