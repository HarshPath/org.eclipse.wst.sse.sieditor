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
 Checks equality of objects.
 
 * @param <T> the type of the objects to be compared
 */
public interface Equal<T>
{
	/**
	 * Returns whether <code>x1</code> and <code>x2</code> are equal. 
	 * @param x1 the object to compare with <code>x2</code>
	 * @param x2 the object to compare with <code>x1</code>
	 * @return whether <code>x1</code> and <code>x2</code> are equal
	 * @pre x1 != null
	 * @pre x2 != null
	 */
	boolean isEqual(T x1, T x2);
}
