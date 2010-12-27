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

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizard;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizardPage;

/**
 *
 * This class is used to create the Main wizard and its pages.
 */

public class WizardViewFactory {
	private static WizardViewFactory singletonInstance;
	private SIFWizard wizard=null;
	private SIFWizardPage page=null;
	private WizardViewFactory(){
		
	}
	public static WizardViewFactory getInstance(){
		if(null==singletonInstance)
			singletonInstance = new WizardViewFactory();
		return singletonInstance;
	}

	
	public ISIFWizardView createSIFWizard(){
		wizard= new SIFWizard();
		return wizard;
		 
	}
	public ISIFWizardPageView createSIFWizardPage(IStructuredSelection selection) {
		page= new SIFWizardPage(Messages.WIZARD_TITLE_XTIT,selection);
		return page;
	}

}