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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLFactory;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.wst.wsdl.internal.util.WSDLResourceFactoryImpl;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * EMF WSDL Utils
 * 
 * 
 */
public final class EmfWsdlUtils {
    private static final String NAMESPACE_PREFIX_PREFIX = "ns"; //$NON-NLS-1$

    private EmfWsdlUtils() {
        // Stop any instance creation
    }

    /**
     * Returns a {@link WSDLFactory} class for creating EMF WSDL Objects
     * 
     * @return
     */
    public static WSDLFactory getWSDLFactory() {
        return WSDLFactory.eINSTANCE;
    }

    public static String makeMessageName(final String name, final String suffix, final Definition definition) {
        int counter = 1;
        final String opName = name + suffix;
        QName qName = new QName(definition.getTargetNamespace(), opName);

        while (true) {
            if (null == definition.getMessage(qName) || counter == 0) {
                if (counter == 0)
                    throw new IllegalStateException("Message Numbers limit reached"); //$NON-NLS-1$
                break;
            }
            qName = new QName(definition.getTargetNamespace(), opName + counter++);
        }
        return qName.getLocalPart();
    }

    /**
     * Return {@link WSDLPackage} instance for accessing all the EMF proxy
     * classes
     * 
     * @return
     */
    public static WSDLPackage getWSDLPackage() {
        return getWSDLFactory().getWSDLPackage();
    }

    /**
     * Returns respt. {@link Definition} object for a given WSDL {@link IFile}
     * 
     * @param file
     * @return
     */

    public static final Definition resolveWSDL(final java.net.URI uri) {
        final URI emfUri = URI.createURI(uri.toString());
        final ResourceSet rs = new ResourceSetImpl();
        final WSDLResourceImpl wsdlr = (WSDLResourceImpl) rs.getResource(emfUri, true);

        return wsdlr.getDefinition();
    }

    public static void applyFileExtensionForWSDLToResourceSet(final ResourceSet resourceSet, final String fileExtension) {
        final Resource.Factory.Registry reg = resourceSet.getResourceFactoryRegistry();
        final Map<String, Object> currentMap = reg.getExtensionToFactoryMap();
        currentMap.put(fileExtension, new WSDLResourceFactoryImpl());

        if ("xsd".equalsIgnoreCase(fileExtension)) { //$NON-NLS-1$
            currentMap.put(fileExtension, new XSDResourceFactoryImpl());
        }
    }

    public static ResourceSet getResourceSet() {
        final ResourceSet resourceSet = new ResourceSetImpl();
        final Resource.Factory.Registry reg = new ResourceFactoryRegistryImpl();
        final Resource.Factory.Registry globalReg = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> globalMap = globalReg.getExtensionToFactoryMap();
        final Map<String, Object> newMap = new HashMap<String, Object>(globalMap);
        reg.getExtensionToFactoryMap().putAll(newMap);
        resourceSet.setResourceFactoryRegistry(reg);
        return resourceSet;
    }

    /**
     * Check whether the schema is just used to import another schema.
     * http://ws-i.org/profiles/BasicProfile-1.2-WGD.html#WSDL_and_Schema_Import
     * - R2105 All xsd:schema elements contained in a wsdl:types element of a
     * DESCRIPTION MUST have a targetNamespace attribute with a valid and
     * non-null value, UNLESS the xsd:schema element has xsd:import and/or
     * xsd:annotation as its only child element(s).
     * 
     * @return empty list if schema is not WS-I R2105, otherwise schema's
     *         imports are returned.
     */
    public static List<XSDImport> getWsiImports(final XSDSchema schema) {
        final List<XSDImport> importedSchemas = new ArrayList<XSDImport>();
        if (schema == null || schema.getTargetNamespace() != null && !"".equals(schema.getTargetNamespace())) { //$NON-NLS-1$
            return importedSchemas;
        }

        for (final XSDSchemaContent schemaContent : schema.getContents()) {
            if (!(schemaContent instanceof XSDAnnotation) && !(schemaContent instanceof XSDImport)) {
                // not WS-I R2105 complaint schema
                importedSchemas.clear();
                return importedSchemas;
            }

            if (schemaContent instanceof XSDImport) {
                importedSchemas.add((XSDImport) schemaContent);
            }
        }
        return importedSchemas;
    }

    /**
     * Ensures a prefix exists on {@link Definition} for given namespace
     * 
     * @param definition
     * @param namespace
     */
    public static void ensurePrefix(final Definition definition, final String namespace) {
        if (isPrefixMissing(definition, namespace)) {
            final String nsPrefix = generateNewNSAttribute(definition);
            definition.addNamespace(nsPrefix, namespace);
        }
    }

    /**
     * A boolean expression determining if a prefix for this namespace exists in
     * the definitions element.
     * 
     * @param definition
     *            the definitions in which the ns will be searched
     * @param namespace
     *            the namespace to find
     * @return true if an ns prefix is defined, false otherwise
     */
    public static boolean isPrefixMissing(final Definition definition, final String namespace) {
        return !(namespace == null || namespace.trim().length() == 0 || definition.getPrefix(namespace) != null);
    }

    /**
     * Generates a new unique xmlns:xxx namespace prefix for the given
     * definition
     * 
     * @param definition
     *            for which the prefix is generated
     * @return the newly generated attribute name
     */
    public static String generateNewNSAttribute(final Definition definition) {
        return generateNewNSAttribute(definition.getNamespaces());
    }

    /**
     * Generates new xmlns:xxx namespace prefix for the given map of such
     */
    public static String generateNewNSAttribute(final Map<String, String> namespaceMap) {
        for (int i = 0; i >= 0 && i < 10000; i++) {
            final String prefix = new StringBuilder(NAMESPACE_PREFIX_PREFIX).append(i).toString();
            if (!namespaceMap.containsKey(prefix)) {
                return prefix;
            }
        }
        // This point should never be reachable
        throw new IllegalStateException(String.format("Cannot ensure unique prefix in WSDL document")); //$NON-NLS-1$
    }

    /**
     * Adds a new EMF {@link XSDSchema} in {@link Definition} with the specified
     * namespace
     * 
     * @param definition
     *            - wsdl where new schema is to be added
     * @param namespace
     *            - namespace of the new schema
     * @return - added schema object
     */
    public static XSDSchema addXSDSchema(final Definition definition, final String namespace) {
        Types types = definition.getETypes();
        if (null == types) {
            types = EmfWsdlUtils.getWSDLFactory().createTypes();
            definition.setTypes(types);
        }

        final XSDSchema xsdSchema = EmfXsdUtils.getXSDFactory().createXSDSchema();
        xsdSchema.setSchemaForSchemaQNamePrefix(EmfXsdUtils.XSD_PREFIX); //$NON-NLS-1$
        final Map<String, String> qNamePrefixToNamespaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
        qNamePrefixToNamespaceMap.put(xsdSchema.getSchemaForSchemaQNamePrefix(), EmfXsdUtils.getSchemaForSchemaNS());

        final XSDSchemaExtensibilityElement schemaExtensibilityEntity = EmfWsdlUtils.getWSDLFactory()
                .createXSDSchemaExtensibilityElement();
        schemaExtensibilityEntity.setSchema(xsdSchema);

        types.addExtensibilityElement(schemaExtensibilityEntity);
        schemaExtensibilityEntity.setEnclosingDefinition(definition);

        final String wsdlLocation = definition.getLocation();
        xsdSchema.setSchemaLocation(wsdlLocation);

        if (null != namespace && !"".equals(namespace.trim())) //$NON-NLS-1$
            xsdSchema.setTargetNamespace(namespace);

        return xsdSchema;
    }

    /**
     * Use instead of {@link Definition#updateElement(boolean)} to prevent the
     * DOM Model to notify thus change the EMF model, while the DOM it self is
     * being updated from it.
     * 
     * @param definition
     *            the definition whic's element is updated
     * @param deep
     *            :
     * 
     * @see WSDLElement#updateElement(boolean deep)
     */
    public static void updateDefinition(final Definition definition, final boolean deep) {
        XMLModelNotifier modelNotifier = null;
        XMLModelNotifierWrapper notifierWrapper = null;
        final Document document = definition.getDocument();

        if (document instanceof DocumentImpl) {
            modelNotifier = ((DocumentImpl) document).getModel().getModelNotifier();
        }

        try {
            if (modelNotifier instanceof XMLModelNotifierWrapper) {
                notifierWrapper = (XMLModelNotifierWrapper) modelNotifier;
                notifierWrapper.setInterceptChanges(true);
            }
            definition.updateElement(deep);
        } finally {
            if (notifierWrapper != null) {
                notifierWrapper.setInterceptChanges(false);
            }
        }
    }

    public static ISchema getDefaultSchema(final Description description) {
        final Definition definition = description.getComponent();
        final String targetNamespace = definition.getTargetNamespace();
        final ISchema[] schemas = description.getSchema(targetNamespace);
        ISchema schema = null;
        if (schemas != null && schemas.length > 0) {
            schema = schemas[0];
        }
        return schema;
    }

    /**
     * Checks modelObject.getModelRoot() referred definitions, do match of
     * modelObject's Definition
     * 
     * @param modelObject
     *            which is supposed to be from referred definition.
     * @return referred Description, or null
     */
    public static IDescription getReferredDescription(IModelObject modelObject) {
        if (modelObject instanceof IFault) {
            final Collection<IParameter> parameters = ((IFault) modelObject).getParameters();
            modelObject = parameters.isEmpty() ? null : parameters.iterator().next();
        }
        if (modelObject == null || !(modelObject.getModelRoot() instanceof IWsdlModelRoot)) {
            return null;
        }

        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) modelObject.getModelRoot();
        final IDescription description = wsdlModelRoot.getDescription();

        // get referred Definition
        EObject referredDefinition = modelObject.getComponent();
        while (referredDefinition != null && !(referredDefinition instanceof Definition)) {
            referredDefinition = referredDefinition.eContainer();
        }

        if (referredDefinition != null) {
            for (final IDescription referredDescription : description.getReferencedServices()) {
                if (referredDefinition.equals(referredDescription.getComponent())) {
                    return referredDescription;
                }
            }
        }
        return null;
    }

    /**
     * Checks modelObject.getModelRoot() referred schemas, do match
     * modelObject's XSDSchema
     * 
     * @param modelObject
     *            which is supposed to be from referred schema.
     * @return referred ISchema, or null
     */
    public static ISchema getReferredSchema(final IModelObject modelObject) {
        if (modelObject == null || !(modelObject.getModelRoot() instanceof IXSDModelRoot)) {
            return null;
        }

        final IXSDModelRoot xsdModelRoot = (IXSDModelRoot) modelObject.getModelRoot();
        final ISchema schema = xsdModelRoot.getSchema();

        // get referred XSDSchema
        EObject referredXSDSchema = modelObject.getComponent();
        while (referredXSDSchema != null && !(referredXSDSchema instanceof XSDSchema)) {
            referredXSDSchema = referredXSDSchema.eContainer();
        }

        if (referredXSDSchema != null) {
            for (final ISchema referredSchema : schema.getAllReferredSchemas()) {
                if (referredXSDSchema.equals(referredSchema.getComponent())) {
                    return referredSchema;
                }
            }
        }
        return null;
    }

    /**
     * Check whether 'modelObject' is part of 'modelRoot' resource.
     */
    public static boolean isModelObjectPartOfModelRoot(final IModelRoot modelRoot, final IModelObject modelObject) {
        if (modelObject == null) {
            return false;
        }

        final EObject childComponent = modelObject.getComponent();
        final EObject rootComponent = modelRoot.getModelObject().getComponent();

        EObject currentComponent = childComponent;
        while (currentComponent != null && !rootComponent.equals(currentComponent)) {
            currentComponent = currentComponent.eContainer();
        }

        return currentComponent != null;
    }

    public static boolean isSchemaForSchemaMissingForAnySchema(final IModelRoot root) {
        if (!(root instanceof IWsdlModelRoot)) {
            return false;
        }
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        final IDescription object = wsdlModelRoot.getDescription();
        final List<ISchema> schemas = object.getAllVisibleSchemas();
        for (final ISchema schema : schemas) {
            // do not reload
            if (EmfXsdUtils.isSchemaForSchemaMissing(schema)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the index in sourcePage for a given WSDLElement
     * 
     * @param wsdlComponent
     *            must be NOT null
     * @return the start index for a given WSDLElement, or -1 otherwise
     */
    public static int getIndexInSourcePage(WSDLElement wsdlComponent) {
        final Element element = wsdlComponent.getElement();
        return EmfXsdUtils.getIndexInSourcePage(element);
    }

    /**
     * Return true if the searched EMF object is represented in ServiceInterface
     * UI tree
     * 
     * @param searchedObject
     *            is an EMF object
     * @return boolean flag which value depends on the type of the
     *         "searchObject" parameter
     */
    public static boolean hasCorrespondingITreeNode(final EObject searchedObject) {
        // Only these EMF objects are represented in the UI Tree
        if (searchedObject instanceof PortType) {
            return true;
        }
        if (searchedObject instanceof Part) {
            return true;
        }
        if (searchedObject instanceof Fault) {
            return true;
        }
        if (searchedObject instanceof Operation) {
            return true;
        }
        return false;
    }

    public static boolean couldBeVisibleType(XSDConcreteComponent xsdComponent) {
        EObject emfContainer = xsdComponent.eContainer();
        if (!(emfContainer instanceof XSDSchema)) {
            return false;
        }

        EObject parentEmfContainer = emfContainer.eContainer();
        if (parentEmfContainer instanceof XSDSchemaExtensibilityElement) {

            XSDSchemaExtensibilityElement exElement = (XSDSchemaExtensibilityElement) parentEmfContainer;
            if (exElement == null) {
                return false;
            }

            Definition definition = exElement.getEnclosingDefinition();
            boolean locationMatches = definition.getLocation() != null
                    && definition.getLocation().equals(xsdComponent.getSchema().getSchemaLocation());
            boolean isSchemaPresentInDocument = definition.getETypes().getSchemas().contains(xsdComponent.getSchema());

            if (locationMatches && isSchemaPresentInDocument) {
                return true;
            }

        }

        return xsdComponent.getSchema() != null;

    }
}
