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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.ViewDelegate;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

public class SIFWizardMock implements ISIFWizardView {
	// private ServiceIntefacewizardPage page;
	private ViewDelegate viewDelegate;
	private ISIFWizardPageView view;
	private ISIFWizardListener listener;

	public SIFWizardMock() {
		viewDelegate = new ViewDelegate(this);

	}

	public boolean performFinish() {
		return listener.performFinish();
	}

	public void addPages() {
		viewDelegate.notifyViewCreated();

	}

	public boolean addViewListener(IViewListener viewListener) {
		if (viewListener instanceof ISIFWizardListener)
			this.listener = (ISIFWizardListener) viewListener;
		return viewDelegate.addViewListener(viewListener);
	}

	public Composite getUIHost() {
		return null;
	}

	public boolean removeViewListener(IViewListener viewListener) {
		return viewDelegate.removeViewListener(viewListener);
	}

	public void doAddPage(ISIFWizardPageView page) {
			view = (ISIFWizardPageView) page;
	}

	public IStructuredSelection getSelection() {
		return null;
	}

	public void viewCreated() {
		viewDelegate.notifyViewCreated();
	}

	public String getInterfaceName() {
		return view.getInterfaceName();
	}

	public String getSavedLocation() {
		return view.getSavedLocation();
	}

	public String getWsdlNamespace() {
		return view.getWsdlNamespace();
	}

	public void showEditorUnavailable(PartInitException e) {

	}

	public void showErrorMessage() {

	}

	public ISIFWizardListener getListener() {
		return listener;
	}

	public String getSchemaNamespace() {
		return view.getSchemaNamespace();
	}

	public void openSIEditor(IFile file) {
		// do nothing.
	}
}
