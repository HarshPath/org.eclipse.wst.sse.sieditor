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

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.ISIFWizardPageListener;
import org.eclipse.wst.sse.sieditor.ui.presenters.SIFWizardPagePresenter;
import org.eclipse.wst.sse.sieditor.ui.presenters.SIFWizardPresenter;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

public class WizardCreatorMock implements ISIFWizardPageListener,ISIFWizardListener {
	
	private ISIFWizardView sifWizardView;
	private ISIFWizardPageView sifWizardPageView;
	private SIFWizardPagePresenter sifWizardPagePresenter;
	private SIFWizardPresenter sifWizardPresenter;

	public void viewCreated(IView view) {
		// TODO Auto-generated method stub
		if (view instanceof ISIFWizardView) {
			sifWizardPageView = ViewFactoryMock.getInstance()
					.createSIFWizardPage();
			sifWizardView.doAddPage((ISIFWizardPageView) sifWizardPageView);
			sifWizardPageView.addViewListener(this);
			sifWizardPresenter = new SIFWizardPresenter(sifWizardView);
			sifWizardPageView.viewCreated();

		}
		if (view instanceof ISIFWizardPageView) {

			sifWizardPagePresenter = new SIFWizardPagePresenter(
					sifWizardPageView);

		}

	}

	public void viewDispose(IView view) {

	}

	public Object create() {
		if (sifWizardView == null)
			sifWizardView = ViewFactoryMock.getInstance().createSIFWizard();
		sifWizardView.addViewListener(this);
		sifWizardView.viewCreated();
		return sifWizardView;
	}

	public void validateInterfaceName() {
		sifWizardPagePresenter.validateInterfaceName();
	}

	public void validateSavedLocation() {
		sifWizardPagePresenter.validateSavedLocation();
	}

	public void validateSchemaNamespace() {
		sifWizardPagePresenter.validateSchemaNamespace();
	}

	public void validateWsdlNamespace() {
		sifWizardPagePresenter.validateWsdlNamespace();
	}

	public void showDiloag() {
		sifWizardPagePresenter.showDailog();
	}

	public boolean canFinish() {
		return sifWizardPagePresenter.canFinish();
	}

	public void validatePage() {
		sifWizardPagePresenter.validatePage();
	}

	public boolean performFinish() {
		return sifWizardPresenter.performFinish();
	}
}
