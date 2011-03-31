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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.sse.sieditor.ui.v2.common.TypeSearchDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class TestTypeSearchDialog {

	private Shell shell;
	private Composite composite;
	private TypeSearchDialogExposed dialog;
	private Display display;

    @Before
    public void setUp() throws Exception {
        display = Display.getDefault();
        shell = new Shell(display);
        composite = new Composite(shell, SWT.NO_BACKGROUND);
        dialog = new TypeSearchDialogExposed(shell, "title");
		dialog.create();
    }

    @After
    public void tearDown() throws Exception {
        shell = null;
    }

	@Test
	public void testControlsInitialState() {
		assertEquals("Type filter text here", dialog.getTextFilter().getText());
		assertTrue(dialog.getAllButton().getSelection());
		assertFalse(dialog.getInlineTypeButton().getSelection());
		assertFalse(dialog.getPrimitiveTypeButton().getSelection());
	}
	
	@Test
	public void testTextFilterFocusGained() {
		assertEquals("Type filter text here", dialog.getTextFilter().getText());
		
		dialog.getTextFilter().notifyListeners(SWT.FocusIn, new Event());
		
		assertEquals("", dialog.getTextFilter().getText());
	}
	
	@Test
	public void testTextFilterModifyTextListenerDelayOnTypesUpUpdate() throws InterruptedException, InvocationTargetException {
		assertTrue(dialog.getMasterTypeList().size() > 0);
		assertEquals(0, ((ArrayList<IType>)dialog.getTypesTableViewer().getInput()).size());
		
		dialog.getTextFilter().setText("boolean");
		
		assertEquals(0, ((ArrayList<IType>)dialog.getTypesTableViewer().getInput()).size());
		
		waitTypesTableToRefresh();
		
		assertEquals(1, ((ArrayList<IType>)dialog.getTypesTableViewer().getInput()).size());
	}
	
	@Test
	public void testTypesTableItemsWithSameNames() throws InvocationTargetException, InterruptedException {
		IDescription wsdl = createNiceMock(IDescription.class);
		expect(wsdl.getLocation()).andReturn("/mywsdl.wsdl").anyTimes();
		replay(wsdl);
		
		final IType duplicateBoolean = createNiceMock(IType.class);
		expect(duplicateBoolean.getName()).andReturn("boolean").anyTimes();
		expect(duplicateBoolean.getNamespace()).andReturn("http://www.w3.org/2001/XMLSchema").anyTimes();
		expect(duplicateBoolean.getRoot()).andReturn(wsdl).anyTimes();
		replay(duplicateBoolean);
		
		dialog = new TypeSearchDialogExposed(shell, "title") {
			@Override
			protected void populateMasterTypeList() {
				super.populateMasterTypeList();
				masterTypeList.add(duplicateBoolean);
			}
			
		};
		dialog.create();

		dialog.getTextFilter().setText("boolean");
		waitTypesTableToRefresh();
		
		final TableItem[] items = dialog.getTypesTableViewer().getTable().getItems();
		
		assertEquals(2, items.length);
		assertEquals("boolean  -  http://www.w3.org/2001/XMLSchema", items[0].getText());
		assertEquals("boolean  -  http://www.w3.org/2001/XMLSchema  -  mywsdl.wsdl", items[1].getText());
	}
	
	@Test
	public void testTypesTableVieweraddSelectionChangedListener() throws InvocationTargetException, InterruptedException {
		dialog.getTextFilter().setText("boolean");
		waitTypesTableToRefresh();
		
		assertTrue(dialog.getButton(IDialogConstants.OK_ID).isEnabled());
		
		dialog.getTypesTableViewer().setSelection(null);
		
		assertFalse(dialog.getButton(IDialogConstants.OK_ID).isEnabled());
	}
	
	@Test
	public void testTypesTableVieweraddDoubleClickListener() throws InvocationTargetException, InterruptedException {
		assertNull(dialog.getTypeSelection());
		
		dialog.getTextFilter().setText("boolean");
		waitTypesTableToRefresh();
		
		dialog.getTypesTableViewer().getControl().getListeners(SWT.MouseDoubleClick);
		
		final Event event = new Event();
		event.detail = SWT.DefaultSelection;
		event.widget = dialog.getTypesTableViewer().getControl();
		dialog.getTypesTableViewer().getControl().notifyListeners(SWT.DefaultSelection, event);
		
		assertTrue(Window.OK == dialog.getReturnCode());
		assertNotNull(dialog.getTypeSelection());
	}
	
	@Test
	public void testLabelProviderGetImage() throws InvocationTargetException, InterruptedException {
		final ILabelProvider lableProvider = dialog.getLableProvider();
		
		ISimpleType simpleType = createMock(ISimpleType.class);
		IStructureType structureType = createMock(IStructureType.class);
		
		IStructureType structureTypeElement = createMock(IStructureType.class);
		expect(structureTypeElement.isElement()).andReturn(true);
		replay(structureTypeElement);
		
		IType primitiveType = createMock(IType.class);
		
		assertEquals(Activator.getDefault().getImage(Activator.NODE_SIMPLE_TYPE), lableProvider.getImage(simpleType));
		assertEquals(Activator.getDefault().getImage(Activator.NODE_STRUCTURE_TYPE), lableProvider.getImage(structureType));
		assertEquals(Activator.getDefault().getImage(Activator.NODE_ELEMENT), lableProvider.getImage(structureTypeElement));
		assertEquals(Activator.getDefault().getImage(Activator.NODE_PRIMITIVE), lableProvider.getImage(primitiveType));
	}
	
	

	private void waitTypesTableToRefresh() throws InvocationTargetException,
			InterruptedException {
		IRunnableWithProgress delay = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				Thread.sleep(dialog.getTEXT_TYPED_DELAY()*2);
			}
		};
		ModalContext.run(delay, true, new NullProgressMonitor(), display);
	}
	
	private class TypeSearchDialogExposed extends TypeSearchDialog {

		@Override
		protected Button getButton(int id) {
			return super.getButton(id);
		}

		public TypeSearchDialogExposed(Shell shell, String dialogTitle) {
			super(shell, dialogTitle);
		}
		
		public Text getTextFilter() {
			return textFilter;
		}
		
		public Button getAllButton() {
			return all;
		}
		
		public Button getInlineTypeButton() {
			return inlineType;
		}
		
		public Button getPrimitiveTypeButton() {
			return primitiveType;
		}
		
		public TableViewer getTypesTableViewer() {
			return typesTableViewer;
		}
		
		public List<IType> getMasterTypeList() {
			return masterTypeList;
		}
		
		public int getTEXT_TYPED_DELAY() {
			return TEXT_TYPED_DELAY;
		}
		
		public ILabelProvider getLableProvider() {
			return lableProvider;
		}
	}
}
