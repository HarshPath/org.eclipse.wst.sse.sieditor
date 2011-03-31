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
package org.eclipse.wst.sse.sieditor.test.smoke;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.model.Constants;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RemoveSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.FacetsCommandFactory;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementNillableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementOccurences;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractXSDComponent;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;



@SuppressWarnings("nls")
public class SIEditorSmokeTest extends SIEditorBaseTest {
	
	 protected static final String WSDL_FILE_NAME = "ECC_CUSTOMER002QR.wsdl";
	 protected static final int NUMBER_OF_INTERFACES_IN_FILE = 1;
	 protected static final String INTERFACE_NAME = "CustomerSimpleByNameAndAddressQueryResponse_In";
	 protected static final String CREATED_INTERFACE_NAME = "SmokeInterface";
	 protected static final int NUMBER_OF_CONTAINED_SCHEMAS = 2;
	 protected static final int NUMBER_OF_VISIBLE_SCHEMAS = 3;
	 protected static final String SCHEMA_FOR_SCHEMANS = "http://www.w3.org/2001/XMLSchema";
	 protected static final String[] SCHEMA_NAMESPACES = new String[] { "http://sap.com/xi/APPL/SE/Global",
     "http://sap.com/xi/SAPGlobal20/Global" };
	 protected static final int NUMBER_OF_REFERENCED_SERVICES = 0;
	 protected static final String DESCRIPTION_LOCATION = "/SIEditorSmokeTest/data/ECC_CUSTOMER002QR.wsdl";
	 protected static final String DESCRIPTION_NAMESPACE = "http://sap.com/xi/APPL/SE/Global";
	 protected static final int NUMBER_OF_OPERATIONS = 1;
	 protected static final String TESTED_OPERATION = "CustomerSimpleByNameAndAddressQueryResponse_In";
	 protected static final OperationType OPERATION_TYPE = OperationType.REQUEST_RESPONSE;
	 protected static final int NUMBER_OF_INPUT_PARAMETERS = 1;
	 protected static final String TESTED_INPUT_PARAMETER = "parameters";
	 protected static final int NUMBER_OF_OUTPUT_PARAMETERS = 1;
	 protected static final String TESTED_OUTPUT_PARAMETER = "parameters";
	 protected static final int NUMBER_OF_FAULTS = 1;
	 protected static final String TESTED_FAULT = "exception00";
	 protected static final int NUMBER_OF_PARAMETERS_IN_FAULT = 1;
	 protected static final String TESTED_PARAMETER_IN_FAULT = "exception00";
	 protected static final String OPERATION_NAME = "SmokeReqResponseOperation";
	 protected static final String FAULT_NAME = "Smokefault";
	 protected static final String IN_PARAM_NAME = "SmokeInputParameter";
	 protected static final String OUT_PARAM_NAME = "SmokeOutputParameter";
	 protected static final String ELEMENT_NAME = "SmokeElement";
	 protected static final String SIMPLE_TYPE_NAME = "SmokeSimpleType";
	 protected static final String STRUCTURE_TYPE_NAME = "SmokeStructureType";
	 
	protected String getProjectName() {
        return "SIEditorSmokeTest";
    }
	
	@Test
    public void test_ModelAccess() throws Exception {
        final IWsdlModelRoot root = copyAndGetModelRoot(WSDL_FILE_NAME);
        assertNotNull("Model could not be acquired", root);
        assertNotNull("Description could not be acquired", root.getDescription());
	}
   
    @Test
    public void test_Description() throws Exception {
            IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

            IDescription description = root.getDescription();

            String nameSpace = description.getNamespace();
            assertEquals(DESCRIPTION_NAMESPACE, nameSpace);

            Collection<IServiceInterface> interfaces = description.getAllInterfaces();
            assertTrue(interfaces.size() + " interfaces returned instead of " + NUMBER_OF_INTERFACES_IN_FILE,
                    NUMBER_OF_INTERFACES_IN_FILE == interfaces.size()); // @Test-0001
            interfaces = description.getAllInterfaces();
            assertTrue(interfaces.size() + " interfaces returned instead of " + NUMBER_OF_INTERFACES_IN_FILE,
                    NUMBER_OF_INTERFACES_IN_FILE == interfaces.size()); // @Test-0001

            assertFalse(description.getInterface(INTERFACE_NAME).isEmpty());
            IServiceInterface service1 = description.getInterface(INTERFACE_NAME).get(0);
            assertNotNull("Null returned for getInterface", service1); // @Test-0002

            assertTrue("Interface exists with name foo", description.getInterface("foo").isEmpty()); // @Test-0002

            Collection<ISchema> schemas = description.getContainedSchemas();
            assertTrue(schemas.size() + " Contained schemas returned instead of " + NUMBER_OF_CONTAINED_SCHEMAS,
                    NUMBER_OF_CONTAINED_SCHEMAS == schemas.size()); // @Test-0003

            Collection<ISchema> visibleSchemas = description.getAllVisibleSchemas();
            assertTrue(visibleSchemas.size() + " Visible Schemas returned instead of " + NUMBER_OF_VISIBLE_SCHEMAS,
                    NUMBER_OF_VISIBLE_SCHEMAS == visibleSchemas.size()); // @Test-0004

            boolean exists = false;
            for(ISchema schema : visibleSchemas) {
            	if(SCHEMA_FOR_SCHEMANS.equals(schema.getNamespace())) {
            		exists = true;
            		break;
            	}
            }
       assertTrue("Return Visible schema is not schema for schema", exists);

            for (String schemans : SCHEMA_NAMESPACES) {
                ISchema[] schemaArray = description.getSchema(schemans);
                assertTrue("No schema returned for namespace '" + schemans + "'", 1 == schemaArray.length); // @Test-0005
                assertEquals("Wrong Schema returned for namespace '" + schemans + "'", schemans, schemaArray[0].getNamespace());
            }

            ISchema[] schemaArray = description.getSchema("foo");
            assertTrue("Schemas exists for namespace 'foo'", 0 == schemaArray.length); // @Test-0005

            Collection<IDescription> referencedServices = description.getReferencedServices();
            assertTrue("Referenced services exist", NUMBER_OF_REFERENCED_SERVICES == referencedServices.size()); // @Test-0006

            String location = description.getLocation();
            assertTrue(location.endsWith(DESCRIPTION_LOCATION)); // @Test-007

            String namespace = description.getNamespace();
            assertEquals("'" + namespace + "' returned as namespace instead of '" + DESCRIPTION_NAMESPACE + "'",
                    DESCRIPTION_NAMESPACE, namespace); // @Test-0008
            assertTrue(description.getParent() == null);
            assertTrue(description.getRoot() == null);
    }
            
            @Test
            public void test_ServiceInterface() throws Exception {
                IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

                IDescription description = root.getDescription();
                assertFalse("No interface returned for name " + INTERFACE_NAME + " is  null", description.getInterface(INTERFACE_NAME).isEmpty());
                IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
                assertTrue("Wrong interface returned for name " + INTERFACE_NAME, INTERFACE_NAME.equals(service.getName()));

                Collection<IOperation> operations = service.getAllOperations();
                assertEquals(operations.size() + " operations returned instead of " + NUMBER_OF_OPERATIONS, NUMBER_OF_OPERATIONS,
                        operations.size()); // @Test-0009
                operations = service.getAllOperations();
                assertEquals(operations.size() + " operations returned instead of " + NUMBER_OF_OPERATIONS, NUMBER_OF_OPERATIONS,
                        operations.size()); // @Test-0009

                List<IOperation> ops = service.getOperation(TESTED_OPERATION);
                assertFalse(ops.isEmpty());
                IOperation operation = ops.get(0);
                assertNotNull("No operation returned for name " + TESTED_OPERATION, operation); // @Test-0010
                assertTrue("Wrong Operaiton returned for name " + TESTED_OPERATION, TESTED_OPERATION.equals(operation.getName()));

                assertTrue(description == service.getParent());
                assertTrue(description == service.getRoot());
            }
                
                @Test
                public void test_Operation() throws Exception {
                    IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);

                    IDescription description = root.getDescription();
                    IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
                    IOperation operation = service.getOperation(TESTED_OPERATION).get(0);

                    OperationType operationType = operation.getOperationStyle();
                    assertTrue("Returned operation is not Request-Response", OPERATION_TYPE == operationType); // @Test-0012

                    Collection<IParameter> inputParameters = operation.getAllInputParameters();
                    assertEquals(inputParameters.size() + " in parameters returned instead of " + NUMBER_OF_INPUT_PARAMETERS,
                            NUMBER_OF_INPUT_PARAMETERS, inputParameters.size()); // @Test-0013
                    inputParameters = operation.getAllInputParameters();
                    assertEquals(inputParameters.size() + " in parameters returned instead of " + NUMBER_OF_INPUT_PARAMETERS,
                            NUMBER_OF_INPUT_PARAMETERS, inputParameters.size()); // @Test-0013

                    List<IParameter> params = operation.getInputParameter(TESTED_INPUT_PARAMETER);
                    assertFalse(params.isEmpty());
                    IParameter inputParameter = params.get(0);
                    assertNotNull("No input parameter returned for name " + TESTED_INPUT_PARAMETER, inputParameter); // @Test-0014
                    assertTrue("Wrong in parameter returned for name " + TESTED_INPUT_PARAMETER, TESTED_INPUT_PARAMETER.equals(inputParameter
                            .getName()));
                    assertTrue(operation == inputParameter.getParent());
                    assertTrue(description == inputParameter.getRoot());

                    Collection<IParameter> outputParameters = operation.getAllOutputParameters();
                    assertEquals(outputParameters.size() + " out parameters returned instead of " + NUMBER_OF_OUTPUT_PARAMETERS,
                            NUMBER_OF_OUTPUT_PARAMETERS, outputParameters.size()); // @Test-0015
                    outputParameters = operation.getAllOutputParameters();
                    assertEquals(outputParameters.size() + " out parameters returned instead of " + NUMBER_OF_OUTPUT_PARAMETERS,
                            NUMBER_OF_OUTPUT_PARAMETERS, outputParameters.size()); // @Test-0015

                    params = operation.getOutputParameter(TESTED_OUTPUT_PARAMETER);
                    assertFalse(params.isEmpty());
                    IParameter outputParameter = params.get(0);
                    assertNotNull("No out parameter returned for name " + TESTED_OUTPUT_PARAMETER, outputParameter); // @Test-0016
                    assertTrue("Wrong out parameter returned for name " + TESTED_OUTPUT_PARAMETER, TESTED_OUTPUT_PARAMETER
                            .equals(outputParameter.getName()));
                    assertTrue(operation == outputParameter.getParent());
                    assertTrue(description == outputParameter.getRoot());

                    Collection<IFault> faults = operation.getAllFaults();
                    assertEquals(faults.size() + " faults returned instead of " + NUMBER_OF_FAULTS, NUMBER_OF_FAULTS, faults.size()); // @Test-0017
                    faults = operation.getAllFaults();
                    assertEquals(faults.size() + " faults returned instead of " + NUMBER_OF_FAULTS, NUMBER_OF_FAULTS, faults.size()); // @Test-0017

                    List<IFault> flts = operation.getFault(TESTED_FAULT);
                    assertFalse(flts.isEmpty());
                    IFault fault = flts.get(0);
                    assertNotNull("No Fault returned for name " + TESTED_FAULT, fault); // @Test-0018
                    assertTrue("Wrong fault returned for name " + TESTED_FAULT, TESTED_FAULT.equals(fault.getName()));
                    assertTrue(operation == fault.getParent());
                    assertTrue(description == fault.getRoot());

                    assertTrue(service == operation.getParent());
                    assertTrue(description == operation.getRoot());
             }
                
                
                @Test
                public void test_OperationParameter() throws Exception {
                    IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);
                    IDescription description = root.getDescription();

                    IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
                    IOperation operation = service.getOperation(TESTED_OPERATION).get(0);
                    IParameter parameter = operation.getInputParameter(TESTED_INPUT_PARAMETER).get(0);

                    String name = parameter.getName();
                    assertNotNull("Null returned for name", name);
                    assertTrue("Wrong name returned for parameter " + TESTED_INPUT_PARAMETER, TESTED_INPUT_PARAMETER.equals(name));

                    IType type = parameter.getType();
                    assertNotNull("Null returned for type" + type); // @Test-0019
                    assertTrue("Wrong type returned for parameter", "CustomerSimpleByNameAndAddressQuery_sync".equals(type.getName()));
                    assertTrue("Type must be StructureType", type instanceof StructureType);

                    parameter = operation.getOutputParameter(TESTED_OUTPUT_PARAMETER).get(0);

                    name = parameter.getName();
                    assertNotNull("Null returned for name", name);
                    assertTrue("Wrong name returned for parameter " + TESTED_OUTPUT_PARAMETER, TESTED_OUTPUT_PARAMETER.equals(name));

                    type = parameter.getType();
                    assertNotNull("Null returned for type" + type); // @Test-0019
                    assertTrue("Wrong type returned for parameter", "CustomerSimpleByNameAndAddressResponse_sync".equals(type.getName()));
                    assertTrue("Type must be StructureType", type instanceof StructureType);
                    assertTrue("Returned Type is not an Element", ((StructureType) type).isElement());
                }
                
                @Test
                public void test_OperationFault() throws Exception {
                    IWsdlModelRoot root = getModelRoot(WSDL_FILE_NAME);
                    IDescription description = root.getDescription();
                    IServiceInterface service = description.getInterface(INTERFACE_NAME).get(0);
                    IOperation operation = service.getOperation(TESTED_OPERATION).get(0);
                    IFault fault = operation.getFault(TESTED_FAULT).get(0);
                    final String name = fault.getName();
                    assertNotNull("Null returned for name", name);
                    assertEquals("Wrong fault name returned ", TESTED_FAULT, name);

                    Collection<IParameter> parameters = fault.getParameters();
                    assertEquals(parameters.size() + " Fault parameters returned instead of " + NUMBER_OF_PARAMETERS_IN_FAULT,
                            NUMBER_OF_PARAMETERS_IN_FAULT, parameters.size()); // @Test-0020
                    IParameter parameter = fault.getParameter(TESTED_PARAMETER_IN_FAULT).get(0);
                    parameters = fault.getParameters();
                    assertEquals(parameters.size() + " Fault parameters returned instead of " + NUMBER_OF_PARAMETERS_IN_FAULT,
                            NUMBER_OF_PARAMETERS_IN_FAULT, parameters.size()); // @Test-0020
                    assertTrue(operation == fault.getParent());
                    assertTrue(description == fault.getRoot());

                    assertNotNull("No parameter returned for name " + TESTED_PARAMETER_IN_FAULT, parameter); // @Test-0021
                    assertEquals("Wrong parameter returned for name " + TESTED_PARAMETER_IN_FAULT, TESTED_PARAMETER_IN_FAULT, parameter
                            .getName());
                }
        
                @Test
                public void test_AddNewServiceInterface() throws Exception {

                	 IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                     IDescription description = modelRoot.getDescription();
                     IServiceInterface interfaceObj = description.getInterface(INTERFACE_NAME).get(0);                                	
                     addSI(modelRoot, description, CREATED_INTERFACE_NAME);
                    ((Description) description).save();
                    assertEquals("http://sap.com/xi/APPL/SE/Global", interfaceObj.getNamespace());
                    assertFalse("Could not find service new SI", description.getInterface(CREATED_INTERFACE_NAME).isEmpty());                   
                }
                
                @Test
                public void test_AddNewOperation() throws Exception {

                	 org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
                	 IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                     IDescription description = modelRoot.getDescription();
                     IServiceInterface interfaceObj =description.getInterface(CREATED_INTERFACE_NAME).get(0); 
                     operation = addOperation(modelRoot, interfaceObj, OPERATION_NAME, OperationType.REQUEST_RESPONSE);
                     ((Description) description).save();
                     assertNotNull("New operation 'SkomeReqResponseOperation' could not be added successfully", operation);
                     assertEquals(1, operation.getAllInputParameters().size());
                     assertEquals(1, operation.getAllOutputParameters().size());
                     assertEquals(0, operation.getAllFaults().size());
                     assertEquals(OPERATION_NAME, operation.getName());
                     assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
                     assertEquals(operation.getParent(), interfaceObj);
                     assertEquals(operation.getRoot(), description);
                     assertEquals(1, interfaceObj.getAllOperations().size());                   
                                   
                }
                
                @Test
                public void test_AddNewParameters() throws Exception {
                
                	org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter parameter;
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault faultObj;
                    
           	 	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                IDescription description = modelRoot.getDescription();
                IServiceInterface interfaceObj = description.getInterface(CREATED_INTERFACE_NAME).get(0);
                operation = interfaceObj.getOperation(OPERATION_NAME).get(0);
                
                parameter = addInputParameter(modelRoot, operation, IN_PARAM_NAME);
                ((Description) description).save();
                assertEquals(2, operation.getAllInputParameters().size());
                assertEquals(IN_PARAM_NAME, parameter.getName());
                assertEquals(operation, parameter.getParent());
                assertEquals(description, parameter.getRoot());
                parameter = addOutputParameter(modelRoot, operation, OUT_PARAM_NAME);
                ((Description) description).save();
                assertEquals(2, operation.getAllOutputParameters().size());
                assertEquals(OUT_PARAM_NAME, parameter.getName());
                assertEquals(operation, parameter.getParent());
                assertEquals(description, parameter.getRoot());
                faultObj = addFault(modelRoot, operation, FAULT_NAME);
                ((Description) description).save();
                assertNotNull("New Fault is null", faultObj);
                assertEquals(FAULT_NAME, faultObj.getName());      
                
                }
                
                @Test
                public void test_AddNewNamespace() throws Exception {
                	
                	 IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                	 org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                     org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;     
                     description = modelRoot.getDescription();
                     addNamespace(modelRoot, "http://SmokeNamespace");
                     ((Description) description).save();
                     schema = description.getSchema("http://SmokeNamespace")[0];
                     assertEquals("http://SmokeNamespace", schema.getNamespace());
                     
                }
                
                @Test
                public void testAddNewSimpleType() throws Exception {
                	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
                    description = modelRoot.getDescription();
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType simpleType;
                    schema = description.getSchema("http://SmokeNamespace")[0];
                    assertNotNull("Could not find schema", schema);
                    simpleType = addSimpleType(((AbstractXSDComponent)schema).getModelRoot(), schema, SIMPLE_TYPE_NAME);
                    ((Description) description).save();
                    assertNotNull("Could not find Newly added type SmokeSimpleType", schema.getType(false, SIMPLE_TYPE_NAME));                    
                    setMinLength(modelRoot, simpleType, "1");
                    setMaxLength(modelRoot, simpleType, "10");
                    addEnumeration(modelRoot, simpleType, "value8");
                    addPattern(modelRoot, simpleType, "smoke*");
                    ((Description) description).save();
                    assertEquals("1", simpleType.getMinLength());
                    assertEquals("10", simpleType.getMaxLength());
                    assertEquals("value8", simpleType.getEnumerations()[0].getValue());
                    assertEquals(1, simpleType.getPatterns().length);
                    assertEquals("smoke*", simpleType.getPatterns()[0].getValue());
                }
                
                @Test
                public void testAddComplexType() throws Exception {
                	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
                    description = modelRoot.getDescription();	
                    schema = description.getSchema("http://SmokeNamespace")[0];                   
                    addStructureType(((AbstractXSDComponent)schema).getModelRoot(), schema, STRUCTURE_TYPE_NAME, false, null);
                    ((Description) description).save();
                    assertNotNull("Could not find Newly added type SmokeStructureType", schema.getType(false, "SmokeStructureType"));
                }
                
                @Test
                public void testAddElement() throws Exception {
                	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
                    description = modelRoot.getDescription();	
                    schema = description.getSchema("http://SmokeNamespace")[0];
                    structureType = (IStructureType) schema.getType(false, STRUCTURE_TYPE_NAME);
                    IElement element = addElement(modelRoot, structureType, ELEMENT_NAME);
                    ((Description) description).save();
                    assertEquals(1, structureType.getAllElements().size());
                    setMinOccurs(modelRoot, element, 2);
                    ((Description) description).save();  
                    assertEquals(2, element.getMinOccurs());
                    assertEquals(1, element.getMaxOccurs());
                    setMaxOccurs(modelRoot, element, 4);
                    ((Description) description).save();  
                    assertEquals(4, element.getMaxOccurs());
                    assertEquals(false, element.getNillable());
                    setNillable(modelRoot, element, true);
                    ((Description) description).save();  
                    assertEquals(true, element.getNillable());     
        }
                
                @Test
                public void testAddAttribute() throws Exception {
                	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
                    description = modelRoot.getDescription();	
                    schema = description.getSchema("http://SmokeNamespace")[0];
                    structureType = (IStructureType) schema.getType(false, STRUCTURE_TYPE_NAME);   
                    IXSDModelRoot xsdmodelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
                    addAttribute(xsdmodelRoot, structureType, "SmokeAttribute");
                    ((Description) description).save();
                    assertEquals(2, structureType.getAllElements().size());         
                }
                
                @Test
                public void testDeleteAllCreatedObjects() throws Exception {                	
                	IWsdlModelRoot modelRoot = getModelRoot(WSDL_FILE_NAME);
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
                    org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault fault;
                    org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation;
                    description = modelRoot.getDescription();	  
                    IServiceInterface service = description.getInterface(CREATED_INTERFACE_NAME).get(0);
                    schema = description.getSchema("http://SmokeNamespace")[0];
                    structureType = (IStructureType) schema.getType(false, STRUCTURE_TYPE_NAME);
                    operation = service.getOperation(OPERATION_NAME).get(0);
                    List<IFault> flts = operation.getFault(FAULT_NAME);
                    assertFalse(flts.isEmpty());
                    fault = flts.get(0);
                    List<IParameter> prms = operation.getOutputParameter(OUT_PARAM_NAME);
                    assertFalse(prms.isEmpty());
                    IParameter parameterOutput = prms.get(0);
                    prms = operation.getInputParameter(IN_PARAM_NAME);
                    assertFalse(prms.isEmpty());
                    IParameter parameterInput = prms.get(0);
                   
                    
                    removeElement(modelRoot, structureType, ELEMENT_NAME);
                    ((Description) description).save();
                    
                    removeNamespace(modelRoot, schema);
                    ((Description) description).save();
                    
                    removeFault(modelRoot, operation, fault);
                    ((Description) description).save();
                    
                    removeOutputParameter(modelRoot, operation, parameterOutput);
                    ((Description) description).save();
                    
                    removeInputParameter(modelRoot, operation, parameterInput);
                    ((Description) description).save();
                    
                    removeOperation(modelRoot, service, operation);
                    ((Description) description).save();
                    
                    removeSI(modelRoot, description, CREATED_INTERFACE_NAME);
                    ((Description) description).save();
                    
                }
                
                
                
        private IWsdlModelRoot copyAndGetModelRoot(String fileName) throws Exception {
        	final String fullPath = Constants.DATA_PUBLIC_SELF_MIX2 + fileName;
        	final IWsdlModelRoot modelRoot = getWSDLModelRoot(fullPath, fileName);
        	assertNotNull("Could not Acquire Model Root for " + fullPath, modelRoot);
        	return modelRoot;
    	}
	
		private IWsdlModelRoot getModelRoot (String fileName) throws Exception {
	
			IFile file = this.getProject().getFolder("data").getFile(fileName);
			final IWsdlModelRoot modelRoot = getWSDLModelRoot(file);
			assertNotNull("Could not Acquire Model Root for " + file, modelRoot);
			return modelRoot;
		}
		
		private static void addSI(IWsdlModelRoot modelRoot, IDescription description, String name) throws ExecutionException {
			AddServiceInterfaceCommand cmd = new AddServiceInterfaceCommand(modelRoot, description, name);
			assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
		}
		
		private static void removeSI(IWsdlModelRoot modelRoot, IDescription description, String name) throws ExecutionException {
			List<IServiceInterface> interfaces = description.getInterface(name);
                        IServiceInterface service = interfaces.isEmpty()? null: interfaces.get(0);
			DeleteServiceInterfaceCommand cmd = new DeleteServiceInterfaceCommand(modelRoot, description, service);
			assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
		}
	
		private static IOperation addOperation(IWsdlModelRoot modelRoot, IServiceInterface serviceInterface, String name, OperationType operationType) throws ExecutionException {
			AddOperationCommand cmd = new AddOperationCommand(modelRoot, serviceInterface, name, operationType);
			assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    	return cmd.getOperation();
		}
		
		private static void removeOperation(IWsdlModelRoot modelRoot, IServiceInterface serviceInterface, IOperation operation) throws ExecutionException {
			DeleteOperationCommand cmd = new DeleteOperationCommand(modelRoot, serviceInterface, operation);
			assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
  
		}
	
	 	private static IParameter addInputParameter(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
	    	AddInParameterCommand cmd = new AddInParameterCommand(modelRoot, operation, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getParameter();
	    }
	 
	    private static void removeInputParameter(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, IParameter parameter) throws ExecutionException {
	    	DeleteInParameterCommand cmd = new DeleteInParameterCommand(modelRoot, operation, parameter);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	 	private static IParameter addOutputParameter(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
	    	AddOutParameterCommand cmd = new AddOutParameterCommand(modelRoot, operation, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getParameter();
	    }
	    
	 	private static void removeOutputParameter(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, IParameter parameter) throws ExecutionException {
	    	DeleteOutParameterCommand cmd = new DeleteOutParameterCommand(modelRoot, operation, parameter);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	 	
	    private static IFault addFault(IWsdlModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation operation, String name) throws ExecutionException {
	    	AddFaultCommand cmd = new AddFaultCommand(modelRoot, operation, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getFault();
	    }   
	    
	    private static void removeFault(IWsdlModelRoot modelRoot, IOperation operation, IFault fault) throws ExecutionException {	    	
	    	DeleteFaultCommand cmd = new DeleteFaultCommand(modelRoot, operation, fault);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static void addNamespace(IWsdlModelRoot modelRoot, String namespace) throws ExecutionException {
	    	AddNewSchemaCommand cmd = new AddNewSchemaCommand(modelRoot, namespace);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static void removeNamespace(IWsdlModelRoot modelRoot, ISchema schema) throws ExecutionException {
	    	RemoveSchemaCommand cmd = new RemoveSchemaCommand(modelRoot, schema);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    	 
	    
	    private static ISimpleType addSimpleType(IXSDModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, String name) throws ExecutionException {
	    	AddSimpleTypeCommand cmd = new AddSimpleTypeCommand(modelRoot, schema, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getSimpleType();
	    } 
	    
	    private static void addPattern(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, String value) throws ExecutionException {
	    	AddFacetCommand cmd = FacetsCommandFactory.createAddPatternFacetCommand(modelRoot, type, value);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static final void setMinLength(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, String length) throws ExecutionException {
	    	AddFacetCommand cmd = FacetsCommandFactory.createAddMinLengthFacetCommand(modelRoot, type, length);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static final void setMaxLength(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, String length) throws ExecutionException {
	    	AddFacetCommand cmd = FacetsCommandFactory.createAddMaxLengthFacetCommand(modelRoot, type, length);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static void addEnumeration(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, String value) throws ExecutionException {
	    	AddFacetCommand cmd = FacetsCommandFactory.createAddEnumerationFacetCommand(modelRoot, type, value);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    private static void addAttribute(IXSDModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type ,String name )throws ExecutionException {
	    	AddAttributeCommand cmd = new AddAttributeCommand(modelRoot, type, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }	    
	    
	    private static IElement addElement(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type, String name) throws ExecutionException {
	    	AddElementCommand cmd = new AddElementCommand(modelRoot, type, name);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getElement();
	    }
	    
	    public static void removeElement(IModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type, String elementName) throws ExecutionException {
	    	IElement element = type.getElements(elementName).iterator().next();
	    	DeleteElementCommand cmd = new DeleteElementCommand(modelRoot, type, element);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    }
	    
	    
	    private static IStructureType addStructureType(IXSDModelRoot modelRoot, org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, String name, boolean element, AbstractType referencedType) throws ExecutionException {
	    	AddStructureTypeCommand cmd = new AddStructureTypeCommand(modelRoot, schema, name, element, referencedType);
	    	assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
	    	return cmd.getStructureType();
	    }
	    
	    private static void setMinOccurs(IModelRoot modelRoot, IElement element, int value) throws ExecutionException {
	    	setMinOccurs(modelRoot, element, value, Status.OK);
	    }
	    
	    private static void setMinOccurs(IModelRoot modelRoot, IElement element, int value, int expectedSeverity) throws ExecutionException {
	    	SetElementOccurences cmd = new SetElementOccurences(modelRoot, element, value, element.getMaxOccurs());
	    	IStatus status = modelRoot.getEnv().execute(cmd);
	    	assertEquals(expectedSeverity, status.getSeverity());
	    } 
	    	    
	    private static void setMaxOccurs(IModelRoot modelRoot, IElement element, int value) throws ExecutionException {
	    	setMaxOccurs(modelRoot, element, value, IStatus.OK);
	    }
	    
	    private static void setMaxOccurs(IModelRoot modelRoot, IElement element, int value, int expectedSeverity) throws ExecutionException {
	    	SetElementOccurences cmd = new SetElementOccurences(modelRoot, element, element.getMinOccurs(), value);
	    	IStatus status = modelRoot.getEnv().execute(cmd);
	    	assertEquals(expectedSeverity, status.getSeverity());
	    }
	    
	    private static void setNillable(IModelRoot modelRoot, IElement element, boolean nillable) throws ExecutionException {
	    	setNillable(modelRoot, element, nillable, IStatus.OK);
	    }
	    
	    private static void setNillable(IModelRoot modelRoot, IElement element, boolean nillable, int expectedSeverity) throws ExecutionException {
	    	SetElementNillableCommand cmd = new SetElementNillableCommand(modelRoot, element, nillable);
	    	IStatus status = modelRoot.getEnv().execute(cmd);
	    	assertEquals(expectedSeverity, status.getSeverity());
	    }
	    
}
