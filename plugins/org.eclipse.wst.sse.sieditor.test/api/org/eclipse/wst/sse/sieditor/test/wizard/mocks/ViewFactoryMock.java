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

import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;

public class ViewFactoryMock {
	
	private static ViewFactoryMock singletonInstance;
	private SIFWizardMock wizard=null;
	private SIFWizardPageMock page=null;
	
	private ViewFactoryMock(){
		
	}
	public static ViewFactoryMock getInstance(){
		if(null==singletonInstance)
			singletonInstance = new ViewFactoryMock();
		return singletonInstance;
	}

	
	public ISIFWizardView createSIFWizard(){
		if(wizard==null)
			wizard= new SIFWizardMock();
		return wizard;
		 
	}
	public ISIFWizardPageView createSIFWizardPage() {
		if(page==null)
			page= new SIFWizardPageMock();
		return page;
	}
}
