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
package org.eclipse.wst.sse.sieditor.utils;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import org.eclipse.wst.sse.sieditor.core.common.Nil;

/**
 * Eclipse Platform Plugin Utilities
 * 
 *
 */
public class PlatformUtils {
	
	public static IExtensionPoint getExtensionPoint(final String extensionID){
		Nil.checkNil(extensionID, "extensionID"); //$NON-NLS-1$
		return Platform.getExtensionRegistry().getExtensionPoint(extensionID);
	}

	public static IExtension[] getAllExtensions(final String extensionID){
		Nil.checkNil(extensionID, "extensionID"); //$NON-NLS-1$
		IExtensionPoint extensionPt = getExtensionPoint(extensionID);
		return extensionPt.getExtensions();
	}
	
	/**
	 * Returns the <B>Platform Adapter</B> for given object and class
	 * If the returned <B>Platform Adapter</B> is not of type <code>adapterClass</code>
	 * then this method return null
	 * @param <T>
	 * @param adaptableObject
	 * @param adapterClass
	 * @return Object of type T
	 */
	public static <T> T getAdapter(Object adaptableObject, Class<T> adapterClass){
		Nil.checkNil(adaptableObject, "adaptableObject"); //$NON-NLS-1$
		Nil.checkNil(adapterClass, "adapterClass"); //$NON-NLS-1$
		final Object adapter = Platform.getAdapterManager().getAdapter(adaptableObject, adapterClass);
		if(adapterClass.isInstance(adapter))
			return (T)adapter;
		return null;
	}
}