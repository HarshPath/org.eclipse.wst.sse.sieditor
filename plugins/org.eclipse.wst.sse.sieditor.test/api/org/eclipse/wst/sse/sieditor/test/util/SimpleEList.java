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
package org.eclipse.wst.sse.sieditor.test.util;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;

@SuppressWarnings("serial")
public class SimpleEList<E> extends ArrayList<E> implements EList<E>{

	@Override
	public void move(int newPosition, E object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException();
	}

}
