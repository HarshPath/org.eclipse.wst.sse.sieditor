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
 Thrown if something is illegally nil.
 */
public class Nil extends Precondition
{
	/**
	 * Throws a {@link Nil} if the argument <code>x</code> is nil.
	 * @param x the object to check for being nil
	 * @param arg the name of <code>c</code>
	 * @throws Nil if <code>x == null</code>
	 */
	public static void checkNil(final Object x, final String arg){
		if (x == null){
			if (arg == null){
				throw new Nil();				
			}
			throw new Nil(arg + " is nil"); //$NON-NLS-1$
		}
	}	
	
	/**
	 * Checks whether one of the supplied objects is nil.
	 * @param source one of the objects to check
	 * @param target one of the objects to check
	 * @throws Nil if <code>source == null || target == null</code>
	 */
	public static void checkNil(final Object source, final Object target){
		checkNil(source, "source"); //$NON-NLS-1$
		checkNil(target, "target"); //$NON-NLS-1$
	}

	/**
	 * Creates an instance containing the given message.
	 * @param message the message
	 */
	public Nil(final String message){
		super(message);
	}
	
	/**
	 * Creates an instance without a message.
	 */
	public Nil(){
		super();
	}
}
