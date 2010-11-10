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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.validation.impl;

import java.util.Set;

import org.eclipse.wst.sse.sieditor.model.validation.IValidationEvent;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;

public class ValidationEvent implements IValidationEvent {
	
	private final IValidationStatusProvider statusProvider;
	private final Set<Object> modelObjects;
	private final int mode;

	public ValidationEvent(IValidationStatusProvider statusProvider, Set<Object> modelObjects, int mode) {
		this.statusProvider = statusProvider;
		this.modelObjects = modelObjects;
		this.mode = mode;
	}

	public Set<Object> getObjects() {
		return modelObjects;
	}

	public IValidationStatusProvider getStatusProvider() {
		return statusProvider;
	}

	public int getValidationMode() {
		return mode;
	}

}
