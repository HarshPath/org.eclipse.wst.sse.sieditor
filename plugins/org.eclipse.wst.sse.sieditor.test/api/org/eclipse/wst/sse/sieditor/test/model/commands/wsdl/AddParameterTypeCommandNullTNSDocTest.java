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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * This test checks the proper addition/removal of the xmlns="" attribute when
 * needed.
 * 
 *
 * 
 */
public class AddParameterTypeCommandNullTNSDocTest extends AbstractCommandTest {

	private static final String MY_PARAMETER_NAME = "justAddedParam";

	@Override
	protected void assertPostRedoState(final IStatus redoStatus,
			final IWsdlModelRoot modelRoot) {

		final IDescription description = modelRoot.getDescription();
		// DOM CHECK
		assertTrue(description.getComponent().getElement()
				.hasAttribute("xmlns"));
		assertEquals("", description.getComponent().getElement().getAttribute(
				"xmlns"));
	}

	@Override
	protected void assertPostUndoState(final IStatus undoStatus,
			final IWsdlModelRoot modelRoot) {
		final IDescription description = modelRoot.getDescription();
		// DOM CHECK
		assertFalse(description.getComponent().getElement().hasAttribute(
				"xmlns"));
	}

	@Override
	protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot)
			throws Exception {
		final IDescription description = modelRoot.getDescription();
		final IServiceInterface intf = description.getAllInterfaces()
				.iterator().next();
		final ServiceOperation operation = (ServiceOperation) intf
				.getAllOperations().iterator().next();

		assertFalse(description.getComponent().getElement().hasAttribute(
				"xmlns"));

		return new AddInParameterCommand(modelRoot, operation,
				MY_PARAMETER_NAME);
	}

	private static final String WSDL_FILE = "NewWSDLFile.wsdl";
	private static final String WSDL_FILE_PATH = "pub/csns/nullTNS/";

	@Override
	protected String getWsdlFoldername() {
		return WSDL_FILE_PATH;
	}

	@Override
	protected String getWsdlFilename() {
		return WSDL_FILE;
	}
}
