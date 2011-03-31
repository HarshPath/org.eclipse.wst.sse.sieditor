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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.internal.impl.WSDLFactoryImpl;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.impl.XSDFactoryImpl;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class ResolveImportedAndIncludedSchemasDefinitionsCommandsExecutionTest
		extends SIEditorBaseTest {

	private final String DOCUMENTS_FOLDER = "pub/csns/resolveImportedSchemas/";
	private final String IMPORTED_DOC = "a.xsd";
	private final String IMPORTING_DOC = "aaa.wsdl";

	@Test
	public void testWsdlImportCaseTest() throws IOException, CoreException,
			ExecutionException, URISyntaxException {
		final IXSDModelRoot importedModelRoot = getXSDModelRoot(
				DOCUMENTS_FOLDER + IMPORTED_DOC, IMPORTED_DOC);
		final ISchema importedSchema = importedModelRoot.getSchema();
		final IType typeToImport = importedSchema.getAllContainedTypes()
				.iterator().next();
		final IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(DOCUMENTS_FOLDER
				+ IMPORTING_DOC, IMPORTING_DOC);

		final IEnvironment env = wsdlModelRoot.getEnv();
		final IOperationHistory operationHistory = env.getOperationHistory();
		final IUndoableOperation[] oldUndoHistoryContent = operationHistory
				.getUndoHistory(env.getUndoContext());
		assertEquals(0, oldUndoHistoryContent.length);

		final IDescription description = wsdlModelRoot.getDescription();
		final AbstractNotificationOperation importOperation = new AbstractNotificationOperation(
				wsdlModelRoot, description,
				"test set up command adding a wsdl import") {
			@Override
			public IStatus run(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				final Import schemaImport = new WSDLFactoryImpl()
						.createImport();
				final Definition component = description.getComponent();
				schemaImport.setEnclosingDefinition(component);
				schemaImport.setESchema(importedSchema.getComponent());
				try {
					schemaImport.setLocationURI(URIHelper.createEncodedURI(
							typeToImport.getParent().getComponent()
									.getSchemaLocation()).toString());
				} catch (UnsupportedEncodingException e) {
					fail(e.toString());
				} catch (URISyntaxException e) {
					fail(e.toString());
				}
				component.addImport(schemaImport);
				component.updateElement();
				return Status.OK_STATUS;
			}
		};

		importOperation.execute(new NullProgressMonitor(), null);
		wsdlModelRoot.getDescription().getAllVisibleSchemas();

		IUndoableOperation[] newUndoHistoryContent = operationHistory
				.getUndoHistory(env.getUndoContext());
		assertEquals(0, newUndoHistoryContent.length);

	}

	@Test
	public void testXsdImportCaseTest() throws IOException, CoreException,
			ExecutionException, URISyntaxException {
		IXSDModelRoot importedModelRoot = getXSDModelRoot(DOCUMENTS_FOLDER
				+ IMPORTED_DOC, IMPORTED_DOC);
		IType typeToImport = importedModelRoot.getSchema()
				.getAllContainedTypes().iterator().next();
		IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(DOCUMENTS_FOLDER
				+ IMPORTING_DOC, IMPORTING_DOC);

		IEnvironment env = wsdlModelRoot.getEnv();
		IOperationHistory operationHistory = env.getOperationHistory();
		IUndoableOperation[] undoHistory = operationHistory.getUndoHistory(env
				.getUndoContext());
		assertEquals(0, undoHistory.length);

		IDescription description = wsdlModelRoot.getDescription();
		ISchema firstSchema = description.getContainedSchemas().iterator()
				.next();
		ImportSchemaCommand importSchemaCommand = new ImportSchemaCommand(
				wsdlModelRoot, description, URIHelper
						.createEncodedURI(description.getLocation()), URIHelper
						.createEncodedURI(typeToImport.getParent()
								.getComponent().getSchemaLocation()),
				(AbstractType) typeToImport, DocumentType.XSD_SHEMA);
		importSchemaCommand.execute(new NullProgressMonitor(), null);
		wsdlModelRoot.getDescription().getAllVisibleSchemas();

		undoHistory = operationHistory.getUndoHistory(env.getUndoContext());
		assertEquals(0, undoHistory.length);

	}

	@Test
	public void testXsdImportDomCaseTest() throws IOException, CoreException,
			ExecutionException, URISyntaxException {
		final IXSDModelRoot importedModelRoot = getXSDModelRoot(
				DOCUMENTS_FOLDER + IMPORTED_DOC, IMPORTED_DOC);
		final IType typeToImport = importedModelRoot.getSchema()
				.getAllContainedTypes().iterator().next();
		final IWsdlModelRoot wsdlModelRoot = getWSDLModelRoot(DOCUMENTS_FOLDER
				+ IMPORTING_DOC, IMPORTING_DOC);

		final IEnvironment env = wsdlModelRoot.getEnv();
		final IOperationHistory operationHistory = env.getOperationHistory();
		final IUndoableOperation[] oldUndoHistory = operationHistory
				.getUndoHistory(env.getUndoContext());
		assertEquals(0, oldUndoHistory.length);

		final IDescription description = wsdlModelRoot.getDescription();
		final ISchema firstSchema = description.getContainedSchemas()
				.iterator().next();
		AbstractNotificationOperation command = new AbstractNotificationOperation(
				wsdlModelRoot, description,
				"adds an import element to a schema but does'nt resolve it.") {
			@Override
			public IStatus run(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				XSDImport importElement = XSDFactoryImpl.eINSTANCE
						.createXSDImport();
				importElement.setNamespace("A");
				importElement.setSchemaLocation(IMPORTED_DOC);
				firstSchema.getComponent().getContents().add(0,importElement);
				firstSchema.getComponent().updateElement();
				return Status.OK_STATUS;
			}
		};
		command.execute(new NullProgressMonitor(), null);
		wsdlModelRoot.getDescription().getAllVisibleSchemas();

		final IUndoableOperation[] newUndoHistory = operationHistory
				.getUndoHistory(env.getUndoContext());
		assertEquals(0, newUndoHistory.length);

	}

}
