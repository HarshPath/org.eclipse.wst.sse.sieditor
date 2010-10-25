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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * 
 * 
 * 
 */
public class CopyTypeCommand extends AbstractNotificationOperation {

    private final XSDNamedComponent _sourceType;
    private AbstractType _clone;
    private final IModelObject _parent;
    private Map<String, XSDSchema> _schemasUsed;
    private final ISchema _targetSchema;
    private final String _name;

    // List of all the components copied to targetschema
    private final HashSet<XSDNamedComponent> _inconsistentClones = new HashSet<XSDNamedComponent>();
    private XSDNamedComponent _clonedComponent;

    private IStatus _executionStatus;

    private static final String XMLNS_PREFIX_BASE = "imp"; //$NON-NLS-1$

    public CopyTypeCommand(final IModelRoot root, final IModelObject modelObject, final XSDNamedComponent typeToBeCopied, final ISchema targetSchema,
            final String name) {
        super(root, modelObject, Messages.CopyTypeCommand_copy_type_comand_label);
        this._sourceType = typeToBeCopied;
        this._parent = modelObject;
        this._targetSchema = targetSchema;
        this._name = name;
    }

    @Override
    public boolean canExecute() {
        return null != getModelRoot() && null != _parent && null != _sourceType && _sourceType.eResource()!=null;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<XSDSchema> getInlineSchemas() {
        final ArrayList<XSDSchema> inlineSchemas = new ArrayList<XSDSchema>();

        if (getModelRoot() instanceof IXSDModelRoot) {
            final IXSDModelRoot modelRoot = (IXSDModelRoot) getModelRoot();
            final EList<XSDTypeDefinition> typeDefinitions = modelRoot.getSchema().getComponent().getTypeDefinitions();
            for (final XSDTypeDefinition typeDef : typeDefinitions) {
                final XSDSchema schema = typeDef.getSchema();
                if (!inlineSchemas.contains(schema)) {
                    inlineSchemas.add(schema);
                }
            }

        } else {
            final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot();
            final Definition definition = ((Description) modelRoot.getDescription()).getComponent();
            final Types types = definition.getETypes();
            if (null != types) {
                inlineSchemas.addAll(types.getSchemas());
            }
        }

        return inlineSchemas;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$

        _executionStatus = Status.OK_STATUS;
        _schemasUsed = new HashMap<String, XSDSchema>();
        final XSDNamedComponent component = _sourceType;
        final boolean isGlobal = isGlobal(component);

        final XSDSchema schema = isGlobal ? null : _targetSchema.getComponent();

        // source in visit methods can be null only when call is from this
        // method
        if (component instanceof XSDElementDeclaration) {
            if (debug)
                Logger.getDebugTrace().trace("", "Begin copy Element " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            visitElementDeclaration((XSDElementDeclaration) component, schema);
            if (debug)
                Logger.getDebugTrace().trace("", "End copy Element " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (component instanceof XSDTypeDefinition) {
            if (debug)
                Logger.getDebugTrace().trace("", "Begin copy Type " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            visitTypeDefinition((XSDTypeDefinition) component, schema);
            if (debug)
                Logger.getDebugTrace().trace("", "End copy Type " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (component instanceof XSDAttributeDeclaration) {
            if (debug)
                Logger.getDebugTrace().trace("", "Begin copy Attribute " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            visitAttributeDeclaration((XSDAttributeDeclaration) component, schema);
            if (debug)
                Logger.getDebugTrace().trace("", "End copy Attribute " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // update all the references to the target schema components
        if (debug)
            Logger.getDebugTrace().trace("", "Update references"); //$NON-NLS-1$ //$NON-NLS-2$
        updateComponents();

        if (debug)
            Logger.getDebugTrace().trace("", "Refresh Schemas"); //$NON-NLS-1$ //$NON-NLS-2$
        if (getModelRoot() instanceof IWsdlModelRoot) {
            final Description description = (Description) ((IWsdlModelRoot) getModelRoot()).getDescription();
            description.refreshSchemas();
        }

        if (isGlobal && _clonedComponent != null) {
            if (debug)
                Logger.getDebugTrace().trace("", "Get the copied Type"); //$NON-NLS-1$ //$NON-NLS-2$
            _clone = (AbstractType) _targetSchema.getType(_clonedComponent instanceof XSDElementDeclaration, _clonedComponent
                    .getName());
        }

        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$

        if (_executionStatus.isOK() && null == _clone && isGlobal) {
            _executionStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.CopyTypeCommand_msg_can_not_copy_type_X, _sourceType.getName()));
        }
        return _executionStatus;
    }

    private boolean isGlobal(final XSDNamedComponent component) {
        final boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        if (component instanceof XSDTypeDefinition)
            return null != component.getName();
        else if (component instanceof XSDElementDeclaration)
            return ((XSDElementDeclaration) component).isGlobal();
        else if (component instanceof XSDAttributeDeclaration)
            return ((XSDAttributeDeclaration) component).isGlobal();
        else if (component instanceof XSDAttributeGroupDefinition)
            return !((XSDAttributeGroupDefinition) component).isAttributeGroupDefinitionReference();
        else if (component instanceof XSDModelGroupDefinition)
            return !((XSDModelGroupDefinition) component).isModelGroupDefinitionReference();
        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return false;
    }

    public AbstractType getCopiedType() {
        final boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return _clone;
    }

    private boolean visitAttributeGroup(final XSDAttributeGroupDefinition attributeGroup, final XSDSchema source) {
        final boolean debug = Logger.isDebugEnabled();
        if (debug)
            Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$

        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        if (isGlobal(attributeGroup)) {
            // check if attributeGroup already exists
            if (debug)
                Logger.getDebugTrace().trace("", "transversing Global AttributeGroup " + attributeGroup.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            final boolean copyToTarget = isTargetSchemaComponent(attributeGroup);
            if (copyToTarget)
                if (debug)
                    Logger.getDebugTrace().trace("", "source schema component"); //$NON-NLS-1$ //$NON-NLS-2$

            final XSDAttributeGroupDefinition match = contains(attributeGroup, XSDAttributeGroupDefinition.class, copyToTarget);
            if (null != match) {
                if (debug)
                    Logger.getDebugTrace().trace("", "component already exists"); //$NON-NLS-1$ //$NON-NLS-2$				
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : attributeGroup.getTargetNamespace(), null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, attributeGroup.getTargetNamespace(), attributeGroup))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(attributeGroup, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (attributeGroup.isAttributeGroupDefinitionReference()) {
            final XSDAttributeGroupDefinition resolvedAttributeGroupDefinition = attributeGroup
                    .getResolvedAttributeGroupDefinition();
            if (!(null == resolvedAttributeGroupDefinition || isSynthetic(resolvedAttributeGroupDefinition))) {
                visitAttributeGroup(resolvedAttributeGroupDefinition, copiedTo);
                needsUpdate = isTargetSchemaComponent(resolvedAttributeGroupDefinition);
            }
        } else {
            if (visitAttributeGroupContent(attributeGroup.getContents(), copiedTo))
                _inconsistentClones.add(clone);
        }

        if (debug)
            Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return needsUpdate;
    }

    private boolean visitAttributeDeclaration(final XSDAttributeDeclaration attribute, final XSDSchema source) {
        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        if (isGlobal(attribute)) {
            // check if attributeGroup already exists
            final boolean copyToTarget = isTargetSchemaComponent(attribute);
            final XSDAttributeDeclaration match = contains(attribute, XSDAttributeDeclaration.class, copyToTarget);
            if (null != match) {
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : attribute.getTargetNamespace(), null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, attribute.getTargetNamespace(), attribute))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(attribute, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        XSDSimpleTypeDefinition typeDefinition = attribute.getAnonymousTypeDefinition();
        boolean refersTargetComp = false;

        if (null == typeDefinition) {
            typeDefinition = attribute.getTypeDefinition();
            // If an attribute refers to a target schema type and is global then
            // add to inconsistencies
            // else its local attribute tell the parent of this attribute to add
            // itself
            if ((!(null == typeDefinition || isSynthetic(typeDefinition))) && isTargetSchemaComponent(typeDefinition)) {
                if (attribute.isGlobal()) {
                    _inconsistentClones.add(clone);
                    refersTargetComp = true;
                } else
                    needsUpdate = true;
            }
        }

        if (!(null == typeDefinition || isSynthetic(typeDefinition))) {
            refersTargetComp = visitTypeDefinition(typeDefinition, copiedTo);
            // If an attribute has anonymous type that refers to a target schema
            // type
            // and if attribute is global then add to inconsistencies
            // else its local attribute tell the parent of this attribute to add
            // itself
            if (refersTargetComp && null != attribute.getAnonymousTypeDefinition()) {
                if (attribute.isGlobal()) {
                    _inconsistentClones.add(clone);
                } else
                    needsUpdate = true;
            }
        }

        // process referred element declaration if exists
        if (attribute.isAttributeDeclarationReference()) {
            final XSDAttributeDeclaration resolvedAttribute = attribute.getResolvedAttributeDeclaration();
            if (!(null == resolvedAttribute || isSynthetic(resolvedAttribute))) {
                visitAttributeDeclaration(resolvedAttribute, copiedTo);
                if (isTargetSchemaComponent(resolvedAttribute))
                    needsUpdate = true;
            }
        }

        return needsUpdate;
    }

    private boolean visitElementDeclaration(final XSDElementDeclaration element, final XSDSchema source) {

        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        if (isGlobal(element)) {
            // check if element already exists
            final boolean copyToTarget = isTargetSchemaComponent(element);
            final XSDElementDeclaration match = contains(element, XSDElementDeclaration.class, copyToTarget);
            if (null != match) {
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : element.getTargetNamespace(), null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, element.getTargetNamespace(), element))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(element, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        // process type definition if exists
        XSDTypeDefinition typeDefinition = element.getAnonymousTypeDefinition();
        boolean refersTargetComp = false;

        if (null == typeDefinition) {
            typeDefinition = element.getTypeDefinition();
            // If an element refers to a target schema type and is global then
            // add element to
            // inconsistencies else if its local element tell the parent of this
            // element to add itself
            if ((!(null == typeDefinition || isSynthetic(typeDefinition))) && isTargetSchemaComponent(typeDefinition)) {
                if (element.isGlobal()) {
                    _inconsistentClones.add(clone);
                    refersTargetComp = true;
                } else
                    needsUpdate = true;
            }
        }

        if (!(null == typeDefinition || isSynthetic(typeDefinition))) {
            refersTargetComp = visitTypeDefinition(typeDefinition, copiedTo);
            // If an element has anonymous type that refers to a target schema
            // type
            // and if element is global then add element to inconsistencies
            // else it is local element tell the parent of this element to add
            // itself
            if (refersTargetComp && null != element.getAnonymousTypeDefinition()) {
                if (element.isGlobal()) {
                    _inconsistentClones.add(clone);
                } else
                    needsUpdate = true;
            }
        }

        // process referred element declaration if exists
        if (element.isElementDeclarationReference()) {
            final XSDElementDeclaration resolvedElementDeclaration = element.getResolvedElementDeclaration();
            if (!(null == resolvedElementDeclaration || isSynthetic(resolvedElementDeclaration))) {
                visitElementDeclaration(resolvedElementDeclaration, copiedTo);
                if (isTargetSchemaComponent(resolvedElementDeclaration))
                    needsUpdate = true;
            }
        }
        return needsUpdate;
    }

    private boolean visitModelGroupDefinition(final XSDModelGroupDefinition modelGroupDefinition, final XSDSchema source) {

        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        if (isGlobal(modelGroupDefinition)) {
            // check if element already exists
            final boolean copyToTarget = isTargetSchemaComponent(modelGroupDefinition);
            final XSDModelGroupDefinition match = contains(modelGroupDefinition, XSDModelGroupDefinition.class, copyToTarget);
            if (null != match) {
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : modelGroupDefinition.getTargetNamespace(),
                            null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, modelGroupDefinition.getTargetNamespace(), modelGroupDefinition))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(modelGroupDefinition, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (modelGroupDefinition.isModelGroupDefinitionReference()) {
            final XSDModelGroupDefinition resolvedModelGroupDefinition = modelGroupDefinition.getResolvedModelGroupDefinition();
            if (!(null == resolvedModelGroupDefinition.eContainer() || isSynthetic(resolvedModelGroupDefinition))) {
                visitModelGroupDefinition(resolvedModelGroupDefinition, copiedTo);
                needsUpdate = isTargetSchemaComponent(resolvedModelGroupDefinition);
            }
        } else {
            final XSDModelGroup content = modelGroupDefinition.getModelGroup();
            if (null != content && visitModelGroup(content, copiedTo))
                _inconsistentClones.add(clone);
        }
        return needsUpdate;
    }

    private boolean visitTypeDefinition(final XSDTypeDefinition type, final XSDSchema source) {
        if (null != type) {
            if (XSDConstants.isSchemaForSchemaNamespace(type.getTargetNamespace()))
                return false;
        }

        if (type instanceof XSDSimpleTypeDefinition) {
            return visitSimpleTypeDefinition((XSDSimpleTypeDefinition) type, source);
        } else {
            return visitComplexTypeDefinition((XSDComplexTypeDefinition) type, source);
        }
    }

    private boolean visitSimpleTypeDefinition(final XSDSimpleTypeDefinition simpleType, final XSDSchema source) {

        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        final boolean globalType = isGlobal(simpleType);

        if (globalType) {
            // check if element already exists
            final boolean copyToTarget = isTargetSchemaComponent(simpleType);
            final XSDSimpleTypeDefinition match = contains(simpleType, XSDSimpleTypeDefinition.class, copyToTarget);
            if (null != match) {
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : simpleType.getTargetNamespace(), null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, simpleType.getTargetNamespace(), simpleType))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(simpleType, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        final Collection<XSDTypeDefinition> types = new ArrayList<XSDTypeDefinition>();
        final XSDTypeDefinition baseType = simpleType.getBaseType();
        if (null != baseType)
            types.add(baseType);
        for (final XSDSimpleTypeDefinition type : simpleType.getMemberTypeDefinitions()) {
            types.add(type);
        }
        if (null != simpleType.getItemTypeDefinition())
            types.add(simpleType.getItemTypeDefinition());

        for (final XSDTypeDefinition type : types) {
            if (!isSynthetic(type)) {
                final boolean refersTargetComp = visitTypeDefinition(type, copiedTo);
                final boolean isTargetComp = null == type.getName() ? false : isTargetSchemaComponent(type);
                if (globalType && (isTargetComp || refersTargetComp)) {
                    _inconsistentClones.add(clone);
                } else if (!globalType && (isTargetComp || refersTargetComp)) {
                    needsUpdate = true;
                }
            }
        }

        return needsUpdate;
    }

    private boolean visitComplexTypeDefinition(final XSDComplexTypeDefinition complexType, final XSDSchema source) {

        XSDSchema copiedTo;
        boolean needsUpdate = false;
        XSDNamedComponent clone = null;

        final boolean globalType = isGlobal(complexType);

        if (null != complexType.getName()) {
            // check if element already exists
            final boolean copyToTarget = isTargetSchemaComponent(complexType);
            final XSDComplexTypeDefinition match = contains(complexType, XSDComplexTypeDefinition.class, copyToTarget);
            if (null != match) {
                // return if source is null
                if (null == source || (copyToTarget && source.equals(_targetSchema.getComponent()))) {
                    return needsUpdate;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, copyToTarget ? _targetSchema.getNamespace() : complexType.getTargetNamespace(), null);
                    return needsUpdate;
                }
            } else {
                if ((!copyToTarget) && ensureImport(source, complexType.getTargetNamespace(), complexType))
                    return needsUpdate;
            }
            // copy and create reference in source
            copiedTo = createClone(complexType, copyToTarget);
            clone = _recentClone;
            if (null != source)
                ensureImport(source, copiedTo.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        // process content
        final XSDComplexTypeContent content = complexType.getContent();
        boolean refersTargetComp = false;
        boolean isTargetComp = false;

        if (content != null) {
            if (content instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle) content;
                refersTargetComp = visitParticle(particle, copiedTo);
                final XSDTypeDefinition baseType = complexType.getBaseType();

                if (!(null == baseType || isSynthetic(baseType))) {
                    refersTargetComp |= visitTypeDefinition(baseType, copiedTo);
                    isTargetComp |= null == baseType.getName() ? false : isTargetSchemaComponent(baseType);
                }
            } else {
                refersTargetComp |= visitSimpleTypeDefinition((XSDSimpleTypeDefinition) content, copiedTo);
            }
        }

        final List<XSDAttributeGroupContent> attributeContents = complexType.getAttributeContents();
        refersTargetComp |= visitAttributeGroupContent(attributeContents, copiedTo);

        if (globalType && (isTargetComp || refersTargetComp)) {
            _inconsistentClones.add(clone);
        } else if (!globalType && (isTargetComp || refersTargetComp)) {
            needsUpdate = true;
        }
        return needsUpdate;
    }

    private boolean visitParticle(final XSDParticle particle, final XSDSchema source) {
        final XSDParticleContent content = particle.getContent();
        if (content instanceof XSDModelGroup) {
            return visitModelGroup((XSDModelGroup) content, source);
        } else if (content instanceof XSDElementDeclaration) {
            return visitElementDeclaration((XSDElementDeclaration) content, source);
        } else if (content instanceof XSDModelGroupDefinition) {
            return visitModelGroupDefinition((XSDModelGroupDefinition) content, source);
        }
        return false;
    }

    private boolean visitModelGroup(final XSDModelGroup modelGroup, final XSDSchema source) {
        final List<XSDParticle> particles = (modelGroup).getContents();
        boolean refersTargetComp = false;
        for (final XSDParticle particle : particles) {
            refersTargetComp |= visitParticle(particle, source);
        }
        return refersTargetComp;
    }

    private boolean visitAttributeGroupContent(final Collection<XSDAttributeGroupContent> attributeContents, final XSDSchema source) {
        boolean refersTargetComp = false;
        for (final XSDAttributeGroupContent attributeGroupContent : attributeContents) {
            if (attributeGroupContent instanceof XSDAttributeGroupDefinition) {
                refersTargetComp |= visitAttributeGroup((XSDAttributeGroupDefinition) attributeGroupContent, source);
                continue;
            }

            final XSDAttributeUse attributeUse = (XSDAttributeUse) attributeGroupContent;

            final XSDAttributeDeclaration attributeDeclaration = attributeUse.getContent();

            if (null != attributeDeclaration) {
                refersTargetComp |= visitAttributeDeclaration(attributeDeclaration, source);
            }
        }
        return refersTargetComp;
    }

    private XSDNamedComponent _recentClone = null;

    /**
     * Creates a clone of given component in given schema and returns the clone
     * 
     * @param <T>
     * @param schema
     * @param component
     * @return
     */
    private XSDSchema createClone(final XSDNamedComponent component, final boolean copyToTarget) {
        final XSDSchema schema;
        schema = copyToTarget ? _targetSchema.getComponent() : ensureSchema(component.getTargetNamespace());

        final XSDNamedComponent clone = EmfXsdUtils.cloneWithAnnotation(component, schema);

        clone.setTargetNamespace(copyToTarget ? _targetSchema.getNamespace() : component.getTargetNamespace());

        if (copyToTarget)
            _inconsistentClones.add(clone);

        // Set new name to the cloned component if its a source component
        if (copyToTarget && component == _sourceType) {
            Assert.isTrue(null == _clonedComponent);
            _clonedComponent = clone;
            (clone).setName(_name);
        }
        _recentClone = clone;
        return schema;
    }

    private <T extends XSDNamedComponent> T contains(final XSDNamedComponent component, final Class<T> metaClass,
            final boolean copyToTarget) {
        final String namespace = component.getTargetNamespace();

        final Collection<XSDSchema> schemas;
        if (copyToTarget) {
            schemas = new ArrayList<XSDSchema>(1);
            schemas.add(_targetSchema.getComponent());
        } else {
            // Get schemas with same namespace
            schemas = CollectionTypeUtils.findAll(getInlineSchemas(), new Condition<XSDSchema>() {

                public boolean isSatisfied(final XSDSchema in) {
                    return namespace.equals(in.getTargetNamespace());
                }

            });
        }

        // Check is type is visible in contained schemas or included schemas in
        // contained schemas
        T result = null;
        for (final XSDSchema schema : schemas) {
            result = contains(schema, component, new ArrayList<String>(), metaClass, copyToTarget);
            if (null != result)
                return result;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends XSDNamedComponent> T contains(final XSDSchema schema, final XSDNamedComponent component,
            final Collection<String> locations, final Class<T> metaClass, final boolean copyToTarget) {
        String name = component.getName();
        // After copy the component name will be @field(_name) and not the old
        // name
        // handle this special case case.
        if (copyToTarget && (null != _sourceType.getName() && _sourceType.getName().equals(name))
                && metaClass.isInstance(_sourceType)) {
            name = _name;
        }

        final EList<XSDSchemaContent> contents = schema.getContents();
        final ArrayList<XSDInclude> includes = new ArrayList<XSDInclude>();

        for (final XSDSchemaContent schemaContent : contents) {
            if (metaClass.isInstance(schemaContent)) {
                if (name.equals(((XSDNamedComponent) schemaContent).getName())) {
                    return (T) schemaContent;
                }
            }
        }

        T result = null;
        if (!copyToTarget) {
            for (final XSDInclude include : includes) {
                final XSDSchema includedSchema = include.getResolvedSchema();
                if (null != includedSchema && !locations.contains(includedSchema.getSchemaLocation())) {
                    locations.add(includedSchema.getSchemaLocation());
                    result = contains(includedSchema, component, locations, metaClass, copyToTarget);
                    if (null != result)
                        return result;
                }
            }
        }

        return result;
    }

    private boolean ensureImport(final XSDSchema schema, final String nameSpace, final XSDNamedComponent component) {
        if (null == component) {
            // for cloned components or visible components using includes
            if (schema.getTargetNamespace().equals(nameSpace))
                return false;

            final Collection<XSDImport> imports = EmfXsdUtils.filterComponents(schema.getContents(), XSDImport.class);
            for (final XSDImport importObj : imports) {
                if (nameSpace.equals(importObj.getNamespace()) && null == importObj.getSchemaLocation()) {
                    // Import already exists
                    return true;
                }
            }

            // we need to add an import
            final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
            xsdImport.setNamespace(nameSpace);
            schema.getContents().add(0, xsdImport);
            return true;
        } else {
            // for components in schemas
            final EObject parent = component.eContainer();
            if (null != parent && parent instanceof XSDSchema) {
                final String sourceTypeSchemaLocation = ((XSDSchema) parent).getSchemaLocation();
                String schemaRelativePath = null;
                String targetSchemaLocation = null;

                final IXSDModelRoot modelRoot = (IXSDModelRoot) getModelRoot();
                targetSchemaLocation = modelRoot.getSchema().getComponent().getSchemaLocation();

                schemaRelativePath = ResourceUtils.constructURI(targetSchemaLocation, sourceTypeSchemaLocation).toString();
                if (schemaRelativePath.length() == 0) {
                    schemaRelativePath = null;
                }

                XSDSchemaDirective directive = null;
                if (null == nameSpace || schema.getTargetNamespace().equals(nameSpace)) {
                    final Collection<XSDInclude> includes = EmfXsdUtils.filterComponents(schema.getContents(), XSDInclude.class);
                    for (final XSDInclude include : includes) {
                        if (null != schemaRelativePath && schemaRelativePath.equals(include.getSchemaLocation())) {
                            // Include already exists
                            return true;
                        }
                    }
                    directive = XSDFactory.eINSTANCE.createXSDInclude();
                } else {
                    final Collection<XSDImport> imports = EmfXsdUtils.filterComponents(schema.getContents(), XSDImport.class);
                    for (final XSDImport importObj : imports) {
                        if (nameSpace.equals(importObj.getNamespace())
                                && (null != schemaRelativePath && schemaRelativePath.equals(importObj.getSchemaLocation()))) {
                            // Import already exists
                            return true;
                        }
                    }
                    directive = XSDFactory.eINSTANCE.createXSDImport();
                    ((XSDImport) directive).setNamespace(nameSpace);
                }

                directive.setSchemaLocation(schemaRelativePath);

                XSDSchema resolvedSchema = directive.getResolvedSchema();
                if (resolvedSchema == null && _parent instanceof Description) {
                    final List<Schema> resolvedSchemas = ((Description) _parent).getSchemaResolver().resolveSchema(nameSpace, null);
                    if (!resolvedSchemas.isEmpty()) {
                        resolvedSchema = resolvedSchemas.get(0).getComponent();
                    }
                }

                if (null == resolvedSchema) {
                    URI parentSchemaURI = null;
                    try {
                        parentSchemaURI = URIUtil.fromString(sourceTypeSchemaLocation);
                    } catch (final URISyntaxException e) {
                        throw new IllegalArgumentException(e);
                    }
                    resolvedSchema = EmfXsdUtils.resolveSchema(parentSchemaURI);
                    if (null != resolvedSchema) {
                        directive.setResolvedSchema(resolvedSchema);
                        schema.getContents().add(0, directive);
                        return true;
                    }
                } else {
                    directive.setResolvedSchema(resolvedSchema);
                    schema.getContents().add(0, directive);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private XSDSchema ensureSchema(final String namespace) {
        XSDSchema schema = _schemasUsed.get(namespace);

        if (null == schema) {
            final Collection<XSDSchema> schemas = CollectionTypeUtils.findAll(getInlineSchemas(), new Condition<XSDSchema>() {

                public boolean isSatisfied(final XSDSchema in) {
                    return null != namespace && namespace.equals(in.getTargetNamespace()); // DOIT
                    // check
                    // this
                }

            });

            if (schemas.size() > 0) {
                schema = schemas.iterator().next();
            } else if (getModelRoot() instanceof IWsdlModelRoot) {
                schema = EmfXsdUtils.getXSDFactory().createXSDSchema();
                schema.setElementFormDefault(XSDForm.UNQUALIFIED_LITERAL);
                schema.setAttributeFormDefault(XSDForm.UNQUALIFIED_LITERAL);
                schema.setSchemaForSchemaQNamePrefix(EmfXsdUtils.XSD_PREFIX);
                schema.setTargetNamespace(namespace);

                final Map<String, String> qNamePrefixToNamespaceMap = schema.getQNamePrefixToNamespaceMap();
                qNamePrefixToNamespaceMap.put(schema.getSchemaForSchemaQNamePrefix(), EmfXsdUtils.getSchemaForSchemaNS());

                final XSDSchemaExtensibilityElement schemaExtensibilityEntity = EmfWsdlUtils.getWSDLFactory()
                        .createXSDSchemaExtensibilityElement();
                schemaExtensibilityEntity.setSchema(schema);

                final Description description = (Description) ((IWsdlModelRoot) getModelRoot()).getDescription();
                final Definition definition = description.getComponent();

                schema.setSchemaLocation(definition.getDocumentBaseURI());

                final String xmlnsPrefix = generateXmlnsPrefix(definition.getNamespaces(), qNamePrefixToNamespaceMap);
                qNamePrefixToNamespaceMap.put(xmlnsPrefix, namespace);

                Types types = definition.getETypes();
                if (types == null) {
                    types = EmfWsdlUtils.getWSDLFactory().createTypes();
                    definition.setTypes(types);
                }

                types.addExtensibilityElement(schemaExtensibilityEntity);
                schemaExtensibilityEntity.setEnclosingDefinition(definition);

                schema.updateElement();
                schema.getDocument().normalizeDocument();

                final String wsdlXmlnsPrefix = generateXmlnsPrefix(definition.getNamespaces(), qNamePrefixToNamespaceMap);
                definition.addNamespace(wsdlXmlnsPrefix, namespace);
            }

            _schemasUsed.put(namespace, schema);
        }
        return schema;
    }

    private boolean isTargetSchemaComponent(final XSDNamedComponent component) {
        boolean copy = true;
        final XSDSchema schema = component.getSchema();
        Assert.isTrue(null != schema, "Components schema is null"); //$NON-NLS-1$
        final XSDSchema sourceSchema = _sourceType.getSchema();
        Assert.isTrue(null != schema, "Source schema is null"); //$NON-NLS-1$
        copy = schema.getSchemaLocation().equals(sourceSchema.getSchemaLocation());
        copy &= (null == component.getTargetNamespace() && null == sourceSchema.getTargetNamespace())
                || (null != component.getTargetNamespace() && component.getTargetNamespace().equals(
                        sourceSchema.getTargetNamespace()));
        return copy;
    }

    private boolean isSynthetic(final XSDNamedComponent component) {
        return null == component.eContainer();
    }

    private static String generateXmlnsPrefix(final Map<String, String> wsdlNamespacesMap, final Map<String, String> xsdNamespaceMap) {
        for (int i = 0; i < 10000; i++) {
            final String key = XMLNS_PREFIX_BASE + String.valueOf(i);
            if (wsdlNamespacesMap.containsKey(key) || xsdNamespaceMap.containsKey(key)) {
                continue;
            }
            return key;
        }

        throw new IllegalStateException("Cannot generate xmlns prefix"); //$NON-NLS-1$
    }

    private void updateComponents() {
        // DOIT fix cyclic reference for name change
        for (final XSDNamedComponent component : _inconsistentClones) {
            if (component instanceof XSDTypeDefinition)
                updateComponent((XSDTypeDefinition) component);
            else if (component instanceof XSDElementDeclaration)
                updateComponent((XSDElementDeclaration) component);
            else if (component instanceof XSDAttributeDeclaration)
                updateComponent((XSDAttributeDeclaration) component);
            else if (component instanceof XSDAttributeGroupDefinition)
                updateComponent((XSDAttributeGroupDefinition) component);
            else if (component instanceof XSDModelGroupDefinition)
                updateComponent((XSDModelGroupDefinition) component);
        }
    }

    private void updateComponent(final XSDAttributeDeclaration attribute) {
        XSDSimpleTypeDefinition typeDefinition = attribute.getAnonymousTypeDefinition();
        if (null == typeDefinition) {
            typeDefinition = getTargetSchemaComponent(attribute.getTypeDefinition(), XSDSimpleTypeDefinition.class);
            if (null != typeDefinition)
                attribute.setTypeDefinition(typeDefinition);
        } else
            updateComponent(typeDefinition);

        // process referred element declaration if exists
        if (attribute.isAttributeDeclarationReference()) {
            final XSDAttributeDeclaration resolvedAttribute = getTargetSchemaComponent(attribute
                    .getResolvedAttributeDeclaration(), XSDAttributeDeclaration.class);
            if (null != resolvedAttribute)
                attribute.setResolvedAttributeDeclaration(resolvedAttribute);
        }
    }

    private void updateComponent(final XSDAttributeGroupDefinition attributeGroup) {
        if (attributeGroup.isAttributeGroupDefinitionReference()) {
            final XSDAttributeGroupDefinition attrGroup = getTargetSchemaComponent(attributeGroup
                    .getResolvedAttributeGroupDefinition(), XSDAttributeGroupDefinition.class);
            if (null != attrGroup)
                attributeGroup.setResolvedAttributeGroupDefinition(attrGroup);
        } else {
            updateComponent(attributeGroup.getContents());
        }
    }

    private void updateComponent(final XSDModelGroupDefinition modelGroupDefinition) {
        if (modelGroupDefinition.isModelGroupDefinitionReference()) {
            final XSDModelGroupDefinition modelGroupDef = getTargetSchemaComponent(modelGroupDefinition
                    .getResolvedModelGroupDefinition(), XSDModelGroupDefinition.class);
            if (null != modelGroupDef)
                modelGroupDefinition.setResolvedModelGroupDefinition(modelGroupDef);
        } else {
            final XSDModelGroup modelGroup = modelGroupDefinition.getModelGroup();
            if (null != modelGroup)
                updateComponent(modelGroup);
        }
    }

    private void updateComponent(final XSDModelGroup modelGroup) {
        final List<XSDParticle> particles = (modelGroup).getContents();
        for (final XSDParticle particle : particles) {
            updateComponent(particle);
        }
    }

    private void updateComponent(final XSDParticle particle) {
        final XSDParticleContent content = particle.getContent();
        if (content instanceof XSDModelGroup) {
            updateComponent((XSDModelGroup) content);
        } else if (content instanceof XSDElementDeclaration) {
            updateComponent((XSDElementDeclaration) content);
        } else if (content instanceof XSDModelGroupDefinition) {
            updateComponent((XSDModelGroupDefinition) content);
        }
    }

    private void updateComponent(final XSDSimpleTypeDefinition simpleType) {
        final XSDSimpleTypeDefinition baseType = simpleType.getBaseTypeDefinition();
        if (null != baseType && null == baseType.getName())
            updateComponent(baseType);
        else {
            final XSDSimpleTypeDefinition type = getTargetSchemaComponent(baseType, XSDSimpleTypeDefinition.class);
            if (null != type)
                simpleType.setBaseTypeDefinition(type);
        }
    }

    private void updateComponent(final XSDComplexTypeDefinition complexType) {
        // process content
        final XSDComplexTypeContent content = complexType.getContent();
        if (content != null) {
            if (content instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle) content;
                updateComponent(particle);
                final XSDTypeDefinition baseType = complexType.getBaseTypeDefinition();
                if (null != baseType && null == baseType.getName())
                    updateComponent(baseType);
                else {
                    final XSDTypeDefinition type = getTargetSchemaComponent(baseType, XSDTypeDefinition.class);
                    if (null != type)
                        complexType.setBaseTypeDefinition(type);
                }
            } else {
                updateComponent((XSDSimpleTypeDefinition) content);
                final XSDTypeDefinition baseType = complexType.getBaseTypeDefinition();
                if (null != baseType && null == baseType.getName())
                    updateComponent(baseType);
                else {
                    final XSDTypeDefinition type = getTargetSchemaComponent(baseType, XSDTypeDefinition.class);
                    if (null != type)
                        complexType.setBaseTypeDefinition(type);
                }
            }
        }

        final List<XSDAttributeGroupContent> attributeContents = complexType.getAttributeContents();
        updateComponent(attributeContents);
    }

    private void updateComponent(final List<XSDAttributeGroupContent> contents) {
        for (final XSDAttributeGroupContent content : contents) {
            if (content instanceof XSDAttributeUse) {
                updateComponent(((XSDAttributeUse) content).getContent());
            } else {
                updateComponent((XSDAttributeGroupDefinition) content);
            }
        }
    }

    private void updateComponent(final XSDElementDeclaration element) {
        // process referred element declaration if exists
        if (element.isElementDeclarationReference()) {
            final XSDElementDeclaration resolvedElementDeclaration = getTargetSchemaComponent(element
                    .getResolvedElementDeclaration(), XSDElementDeclaration.class);
            if (null != resolvedElementDeclaration)
                element.setResolvedElementDeclaration(resolvedElementDeclaration);
        }

        // process type definition if exists
        XSDTypeDefinition typeDefinition = element.getAnonymousTypeDefinition();
        if (null == typeDefinition) {
            typeDefinition = getTargetSchemaComponent(element.getTypeDefinition(), XSDTypeDefinition.class);
            if (null != typeDefinition)
                element.setTypeDefinition(typeDefinition);
        } else {
            updateComponent(typeDefinition);
        }
    }

    private void updateComponent(final XSDTypeDefinition type) {
        if (type instanceof XSDSimpleTypeDefinition) {
            updateComponent((XSDSimpleTypeDefinition) type);
        } else {
            updateComponent((XSDComplexTypeDefinition) type);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends XSDNamedComponent> T getTargetSchemaComponent(final XSDNamedComponent component, final Class<T> componentType) {
        if (null == component)
            return null;
        final String name = component.getName();
        final String namespace = component.getTargetNamespace();
        if (null != namespace && namespace.equals(_sourceType.getSchema().getTargetNamespace())) {
            final XSDSchema schema = _targetSchema.getComponent();
            for (final XSDSchemaContent content : schema.getContents()) {
                if (componentType.isInstance(content) && name.equals(((XSDNamedComponent) content).getName())) {
                    return (T) content;
                }
            }
        }
        return null;
    }

    public void updateReferences(final XSDNamedComponent component) {
        _inconsistentClones.add(component);
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public boolean canUndo() {
        return true;
    }
}