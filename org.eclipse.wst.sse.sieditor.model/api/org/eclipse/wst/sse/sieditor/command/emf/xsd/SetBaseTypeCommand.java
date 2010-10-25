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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpaceFacet;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.utils.SimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * Sets the basetype for a structure type and a simple type. This also ensures
 * that the component for which we are setting the baseType is in proper state
 * for setting the respt baseType.
 * 
 * 
 * @pre null != baseType <code>&&</code> <BR>
 *      !sourceType instanceof {@link XSDElementDeclaration} <code>&&</code><BR>
 *      (baseType instanceof {@link XSDSimpleTypeDefinition}<code>||</code><BR>
 *      baseType instanceof {@link XSDComplexTypeDefinition})
 */
public class SetBaseTypeCommand extends AbstractNotificationOperation {
    private IType _baseType;
    private final IType _type;

    public SetBaseTypeCommand(final IModelRoot root, final IType type, final IType baseType) {
        super(root, type, Messages.SetBaseTypeCommand_set_base_type_command_label);
        this._type = type;
        this._baseType = baseType;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final ISchema schema = _type.getParent();

        IType resolvedType = null;
        try {
            resolvedType = schema.resolveType((AbstractType) _baseType);
        } catch (final ExecutionException e) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Cannot resolve type %s.", _baseType.getName())); //$NON-NLS-1$
        }
        if (resolvedType instanceof UnresolvedType) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SetBaseTypeCommand_msg_can_not_resolve_base_type_X, _baseType.getName()));
        }

        if (_type instanceof ISimpleType) {
            if (resolvedType instanceof SimpleType) {
                final XSDNamedComponent type = resolvedType.getComponent();
                if (type instanceof XSDSimpleTypeDefinition) {
                    _baseType = resolvedType;
                } else {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                            Messages.SetBaseTypeCommand_msg_base_type_must_be_simple_X, _baseType.getName()));
                }
            } else {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(Messages.SetBaseTypeCommand_0,
                        _baseType.getName()));
            }
        } else {// structure type
            if (resolvedType.getComponent() instanceof XSDElementDeclaration) {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                        Messages.SetBaseTypeCommand_msg_base_type_must_be_simple_X, _baseType.getName()));
            }
        }

        final XSDNamedComponent component = (XSDNamedComponent) modelObject.getComponent();
        XSDTypeDefinition type = null;
        final XSDPackage xsdPackage = EmfXsdUtils.getXSDPackage();
        final XSDFactory factory = EmfXsdUtils.getXSDFactory();
        final XSDTypeDefinition baseType = (XSDTypeDefinition) _baseType.getComponent();
        // Do initial cleanup
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration) component;
            type = element.getTypeDefinition();
            if (null == type || null != type.getName()) {
                // If Element refers to global type un-set type reference
                component.eUnset(xsdPackage.getXSDElementDeclaration_TypeDefinition());
                type = null;
            }

            /*
             * BaseType -> ST CT AnonymousType -> null Create Anonymous ST
             * Create Anonymous CT ST - Create Anonymous CT CT - -
             */
            if (null == type && baseType instanceof XSDSimpleTypeDefinition) {
                element.setAnonymousTypeDefinition(type = factory.createXSDSimpleTypeDefinition());
            } else if (baseType instanceof XSDComplexTypeDefinition && (null == type || type instanceof XSDSimpleTypeDefinition)) {
                element.eUnset(xsdPackage.getXSDElementDeclaration_AnonymousTypeDefinition());

                element.setAnonymousTypeDefinition(type = factory.createXSDComplexTypeDefinition());

                final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) type;
                complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                final XSDParticle content = factory.createXSDParticle();
                final XSDModelGroup modelGroup = factory.createXSDModelGroup();
                modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
                content.setContent(modelGroup);
                complexType.setContent(content);
            }
        } else if (component instanceof XSDTypeDefinition) {
            type = (XSDTypeDefinition) component;
        } else if (!(component instanceof XSDSimpleTypeDefinition)) {
            throw new RuntimeException("component has to be XSDElementDeclaration or XSDComplexTypeDefinition"); //$NON-NLS-1$
        }

        // Set the Base Type
        if (type instanceof XSDSimpleTypeDefinition && baseType instanceof XSDSimpleTypeDefinition) {
            updateSimpleTypeFacets((XSDSimpleTypeDefinition) type, (XSDSimpleTypeDefinition) baseType);
            ((XSDSimpleTypeDefinition) type).setBaseTypeDefinition((XSDSimpleTypeDefinition) baseType);
        } else if (type instanceof XSDComplexTypeDefinition) {
            final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) type;
            XSDComplexTypeContent content = complexType.getContent();
            /*
             * Do initial cleanup BaseType -> ST CT content -> null Create ST
             * Create PT ST - Create PT PT Create ST -
             */
            if (baseType instanceof XSDSimpleTypeDefinition && (null == content || content instanceof XSDParticle)) {
                complexType.eUnset(xsdPackage.getXSDComplexTypeDefinition_Content());
                complexType.setContent(content = factory.createXSDSimpleTypeDefinition());
                complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
            } else {
                if (baseType instanceof XSDComplexTypeDefinition) {
                    if (null == content || content instanceof XSDSimpleTypeDefinition) {
                        complexType.eUnset(xsdPackage.getXSDComplexTypeDefinition_Content());

                        final XSDParticle particle = factory.createXSDParticle();
                        final XSDModelGroup modelGroup = factory.createXSDModelGroup();
                        modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
                        particle.setContent(modelGroup);
                        complexType.setContent(particle);
                    }
                }
            }

            if (baseType instanceof XSDSimpleTypeDefinition) {
                ((XSDSimpleTypeDefinition) content).setBaseTypeDefinition((XSDSimpleTypeDefinition) baseType);
                // DOIT check this
                complexType.setBaseTypeDefinition(baseType);
                if (complexType.getContent() instanceof XSDSimpleTypeDefinition)
                    complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
            } else if (baseType instanceof XSDComplexTypeDefinition) {
                if (null == complexType.getDerivationMethod()) {
                    complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                }
                complexType.setBaseTypeDefinition(baseType);
            }
        }
        return Status.OK_STATUS;
    }

    @Override
    public boolean canExecute() {
        return modelObject != null && _baseType != null;
    }

    /**
     * Utility method. Checks if the simple type can reuse at least one of the
     * facets from the old type
     * 
     * @param oldType
     *            - the old type we are changing
     * @param newType
     *            - the new type we are setting
     * @return <code>true</code> if at least one facet from the old type can be
     *         reused. <code>false</code> otherwise
     */
    public static boolean canReuseAtLeastOneFacet(final XSDSimpleTypeDefinition oldType, final XSDSimpleTypeDefinition newType) {
        if (getSimpleTypeFacets().areLengthFacetsSupported(newType)) {
            if ((oldType.getLengthFacet() != null) || (oldType.getMinLengthFacet() != null)
                    || (oldType.getMaxLengthFacet() != null)) {
                return true;
            }
        }
        if (getSimpleTypeFacets().areInclusiveExclusiveFacetsSupported(newType)) {
            if ((oldType.getMinInclusiveFacet() != null) || (oldType.getMaxInclusiveFacet() != null)
                    || (oldType.getMinExclusiveFacet() != null) || (oldType.getMaxExclusiveFacet() != null)) {
                return true;
            }
        }
        if (getSimpleTypeFacets().isFractionDigitsFacetSupported(newType) && oldType.getFractionDigitsFacet() != null) {
            return true;
        }
        if (getSimpleTypeFacets().isTotalDigitsFacetSupported(newType) && oldType.getTotalDigitsFacet() != null) {
            return true;
        }
        if (getSimpleTypeFacets().isEnumerationFacetSupported(newType) && !oldType.getEnumerationFacets().isEmpty()) {
            return true;
        }

        if (getSimpleTypeFacets().isWhitespaceFacetSupported(newType)) {
            // if one of the whitespace facets is fixed - no need to keep it
            final XSDWhiteSpaceFacet whiteSpaceFacet = oldType.getWhiteSpaceFacet();
            final XSDWhiteSpaceFacet newWhiteSpaceFacet = newType.getWhiteSpaceFacet();
            if (whiteSpaceFacet != null) {
                if ((!whiteSpaceFacet.isFixed()) && ((newWhiteSpaceFacet == null) || (!newWhiteSpaceFacet.isFixed()))) {
                    return true;
                }
            }
        }

        if (!oldType.getPatternFacets().isEmpty()) {
            return true;
        }

        return false;
    }

    private void updateSimpleTypeFacets(final XSDSimpleTypeDefinition type, final XSDSimpleTypeDefinition baseType) {
        final List<XSDConstrainingFacet> facets = type.getFacetContents();
        if (!getSimpleTypeFacets().areLengthFacetsSupported(baseType)) {
            removeFacet(facets, type.getLengthFacet());
            removeFacet(facets, type.getMinLengthFacet());
            removeFacet(facets, type.getMaxLengthFacet());
        }
        if (!getSimpleTypeFacets().areInclusiveExclusiveFacetsSupported(baseType)) {
            removeFacet(facets, type.getMinInclusiveFacet());
            removeFacet(facets, type.getMaxInclusiveFacet());
            removeFacet(facets, type.getMinExclusiveFacet());
            removeFacet(facets, type.getMaxExclusiveFacet());
        }
        if (!getSimpleTypeFacets().isFractionDigitsFacetSupported(baseType)) {
            removeFacet(facets, type.getFractionDigitsFacet());
        }
        if (!getSimpleTypeFacets().isTotalDigitsFacetSupported(baseType)) {
            removeFacet(facets, type.getTotalDigitsFacet());
        }
        if (!getSimpleTypeFacets().isEnumerationFacetSupported(baseType)) {
            for (final Iterator<XSDConstrainingFacet> it = facets.iterator(); it.hasNext();) {
                if (it.next() instanceof XSDEnumerationFacet) {
                    it.remove();
                }
            }
        }
        if (!getSimpleTypeFacets().isWhitespaceFacetSupported(baseType)) {
            removeFacet(facets, type.getWhiteSpaceFacet());
        }
    }

    private void removeFacet(final List<XSDConstrainingFacet> facets, final XSDConstrainingFacet facet) {
        if (facet != null) {
            facets.remove(facet);
        }
    }

    /**
     * Utility method. Checks if the given element is of simple type. If it is
     * and the old facets can be reused by the new simple type, executes the
     * {@link SetBaseTypeCommand}. <br>
     * If the old facets cannot be reused, returns <code>null</code>. <br>
     * <br>
     * <i>NOTE: The set base type command is not executed through the
     * environment, but directly through its run method.</i>
     * 
     * @param elementType
     *            - the element type that we are changing
     * @param newType
     *            - the new type we are trying to set
     * @param monitor
     *            - the progress monitor to reuse in the
     *            {@link SetBaseTypeCommand} execution
     * @param info
     * @param root
     * @return the status of the {@link SetBaseTypeCommand} execution or
     *         <code>null</code> if the {@link SetBaseTypeCommand} was not
     *         executed
     * @throws ExecutionException
     */
    public static IStatus setBaseTypeIfFacetsCanBeReused(final IType elementType, final IType newType,
            final IProgressMonitor monitor, final IAdaptable info, final IModelRoot root) throws ExecutionException {
        if ((elementType != null) && elementType.isAnonymous() && (elementType instanceof SimpleType)
                && (elementType.getBaseType() instanceof SimpleType) && (newType instanceof SimpleType)) {

            // if one of the constraints can be reused - set restriction base
            // type
            if (SetBaseTypeCommand.canReuseAtLeastOneFacet((XSDSimpleTypeDefinition) elementType.getComponent(),
                    (XSDSimpleTypeDefinition) newType.getComponent())) {
                final SetBaseTypeCommand baseTypeCommand = new SetBaseTypeCommand(root, elementType, newType);
                return baseTypeCommand.run(monitor, info);
            }
        }
        return null;
    }

    // =========================================================
    // helpers
    // =========================================================

    private static ISimpleTypeFacetsUtils getSimpleTypeFacets() {
        return SimpleTypeFacetsUtils.instance();
    }
}
