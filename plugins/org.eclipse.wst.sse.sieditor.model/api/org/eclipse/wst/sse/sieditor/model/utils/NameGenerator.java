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
package org.eclipse.wst.sse.sieditor.model.utils;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class NameGenerator {

    public static final String ELEMENT_DEFAULT_NAME = "Element"; //$NON-NLS-1$
    public static final String FAULT_ELEMENT_DEFAULT_NAME = "FaultElement"; //$NON-NLS-1$
    public static final String STRUCTURE_TYPE_DEFAULT_NAME = "StructureType"; //$NON-NLS-1$
    public static final String SIMPLE_TYPE_DEFAULT_NAME = "SimpleType"; //$NON-NLS-1$
    public static final String ATTRIBUTE_DEFAULT_NAME = "Attribute"; //$NON-NLS-1$
    // WSDL related
    public static final String SERVICE_INTERFACE = "ServiceInterface"; //$NON-NLS-1$
    public static final String NEW_OPERATION = "NewOperation"; //$NON-NLS-1$
    public static final String PARAMETER = "Parameter"; //$NON-NLS-1$
    public static final String FAULT = "Fault"; //$NON-NLS-1$

    public static String getNewFaultElementDefaultName(final IModelObject modelObject) {
        return getNewElementDefaultName(modelObject, FAULT_ELEMENT_DEFAULT_NAME);
    }

    public static String getNewElementDefaultName(final IModelObject modelObject) {
        return getNewElementDefaultName(modelObject, ELEMENT_DEFAULT_NAME);
    }

    private static String getNewElementDefaultName(final IModelObject modelObject, final String elementDefaultName) {
        return generateName(elementDefaultName, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                if (modelObject instanceof ISchema) {
                    return ((ISchema) modelObject).getAllTypes(in) == null;
                }
                if (modelObject instanceof IStructureType) {
                    final IStructureType structureType = (IStructureType) modelObject;

                    boolean isSatisfied = structureType.getElements(in).size() == 0;
                    final IType baseType = structureType.getBaseType();
                    if (baseType instanceof IStructureType) {
                        isSatisfied &= ((IStructureType) baseType).getElements(in).size() == 0;
                    }

                    return isSatisfied;
                }

                throw new IllegalArgumentException(
                        "The new element name method should be used with a schema or a Structured type"); //$NON-NLS-1$
            }
        });
    }

    public static String getNewAttributeDefaultName(final IStructureType structureType) {
        return NameGenerator.generateName(ATTRIBUTE_DEFAULT_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                boolean isSatisfied = structureType.getElements(in).size() == 0;
                final IType baseType = structureType.getBaseType();
                if (baseType instanceof IStructureType) {
                    isSatisfied &= ((IStructureType) baseType).getElements(in).size() == 0;
                }
                return isSatisfied;
            }
        });
    }

    public static String generateName(final String prefix, final ICondition<String> condition) {
        for (int counter = 1; counter < Integer.MAX_VALUE; counter++) {
            if (condition.isSatisfied(prefix + counter))
                return prefix + counter;
        }
        throw new IllegalStateException("Could not generate name for prefix " + prefix); //$NON-NLS-1$
    }

    public static String getNewSimpleTypeDefaultName(final ISchema schema) {
        return NameGenerator.generateName(SIMPLE_TYPE_DEFAULT_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return schema.getAllTypes(in) == null;
            }
        });
    }

    public static String getNewStructureTypeDefaultName(final ISchema schema) {
        return NameGenerator.generateName(STRUCTURE_TYPE_DEFAULT_NAME, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return schema.getAllTypes(in) == null;
            }
        });
    }

    // WSDL related
    /**
     * Generates Name for a new Operation
     */
    public static String getNewOperationName(final IServiceInterface serviceInterface) {
        return NameGenerator.generateName(NEW_OPERATION, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return serviceInterface.getOperation(in).isEmpty();
            }
        });
    }

    /**
     * Generates Name for a new Service Interface
     */
    public static String getNewServiceInterfaceName(final IDescription description) {
        return NameGenerator.generateName(SERVICE_INTERFACE, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return description.getInterface(in).isEmpty();
            }
        });
    }

    /**
     * Generates Name for a new Parameter
     */
    public static String getInputParameterName(final IOperation operation) {
        return NameGenerator.generateName(PARAMETER, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return operation.getInputParameter(in).isEmpty();
            }
        });
    }

    /**
     * Generates Name for a new Parameter
     */
    public static String getOutputParameterName(final IOperation operation) {
        return NameGenerator.generateName(PARAMETER, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return operation.getOutputParameter(in).isEmpty();
            }
        });
    }

    /**
     * Generates Name for a new Fault
     */
    public static String getNewFaultName(final IOperation operation) {
        return NameGenerator.generateName(FAULT, new ICondition<String>() {
            public boolean isSatisfied(final String in) {
                return operation.getFault(in).isEmpty();
            }
        });
    }
}
