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
package org.eclipse.wst.sse.sieditor.ui.listeners;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;

public interface ISIFWizardPageListener extends IViewListener {

	/**
	 * Used to validate the ServiceInterface name
	 */
	void validateInterfaceName();

	/**
	 * Used to validate the Location where the WSDL would be created
	 */
	void validateSavedLocation();

	/**
	 * Used to validate the namspace of the WSDL
	 */
	void validateWsdlNamespace();

	/**
	 * Used to validate the namespace of a Schema
	 */
	void validateSchemaNamespace();

	/**
	 * Used to show the dialog for all projects, to select a save location
	 */
	void showDiloag();

	/**
	 * 
	 * @return - Returns true if all fields are filled and validated
	 */
	boolean canFinish();
	
	/**
	 * Validates if the page is complete and all entries are valid
	 */
	void validatePage();
}
