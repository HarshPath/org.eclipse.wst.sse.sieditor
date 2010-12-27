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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.wsdl.WSDLPackage;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

/**
 * A problem decorator holds bindings of type EMF structural feature ID -> UI
 * Control. These bindings define which UI control is responsible for which EMF
 * structural feature. The problem decorator has to have {@link IModelObject}
 * set.<br>
 * For example let's have two UI controls to show the name and the type of a
 * Parameter. In this case we should create a problem decorator with the
 * parameter object set as model object. The two UI controls should be bound to
 * the corresponding EMF features {@link WSDLPackage#PART__NAME} and
 * {@link WSDLPackage#PART__TYPE_DEFINITION}. When we call updateDecorations
 * method the ProblemDecorator will query the validation results for the
 * corresponding features and decorate the corresponding UI controls
 * 
 */
public interface IProblemDecorator {

    /**
     * Binds a UI control to an EMF feature ID. This means that the UI control
     * is responsible to show problems in an EMF feature with the specified ID.
     * 
     * @param featureId
     *            the ID of the EMF feature
     * @param control
     *            the UI control responsible to show problems for the specified
     *            feature
     */
    void bind(EStructuralFeature feature, IProblemDecoratableControl control);

    /**
     * Sets the model object. It is the instance, which validation results are
     * queried for problems.
     * 
     * @param modelObject
     */
    void setModelObject(IModelObject modelObject);

    /**
     * Updates the decorations of all UI controls, which have been bound to some
     * EMF feature. Showing a decoration for certain UI control means that for
     * this decorator's model object exists a validation result, which contains
     * the EMF feature (corresponding to the UI control ) in the result locus.
     */
    void updateDecorations();
    
    boolean decorationNeedsUpdate();

}