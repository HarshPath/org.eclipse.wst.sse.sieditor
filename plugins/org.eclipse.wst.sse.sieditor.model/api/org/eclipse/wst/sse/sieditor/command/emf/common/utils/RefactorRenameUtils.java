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
package org.eclipse.wst.sse.sieditor.command.emf.common.utils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.emf.query.conditions.eobjects.structuralfeatures.EObjectReferencerCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;

/**
 * Utility class handling the refactor rename functionality for types, elements,
 * parameters, etc...
 * 
 */
public class RefactorRenameUtils {

    private static final String SOAP_HEADER_PART = "part"; //$NON-NLS-1$
    private static final String SOAP_BODY_PARTS = "parts"; //$NON-NLS-1$

    private static final RefactorRenameUtils INSTANCE = new RefactorRenameUtils();

    private RefactorRenameUtils() {

    }

    public static RefactorRenameUtils instance() {
        return INSTANCE;
    }

    public void refactorRenameComponent(final EObject baseComponent, final XSDNamedComponent componentToRename,
            final String newName) {
        final EObjectCondition refCondition = new EObjectReferencerCondition(componentToRename);
        final IQueryResult result = new SELECT(new FROM(baseComponent), new WHERE(refCondition)).execute();
        processQueryResult(componentToRename.getName(), newName, componentToRename, result);
        componentToRename.setName(newName);
    }

    public void refactorRenameComponent(final EObject baseComponent, final WSDLElement wsdlElement, final String newName) {
        final EObjectCondition refCondition = new EObjectReferencerCondition(wsdlElement);
        final IQueryResult result = new SELECT(new FROM(baseComponent), new WHERE(refCondition)).execute();
        processQueryResult(getWsdlElementName(wsdlElement), newName, wsdlElement, result);
        setWsdlElementName(wsdlElement, newName);
    }

    private void processQueryResult(final String oldName, final String newName, final XSDNamedComponent component,
            final IQueryResult result) {

        for (final Object next : result) {
            if (next instanceof XSDSchema || next instanceof Definition) {
                continue; // we do not need to check root elements
            }
            if (next == component) {
                continue;
            }
            if (next instanceof XSDConcreteComponent) {
                refactorRenameConcreteComponent(oldName, newName, (XSDConcreteComponent) next);
            } else if (next instanceof WSDLElement) {
                refactorRenameWsdlElements(oldName, newName, (WSDLElement) next);
            }
        }
    }

    private void processQueryResult(final String oldName, final String newName, final WSDLElement wsdlElement,
            final IQueryResult result) {
        for (final Object next : result) {
            if (next instanceof XSDSchema || next instanceof Definition) {
                continue;
            }
            if (!(next instanceof WSDLElement)) {
                continue;
            }
            refactorRenameWsdlElements(oldName, newName, (WSDLElement) next);
        }
    }

    private void refactorRenameConcreteComponent(final String oldName, final String newName,
            final XSDConcreteComponent concreteComponent) {

        processContentElement(oldName, newName, concreteComponent.getElement(), XSDConstants.TYPE_ATTRIBUTE);
        processContentElement(oldName, newName, concreteComponent.getElement(), XSDConstants.REF_ATTRIBUTE);
        processContentElement(oldName, newName, concreteComponent.getElement(), XSDConstants.BASE_ATTRIBUTE);
        processComplexTypeContent(oldName, newName, concreteComponent);
        processElementDeclarationComplexType(oldName, newName, concreteComponent);
        processRestrictedElement(oldName, newName, concreteComponent);
    }

    private void processComplexTypeContent(final String oldName, final String newName,
            final XSDConcreteComponent concreteComponent) {
        if (concreteComponent instanceof XSDComplexTypeDefinition) {
            final Element contentElement = RemapReferencesUtils.instance().getComplexTypeContentElement(
                    concreteComponent.getElement());
            if (contentElement != null) {
                processDerivationElement(oldName, newName, contentElement, XSDConstants.EXTENSION_ELEMENT_TAG);
                processDerivationElement(oldName, newName, contentElement, XSDConstants.REDEFINE_ELEMENT_TAG);
            }
        }
    }

    private void processElementDeclarationComplexType(final String oldName, final String newName,
            final XSDConcreteComponent concreteComponent) {
        if (concreteComponent instanceof XSDElementDeclaration) {
            final XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) concreteComponent;
            if (!xsdElementDeclaration.isElementDeclarationReference() && xsdElementDeclaration.getTypeDefinition() != null
                    && xsdElementDeclaration.getTypeDefinition().getComplexType() != null) {
                processDerivationElement(oldName, newName, xsdElementDeclaration.getElement(), XSDConstants.EXTENSION_ELEMENT_TAG);
                processDerivationElement(oldName, newName, xsdElementDeclaration.getElement(), XSDConstants.REDEFINE_ELEMENT_TAG);
            }
        }
    }

    private void processRestrictedElement(final String oldName, final String newName, final XSDConcreteComponent concreteComponent) {
        final Element componentElement = concreteComponent.getElement();

        Element elementToCheckForRestrictionTag = getChildElementWithTag(componentElement, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
        if (elementToCheckForRestrictionTag == null) {
            elementToCheckForRestrictionTag = componentElement;
        }
        final Element restrictionElement = getChildElementWithTag(elementToCheckForRestrictionTag,
                XSDConstants.RESTRICTION_ELEMENT_TAG);

        if (restrictionElement == null) {
            return;
        }
        processContentElement(oldName, newName, restrictionElement, XSDConstants.BASE_ATTRIBUTE);

    }

    private Element getChildElementWithTag(final Element element, final String childElementTag) {
        if (element == null) {
            return null;
        }
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childElementTag.equals(childNodes.item(i).getNodeName())) {
                return (Element) childNodes.item(i);
            }
        }
        return null;
    }

    private void refactorRenameWsdlElements(final String oldName, final String newName, final WSDLElement wsdlElement) {
        processContentElement(oldName, newName, wsdlElement.getElement(), WSDLConstants.NAME_ATTRIBUTE);
        processContentElement(oldName, newName, wsdlElement.getElement(), WSDLConstants.MESSAGE_ATTRIBUTE);
        processContentElement(oldName, newName, wsdlElement.getElement(), WSDLConstants.ELEMENT_ATTRIBUTE);
        processContentElement(oldName, newName, wsdlElement.getElement(), WSDLConstants.TYPE_ATTRIBUTE);
        processContentElement(oldName, newName, wsdlElement.getElement(), SOAP_HEADER_PART);
        processContentElement(oldName, newName, wsdlElement.getElement(), SOAP_BODY_PARTS);
    }

    private void processDerivationElement(final String oldName, final String newName, final Element contentElement,
            final String derivationTag) {
        final Element derivationMethodElement = RemapReferencesUtils.instance().getComplexTypeContainingElement(contentElement,
                derivationTag);
        if (derivationMethodElement == null) {
            return;
        }
        processContentElement(oldName, newName, derivationMethodElement, XSDConstants.BASE_ATTRIBUTE);
    }

    // =========================================================
    // wsdl elements helpers
    // =========================================================

    private void setWsdlElementName(final WSDLElement wsdlElement, final String name) {
        if (ElementAttributeUtils.hasAttributeValue(wsdlElement.getElement(), WSDLConstants.NAME_ATTRIBUTE)) {
            wsdlElement.getElement().setAttribute(WSDLConstants.NAME_ATTRIBUTE, name);
        }
    }

    private String getWsdlElementName(final WSDLElement wsdlElement) {
        if (ElementAttributeUtils.hasAttributeValue(wsdlElement.getElement(), WSDLConstants.NAME_ATTRIBUTE)) {
            return wsdlElement.getElement().getAttribute(WSDLConstants.NAME_ATTRIBUTE);
        }
        return null;
    }

    // =========================================================
    // DOM update helpers
    // =========================================================

    private void processContentElement(final String oldName, final String newName, final Element contentElement,
            final String attributeName) {
        if (contentElement == null) {
            return;
        }
        if (ElementAttributeUtils.hasAttributeValue(contentElement, attributeName)) {
            final String qName = contentElement.getAttribute(attributeName);
            final String simpleName = qName.substring(qName.indexOf(':') + 1);
            if (simpleName.equals(oldName)) {
                updateElementName(contentElement, newName, attributeName);
            }
        }
    }

    private void updateElementName(final Element componentElement, final String newName, final String attributeName) {
        final String oldQName = componentElement.getAttribute(attributeName);
        String nsPrefix = UpdateNSPrefixUtils.instance().extractPrefixFromQName(oldQName);
        if (nsPrefix == null) {
            nsPrefix = ""; //$NON-NLS-1$
        } else {
            nsPrefix += ":"; //$NON-NLS-1$
        }
        componentElement.setAttribute(attributeName, nsPrefix + newName);
    }

}
