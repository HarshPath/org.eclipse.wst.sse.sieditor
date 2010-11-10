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
package org.eclipse.wst.sse.sieditor.ui.preedit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class PreEditService {

	private static final String EXTENSION_POINT = ".esmSIEditorPreEditListener"; //$NON-NLS-1$
	private static final String EXTENSION_POINT_ATTRIBUTE = "listener"; //$NON-NLS-1$
	private List<ISIEditorPreEditListener> preEditListeners = new ArrayList<ISIEditorPreEditListener>();
	
	private static PreEditService singletonInstance;

	private PreEditService(){
		
	}
	public static PreEditService getInstance(){
		if(null==singletonInstance)
			singletonInstance = new PreEditService();
		return singletonInstance;
	}	

	
	public boolean startEdit(Object editedObject){
		//return true; 
		//TODO enable the DTR checkout logic after testing it 
		return informListenersOfEdit(editedObject);
	}

	
	public boolean startDelete(Object deletedObject){
		//return true;
		//TODO enable the DTR checkout logic after testing it
		return informListenersOfDelete(deletedObject);
	}
	
	private boolean informListenersOfEdit(Object editedObject){
		boolean result=true; //if there are no listeners the result should be true
		for(ISIEditorPreEditListener listener: getPreEditListeners()){
			result = result && listener.startObjectEdit(editedObject);
			if(!result)
				return result;
		}
		return result;
	}
	
	private boolean informListenersOfDelete(Object deletedObject){
		boolean result=true; //if there are no listeners the result should be true
		for(ISIEditorPreEditListener listener: getPreEditListeners()){
			result = result && listener.startObjectDelete(deletedObject);
			if(!result)
				return result;
		}
		return result;
	}

	
	private List<ISIEditorPreEditListener> getExecutableExtension(
			String attributeName) {
		List<ISIEditorPreEditListener> extensions = new ArrayList<ISIEditorPreEditListener>();
		IExtensionPoint point = Platform.getExtensionRegistry()
						.getExtensionPoint(
								Activator.getDefault().getBundle().getSymbolicName()
									+ EXTENSION_POINT);
		if (null!=point) {
			for (IExtension extension : point.getExtensions()) {
				for (IConfigurationElement config : extension
						.getConfigurationElements()) {
					try {
						if (null!=config.getAttribute(attributeName)) {
							extensions.add((ISIEditorPreEditListener) config
									.createExecutableExtension(attributeName));
						}
					} catch (Exception e) {
						Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
								"Can not create executable extension for " + EXTENSION_POINT +  //$NON-NLS-1$
								" Element name=" + config.getName() + //$NON-NLS-1$
								", value=" + config.getValue(), e); //$NON-NLS-1$
					}
				}
			}
		}
		return extensions;
	}

	public List<ISIEditorPreEditListener> getPreEditListeners() {
		if(preEditListeners.isEmpty()) {
			List<ISIEditorPreEditListener> listeners = getExecutableExtension(EXTENSION_POINT_ATTRIBUTE);
			for(ISIEditorPreEditListener listener: listeners) {
				preEditListeners.add(listener);
			}
		}
		return preEditListeners;
	}

}
