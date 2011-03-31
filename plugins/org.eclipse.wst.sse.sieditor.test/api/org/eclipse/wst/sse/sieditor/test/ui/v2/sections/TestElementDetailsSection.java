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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.ElementDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class TestElementDetailsSection {

    private static final String OLD_NAME = "oldName"; //$NON-NLS-1$

    private static final String NEW_NAME = "newName"; //$NON-NLS-1$

    private static final String NAME = "name"; //$NON-NLS-1$

    private static final String BOOLEAN = "boolean"; //$NON-NLS-1$

    private Shell shell;

    private ElementNodeDetailsController controller;

    private FormToolkit toolkit;

    private IManagedForm managedForm;

    private ITypeDisplayer typeDisplayer;

    private ElementDetailsSectionExpose section;

    @Before
    public void setUp() throws Exception {
        final DataTypesFormPageController dataTypesController = createNiceMock(DataTypesFormPageController.class);
        expect(dataTypesController.getCommonTypesDropDownList()).andReturn(new String[] { BOOLEAN }).atLeastOnce();
        
        expect(dataTypesController.isPartOfEdittedDocument((IModelObject) anyObject())).andReturn(true).anyTimes();
        replay(dataTypesController);

        controller = createNiceMock(ElementNodeDetailsController.class);
        expect(controller.getFormPageController()).andReturn(dataTypesController).atLeastOnce();

        final Display display = Display.getDefault();
        toolkit = new FormToolkit(display);
        managedForm = createMock(IManagedForm.class);
        typeDisplayer = createMock(ITypeDisplayer.class);
        shell = new Shell(display);
    }
    
    @Test
    public void testReferencingElementHandlesTypeShow() {
    	final IElement refElement = EasymockModelUtils.createReferingIElementMockFromSameModel();
    	final IType type = createMock(IType.class);
    	final ITreeNode selectedNode = createMock(ITreeNode.class);

    	expect(refElement.getType()).andReturn(type).anyTimes();
    	expect(selectedNode.getModelObject()).andReturn(refElement).anyTimes();

    	typeDisplayer.showType(type);
    	replay(controller, typeDisplayer, selectedNode, refElement);
        
        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer) {
			@Override
			protected ITreeNode getNode() {
				return selectedNode;
			}
        };
        section.createContents(shell);
        section.getRefControl().setSize(1, 1);
        
        final Event event = new Event();
        event.widget = section.getRefControl();
        event.button = 1;
        event.type = SWT.MouseDown;
		section.getRefControl().notifyListeners(SWT.MouseDown, event);
		section.getRefControl().notifyListeners(SWT.MouseUp, event);
        
        verify(typeDisplayer);
    }

    @Test
    public void testInitialControlsState() {
        replay(controller);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);

        assertTrue(shell.getChildren()[0] instanceof Section);

        assertEquals(CardinalityType.ZERO_TO_ONE.toString(), section.getCardinalityControl().getItem(0));
        assertEquals(CardinalityType.ONE_TO_ONE.toString(), section.getCardinalityControl().getItem(1));
        assertEquals(CardinalityType.ZERO_TO_MANY.toString(), section.getCardinalityControl().getItem(2));
        assertEquals(CardinalityType.ONE_TO_MANY.toString(), section.getCardinalityControl().getItem(3));

        assertFalse(section.getNillableControl().getSelection());

        verify(controller);
    }

    @Test
    public void testRefresh() {
        expect(controller.getName()).andReturn(NAME).atLeastOnce();
        expect(controller.isNillable()).andReturn(true).atLeastOnce();
        expect(controller.getCardinality()).andReturn(CardinalityType.ONE_TO_ONE).atLeastOnce();
        expect(controller.isNameEditable()).andReturn(true).atLeastOnce();
        expect(controller.isNillableEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityVisible()).andReturn(true).atLeastOnce();
        expect(controller.isNillableVisible()).andReturn(true).atLeastOnce();
        expect(controller.isTypeApplicable()).andReturn(true).atLeastOnce();
        expect(controller.isBaseTypeApplicable()).andReturn(true).atLeastOnce();
        replay(controller);

        final ITreeNode inputMock = createNiceMock(ITreeNode.class);
        final IModelObject modelObjectMock = EasymockModelUtils.createIElementMockFromSameModel();
        expect(inputMock.getModelObject()).andReturn(modelObjectMock).atLeastOnce();
        expect(inputMock.isReadOnly()).andReturn(false).anyTimes();
        replay(inputMock, modelObjectMock);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);
        section.selectionChanged(null, new StructuredSelection(new ITreeNode[] { inputMock }));

        section.refresh();

        assertTrue(section.getNameControl().getVisible());
        assertFalse(section.getRefControl().getVisible());
        assertTrue(section.getNameControl().getEditable());
        // assertTrue(section.getNamespaceControl().getEditable());
        assertTrue(section.getNillableControl().isEnabled());
        assertTrue(section.getCardinalityControl().isEnabled());

        verify(controller, inputMock);
    }
    
    @Test
    public void testRefreshForReferencedElement() {
        expect(controller.getName()).andReturn(NAME).atLeastOnce();
        expect(controller.isNillable()).andReturn(true).atLeastOnce();
        expect(controller.getCardinality()).andReturn(CardinalityType.ONE_TO_ONE).atLeastOnce();
        expect(controller.isNameEditable()).andReturn(true).atLeastOnce();
        expect(controller.isNillableEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityEditable()).andReturn(true).atLeastOnce();
        expect(controller.isCardinalityVisible()).andReturn(true).atLeastOnce();
        expect(controller.isNillableVisible()).andReturn(true).atLeastOnce();
        expect(controller.isTypeApplicable()).andReturn(true).atLeastOnce();
        expect(controller.isBaseTypeApplicable()).andReturn(true).atLeastOnce();
        replay(controller);

        final ITreeNode inputMock = createNiceMock(ITreeNode.class);
        final IElement referingElement = EasymockModelUtils.createReferingIElementMockFromSameModel();
        expect(inputMock.getModelObject()).andReturn(referingElement).atLeastOnce();
        expect(inputMock.isReadOnly()).andReturn(false).anyTimes();
        replay(inputMock, referingElement);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);
        section.selectionChanged(null, new StructuredSelection(new ITreeNode[] { inputMock }));

        section.refresh();

        assertFalse(section.getNameControl().getVisible());
        assertTrue(section.getRefControl().getVisible());
    }

    @Test
    public void testRefreshReadOnly() {
        expect(controller.getName()).andReturn(NAME).atLeastOnce();
        // expect(controller.getNamespace()).andReturn("namespace").atLeastOnce();
        expect(controller.isNillable()).andReturn(true).atLeastOnce();
        expect(controller.getCardinality()).andReturn(CardinalityType.ONE_TO_ONE).atLeastOnce();
        expect(controller.isNameEditable()).andReturn(false).atLeastOnce();
        // expect(controller.isNamespaceEditable()).andReturn(false).atLeastOnce();
        expect(controller.isNillableEditable()).andReturn(false).atLeastOnce();
        expect(controller.isCardinalityEditable()).andReturn(false).atLeastOnce();
        expect(controller.isCardinalityVisible()).andReturn(true).atLeastOnce();
        expect(controller.isNillableVisible()).andReturn(true).atLeastOnce();
        expect(controller.isTypeApplicable()).andReturn(true).atLeastOnce();
        expect(controller.isBaseTypeApplicable()).andReturn(true).atLeastOnce();

        replay(controller);

        final ITreeNode inputMock = createNiceMock(ITreeNode.class);
        final IModelObject modelObjectMock = EasymockModelUtils.createIElementMockFromSameModel();
        expect(inputMock.getModelObject()).andReturn(modelObjectMock).atLeastOnce();
        replay(inputMock, modelObjectMock);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);
        section.selectionChanged(null, new StructuredSelection(new ITreeNode[] { inputMock }));

        section.refresh();

        verify(controller, inputMock);

        assertFalse(section.getNameControl().getEditable());
        // assertFalse(section.getNamespaceControl().getEditable());
        assertFalse(section.getNillableControl().isEnabled());
    }

    @Test
    public void testIsStale() {
        expect(controller.getName()).andReturn(NAME).atLeastOnce();
        // expect(controller.getNamespace()).andReturn("namespace").atLeastOnce();
        expect(controller.isNillable()).andReturn(true).atLeastOnce();
        expect(controller.getCardinality()).andReturn(CardinalityType.ONE_TO_ONE).atLeastOnce();
        expect(controller.isBaseTypeApplicable()).andReturn(true).atLeastOnce();
        replay(controller);

        final IModelObject model = createNiceMock(IModelObject.class);
        EasymockModelUtils.addComponentAndContainerCalls(model, EObject.class);
        replay(model);

        final ITreeNode node = createNiceMock(ITreeNode.class);
        expect(node.getModelObject()).andReturn(model).atLeastOnce();
        replay(node);

        final IStructuredSelection selection = createNiceMock(IStructuredSelection.class);
        expect(selection.size()).andReturn(1).anyTimes();
        expect(selection.getFirstElement()).andReturn(node).atLeastOnce();
        replay(selection);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);
        section.getTypeEditor().createControl(toolkit, shell);
        section.selectionChanged(null, selection);

        section.getNameControl().setText(NAME);
        // section.getNamespaceControl().setText("namespace");
        section.getNillableControl().setSelection(true);
        // 1 = CardinalityType.ONE_TO_ONE
        section.getCardinalityControl().select(1);

        assertNotNull(section.getModelObject());
        assertTrue(section.getTypeEditor().isStale());
        assertTrue(section.isStale());

        verify(controller);
    }

    @Test
    public void testNameTextFocusListener() {
        controller.setName(NEW_NAME);
        replay(controller);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);
        section.getNameControl().setText(NEW_NAME);

        final Event event = new Event();
        event.widget = section.getNameControl();
        section.getNameControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
    }

    @Test
    public void testCardinalitySelectionListener() {
        controller.setCardinality(CardinalityType.ONE_TO_ONE);
        replay(controller);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);

        // -1 = Nothing selected
        assertEquals(-1, section.getCardinalityControl().getSelectionIndex());

        // 1 = ONE_TO_ONE
        section.getCardinalityControl().select(1);

        final Event event = new Event();
        event.widget = section.getCardinalityControl();
        section.getCardinalityControl().notifyListeners(SWT.Selection, event);
        section.getCardinalityControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
    }

    @Test
    public void testNillableControlSelectionListener() {
        controller.setNillable(true);
        replay(controller);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);

        assertFalse(section.getNillableControl().getSelection());

        section.getNillableControl().setSelection(true);

        final Event event = new Event();
        event.widget = section.getNillableControl();
        section.getNillableControl().notifyListeners(SWT.Selection, event);

        verify(controller);
    }
    
//    @Test
//    public void testAnonymousControlSelectionListener() {
//        controller.setAnonymous(true);
//        replay(controller);
//        
//        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
//        section.createContents(shell);
//        
//        assertFalse(section.getAnonymousControl().getSelection());
//        
//        section.getAnonymousControl().setSelection(true);
//        
//        final Event event = new Event();
//        event.widget = section.getAnonymousControl();
//        section.getAnonymousControl().notifyListeners(SWT.Selection, event);
//        
//        verify(controller);
//    }

    @Test
    public void testNameControlModifyTextListener() {
        expect(controller.getName()).andReturn(OLD_NAME).atLeastOnce();
        replay(controller);

        section = new ElementDetailsSectionExpose(controller, toolkit, managedForm, typeDisplayer);
        section.createContents(shell);

        assertFalse(section.isDirty());

        section.getNameControl().setText(NEW_NAME);
        verify(controller);
    }


    
    // @Test
    // public void testNamespaceControlModifyTextListener() {
    // expect(controller.getNamespace()).andReturn("oldNamespace").atLeastOnce();
    // replay(controller);
    //
    // section = new ElementDetailsSectionExpose(controller, toolkit,
    // managedForm, typeDisplayer);
    // section.createContents(shell);
    //		
    // assertFalse(section.isDirty());
    //		
    // section.getNamespaceControl().setText("newNamespace");
    // verify(controller);
    // }

    private class ElementDetailsSectionExpose extends ElementDetailsSection {

        public ElementDetailsSectionExpose(final ElementNodeDetailsController detailsController, final FormToolkit toolkit,
                final IManagedForm managedForm, final ITypeDisplayer typeDisplayer) {
            super(detailsController, toolkit, managedForm, typeDisplayer);

        }

        public Text getNameControl() {
            return nameControl.getControl();
        }

        public Hyperlink getRefControl() {
            return refControl.getControl();
        }

        // public Text getNamespaceControl() {
        // return namespaceControl.getControl();
        // }

        public CCombo getCardinalityControl() {
            return cardinalityControl.getControl();
        }

        public Button getNillableControl() {
            return nillableControl.getControl();
        }

        @Override
        public IModelObject getModelObject() {
            return super.getModelObject();
        }

        public ElementTypeEditor getTypeEditor() {
            return typeEditor;
        }

        public TypePropertyEditor getBaseTypeEditor() {
            return this.baseTypeEditor;
        }
        
    }
}
