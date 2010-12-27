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
package org.eclipse.wst.sse.sieditor.ui.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;
import org.eclipse.wst.sse.sieditor.ui.presenters.SIFWizardPagePresenter;
import org.eclipse.wst.sse.sieditor.ui.presenters.SIFWizardPresenter;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

public class WizardCreator implements ISIFWizardPageListener,
		ISIFWizardListener, IExecutableExtensionFactory {

	private ISIFWizardView sifWizardView;
	private ISIFWizardPageView sifWizardPageView;
	private SIFWizardPagePresenter sifWizardPagePresenter;
	private SIFWizardPresenter sifWizardPresenter;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener#viewCreated(org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView)
	 */
	public void viewCreated(IView view) {
		if (view instanceof ISIFWizardView) {

			sifWizardPageView = WizardViewFactory.getInstance()
					.createSIFWizardPage(sifWizardView.getSelection());
			sifWizardView.doAddPage((ISIFWizardPageView) sifWizardPageView);
			sifWizardPageView.addViewListener(this);
			sifWizardPresenter = new SIFWizardPresenter(sifWizardView);

		}
		if (view instanceof ISIFWizardPageView) {

			sifWizardPagePresenter = new SIFWizardPagePresenter(
					sifWizardPageView);

		}

	}

	public void viewDispose(IView view) {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
	 */
	public Object create() throws CoreException {
		if (sifWizardView == null)
			sifWizardView = WizardViewFactory.getInstance().createSIFWizard();
		sifWizardView.addViewListener(this);
		return sifWizardView;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#validateInterfaceName()
	 */
	public void validateInterfaceName() {
		sifWizardPagePresenter.validateInterfaceName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#validateSavedLocation()
	 */
	public void validateSavedLocation() {
		sifWizardPagePresenter.validateSavedLocation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#validateSchemaNamespace()
	 */
	public void validateSchemaNamespace() {
		sifWizardPagePresenter.validateSchemaNamespace();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#validateWsdlNamespace()
	 */
	public void validateWsdlNamespace() {
		sifWizardPagePresenter.validateWsdlNamespace();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#showDiloag()
	 */
	public void showDiloag() {
		sifWizardPagePresenter.showDailog();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#canFinish()
	 */
	public boolean canFinish() {
		return sifWizardPagePresenter.canFinish();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener#validatePage()
	 */
	public void validatePage() {
		sifWizardPagePresenter.validatePage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener#performFinish()
	 */
	public boolean performFinish() {
		return sifWizardPresenter.performFinish();
	}

}
