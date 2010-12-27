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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.wsdl.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.internal.impl.ImportImpl;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RemoveSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeTypeResolvableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.ResolveImportedSchemaCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DocumentSaveException;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * {@link IDescription} implementation for the EMF WSDL model
 * 
 */
public final class Description extends AbstractWSDLComponent implements IDescription,
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IDescription {

    private static final String XI_EXTENSION = ".xi"; //$NON-NLS-1$
    private static final String XSD_EXTENSION = ".xsd"; //$NON-NLS-1$
    private static final String WSDL_EXTENSION = ".wsdl"; //$NON-NLS-1$
    private final URI uri;
    private final SchemaResolver _schemaResolver;

    public Description(final IWsdlModelRoot modelRoot, final Definition definition, final URI uri) {
        super(definition, modelRoot, null);
        Nil.checkNil(definition, "definition"); //$NON-NLS-1$

        this._schemaResolver = new SchemaResolver(this);
        this.uri = uri;
    }

    @SuppressWarnings("unchecked")
    public Collection<IServiceInterface> getAllInterfaces() {
        final List<IServiceInterface> interfaces = new ArrayList<IServiceInterface>(1);
        final List<PortType> portTypes = ((Definition) component).getEPortTypes();
        for (final PortType current : portTypes) {
            interfaces.add(new ServiceInterface(getModelRoot(), this, current));
        }
        return interfaces;
    }

    @SuppressWarnings("unchecked")
    public List<IServiceInterface> getInterface(final String name) {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        final List<IServiceInterface> interfaces = new ArrayList<IServiceInterface>();
        final List<PortType> portTypes = ((Definition) component).getEPortTypes();
        for (final PortType current : portTypes) {
            final String localPart = current.getQName().getLocalPart();
            if (name.equals(localPart)) {
                interfaces.add(new ServiceInterface(getModelRoot(), this, current));
            }
        }
        return interfaces;
    }

    public String getLocation() {
        try {
            return URIHelper.decodeURI(uri);
        } catch (final UnsupportedEncodingException e) {
            final String msg = e.getMessage();

            Logger.logError(msg, e);

            throw new IllegalStateException(msg, e);
        }
    }

    public String getRawLocation() {
        return uri.toString();
    }

    @SuppressWarnings("unchecked")
    public Collection<IDescription> getReferencedServices() {
        final List<IDescription> imported = new ArrayList<IDescription>();
        final List<Import> wsdlImports = ((Definition) component).getEImports();
        for (final Import wsdlImport : wsdlImports) {
            Definition importedDefinition = (Definition) wsdlImport.getDefinition();
            final String targetNamespace = importedDefinition == null ? null : importedDefinition.getTargetNamespace();
            final String location = importedDefinition == null ? null : importedDefinition.getLocation();
            final String importNamespace = wsdlImport.getNamespaceURI();
            final String importLocation = wsdlImport.getLocationURI();
            
            final boolean isNamespaceEqual = targetNamespace == null ? importNamespace == null : targetNamespace.equals(importNamespace);
            final boolean isLocationEqual = location == null ? importLocation == null : location.equals(importLocation);
            if (// imported definition is not used anywhere in the enclosing
                // definition (that is the reason for null), so ask "import" to resolve it explicitly
                    null == importedDefinition ||
                // following condition happens when copy/paste import element in source page, and change 
                // location or namespace attribute of that import
                    !isNamespaceEqual || !isLocationEqual) {

                // this operation has to be executed within a write transaction
                final Map options = new HashMap(2);
                options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
                final AbstractEMFOperation importCommand = new AbstractEMFOperation(getModelRoot().getEnv().getEditingDomain(), "", //$NON-NLS-1$
                        options) {

                    @Override
                    protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                        ((ImportImpl) wsdlImport).importDefinitionOrSchema();
                        return Status.OK_STATUS;
                    }
                };
                try {
                    final IStatus status = importCommand.execute(null, null);
                    if (!status.isOK())
                        Logger.log(status);
                } catch (final ExecutionException e) {
                    Logger.logError("SI editor could not resolve some imports", e); //$NON-NLS-1$
                }

                importedDefinition = (Definition) wsdlImport.getDefinition();
            }

            if (null != importedDefinition &&
            		// do not add referred WSDLs, which TNS differs the Import tag
            		isNamespaceEqual) {
                final URI importedWsdlURI = ResourceUtils.constructURI(this, importedDefinition);
                imported.add(new Description(getModelRoot(), importedDefinition, importedWsdlURI));
            }
        }
        return imported;
    }

    @SuppressWarnings("unchecked")
    public List<ISchema> getAllVisibleSchemas() {
        final boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$

        final List<ISchema> visibleSchemas = new ArrayList<ISchema>();
        visibleSchemas.add(Schema.getSchemaForSchema());

        if (debug)
            Logger.getDebugTrace().trace("", "Processing WSDL Imports"); //$NON-NLS-1$ //$NON-NLS-2$

        final List<Import> wsdlImports = ((Definition) component).getEImports();
        for (final Import wsdlImport : wsdlImports) {
            final Definition importedDefinition = (Definition) wsdlImport.getDefinition();
            if (null != importedDefinition) {
                // Schemas from imported wsdls must not be visible
                continue;
            }

            // These imports confront WS-I, but however will be processed by
            // this editor
            // http://ws-i.org/profiles/BasicProfile-1.2-WGD.html#WSDL_and_Schema_Import
            // - R2001 A DESCRIPTION MUST only use the WSDL "import" statement
            // to import another
            // WSDL description.
            // - R2003 A DESCRIPTION MUST use the XML Schema "import" statement
            // only within
            // the xsd:schema element of the types section.
            final XSDSchema wsdlImportSchema = wsdlImport.getESchema();
            final ISchema schema = createISchema(wsdlImportSchema);
            if (null != schema) {
                visibleSchemas.add(schema);
                visibleSchemas.addAll(resolveImportedAndIncludedSchemas(wsdlImportSchema));
            }
        }

        if (debug)
            Logger.getDebugTrace().trace("", "Processing WS-I Imports"); //$NON-NLS-1$ //$NON-NLS-2$             

        final Types types = ((Definition) component).getETypes();
        if (types == null)
            return visibleSchemas;

        final List<XSDSchema> schemas = types.getSchemas();
        for (final XSDSchema schema : schemas) {
            if (EmfWsdlUtils.getWsiImports(schema).isEmpty()) {
                final ISchema i_schema = createISchema(schema, this);
                if (null != i_schema) {
                    visibleSchemas.add(i_schema);
                }
            }
            visibleSchemas.addAll(resolveImportedAndIncludedSchemas(schema));
        }

        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$

        final Set<ISchema> noDuplicates = new HashSet<ISchema>(visibleSchemas);
        visibleSchemas.clear();
        visibleSchemas.addAll(noDuplicates);

        return visibleSchemas;
    }

    public ISchema[] getSchema(final String namespace) {
        return getSchema(namespace, true);
    }

    public ISchema[] getSchema(final String namespace, final boolean shouldTraverseVisibleSchemas) {
        // Initialize imported and contained documents
        final ArrayList<ISchema> result = new ArrayList<ISchema>(1);
        final Condition<ISchema> matchingSchema = new Condition<ISchema>() {
            public boolean isSatisfied(final ISchema schema) {
                return namespace == null ? schema.getNamespace() == null : namespace.equals(schema.getNamespace());
            }
        };

        result.addAll(CollectionTypeUtils.findAll(shouldTraverseVisibleSchemas ? getAllVisibleSchemas() : getContainedSchemas(),
                matchingSchema));

        return result.toArray(new ISchema[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public List<ISchema> getContainedSchemas() {
        final List<ISchema> schemas = new ArrayList<ISchema>(1);
        final List<XSDSchema> xsdSchemas = new ArrayList<XSDSchema>();
        final Types types = ((Definition) component).getETypes();
        if (null != types) {
            xsdSchemas.addAll(types.getSchemas());
        }

        for (final XSDSchema schema : xsdSchemas) {
            Schema containedSchema = null;
            containedSchema = new Schema(schema, this, uri);

            containedSchema.setResolver(_schemaResolver);
            schemas.add(containedSchema);
        }

        return schemas;
    }

    public String getNamespace() {
        return ((Definition) component).getTargetNamespace();
    }

    @Override
    public IModelObject getParent() {
        // Definition is the root of the Model
        return null;
    }

    public IServiceInterface addInterface(final String name) throws DuplicateException, IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        final AddServiceInterfaceCommand command = new AddServiceInterfaceCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);

        return command.getInterface();
    }

    public ISchema addSchema(final String namespace) throws IllegalInputException, ExecutionException {
        Nil.checkNil(namespace, "namespace"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidURI(namespace))
            throw new IllegalInputException("Schema Namespace is not valid"); //$NON-NLS-1$
        final AddNewSchemaCommand cmd;

        cmd = new AddNewSchemaCommand(getModelRoot(), namespace);
        getModelRoot().getEnv().execute(cmd);
        Assert.isTrue(null != cmd.getNewSchema());
        ((Schema) cmd.getNewSchema()).setResolver(_schemaResolver);

        return cmd.getNewSchema();
    }

    public IType makeResolvable(final IType type) throws ExecutionException {
        final IType resolvedType = resolveType(type.getComponent());
        final IModelObject parent = type.getParent();
        final IModelObject root = type.getRoot();

        if (null == resolvedType && parent instanceof Schema) {
            if (root instanceof Schema) {
                final ISchema[] schemas = getSchema(type.getNamespace());
                ISchema schema = null;
                if (null == schemas || 0 == schemas.length) {
                    final AddNewSchemaCommand cmd;

                    cmd = new AddNewSchemaCommand(getModelRoot(), type.getNamespace());
                    getModelRoot().getEnv().execute(cmd);
                    schema = cmd.getNewSchema();
                    ((Schema) schema).setResolver(_schemaResolver);
                } else
                    schema = schemas[0];

                final XSDNamedComponent eType = type.getComponent();
                final IType oldType = schema.getType(eType instanceof XSDElementDeclaration, type.getName());

                if (null != oldType)
                    return oldType;

                final CopyTypeCommand command = new CopyTypeCommand(getModelRoot(), this, eType, schema, type.getName());
                getModelRoot().getEnv().execute(command);

                return command.getCopiedType();
            }
            final MakeTypeResolvableCommand command = new MakeTypeResolvableCommand(getModelRoot(), this, (AbstractType) type);

            getModelRoot().getEnv().execute(command);
            return command.getCopiedType();

        }
        return resolvedType;
    }

    /*
     * public void removeInterface(final String name) throws ExecutionException
     * { Nil.checkNil(name, "name"); //$NON-NLS-1$
     * getModelRoot().getEnv().execute(new
     * DeleteServiceInterfaceCommand(getModelRoot(), this, name)); }
     */

    public void removeInterface(final IServiceInterface serviceInterface) throws ExecutionException {
        Nil.checkNil(serviceInterface, "serviceInterface"); //$NON-NLS-1$
        getModelRoot().getEnv().execute(new DeleteServiceInterfaceCommand(getModelRoot(), this, serviceInterface));
    }

    public boolean removeSchema(final String namespace) throws ExecutionException {
        final Set<ISchema> schemas = new HashSet<ISchema>(getContainedSchemas());
        schemas.addAll(getAllVisibleSchemas());
        final Collection<ISchema> matches = CollectionTypeUtils.findAll(schemas, new Condition<ISchema>() {
            public boolean isSatisfied(final ISchema in) {
                return null == namespace ? null == in.getNamespace() : namespace.equals(in.getNamespace());
            }
        });

        for (final ISchema schema : matches) {
            removeSchema(schema);
        }
        return true;
    }

    public boolean removeSchema(final ISchema schema) throws ExecutionException {
        final RemoveSchemaCommand command = new RemoveSchemaCommand(getModelRoot(), schema);
        getModelRoot().getEnv().execute(command);
        return true;
    }

    public void setNamespace(final String namespace) throws IllegalInputException, ExecutionException {
        Nil.checkNil(namespace, "namespace"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidURI(namespace))
            throw new IllegalInputException("Namespace is not valid"); //$NON-NLS-1$
        getModelRoot().getEnv().execute(new ChangeDefinitionTNSCompositeCommand(getModelRoot(), this, namespace));
    }

    @Override
    public Definition getComponent() {
        return (Definition) super.getComponent();
    }

    public boolean save() throws DocumentSaveException {
        try {
            component.eResource().save(new HashMap<String, String>(1));
            // GFB-POC TODO: this should not be done here
            // _source.refreshLocal(IResource.DEPTH_INFINITE,
            // new NullProgressMonitor());
        } catch (final IOException e) {
            Logger.logError("WSDL Resource " + uri.toString() + " could not be saved", e); //$NON-NLS-1$ //$NON-NLS-2$
            throw new DocumentSaveException("WSDL Resource " + uri.toString() + " could not be saved", e); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (final Exception e) {
            Logger.logError("WSDL Resource " + uri.toString() + " could not be saved", e); //$NON-NLS-1$ //$NON-NLS-2$
            throw new DocumentSaveException("WSDL Resource " + uri.toString() + " could not be saved", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return true;
    }

    public IType resolveType(final XSDNamedComponent xsdComponent) {
        return resolveType(xsdComponent, true);
    }

    public IType resolveType(final XSDNamedComponent xsdComponent, final boolean processSchemaImportedSchemas) {
        final boolean debug = Logger.isDebugEnabled();
        Nil.checkNil(xsdComponent, "name"); //$NON-NLS-1$
        if (!(xsdComponent instanceof XSDTypeDefinition || xsdComponent instanceof XSDElementDeclaration))
            return null;

        IType result = null;
        // If schema for schema nameSpace
        final String componentTargetNamespace = xsdComponent.getTargetNamespace();
        if (XSDConstants.isSchemaForSchemaNamespace(componentTargetNamespace)) {
            if (debug)
                Logger.getDebugTrace().trace("", "Schema for schema type"); //$NON-NLS-1$ //$NON-NLS-2$
            final Schema schemaForSchema = Schema.getSchemaForSchema();
            result = schemaForSchema.getType(false, xsdComponent.getName());
            if (result != null) {
                return result;
            }
        }
        

        final List<ISchema> containedSchemas = getContainedSchemas();

        // If with same namesSpace search in current schema and included schema
        for (final ISchema schema : containedSchemas) {
            String namespace = schema.getNamespace();
            final String xsdComponentNamespace = xsdComponent.getTargetNamespace();
            boolean equalNamespaces = namespace == null ? xsdComponentNamespace == null : namespace.equals(xsdComponentNamespace);
            if (equalNamespaces) {
                result = (IType) ((Schema) schema).resolveComponent(xsdComponent, false);
            } else if (processSchemaImportedSchemas) {
                final Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
                for (final ISchema iSchema : referredSchemas) {
                    if (Schema.getSchemaForSchema().equals(iSchema)) {
                        continue;
                    }

                    namespace = iSchema.getNamespace();
                    equalNamespaces = namespace == null ? xsdComponentNamespace == null || xsdComponentNamespace.isEmpty(): namespace.equals(xsdComponentNamespace);
                    if (equalNamespaces) {
                        result = ((Schema) iSchema).getTypeByComponent(xsdComponent);
                    }
                }
            }
            if (null != result && !(result instanceof UnresolvedType)) {
                if (debug)
                    Logger.getDebugTrace().trace("", "Resolved Schema(Contained and Included in Schemas) type"); //$NON-NLS-1$ //$NON-NLS-2$
                break;
            }
        }

        if (result != null && !(result instanceof UnresolvedType)) {
            return result;
        }

        final List<ISchema> visibleSchemas = getAllVisibleSchemas();
        visibleSchemas.removeAll(containedSchemas);
        // If not found nameSpace search all the visible schemas also
        for (final ISchema visibleSchema : visibleSchemas) {
            final String namespace = visibleSchema.getNamespace();
            final String xsdComponentNamespace = xsdComponent.getTargetNamespace();
            final boolean equalNamespaces = namespace == null ? xsdComponentNamespace == null || xsdComponentNamespace.equals("") : namespace.equals(xsdComponentNamespace); //$NON-NLS-1$
            if (equalNamespaces) {
                result = (IType) ((Schema) visibleSchema).resolveComponent(xsdComponent, false);
            }

            if (null != result && !(result instanceof UnresolvedType)) {
                if (debug)
                    Logger.getDebugTrace().trace("", "WSI Import type"); //$NON-NLS-1$ //$NON-NLS-2$
                break;
            }
        }

        return result;
    }

    public final IType resolveType(final AbstractType type) throws ExecutionException {
        IType result = resolveType(type.getComponent());
        final IModelObject parent = type.getParent();

        if (null == result && parent instanceof Schema) {
            final URI uri2 = ((Schema) parent).getContainingResource();

            final String locationURI = uri2.toString();
            if (locationURI.endsWith(XSD_EXTENSION) || locationURI.endsWith(XI_EXTENSION)) {
                final ImportSchemaCommand command = new ImportSchemaCommand(getModelRoot(), this, uri, uri2, type,
                        DocumentType.XSD_SHEMA);
                getModelRoot().getEnv().execute(command);

                final Schema resolvedSchema = command.getSchema();
                return (IType) (null == resolvedSchema ? null : resolvedSchema.resolveComponent(type.getComponent(), false));

            } else if (uri.toString().endsWith(WSDL_EXTENSION)) {
                final MakeTypeResolvableCommand command = new MakeTypeResolvableCommand(getModelRoot(), this, type);
                getModelRoot().getEnv().execute(command);
                result = command.getCopiedType();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void refreshSchemas() {
        final List<ISchema> containedSchemas = getContainedSchemas();
        final Map<XSDSchema, ISchema> processed = new HashMap<XSDSchema, ISchema>(1);
        for (final ISchema schema : containedSchemas) {
            processed.put(schema.getComponent(), schema);
        }

        final Types types = ((Definition) component).getETypes();
        final List<XSDSchema> schemas = types.getSchemas();
        for (final XSDSchema schema : schemas) {
            if (null == schema.getTargetNamespace()) {
                final ArrayList<XSDImport> directives = new ArrayList<XSDImport>(1);
                boolean containsOthers = false;
                for (final XSDSchemaContent schemaContent : schema.getContents()) {
                    if (schemaContent instanceof XSDImport)
                        directives.add((XSDImport) schemaContent);
                    else {
                        containsOthers = true;
                        break;
                    }
                }
                if (directives.size() == 1 && !containsOthers)
                    continue;
            }
            if (processed.containsKey(schema)) {
                return;
            }
            final Schema containedSchema;
            containedSchema = new Schema(schema, this, uri);
            containedSchema.setResolver(_schemaResolver);
        }
    }

    public URI getContainingResource() {
        return uri;
    }

    public SchemaResolver getSchemaResolver() {
        return _schemaResolver;
    }

    private ISchema createISchema(final XSDSchema xsdSchema) {
        return this.createISchema(xsdSchema, null);
    }

    private ISchema createISchema(final XSDSchema xsdSchema, final Description parent) {
        if (xsdSchema == null) {
            return null;
        }

        Schema schema = null;
        final Description parentForURI = parent == null ? this : parent;

        final URI relativeUri = parent == null ? ResourceUtils.constructURI(parentForURI, xsdSchema) : ResourceUtils.constructURI(
                parent, xsdSchema);

        schema = new Schema(xsdSchema, parent, relativeUri);
        if (parent != null) {
            schema.setResolver(parent.getSchemaResolver());
        }
        return schema;
    }

    private List<ISchema> resolveImportedAndIncludedSchemas(final XSDSchema schema) {
        final List<ISchema> importedSchemas = new ArrayList<ISchema>();
        if (schema == null) {
            return importedSchemas;
        }

        final ArrayList<XSDSchemaDirective> directives = new ArrayList<XSDSchemaDirective>();
        final List<XSDImport> wsiImports = EmfWsdlUtils.getWsiImports(schema);
        if (!wsiImports.isEmpty()) {
            directives.addAll(wsiImports);
        } else {
            for (final XSDSchemaContent schemaContent : schema.getContents()) {
                XSDSchemaDirective directive = null;
                if (schemaContent instanceof XSDInclude) {
                    directive = (XSDInclude) schemaContent;
                }

                if (directive != null) {
                    directives.add(directive);
                }

            }
        }

        for (final XSDSchemaDirective directive : directives) {
            XSDSchema resolvedSchema = directive.getResolvedSchema();
            // Make the file resolvable
            if ((null == resolvedSchema || resolvedSchema.eResource() == null) && 
            		directive instanceof XSDImport) {
                ISchema abstractSchema = null;
                if (null == getModelRoot()) {
                    abstractSchema = new Schema(schema, this, uri);
                } else {
                    final Schema newSchema = new Schema(schema, this, uri);
                    newSchema.setResolver(_schemaResolver);
                    abstractSchema = newSchema;
                }
                final ResolveImportedSchemaCommand command = new ResolveImportedSchemaCommand((XSDImportImpl) directive,
                        ((Schema) abstractSchema).getModelRoot(), this);
                try {
                    if (command.execute(new NullProgressMonitor(), null).isOK()) {
                        resolvedSchema = command.getResolvedSchema();
                    }
                } catch (final ExecutionException e) {
                    Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not resolve imported schema for location " + //$NON-NLS-1$
                            directive.getSchemaLocation(), e);
                    continue;
                }
            }

            if (null == resolvedSchema)
                continue;

            final ISchema visibleSchema = createISchema(resolvedSchema, this);
            importedSchemas.add(visibleSchema);
            importedSchemas.addAll(resolveImportedAndIncludedSchemas(resolvedSchema));
        }

        return importedSchemas;
    }

    public IDescription getReferencedDescription(final String targetNamespace) {

        final Condition<IDescription> importedDescription = new Condition<IDescription>() {

            public boolean isSatisfied(final IDescription x) {
            	
                return x != null && (x.getComponent().getTargetNamespace().equals(targetNamespace));
            }
        };
        final List<IDescription> found = CollectionTypeUtils.find(getReferencedServices(), importedDescription);
        return found.isEmpty() ? null : found.get(0);

    }
}
