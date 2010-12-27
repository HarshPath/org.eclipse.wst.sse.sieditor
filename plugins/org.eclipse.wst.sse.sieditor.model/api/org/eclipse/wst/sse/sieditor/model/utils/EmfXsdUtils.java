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

import java.net.URISyntaxException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.emf.query.conditions.eobjects.structuralfeatures.EObjectReferencerCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * EMF XSD Utils
 * 
 * 
 */
public final class EmfXsdUtils {

    private static ArrayList<String> _schemaTypes = new ArrayList<String>();

    static {
        // Add all primitive types to list
        _schemaTypes.add("string"); //$NON-NLS-1$
        _schemaTypes.add("boolean"); //$NON-NLS-1$
        _schemaTypes.add("float"); //$NON-NLS-1$
        _schemaTypes.add("double"); //$NON-NLS-1$
        _schemaTypes.add("decimal"); //$NON-NLS-1$
        _schemaTypes.add("duration"); //$NON-NLS-1$
        _schemaTypes.add("dateTime"); //$NON-NLS-1$
        _schemaTypes.add("time"); //$NON-NLS-1$
        _schemaTypes.add("date"); //$NON-NLS-1$
        _schemaTypes.add("gYearMonth"); //$NON-NLS-1$
        _schemaTypes.add("gYear"); //$NON-NLS-1$
        _schemaTypes.add("gMonthDay"); //$NON-NLS-1$
        _schemaTypes.add("gDay"); //$NON-NLS-1$
        _schemaTypes.add("gMonth"); //$NON-NLS-1$
        _schemaTypes.add("hexBinary"); //$NON-NLS-1$
        _schemaTypes.add("base64Binary"); //$NON-NLS-1$
        _schemaTypes.add("anyURI"); //$NON-NLS-1$
        _schemaTypes.add("QName"); //$NON-NLS-1$
        _schemaTypes.add("NOTATION"); //$NON-NLS-1$
        _schemaTypes.add("normalizedString"); //$NON-NLS-1$
        _schemaTypes.add("token"); //$NON-NLS-1$
        _schemaTypes.add("language"); //$NON-NLS-1$
        _schemaTypes.add("IDREFS"); //$NON-NLS-1$
        _schemaTypes.add("ENTITIES"); //$NON-NLS-1$
        _schemaTypes.add("NMTOKEN"); //$NON-NLS-1$
        _schemaTypes.add("NMTOKENS"); //$NON-NLS-1$
        _schemaTypes.add("Name"); //$NON-NLS-1$
        _schemaTypes.add("NCName"); //$NON-NLS-1$
        _schemaTypes.add("ID"); //$NON-NLS-1$
        _schemaTypes.add("IDREF"); //$NON-NLS-1$
        _schemaTypes.add("ENTITY"); //$NON-NLS-1$
        _schemaTypes.add("integer"); //$NON-NLS-1$
        _schemaTypes.add("nonPositiveInteger"); //$NON-NLS-1$
        _schemaTypes.add("negativeInteger"); //$NON-NLS-1$
        _schemaTypes.add("long"); //$NON-NLS-1$
        _schemaTypes.add("int"); //$NON-NLS-1$
        _schemaTypes.add("short"); //$NON-NLS-1$
        _schemaTypes.add("byte"); //$NON-NLS-1$
        _schemaTypes.add("nonNegativeInteger"); //$NON-NLS-1$
        _schemaTypes.add("unsignedInt"); //$NON-NLS-1$
        _schemaTypes.add("unsignedLong"); //$NON-NLS-1$
        _schemaTypes.add("unsignedShort"); //$NON-NLS-1$
        _schemaTypes.add("unsignedByte"); //$NON-NLS-1$
        _schemaTypes.add("positiveInteger"); //$NON-NLS-1$
    }

    private static XSDSchema _schemaForSchema;

    private static final String EXAMPLE_SCHEMA_NS = "http://www.example.xsd"; //$NON-NLS-1$

    private static final String TNS_PREFIX = "tns"; //$NON-NLS-1$

    public static final String XSD_PREFIX = "xsd"; //$NON-NLS-1$

    public static final String XMLNS_PREFIX = "xmlns"; //$NON-NLS-1$

    private static final String NAMESPACE_PREFIX_PREFIX = "ns"; //$NON-NLS-1$

    public static final String NAMESPACE_DELIMITER = ":"; //$NON-NLS-1$

    public static final String TNS_ATTRIBUTE_NAME = "targetNamespace"; //$NON-NLS-1$

    public static final String XML_TAG_NAME = "xml"; //$NON-NLS-1$

    public static final String ATTRIBUTE_REF = "ref"; //$NON-NLS-1$

    private EmfXsdUtils() {
        // Stop any instance creation
    }

    /**
     * Returns a Factory for creation of EMF XSD Objects
     * 
     * @return
     */
    public static final XSDFactory getXSDFactory() {
        return XSDFactory.eINSTANCE;
    }

    /**
     * Returns a Factory for creation of EMF XSD Objects
     * 
     * @return
     */
    public static final XSDPackage getXSDPackage() {
        return XSDFactory.eINSTANCE.getXSDPackage();
    }

    /**
     * Wrap XSD type in SIE model ISimpleType and calls
     * getRootBaseType(ISimpleType type)
     * 
     * @param type
     *            which's hierarchy is being examined
     * @see EmfXsdUtils#getRootBaseType(ISimpleType)
     */
    public static XSDTypeDefinition getRootBaseType(final XSDSimpleTypeDefinition type) {
        final XSDSchema xsdSchema = type.getSchema();
        final IXSDModelRoot modelRoot = org.eclipse.wst.sse.sieditor.model.impl.XSDFactory.getInstance().createXSDModelRoot(
                xsdSchema);
        final ISchema schema = modelRoot.getSchema();
        final ISimpleType simpleType = new SimpleType(modelRoot, schema, type);

        return (XSDTypeDefinition) getRootBaseType(simpleType).getComponent();
    }

    /**
     * Drills down the base type hierarchy of a simple type,<br>
     * until root (primitive) type is reached.
     * 
     * @param type
     *            which's hierarchy is being examined
     * @return the primitive parent or null if the type is recursively defined
     *         (contained in it's own hierarchy)
     */
    public static ISimpleType getRootBaseType(final ISimpleType type) {
        if (EmfXsdUtils.isSchemaForSchemaNS(type.getNamespace())) {
            return type;
        }

        IType baseType = type;
        final Set<IType> typeHierarchySet = new HashSet<IType>();
        typeHierarchySet.add(baseType);
        while (baseType instanceof ISimpleType && !EmfXsdUtils.isSchemaForSchemaNS(baseType.getNamespace())) {
            baseType = baseType.getBaseType();
            if (!typeHierarchySet.add(baseType)) {
                return UnresolvedType.instance();
            }
        }
        return baseType instanceof ISimpleType && EmfXsdUtils.isSchemaForSchemaNS(baseType.getNamespace()) ? (ISimpleType) baseType
                : UnresolvedType.instance();
    }

    /**
     * Returns the all the components that are of type objectType
     * 
     * @param <T>
     *            - ? extends {@link XSDConcreteComponent}
     * @param components
     *            - collection of input components
     * @param objectType
     *            - type of objects to be returned
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T extends XSDSchemaContent> Collection<T> filterComponents(
            final Collection<? extends XSDSchemaContent> components, final Class<T> objectType) {

        Nil.checkNil(components, "components"); //$NON-NLS-1$
        Nil.checkNil(objectType, "objectType"); //$NON-NLS-1$

        final Collection<T> result = new HashSet<T>(1);
        for (final XSDConcreteComponent component : components) {
            if (objectType.isInstance(component))
                result.add((T) component);
        }

        return result;
    }

    /**
     * Checks if the type is primitive or not
     * 
     * @param type
     *            - {@link XSDTypeDefinition}
     * @return <code>true</code> - if type is primitive <BR>
     *         <code>false</code> - if its not primitive type or null
     * @pre type != null
     */
    public static final boolean isPrimitiveType(final XSDTypeDefinition type) {
        Nil.checkNil(type, "type"); //$NON-NLS-1$
        if (XSDConstants.isURType(type)
                || (XSDConstants.isSchemaForSchemaNamespace(type.getTargetNamespace()) && _schemaTypes.contains(type.getName())))
            return true;
        return false;
    }

    /**
     * Checks if the type is primitive or not
     * 
     * @param namespace
     * @param name
     *            <code>true</code> if type is primitive <code>false</code> if
     *            type is not primitive
     */
    public static final boolean isPrimitiveType(final String namespace, final String name) {
        Nil.checkNil(namespace, "name"); //$NON-NLS-1$
        Nil.checkNil(namespace, "namespace"); //$NON-NLS-1$
        if (isURType(namespace, name) || (XSDConstants.isSchemaForSchemaNamespace(namespace) && _schemaTypes.contains(name)))
            return true;
        return false;
    }

    /**
     * Returns whether the type definition is one of the flavours of the
     * ur-type, i.e., complex <a
     * href="http://www.w3.org/TR/xmlschema-1/#ur-type-itself">anyType</a>,
     * simple <a
     * href="http://www.w3.org/TR/xmlschema-2/#built-in-datatypes">anyType</a>,
     * or <a
     * href="http://www.w3.org/TR/xmlschema-2/#dt-anySimpleType">anySimpleType
     * </a>.
     * 
     * @param namespace
     * @param name
     * @return <code>true</code> if type is URType <code>false</code> if type is
     *         not URType
     */
    public static boolean isURType(final String namespace, final String name) {
        Nil.checkNil(namespace, "name"); //$NON-NLS-1$
        Nil.checkNil(namespace, "namespace"); //$NON-NLS-1$
        return XSDConstants.isSchemaForSchemaNamespace(namespace) && ("anyType".equals(name) || "anySimpleType".equals(name)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the Schema for Schema Namespace
     * 
     * @return
     */
    public static final String getSchemaForSchemaNS() {
        return XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001;
    }

    /**
     * Checks if the passed namespace if Schema for Schema Namespace
     * 
     * @param nameSpace
     * @return <code>true</code> - input string is schema for schema namespace <BR>
     *         <code>false</code> - input string is not schema for schema
     *         namespace or <code>null</code>
     */
    public static final boolean isSchemaForSchemaNS(final String nameSpace) {
        return nameSpace == null ? false : XSDConstants.isSchemaForSchemaNamespace(nameSpace);
    }

    /**
     * Returns EMF Model for Schema for Schema Namespace
     * 
     * @return {@link XSDSchema}
     */
    public static XSDSchema getSchemaForSchema() {
        if (null == _schemaForSchema) {
            synchronized (EmfXsdUtils.class) {
                if (null != _schemaForSchema)
                    return _schemaForSchema;
                final XSDSchema xsdSchema = getXSDFactory().createXSDSchema();
                xsdSchema.setTargetNamespace(EXAMPLE_SCHEMA_NS);
                xsdSchema.setElementFormDefault(XSDForm.QUALIFIED_LITERAL);
                xsdSchema.setAttributeFormDefault(XSDForm.QUALIFIED_LITERAL);
                xsdSchema.setSchemaForSchemaQNamePrefix(XSD_PREFIX);
                final Map<String, String> qNamePrefixToNamespaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
                qNamePrefixToNamespaceMap.put(TNS_PREFIX, xsdSchema.getTargetNamespace());
                qNamePrefixToNamespaceMap.put(xsdSchema.getSchemaForSchemaQNamePrefix(), EmfXsdUtils.getSchemaForSchemaNS());

                final XSDSchema schemaForSchema = xsdSchema.getSchemaForSchema();
                if (null != schemaForSchema)
                    _schemaForSchema = schemaForSchema;
            }
        }
        return _schemaForSchema;
    }

    /**
     * Returns a respt. {@link XSDSchema} model object for a given XSD
     * {@link IFile}
     * 
     * @param file
     * @return
     */
    /* GFB-POC Modified to take uri */
    public static final XSDSchema resolveSchema(final java.net.URI uri) {

        final ResourceSet resourceSet = new ResourceSetImpl();
        final XSDResourceImpl xsdr = (XSDResourceImpl) resourceSet.getResource(URI.createURI(uri.toString()), true);
        return xsdr.getSchema();
    }

    /**
     * Determine if the supplied name is a valid XML NCName.
     * 
     * @param name
     *            the string being checked
     * @return true if the supplied name is indeed a valid XML NCName, or false
     *         otherwise
     */
    public static boolean isValidNCName(final String name) {
        if (name == null || name.length() == 0)
            return false;
        final CharacterIterator iter = new StringCharacterIterator(name);
        char c = iter.first();
        if (!XMLTypeUtil.isNCNameStart(c))
            return false;
        while (c != CharacterIterator.DONE) {
            if (!XMLTypeUtil.isNCNamePart(c))
                return false;
            c = iter.next();
        }
        return true;
    }

    /**
     * Determine if the supplied name is a valid URI.
     * 
     * @param namespace
     *            the string being checked
     * @return true if the supplied name is indeed a valid URI, or false
     *         otherwise
     */
    public static boolean isValidURI(final String namespace) {
        if (namespace == null || namespace.length() == 0)
            return false;
        try {
            new java.net.URI(namespace);
        } catch (final URISyntaxException e) {
            return false;
        }
        return true;
    }

    public static final XSDAnnotation createXSDAnnotation() {
        final XSDAnnotation annotation = getXSDFactory().createXSDAnnotation();
        return annotation;
    }

    /**
     * Creates a new clone for given component and ensures that the
     * {@link XSDAnnotation} objects and the form values for the local
     * {@link XSDElementDeclaration} and {@link XSDAttributeGroupDefinition} are
     * copied as well. <BR>
     * <BR>
     * This is a workaround until the issue reported at below locations gets
     * fixed - <BR>
     * <a href="http://www.eclipse.org/newsportal/article.php?id=2288&group=eclipse.technology.xsd#2288"
     * > http://www.eclipse.org/newsportal/article.php?id=2288&group=eclipse.
     * technology.xsd#2288</a><BR>
     * <a href="http://www.eclipse.org/newsportal/article.php?id=2218&group=eclipse.technology.xsd#2218"
     * > http://www.eclipse.org/newsportal/article.php?id=2218&group=eclipse.
     * technology.xsd#2218</a><BR>
     * 
     * @param <T>
     * @param source
     * @param targetSchema
     * @return
     * @see XSDConcreteComponent#cloneConcreteComponent(boolean, boolean)
     */
    public static <T extends XSDNamedComponent> T cloneWithAnnotation(final T source, final XSDSchema targetSchema) {
        return cloneWithAnnotation(source, targetSchema, -1);
    }

    /**
     * 
     * @param <T>
     * @param source
     * @param targetSchema
     * @param index
     * @return
     */
    public static <T extends XSDNamedComponent> T cloneWithAnnotation(final T source, final XSDSchema targetSchema,
            final int index) {
        T result = null;
        if (null != source && source instanceof XSDElementDeclaration) {
            result = createClone(source);
            if (index == -1)
                targetSchema.getContents().add((XSDSchemaContent) result);
            else
                targetSchema.getContents().add(index, (XSDSchemaContent) result);
            visitElementDeclaration((XSDElementDeclaration) source, (XSDElementDeclaration) result, targetSchema);
        } else if (null != source && source instanceof XSDTypeDefinition) {
            result = createClone(source);
            if (index == -1)
                targetSchema.getContents().add((XSDSchemaContent) result);
            else
                targetSchema.getContents().add(index, (XSDSchemaContent) result);
            visitTypeDefinition((XSDTypeDefinition) source, (XSDTypeDefinition) result, targetSchema);
        } else if (null != source && source instanceof XSDAttributeDeclaration) {
            result = createClone(source);
            if (index == -1)
                targetSchema.getContents().add((XSDSchemaContent) result);
            else
                targetSchema.getContents().add(index, (XSDSchemaContent) result);
            visitAttributeDeclaration((XSDAttributeDeclaration) source, (XSDAttributeDeclaration) result, targetSchema);
        } else if (null != source && source instanceof XSDAttributeGroupDefinition) {
            result = createClone(source);
            if (index == -1)
                targetSchema.getContents().add((XSDSchemaContent) result);
            else
                targetSchema.getContents().add(index, (XSDSchemaContent) result);
            visitAttributeGroupDefinition((XSDAttributeGroupDefinition) source, (XSDAttributeGroupDefinition) result,
                    targetSchema);
        } else if (null != source && source instanceof XSDModelGroupDefinition) {
            result = createClone(source);
            if (index == -1)
                targetSchema.getContents().add((XSDSchemaContent) result);
            else
                targetSchema.getContents().add(index, (XSDSchemaContent) result);
            visitModelGroupDefinition((XSDModelGroupDefinition) source, (XSDModelGroupDefinition) result, targetSchema);
        }
        return result;
    }

    /**
     * Update all components from XSD model, which refers searchForComponent.
     * 
     * @param baseComponent
     *            where referencer will be searched for. This is used to be
     *            XSDSchema, or Definition
     * @param searchForComponent
     *            any XSD component
     */
    public static void updateModelReferencers(final EObject baseComponent, final XSDConcreteComponent searchForComponent) {
        // update base component before searching for referencers
        if (baseComponent instanceof Definition) {
            final Element element = ((Definition) baseComponent).getElement();
            ((Definition) baseComponent).elementChanged(element);
        } else if (baseComponent instanceof XSDSchema) {
            final Element element = ((XSDSchema) baseComponent).getElement();
            ((XSDSchema) baseComponent).elementChanged(element);
        }

        final EObjectCondition refCondition = new EObjectReferencerCondition(searchForComponent);

        final IQueryResult result = new SELECT(new FROM(baseComponent), new WHERE(refCondition)).execute();

        for (final Object next : result) {
            // These elements are expected to have inner elements which also
            // points to searchForComponent. So we skip the root ones, and will
            // update the inner ones - if such.
            if (next instanceof XSDSchema || next instanceof Definition) {
                continue;
            }
            // XSD API
            if (next instanceof XSDConcreteComponent) {
                final XSDConcreteComponent eObject = (XSDConcreteComponent) next;
                final Element element = eObject.getElement();
                if (element != null && eObject != null && eObject.eResource() != null) {
                    eObject.elementChanged(element);
                }
            }
            // WSDL API
            else if (next instanceof WSDLElement) {
                final WSDLElement eObject = (WSDLElement) next;
                final Element element = eObject.getElement();
                if (element != null && eObject != null && eObject.eResource() != null) {
                    eObject.elementChanged(element);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends XSDNamedComponent> T createClone(final T object) {
        return (T) object.cloneConcreteComponent(true, false);
    }

    private static void visitElementDeclaration(final XSDElementDeclaration source, final XSDElementDeclaration target,
            final XSDSchema schema) {
        final XSDAnnotation annotation = source.getAnnotation();
        if (null != annotation) {
            copyAnnotations(annotation, target.getAnnotation(), schema.getDocument());
        }

        // process type definition if exists
        final XSDTypeDefinition typeDefinition = source.getAnonymousTypeDefinition();

        if (null != typeDefinition) {
            visitTypeDefinition(typeDefinition, target.getAnonymousTypeDefinition(), schema);
        }

        visitIdentityConstraint(source.getIdentityConstraintDefinitions(), target.getIdentityConstraintDefinitions(), schema);

        if (source.isGlobal())
            return;

        final XSDSchema sourceSchema = source.getSchema();
        if (null == sourceSchema)
            throw new RuntimeException("Schema for source component is null"); //$NON-NLS-1$

        XSDForm sourceForm = sourceSchema.isSetElementFormDefault() ? sourceSchema.getElementFormDefault() : null;
        if (source.isSetForm())
            sourceForm = source.getForm();

        final XSDForm targetForm = schema.isSetElementFormDefault() ? schema.getElementFormDefault() : null;

        if ((XSDForm.QUALIFIED_LITERAL == sourceForm && XSDForm.QUALIFIED_LITERAL != targetForm)
                || (XSDForm.QUALIFIED_LITERAL == targetForm && XSDForm.QUALIFIED_LITERAL != sourceForm)) {
            target.setForm(null == sourceForm ? XSDForm.UNQUALIFIED_LITERAL : sourceForm);
        }
    }

    private static void visitTypeDefinition(final XSDTypeDefinition srcType, final XSDTypeDefinition targetType,
            final XSDSchema schema) {
        if (srcType instanceof XSDSimpleTypeDefinition) {
            Assert.isTrue(targetType instanceof XSDSimpleTypeDefinition);
            visitSimpleTypeDefinition((XSDSimpleTypeDefinition) srcType, (XSDSimpleTypeDefinition) targetType, schema);
        } else {
            Assert.isTrue(targetType instanceof XSDComplexTypeDefinition);
            visitComplexTypeDefinition((XSDComplexTypeDefinition) srcType, (XSDComplexTypeDefinition) targetType, schema);
        }
    }

    private static void visitSimpleTypeDefinition(final XSDSimpleTypeDefinition srcType,
            final XSDSimpleTypeDefinition targetType, final XSDSchema schema) {

        final XSDAnnotation annotation = srcType.getAnnotation();
        if (null != annotation) {
            copyAnnotations(annotation, targetType.getAnnotation(), schema.getDocument());
        }

        final XSDAnnotation derivationAnnotation = srcType.getDerivationAnnotation();
        if (null != derivationAnnotation) {
            copyAnnotations(derivationAnnotation, targetType.getDerivationAnnotation(), schema.getDocument());
        }

        final XSDSimpleTypeDefinition baseType = srcType.getBaseTypeDefinition();
        if (null == baseType.getName()) {
            visitSimpleTypeDefinition(baseType, targetType.getBaseTypeDefinition(), schema);
        }
    }

    private static void visitComplexTypeDefinition(final XSDComplexTypeDefinition srcType,
            final XSDComplexTypeDefinition targetType, final XSDSchema schema) {
        final XSDAnnotation annotation = srcType.getAnnotation();

        if (null != annotation) {
            copyAnnotations(annotation, targetType.getAnnotation(), schema.getDocument());
        }

        final XSDAnnotation derivationAnnotation = srcType.getDerivationAnnotation();
        if (null != derivationAnnotation) {
            copyAnnotations(derivationAnnotation, targetType.getDerivationAnnotation(), schema.getDocument());
        }

        final XSDAnnotation contentAnnotation = srcType.getContentAnnotation();
        if (null != contentAnnotation) {
            copyAnnotations(contentAnnotation, targetType.getContentAnnotation(), schema.getDocument());
        }

        final XSDTypeDefinition srcbaseType = srcType.getBaseType();
        final XSDTypeDefinition targetbaseType = srcType.getBaseType();
        if (null == srcbaseType.getName())
            visitTypeDefinition(srcbaseType, targetbaseType, schema);

        // process content
        final XSDComplexTypeContent srcContent = srcType.getContent();
        final XSDComplexTypeContent targetContent = targetType.getContent();
        if (srcContent != null) {
            if (srcContent instanceof XSDParticle) {
                final XSDParticle srcParticle = (XSDParticle) srcContent;
                final XSDParticle targetParticle = (XSDParticle) targetContent;
                visitParticle(srcParticle, targetParticle, schema);
            } else if (srcContent instanceof XSDSimpleTypeDefinition) {
                visitSimpleTypeDefinition((XSDSimpleTypeDefinition) srcContent, (XSDSimpleTypeDefinition) targetContent, schema);
            }
        }
        // process attribute group content
        visitAttributeGroupContent(srcType.getAttributeContents(), targetType.getAttributeContents(), schema);

    }

    private static void visitAttributeDeclaration(final XSDAttributeDeclaration source, final XSDAttributeDeclaration target,
            final XSDSchema targetSchema) {
        final XSDAnnotation annotation = source.getAnnotation();
        if (null != annotation) {
            copyAnnotations(annotation, target.getAnnotation(), targetSchema.getDocument());
        }

        final XSDSimpleTypeDefinition typeDefinition = source.getAnonymousTypeDefinition();
        if (null != typeDefinition) {
            visitSimpleTypeDefinition(typeDefinition, target.getAnonymousTypeDefinition(), targetSchema);
        }

        if (source.isGlobal())
            return;

        final XSDSchema sourceSchema = source.getSchema();
        if (null == sourceSchema)
            throw new RuntimeException("Schema for source component is null"); //$NON-NLS-1$

        XSDForm sourceForm = sourceSchema.isSetAttributeFormDefault() ? sourceSchema.getAttributeFormDefault() : null;
        if (source.isSetForm())
            sourceForm = source.getForm();

        final XSDForm targetForm = targetSchema.isSetAttributeFormDefault() ? targetSchema.getAttributeFormDefault() : null;

        if ((XSDForm.QUALIFIED_LITERAL == sourceForm && XSDForm.QUALIFIED_LITERAL != targetForm)
                || (XSDForm.QUALIFIED_LITERAL == targetForm && XSDForm.QUALIFIED_LITERAL != sourceForm)) {
            target.setForm(null == sourceForm ? XSDForm.UNQUALIFIED_LITERAL : sourceForm);
        }
    }

    private static void visitAttributeGroupDefinition(final XSDAttributeGroupDefinition source,
            final XSDAttributeGroupDefinition target, final XSDSchema schema) {
        final XSDAnnotation annotation = source.getAnnotation();
        if (null != annotation) {
            copyAnnotations(annotation, target.getAnnotation(), schema.getDocument());
        }

        if (!source.isAttributeGroupDefinitionReference())
            visitAttributeGroupContent(source.getContents(), target.getContents(), schema);
    }

    private static void visitModelGroupDefinition(final XSDModelGroupDefinition source, final XSDModelGroupDefinition target,
            final XSDSchema targetSchema) {
        final XSDAnnotation annotation = source.getAnnotation();
        if (null != annotation) {
            copyAnnotations(annotation, target.getAnnotation(), targetSchema.getDocument());
        }

        if (!source.isModelGroupDefinitionReference())
            visitModelGroup(source.getModelGroup(), target.getModelGroup(), targetSchema);
    }

    private static void visitParticle(final XSDParticle srcParticle, final XSDParticle targetParticle, final XSDSchema schema) {
        final XSDParticleContent srcContent = srcParticle.getContent();
        if (null != srcContent) {
            final XSDParticleContent targetContent = targetParticle.getContent();
            if (srcContent instanceof XSDModelGroup) {
                visitModelGroup((XSDModelGroup) srcContent, (XSDModelGroup) targetContent, schema);
            } else if (srcContent instanceof XSDElementDeclaration) {
                visitElementDeclaration((XSDElementDeclaration) srcContent, (XSDElementDeclaration) targetContent, schema);
            } else if (srcContent instanceof XSDModelGroupDefinition) {
                visitModelGroupDefinition((XSDModelGroupDefinition) srcContent, (XSDModelGroupDefinition) targetContent, schema);
            }
        }
    }

    private static void visitModelGroup(final XSDModelGroup srcModelGroup, final XSDModelGroup targetModelGroup,
            final XSDSchema source) {
        final List<XSDParticle> srcParticles = (srcModelGroup).getContents();
        final Iterator<XSDParticle> targetParticles = (targetModelGroup).getContents().iterator();
        for (final XSDParticle srcParticle : srcParticles) {
            final XSDParticle targetParticle = targetParticles.next();
            visitParticle(srcParticle, targetParticle, source);
        }
    }

    private static void visitAttributeGroupContent(final Collection<XSDAttributeGroupContent> source,
            final Collection<XSDAttributeGroupContent> target, final XSDSchema schema) {
        final Iterator<XSDAttributeGroupContent> targetAttributeContent = target.iterator();

        XSDAttributeGroupDefinition targetAGContent = null;
        XSDAttributeGroupContent targetComp = null;

        for (final XSDAttributeGroupContent sourceComp : source) {
            targetComp = targetAttributeContent.next();

            if (sourceComp instanceof XSDAttributeGroupDefinition) {
                targetAGContent = (XSDAttributeGroupDefinition) targetComp;
                visitAttributeGroupDefinition((XSDAttributeGroupDefinition) sourceComp, targetAGContent, schema);
                continue;
            } else if (sourceComp instanceof XSDAttributeUse) {
                visitAttributeDeclaration(((XSDAttributeUse) sourceComp).getContent(), ((XSDAttributeUse) targetComp)
                        .getContent(), schema);
            }
        }
    }

    private static void visitIdentityConstraint(final EList<XSDIdentityConstraintDefinition> src,
            final EList<XSDIdentityConstraintDefinition> target, final XSDSchema schema) {
        final Iterator<XSDIdentityConstraintDefinition> targetIdentity = target.iterator();
        for (final XSDIdentityConstraintDefinition identity : src) {
            final XSDAnnotation srcAnnotation = identity.getAnnotation();
            if (null != srcAnnotation) {
                copyAnnotations(srcAnnotation, targetIdentity.next().getAnnotation(), schema.getDocument());
            }
        }

    }

    public static void copyAnnotations(final XSDAnnotation annotation, final XSDAnnotation newAnnotation, final Document document) {
        if (newAnnotation == null || annotation == null) {
            return;
        }
        importDOMElements(annotation.getApplicationInformation(), newAnnotation.getElement(), document);
        importDOMElements(annotation.getUserInformation(), newAnnotation.getElement(), document);
    }

    private static void importDOMElements(final EList<Element> infos, final Element targetElement, final Document document) {
        if (document == null) {
            return;
        }
        for (final Element element : infos) {
            // internally these methods do casts to NodeImpl, so make a check
            // before using them
            if (element instanceof NodeImpl) {
                final Node importedNode = document.importNode(element, true);
                targetElement.appendChild(importedNode);
            }
        }
    }

    /**
     * Check whether eObject contains Element ref, or Attribute ref.
     */
    public static boolean isReference(final IModelObject modelObject) {
        if (modelObject == null) {
            return false;
        }
        final EObject eObject = modelObject.getComponent();
        if (eObject instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle) eObject;
            if (particle.getContent() instanceof XSDElementDeclaration) {
                final XSDElementDeclaration element = (XSDElementDeclaration) particle.getContent();
                return element.isElementDeclarationReference();
            }
        } else if (eObject instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) eObject;
            return attribute.isAttributeDeclarationReference();
        }
        return false;
    }

    /**
     * Generates the name for a new xmlns:XXX attribute, to be added to the
     * given schema
     * 
     * @param schema
     *            for which the attribute name is generated
     * @return the newly generated name
     */
    public static String generateSchemaNamespacePrefix(final XSDSchema schema) {
        for (int i = 0; i >= 0 && i < 10000; i++) {
            final String prefix = new StringBuilder(NAMESPACE_PREFIX_PREFIX).append(i).toString();
            if (!schema.getQNamePrefixToNamespaceMap().containsKey(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    public static boolean isSchemaForSchemaMissing(final IModelObject modelObject) {
        if (modelObject == null) {
            return false;
        }
        final EObject component = modelObject.getComponent();
        if (component instanceof XSDConcreteComponent) {
            return isSchemaForSchemaMissing((XSDConcreteComponent) component);
        }
        return false;

    }

    public static boolean isSchemaForSchemaMissing(final XSDConcreteComponent component) {
        if (component == null) {
            return false;
        }
        final XSDSchemaImpl schema = (XSDSchemaImpl) (component).getSchema();
        if (schema != null && schema.getSchemaForSchema() == null) {
            return true;
        }
        return false;

    }

    public static Collection<ISchema> getAllVisibleSchemas(ISchema schema) {
        final Collection<ISchema> result = new HashSet<ISchema>(schema.getAllReferredSchemas());
        IModelObject parent = schema.getParent();
        if (!(parent instanceof IDescription)) {
            return result;
        }

        for (ISchema currentSchema : ((IDescription) parent).getAllVisibleSchemas()) {
            if (currentSchema.getNamespace() == null || !currentSchema.getNamespace().equals(schema.getNamespace())) {
                continue;
            }
            result.add(currentSchema);
        }

        return result;
    }

    public static boolean isSchemaElementMissing(final ISchema schema) {
        if (schema == null) {
            return true;
        }
        final XSDSchema xsdSchema = schema.getComponent();
        return isSchemaElementMissing(xsdSchema);
    }

    public static boolean isSchemaElementMissing(final XSDSchema schema) {
        return schema == null || schema.getElement() == null;
    }

    public static boolean hasXmlTag(final Document document) {
        if (document == null) {
            return false;
        }
        final Node firstChild = document.getFirstChild();
        if (firstChild == null || Node.PROCESSING_INSTRUCTION_NODE != firstChild.getNodeType()) {
            return false;
        }
        return XML_TAG_NAME.equals(((ProcessingInstruction) firstChild).getTarget());
    }

    public static ISchema getReferedSchema(final ISchema schema, final String namespace) {
        for (final ISchema refSchema : schema.getAllReferredSchemas()) {
            final boolean equalNS = refSchema.getNamespace() == null ? namespace == null : refSchema.getNamespace().equals(
                    namespace);
            if (equalNS) {
                return refSchema;
            }
        }
        return null;
    }

    /**
     * Return the index in sourcePage for a given XSDConcreteComponent
     * 
     * @param xsdComponent
     *            must be NOT null
     * @return the start index for a given XSDConcreteComponent, or -1 otherwise
     */
    public static int getIndexInSourcePage(final XSDConcreteComponent xsdComponent) {
        final Element element = xsdComponent.getElement();
        return getIndexInSourcePage(element);
    }

    /**
     * Return the index in sourcePage for a given Element
     * 
     * @param element
     * @return the start index for a given Element, or -1 otherwise
     */
    public static int getIndexInSourcePage(final Element element) {
        IndexedRegion item = null;
        if (element instanceof IndexedRegion) {
            item = (IndexedRegion) element;
            return item.getStartOffset();
        }

        return -1;
    }

    /**
     * Return true if the searched EMF object is a part of DataTypes UI tree,
     * otherwise false
     * 
     * @param searchedObject
     *            is an EMF object
     * @return boolean flag which value depends on the type of the
     *         "searchObject" parameter
     */
    public static boolean hasCorrespondingITreeNode(final EObject searchedObject) {
        if (searchedObject instanceof XSDSimpleTypeDefinition && ((XSDSimpleTypeDefinition) searchedObject).getName() != null) {
            return true;
        }
        if (searchedObject instanceof XSDComplexTypeDefinition && ((XSDComplexTypeDefinition) searchedObject).getName() != null) {
            return true;
        }
        if (searchedObject instanceof XSDAttributeDeclaration) {
            return true;
        }
        if (searchedObject instanceof XSDElementDeclaration) {
            return true;
        }
        if (searchedObject instanceof XSDSchemaImpl) {
            return true;
        }

        return false;
    }

    public static boolean isAnonymous(final XSDElementDeclaration elementDeclaration) {
        final XSDTypeDefinition anonymousTypeDefinition = elementDeclaration.getAnonymousTypeDefinition();
        return isElementDeclarationTypeAnonymous(anonymousTypeDefinition);
    }

    private static boolean isElementDeclarationTypeAnonymous(final XSDTypeDefinition anonymousTypeDefinition) {
        if (anonymousTypeDefinition == null || anonymousTypeDefinition.getBaseType() == null) {
            return false;
        }
        final String baseTypeName = anonymousTypeDefinition.getBaseType().getName();
        return "anyType".equals(baseTypeName) //$NON-NLS-1$
                || anonymousTypeDefinition.getComplexType() instanceof XSDComplexTypeDefinition;
    }

    public static boolean isRestriction(final XSDSimpleTypeDefinition simpleType) {
        return simpleType != null && simpleType.getFacets() != null && !simpleType.getFacets().isEmpty();
    }

    public static boolean isAnonymous(final XSDTypeDefinition typeDefinition) {
        final XSDTypeDefinition xsdTypeDefinition = typeDefinition;
        final XSDConcreteComponent container = xsdTypeDefinition.getContainer();
        return null == xsdTypeDefinition.getName() && !(container instanceof XSDSchema);
    }

}