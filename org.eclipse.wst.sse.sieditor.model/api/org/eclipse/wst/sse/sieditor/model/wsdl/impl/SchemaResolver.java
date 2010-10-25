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
package org.eclipse.wst.sse.sieditor.model.wsdl.impl;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.filterComponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchemaResolver;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

//GFB-POC Modified to accommodate uri
public class SchemaResolver implements ISchemaResolver {

    private final Description _description;

    public SchemaResolver(final Description description) {
        this._description = description;
    }

    public List<Schema> resolveSchema(final String nameSpace, String location) {
        if (null == location) {
            final Collection<ISchema> schemas = _description.getContainedSchemas();
            final ArrayList<Schema> result = new ArrayList<Schema>(1);
            for (ISchema containedSchema : schemas) {
            	boolean equalNamespaces = nameSpace == null ? 
            			containedSchema.getNamespace() == null :
            				nameSpace.equals(containedSchema.getNamespace());
                if (equalNamespaces) {
                    result.add((Schema) containedSchema);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    public AbstractType makeResolvable(Schema schema, AbstractType type) throws ExecutionException {
        AbstractType result = null;
        final IModelObject parent = type.getParent();
        final Schema referredSchema = (Schema) parent;
        final XSDSchema referringSchema = schema.getComponent();
        final String referredResource = referredSchema.getLocation();
        final String descriptionResource = _description.getLocation();
        if (descriptionResource.equals(referredResource)) {
            /*// we need to add an import
            XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
            xsdImport.setNamespace(type.getNamespace());
            referringSchema.getContents().add(0, xsdImport);
            return type;*/
            //replaced the prev. lines with this command in order all editions to the model to be wrapped in commands
            ImportSchemaCommand command = new ImportSchemaCommand(schema.getModelRoot(), schema, type);
            if(schema.getModelRoot().getEnv().execute(command).isOK()){
                return type;
            }
        } else if (referredResource.endsWith(".xsd")) { //$NON-NLS-1$
            // we now have no option than to copy this type in current WSDL
            result = (AbstractType) _description.resolveType(type);
            // Add an import to namespace
            if (null != result && !schema.getNamespace().equals(result.getNamespace())) {
                final String namespace = result.getNamespace();
                final Collection<XSDSchemaDirective> imports = filterComponents(referringSchema.getContents(),
                        XSDSchemaDirective.class);
                boolean found = false;
                for (XSDSchemaDirective importObj : imports) {
                    if (importObj instanceof XSDImport && namespace.equals(((XSDImport) importObj).getNamespace())
                            && null == importObj.getSchemaLocation()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
                    xsdImport.setNamespace(type.getNamespace());
                    referringSchema.getContents().add(0, xsdImport);
                }
            }
        }
        return result;
    }

}
