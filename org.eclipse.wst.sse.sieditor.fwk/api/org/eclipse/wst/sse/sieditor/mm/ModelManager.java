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
package org.eclipse.wst.sse.sieditor.mm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.fwk.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelExtension;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.write.api.IWritable;
import org.eclipse.wst.sse.sieditor.utils.PlatformUtils;

/**
 * Provides {@link IModelRoot} for a particular source
 * 
 *
 */
public class ModelManager {
	
	private static ModelManager _instance;
	
	private static final String MODEL_PROVIDER_EXTN_ID = "org.eclipse.wst.sse.sieditor.fwk.modelProvider"; //$NON-NLS-1$
	
	//private static final String EXTN_PROVIDER_EXTN_ID = "org.eclipse.wst.sse.sieditor.fwk.extnProvider"; //$NON-NLS-1$
	
	private static final String MODEL_PROVIDER_ELEMENT = "modelProvider"; //$NON-NLS-1$
	
	//private static final String EXTENSION_PROVIDER_ELEMENT = "extnProvider"; //$NON-NLS-1$
	
	private static final String PROVIDER_ATTRIBUTE = "providerClass"; //$NON-NLS-1$
	
	private HashSet<IModelProvider> _modelProviders;
	
	//Singleton
	private ModelManager(){
		initialize();
	}
	
	//Initializes and loads all the modelProviders and ExtensionProviders
	private void initialize(){
		if (Logger.isDebugEnabled()) {
			Logger.getDebugTrace().traceEntry(""); //$NON-NLS-1$
		}
		
		if(null != _modelProviders)
			return;
		
		//Load modelProvider extensions
		_modelProviders = new HashSet<IModelProvider>(1);
		
		IExtension[] extensions = PlatformUtils.getAllExtensions(MODEL_PROVIDER_EXTN_ID);
		IConfigurationElement[] elements = null;
		for (IExtension extension : extensions) {
			elements = extension.getConfigurationElements();
			List<IConfigurationElement> configurationElements = CollectionTypeUtils.find(Arrays.asList(elements), new Condition<IConfigurationElement>(){

				public boolean isSatisfied(IConfigurationElement in) {
					return MODEL_PROVIDER_ELEMENT.equals(in.getName())? true : false;
				}
				
			});
                        IConfigurationElement element = configurationElements.isEmpty() ? null : configurationElements.get(0);
			if(null != element){
				try {
					Object obj = element.createExecutableExtension(PROVIDER_ATTRIBUTE);
					if(obj instanceof IModelProvider){
						final IModelProvider modelProvider = (IModelProvider)obj;
						_modelProviders.add(modelProvider);
					}
				} catch (CoreException e) {
					//Just ignore if some ModelProvider could not be instantiated
					Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, 
							"Can not create executable extension for Model Provider. Element name=" +  //$NON-NLS-1$
							element.getName() +
							", value=" + element.getValue(), e); //$NON-NLS-1$
				}
			}
		}
		
		if (Logger.isDebugEnabled()) {
			Logger.getDebugTrace().traceExit(""); //$NON-NLS-1$
		}
	}
	
	public <T extends IModelExtension> T getModelExtension(IModelObject object, Class<T> extensionClass){
		T extension = null;
		for(IModelProvider provider : _modelProviders){
			if(provider.supports(object)){
				extension = provider.getModleExtension(object, extensionClass);
				if(null != extension)
					break;
			}
		}
		return extension;
	}
	
	public IWritable getWriteSupport(final IModelObject object){
		IWritable extension = null;
		for(IModelProvider provider : _modelProviders){
			if(provider.supports(object)){
				extension = provider.getWriteObject(object);
				if(null != extension)
					break;
			}
		}
		return extension;
	}
	
	public IWsdlModelRoot getWsdlModelRoot(final Object source){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		return PlatformUtils.getAdapter(source, IWsdlModelRoot.class);
	}
	
	public IXSDModelRoot getXSDModelRoot(final Object source){
		Nil.checkNil(source, "source"); //$NON-NLS-1$
		return PlatformUtils.getAdapter(source, IXSDModelRoot.class);
	}
	
	/**
	 * Returns an instance of {@link ModelManager}
	 * @return {@link ModelManager}
	 */
	public static ModelManager getInstance(){
		if(null != _instance)
			return _instance;
		synchronized (ModelManager.class) {
			if(null != _instance)
				return _instance;
			_instance = new ModelManager();
		}
		return _instance;
	}
	
}