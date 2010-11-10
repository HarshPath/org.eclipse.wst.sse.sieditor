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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.filterComponents;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CreateGlobalTypeFromAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RemoveTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.ResolveImportedSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchemaResolver;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * Wrapper for {@link XSDSchema} for providing simplified API
 * 
 * 
 * @Limitations Cannot process a invalid WSDL having multiple components with
 *              same name and value space (Whichever component comes first is
 *              considered)
 */
public class Schema extends AbstractXSDComponent implements ISchema, org.eclipse.wst.sse.sieditor.model.write.xsd.api.ISchema {

    public final static String DEFAULT_SIMPLE_BASETYPE = "string"; //$NON-NLS-1$

    private ISchemaResolver _resolver;
    private final XSDSchema _eSchema;
    private final IModelObject _parent;
    // private final IFile _file;
    private URI uri;

    private static Schema _schemaForSchema;

    public Schema(final XSDSchema schema, final IXSDModelRoot modelRoot, IModelObject parent, URI uri) {
        super(modelRoot);

        Nil.checkNil(schema, "schema"); //$NON-NLS-1$
        Nil.checkNil(modelRoot, "modelRoot"); //$NON-NLS-1$

        URI resolvedURI = null;
        if (!XSDConstants.isSchemaForSchemaNamespace(schema.getTargetNamespace())) {
            if (resolvedURI == null && (parent instanceof IDescription)) {
                resolvedURI = ((IDescription) parent).getContainingResource();
            } else {
                resolvedURI = uri;
            }
            Nil.checkNil(resolvedURI, "resolved uri"); //$NON-NLS-1$
        }

        this._eSchema = schema;
        this._parent = parent == null ? findWsdlDefinition(schema) : parent;
        // this._file = file;
        this.uri = resolvedURI;
    }

    private IModelObject findWsdlDefinition(XSDSchema schema) {
        Definition wsdlDefinition = getDefinition(schema);
        if (wsdlDefinition != null) {
            return WSDLFactory.getInstance().createWSDLModelRoot(wsdlDefinition).getDescription();
        }
        return null;
    }

    private Definition getDefinition(EObject eContainer) {
        EObject container = eContainer.eContainer();
        if (container == null || container instanceof Definition) {
            return (Definition) container;
        }
        return getDefinition(container);
    }

    public Schema(final XSDSchema schema, final IModelObject parent, final URI uri) {
        this(schema, XSDFactory.getInstance().createXSDModelRoot(schema), parent, uri);
    }

    public Collection<IType> getAllContainedTypes() {
        final List<IType> types = new ArrayList<IType>();
        final boolean isSchemaForSchema = EmfXsdUtils.isSchemaForSchemaNS(this.getNamespace());

        final List<XSDSchemaContent> contents = new ArrayList<XSDSchemaContent>();
        contents.addAll(isSchemaForSchema ? _eSchema.getTypeDefinitions() : _eSchema.getContents());

        for (XSDSchemaContent content : contents) {
            AbstractType type = null;

            if (content instanceof XSDSimpleTypeDefinition) {
                if (!isSchemaForSchema || (isSchemaForSchema && EmfXsdUtils.isPrimitiveType((XSDTypeDefinition) content))) {
                    type = new SimpleType(getModelRoot(), this, (XSDSimpleTypeDefinition) content);
                    types.add(type);
                }
            } else if (content instanceof XSDComplexTypeDefinition) {
                if (!isSchemaForSchema || (isSchemaForSchema && EmfXsdUtils.isPrimitiveType((XSDTypeDefinition) content))) {
                    type = new StructureType(getModelRoot(), this, (XSDComplexTypeDefinition) content);
                    types.add(type);
                }
            } else if (!isSchemaForSchema && content instanceof XSDElementDeclaration) {
                StructureType element = new StructureType(getModelRoot(), this, (XSDNamedComponent) content);
                types.add(element);
            }
        }

        return types;
    }

    public IType[] getAllTypes(final String name) {
        if (name == null) {
            return null;
        }

        final List<IType> typesReturned = new ArrayList<IType>(1);

        Collection<IType> allContainedTypes = getAllContainedTypes();
        for (IType type : allContainedTypes) {
            if (name.equals(type.getName())) {
                typesReturned.add(type);
            }
        }

        if (typesReturned.size() == 0) {
            return null;
        }

        return typesReturned.toArray(new IType[typesReturned.size()]);
    }

    public Collection<ISchema> getAllReferredSchemas() {
        final ArrayList<ISchema> result = new ArrayList<ISchema>();
        if (!EmfXsdUtils.isSchemaForSchemaNS(getNamespace())) {
            result.addAll(getReferredSchemas());
        }
        addResolvedDocuments(result, true);

        return result;
    }

    private List<ISchema> getReferredSchemas() {
        final List<ISchema> referredSchemas = new ArrayList<ISchema>(1);
        final Collection<XSDSchemaDirective> directives = filterComponents(_eSchema.getContents(), XSDSchemaDirective.class);
        for (XSDSchemaDirective directive : directives) {
            // Redefine not supported
            if (directive instanceof XSDRedefine)
                continue;

            if (directive instanceof XSDImport && null != directive.getSchemaLocation()) {
                XSDSchema importedSchema = directive.getResolvedSchema();
                if (null == importedSchema) {
                    ResolveImportedSchemaCommand command = new ResolveImportedSchemaCommand((XSDImportImpl) directive,
                            getModelRoot(), this);
                    try {
                        if (command.execute(new NullProgressMonitor(),null).isOK()) {
                            importedSchema = command.getResolvedSchema();
                        }
                    } catch (ExecutionException e) {
                        Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not resolve imported schema for location " //$NON-NLS-1$
                                + directive.getSchemaLocation(), e);
                    }
                    /*
                     * ((XSDImportImpl) importObj).importSchema();
                     * importedSchema = importObj.getResolvedSchema();
                     */
                }
                if (null != importedSchema) {
                    URI relativeURI = ResourceUtils.constructURI(getComponent(), importedSchema);
                    // final IFile file = ResourceUtils.getWorkSpaceFile(new
                    // Path(importedSchema.getSchemaLocation()));
                    if (null != relativeURI) {
                        Schema schema;
                        if (null == getModelRoot())
                            schema = new Schema(importedSchema, null, relativeURI);
                        else
                            schema = new Schema(importedSchema, getModelRoot(), null, relativeURI);
                        referredSchemas.add(schema);
                    }
                }
            }

            if (directive instanceof XSDInclude && null != directive.getSchemaLocation()) {
                final XSDSchema includedSchema = directive.getResolvedSchema();
                if (null != includedSchema) {
                    URI relativeURI = ResourceUtils.constructURI(getComponent(), includedSchema);
                    if (null != relativeURI) {
                        Schema schema;
                        if (null == getModelRoot())
                            schema = new Schema(includedSchema, null, relativeURI);
                        else
                            schema = new Schema(includedSchema, getModelRoot(), null, relativeURI);
                        referredSchemas.add(schema);
                    }
                }
            }
        }

        // Add schema for schema in referred Documents
        referredSchemas.add(getSchemaForSchema());

        return referredSchemas;
    }

    private List<ISchema> getIncludedSchemas() {
        final List<ISchema> includedSchemas = new ArrayList<ISchema>(1);

        final Collection<XSDSchemaDirective> directives = filterComponents(_eSchema.getContents(), XSDSchemaDirective.class);

        for (XSDSchemaDirective directive : directives) {
            // Redefine not supported
            if (directive instanceof XSDInclude && null != directive.getSchemaLocation()) {
                final XSDSchema includedSchema = directive.getResolvedSchema();
                if (null != includedSchema) {
                    URI relativeURI = ResourceUtils.constructURI(getComponent(), includedSchema);
                    // IFile file = ResourceUtils.getWorkSpaceFile(new
                    // Path(importedSchema.eResource().getURI().toString()));
                    if (null != relativeURI) {
                        Schema schema;
                        if (null == getModelRoot())
                            schema = new Schema(includedSchema, null, relativeURI);
                        else
                            schema = new Schema(includedSchema, getModelRoot(), null, relativeURI);
                        includedSchemas.add(schema);
                    }
                }
            }
        }

        return includedSchemas;
    }

    private synchronized void addResolvedDocuments(final List<ISchema> schemas, boolean addImports) {
        if (EmfXsdUtils.isSchemaForSchemaNS(getNamespace()) || null == _resolver)
            return;
        // Add Imports
        if (addImports) {
            final Collection<XSDSchemaDirective> imports = filterComponents(_eSchema.getContents(), XSDSchemaDirective.class);

            for (XSDSchemaDirective importObj : imports) {
                if (importObj instanceof XSDImport) {
                    if (null != _resolver) {
                        schemas.addAll(_resolver.resolveSchema(((XSDImport) importObj).getNamespace(), importObj
                                .getSchemaLocation()));
                    }
                }
            }
        }
        // Add includes
        final List<Schema> included = _resolver.resolveSchema(this.getNamespace(), null);
        included.remove(this);
        schemas.addAll(included);
    }

    public Collection<ISchema> getAllIncludedSchemas() {
        final ArrayList<ISchema> result = new ArrayList<ISchema>();
        result.addAll(getIncludedSchemas());
        addResolvedDocuments(result, false);

        return result;
    }

    public String getLocation() {
        try {
            return uri == null ? null : URIHelper.decodeURI(uri);
        } catch (UnsupportedEncodingException e) {
            final String msg = e.getMessage();

            Logger.logError(msg, e);

            throw new IllegalStateException(msg, e);
        }
    }

    public URI getContainingResource() {
        return uri; // GFB-POC TODO _file;
    }

    public String getNamespace() {
        return _eSchema.getTargetNamespace();
    }

    public String getDocumentation() {
        final Element documentation = getFirstElement(_eSchema.getAnnotations());
        return super.getDocumentation(documentation);
    }

    public IModelObject getParent() {
        return _parent;
    }

    public ISimpleType addSimpleType(final String name) throws DuplicateException, IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (!EmfXsdUtils.isValidNCName(name)) {
            throw new IllegalInputException("Entered Type name is not valid"); //$NON-NLS-1$
        }

        final XSDTypeDefinition resolvedTypeDefinition = getComponent().resolveTypeDefinition(name);
        if (resolvedTypeDefinition.eContainer() != null) {
            throw new DuplicateException("Type with name " + name + " already exists"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final AddSimpleTypeCommand command = new AddSimpleTypeCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
        return command.getSimpleType();
    }

    public IStructureType addStructureType(final String name, final boolean element, AbstractType referencedType)
            throws DuplicateException, IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidNCName(name)) {
            throw new IllegalInputException("Entered Type name is not valid"); //$NON-NLS-1$
        }

        final XSDSchema schema = getComponent();
        final XSDSchemaContent resolvedTypeDefinition = element ? schema.resolveElementDeclaration(name) : schema
                .resolveTypeDefinition(name);

        if (resolvedTypeDefinition.eContainer() != null) {
            throw new DuplicateException("Type with name " + name + " already exists"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final AddStructureTypeCommand command = new AddStructureTypeCommand(getModelRoot(), this, name, element, referencedType);
        getModelRoot().getEnv().execute(command);
        return command.getStructureType();
    }

    public IStructureType addStructureType(final String name, final boolean element) throws DuplicateException,
            IllegalInputException, ExecutionException {
        return addStructureType(name, element, null);
    }

    public IType copyType(final IType type, final String newName) throws DuplicateException, ExecutionException {
        Nil.checkNil(newName, "newName"); //$NON-NLS-1$
        Nil.checkNil(type, "type"); //$NON-NLS-1$

        if (type instanceof AbstractType && null != type.getName()) {
            final XSDNamedComponent component = type.getComponent();

            final IType[] resolvedTypes = getAllTypes(newName);
            if (resolvedTypes != null) {
                throw new DuplicateException("Type with name '" + newName + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            final CopyTypeCommand command = new CopyTypeCommand(getModelRoot(), this, component, this, newName);
            getModelRoot().getEnv().execute(command);
            return command.getCopiedType();
        }

        return null;
    }

    public static IType getDefaultSimpleType() {
        return Schema.getSchemaForSchema().getType(false, DEFAULT_SIMPLE_BASETYPE);
    }

    public static Schema getSchemaForSchema() {
        if (null == _schemaForSchema) {
            synchronized (Schema.class) {
                if (null != _schemaForSchema)
                    return _schemaForSchema;
                final XSDSchema schemaForSchema = EmfXsdUtils.getSchemaForSchema();
                _schemaForSchema = new Schema(schemaForSchema, null, null);
            }
        }
        return _schemaForSchema;
    }

    public IType resolveType(final AbstractType type) throws ExecutionException {
        IType[] resolvedTypes = resolveType(new QName(type.getNamespace(), type.getName()), true, null);

        boolean isElement = StructureType.isGlobalElement(type);

        IType result = null;

        if (resolvedTypes.length == 1) {
            result = resolvedTypes[0];
        } else if (resolvedTypes.length > 1) {
            result = getTypeFromAllTypesArray(isElement, resolvedTypes);

        }
        final IModelObject parent = type.getParent();
        if (result instanceof UnresolvedType && parent instanceof Schema) {
            final IXSDModelRoot modelRoot = getModelRoot();
            if (uri.toString().endsWith(".xsd") //$NON-NLS-1$
            /*
             * GFB ResourceUtils.checkContentType (file, DocumentType.XSD_SHEMA.
             * getResourceID())
             */) {
                final ImportSchemaCommand command = new ImportSchemaCommand(modelRoot, this, type);
                modelRoot.getEnv().execute(command);

                final ISchema resolvedSchema = command.getSchema();
                if (resolvedSchema == null) {
                    result = UnresolvedType.instance();
                } else {
                    IType[] allTypes = resolvedSchema.getAllTypes(type.getName());
                    result = getTypeFromAllTypesArray(isElement, allTypes);
                }
            } else {
                final ImportSchemaCommand cmd = new ImportSchemaCommand(modelRoot, this, type);
                IStatus resultStatus = modelRoot.getEnv().execute(cmd);
                if (resultStatus.isOK()) {
                    final Schema resolvedSchema = (Schema)cmd.getSchema();
                    result = null == resolvedSchema ? UnresolvedType.instance() : getTypeFromAllTypesArray(isElement,
                            resolvedSchema.resolveType(new QName(type.getNamespace(), type.getName()), true, null));
                } else {
                    throw new ExecutionException(resultStatus.getMessage());
                }
            }
        }
        return result;
    }

    public IType getType(boolean isElement, String name) {
        IType[] allTypes = getAllTypes(name);
        return getTypeFromAllTypesArray(isElement, allTypes);
    }

    private IType getTypeFromAllTypesArray(boolean isElement, IType[] allTypes) {
        if (allTypes == null ||  CollectionTypeUtils.containsObject(allTypes, UnresolvedType.instance())) {
            return null;
        }

        if (isElement) {
            for (IType resolvedType : allTypes) {
                if (StructureType.isGlobalElement(resolvedType)) {
                    return resolvedType;
                }
            }
        } else {
            for (IType resolvedType : allTypes) {
                if (!StructureType.isGlobalElement(resolvedType)) {
                    return resolvedType;
                }
            }
        }

        return null;
    }

    /**
     * Returns a type if it is
     * 
     * @param name
     * @return
     */
    private IType[] resolveType(final QName name, final boolean processImports, List<ISchema> checkedSchemas) {
        boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$

        IType[] result = null;

        if (null == name)
            return new IType[] { UnresolvedType.instance() };

        final String namespace = name.getNamespaceURI() == null ? "" : name.getNamespaceURI(); //$NON-NLS-1$
        final String typeName = name.getLocalPart() == null ? "" : name.getLocalPart(); //$NON-NLS-1$

        if (checkedSchemas == null) {
            checkedSchemas = new ArrayList<ISchema>();
        }
        checkedSchemas.add(this);

        if (EmfXsdUtils.isSchemaForSchemaNS(namespace)) {
            final Schema schemaForSchema = getSchemaForSchema();
            result = schemaForSchema.getAllTypes(name.getLocalPart());
        } else if (null == namespace || "".equals(namespace) || namespace.equals(this.getNamespace())) { //$NON-NLS-1$
            result = this.getAllTypes(typeName);
            if (null == result) {
                for (ISchema schema : getAllIncludedSchemas()) {
                    if (checkedSchemas.contains(schema)) {
                        continue;
                    }
                    result = ((Schema) schema).resolveType(new QName(null, typeName), false, checkedSchemas);
                    if (null != result && !CollectionTypeUtils.containsObject(result, UnresolvedType.instance())) {
                        if (debug)
                            Logger.getDebugTrace().trace("", "Included Type"); //$NON-NLS-1$ //$NON-NLS-2$
                        break;
                    }
                }
            } else if (debug)
                Logger.getDebugTrace().trace("", "Contained Type"); //$NON-NLS-1$ //$NON-NLS-2$
            if (debug)
                Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
            if (result == null) {
                result = new IType[] { UnresolvedType.instance() };
            }
            return result;
        }

        if (null == result && processImports) {
            final List<ISchema> referredSchemas = (List<ISchema>) getAllReferredSchemas();
            addResolvedDocuments(referredSchemas, true);
            for (ISchema referredSchema : referredSchemas) {
                boolean equalNamespaces = namespace == null ? referredSchema.getNamespace() == null : (referredSchema
                        .getNamespace() != null ? referredSchema.getNamespace().equals(namespace) : false);
                if (equalNamespaces) {
                    result = ((Schema) referredSchema).resolveType(name, false, checkedSchemas);
                }
                if (null != result && !CollectionTypeUtils.containsObject(result, UnresolvedType.instance())) {
                    if (debug)
                        Logger.getDebugTrace().trace("", "Imported Type"); //$NON-NLS-1$ //$NON-NLS-2$
                    break;
                }
            }
        }
        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$

        if (result == null) {
            result = new IType[] { UnresolvedType.instance() };
        }
        return result;
    }

    public AbstractXSDComponent resolveComponent(final XSDNamedComponent component, boolean processImports) {
        if (null == component) {
            return UnresolvedType.instance();
        }

        String name = component.getName();
        if (null == name) {
            return UnresolvedType.instance();
        }

        IType[] resolveTypes = resolveType(new QName(component.getTargetNamespace(), name), processImports, null);
        AbstractType typeFromAllTypesArray = (AbstractType) getTypeFromAllTypesArray(component instanceof XSDElementDeclaration,
                resolveTypes);
        if (typeFromAllTypesArray == null) {
            return UnresolvedType.instance();
        }
        return typeFromAllTypesArray;
    }

    public void setResolver(final ISchemaResolver resolver) {
        this._resolver = resolver;
    }

    public void setNamespace(String namespace) throws IllegalInputException, ExecutionException {
        Nil.checkNil(namespace, "namespace"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidURI(namespace))
            throw new IllegalInputException("Entered Namespace is not valid"); //$NON-NLS-1$
        final SetNamespaceCommand command = new SetNamespaceCommand(getModelRoot(), this, namespace);
        getModelRoot().getEnv().execute(command);
    }

    /*
     * public Element setDocumentation(String description) throws
     * CommandException { final List<XSDAnnotation> annotations =
     * _eSchema.getAnnotations(); XSDAnnotation annotation = null; if
     * (annotations.size() > 0) { annotation = annotations.get(0); } else {
     * AddAnotationCommand command = new AddAnotationCommand(_eSchema,
     * getModelRoot(), this); if
     * (getModelRoot().getEnv().execute(command).isOK()) { annotation =
     * command.getAnnotation(); } } super.setDocumentation(annotation,
     * description); return null; }
     */

    public void removeType(IType type) throws ExecutionException {
        Nil.checkNil(type, "type"); //$NON-NLS-1$
        final RemoveTypeCommand command = new RemoveTypeCommand(getModelRoot(), this, type);
        getModelRoot().getEnv().execute(command);
    }

    @Override
    public XSDSchema getComponent() {
        return _eSchema;
    }

    public IType createGlobalTypeFromAnonymous(IElement element, String newName) throws DuplicateException, ExecutionException {
        Nil.checkNil(newName, "newName"); //$NON-NLS-1$
        Nil.checkNil(element, "type"); //$NON-NLS-1$

        IType type = element.getType();

        if (type instanceof AbstractType) {
            final XSDNamedComponent component = type.getComponent();

            final IType resolvedType = getType(component instanceof XSDElementDeclaration, newName);
            if (null != resolvedType) {
                throw new DuplicateException("Type with name '" + newName + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            final CreateGlobalTypeFromAnonymousCommand command = new CreateGlobalTypeFromAnonymousCommand(getModelRoot(), this,
                    element, newName);
            getModelRoot().getEnv().execute(command);
            return type;
        }

        return null;
    }

    public String getRawLocation() {
        return uri.toString();
    }

    public IType getTypeByComponent(XSDNamedComponent xsdComponent) {
        IType[] allTypes = getAllTypes(xsdComponent.getName());
        if (allTypes != null) {
            for (IType iType : allTypes) {
                if (iType.getComponent().equals(xsdComponent)) {
                    return iType;
                }
            }
        }

        return UnresolvedType.instance();
    }

    public ISchema getReferredSchema(final XSDSchema xsdSchema) {

        final Condition<ISchema> matchingSchema = new Condition<ISchema>() {
            public boolean isSatisfied(ISchema schema) {
                return xsdSchema != null && xsdSchema.equals(schema.getComponent());
            }
        };

        Collection<ISchema> referredSchemas = CollectionTypeUtils.findAll(getAllReferredSchemas(), matchingSchema);

        return referredSchemas.isEmpty() ? null : referredSchemas.iterator().next();

    }
}
