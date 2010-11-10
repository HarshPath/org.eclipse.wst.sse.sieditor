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
 * A condition on an object.
 * 
 * @param <T> the type of objects for which the {@link Condition} can hold
 */
public interface Condition<T>
{
	/**
	 * Returns whether this <code>Condition</code> holds for the given {@link Object}.
	 * @param x the {@link Object} to check
	 * @return whether this <code>Condition</code> holds for <code>x</code>
	 */
	boolean isSatisfied(T x);
}
