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
package org.eclipse.wst.sse.sieditor.model.api;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.WSDLElement;

/**
 * A generic parent class for all the model classes
 * 
 *
 */
public interface IModelObject {

	/**
	 * Returns parent of the Object
	 * @return parent - {@link IModelObject}
	 */
	IModelObject getParent();
	
	/**
	 * Returns the description of the Object
	 * @return description
	 */
	String getDocumentation();
	
	/**
	 * Returns the root Container for the current {@link IModelObject}
	 * @return root - {@link IModelObject}
	 */
	IModelObject getRoot();
	/**
	 * Returns the underlying emf model object {@link EObject}
	 * @return component
	 */
	public abstract EObject getComponent();
	
	public abstract IModelRoot getModelRoot();
}
