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
package org.eclipse.wst.sse.sieditor.ui.view;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;

public interface ISIFWizardPageView extends IView {

	/**
	 * 
	 * @return - Returns the Service Interface name entered by the User in the UI
	 */
	String getInterfaceName();

	/**
	 * 
	 * @return - Returns the Location where the file will be saved, entered by the User in the UI
	 */
	String getSavedLocation();

	/**
	 * 
	 * @return - Returns the WSDL Namespace entered by the User in the UI
	 */
	String getWsdlNamespace();

	/**
	 * 
	 * @return - Returns the Schema namespace entered by the User in the UI
	 */
	String getSchemaNamespace();

	/**
	 * Sets the given name to the widget in UI
	 * @param interfaceName
	 */
	void setInterfaceName(String interfaceName);

	/**
	 * Sets the given Location to the widget in UI
	 * @param saveLocation
	 */
	void setSavedLocation(String saveLocation);

	/**
	 * Sets the given namespace to the widget in UI
	 * @param wsdlNamespace
	 */
	void setWsdlNamespace(String wsdlNamespace);

	/**
	 * Sets the given namespace to the widget in UI
	 * @param schemaNamespace
	 */
	void setSchemaNamespace(String schemaNamespace);

	/**
	 * Sets the error message in the UI.
	 * @param message
	 */
	void updateStatus(String message);

	/**
	 * Gets the UI message
	 * @return
	 */
	String getStatus();

	/**
	 * Used to show the Types dialog which contains a list of all available types
	 */
	void showDiloag();

	void viewCreated();

	ISIFWizardPageListener getListener();

	/**
	 * Checks if there are any projects available
	 * @return
	 */
	boolean isProjectAvailable();
}
