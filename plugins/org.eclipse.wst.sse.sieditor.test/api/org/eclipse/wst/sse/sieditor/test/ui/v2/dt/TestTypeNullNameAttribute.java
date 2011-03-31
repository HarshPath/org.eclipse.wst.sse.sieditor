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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.SimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.StructureDetailsSection;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Test;

public class TestTypeNullNameAttribute {
	
	@Test
	public void testStructureDetailsSectionNameTextDoesNotChangeForNullModelObjectName() {
		// Setup
		final Display display = Display.getDefault();

		final IDataTypesFormPageController controller = createMock(IDataTypesFormPageController.class);
        final FormToolkit toolkit = new FormToolkit(display);
        final IManagedForm managedForm = createMock(IManagedForm.class);

        final INamedObject modelObject = createMock(INamedObject.class);
        // Here the name is NULL
		expect(modelObject.getName()).andReturn(null).anyTimes();
		EasymockModelUtils.addComponentAndContainerCalls(modelObject, EObject.class);
		replay(modelObject);
        
        final ITreeNode node = createMock(ITreeNode.class);
        expect(node.getModelObject()).andReturn(modelObject).anyTimes();
        replay(node);
        
        final StructureDetailsSectionExpose section = new StructureDetailsSectionExpose(controller, toolkit, managedForm, node);
        final Shell shell = new Shell(display);
        section.createContents(shell);
        
        // Test ModifyListener for null model object name
        assertFalse(section.isDirty());
        
        final Text nameText = section.getNameText();
        final Event event = new Event();
		nameText.notifyListeners(SWT.Modify, event );
        
		// do not change because nameText="" and model.name=null
        assertFalse(section.isDirty());
	}
	
	@Test
	public void testStructureTypeNodeGetDisplayName() {
		final INamedObject modelObject = EasymockModelUtils.createINamedObjectMockFromSameModel();
		expect(modelObject.getName()).andReturn(null).anyTimes();
		replay(modelObject);
		
		final StructureTypeNode node = new StructureTypeNode(modelObject, null, null);
		
		assertEquals(UIConstants.EMPTY_STRING, node.getDisplayName());
	}
	
	@Test
	public void testSimpleTypeNodeGetDisplayName() {
		final INamedObject modelObject = EasymockModelUtils.createINamedObjectMockFromSameModel();
		expect(modelObject.getName()).andReturn(null).anyTimes();
		replay(modelObject);
		
		final SimpleTypeNode node = new SimpleTypeNode(modelObject, null);
		
		assertEquals(UIConstants.EMPTY_STRING, node.getDisplayName());
	}
	
	@Test
	public void testElementNodeGetDisplayName() {
		final IElement modelObject = EasymockModelUtils.createIElementMockFromSameModel();
		expect(modelObject.getName()).andReturn(null).anyTimes();
		replay(modelObject);
		
		final ElementNode node = new ElementNode(modelObject, null, null);
		
		assertEquals(UIConstants.EMPTY_STRING, node.getDisplayName());
	}
	
	
	@Test
	public void testTypeIsAnonymousWithNullNameAndParentSchema() {
		final IXSDModelRoot modelRoot = createMock(IXSDModelRoot.class);
		final ISchema schema = createMock(ISchema.class);
		
		final XSDSchema container = createMock(XSDSchema.class);
		
		final XSDTypeDefinition xsdComp = createMock(XSDTypeDefinition.class);
		expect(xsdComp.getContainer()).andReturn(container).anyTimes();
		expect(xsdComp.getName()).andReturn(null).anyTimes();
		replay(xsdComp);
		
		final TestAbstractType type = new TestAbstractType(modelRoot, xsdComp, schema);
		
		assertFalse(type.isAnonymous());
	}
	
	@Test
	public void testTypeIsAnonymousWithNullNameAndNotParentSchema() {
		final IXSDModelRoot modelRoot = createMock(IXSDModelRoot.class);
		final ISchema schema = createMock(ISchema.class);
		
		final XSDComplexTypeDefinition container = createMock(XSDComplexTypeDefinition.class);
		
		final XSDTypeDefinition xsdComp = createMock(XSDTypeDefinition.class);
		expect(xsdComp.getContainer()).andReturn(container).anyTimes();
		expect(xsdComp.getName()).andReturn(null).anyTimes();
		replay(xsdComp);
		
		final TestAbstractType type = new TestAbstractType(modelRoot, xsdComp, schema);
		
		assertTrue(type.isAnonymous());
	}
	
	private class TestAbstractType extends AbstractType {

		protected TestAbstractType(final IXSDModelRoot modelRoot,
				final XSDNamedComponent component, final ISchema schema) {
			super(modelRoot, component, schema);
			// TODO Auto-generated constructor stub
		}

		@Override
		public IType getBaseType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDocumentation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setBaseType(final IType baseType) throws ExecutionException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setName(final String name) throws IllegalInputException,
				DuplicateException, ExecutionException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setNamespace(final String namespace)
				throws IllegalInputException, ExecutionException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class StructureDetailsSectionExpose extends StructureDetailsSection {

		public StructureDetailsSectionExpose(final IFormPageController controller,
				final FormToolkit toolkit, final IManagedForm managedForm, final ITreeNode node) {
			super(controller, toolkit, managedForm);
			this.node = node;
		}
		
		public Text getNameText() {
			return nameText;
		}
	}
}
