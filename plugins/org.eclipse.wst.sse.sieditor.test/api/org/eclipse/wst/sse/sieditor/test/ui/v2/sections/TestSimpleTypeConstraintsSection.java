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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.SimpleTypeConstraintsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.FacetTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;

public class TestSimpleTypeConstraintsSection {

    private static final String _10 = "10"; //$NON-NLS-1$

    private static final String _3 = "3"; //$NON-NLS-1$

    private static final String _1 = "1"; //$NON-NLS-1$

    private static final String _2 = "2"; //$NON-NLS-1$

    private static final String BOOLEAN_STRING = "boolean"; //$NON-NLS-1$

    private static final String DIALOG_VALUE = "DIALOG_VALUE"; //$NON-NLS-1$

    private Shell shell;

    private ElementNodeDetailsController controller;

    private FormToolkit toolkit;

    private IManagedForm managedForm;

    private SimpleTypeConstraintsSectionExpose section;

    @Before
    public void setUp() throws Exception {
        final DataTypesFormPageController dataTypesController = createNiceMock(DataTypesFormPageController.class);
        expect(dataTypesController.getCommonTypesDropDownList()).andReturn(new String[] { BOOLEAN_STRING }).atLeastOnce();
        expect(dataTypesController.isPartOfEdittedDocument((IModelObject) EasyMock.anyObject())).andReturn(true).anyTimes();
        replay(dataTypesController);

        controller = createNiceMock(ElementNodeDetailsController.class);
        expect(controller.getFormPageController()).andReturn(dataTypesController).atLeastOnce();

        final Display display = Display.getDefault();
        toolkit = new FormToolkit(display);
        managedForm = createMock(IManagedForm.class);
        shell = new Shell(display);
        shell.setLayout(new GridLayout());
    }

    @Test
    public void testInitialControlsState() {
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        assertTrue(shell.getChildren()[0] instanceof Section);

        assertEquals(section.getWhitespaceComboItemLabel(0), section.getWhitespaceControl().getItem(0));
        assertEquals(section.getWhitespaceComboItemLabel(1), section.getWhitespaceControl().getItem(1));
        assertEquals(section.getWhitespaceComboItemLabel(2), section.getWhitespaceControl().getItem(2));
        assertEquals(section.getWhitespaceComboItemLabel(3), section.getWhitespaceControl().getItem(3));

        verify(controller);
    }

    @Test
    public void testIsStale() {
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        assertNull(section.getModelObject());
        assertFalse(section.isStale());

        verify(controller);
    }

    @Test
    public void testControlsAreInPage() {
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        assertTrue(shell.getChildren()[0] instanceof Section);

        final Section sectionComposite = (Section) shell.getChildren()[0];
        final Composite client = (Composite) sectionComposite.getClient();
        final List<Control> children = Arrays.asList(client.getChildren());

        assertTrue(children.contains(section.getEnumsControl()));
        assertTrue(children.contains(section.getFractionDigitsControl()));
        assertTrue(children.contains(section.getLengthControl()));
        assertTrue(children.contains(section.getMaxExclusiveControl()));
        assertTrue(children.contains(section.getMaxInclusiveControl()));
        assertTrue(children.contains(section.getMaxLengthControl()));
        assertTrue(children.contains(section.getMinExclusiveControl()));
        assertTrue(children.contains(section.getMinInclusiveControl()));
        assertTrue(children.contains(section.getMinLengthControl()));
        assertTrue(children.contains(section.getPatternsControl()));
        assertTrue(children.contains(section.getTotalDigitsControl()));
        assertTrue(children.contains(section.getWhitespaceControl()));

        verify(controller);
    }

    @Test
    public void testRefresh() {
        refresh(false);
    }

    @Test
    public void testRefreshReadOnly() {
        refresh(true);
    }

    private void refresh(final boolean constraintsControllerEditable) {
        final ITreeNode selectedNode = createNiceMock(ITreeNode.class);
        replay(selectedNode);

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.isEditable()).andReturn(Boolean.valueOf(constraintsControllerEditable)).atLeastOnce();
        expect(simpleTypesController.isLengthVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isMinMaxVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getLength()).andReturn(_2).atLeastOnce();
        expect(simpleTypesController.getMinLength()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxLength()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isMinMaxExclusiveVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isMinMaxInclusiveVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getMinInclusive()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxInclusive()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.getMinExclusive()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxExclusive()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isTotalDigitsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getTotalDigits()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isFractionDigitsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getFractionDigits()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isWhitespaceVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getWhitespace()).andReturn(Whitespace.REPLACE).atLeastOnce();
        expect(simpleTypesController.isPatternsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isEnumsVisible()).andReturn(true).atLeastOnce();
        replay(simpleTypesController);

        expect(controller.isConstraintsSectionApplicable()).andReturn(true).atLeastOnce();
        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        final ISelection selection = new StructuredSelection(new Object[] { selectedNode });

        section.selectionChanged(null, selection);
        section.refresh();

        final boolean enabled = constraintsControllerEditable && !selectedNode.isReadOnly();
        assertEquals(enabled, section.getLengthControl().getEnabled());
        assertEquals(enabled, section.getMinLengthControl().getEnabled());
        assertEquals(enabled, section.getMaxLengthControl().getEnabled());
        assertEquals(enabled, section.getMinInclusiveControl().getEnabled());
        assertEquals(enabled, section.getMaxInclusiveControl().getEnabled());
        assertEquals(enabled, section.getMinExclusiveControl().getEnabled());
        assertEquals(enabled, section.getMaxExclusiveControl().getEnabled());
        assertEquals(enabled, section.getTotalDigitsControl().getEnabled());
        assertEquals(enabled, section.getFractionDigitsControl().getEnabled());
        assertEquals(enabled, section.getWhitespaceControl().getEnabled());
        assertLabeledControlControlEnabled(section, section.getEnumsControl(), enabled);
        assertLabeledControlControlEnabled(section, section.getPatternsControl(), enabled);

        verify(controller);
        verify(simpleTypesController);
    }

    private void assertLabeledControlControlEnabled(final SimpleTypeConstraintsSectionExpose section, final Control control, final boolean enabled) {
        final ArrayList<Control> flatList = new ArrayList<Control>();
        flatList.add(control);
        int index = 0;
        while (index < flatList.size()) {
            final Control next = flatList.get(index);
            if (next instanceof Composite && ((Composite) next).getChildren().length != 0) {
                Collections.addAll(flatList, ((Composite) next).getChildren());
            } else {
                if (next instanceof Text) {
                    assertEquals(enabled, ((Text) next).getEditable());
                } else {
                	if(next == section.getRemoveEnumsButton() || next == section.getRemovePatternButton()) {
                		// no selection in the table
                		assertEquals(false, next.getEnabled());
                	}
                	else {
                		assertEquals(enabled, next.getEnabled());
                	}
                }
            }
            index++;
        }
    }

    /**
     * Asserts that after refresh the section UI will be re-layouted in case
     * some controls have been shown/hidden
     */
    @Test
    public void testRedrawAfterRefresh() {
        // by default the controls are created visible
        SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.isLengthVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getLength()).andReturn(_2).atLeastOnce();
        expect(simpleTypesController.isMinMaxVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getMinLength()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxLength()).andReturn(_3).atLeastOnce();

        expect(simpleTypesController.isMinMaxExclusiveVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isMinMaxInclusiveVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getMinInclusive()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxInclusive()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.getMinExclusive()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxExclusive()).andReturn(_3).atLeastOnce();

        expect(simpleTypesController.isTotalDigitsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getTotalDigits()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isFractionDigitsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getFractionDigits()).andReturn(_3).atLeastOnce();
        expect(simpleTypesController.isWhitespaceVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getWhitespace()).andReturn(Whitespace.REPLACE).atLeastOnce();
        expect(simpleTypesController.isPatternsVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isEnumsVisible()).andReturn(true).atLeastOnce();
        replay(simpleTypesController);

        expect(controller.isConstraintsSectionApplicable()).andReturn(true).atLeastOnce();
        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        final boolean[] parentRelayouted = new boolean[1];
        section.createContents(shell);

        final boolean[] relayouted = new boolean[1];

        // it's enough to test just the length control
        final Control lenghtControl = section.getLengthControl();

        lenghtControl.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(final ControlEvent e) {
                relayouted[0] = true;
            }
        });
        final Composite sectionClient = lenghtControl.getParent();
        sectionClient.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(final ControlEvent e) {
                parentRelayouted[0] = true;
            }
        });

        section.refresh();

        // no layout should had happened
        Assert.assertFalse(relayouted[0]);
        Assert.assertFalse(parentRelayouted[0]);

        // now change the section, so that length control is visible
        reset(controller);

        simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.isLengthVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.isMinMaxVisible()).andReturn(true).atLeastOnce();
        expect(simpleTypesController.getLength()).andReturn(_2).atLeastOnce();
        expect(simpleTypesController.getMinLength()).andReturn(_1).atLeastOnce();
        expect(simpleTypesController.getMaxLength()).andReturn(_3).atLeastOnce();

        expect(simpleTypesController.isMinMaxInclusiveVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isMinMaxExclusiveVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isTotalDigitsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isFractionDigitsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isWhitespaceVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isPatternsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isEnumsVisible()).andReturn(false).atLeastOnce();
        replay(simpleTypesController);

        expect(controller.isConstraintsSectionApplicable()).andReturn(true).atLeastOnce();
        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section.refresh();

        // layout should had happened
        Assert.assertTrue(relayouted[0]);
        Assert.assertTrue(parentRelayouted[0]);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testLengthControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setLength(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getLengthControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getLengthControl();
        section.getLengthControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMinLengthControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMinLength(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMinLengthControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMinLengthControl();
        section.getMinLengthControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMaxLengthControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMaxLength(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMaxLengthControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMaxLengthControl();
        section.getMaxLengthControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMinInclusiveControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMinInclusive(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMinInclusiveControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMinInclusiveControl();
        section.getMinInclusiveControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMaxInclusiveControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMaxInclusive(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMaxInclusiveControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMaxInclusiveControl();
        section.getMaxInclusiveControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMinExclusiveControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMinExclusive(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMinExclusiveControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMinExclusiveControl();
        section.getMinExclusiveControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testMaxExclusiveControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setMaxExclusive(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getMaxExclusiveControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getMaxExclusiveControl();
        section.getMaxExclusiveControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testWhitespaceControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setWhitespace(Whitespace.REPLACE);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        // 3 = Whitespace.REPLACE
        section.getWhitespaceControl().select(3);

        final Event event = new Event();
        event.widget = section.getWhitespaceControl();
        section.getWhitespaceControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testTotalDigitsControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setTotalDigits(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getTotalDigitsControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getTotalDigitsControl();
        section.getTotalDigitsControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testFractionDigitsControlFocusLost() {
        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.setFractionDigits(_10);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getFractionDigitsControl().setText(_10);

        final Event event = new Event();
        event.widget = section.getFractionDigitsControl();
        section.getFractionDigitsControl().notifyListeners(SWT.FocusOut, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testAddPatternSelectionListener() {
        final boolean inputDialog_open_Called[] = { false };

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.addPattern(DIALOG_VALUE);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm) {

            @Override
            protected InputDialog getInputDialog(final Shell parentShell, final String dialogTitle, final String dialogMessage,
                    final String initialValue, final IInputValidator validator) {

                return new InputDialog(parentShell, dialogTitle, dialogMessage, initialValue, validator) {
                    @Override
                    public int open() {
                        assertNotNull(validator);
                        assertNotNull(validator.isValid(""));
                        inputDialog_open_Called[0] = true;
                        return Window.OK;
                    }

                    @Override
                    public String getValue() {
                        return DIALOG_VALUE;
                    }

                };
            }

        };
        section.createContents(shell);

        final Event event = new Event();
        event.widget = section.getAddPatternButton();
        section.getAddPatternButton().notifyListeners(SWT.Selection, event);

        assertTrue(inputDialog_open_Called[0]);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testRemovePatternSelectionListener() {
        final IFacet facet = createNiceMock(IFacet.class);
        replay(facet);

        final ISimpleType simpleType = createNiceMock(ISimpleType.class);
        replay(simpleType);

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.getPatterns()).andReturn(new IFacet[] { facet }).atLeastOnce();
        simpleTypesController.deletePattern(facet);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getPatternsTableViewer().setInput(simpleType);
        section.getPatternsTableViewer().setSelection(new StructuredSelection(facet));

        final Event event = new Event();
        event.widget = section.getRemovePatternButton();
        section.getRemovePatternButton().notifyListeners(SWT.Selection, event);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testAddEnumsSelectionListener() {
        final boolean inputDialog_open_Called[] = { false };

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        simpleTypesController.addEnum(DIALOG_VALUE);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm) {

            @Override
            protected InputDialog getInputDialog(final Shell parentShell, final String dialogTitle, final String dialogMessage,
                    final String initialValue, final IInputValidator validator) {

                return new InputDialog(parentShell, dialogTitle, dialogMessage, initialValue, validator) {
                    @Override
                    public int open() {
                        assertNotNull(validator);
                        assertNotNull(validator.isValid(""));
                        inputDialog_open_Called[0] = true;
                        return Window.OK;
                    }

                    @Override
                    public String getValue() {
                        return DIALOG_VALUE;
                    }

                };
            }

        };
        section.createContents(shell);

        final Event event = new Event();
        event.widget = section.getAddEnumsButton();
        section.getAddEnumsButton().notifyListeners(SWT.Selection, event);

        assertTrue(inputDialog_open_Called[0]);

        verify(controller);
        verify(simpleTypesController);
    }

    @Test
    public void testRemoveEnumsSelectionListener() {
        final IFacet facet = createNiceMock(IFacet.class);
        replay(facet);

        final ISimpleType simpleType = createNiceMock(ISimpleType.class);
        replay(simpleType);

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.getEnums()).andReturn(new IFacet[] { facet }).atLeastOnce();
        simpleTypesController.deleteEnum(facet);
        replay(simpleTypesController);

        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();
        replay(controller);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);

        section.getEnumsTableViewer().setInput(simpleType);
        section.getEnumsTableViewer().setSelection(new StructuredSelection(facet));

        final Event event = new Event();
        event.widget = section.getRemoveEnumsButton();
        section.getRemoveEnumsButton().notifyListeners(SWT.Selection, event);

        verify(controller);
        verify(simpleTypesController);
    }

    private class SimpleTypeConstraintsSectionExpose extends SimpleTypeConstraintsSection {

        public SimpleTypeConstraintsSectionExpose(final ElementNodeDetailsController detailsController,
                final FormToolkit toolkit, final IManagedForm managedForm) {
            super(detailsController, toolkit, managedForm);
        }

        public CCombo getWhitespaceControl() {
            return whitespaceControl.getControl();
        }

        public String getWhitespaceComboItemLabel(final int position) {
            return ((Object) whitespaceComboItems[position]).toString();
        }

        public Text getLengthControl() {
            return lengthControl.getControl();
        }

        public Text getMinLengthControl() {
            return minLengthControl.getControl();
        }

        public Text getMaxLengthControl() {
            return maxLengthControl.getControl();
        }

        public Text getMinInclusiveControl() {
            return minInclusiveControl.getControl();
        }

        public Text getMaxInclusiveControl() {
            return maxInclusiveControl.getControl();
        }

        public Text getMinExclusiveControl() {
            return minExclusiveControl.getControl();
        }

        public Text getMaxExclusiveControl() {
            return maxExclusiveControl.getControl();
        }

        public Text getTotalDigitsControl() {
            return totalDigitsControl.getControl();
        }

        public Text getFractionDigitsControl() {
            return fractionDigitsControl.getControl();
        }

        public Composite getPatternsControl() {
            return patternsControl.getControl();
        }

        public Composite getEnumsControl() {
            return enumsControl.getControl();
        }

        @Override
        public IModelObject getModelObject() {
            return super.getModelObject();
        }
        
        public FacetTable getPatternsControl_facetTable() {
            return patternsControl_facetTable;
        }
        
        public FacetTable getEnumsControl_facetTable() {
            return enumsControl_facetTable;
        }

        public Control getAddPatternButton() {
            return patternsControl_facetTable.getAddPatternButton();
        }

        public Control getRemovePatternButton() {
            return patternsControl_facetTable.getRemovePatternButton();
        }

        public Control getAddEnumsButton() {
            return enumsControl_facetTable.getAddPatternButton();
        }

        public Control getRemoveEnumsButton() {
            return enumsControl_facetTable.getRemovePatternButton();
        }

        public TableViewer getPatternsTableViewer() {
            return patternsTableViewer;
        }

        public TableViewer getEnumsTableViewer() {
            return enumsTableViewer;
        }

        public boolean isVisible() {
            return getControl().isVisible();
        }
    }

    /**
     * Ensures that the section will not be visible when there are no applicable
     * Facets
     */
    @Test
    public void testSectionVisibleWhenNoApplicableFacets() {

        final SimpleTypeConstraintsController simpleTypesController = createNiceMock(SimpleTypeConstraintsController.class);
        expect(simpleTypesController.isLengthVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isMinMaxVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isMinMaxExclusiveVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isMinMaxInclusiveVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isTotalDigitsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isFractionDigitsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isWhitespaceVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isPatternsVisible()).andReturn(false).atLeastOnce();
        expect(simpleTypesController.isEnumsVisible()).andReturn(false).atLeastOnce();

        expect(controller.isConstraintsSectionApplicable()).andReturn(true).atLeastOnce();
        expect(controller.getConstraintsController()).andReturn(simpleTypesController).atLeastOnce();

        replay(controller, simpleTypesController);

        section = new SimpleTypeConstraintsSectionExpose(controller, toolkit, managedForm);
        section.createContents(shell);
        section.refresh();

        Assert.assertFalse(section.isVisible());

        verify(controller, simpleTypesController);
    }
}
