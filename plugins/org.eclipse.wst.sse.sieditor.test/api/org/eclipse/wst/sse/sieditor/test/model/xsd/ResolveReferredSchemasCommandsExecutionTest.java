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
package org.eclipse.wst.sse.sieditor.test.model.xsd;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class ResolveReferredSchemasCommandsExecutionTest extends SIEditorBaseTest{

	private final String DOCUMENTS_FOLDER = "pub/csns/resolveImportedSchemas/";
	private final String IMPORTED_DOC = "a.xsd";
	private final String IMPORTING_DOC = "aa.xsd";
	
	@Test
	public void testGetAllReferredSchemas() throws IOException, CoreException, ExecutionException {
		IXSDModelRoot importedModelRoot = getXSDModelRoot(DOCUMENTS_FOLDER+IMPORTED_DOC, IMPORTED_DOC);
		IType typeToImport = importedModelRoot.getSchema().getAllContainedTypes().iterator().next();
		IXSDModelRoot xsdModelRoot = getXSDModelRoot(DOCUMENTS_FOLDER+IMPORTING_DOC, IMPORTING_DOC);
		
		IEnvironment env = xsdModelRoot.getEnv();
		IOperationHistory operationHistory = env.getOperationHistory();
		IUndoableOperation[] undoHistory = operationHistory.getUndoHistory(env.getUndoContext());
		assertEquals(0,undoHistory.length);
		
		ImportSchemaCommand importSchemaCommand = new ImportSchemaCommand(xsdModelRoot, xsdModelRoot.getSchema(), (AbstractType) typeToImport);
		importSchemaCommand.execute(new NullProgressMonitor(), null);
		xsdModelRoot.getSchema().getAllReferredSchemas();
		
		undoHistory = operationHistory.getUndoHistory(env.getUndoContext());
		assertEquals(0,undoHistory.length);
		
	}

}
