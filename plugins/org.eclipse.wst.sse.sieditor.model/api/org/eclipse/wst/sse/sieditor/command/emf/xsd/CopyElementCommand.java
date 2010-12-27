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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDFactory;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractXSDComponent;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for copying a local ElementDeclaration or an AttributeDeclaration
 * 
 * 
 */
public class CopyElementCommand extends AbstractXSDNotificationOperation {
    private final IElement _element;
    private IElement _copiedElement;
    private final ISchema _targetSchema;

    public CopyElementCommand(final IXSDModelRoot root, final IStructureType structure, final IElement element,
            final ISchema targetSchema) {
        super(root, structure, Messages.CopyElementCommand_copy_element_command_label);
        this._element = element;
        this._targetSchema = targetSchema;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final StructureType structureType = (StructureType) modelObject;
        final XSDNamedComponent schemaComponent = structureType.getComponent();
        final XSDElementDeclaration element;
        final XSDTypeDefinition xsdType;

        element = schemaComponent instanceof XSDElementDeclaration ? (XSDElementDeclaration) schemaComponent : null;
        xsdType = element == null ? (XSDTypeDefinition) schemaComponent : element.getType();

        XSDComplexTypeDefinition type = null;
        if (null != element) {
            final XSDTypeDefinition elementType = element.getAnonymousTypeDefinition();
            if (element.isElementDeclarationReference() || null == elementType || elementType instanceof XSDSimpleTypeDefinition) {

                final EnsureElementAnonymousTypeCommand command = new EnsureElementAnonymousTypeCommand(getModelRoot(), element);
                IStatus status = getModelRoot().getEnv().execute(command);
                if(!StatusUtils.canContinue(status)) {
                	return status;
                }
                type = command.getAnonymousType();
            } else {
                type = (XSDComplexTypeDefinition) elementType;
            }
        } else {
            type = (XSDComplexTypeDefinition) xsdType;
        }

        final Schema schema = (Schema) structureType.getParent();
        if (type == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.CopyElementCommand_msg_can_not_create_type_definition);
        }
        XSDComplexTypeContent content = type.getContent();
        if (null == content || content instanceof XSDSimpleTypeDefinition) {
            content = getXSDFactory().createXSDParticle();
            XSDModelGroup particleContent = getXSDFactory().createXSDModelGroup();
            ((XSDModelGroup) particleContent).setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            ((XSDParticle) content).setContent(particleContent);
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_Content());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_ContentType());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_BaseTypeDefinition());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_Block());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_DerivationMethod());
            type.setContent(content);
            type.setContentType(content);
        }
        final XSDParticle particle = (XSDParticle) content;
        XSDParticleContent particleContent = particle.getContent();
        if (null == particleContent) {
            particleContent = getXSDFactory().createXSDModelGroup();
            ((XSDModelGroup) particleContent).setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            particle.setContent(particleContent);
        }
        final XSDModelGroup modelGroup = (XSDModelGroup) particleContent;

        XSDConcreteComponent copiedComponent = null;
        XSDNamedComponent namedComponent = null;
        final XSDConcreteComponent component = _element.getComponent();
        if (component instanceof XSDParticle) {
            final XSDParticle elementParticle = (XSDParticle) component.cloneConcreteComponent(true, false);
            modelGroup.getContents().add(elementParticle);
            namedComponent = (XSDNamedComponent) elementParticle.getContent();
            copiedComponent = elementParticle;
        } else if (component instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) component.cloneConcreteComponent(true, false);
            XSDAttributeUse attributeUse = getXSDFactory().createXSDAttributeUse();
            attributeUse.setContent(attribute);
            type.getAttributeContents().add(attributeUse);
            copiedComponent = attribute;
            namedComponent = attribute;
        }

        IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(type.getSchema());
        _copiedElement = new Element(xsdModelRoot, copiedComponent, modelGroup, structureType, schema);
		
		IModelObject modelObject = ((IXSDModelRoot) getModelRoot()).getSchema();
	    
        final CopyTypeCommand command = new CopyTypeCommand(getModelRoot(), modelObject,
                (XSDNamedComponent) (component instanceof XSDParticle ? ((XSDParticle) component).getContent() : component),
                _targetSchema, _element.getName());
        command.updateReferences(namedComponent);
        IStatus status = getModelRoot().getEnv().execute(command);
		if (!StatusUtils.canContinue(status)) {
            return status;
        }

        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return !(null == modelObject || null == _targetSchema || null == _element);
    }

    public IElement getCopiedElement() {
        return _copiedElement;
    }

}
