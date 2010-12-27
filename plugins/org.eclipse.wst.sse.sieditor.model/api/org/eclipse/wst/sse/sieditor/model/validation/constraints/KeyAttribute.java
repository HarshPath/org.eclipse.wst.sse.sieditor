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
package org.eclipse.wst.sse.sieditor.model.validation.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Service;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class KeyAttribute extends AbstractConstraint {

    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        ConstraintData[] constraintData = getConstraintData(ctx.getTarget());
        List<IStatus> statusList = new ArrayList<IStatus>();
        for (ConstraintData data : constraintData) {
            statusList.addAll(data.validate(ctx));
        }

        return createStatus(ctx, statusList);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        EObject target = ctx.getTarget();
        if (target instanceof Definition) {
            return isBatchValidation(ctx);
        }
        return isLiveValidation(ctx);
    }

    private ConstraintData[] getConstraintData(EObject target) {
        WSDLElement element = (WSDLElement) target;
        if (element instanceof Definition) {
            return new ConstraintData[] {
                    new ConstraintData(element, WSDLPackage.DEFINITION__EMESSAGES, WSDLPackage.Literals.MESSAGE__QNAME),
                    new ConstraintData(element, WSDLPackage.DEFINITION__EPORT_TYPES, WSDLPackage.Literals.PORT_TYPE__QNAME),
                    new ConstraintData(element, WSDLPackage.DEFINITION__EBINDINGS, WSDLPackage.Literals.BINDING__QNAME),
                    new ConstraintData(element, WSDLPackage.DEFINITION__ESERVICES, WSDLPackage.Literals.SERVICE__QNAME),
                    new ConstraintData(element, WSDLPackage.DEFINITION__EIMPORTS, WSDLPackage.Literals.IMPORT__NAMESPACE_URI) };
        } else if (element instanceof Message) {
            return new ConstraintData[] { new ConstraintData(element.getContainer(), WSDLPackage.DEFINITION__EMESSAGES,
                    WSDLPackage.Literals.MESSAGE__QNAME) };
        } else if (element instanceof PortType) {
            return new ConstraintData[] { new ConstraintData(element.getContainer(), WSDLPackage.DEFINITION__EPORT_TYPES,
                    WSDLPackage.Literals.PORT_TYPE__QNAME) };
        } else if (element instanceof Binding) {
            return new ConstraintData[] { new ConstraintData(element.getContainer(), WSDLPackage.DEFINITION__EBINDINGS,
                    WSDLPackage.Literals.BINDING__QNAME) };
        } else if (element instanceof Service) {
            return new ConstraintData[] { new ConstraintData(element.getContainer(), WSDLPackage.DEFINITION__ESERVICES,
                    WSDLPackage.Literals.SERVICE__QNAME) };
        } else if (element instanceof Import) {
            return new ConstraintData[] { new ConstraintData(element.getContainer(), WSDLPackage.DEFINITION__EIMPORTS,
                    WSDLPackage.Literals.IMPORT__NAMESPACE_URI) };
        }

        throw new IllegalArgumentException();
    }

    private static class ConstraintData {
        private WSDLElement parent;
        private EStructuralFeature collectionFeature;
        private EStructuralFeature keyFeature;

        public ConstraintData(WSDLElement parent, int collectionFeatureId, EStructuralFeature keyFeature) {
            this.parent = parent;
            collectionFeature = parent.eClass().getEStructuralFeature(collectionFeatureId);
            this.keyFeature = keyFeature;
        }

        @SuppressWarnings("unchecked")
        public List<IStatus> validate(IValidationContext ctx) {
            Set<Object> allKeys = new HashSet<Object>();
            EList<WSDLElement> collection = (EList<WSDLElement>) parent.eGet(collectionFeature);
            List<IStatus> statusList = new ArrayList<IStatus>(collection.size());
            
            // the location of the error will be the name
            Collection<EObject> locus = new ArrayList<EObject>();
            locus.add(this.keyFeature);

            final Set<Object> duplicateKeys = new HashSet<Object>();

            for (WSDLElement element : collection) {
                Object key = element.eGet(keyFeature);
                IStatus status = null;
                if (allKeys.contains(key)) {
                    duplicateKeys.add(key);
                    status = ConstraintStatus.createStatus(ctx, element, locus, Messages.DuplicateKey, getKeyName(key), parent
                            .getElement().getLocalName(), element.getElement().getLocalName());
                } else {
                    allKeys.add(key);
                    status = ConstraintStatus.createSuccessStatus(ctx, element, null);
                }

                statusList.add(status);
            }

            // adds the first elements to the status list
            for (int i = 0; i < collection.size(); i++) {
                final WSDLElement element = collection.get(i);
                final Object key = element.eGet(keyFeature);
                if (duplicateKeys.contains(key)) {
                    duplicateKeys.remove(key);
                    ConstraintStatus errorStatus = ConstraintStatus.createStatus(ctx, element, locus, Messages.DuplicateKey,
                            getKeyName(key), parent.getElement().getLocalName(), element.getElement().getLocalName());
                    statusList.set(i, errorStatus);
                }
            }
            return statusList;

        }

        private static String getKeyName(Object key) {
            if (key instanceof QName) {
                return ((QName) key).getLocalPart();
            } else if (key instanceof String) {
                return (String) key;
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

}
