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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDConcreteComponentImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class SetStructureTypeBaseTypeCompositeCommand extends AbstractCompositeNotificationOperation {

    private final IType iNewBaseType;
    private final IStructureType iComplexType;

    private ImportSchemaCommand importSchemaCommand;
    private SetStructureTypeBaseTypeCommandInternal setBaseTypeCommand;

    public SetStructureTypeBaseTypeCompositeCommand(final IModelRoot root, final IStructureType iComplexType,
            final IType iNewBaseType) {
        super(root, iComplexType, Messages.SetBaseTypeCommand_set_base_type_command_label);
        this.iComplexType = iComplexType;
        this.iNewBaseType = iNewBaseType;
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        if (importSchemaCommand == null) {
            importSchemaCommand = new ImportSchemaCommand((IXSDModelRoot) iComplexType.getModelRoot(), iComplexType.getParent(),
                    (AbstractType) iNewBaseType);
            return importSchemaCommand;
        }

        if (setBaseTypeCommand == null) {
            setBaseTypeCommand = new SetStructureTypeBaseTypeCommandInternal(root, iComplexType, iNewBaseType);
            return setBaseTypeCommand;
        }

        return null;
    }

    @Override
    public boolean canExecute() {
        return modelObject != null && iNewBaseType != null;
    }

    // =========================================================
    // complex type internal command
    // =========================================================

    private class SetStructureTypeBaseTypeCommandInternal extends AbstractNotificationOperation {

        private final IType iNewBaseType;
        private final IStructureType iComplexType;

        public SetStructureTypeBaseTypeCommandInternal(final IModelRoot root, final IStructureType iComplexType,
                final IType iNewBaseType) {
            super(root, iComplexType, Messages.SetBaseTypeCommand_set_base_type_command_label);
            this.iComplexType = iComplexType;
            this.iNewBaseType = iNewBaseType;
        }

        @Override
        public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

            final XSDComplexTypeDefinition xsdComplexType = (XSDComplexTypeDefinition) iComplexType.getComponent();
            final XSDTypeDefinition xsdNewBaseType = (XSDTypeDefinition) iNewBaseType.getComponent();
            final XSDTypeDefinition xsdOldBaseType = xsdComplexType.getBaseTypeDefinition();

            if (areEqual(xsdOldBaseType, xsdNewBaseType)) {
                return Status.OK_STATUS;
            }

            final IStatus status = resolveNewBaseType(xsdNewBaseType);
            if (!status.isOK()) {
                return status;
            }

            processNewSimpleTypeDefinition(xsdComplexType, xsdNewBaseType);
            processNewComplexTypeDefinition(xsdComplexType, xsdNewBaseType);

            return Status.OK_STATUS;
        }

        /**
         * Helper method. Checks if the new type to set is the same as the old
         * type.
         * 
         * @param complexType
         * @param newBaseType
         * @return
         */
        private boolean areEqual(final XSDTypeDefinition oldBaseType, final XSDTypeDefinition newBaseType) {
            return newBaseType.getName() != null && newBaseType.getName().equals(oldBaseType.getName())
                    && newBaseType.getTargetNamespace() != null
                    && newBaseType.getTargetNamespace().equals(oldBaseType.getTargetNamespace());
        }

        private void processNewSimpleTypeDefinition(final XSDComplexTypeDefinition complexType,
                final XSDTypeDefinition newBaseType) {
            if (isContentChangingFromComplexToSimpleContent(complexType.getContent(), newBaseType)) {
                updateFromComplexToSimpleContent(complexType);
            }
            if (newBaseType instanceof XSDSimpleTypeDefinition) {
                if (shouldUpdateDerivationMethodToExtension(complexType)) {
                    complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                }
                ((XSDSimpleTypeDefinition) complexType.getContent()).setBaseTypeDefinition((XSDSimpleTypeDefinition) newBaseType);
                complexType.setBaseTypeDefinition(newBaseType);
            }
        }

        private void processNewComplexTypeDefinition(final XSDComplexTypeDefinition complexType,
                final XSDTypeDefinition newBaseType) {
            if (isContentChangingFromSimpleToComplexContent(complexType, newBaseType)) {
                updateFromSimpleToComplexContent(complexType);
            }
            if (newBaseType instanceof XSDComplexTypeDefinition) {
                if (shouldUpdateDerivationMethodToExtension(complexType)) {
                    complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                }
                complexType.setBaseTypeDefinition(newBaseType);
            }
        }

        private boolean shouldUpdateDerivationMethodToExtension(final XSDComplexTypeDefinition complexType) {
            return null == complexType.getDerivationMethod()
                    || complexType.getDerivationMethod() != XSDDerivationMethod.EXTENSION_LITERAL;
        }

        // =========================================================
        // complex to simple content update methods
        // =========================================================

        private boolean isContentChangingFromComplexToSimpleContent(final XSDComplexTypeContent complexTypeContent,
                final XSDTypeDefinition newBaseType) {
            return newBaseType instanceof XSDSimpleTypeDefinition
                    && (null == complexTypeContent || complexTypeContent instanceof XSDParticle);
        }

        private XSDComplexTypeContent updateFromComplexToSimpleContent(final XSDComplexTypeDefinition complexType) {
            final EList<XSDConcreteComponent> oldXsdContents = ((XSDConcreteComponentImpl) complexType).getXSDContents();

            final Map<XSDAttributeUse, XSDAnnotation> clonedAttributes = getClonedAttributes(oldXsdContents);

            complexType.setContent(null);
            complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);

            final XSDComplexTypeContent complexTypeContent = EmfXsdUtils.getXSDFactory().createXSDSimpleTypeDefinition();
            complexType.setContent(complexTypeContent);
            
            complexType.getAttributeContents().clear();
            addAttributes(complexType, clonedAttributes);

            complexType.elementChanged(complexType.getElement());
            return complexTypeContent;
        }

        /**
         * Helper method. Returns collection of the cloned attributes in the
         * oldXsdContents
         */
        private Map<XSDAttributeUse, XSDAnnotation> getClonedAttributes(final EList<XSDConcreteComponent> oldXsdContents) {

            final Map<XSDAttributeUse, XSDAnnotation> attributes = new HashMap<XSDAttributeUse, XSDAnnotation>();

            for (final XSDConcreteComponent component : oldXsdContents) {
                if (component instanceof XSDAttributeUse) {
                    final XSDAttributeUse oldXsdAttributeUse = (XSDAttributeUse) component;
                    final XSDAnnotation oldAnnotation = oldXsdAttributeUse.getAttributeDeclaration().getAnnotation();

                    final XSDAttributeUse newAttributeUse = (XSDAttributeUse) oldXsdAttributeUse.cloneConcreteComponent(true,
                            false);
                    attributes.put(newAttributeUse, oldAnnotation);
                }
            }
            return attributes;
        }

        // =========================================================
        // simple to complex content update methods
        // =========================================================

        private boolean isContentChangingFromSimpleToComplexContent(final XSDComplexTypeDefinition complexType,
                final XSDTypeDefinition newBaseType) {
            return newBaseType instanceof XSDComplexTypeDefinition
                    && (null == complexType.getContent() || complexType.getContent() instanceof XSDSimpleTypeDefinition);
        }

        private void updateFromSimpleToComplexContent(final XSDComplexTypeDefinition complexType) {
            final EList<XSDConcreteComponent> oldXsdContents = ((XSDConcreteComponentImpl) complexType).getXSDContents();

            final Map<XSDAttributeUse, XSDAnnotation> clonedAttributes = getClonedAttributes(oldXsdContents);

            complexType.setBaseTypeDefinition(null);
            complexType.setContent(null);

            final XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
            complexType.setContent(particle);

            complexType.getAttributeContents().clear();
            addAttributes(complexType, clonedAttributes);

            complexType.elementChanged(complexType.getElement());
        }

        private void addAttributes(final XSDComplexTypeDefinition complexType,
                final Map<XSDAttributeUse, XSDAnnotation> clonedAttributes) {
            // add attributes one-by-one so that notifications are correctly
            // fired
            for (final XSDAttributeUse attribute : clonedAttributes.keySet()) {
                complexType.getAttributeContents().add(attribute);
                EmfXsdUtils.copyAnnotations(clonedAttributes.get(attribute), attribute.getAttributeDeclaration().getAnnotation(),
                        attribute.getSchema().getDocument());
            }
        }

        // =========================================================
        // resolve helpers
        // =========================================================

        private IStatus resolveNewBaseType(final XSDTypeDefinition newBaseType) throws ExecutionException {
            if (newBaseType.getTargetNamespace() == null) {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                        Messages.SetStructureTypeCommand_cannot_refer_type_with_no_namespace);
            }
            final IType resolvedType = iComplexType.getParent().resolveType((AbstractType) iNewBaseType);
            if (resolvedType == null) {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                        Messages.SetStructureTypeCommand_msg_can_not_resolve_type_X, iNewBaseType.getName()));
            }
            return Status.OK_STATUS;
        }

        @Override
        public boolean canExecute() {
            return modelObject != null && iNewBaseType != null;
        }

    }

}
