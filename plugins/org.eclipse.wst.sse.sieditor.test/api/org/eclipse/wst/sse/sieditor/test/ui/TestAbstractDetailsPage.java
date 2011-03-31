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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.junit.Before;
import org.junit.Test;


public class TestAbstractDetailsPage {
	
	private AbstractDetailsPageChild page;
	
	private IDetailsPageSection section1;
	
	private IDetailsPageSection section2;

	@Before
	public void setUp() {
		section1 = createMock(IDetailsPageSection.class);
		section2 = createMock(IDetailsPageSection.class);
		
		final List<IDetailsPageSection> sections = new ArrayList<IDetailsPageSection>();
		sections.add(section1);
		sections.add(section2);
		
		final IFormPageController controller = createMock(IFormPageController.class);
		replay(controller);
		
		page = new AbstractDetailsPageChild(controller);
		page.setSections(sections);
	}
	
	@Test
	public void testIsDirtyFalse() {
		expect(section1.isDirty()).andReturn(false).anyTimes();
		replay(section1);
		
		expect(section2.isDirty()).andReturn(false).anyTimes();
		replay(section2);
		
		assertFalse(page.isDirty());
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testIsDirtyTrue() {
		expect(section1.isDirty()).andReturn(false).anyTimes();
		replay(section1);
		
		expect(section2.isDirty()).andReturn(true).anyTimes();
		replay(section2);
		
		assertTrue(page.isDirty());
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testIsStaleFalse() {
		expect(section1.isStale()).andReturn(false).anyTimes();
		replay(section1);
		
		expect(section2.isStale()).andReturn(false).anyTimes();
		replay(section2);

		assertFalse(page.isStale());
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testIsStaleTrue() {
		expect(section1.isStale()).andReturn(false).anyTimes();
		replay(section1);
		
		expect(section2.isStale()).andReturn(true).anyTimes();
		replay(section2);
		
		assertTrue(page.isStale());
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testRefresh() {
		section1.refresh();
		replay(section1);
		
		section2.refresh();
		replay(section2);
		
		page.refresh();
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testSelectionChanged() {
		final IFormPart part = createMock(IFormPart.class);
		replay(part);
		
		final ISelection selection = createMock(ISelection.class);
		replay(selection);
		
		section1.selectionChanged(part, selection);
		replay(section1);
		
		section2.selectionChanged(part, selection);
		replay(section2);
		
		page.selectionChanged(part, selection);
		
		verify(section1);
		verify(section2);
	}
	
	@Test
	public void testCreateContents() {
		final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        
        page.createContents(shell);
        
        assertTrue(page.createSections_called);
        assertEquals(GridLayout.class, shell.getLayout().getClass());
	}
	
	@Test
	public void testIsReadOnlyForReadonlyResource() {
	    final IFormPageController controller = createMock(IFormPageController.class);
	    expect(controller.isResourceReadOnly()).andReturn(true);
	    expect(controller.isPartOfEdittedDocument(null)).andReturn(true);
            replay(controller);
            
            final ITreeNode node = createNiceMock(ITreeNode.class);
            replay(node);
            
            final AbstractDetailsPageChild pageToTest = new AbstractDetailsPageChild(controller);
            pageToTest.setTreeNode(node);
            
            assertTrue(pageToTest.isReadOnly());
	}
	
	@Test
        public void testIsReadOnlyForModelObjectInOtherDomain() {
            final IFormPageController controller = createMock(IFormPageController.class);
            expect(controller.isResourceReadOnly()).andReturn(false);
            replay(controller);
            
            final ITreeNode node = createNiceMock(ITreeNode.class);
            expect(node.isReadOnly()).andReturn(true).anyTimes();
            replay(node);
            
            final AbstractDetailsPageChild pageToTest = new AbstractDetailsPageChild(controller);
            pageToTest.setTreeNode(node);
            
            assertTrue(pageToTest.isReadOnly());
        }
	
	private class AbstractDetailsPageChild extends AbstractDetailsPage {
		
		public boolean createSections_called = false;
		
		public AbstractDetailsPageChild(final IFormPageController controller) {
			super(controller);
		}

		@Override
		public void createSections(final Composite parent) {
			createSections_called = true;
		}

		@Override
		public void setSections(final List<IDetailsPageSection> sections) {
			super.setSections(sections);
		}
		
		public void setTreeNode(final ITreeNode node) {
		    super.treeNode = node;
		}
		
	}
}
