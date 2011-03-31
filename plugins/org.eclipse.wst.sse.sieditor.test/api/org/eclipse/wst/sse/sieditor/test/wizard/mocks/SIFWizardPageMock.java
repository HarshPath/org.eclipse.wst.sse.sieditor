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
package org.eclipse.wst.sse.sieditor.test.wizard.mocks;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.ViewDelegate;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;

public class SIFWizardPageMock implements ISIFWizardPageView {

	private String interfaceName;
	private String savedLocation;
	private String wsdlNamespace;
	private String schemaNamespace;
	private ViewDelegate viewDelegate;
	private String message;
	private ISIFWizardPageListener listener;

	public SIFWizardPageMock() {
		viewDelegate = new ViewDelegate(this);
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getSavedLocation() {
		return savedLocation;
	}

	public String getSchemaNamespace() {
		return schemaNamespace;
	}

	public String getStatus() {
		return message;
	}

	public String getWsdlNamespace() {
		return wsdlNamespace;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setSavedLocation(String saveLocation) {
		this.savedLocation = saveLocation;
	}

	public void setSchemaNamespace(String schemaNamespace) {
		this.schemaNamespace = schemaNamespace;
	}

	public void setWsdlNamespace(String wsdlNamespace) {
		this.wsdlNamespace = wsdlNamespace;
	}

	public void showDiloag() {

	}

	public void updateStatus(String message) {
		this.message = message;
	}

	public boolean addViewListener(IViewListener viewListener) {
		if (viewListener instanceof ISIFWizardPageListener)
			this.listener = (ISIFWizardPageListener) viewListener;
		return viewDelegate.addViewListener(viewListener);

	}

	public Composite getUIHost() {
		return null;
	}

	public boolean removeViewListener(IViewListener viewListener) {
		return viewDelegate.removeViewListener(viewListener);
	}

	public ISIFWizardPageListener getListener() {
		return listener;
	}

	public void viewCreated() {
		viewDelegate.notifyViewCreated();
	}

	public boolean isProjectAvailable() {
		return true;
	}

}
