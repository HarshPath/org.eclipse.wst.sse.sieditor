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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.ElementDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.SimpleTypeConstraintsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

public class TestElementNodeDetailsPage {

	private Shell shell;

    private IManagedForm managedForm;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        managedForm = createNiceMock(IManagedForm.class);
        shell = new Shell(display);
    }
    
	@Test
	public void testPageSectionsExists() {
		final DataTypesFormPageController controller = createNiceMock(DataTypesFormPageController.class);
		expect(controller.getCommonTypesDropDownList()).andReturn(new String[] {"boolean"}).atLeastOnce();
		replay(controller);
		
		final ITypeDisplayer typeDisplayer = createNiceMock(ITypeDisplayer.class);
		replay(typeDisplayer);
		
		final ElementNodeDetailsPage page = new ElementNodeDetailsPage(controller , typeDisplayer);
		page.initialize(managedForm);
		page.createContents(shell);
		
		final Iterator<IDetailsPageSection> sectionsIter = page.getSections().iterator();
		assertEquals(ElementDetailsSection.class, sectionsIter.next().getClass());
		assertEquals(SimpleTypeConstraintsSection.class, sectionsIter.next().getClass());
		assertEquals(DocumentationSection.class, sectionsIter.next().getClass());
	}
	
	@Test
	public void testPageSelectionChangedListener() {
		final DataTypesFormPageController controller = createNiceMock(DataTypesFormPageController.class);
		expect(controller.getCommonTypesDropDownList()).andReturn(new String[] {"boolean"}).atLeastOnce();
		replay(controller);
		
		final ITypeDisplayer typeDisplayer = createNiceMock(ITypeDisplayer.class);
		replay(typeDisplayer);
		
		final ElementNodeDetailsPageExpose page = new ElementNodeDetailsPageExpose(controller , typeDisplayer);
		page.initialize(managedForm);
		page.createContents(shell);

		final ISimpleType modelObject = (ISimpleType)BuiltinTypesHelper.getInstance().getCommonBuiltinType("boolean");
		
		final ITreeNode node = createNiceMock(ITreeNode.class);
		expect(node.getModelObject()).andReturn(modelObject).atLeastOnce();
		replay(node);
		
		final StructuredSelection selection = new StructuredSelection(node);
		
		final IFormPart part = createNiceMock(IFormPart.class);
		replay(part);
		
		page.selectionChanged(part, selection);
		
		assertSame(node, page.getDetailsController().getInput());
		verify(node);
	}
	
	private class ElementNodeDetailsPageExpose extends ElementNodeDetailsPage {

		public ElementNodeDetailsPageExpose(
				final IDataTypesFormPageController controller,
				final ITypeDisplayer typeDisplayer) {
			super(controller, typeDisplayer);
		}
		
		public ElementNodeDetailsController getDetailsController() {
			return detailsController;
		}
	}
}
