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
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetParameterTypeImportFromNullTNSSchemaCommandTest extends
		SIEditorBaseTest {
	private static final String PARAMETER_NAME = "param1";
	private static final String WSDL_FILE = "NewWsdl1.wsdl";
	private static final String WSDL_FILE_PATH = DATA_PUBLIC_SELF_MIX2_REL_PATH
			+ WSDL_FILE;
	private static final String XSD_FILE = "NewXMLSchemaNoTNS.xsd";
	private static final String XSD_FILE_PATH = "pub/csns/nullTNS/" + XSD_FILE;

	@Test
	public void testOperationFailureOfExecution() throws Exception {
		IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(WSDL_FILE_PATH,
				WSDL_FILE);
		IDescription description = wsdlModelRoot.getDescription();
		IXSDModelRoot xsdModelRoot = getXSDModelRoot(XSD_FILE_PATH, XSD_FILE);
		IType type = xsdModelRoot.getSchema().getType(true, "Element1UN");

		IOperation firstOperation = description.getAllInterfaces().iterator()
				.next().getAllOperations().iterator().next();
		assertEquals(Status.OK_STATUS, wsdlModelRoot.getEnv().execute(
				new AddInParameterCommand(wsdlModelRoot, firstOperation,
						PARAMETER_NAME)));
		IParameter parameter = firstOperation.getInputParameter(PARAMETER_NAME)
				.get(0);
        SetParameterTypeCommand setParameterTypeCommand = new SetParameterTypeCommand(parameter, type);
		IStatus status = wsdlModelRoot.getEnv()
				.execute(setParameterTypeCommand);
		assertEquals(Status.ERROR, status.getSeverity());
	}

}
