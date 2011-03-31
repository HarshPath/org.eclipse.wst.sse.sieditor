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
package org.eclipse.wst.sse.sieditor.test.ui.v2.typeselect;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialogConfiguration;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentDescriptionProvider;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeSelectionDialogDelegate;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class TypeSelectionDialogDelegateTest {

	protected static class TypeSelectionDialogDelegateExposer extends
			TypeSelectionDialogDelegate {

		public TypeSelectionDialogDelegateExposer(IFile contextFile,
				XSDSchema[] schemas) {
			super(contextFile, schemas);
		}

		@Override
		public ComponentSearchListDialogConfiguration createDialogConfiguration(
				IComponentSearchListProvider iComponentSearchListProvider) {
			return super
					.createDialogConfiguration(iComponentSearchListProvider);
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.wst.sse.sieditor.ui.v2.typeselect.TypeSelectionDialogDelegate}
	 * {@link TypeSelectionDialogDelegate#createDialogConfiguration()}
	 */
	@Test
	public final void testCreateDialogConfiguration() {
		TypeSelectionDialogDelegateExposer delegate = new TypeSelectionDialogDelegateExposer(
				createNiceMock(IFile.class), new XSDSchema[0]);
		ComponentSearchListDialogConfiguration configuration = delegate
				.createDialogConfiguration(createNiceMock(IComponentSearchListProvider.class));
		IComponentDescriptionProvider descriptionProvider = configuration
				.getDescriptionProvider();
		String qualifier = descriptionProvider
				.getQualifier(createNiceMock(XSDNamedComponent.class));
		assertNotNull(qualifier);
		assertEquals(Messages.TypeSelectionDialogDelegate_NullNamespaceText,
				qualifier);
	}
}
