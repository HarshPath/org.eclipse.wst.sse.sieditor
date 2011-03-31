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
package org.eclipse.wst.sse.sieditor.test.wizard;

import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.factory.WizardViewFactory;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardPageView;
import org.eclipse.wst.sse.sieditor.ui.view.ISIFWizardView;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizard;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizardPage;

public class WizardViewFactoryTest{

	@Test
	public void testWizardViewFactory() throws CoreException {
		WizardViewFactory factory= WizardViewFactory.getInstance(); 
		Assert.assertNotNull(factory);
		Assert.assertNotNull(WizardViewFactory.getInstance());
		ISIFWizardView createSIFWizard = factory.createSIFWizard();
		Assert.assertNotNull(createSIFWizard);
		Assert.assertTrue(createSIFWizard instanceof SIFWizard);
		ISIFWizardPageView sifWizardPage = factory.createSIFWizardPage(null);
		Assert.assertNotNull(sifWizardPage);
		Assert.assertTrue(sifWizardPage instanceof SIFWizardPage);
	}
}
