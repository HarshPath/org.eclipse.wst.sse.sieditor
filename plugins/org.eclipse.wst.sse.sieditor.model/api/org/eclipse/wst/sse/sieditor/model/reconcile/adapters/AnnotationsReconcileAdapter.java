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
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.impl.XSDAttributeDeclarationImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.IConcreteComponentSource;

/**
 * Subclass of the {@link AbstractModelReconcileAdapter}. This adapter is
 * responsible for the fixing of problems caused by element annotations changes.
 * 
 */
public class AnnotationsReconcileAdapter extends AbstractModelReconcileAdapter {

    public AnnotationsReconcileAdapter(final IConcreteComponentSource concreteComponentSource) {
        super(concreteComponentSource);
    }

    @Override
    protected void processNotifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos) {
        if (changedFeature != null && !(changedFeature instanceof Element)) {
            return;
        }
        if (notifier instanceof Element && oldValue instanceof Element && INodeNotifier.REMOVE == eventType) {
            fixAttributeAnnotationRemove((Element) notifier, (Element) changedFeature, oldValue, newValue);
        } else if (notifier instanceof Element && newValue instanceof Element && INodeNotifier.ADD == eventType) {
            fixAttributeAnnotationAdd((Element) notifier, changedFeature, oldValue, (Element) newValue);
        }
    }

    private void fixAttributeAnnotationRemove(final Element parentElement, final Element removedElement, final Object oldValue,
            final Object newValue) {
        // the changed feature should be the old (removed) value
        if (removedElement == null ? oldValue != null : !removedElement.equals(oldValue)) {
            return;
        }
        // if not from this NS
        if (!isPropperNamespace(removedElement, parentElement)) {
            return;
        }

        final String removedElementName = removedElement.getLocalName();

        final String parentElementName = parentElement.getLocalName();

        if (XSDConstants.ANNOTATION_ELEMENT_TAG.equals(removedElementName)
                && (XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(parentElementName))) {

            final EObject parentEObject = concreteComponentSource.getConcreteComponentFor(parentElement);

            if (parentEObject instanceof XSDAttributeDeclaration) {
                ((XSDAttributeDeclarationImpl) parentEObject).basicSetAnnotation(null, null);
            }
        }
        // the case of adding/removing annotation from attribute of an extension
        // of a simpleContent
        else if (XSDConstants.EXTENSION_ELEMENT_TAG.equals(parentElementName)
                && XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(removedElementName)) {
            processRareRemoveAttributeAnnotation(removedElement, parentElement);

        }
    }

    private void fixAttributeAnnotationAdd(final Element parentElement, final Object changedFeature, final Object oldValue,
            final Element addedElement) {
        if (changedFeature == null ? oldValue != null : !changedFeature.equals(oldValue)) {
            return;
        }
        final boolean propoerNamespace = isPropperNamespace(addedElement, parentElement);

        final String addedElementName = addedElement.getLocalName();

        final String parentElementName = parentElement.getLocalName();
        // the case where an annotation is added to an attribute of an extension
        // element.
        if (propoerNamespace && XSDConstants.EXTENSION_ELEMENT_TAG.equals(parentElementName)
                && XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(addedElementName)) {
            processRareAddAttributeAnnotation(parentElement, addedElement);
        }
    }

    /**
     * Add Annotation to : &lt;xsd:complexType name="Amount"><br>
     * &lt;xsd:simpleContent><br>
     * &lt;xsd:extension base="AmountContent"><br>
     * &lt;xsd:attribute name="currencyCode" type="xsd:token"><br>
     * <b>&lt;xsd:annotation>... &lt;/xsd:annotation></b><br>
     * &lt;/xsd:attribute><br>
     * &lt;/xsd:extension><br>
     * &lt;/xsd:simpleContent><br>
     * &lt;/xsd:complexType>
     * 
     * @param removedElement
     *            the attribute if removed
     * @param parentElement
     *            the Extension element
     * @param newValue
     *            the newly set attribute
     * @param eventType
     *            {@link INodeNotifier#ADD} or {@link INodeNotifier#REMOVE}
     */
    private void processRareAddAttributeAnnotation(final Element parentElement, final Element addedElement) {
        final List<XSDAttributeDeclaration> eAttributes = findAttributeInExtension(addedElement, parentElement);
        if (eAttributes.isEmpty()) {
            return;
        }
        // for all attributes with matching name of the added
        for (final XSDAttributeDeclaration eAttribute : eAttributes) {
            if (eAttribute.getAnnotation() != null
                    || (eAttribute.getName() != null && !eAttribute.getName().equals(
                            addedElement.getAttribute(XSDConstants.NAME_ATTRIBUTE)))) {
                continue;
            }
            // Apply fix only for the first annotation element found in the
            // attribute.
            // all other attributes will be ignored as invalid!
            final Element annotationElement = getAnnotationElement(addedElement, parentElement);
            if (annotationElement != null) {
                final XSDAnnotation eAnnotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                eAnnotation.setElement(annotationElement);
                eAnnotation.elementChanged(eAnnotation.getElement());
                eAttribute.setAnnotation(eAnnotation);
            }
        }
    }

    /**
     * Remove Annotation from : &lt;xsd:complexType name="Amount"><br>
     * &lt;xsd:simpleContent><br>
     * &lt;xsd:extension base="AmountContent"><br>
     * &lt;xsd:attribute name="currencyCode" type="xsd:token"><br>
     * &lt;xsd:annotation>... &lt;/xsd:annotation><br>
     * &lt;/xsd:attribute><br>
     * &lt;/xsd:extension><br>
     * &lt;/xsd:simpleContent><br>
     * &lt;/xsd:complexType>
     * 
     * @param removedElement
     *            the attribute if removed
     * @param parentElement
     *            the Extension element
     * @param newValue
     *            the newly set attribute
     * @param eventType
     *            {@link INodeNotifier#ADD} or {@link INodeNotifier#REMOVE}
     */
    private void processRareRemoveAttributeAnnotation(final Element removedElement, final Element parentElement) {
        final List<XSDAttributeDeclaration> eAttributes = findAttributeInExtension(removedElement, parentElement);
        for (final XSDAttributeDeclaration eAttribute : eAttributes) {
            if (eAttribute == null) {
                return;
            }
            if (getAnnotationElement(removedElement, null) == null) {
                ((XSDAttributeDeclarationImpl) eAttribute).basicSetAnnotation(null, null);
            }
        }
    }

    /**
     * Finds the given eObject for the manipulatedAttribute which is
     * added/removed from the element of the xsd:extension.
     * 
     * @param manipulatedAttribute
     *            the attribute
     * @param extensionElement
     *            the extension element
     * @return the found eObject for the given attribute or null if no such is
     *         found.
     */
    private List<XSDAttributeDeclaration> findAttributeInExtension(final Element manipulatedAttribute,
            final Element extensionElement) {
        final List<XSDAttributeDeclaration> attributes = new ArrayList<XSDAttributeDeclaration>();
        final Node simpleContentNode = extensionElement.getParentNode();
        final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) simpleContentNode.getParentNode());

        if (!(eObject instanceof XSDComplexTypeDefinition)) {
            return null;// this is not the case
        }
        final XSDConcreteComponent complexType = (XSDConcreteComponent) eObject;

        final XSDComplexTypeDefinition eComplexType = (XSDComplexTypeDefinition) complexType;
        final EList<XSDAttributeGroupContent> eAttributes = eComplexType.getAttributeContents();

        final XSDAttributeDeclaration eAttribute = null;
        for (final XSDAttributeGroupContent eAttGroupContent : eAttributes) {
            if (eAttGroupContent instanceof XSDAttributeUse) {
                attributes.add(checkAttributeUse(manipulatedAttribute, (XSDAttributeUse) eAttGroupContent));
            } else if (eAttGroupContent instanceof XSDAttributeGroupDefinition) {
                attributes.addAll(findAttribute(manipulatedAttribute, (XSDAttributeGroupDefinition) eAttGroupContent));
            }
            if (eAttribute != null) {
                attributes.add(eAttribute);
            }
        }
        return attributes;
    }

    private XSDAttributeDeclaration checkAttributeUse(final Element attrElement, final XSDAttributeUse attributeUse) {
        final XSDAttributeDeclaration currentAttribute = attributeUse.getAttributeDeclaration();
        final Element element = currentAttribute.getElement();
        if (element != null && element.getLocalName() != null && element.getLocalName().equals(attrElement.getLocalName())) {
            return currentAttribute;
        }
        return null;
    }

    private List<XSDAttributeDeclaration> findAttribute(final Element attrElement,
            final XSDAttributeGroupDefinition eAttGroupContent) {
        final List<XSDAttributeDeclaration> attributes = new ArrayList<XSDAttributeDeclaration>();
        for (final XSDAttributeUse use : eAttGroupContent.getAttributeUses()) {
            final XSDAttributeDeclaration attribute = checkAttributeUse(attrElement, use);
            if (attribute != null) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    /**
     * @param element
     *            the element containing the annotation
     * @param parentElement
     *            the parent of the element, containing the annotation. May be
     *            null if not extension
     * @return the annotation element or null if no such is found
     */
    private Element getAnnotationElement(final Element element, final Element parentElement) {
        Node child = element.getFirstChild();
        if (child == null) {
            return null;
        }
        do {
            if (XSDConstants.ANNOTATION_ELEMENT_TAG.equals(child.getLocalName())) {
                return (Element) child;
            }
        } while ((child = child.getNextSibling()) != null);
        return null;
    }

    /**
     * Checks if the elements are from the w3 schema for schema NS
     * 
     * @param element
     * @param parentElement
     * @return
     */
    private boolean isPropperNamespace(final Node element, final Node parentElement) {
        final String removedElementNamespace = element.getNamespaceURI();
        boolean propoerNamespace = removedElementNamespace == null
                || XSDConstants.isSchemaForSchemaNamespace(removedElementNamespace);
        final String parentNamespace = parentElement.getNamespaceURI();
        // on undo/redo the element does not contain data about it's
        // namespace - this case should not be excluded
        propoerNamespace = propoerNamespace
                && (parentNamespace == null || XSDConstants.isSchemaForSchemaNamespace(parentNamespace));
        return propoerNamespace;
    }

}
