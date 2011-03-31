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
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class ChangeToAsynchronousOperationTypeNoMessageTest extends ChangeToAsynchronousOperationTypeNoInputTest{

	@Override
	protected void assertPostUndoState(IStatus undoStatus, IWsdlModelRoot modelRoot) {
		Operation wsdlOperation = (Operation) operation.getComponent();
		
        assertNotNull(wsdlOperation.getEInput());
        assertNull(wsdlOperation.getEInput().getEMessage());
        assertNotNull(wsdlOperation.getEOutput());
        assertNotNull(wsdlOperation.getEOutput().getMessage());
	}
	
	@Override
	protected String getWsdlFilename() {
		return "nomessage.wsdl";
	}
}
