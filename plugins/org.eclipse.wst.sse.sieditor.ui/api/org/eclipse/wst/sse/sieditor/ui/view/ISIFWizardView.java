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
package org.eclipse.wst.sse.sieditor.ui.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;

public interface ISIFWizardView extends IView {
	
	/**
	 * Method used to add pages to the wizard
	 * @param page
	 */
	void doAddPage(ISIFWizardPageView page);

	/**
	 * Called when the Wizard is created
	 */
	void viewCreated();

	/**
	 * Selection used to create Wizard Page
	 * @return
	 */
	IStructuredSelection getSelection();

	/**
	 * Shows the error messages
	 */
	void showErrorMessage();

	/**
	 * Shows the error message when SI Editor is not available
	 * @param e
	 */
	void showEditorUnavailable(PartInitException e);

	/**
	 * @return String - returns the location where the new WSDL file will be saved
	 */
	String getSavedLocation();

	/**
	 * @return String - returns the name of the Service Interface of the WSDL to be created
	 */
	String getInterfaceName();

	/**
	 * 
	 * @return String - returns the Namespace for the WSDL
	 */
	String getWsdlNamespace();
	
	/**
	 * 
	 * @return - Returns the listener which listens to the events in UI
	 */
	ISIFWizardListener getListener();

	/**
	 * 
	 * @return String - returns the Namespace for the Schema in the WSDL to be created
	 */
	String getSchemaNamespace();
	
	/**
	 * Method used to open the given file in SIEditor after the Finish button is pressed
	 * @param file
	 */
	void openSIEditor(IFile file);
}
