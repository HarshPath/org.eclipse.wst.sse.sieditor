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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ChangeOperationTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RemoveSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.AbstractWSDLComponent;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

@SuppressWarnings("nls")
public class WSDLWriteTest extends SIEditorBaseTest {

    private static final String SOURCE_FOLDER = "src/wsdl";

    protected String getProjectName() {
        return "WSDLWriteTest";
    }

    @Test
    public void test_Description() throws Exception {
        // check for referenced documents
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "One.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        description = modelRoot.getDescription();

        AddServiceInterfaceCommand cmd = new AddServiceInterfaceCommand(modelRoot, description, "newInterface");
        modelRoot.getEnv().execute(cmd);
        interfaceObj = cmd.getInterface();
        assertEquals("newInterface", interfaceObj.getName());
        assertEquals("http://sap.com/xi/Purchasing", interfaceObj.getNamespace());
        assertFalse("Could not find service new SI", description.getInterface("newInterface").isEmpty());
        assertNotNull("Could not find service new SI", description.getInterface("newInterface").get(0));
        assertEquals(0, interfaceObj.getAllOperations().size());

        DeleteServiceInterfaceCommand deletecmd = new DeleteServiceInterfaceCommand(modelRoot, description, interfaceObj);
        modelRoot.getEnv().execute(deletecmd);
        assertTrue("newInterface is not removed", description.getInterface("newInterface").isEmpty());

        cmd = new AddServiceInterfaceCommand(modelRoot, description, "newInterface");
        modelRoot.getEnv().execute(cmd);
        interfaceObj = cmd.getInterface();
        assertEquals("newInterface", interfaceObj.getName());
        assertEquals("http://sap.com/xi/Purchasing", interfaceObj.getNamespace());
        List<IServiceInterface> interfaces = description.getInterface("newInterface");
        assertFalse(interfaces.isEmpty());
        assertNotNull("Could not find service new SI", interfaces.get(0));
        assertEquals(0, interfaceObj.getAllOperations().size());

        // Implement these methods
        /*
         * Exception error = null; try { wDesc.addExtension(null); } catch
         * (Exception e) { error = e; } assertNotNull(error); error = null; try
         * { wDesc.removeExtension(null); } catch (Exception e) { error = e; }
         * assertNotNull(error);
         */

        deletecmd = new DeleteServiceInterfaceCommand(modelRoot, description, description.getInterface(
                "PurchaseOrderConfirmation").get(0));
        modelRoot.getEnv().execute(deletecmd);
        assertTrue("PurchaseOrderConfirmation is not removed", description.getInterface("PurchaseOrderConfirmation").isEmpty());

        AddNewSchemaCommand newschemacmd = new AddNewSchemaCommand(modelRoot, "http://newNs");
        modelRoot.getEnv().execute(newschemacmd);
        schema = newschemacmd.getNewSchema();
        assertNotNull("Schema http://newNs is not added", schema);
        assertEquals("http://newNs", schema.getNamespace());
        assertEquals(description, schema.getRoot());
        assertEquals(description, schema.getParent());
        assertTrue("Schema location is not WSDL Location", schema.getLocation().endsWith("One.wsdl"));
        assertNotNull("new Schema could not be obtained", description.getSchema("http://newNs"));
        assertEquals(0, schema.getAllContainedTypes().size());

        RemoveSchemaCommand removeschemacmd = new RemoveSchemaCommand(modelRoot, schema);
        modelRoot.getEnv().execute(removeschemacmd);
        assertNotNull("new Schema is not deleted", description.getSchema("http://newNs"));

        newschemacmd = new AddNewSchemaCommand(modelRoot, "http://newNs");
        modelRoot.getEnv().execute(newschemacmd);
        schema = newschemacmd.getNewSchema();
        assertNotNull("Schema http://newNs is not added", schema);
        assertEquals("http://newNs", schema.getNamespace());
        assertEquals(description, schema.getRoot());
        assertEquals(description, schema.getParent());
        assertTrue("Schema location is not WSDL Location", schema.getLocation().endsWith("One.wsdl"));
        assertNotNull("new Schema could not be obtained", description.getSchema("http://newNs"));
        assertEquals(0, schema.getAllContainedTypes().size());

        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "Documentation");
        assertEquals("Documentation", description.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "NewDocumentation");
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "Two.wsdl");
        description = modelRoot.getDescription();
        modelRoot.getEnv().execute(new AddServiceInterfaceCommand(modelRoot, description, "newInterface"));
        modelRoot.getEnv().execute(
                new DeleteServiceInterfaceCommand(modelRoot, description, description.getInterface("PurchaseOrderConfirmation")
                        .get(0)));
        modelRoot.getEnv().execute(new AddNewSchemaCommand(modelRoot, "http://newNs"));

        // TODO Remove schema by name is not implemented by command
        schema = description.getSchema("http://sap.com/xi/SRM/Basis/Global")[0];
        removeSchema(modelRoot, schema);

        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "Documentation");
        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "NewDocumentation");
        ((Description) description).save();

        modelRoot = getModelRoot("Two.wsdl");
        description = modelRoot.getDescription();
        assertFalse("Could not find service new SI", description.getInterface("newInterface").isEmpty());
        assertNotNull("Could not find service new SI", description.getInterface("newInterface").get(0));
        assertNotNull("new Schema is not deleted", description.getSchema("http://newNs"));
        assertEquals("Schema http://sap.com/xi/SRM/Basis/Global is not deleted", 0, description
                .getSchema("http://sap.com/xi/SRM/Basis/Global").length);
        assertTrue("PurchaseOrderConfirmation is not removed", description.getInterface("PurchaseOrderConfirmation").isEmpty());
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("Documentation.wsdl", "Documentation.wsdl");
        description = modelRoot.getDescription();
        assertEquals("Documentation", description.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "NewDocumentation");
        assertEquals("NewDocumentation", description.getDocumentation());
        ((Description) description).save();

        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        assertEquals("NewDocumentation", description.getDocumentation());

        modelRoot = copyAndGetModelRoot("PrefixWSDL.wsd", "PrefixWSDL.wsdl");
        description = modelRoot.getDescription();
        setDocumentation(modelRoot, (AbstractWSDLComponent) description, "Documentation");
        assertEquals("Documentation", description.getDocumentation());

        setDescriptionNamespace(modelRoot, description, "http://example.org");
        assertEquals("http://example.org", description.getNamespace());
        ((Description) description).save();
        modelRoot = getModelRoot("PrefixWSDL.wsdl");
        description = modelRoot.getDescription();
        assertEquals("http://example.org", description.getNamespace());
    }

    @Test
    public void test_ServiceInterface() throws Exception {
        // check for referenced documents
        final String fileName = "Three.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        description = modelRoot.getDescription();

        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);
        operation = addOperation(modelRoot, interfaceObj, "newReqResponseOperation", OperationType.REQUEST_RESPONSE);
        assertNotNull("New operation 'newReqResponseOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newReqResponseOperation", operation.getName());
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(2, interfaceObj.getAllOperations().size());

        operation = addOperation(modelRoot, interfaceObj, "newAsyncOperation", OperationType.ASYNCHRONOUS);
        assertNotNull("New operation 'newAsyncOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newAsyncOperation", operation.getName());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(3, interfaceObj.getAllOperations().size());

        setDocumentation(modelRoot, (AbstractWSDLComponent) interfaceObj, "Documentation");
        assertEquals("Documentation", interfaceObj.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) interfaceObj, "NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());

        AddServiceInterfaceCommand addintfcmd = new AddServiceInterfaceCommand(modelRoot, description, "newInterface");
        modelRoot.getEnv().execute(addintfcmd);
        interfaceObj = addintfcmd.getInterface();
        operation = addOperation(modelRoot, interfaceObj, "newReqResponseOperation", OperationType.REQUEST_RESPONSE);
        assertNotNull("New operation 'newReqResponseOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newReqResponseOperation", operation.getName());
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(1, interfaceObj.getAllOperations().size());

        operation = addOperation(modelRoot, interfaceObj, "newAsyncOperation", OperationType.ASYNCHRONOUS);
        assertNotNull("New operation 'newAsyncOperation' could not be added successfully", operation);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertEquals("newAsyncOperation", operation.getName());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(operation.getParent(), interfaceObj);
        assertEquals(operation.getRoot(), description);
        assertEquals(2, interfaceObj.getAllOperations().size());

        setDocumentation(modelRoot, (AbstractWSDLComponent) interfaceObj, "Documentation");
        assertEquals("Documentation", interfaceObj.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) interfaceObj, "NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());

        DeleteOperationCommand deleteOperationCmd = new DeleteOperationCommand(modelRoot, interfaceObj, interfaceObj
                .getOperation("newReqResponseOperation").get(0));
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(deleteOperationCmd));
        assertTrue(interfaceObj.getOperation("newReqResponseOperation").isEmpty());

        deleteOperationCmd = new DeleteOperationCommand(modelRoot, interfaceObj, operation);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(deleteOperationCmd));
        assertTrue(interfaceObj.getOperation("newAsyncOperation").isEmpty());

        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        List<IServiceInterface> interfaces = description.getInterface("PurchaseOrderConfirmation");
        assertFalse(interfaces.isEmpty());
        interfaceObj = interfaces.get(0);
        assertEquals("Documentation\n\t\t", interfaceObj.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) interfaceObj, "NewDocumentation");
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());
        RenameServiceInterfaceCommand renameCmd = new RenameServiceInterfaceCommand(modelRoot, interfaceObj,
                "NewPurchaseOrderConfirmation");
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(renameCmd));
        assertEquals("NewPurchaseOrderConfirmation", interfaceObj.getName());
        assertFalse("Could not find interface after renaming", description.getInterface("NewPurchaseOrderConfirmation").isEmpty());
        assertNotNull("Could not find interface after renaming", description.getInterface("NewPurchaseOrderConfirmation").get(0));
        ((Description) description).save();

        // Test if changes are saved
        modelRoot = getModelRoot("Documentation.wsdl");
        description = modelRoot.getDescription();
        interfaceObj = description.getInterface("NewPurchaseOrderConfirmation").get(0);
        assertEquals("NewPurchaseOrderConfirmation", interfaceObj.getName());
        assertEquals("NewDocumentation", interfaceObj.getDocumentation());

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", fileName);
        description = modelRoot.getDescription();
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);

        operation = addOperation(modelRoot, interfaceObj, "NewOperation", OperationType.REQUEST_RESPONSE);
        assertEquals(2, interfaceObj.getAllOperations().size());

        deleteOperationCmd = new DeleteOperationCommand(modelRoot, interfaceObj, operation);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(deleteOperationCmd));
        assertEquals(1, interfaceObj.getAllOperations().size());

        deleteOperationCmd = new DeleteOperationCommand(modelRoot, interfaceObj, interfaceObj.getOperation(
                "PurchaseOrderConfirmationRequestResponse_In").get(0));
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(deleteOperationCmd));
        assertEquals(0, interfaceObj.getAllOperations().size());
    }

    @Test
    public void test_Operation() throws Exception {
        // check for referenced documents
        final String fileName = "TestOperation.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("Operations.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter parameter;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
        description = modelRoot.getDescription();

        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("SyncOperation").get(0);

        // Test parameter addition and deletion
        assertEquals(1, operation.getAllInputParameters().size());
        // AddInParameterCommand addInParameterCmd = new
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(2, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = addOutputParameter(modelRoot, operation, "Parameter2");
        assertEquals(2, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("Documentation", operation.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) operation, "NewDocumentation");
        assertEquals("NewDocumentation", operation.getDocumentation());
        faultObj = addFault(modelRoot, operation, "fault2");
        assertNotNull("New Fault is null", faultObj);
        assertEquals("fault2", faultObj.getName());

        operation = interfaceObj.getOperation("SyncOperationWithNoMessage").get(0);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = addOutputParameter(modelRoot, operation, "Parameter2");
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        setDocumentation(modelRoot, (AbstractWSDLComponent) operation, "NewDocumentation");
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithMissingMessages").get(0);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        parameter = addOutputParameter(modelRoot, operation, "Parameter2");
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter2", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("", operation.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) operation, "Documentation");
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithNoMessage").get(0);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        assertEquals("", operation.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) operation, "Documentation");
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithMissingMessage").get(0);
        // Test parameter addition and deletion
        assertEquals(0, operation.getAllInputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());
        renameOperation(modelRoot, operation, "NewASyncOperationWithMissingMessage");
        assertEquals("NewASyncOperationWithMissingMessage", operation.getName());
        assertFalse("Could not find operation after renaming", interfaceObj.getOperation("NewASyncOperationWithMissingMessage")
                .isEmpty());
        assertNotNull("Could not find operation after renaming", interfaceObj.getOperation("NewASyncOperationWithMissingMessage")
                .get(0));
        assertTrue("Old operation is not renamed", interfaceObj.getOperation("ASyncOperationWithMissingMessage").isEmpty());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput1").get(0);
        // Test adding input parameter
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter1");
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput2").get(0);
        // Test adding output parameter
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(0, operation.getAllOutputParameters().size());
        parameter = addOutputParameter(modelRoot, operation, "Parameter1");
        assertEquals(0, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals("Parameter1", parameter.getName());
        assertEquals(operation, parameter.getParent());
        assertEquals(description, parameter.getRoot());

        ((Description) description).save();

        modelRoot = getModelRoot(fileName);
        description = modelRoot.getDescription();

        // Test if changes are saved
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("SyncOperation").get(0);
        assertEquals(2, operation.getAllInputParameters().size());
        assertEquals(2, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithNoMessage").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("NewDocumentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("SyncOperationWithMissingMessages").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertEquals(1, operation.getAllOutputParameters().size());
        assertFalse(operation.getInputParameter("Parameter1").isEmpty());
        assertNotNull(operation.getInputParameter("Parameter1").get(0));
        assertFalse(operation.getOutputParameter("Parameter2").isEmpty());
        assertNotNull(operation.getOutputParameter("Parameter2").get(0));
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("ASyncOperationWithNoMessage").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertNotNull(operation.getInputParameter("Parameter1"));
        assertEquals("Documentation", operation.getDocumentation());

        operation = interfaceObj.getOperation("NewASyncOperationWithMissingMessage").get(0);
        assertNotNull("Operation NewASyncOperationWithMissingMessage could not be found", operation);

        operation = interfaceObj.getOperation("OperationWithNoInputOutput1").get(0);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(1, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("OperationWithNoInputOutput2").get(0);
        assertEquals(1, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllInputParameters().size());

        // Test Removal of parameters and faults
        operation = interfaceObj.getOperation("SyncOperation").get(0);
        assertEquals(2, operation.getAllInputParameters().size());
        parameter = addInputParameter(modelRoot, operation, "Parameter4");
        assertEquals(3, operation.getAllInputParameters().size());
        removeInputParameter(modelRoot, operation, parameter);
        assertEquals(2, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getInputParameter("Parameter4").isEmpty());
        removeInputParameter(modelRoot, operation, operation.getInputParameter("Parameter1").get(0));
        assertEquals(1, operation.getAllInputParameters().size());

        assertEquals(2, operation.getAllOutputParameters().size());
        parameter = addOutputParameter(modelRoot, operation, "Parameter4");
        assertEquals(3, operation.getAllOutputParameters().size());
        removeOutputParameter(modelRoot, operation, parameter);
        assertEquals(2, operation.getAllOutputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getOutputParameter("Parameter4").isEmpty());
        removeOutputParameter(modelRoot, operation, operation.getOutputParameter("Parameter2").get(0));
        assertEquals(1, operation.getAllOutputParameters().size());

        assertEquals(2, operation.getAllFaults().size());
        removeFault(modelRoot, operation, operation.getFault("fault2").get(0));
        assertTrue("fault2 is not deleted", operation.getFault("fault2").isEmpty());
        assertEquals(1, operation.getAllFaults().size());
        removeFault(modelRoot, operation, operation.getFault("fault1").get(0));
        assertTrue("fault1 is not deleted", operation.getFault("fault1").isEmpty());
        assertEquals(0, operation.getAllFaults().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        parameter = addInputParameter(modelRoot, operation, "parameter1");
        assertEquals(2, operation.getAllInputParameters().size());
        removeInputParameter(modelRoot, operation, parameter);
        assertEquals(1, operation.getAllInputParameters().size());
        removeInputParameter(modelRoot, operation, operation.getInputParameter("parameters").get(0));
        assertEquals(0, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("SyncOperation1").get(0);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        setOperationType(modelRoot, operation, OperationType.ASYNCHRONOUS);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());

        operation = interfaceObj.getOperation("ASyncOperation1").get(0);
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        setOperationType(modelRoot, operation, OperationType.REQUEST_RESPONSE);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());

        operation = interfaceObj.getOperation("SyncOperation2").get(0);
        Collection<IParameter> parameters = operation.getAllInputParameters();
        assertEquals(4, parameters.size());

        parameter = operation.getOutputParameter("Parameter1").get(0);
        removeOutputParameter(modelRoot, operation, parameter);
        parameters = operation.getAllOutputParameters();
        assertEquals(3, parameters.size());
        ((Description) description).save();

        modelRoot = getModelRoot(fileName);
        description = modelRoot.getDescription();

        // Test if removal changes are saved
        interfaceObj = description.getInterface("Operations").get(0);

        operation = interfaceObj.getOperation("SyncOperation").get(0);
        assertEquals(1, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getInputParameter("Parameter4").isEmpty());
        assertTrue("Parameter1 is not deleted", operation.getInputParameter("Parameter1").isEmpty());

        assertEquals(1, operation.getAllInputParameters().size());
        assertTrue("Parameter4 is not deleted", operation.getOutputParameter("Parameter4").isEmpty());
        assertTrue("Parameter2 is not deleted", operation.getOutputParameter("Parameter2").isEmpty());
        assertEquals(0, operation.getAllFaults().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        assertEquals(0, operation.getAllInputParameters().size());

        operation = interfaceObj.getOperation("SyncOperation1").get(0);
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());

        operation = interfaceObj.getOperation("ASyncOperation1").get(0);
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());

        operation = interfaceObj.getOperation("SyncOperation2").get(0);
        parameters = operation.getAllInputParameters();
        assertEquals(4, parameters.size());
        Iterator<IParameter> paramIter = parameters.iterator();
        assertEquals("Parameter1", paramIter.next().getName());
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());

        parameters = operation.getAllOutputParameters();
        assertEquals(3, parameters.size());
        paramIter = parameters.iterator();
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());
    }

    @Test
    public void test_Parameter() throws Exception {
        final String fileName = "TestParameter.wsdl";
        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "po.xsd", Document_FOLDER_NAME,
                this.getProject(), "po.xsd");
        assertTrue("File po.xsd could not be obtained", file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "example.xsd", Document_FOLDER_NAME,
                this.getProject(), "example.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "NullNamespace.xsd",
                Document_FOLDER_NAME, this.getProject(), "NullNamespace.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "ParameterType.xsd",
                Document_FOLDER_NAME, this.getProject(), "ParameterType.xsd");
        assertTrue("File example.xsd could not be obtained", file.exists());
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("ParameterType.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        IParameter parameter;

        description = modelRoot.getDescription();

        // check add and remove operations for old serviceinterface
        interfaceObj = description.getInterface("ServiceInterface").get(0);
        operation = interfaceObj.getOperation("Operation").get(0);
        parameter = operation.getInputParameter("parameters").get(0);

        setParameterName(modelRoot, parameter, "Parameter1");
        assertEquals("Parameter1", parameter.getName());
        assertEquals("", parameter.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) parameter, "Documentation");
        assertEquals("Documentation", parameter.getDocumentation());

        ISchema inlineSchema = description.getSchema("http://sap.com/xi/Purchasing")[0];

        // Set Type and Element from same WSDL
        assertEquals("NewOperation", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "BusinessTransactionDocumentID"), false);
        assertEquals("BusinessTransactionDocumentID", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "LanguageCode"), false);
        assertEquals("LanguageCode", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "BusinessTransactionDocumentID"), false);
        setParameterType(modelRoot, parameter, inlineSchema.getType(true, "PurchaseOrderConfirmationRequest"), false);
        assertEquals("PurchaseOrderConfirmationRequest", parameter.getType().getName());
        ISchema[] schemas = description.getSchema("http://www.example.org/ParameterType/");
        inlineSchema = schemas[0].getType(true, "NewOperation") == null ? schemas[1] : schemas[0];
        setParameterType(modelRoot, parameter, inlineSchema.getType(true, "NewOperation"), false);
        assertEquals("NewOperation", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "complexType"), false);
        assertEquals("complexType", parameter.getType().getName());

        SchemaNamespaceCondition condition = new SchemaNamespaceCondition("");

        // Set Type and Element from imported XSD
        condition.setNamespace("http://www.example.com/");
        inlineSchema = getSchema(description.getAllVisibleSchemas(), condition);
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(true, "purchaseOrder"), false);
        assertEquals("purchaseOrder", parameter.getType().getName());
        // Set Type from included XSD
        condition.setNamespace("http://www.example.com/IPO");

        ISchema[] ipoSchemas = description.getSchema("http://www.example.com/IPO");
        assertEquals(3, ipoSchemas.length);
        inlineSchema = getSchema1(ipoSchemas[0].getAllReferredSchemas(), condition);
        if (inlineSchema == null || inlineSchema.getType(false, "Address") == null) {
            inlineSchema = getSchema1(ipoSchemas[1].getAllReferredSchemas(), condition);
        }
        if (inlineSchema == null || inlineSchema.getType(false, "Address") == null) {
            inlineSchema = getSchema1(ipoSchemas[2].getAllReferredSchemas(), condition);
        }

        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "Address"), false);
        assertEquals("Address", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(true, "purchaseOrder"), false);
        assertEquals("purchaseOrder", parameter.getType().getName());
        // Set Type form XSD With Null namespace
        inlineSchema = description.getSchema("http://www.example.com/IPO/Include/NullNamespace")[0];
        condition.setNamespace(null);
        inlineSchema = getSchema1(inlineSchema.getAllReferredSchemas(), condition);

		setParameterType(modelRoot, parameter, inlineSchema.getType(false,
				"Address"), false);
		assertEquals("Address", parameter.getType().getName());
		setParameterType(modelRoot, parameter, inlineSchema.getType(true,
				"purchaseOrder"), false);
		assertEquals("purchaseOrder", parameter.getType().getName());

        // Set Type from XSD With Same Namespace
        condition.setNamespace("http://www.example.org/ParameterType/");
        inlineSchema = getSchema(description.getContainedSchemas(), condition);
        setParameterType(modelRoot, parameter, inlineSchema.getType(true, "NewOperation"), false);
        assertEquals("NewOperation", parameter.getType().getName());
        setParameterType(modelRoot, parameter, inlineSchema.getType(false, "complexType"), false);
        assertEquals("complexType", parameter.getType().getName());

        ResourceUtils.createFolderInProject(getProject(), "src");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported.xsd", SOURCE_FOLDER, this
                .getProject(), "Imported.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "SupplierInvoice.wsdl", SOURCE_FOLDER,
                this.getProject(), "SupplierInvoice.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestSingleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestSingleSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestMultipleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestMultipleSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestExternal.xsd",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestExternal.xsd");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestImportedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestImportedSchema.wsdl");
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestIncludedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestIncludedSchema.wsdl");

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "ParameterTypeTwo.wsdl");
        description = modelRoot.getDescription();
        interfaceObj = description.getInterface("PurchaseOrderConfirmation").get(0);
        operation = interfaceObj.getOperation("PurchaseOrderConfirmationRequestResponse_In").get(0);
        parameter = operation.getInputParameter("parameters").get(0); // What **
        // does
        // this
        // line
        // test?
    }

    @Test
    public void test_Fault() throws Exception {
        // check for referenced documents
        final String fileName = "TestOperationFault.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("Operations.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter parameter;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
        description = modelRoot.getDescription();

        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("TestFault").get(0);
        faultObj = operation.getFault("fault1").get(0);
        assertEquals("fault1", faultObj.getName());
        setFaultName(modelRoot, faultObj, "newFault");
        assertEquals("newFault", faultObj.getName());

        assertEquals("", faultObj.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) faultObj, "Documentation");
        assertEquals("Documentation", faultObj.getDocumentation());
        setDocumentation(modelRoot, (AbstractWSDLComponent) faultObj, "NewDocumentation");
        assertEquals("NewDocumentation", faultObj.getDocumentation());
        assertEquals(1, faultObj.getParameters().size());
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter1", operation);
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter2", operation);
        assertEquals(3, faultObj.getParameters().size());
        removeFaultParameter(modelRoot, faultObj, operation, faultObj.getParameter("parameters").get(0));
        ;
        assertEquals(2, faultObj.getParameters().size());
        removeFaultParameter(modelRoot, faultObj, operation, parameter);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("fault2").get(0);
        assertEquals(4, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithMissingMessage").get(0);
        assertEquals(0, faultObj.getParameters().size());
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter1", operation);
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter2", operation);
        assertEquals(2, faultObj.getParameters().size());
        removeFaultParameter(modelRoot, faultObj, operation, parameter);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithNoMessage").get(0);
        assertEquals(0, faultObj.getParameters().size());
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter1", operation);
        parameter = addFaultParameter(modelRoot, faultObj, "Parameter2", operation);
        assertEquals(2, faultObj.getParameters().size());
        removeFaultParameter(modelRoot, faultObj, operation, parameter);
        assertEquals(1, faultObj.getParameters().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        assertEquals(0, operation.getAllFaults().size());

        faultObj = addFault(modelRoot, operation, "newFault");
        addFaultParameter(modelRoot, faultObj, "Parameter1", operation);
        assertEquals(2, faultObj.getParameters().size());

        ((Description) description).save();

        modelRoot = getModelRoot("TestOperationFault.wsdl");
        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("TestFault").get(0);
        faultObj = operation.getFault("newFault").get(0);
        assertEquals(1, faultObj.getParameters().size());
        assertEquals("NewDocumentation", faultObj.getDocumentation());
        assertFalse("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").isEmpty());
        assertNotNull("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").get(0));

        faultObj = operation.getFault("fault2").get(0);
        assertEquals(4, faultObj.getParameters().size());
        Iterator<IParameter> paramIter = faultObj.getParameters().iterator();
        assertEquals("Parameter1", paramIter.next().getName());
        assertEquals("Parameter2", paramIter.next().getName());
        assertEquals("Parameter3", paramIter.next().getName());
        assertEquals("Parameter4", paramIter.next().getName());

        faultObj = operation.getFault("faultWithMissingMessage").get(0);
        assertEquals(1, faultObj.getParameters().size());

        faultObj = operation.getFault("faultWithNoMessage").get(0);
        assertEquals(1, faultObj.getParameters().size());

        operation = interfaceObj.getOperation("ASyncOperation").get(0);
        faultObj = operation.getFault("newFault").get(0);
        assertEquals(2, faultObj.getParameters().size());
        assertFalse("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").isEmpty());
        assertNotNull("Could not find parameter Parameter1", faultObj.getParameter("Parameter1").get(0));

    }

    @Test
    public void testRenameFault() throws Exception {

        final String fileName = "TestOperationFault.wsdl";
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("Operations.wsdl", fileName);
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface interfaceObj;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
        description = modelRoot.getDescription();

        interfaceObj = description.getInterface("Operations").get(0);
        operation = interfaceObj.getOperation("TestFault").get(0);
        faultObj = operation.getFault("fault1").get(0);
        assertEquals("fault1", faultObj.getName());
        setFaultName(modelRoot, faultObj, "newFault");
        assertEquals("newFault", faultObj.getName());
        EList eBindings = ((Definition) description.getComponent()).getEBindings();
        assertFalse(eBindings.isEmpty());
        for (Object binding : eBindings) {
            EList operations = ((Binding) binding).getEBindingOperations();
            for (Object op : operations) {
                EList faults = ((BindingOperation) op).getEBindingFaults();
                for (Object fault : faults) {
                    assertEquals(faultObj.getComponent(), ((BindingFault) fault).getFault());
                    EList elements = ((BindingFault) fault).getEExtensibilityElements();
                    for (Object soapFault : elements) {
                        assertEquals("newFault", ((SOAPFault) soapFault).getName());
                    }
                }
            }
        }
    }

    private ISchema getSchema(final Collection<ISchema> schemas, ICondition<ISchema> condition) {
        for (ISchema schema : schemas) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    private ISchema getSchema1(final Collection<ISchema> schemas, ICondition<ISchema> condition) {
        for (ISchema schema : schemas) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    interface ICondition<T> {
        boolean isSatisfied(T in);
    }

    private class SchemaNamespaceCondition implements ICondition<ISchema> {

        private String _namespace;

        public SchemaNamespaceCondition(final String namespace) {
            this._namespace = namespace;
        }

        public boolean isSatisfied(ISchema in) {
            if ((null == _namespace && null == in.getNamespace()) || (null != _namespace && _namespace.equals(in.getNamespace())))
                return true;
            return false;
        }

        public void setNamespace(final String namespace) {
            this._namespace = namespace;
        }

    }

    private IWsdlModelRoot copyAndGetModelRoot(final String fileName, final String targetName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, targetName);
    }

    private IWsdlModelRoot getModelRoot(final String fileName) throws Exception {
        final IFile file = (IFile) getProject().findMember(new Path(Document_FOLDER_NAME + IPath.SEPARATOR + fileName));
        assertTrue(file.exists());
        return getWSDLModelRoot(file);
    }

    private static IParameter addInputParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
        AddInParameterCommand cmd = new AddInParameterCommand(modelRoot, operation, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getParameter();
    }

    private static IParameter addOutputParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
        AddOutParameterCommand cmd = new AddOutParameterCommand(modelRoot, operation, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getParameter();
    }

    private static void removeInputParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, IParameter parameter) throws ExecutionException {
        DeleteInParameterCommand cmd = new DeleteInParameterCommand(modelRoot, operation, parameter);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void removeOutputParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, IParameter parameter) throws ExecutionException {
        DeleteOutParameterCommand cmd = new DeleteOutParameterCommand(modelRoot, operation, parameter);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault addFault(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
        AddFaultCommand cmd = new AddFaultCommand(modelRoot, operation, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getFault();
    }

    private static void removeFault(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault fault) throws ExecutionException {
        DeleteFaultCommand cmd = new DeleteFaultCommand(modelRoot, operation, fault);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void renameOperation(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
        RenameOperationCommand cmd = new RenameOperationCommand(modelRoot, operation, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setOperationType(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, OperationType operationType)
            throws ExecutionException {
        ChangeOperationTypeCommand cmd = new ChangeOperationTypeCommand(modelRoot, operation, operationType, false);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setParameterName(IWsdlModelRoot modelRoot, IParameter parameter, String name) throws ExecutionException {
        RenameParameterCommand cmd = new RenameParameterCommand(modelRoot, parameter, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setParameterType(IWsdlModelRoot modelRoot, IParameter parameter, IType newType, boolean createWrapper)
            throws ExecutionException {
        SetParameterTypeCommand cmd = new SetParameterTypeCommand(parameter, (AbstractType) newType);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setFaultName(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault fault,
            String name) throws ExecutionException {
        RenameFaultCommand cmd = new RenameFaultCommand(modelRoot, fault, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static IParameter addFaultParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault fault, String name,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation) throws ExecutionException {
        AddFaultParameterCommand cmd = new AddFaultParameterCommand(modelRoot, fault, name, operation);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getParameter();
    }

    private static void removeFaultParameter(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault fault,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, IParameter parameter) throws ExecutionException {
        DeleteFaultParameterCommand cmd = new DeleteFaultParameterCommand(modelRoot, fault, parameter, operation);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setDescriptionNamespace(IWsdlModelRoot modelRoot,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description, String namespace) throws ExecutionException {
        ChangeDefinitionTNSCompositeCommand cmd = new ChangeDefinitionTNSCompositeCommand(modelRoot, description, namespace);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void removeSchema(IWsdlModelRoot modelRoot, ISchema schema) throws ExecutionException {
        RemoveSchemaCommand cmd = new RemoveSchemaCommand(modelRoot, schema);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static IOperation addOperation(IWsdlModelRoot modelRoot, IServiceInterface serviceInterface, String name,
            OperationType operationType) throws ExecutionException {
        AddOperationCommand cmd = new AddOperationCommand(modelRoot, serviceInterface, name, operationType);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getOperation();
    }

    private static void setDocumentation(IWsdlModelRoot modelRoot, AbstractWSDLComponent modelObject, String documentation)
            throws ExecutionException {
        SetDocumentationCommand cmd = new SetDocumentationCommand(modelRoot, modelObject,
                modelObject.getComponent().getElement(), documentation);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        validateDocumentationElement(modelObject, true);
    }

    private static void validateDocumentationElement(IModelObject modelObject, boolean hasDocumentation) {
        NodeList childNodes = ((WSDLElement) modelObject.getComponent()).getElement().getChildNodes();
        int found = 0;
        for (int ndx = 0; ndx < childNodes.getLength(); ndx++) {
            Node node = childNodes.item(ndx);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && ("wsdl:documentation".equals(node.getNodeName()) || "documentation".equals(node.getNodeName()))) {
                found++;
            }
        }

        if (!hasDocumentation && found > 0) {
            Assert.fail("No documentation nodes were expected, but " + found + " were found");
        }

        if (hasDocumentation && found > 1) {
            Assert.fail("More than one documentation node was found: " + found);
        }

        if (hasDocumentation && found == 0) {
            Assert.fail("Documentation node was not found");
        }
    }
}
