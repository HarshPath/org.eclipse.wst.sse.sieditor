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
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.factory.WizardCreator;
import org.eclipse.wst.sse.sieditor.ui.wizard.SIFWizard;

public class WizardCreatorTest {

	private WizardCreator wizardCreator;

	@Before
	public void setUp() throws Exception {
		wizardCreator = new WizardCreator();
	}

	@After
	public void tearDown() throws Exception {
		wizardCreator = null;
	}

	@Test
	public void testWizardCreator() throws CoreException {
		Object creator = ((IExecutableExtensionFactory) wizardCreator).create();
		Assert.assertNotNull(creator);
		Assert.assertTrue(creator instanceof SIFWizard);
	}
}
