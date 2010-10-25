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
package org.eclipse.wst.sse.sieditor.model.api;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;


/**
 * Model Root for getting the XSD Model and all the project types available
 * for use in the model
 * 
 *
 */
public interface IXSDModelRoot extends IModelRoot {
	
	ISchema getSchema();
}
