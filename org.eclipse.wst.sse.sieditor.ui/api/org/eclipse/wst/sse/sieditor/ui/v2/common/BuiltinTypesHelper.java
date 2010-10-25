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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * Helper class for the Built-in Types
 */
public class BuiltinTypesHelper {

    private static Map<String, IType> builtinTypesMap = new HashMap<String, IType>();

    private static Collection<String> commonlyUsedTypeNames = new ArrayList<String>();

    private static BuiltinTypesHelper instance;

    /**
     * The class provides a list of primitive types which are most frequently
     * used and mapping between the names of these types and ITypes in the
     * model.<br><br>
     * <B>Intended to be extended only for testing purposes</B>
     */
    protected BuiltinTypesHelper() {
    }

    /**
     * Called to obtain an instace of the BuiltintTypesHelper<br>
     * 
     * 
     * @return an instance of this utill class
     */
    public static BuiltinTypesHelper getInstance() {
        if (instance == null) {
            instance = new BuiltinTypesHelper();
        }
        return instance;
    }

    /**
     * Returns an array of commonly used type names
     * 
     * @param description
     * @return
     */
    public final String[] getCommonlyUsedTypeNames(/*IDescription description*/) {
       /* if (description == null)
            return new String[0];*/

        if (builtinTypesMap.size() == 0){
            populateMap(/*description*/);
        }
        String[] retTypeNames = new String[builtinTypesMap.size()];
        return commonlyUsedTypeNames.toArray(retTypeNames);
    }

    /**
     * Populates the map with the commonly used type name and its type
     * 
     * @param description
     */
    private void populateMap(/*final IDescription description*/) {
       /* if (description == null)
            return;*/
        populateCommonlyUsedTypeNames();
        //obtains the common for all models shemaForSchema singleton in the Schema.class
        ISchema schema = Schema.getSchemaForSchema();
        if (schema == null){
            throw new RuntimeException("primitive data types schema can not be resolved"); //$NON-NLS-1$
        }
        Collection<IType> types = schema.getAllContainedTypes();

        for (Iterator<IType> iterator = types.iterator(); iterator.hasNext();) {
            IType type = iterator.next();
            if (commonlyUsedTypeNames.contains(type.getName())) {
                builtinTypesMap.put(type.getName(), type);
            }
        }
    }

    /**
     * Returns the type for the given type name
     * 
     * @param typeName
     * @param description
     * @return
     */
    public IType getCommonBuiltinType(String typeName/*, IDescription description*/) {
        /*if (description == null)
            return null;*/

        if (builtinTypesMap.size() == 0){
            populateMap(/*description*/);
        }
        return builtinTypesMap.get(typeName);
    }

    /**
     * Populates the list with the commonly used type names
     */
    private void populateCommonlyUsedTypeNames() {
        commonlyUsedTypeNames.add("boolean"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("date"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("dateTime"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("double"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("float"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("hexBinary"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("int"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("string"); //$NON-NLS-1$
        commonlyUsedTypeNames.add("time"); //$NON-NLS-1$
    }
}
