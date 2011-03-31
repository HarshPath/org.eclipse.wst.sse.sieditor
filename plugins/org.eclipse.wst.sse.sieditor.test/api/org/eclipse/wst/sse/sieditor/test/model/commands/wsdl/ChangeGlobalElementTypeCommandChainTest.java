package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandChainTest;

public class ChangeGlobalElementTypeCommandChainTest extends AbstractCommandChainTest {

    private static final String HTTP_TEST_NAMESPACE = "http://test_namespace";
    private static final String NEW_ELEMENT = "NewElement";
    private AddNewSchemaCommand addSchemaCommand;
    private AddStructureTypeCommand addGlobalElementCommand;
    private SetStructureTypeCommand setStructureTypeCommand;

    @Override
    protected String getWsdlFilename() {
        return "NewWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected AbstractNotificationOperation getNextOperation(IWsdlModelRoot modelRoot) throws Exception {
        if (addSchemaCommand == null) {
            addSchemaCommand = new AddNewSchemaCommand(modelRoot, HTTP_TEST_NAMESPACE);
            return addSchemaCommand;
        }

        if (addGlobalElementCommand == null) {
            ISchema[] schema = modelRoot.getDescription().getSchema(HTTP_TEST_NAMESPACE);
            assertNotNull(schema);
            assertEquals("more than one schema was found for the given namespace", 1, schema.length);

            addGlobalElementCommand = new AddStructureTypeCommand((IXSDModelRoot) schema[0].getModelRoot(), schema[0],
                    "Add Element", NEW_ELEMENT, true, null);
            return addGlobalElementCommand;
        }

        if (setStructureTypeCommand == null) {
            ISchema[] schema = modelRoot.getDescription().getSchema(HTTP_TEST_NAMESPACE);
            assertNotNull(schema);
            assertEquals("more than one schema was found for the given namespace", 1, schema.length);

            IStructureType element = (IStructureType) schema[0].getType(true, NEW_ELEMENT);
            assertNotNull("test element was not found in the new namespace", element);

            setStructureTypeCommand = new SetStructureTypeCommand(schema[0].getModelRoot(), element, Schema.getSchemaForSchema()
                    .getType(false, "boolean"));
            return setStructureTypeCommand;
        }

        return null;
    }

    @Override
    protected void assertPostOperationRedoState(IUndoableOperation operation, IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostOperationUndoState(IUndoableOperation operation, IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
        assertEquals("only one namespace is expected upon redo", 1, modelRoot.getDescription().getComponent().getETypes()
                .getSchemas().size());
        assertEquals("test schema is present in the initial document state", 0,
                modelRoot.getDescription().getSchema(HTTP_TEST_NAMESPACE).length);
    }

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
        assertEquals("two namespaces are expected after the command chain completion", 2, modelRoot.getDescription()
                .getComponent().getETypes().getSchemas().size());

        ISchema[] testSchemas = modelRoot.getDescription().getSchema(HTTP_TEST_NAMESPACE);
        assertEquals("test schema is not present after the command chain completion", 1, testSchemas.length);

        IStructureType element = (IStructureType) testSchemas[0].getType(true, NEW_ELEMENT);
        assertNotNull("test global element was not added", element);
        assertEquals("boolean", element.getType().getName());
    }

}
