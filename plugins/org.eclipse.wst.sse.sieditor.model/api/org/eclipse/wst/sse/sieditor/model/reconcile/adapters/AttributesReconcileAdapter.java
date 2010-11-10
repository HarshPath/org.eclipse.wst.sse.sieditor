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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.IConcreteComponentSource;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * Subclass of the {@link AbstractModelReconcileAdapter}. This adapter is
 * responsible for the fixing of problems caused by DOM element attributes
 * changes.
 * 
 * 
 * 
 */
public class AttributesReconcileAdapter extends AbstractModelReconcileAdapter {

    public AttributesReconcileAdapter(final IConcreteComponentSource concreteComponentSource) {
        super(concreteComponentSource);
    }

    @Override
    protected void processNotifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos) {
        if (changedFeature instanceof Attr) {
            processAttributeChangeFeature(notifier, (Attr) changedFeature, oldValue, newValue);
        }
    }

    private void processAttributeChangeFeature(final INodeNotifier notifier, final Attr attr, final Object oldValue,
            final Object newValue) {
        processNameAttribute(notifier, attr.getLocalName());
        processRefAttribute(notifier, attr.getLocalName(), newValue);
        processSchemaLocationAttribute(notifier, attr.getLocalName(), newValue);
        processAttributeStartingWithXMLNS(notifier, oldValue, newValue, attr);
        processParameterOrderAttribute(notifier, attr.getLocalName());
        processTargetNamespaceAttribute(notifier, attr.getName());
        processNamespaceAttribute(notifier, attr.getName());
        processElementAttribute(notifier, attr.getLocalName(), newValue);
        processTypeAttribute(notifier, attr.getLocalName(), newValue);
        processDefaultAttribute(notifier, attr.getLocalName(), newValue);
    }

    private void processNameAttribute(final INodeNotifier notifier, final String attributeName) {
        if (XSDConstants.NAME_ATTRIBUTE.equals(attributeName)) {
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            if (eObject instanceof XSDElementDeclaration) {
                this.getModelReconcileRegistry().setNeedsReconciling(true);
            }
        }
    }

    private void processRefAttribute(final INodeNotifier notifier, final String attributeName, final Object newValue) {
        if (XSDConstants.REF_ATTRIBUTE.equals(attributeName) && newValue != null) {
            /*
             * we are in the following scenario: we are setting "ref" to element
             * attribute. if the element we are updating has "type" attribute,
             * the EMF model does not set it to null. that way, the updated
             * element has both "type" and "ref" attributes, which is invalid
             */
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            if (eObject instanceof XSDConcreteComponent) {
                final XSDConcreteComponent element = (XSDConcreteComponent) eObject;
                if (element instanceof XSDElementDeclaration) {
                    ((XSDElementDeclaration) element).setTypeDefinition(null);
                } else if (element instanceof XSDAttributeDeclaration) {
                    ((XSDAttributeDeclaration) element).setTypeDefinition(null);
                }
            }
        }
    }

    private void processSchemaLocationAttribute(final INodeNotifier notifier, final String attributeName, final Object newValue) {
        if (XSDConstants.SCHEMALOCATION_ATTRIBUTE.equals(attributeName)) {
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);

            if (eObject instanceof XSDImportImpl) {
                XSDSchema schema = null;

                final XSDImportImpl xsdImport = (XSDImportImpl) eObject;
                if (xsdImport.getResolvedSchema() == null && newValue == null) {
                    xsdImport.reset();
                    xsdImport.importSchema();
                }
                schema = xsdImport.getResolvedSchema();
                
                getModelReconcileRegistry().setNeedsReconciling(true);
                getModelReconcileRegistry().addChangedSchema(schema);
            }
        }
    }

    private void processAttributeStartingWithXMLNS(final INodeNotifier notifier, final Object oldValue, final Object newValue,
            final Attr attr) {
        if (attr.getName().startsWith(EmfXsdUtils.XMLNS_PREFIX)) {
            /*
             * here we are updating the "xmlns" attribute. if that's the case,
             * we are either updating the schema element of a XSD document, or a
             * WSDL definition element, or a WSDL types schema
             */
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            XSDSchema schema = null;
            if (eObject instanceof XSDSchema) {
                schema = (XSDSchema) eObject;
            } else if (eObject instanceof XSDSchemaExtensibilityElement) {
                schema = ((XSDSchemaExtensibilityElement) eObject).getSchema();
            }

            if (schema != null) {
                this.getModelReconcileRegistry().setNeedsReconciling(true);
                this.getModelReconcileRegistry().addChangedSchema(schema);
                fixSchemaForSchemaState(oldValue, newValue, attr, schema);
            }
        }
    }

    private void fixSchemaForSchemaState(final Object oldValue, final Object newValue, final Attr attr, final XSDSchema xsdSchema) {
        final String prefix = getPrefix(attr);
        if (newValue == null) {
            /*
             * the new value is null, so we need to remove the prefix/namespace
             * from the EMF model map. In some cases (on undo/redo) the EMF
             * didn't do that
             */
            if (xsdSchema.getQNamePrefixToNamespaceMap().containsKey(prefix)) {
                xsdSchema.getQNamePrefixToNamespaceMap().remove(prefix);
            }
            /*
             * notify for the change anyway, since we want to run the
             * validation. the EMF model didn't do that as well
             */
            xsdSchema.eNotify(new ENotificationImpl((InternalEObject) xsdSchema, Notification.REMOVE, null, null, null));
        } else {
            /*
             * we have non-null value, so we must be updating existing
             * namespace, or adding a new one
             */
            if (XSDConstants.isSchemaForSchemaNamespace(attr.getValue())
                    && !attr.getValue().equals(xsdSchema.getQNamePrefixToNamespaceMap().get(prefix))) {
                // add the namespace to the map
                xsdSchema.getQNamePrefixToNamespaceMap().put(prefix, attr.getValue());
            }
            /*
             * notify for the change anyway, since we want to run the
             * validation. the EMF model didn't do that as well
             */
            xsdSchema.eNotify(new ENotificationImpl((InternalEObject) xsdSchema, Notification.ADD, null, null, null));
        }
    }

    private void processParameterOrderAttribute(final INodeNotifier notifier, final String attributeName) {
        if (WSDLConstants.PARAMETER_ORDER_ATTRIBUTE.equals(attributeName)) {
            /* we need to check if we are updating a part name */
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            if (eObject instanceof Operation) {
                /*
                 * we need to tell the operation EMF object to update itself
                 * from its corresponding DOM element. this updates the
                 * parameterOrdering collection of the operation
                 */
                ((Operation) eObject).elementChanged((Element) notifier);
            }
        }
    }

    private void processTargetNamespaceAttribute(final INodeNotifier notifier, final String attributeName) {
        if (XSDConstants.TARGETNAMESPACE_ATTRIBUTE.equals(attributeName)) {
            /*
             * we need to prepare the reconciling information. setting
             * tagetNamespace "breaks" the EMF WSDL references
             */
            this.getModelReconcileRegistry().setNeedsReconciling(true);
            if (notifier instanceof Element) {
                final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
                if (eObject instanceof XSDSchema) {
                    this.getModelReconcileRegistry().addChangedSchema((XSDSchema) eObject);
                } else if (eObject instanceof XSDSchemaExtensibilityElement) {
                    this.getModelReconcileRegistry().addChangedSchema(((XSDSchemaExtensibilityElement) eObject).getSchema());
                }
            }
        }
    }

    private void processNamespaceAttribute(final INodeNotifier notifier, final String attributeName) {
        if (WSDLConstants.NAMESPACE_ATTRIBUTE.equals(attributeName)) {
            this.getModelReconcileRegistry().setNeedsReconciling(true);
            /*
             * we are updating schema import. this breaks the schema references
             * to the changed import. we need to update the information for
             * resolve.
             */
            if (notifier instanceof Element) {
                final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
                if (eObject instanceof XSDImport) {
                    this.getModelReconcileRegistry().addChangedSchema(((XSDImport) eObject).getSchema());
                }
            }
        }
    }

    private void processElementAttribute(final INodeNotifier notifier, final String attributeName, final Object newValue) {
        if (WSDLConstants.ELEMENT_ATTRIBUTE.equals(attributeName)) {
            processPartAttribute(notifier, newValue, WSDLPackage.PART__ELEMENT_DECLARATION);
        }
    }

    private void processTypeAttribute(final INodeNotifier notifier, final String attributeName, final Object newValue) {
        if (WSDLConstants.TYPE_ATTRIBUTE.equals(attributeName)) {
            processPartAttribute(notifier, newValue, WSDLPackage.PART__TYPE_DEFINITION);
            processElementTypeAttribute(notifier, newValue);
        }
    }

    private void processPartAttribute(final INodeNotifier notifier, final Object newValue, final int eStructuralFeatureId) {
        if (notifier instanceof Element) {
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            if (eObject instanceof Part) {
                this.getModelReconcileRegistry().setNeedsReconciling(true);
                final Part part = (Part) eObject;
                if (newValue == null) {
                    part
                            .eNotify(new ENotificationImpl((InternalEObject) part, Notification.SET, eStructuralFeatureId, null,
                                    null));
                }
                part.elementChanged(part.getElement());
            }
        }
    }

    private void processElementTypeAttribute(final INodeNotifier notifier, final Object newValue) {
        if (notifier instanceof Element && newValue != null) {
            final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            if (eObject instanceof XSDElementDeclaration) {
                final XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) eObject;
                if (elementDeclaration.getTypeDefinition() != null && elementDeclaration.getTypeDefinition().eContainer() == null) {
                    getModelReconcileRegistry().setNeedsReconciling(true);
                    getModelReconcileRegistry().addChangedSchema(elementDeclaration.getSchema());
                }
            }
        }
    }
    
    private void processDefaultAttribute(final INodeNotifier notifier, final String attributeName, final Object newValue) {
        if (XSDConstants.DEFAULT_ATTRIBUTE.equals(attributeName)) {
            if (notifier instanceof Element) {
                final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
                ((XSDFeature)eObject).setLexicalValue((String)newValue);
                eObject.eNotify(new ENotificationImpl((InternalEObject) eObject, Notification.SET, XSDPackage.XSD_FEATURE__VALUE, null,
                        newValue));
            }
        }
    }

}
