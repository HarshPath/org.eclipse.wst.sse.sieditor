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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.extract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.listeners.ExtractSchemaTextFieldModifyListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.pages.ExtractSchemaWizardPage;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;

import org.eclipse.core.runtime.Path;

public class ExtractSchemaTextFieldModifyListenerTest {

	private Text text;

	@Before
	public void setUp() {
		text = new Text(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
	}

	@Test
	public void modifyText_PageComplete_EmptyText() {
		final SchemaNode node1 = new SchemaNode("http://test1");
		 node1.setPath(new Path("test.xsd"));

		final ExtractSchemaWizardPage page = new ExtractSchemaWizardPage() {
			@Override
			public Text getSaveLocationText() {
				return text;
			}
		};

		final boolean[] updateCalled = { false };
		final ExtractSchemaTextFieldModifyListener saveLocationTextFieldModifyListener = new ExtractSchemaTextFieldModifyListener(
				page, node1) {
			@Override
			protected void updateButtonsState() {
				updateCalled[0] = true;
			}
		};
		text.addModifyListener(saveLocationTextFieldModifyListener);
		text.setText("/test/stan4o/");
		assertTrue("update buttons state was not called", updateCalled[0]);
	}

	@Test
	public void modifyText_PageComplete_InvalidPath() {
		final SchemaNode node1 = new SchemaNode("http://test1");
		node1.setFilename("test.xsd");

		final ExtractSchemaWizardPage page = new ExtractSchemaWizardPage() {
			@Override
			public Text getSaveLocationText() {
				return text;
			}
		};

		final boolean[] updateCalled = { false };
		final ExtractSchemaTextFieldModifyListener saveLocationTextFieldModifyListener = new ExtractSchemaTextFieldModifyListener(
				page, node1) {
			@Override
			protected void updateButtonsState() {
				updateCalled[0] = true;
			}
		};
		text.addModifyListener(saveLocationTextFieldModifyListener);
		text.setText("/alabala/portocala.xsd");
		assertTrue(updateCalled[0]);
	}

	@Test
	public void modifyText_PageComplete_True() {
		final SchemaNode node1 = new SchemaNode("http://test1");
		node1.setFilename("test.xsd");

		final SchemaNode node2 = new SchemaNode("http://test2");
		node2.setFilename("some1.xsd");
		node2.setPath(null);

		final SchemaNode node3 = new SchemaNode("http://test3");
		node3.setFilename("some2.xsd");
		node3.setPath(null);

		final SchemaNode node4 = new SchemaNode("http://test4");
		node4.setFilename("some3.xsd");
		node4.setPath(null);

		final ExtractSchemaWizardPage page = new ExtractSchemaWizardPage() {
			@Override
			public Text getSaveLocationText() {
				return text;
			}
		};
		node1.addImport(node2);
		node1.addImport(node3);
		node1.addImport(node4);

		final boolean[] updateCalled = { false };
		final ExtractSchemaTextFieldModifyListener saveLocationTextFieldModifyListener = new ExtractSchemaTextFieldModifyListener(
				page, node1) {
			@Override
			protected void updateButtonsState() {
				updateCalled[0] = true;
			}
		};
		text.addModifyListener(saveLocationTextFieldModifyListener);

		assertNull(node1.getPath());
		assertNull(node2.getPath());
		assertNull(node3.getPath());
		assertNull(node4.getPath());

		final String projectPath = File.separator+"project_" + System.currentTimeMillis() + File.separator;
		text.setText(projectPath + "test.xsd");

		assertEquals(projectPath + "test.xsd", node1.getFullPath().toOSString());
		assertEquals(projectPath + "some1.xsd", node2.getFullPath().toOSString());
		assertEquals(projectPath + "some2.xsd", node3.getFullPath().toOSString());
		assertEquals(projectPath + "some3.xsd", node4.getFullPath().toOSString());

		assertTrue(updateCalled[0]);
	}
}
