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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

/**
 * Command for setting a parameter type
 * 
 * 
 * 
 */
public class SetParameterTypeCommand extends AbstractCompositeNotificationOperation {

    private final IType newType;
    private final IParameter parameter;

    public SetParameterTypeCommand(final IParameter parameter, final IType newType) {
        this(parameter.getModelRoot(), parameter, newType);

    }

    public SetParameterTypeCommand(final IModelRoot root, final IParameter parameter, final IType newType) {
        super(root, parameter, Messages.SetParameterTypeCommand_set_parameter_type_command_label);
        this.parameter = parameter;
        this.newType = newType;
        setTransactionPolicy(TransactionPolicy.MULTI);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        if (subOperations.isEmpty()) {
            if (newType == null) {
                SetParameterTypeWithoutImportingCommand.sendTypeIsNull(modelObject);
                return null;
            }

            final IDescription description = wsdlModelRoot.getDescription();
            final String errorMessage = "Failed to resolve type"; //$NON-NLS-1$
            try {
                return new ImportSchemaCommand(wsdlModelRoot, description, URIHelper.createEncodedURI(description.getLocation()),
                        URIHelper.createEncodedURI(newType.getParent().getComponent().getSchemaLocation()),
                        (AbstractType) newType, DocumentType.XSD_SHEMA);
            } catch (final UnsupportedEncodingException e) {
                Logger.logError(errorMessage, e);
            } catch (final URISyntaxException e) {
                Logger.logError(errorMessage, e);
            }
        } else if (subOperations.size() == 1) {
            return new SetParameterTypeWithoutImportingCommand(wsdlModelRoot, parameter, newType);
        }

        return null;
    }

    private static class SetParameterTypeWithoutImportingCommand extends AbstractWSDLNotificationOperation {

        private final IType newType;
        private final IType oldType;
        private String xsdSchemaPrefix = null;
        private boolean emptyDefaultNSAdded;

        public SetParameterTypeWithoutImportingCommand(final IWsdlModelRoot root, final IParameter parameter, final IType newType) {
            super(root, parameter, Messages.SetParameterTypeCommand_set_parameter_type_command_label);
            this.newType = newType;
            this.oldType = parameter.getType();
            emptyDefaultNSAdded = false;
        }

        protected static void sendTypeIsNull(final IModelObject modelObject) {
            final OperationParameter parameter = (OperationParameter) modelObject;
            final Part part = parameter.getComponent();
            part.setElementDeclaration(null);
            part.setElementName(null);
            part.setTypeDefinition(null);
            part.setTypeName(null);
        }

        @Override
        public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            return setType(monitor, info, newType);
        }

        private IStatus setType(final IProgressMonitor monitor, final IAdaptable info, final IType type)
                throws ExecutionException {
            if (type == null) {
                SetParameterTypeWithoutImportingCommand.sendTypeIsNull(modelObject);
                return Status.OK_STATUS;
            }
            // if both the type's schema targetNamespace and the Definition's
            // are null
            // then add an xmlns="" attribute to the definitions, in order for
            // the type
            // reference without prefix to be resolvable.
            if (type.getComponent().getTargetNamespace() == null) {
                final Definition definitions = ((Description) root.getModelObject()).getComponent();
                final String EMPTY_STRING = "";//$NON-NLS-1$
                if (definitions.getNamespace(EMPTY_STRING) == null) {
                    definitions.getElement().setAttribute(EmfXsdUtils.XMLNS_PREFIX, EMPTY_STRING);
                    emptyDefaultNSAdded = true;
                }
            }
            final OperationParameter parameter = (OperationParameter) modelObject;
            final Part part = parameter.getComponent();
            final XSDNamedComponent typeToBeReferenced = type.getComponent();

            // final NamedNodeMap partDomAttributes =
            // part.getElement().getAttributes();
            final Definition definition = part.getEnclosingDefinition();
            final String targetNamespace = typeToBeReferenced.getTargetNamespace();
            final QName typeQname = new QName(/*
                                               * null == targetNamespace ?
                                               * definition.getTargetNamespace()
                                               * :
                                               */targetNamespace, typeToBeReferenced.getName());

            if (definition.getPrefix(targetNamespace) == null) {
                EmfWsdlUtils.ensurePrefix(definition, targetNamespace);
                xsdSchemaPrefix = definition.getPrefix(targetNamespace);
                updateImportedSchemas(targetNamespace);
            }

            if (typeToBeReferenced instanceof XSDTypeDefinition) {
                if (part.getElementDeclaration() != null) {
                    part.getElement().getAttributes().removeNamedItem(WSDLConstants.ELEMENT_ATTRIBUTE);
                }
                
                final XSDTypeDefinition typeDefinition = (XSDTypeDefinition) typeToBeReferenced;
                part.setTypeName(typeQname);
                part.setTypeDefinition(typeDefinition);
                
                return Status.OK_STATUS;
                
            } else if (typeToBeReferenced instanceof XSDElementDeclaration) {
                
                if (part.getTypeDefinition() != null) {
                    part.getElement().getAttributes().removeNamedItem(WSDLConstants.TYPE_ATTRIBUTE);
                }
                final XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) typeToBeReferenced;
                part.setElementDeclaration(elementDeclaration);
                part.setElementName(typeQname);

                return Status.OK_STATUS;
            }

            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.SetParameterTypeCommand_msg_only_definitions_and_element_can_be_referenced);

        }

        private void updateImportedSchemas(final String tns) {
            final IWsdlModelRoot wsdlRoot = getModelRoot();
            final List<ISchema> containedSchemas = wsdlRoot.getDescription().getContainedSchemas();
            for (final ISchema schema : containedSchemas) {
                if (schema.getNamespace() != null) {
                    continue;
                }
                final Collection<ISchema> allReferredSchemas = schema.getAllReferredSchemas();
                for (final ISchema referredSchema : allReferredSchemas) {
                    if (tns == null || !tns.equals(referredSchema.getNamespace())) {
                        continue;
                    }
                    final XSDSchema xsdSchema = schema.getComponent();
                    final Element shemaElement = xsdSchema.getElement();
                    if (shemaElement != null) {
                        xsdSchema.elementChanged(shemaElement);
                    }
                    break;
                }
            }
        }

        @Override
        protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            return run(monitor, info);
        }

        @Override
        protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
            if (emptyDefaultNSAdded) {
                // if the command added an xmlns="" attribute to definitions
                final Definition definitions = ((Description) root.getModelObject()).getComponent();
                definitions.addNamespace(null, null);
                // This is workaround for a bug in the eclipse definitions api:
                definitions.getElement().removeAttribute(EmfXsdUtils.XMLNS_PREFIX);
            }
            if (xsdSchemaPrefix != null) {
                final Definition definition = ((Description) root.getModelObject()).getComponent();
                definition.addNamespace(xsdSchemaPrefix, null);

                final Element element = definition.getElement();
                final NamedNodeMap partDomAttributes = element.getAttributes();
                final String qname = EmfXsdUtils.XMLNS_PREFIX + ":" + xsdSchemaPrefix; //$NON-NLS-1$
                if (partDomAttributes.getNamedItem(qname) != null) {
                    partDomAttributes.removeNamedItem(qname);
                }
            }
            return setType(monitor, info, oldType);
        }

        @Override
        public boolean canExecute() {
            return newType != null && newType instanceof AbstractType;
        }

    }
}
