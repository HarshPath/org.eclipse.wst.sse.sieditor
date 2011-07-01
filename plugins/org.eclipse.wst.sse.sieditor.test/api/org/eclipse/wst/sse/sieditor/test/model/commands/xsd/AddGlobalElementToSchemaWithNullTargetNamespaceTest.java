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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;


/**
 * This test covers the following test case:
 * <ol>
 * <li>Open XSD file without targetNamespace</li>
 * <li>Add new global element</li>
 * <li>Check that the element is added and no validation errors are present</li>
 * </ol>
 * 
 * @author Stanislav Nichev
 *
 */
public class AddGlobalElementToSchemaWithNullTargetNamespaceTest extends AbstractXSDCommandTest {

	private static final String NEW_ELEMENT_NAME = "newGlobalElement";

	@Override
	protected String getFilename() {
		return "element_null_tns_name_not_resolved.xsd";
	}

	@Override
	protected String getFolderName() {
		return "pub/csns/nullTNS/";
	}

	private Schema schema;
	private AbstractType type;

	@Override
	protected AbstractNotificationOperation getOperation(final IXSDModelRoot modelRoot) throws Exception {
		schema = (Schema) modelRoot.getSchema();
		type = (AbstractType) Schema.getDefaultSimpleType();

		assertThereAreNoValidationErrors();
		return new AddStructureTypeCommand(schema.getModelRoot(), schema, "add global element", NEW_ELEMENT_NAME, true, type);
	}

	@Override
	protected void assertPostRedoState(final IStatus redoStatus, final IXSDModelRoot modelRoot) {
		assertThereAreNoValidationErrors();
		
		final IType[] types = schema.getAllTypes(NEW_ELEMENT_NAME);
		assertEquals(1, types.length);
		assertNotNull(types[0]);
		assertTrue(types[0] instanceof IStructureType);

		final IStructureType newElement = (IStructureType) types[0];

		assertTrue(newElement.isElement());
		assertEquals(StructureType.class, newElement.getType().getClass());

		final Collection<IElement> subElements = newElement.getAllElements();

		assertEquals(1, subElements.size());
		assertEquals(type, subElements.iterator().next().getType());
	}

	@Override
	protected void assertPostUndoState(final IStatus undoStatus, final IXSDModelRoot modelRoot) {
		assertThereAreNoValidationErrors();
		assertNull(schema.getAllTypes(NEW_ELEMENT_NAME));
	}

}