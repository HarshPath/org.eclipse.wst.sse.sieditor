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
 *    Jakob Spies - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.core.common;

/**
 Thrown in case of a precondition violation. 
 */
public class Precondition extends IllegalArgumentException
{
	/**
	 * Creates an instance containing the given message.
	 * @param message the message
	 */
	public Precondition(final String message){
		super(message);
	}
	
	/**
	 * Creates an instance containing the given original exception.
	 * @param message the original exception
	 */
	public Precondition(final Throwable message){
		super(message);
	}
	
	/**
	 * Creates an instance without a message.
	 */
	public Precondition(){
		super();
	}	
}
