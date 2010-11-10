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
package org.eclipse.wst.sse.sieditor.core.editorfwk;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;

import org.eclipse.wst.sse.sieditor.core.SIEditorCoreActivator;
import org.eclipse.wst.sse.sieditor.core.common.Logger;

public class ExtensibleObjectFactoryRegistry {
	private static Map<String, ExtensibleObjectFactory> objectFactoryMap;

	// more flexible, maybe not needed
	public static ExtensibleObjectFactory get(URI uri) {
		String lastSegment = uri.lastSegment();
		if (objectFactoryMap == null) {
			initialize();
		}
		for (String extension : objectFactoryMap.keySet()) {
			if (lastSegment.endsWith("." + extension)) { //$NON-NLS-1$
				return objectFactoryMap.get(extension);
			}

		}
		return null;
	}

	public static ExtensibleObjectFactory get(String extension) {
		if (objectFactoryMap == null) {
			initialize();
		}
		return objectFactoryMap.get(extension);
	}

	private static void initialize() {
		objectFactoryMap = new HashMap<String, ExtensibleObjectFactory>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				"org.eclipse.wst.sse.sieditor.core.objectDefinition"); //$NON-NLS-1$
        IExtension[] allExtensions = extensionPoint.getExtensions();
        for (int i = 0; i < allExtensions.length; i++)
        {
        	String extension = null;
        	ExtensibleObjectFactory factory = null;
            IConfigurationElement[] allConfigElements = allExtensions[i].getConfigurationElements();
            for (int j = 0; j < allConfigElements.length; j++)
            {
                IConfigurationElement element = allConfigElements[j];
                if ( element.getName().equals("factory")) { //$NON-NLS-1$
                	try {
						factory = (ExtensibleObjectFactory)element.createExecutableExtension("class"); //$NON-NLS-1$
					} catch (CoreException e) {
						Logger.log(SIEditorCoreActivator.PLUGIN_ID, IStatus.ERROR, 
								"Can not create executable extension for element name=" +  //$NON-NLS-1$
								element.getName() +
								", value=" + element.getValue(), e); //$NON-NLS-1$
					}
                }
                if ( element.getName().equals("objectDefinition")) { //$NON-NLS-1$
                	extension = element.getAttribute("extension"); //$NON-NLS-1$
                }
            }
            if ( extension != null && factory != null ) {
            	objectFactoryMap.put(extension, factory);
            }
        }
  
	}
}
