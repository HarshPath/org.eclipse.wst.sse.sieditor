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
package org.eclipse.wst.sse.sieditor.test.ui;

import static org.junit.Assert.*;

import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestServiceInterfaceEditor {

	@Test
	public void testSIE_DisposesWithoutException() {
		ServiceInterfaceEditor sie = new ServiceInterfaceEditor();
		try {
			sie.dispose();
		} catch (NullPointerException npe) {
			fail("ServiceInterfaceEditor should not throw NPE in dispose() method. " +
					"Note that ESR editor does not use createModelRoot() method.");
		}
	}
}
