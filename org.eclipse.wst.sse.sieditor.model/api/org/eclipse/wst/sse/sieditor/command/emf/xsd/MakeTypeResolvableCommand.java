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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

/**
 * Command for making a type inline
 * 
 * 
 * 
 */
public class MakeTypeResolvableCommand extends AbstractWSDLNotificationOperation {

    private final AbstractType _sourceType;
    private AbstractType _clone;
    private final IModelObject _parent;

    // private ArrayList<ICommand> _commandStack;

    private Map<String, XSDSchema> _schemasUsed;

    public MakeTypeResolvableCommand(IWsdlModelRoot root, IModelObject modelObject, AbstractType typeToBeCopied) {
        super(root, modelObject, Messages.MakeTypeResolvableCommand_make_type_resolvable_command_label);
        this._sourceType = typeToBeCopied;
        this._parent = modelObject;
    }

    public boolean canExecute() {
        return !(null == getModelRoot() || null == _parent || null == _sourceType);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<XSDSchema> getInlineSchemas() {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        final Definition definition = ((Description) getModelRoot().getDescription()).getComponent();
        final Types types = definition.getETypes();
        if (null == types)
            return new ArrayList<XSDSchema>();
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return new ArrayList<XSDSchema>(types.getSchemas());
    }

    public org.eclipse.core.runtime.IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor,
            org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        _schemasUsed = new HashMap<String, XSDSchema>();

        final XSDNamedComponent component = _sourceType.getComponent();

        // source in visit methods can be null only when call is from this
        // method
        if (component instanceof XSDElementDeclaration) {
            Logger.getDebugTrace().trace("", "Resolve Element " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            visitElementDeclaration((XSDElementDeclaration) component, null);
        } else if (component instanceof XSDTypeDefinition) {
            if (debug) Logger.getDebugTrace().trace("", "Begin Type " + _sourceType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            visitTypeDefinition((XSDTypeDefinition) component, null);
        }

        final Description description = (Description) getModelRoot().getDescription();
        if (debug) Logger.getDebugTrace().trace("", "Finally Refresh Schemas for all the components that are copied or are created"); //$NON-NLS-1$ //$NON-NLS-2$
        description.refreshSchemas();

        if (debug) Logger.getDebugTrace().trace("", "Return the copied Type"); //$NON-NLS-1$ //$NON-NLS-2$
        _clone = (AbstractType) description.resolveType(component);

        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return null != _clone ? Status.OK_STATUS : 
        		new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
        				MessageFormat.format(Messages.MakeTypeResolvableCommand_mesg_can_not_resolve_type_X, _sourceType.getName()));
    }

    public AbstractType getCopiedType() {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
    	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return _clone;
    }

    private void visitAttributeGroup(final XSDAttributeGroupDefinition attributeGroup, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        XSDSchema copiedTo;

        if (!attributeGroup.isAttributeGroupDefinitionReference()) {
            // check if attributeGroup already exists
            if (debug) Logger.getDebugTrace().trace("", "transversing Global AttributeGroup '" + attributeGroup.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            final XSDAttributeGroupDefinition match = contains(new QName(attributeGroup.getTargetNamespace(), attributeGroup
                    .getName()), XSDAttributeGroupDefinition.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "AttributeGroup '" + attributeGroup.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse
                    // attributeGroup here
                    ensureImport(source, attributeGroup.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, attributeGroup.getTargetNamespace(), attributeGroup)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in source
            copiedTo = createClone(attributeGroup);
            if (null != source)
                ensureImport(source, attributeGroup.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (debug) Logger.getDebugTrace().trace("", "Visit Referred components"); //$NON-NLS-1$ //$NON-NLS-2$
        if (attributeGroup.isAttributeGroupDefinitionReference()) {
            final XSDAttributeGroupDefinition resolvedAttributeGroupDefinition = attributeGroup
                    .getResolvedAttributeGroupDefinition();
            if (!(null == resolvedAttributeGroupDefinition || isSynthetic(resolvedAttributeGroupDefinition))) {
                visitAttributeGroup(resolvedAttributeGroupDefinition, copiedTo);
            }
        } else {
            visitAttributeGroupContent(attributeGroup.getContents(), copiedTo);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$	
    }

    private void visitAttributeDeclaration(final XSDAttributeDeclaration attribute, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        XSDSchema copiedTo;

        if (attribute.isGlobal()) {
            if (debug) Logger.getDebugTrace().trace("", "transversing Global Attribute '" + attribute.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // check if element already exists
            final XSDAttributeDeclaration match = contains(new QName(attribute.getTargetNamespace(), attribute.getName()),
                    XSDAttributeDeclaration.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "Attribute '" + attribute.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, attribute.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, attribute.getTargetNamespace(), attribute)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in source
            copiedTo = createClone(attribute);
            if (null != source)
                ensureImport(source, attribute.getTargetNamespace(), null);
        } else {
            // this is a local attribute so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (debug) Logger.getDebugTrace().trace("", "Visit Referred components"); //$NON-NLS-1$ //$NON-NLS-2$
        XSDSimpleTypeDefinition typeDefinition = attribute.getAnonymousTypeDefinition();
        if (null == typeDefinition)
            typeDefinition = attribute.getTypeDefinition();

        if (!(null == typeDefinition || isSynthetic(typeDefinition)))
            visitTypeDefinition(typeDefinition, copiedTo);

        // process referred element declaration if exists
        if (attribute.isAttributeDeclarationReference()) {
            final XSDAttributeDeclaration resolvedAttribute = attribute.getResolvedAttributeDeclaration();
            if (!(null == resolvedAttribute || isSynthetic(resolvedAttribute)))
                visitAttributeDeclaration(resolvedAttribute, copiedTo);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitElementDeclaration(final XSDElementDeclaration element, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        XSDSchema copiedTo;

        if (element.isGlobal()) {
            if (debug) Logger.getDebugTrace().trace("", "transversing Global Element Declaration'" + element.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // check if element already exists
            final XSDElementDeclaration match = contains(new QName(element.getTargetNamespace(), element.getName()),
                    XSDElementDeclaration.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "Element '" + element.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, element.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, element.getTargetNamespace(), element)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in source
            copiedTo = createClone(element);
            if (null != source)
                ensureImport(source, element.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (debug) Logger.getDebugTrace().trace("", "Visit Referred components"); //$NON-NLS-1$ //$NON-NLS-2$
        // process type definition if exists
        XSDTypeDefinition typeDefinition = element.getAnonymousTypeDefinition();
        if (null == typeDefinition)
            typeDefinition = element.getTypeDefinition();

        if (!(null == typeDefinition || isSynthetic(typeDefinition)))
            visitTypeDefinition(typeDefinition, copiedTo);

        // process referred element declaration if exists
        if (element.isElementDeclarationReference()) {
            final XSDElementDeclaration resolvedElementDeclaration = element.getResolvedElementDeclaration();
            if (!(null == resolvedElementDeclaration || isSynthetic(resolvedElementDeclaration)))
                visitElementDeclaration(resolvedElementDeclaration, copiedTo);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitModelGroupDefinition(final XSDModelGroupDefinition modelGroupDefinition, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$	
        XSDSchema copiedTo;

        if (!modelGroupDefinition.isModelGroupDefinitionReference()) {
            if (debug) Logger.getDebugTrace().trace("", "transversing Global ModelGroup Definition'" + modelGroupDefinition.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // check if attributeGroup already exists
            final XSDModelGroupDefinition match = contains(new QName(modelGroupDefinition.getTargetNamespace(),
                    modelGroupDefinition.getName()), XSDModelGroupDefinition.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "ModelGroupDefinition '" + modelGroupDefinition.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse
                    // attributeGroup here
                    ensureImport(source, modelGroupDefinition.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, modelGroupDefinition.getTargetNamespace(), modelGroupDefinition)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in source
            copiedTo = createClone(modelGroupDefinition);
            if (null != source)
                ensureImport(source, modelGroupDefinition.getTargetNamespace(), null);
        } else {
            // this is a local element so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (debug) Logger.getDebugTrace().trace("", "Visit Referred components"); //$NON-NLS-1$ //$NON-NLS-2$
        if (modelGroupDefinition.isModelGroupDefinitionReference()) {
            final XSDModelGroupDefinition resolvedModelGroupDefinition = modelGroupDefinition.getResolvedModelGroupDefinition();
            if (!(null == resolvedModelGroupDefinition.eContainer() || isSynthetic(resolvedModelGroupDefinition))) {
                visitModelGroupDefinition(resolvedModelGroupDefinition, copiedTo);
            }
        } else {
            final XSDModelGroup content = modelGroupDefinition.getModelGroup();
            if (null != content)
                visitModelGroup(content, copiedTo);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitTypeDefinition(final XSDTypeDefinition type, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        if (null != type) {
            if (XSDConstants.isSchemaForSchemaNamespace(type.getTargetNamespace())) {
                if (debug) Logger.getDebugTrace().trace("", "Primitive Type - " + type.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                return;
            }
        }

        if (type instanceof XSDSimpleTypeDefinition) {
            visitSimpleTypeDefinition((XSDSimpleTypeDefinition) type, source);
        } else {
            visitComplexTypeDefinition((XSDComplexTypeDefinition) type, source);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitSimpleTypeDefinition(final XSDSimpleTypeDefinition simpleType, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        // holds the schema where the clone is created so that imports can be
        // added to it for
        // referred components
        XSDSchema copiedTo;

        // if global complexType
        if (null != simpleType.getName()) {
            if (debug) Logger.getDebugTrace().trace("", "transversing Global SimpleTypeDefinition'" + simpleType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // check if type already exists
            final XSDTypeDefinition match = contains(new QName(simpleType.getTargetNamespace(), simpleType.getName()),
                    XSDTypeDefinition.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "SimpleType '" + simpleType.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, simpleType.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, simpleType.getTargetNamespace(), simpleType)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in @localvar(source)
            copiedTo = createClone(simpleType);
            if (null != source)
                ensureImport(source, simpleType.getTargetNamespace(), null);
        } else {
            // this is a anonymous type so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        if (debug) Logger.getDebugTrace().trace("", "Visit Referred components"); //$NON-NLS-1$ //$NON-NLS-2$
        final XSDTypeDefinition baseType = simpleType.getBaseType();

        if (null != baseType && !isSynthetic(baseType))
            visitTypeDefinition(baseType, copiedTo);
        for (XSDSimpleTypeDefinition member : simpleType.getMemberTypeDefinitions()) {
            if (!isSynthetic(member))
                visitTypeDefinition(member, copiedTo);
        }

        final XSDSimpleTypeDefinition itemType = simpleType.getItemTypeDefinition();
        if (null != itemType && !isSynthetic(itemType))
            visitTypeDefinition(itemType, copiedTo);
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitComplexTypeDefinition(final XSDComplexTypeDefinition complexType, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        // holds the schema where the clone is created so that imports can be
        // added to it for
        // referred components
        XSDSchema copiedTo;

        // if global complexType
        if (null != complexType.getName()) {
            if (debug) Logger.getDebugTrace().trace("", "transversing Global ComplexTypeDefinition'" + complexType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // check if type already exists
            final XSDTypeDefinition match = contains(new QName(complexType.getTargetNamespace(), complexType.getName()),
                    XSDTypeDefinition.class);
            if (null != match) {
                if (debug) Logger.getDebugTrace().trace("", "ComplexType '" + complexType.getName() + "' already visible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // return if source is null
                if (null == source) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                } else {
                    // ensure import and return, no need to transverse type here
                    ensureImport(source, complexType.getTargetNamespace(), null);
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            } else {
                if (ensureImport(source, complexType.getTargetNamespace(), complexType)) {
                	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return;
                }
            }
            // copy and create reference in @localvar(source)
            copiedTo = createClone(complexType);
            if (null != source)
                ensureImport(source, complexType.getTargetNamespace(), null);
        } else {
            // this is a anonymous type so we need to add imports to
            // @localvar(source)
            copiedTo = source;
        }

        // process content
        final XSDComplexTypeContent content = complexType.getContent();
        if (content != null) {
            if (content instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle) content;
                visitParticle(particle, copiedTo);
                final XSDTypeDefinition baseType = complexType.getBaseType();

                if (!isSynthetic(baseType))
                    visitTypeDefinition(baseType, copiedTo);
            } else {
                visitSimpleTypeDefinition((XSDSimpleTypeDefinition) content, copiedTo);
                final XSDTypeDefinition baseType = complexType.getBaseType();

                if (!(null == baseType || isSynthetic(baseType)))
                    visitTypeDefinition(baseType, copiedTo);
            }
        }

        final List<XSDAttributeGroupContent> attributeContents = complexType.getAttributeContents();
        visitAttributeGroupContent(attributeContents, copiedTo);
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitParticle(final XSDParticle particle, final XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        final XSDParticleContent content = particle.getContent();
        if (content instanceof XSDModelGroup) {
            visitModelGroup((XSDModelGroup) content, source);
        } else if (content instanceof XSDElementDeclaration) {
            visitElementDeclaration((XSDElementDeclaration) content, source);
        } else if (content instanceof XSDModelGroupDefinition) {
            visitModelGroupDefinition((XSDModelGroupDefinition) content, source);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitModelGroup(final XSDModelGroup modelGroup, final XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        final List<XSDParticle> particles = ((XSDModelGroup) modelGroup).getContents();
        for (XSDParticle particle : particles) {
            visitParticle(particle, source);
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    private void visitAttributeGroupContent(Collection<XSDAttributeGroupContent> attributeContents, XSDSchema source) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        for (final XSDAttributeGroupContent attributeGroupContent : attributeContents) {
            if (attributeGroupContent instanceof XSDAttributeGroupDefinition) {
                visitAttributeGroup((XSDAttributeGroupDefinition) attributeGroupContent, source);
                continue;
            }

            final XSDAttributeUse attributeUse = (XSDAttributeUse) attributeGroupContent;

            final XSDAttributeDeclaration attributeDeclaration = attributeUse.getContent();

            if (null != attributeDeclaration) {
                visitAttributeDeclaration(attributeDeclaration, source);
            }
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
    }

    /**
     * Creates a clone of given component in given schema and returns the clone
     * 
     * @param <T>
     * @param schema
     * @param component
     * @return
     */
    private XSDSchema createClone(XSDNamedComponent component) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$	
        // use this code to also clone annotations but be careful that schema is
        // updated
        final XSDSchema schema = ensureSchema(component.getTargetNamespace());

        /*
         * final Element element = component.getElement();
         * 
         * if (element != null) { final Node newComponent =
         * schema.getDocument().importNode(element, true);
         * schema.getElement().appendChild(newComponent);
         * schema.getDocument().normalizeDocument(); } schema.update();
         * schema.getDocument().normalizeDocument();
         */

        /*
         * This doesnt clone the annotations of the component final
         * XSDConcreteComponent clone = component.cloneConcreteComponent(true,
         * false);
         * 
         * if(clone instanceof XSDSchemaContent)
         * schema.getContents().add((XSDSchemaContent) clone);
         */

        // This utility copies the component ensuring that the annotations are
        // also copied
        if (debug) Logger.getDebugTrace().trace("", "Copying " + component.eClass().getName() + " '" + component.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        EmfXsdUtils.cloneWithAnnotation(component, schema);
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return schema;
    }

    private <T extends XSDNamedComponent> T contains(final QName qName, final Class<T> metaClass) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$	
        final String namespace = qName.getNamespaceURI();
        final String name = qName.getLocalPart();

        // Get schemas with same namespace
        final Collection<XSDSchema> schemas = CollectionTypeUtils.findAll(getInlineSchemas(), new Condition<XSDSchema>() {

            public boolean isSatisfied(XSDSchema in) {
                return namespace.equals(in.getTargetNamespace());
            }

        });

        // Check is type is visible in contained schemas or included schemas in
        // contained schemas
        T result = null;
        for (XSDSchema schema : schemas) {
            result = contains(schema, name, new ArrayList<String>(), metaClass);
            if (null != result)
                return result;
        }

        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$	
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends XSDNamedComponent> T contains(XSDSchema schema, String name, Collection<String> locations,
            final Class<T> metaClass) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        final EList<XSDSchemaContent> contents = schema.getContents();
        final ArrayList<XSDInclude> includes = new ArrayList<XSDInclude>();

        for (XSDSchemaContent schemaContent : contents) {
            if (metaClass.isInstance(schemaContent)) {
                if (name.equals(((XSDNamedComponent) schemaContent).getName())) {
                    if (debug) Logger.getDebugTrace().trace("", "Found " + metaClass.getSimpleName() + " '" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                            + name + "' in inline schema '" + schema.getTargetNamespace() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ 
                    return (T) schemaContent;
                }
            }
        }

        T result = null;
        for (XSDInclude include : includes) {
            final XSDSchema includedSchema = include.getResolvedSchema();
            if (null != includedSchema && !locations.contains(includedSchema.getSchemaLocation())) {
                locations.add(includedSchema.getSchemaLocation());
                result = contains(includedSchema, name, locations, metaClass);
                if (null != result) {
                    if (debug) Logger.getDebugTrace().trace("", "Found " + metaClass.getSimpleName() + " '" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                            + name + "' in included schema '" //$NON-NLS-1$ 
                            + includedSchema.getSchemaLocation() + "'"); //$NON-NLS-1$
                    return result;
                }
            }
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return result;
    }

    private boolean ensureImport(final XSDSchema schema, final String nameSpace, final XSDNamedComponent component) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        if (null == component) {
            if (debug) Logger.getDebugTrace().trace("", "Component exists in the same document"); //$NON-NLS-1$ //$NON-NLS-2$
            // for cloned components or visible components using includes
            if (schema.getTargetNamespace().equals(nameSpace)) {
                if (debug) Logger.getDebugTrace().trace("", "Schema and component have same namespace so no include is needed"); //$NON-NLS-1$ //$NON-NLS-2$
                if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                return false;
            }

            final Collection<XSDImport> imports = EmfXsdUtils.filterComponents(schema.getContents(), XSDImport.class);
            for (XSDImport importObj : imports) {
                if (nameSpace.equals(importObj.getNamespace()) && null == importObj.getSchemaLocation()) {
                    // Import already exists
                    if (debug) Logger.getDebugTrace().trace("", "Import already exists"); //$NON-NLS-1$ //$NON-NLS-2$
                    if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                    return true;
                }
            }

            // we need to add an import
            if (debug) Logger.getDebugTrace().trace("", "Adding Import for namespace '" + nameSpace + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
            xsdImport.setNamespace(nameSpace);
            schema.getContents().add(0, xsdImport);
            if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
            return true;
        } else {
            // for components in schemas
            final EObject parent = component.eContainer();
            if (null != parent && parent instanceof XSDSchema) {
                final String location = ((XSDSchema) parent).getSchemaLocation();
                final IFile file = ResourceUtils.getWorkSpaceFile(new Path(location));
                if (null != file && ResourceUtils.checkContentType(file, DocumentType.XSD_SHEMA.getResourceID())) {
                    if (debug) Logger.getDebugTrace().trace("", "Component exists in the external Schema document"); //$NON-NLS-1$ //$NON-NLS-2$
                    final Definition definition = ((Description) getModelRoot().getDescription()).getComponent();
                    final String schemaRelativePath = ResourceUtils.makeRelativeLocation(definition.getLocation(), file
                            .getLocationURI());

                    XSDSchemaDirective directive = null;
                    if (null == nameSpace || schema.getTargetNamespace().equals(nameSpace)) {
                        final Collection<XSDInclude> includes = EmfXsdUtils.filterComponents(schema.getContents(),
                                XSDInclude.class);
                        for (XSDInclude include : includes) {
                            if (null != schemaRelativePath && schemaRelativePath.equals(include.getSchemaLocation())) {
                                // Include already exists
                                if (debug) Logger.getDebugTrace().trace("", "Include already exists"); //$NON-NLS-1$ //$NON-NLS-2$
                                if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                                return true;
                            }
                        }
                        if (debug) Logger.getDebugTrace().trace("", "Add Include"); //$NON-NLS-1$ //$NON-NLS-2$
                        directive = XSDFactory.eINSTANCE.createXSDInclude();
                    } else {
                        final Collection<XSDImport> imports = EmfXsdUtils.filterComponents(schema.getContents(), XSDImport.class);
                        for (XSDImport importObj : imports) {
                            if (nameSpace.equals(importObj.getNamespace())
                                    && (null != schemaRelativePath && schemaRelativePath.equals(importObj.getSchemaLocation()))) {
                                // Import already exists
                                if (debug) Logger.getDebugTrace().trace("", "Import already exists"); //$NON-NLS-1$ //$NON-NLS-2$
                                if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                                return true;
                            }
                        }
                        if (debug) Logger.getDebugTrace().trace("", "Add Import"); //$NON-NLS-1$ //$NON-NLS-2$
                        directive = XSDFactory.eINSTANCE.createXSDImport();
                        ((XSDImport) directive).setNamespace(nameSpace);
                    }

                    directive.setSchemaLocation(schemaRelativePath);

                    XSDSchema resolvedSchema = directive.getResolvedSchema();
                    if (null == resolvedSchema) {
                        /*
                         * GFB-POC Modified to take uri resolvedSchema =
                         * EmfXsdUtils.resolveSchema(file);
                         */
                        resolvedSchema = EmfXsdUtils.resolveSchema(file.getLocationURI());
                        if (null != resolvedSchema) {
                            directive.setResolvedSchema(resolvedSchema);
                            schema.getContents().add(0, directive);
                            if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                            return true;
                        }
                        if (debug) Logger.getDebugTrace().trace("", "Could not resolve schema for the component"); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        schema.getContents().add(0, directive);
                        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
                        return true;
                    }
                }
            }
        }
        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return false;
    }

    @SuppressWarnings("unchecked")
    private XSDSchema ensureSchema(final String namespace) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        XSDSchema schema = _schemasUsed.get(namespace);

        if (null == schema) {
            final Collection<XSDSchema> schemas = CollectionTypeUtils.findAll(getInlineSchemas(), new Condition<XSDSchema>() {

                public boolean isSatisfied(XSDSchema in) {
                    return namespace.equals(in.getTargetNamespace());
                }

            });

            if (schemas.size() > 0) {
                if (debug) Logger.getDebugTrace().trace("", "Schema for namespace '" + namespace + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                schema = schemas.iterator().next();
            } else {
                if (debug) Logger.getDebugTrace().trace("", "Adding Schema for namespace '" + namespace + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

                final Description description = (Description) getModelRoot().getDescription();
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
        } else
            if (debug) Logger.getDebugTrace().trace("", "Schema for namespace '" + namespace + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return schema;
    }

    private boolean isSynthetic(final XSDNamedComponent component) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
    	if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        return null == component.eContainer();
    }

    private static final String XMLNS_PREFIX_BASE = "imp"; //$NON-NLS-1$

    private static String generateXmlnsPrefix(final Map<String, String> wsdlNamespacesMap, Map<String, String> xsdNamespaceMap) {
    	boolean debug = Logger.isDebugEnabled();
    	if (debug)	Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
        for (int i = 0; i < 10000; i++) {
            final String key = XMLNS_PREFIX_BASE + String.valueOf(i);
            if (wsdlNamespacesMap.containsKey(key) || xsdNamespaceMap.containsKey(key)) {
                continue;
            }
            return key;
        }

        if (debug) Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
        throw new IllegalStateException("Cannot generate xmlns prefix"); //$NON-NLS-1$
    }

}