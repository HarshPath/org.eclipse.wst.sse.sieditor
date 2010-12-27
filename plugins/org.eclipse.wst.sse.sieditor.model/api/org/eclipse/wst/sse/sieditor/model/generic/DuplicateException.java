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
package org.eclipse.wst.sse.sieditor.model.generic;

/**
 * Thrown if a component of same type already exists and if its being added again
 */
public class DuplicateException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance with a given message
	 * @param msg
	 * 			message to be displayed
	 */
	public DuplicateException(final String msg){
		super(msg);
	}
	

}