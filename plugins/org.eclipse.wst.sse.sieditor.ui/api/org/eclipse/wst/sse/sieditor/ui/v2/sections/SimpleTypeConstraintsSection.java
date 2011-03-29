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
package org.eclipse.wst.sse.sieditor.ui.v2.sections;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.FacetTable;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.editing.EnumsTableEditingSupport;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.editing.PatternsTableEditingSupport;
import org.eclipse.xsd.XSDPackage;

public class SimpleTypeConstraintsSection extends AbstractDetailsPageSection implements ModifyListener, FocusListener {

    private final ElementNodeDetailsController detailsController;

    protected TableViewer patternsTableViewer;

    protected TableViewer enumsTableViewer;

    protected LabeledControl<Text> lengthControl;

    protected LabeledControl<Text> minLengthControl;

    protected LabeledControl<Text> maxLengthControl;

    protected LabeledControl<Text> minInclusiveControl;

    protected LabeledControl<Text> maxInclusiveControl;

    protected LabeledControl<Text> minExclusiveControl;

    protected LabeledControl<Text> maxExclusiveControl;

    protected LabeledControl<Text> totalDigitsControl;

    protected LabeledControl<Text> fractionDigitsControl;

    protected LabeledControl<CCombo> whitespaceControl;

    protected LabeledControl<Composite> patternsControl;

    protected FacetTable patternsControl_facetTable;

    protected LabeledControl<Composite> enumsControl;

    protected FacetTable enumsControl_facetTable;

    protected WhitespaceComboItem[] whitespaceComboItems;

    protected Label unresolvedLabel;

    public SimpleTypeConstraintsSection(final ElementNodeDetailsController detailsController, final FormToolkit toolkit,
            final IManagedForm managedForm) {
        super(detailsController.getFormPageController(), toolkit, managedForm);
        this.detailsController = detailsController;
        whitespaceComboItems = new WhitespaceComboItem[] {
                new WhitespaceComboItem(null, Messages.SimpleTypeConstraintsSection_NOT_SPECIFIED_WHITESPACE),
                new WhitespaceComboItem(Whitespace.COLLAPSE, Messages.SimpleTypeConstraintsSection_collapse),
                new WhitespaceComboItem(Whitespace.PRESERVE, Messages.SimpleTypeConstraintsSection_preserve),
                new WhitespaceComboItem(Whitespace.REPLACE, Messages.SimpleTypeConstraintsSection_replace), };
    }

    @Override
    public void createContents(final Composite parent) {
        final FormToolkit toolkit = getToolkit();
        control = createSection(parent, Messages.SimpleTypeConstraintsSection_constraints);
        final Composite clientComposite = toolkit.createComposite(control);
        control.setClient(clientComposite);
        setCompositeLayout(clientComposite);

        createControls(toolkit, clientComposite);

        toolkit.paintBordersFor(clientComposite);
    }

    @Override
    public void refresh() {

        boolean applicable = detailsController.isConstraintsSectionApplicable();

        IConstraintsController constraintsController = null;

        boolean lengthVisible = false;
        boolean minMaxVisible = false;
        boolean minMaxInclusiveVisible = false;
        boolean minMaxExclusiveVisible = false;
        boolean totalDigitsVisible = false;
        boolean fractionDigitsVisible = false;
        boolean whitespaceVisible = false;
        boolean patternsVisible = false;
        boolean enumsVisible = false;

        // even if the constraints section is applicable, if no facet is
        // applicable then the section would be empty, so we hide it.
        // it happens when the base type is unresolved
        if (applicable) {

            constraintsController = detailsController.getConstraintsController();

            lengthVisible = constraintsController.isLengthVisible();
            minMaxVisible = constraintsController.isMinMaxVisible();
            minMaxExclusiveVisible = constraintsController.isMinMaxExclusiveVisible(); 
            minMaxInclusiveVisible = constraintsController.isMinMaxInclusiveVisible();
            totalDigitsVisible = constraintsController.isTotalDigitsVisible();
            fractionDigitsVisible = constraintsController.isFractionDigitsVisible();
            whitespaceVisible = constraintsController.isWhitespaceVisible();
            patternsVisible = constraintsController.isPatternsVisible();
            enumsVisible = constraintsController.isEnumsVisible();

            applicable = lengthVisible || minMaxVisible || minMaxInclusiveVisible || minMaxExclusiveVisible || totalDigitsVisible
                    || fractionDigitsVisible || whitespaceVisible || patternsVisible || enumsVisible;

        }

        setVisible(applicable);
        if (!applicable) {
            return;
        }

        boolean changed = false;

        final boolean constraintSectionEditable = constraintsController.isEditable();
        final boolean enabled = constraintSectionEditable && isEditable();

        // changed = showUnresolvableMessage(!resolvable)? true : changed;

        if (lengthVisible) {
            updateTextControl(lengthControl, constraintsController.getLength());
            lengthControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_LENGTH_FACET__VALUE, lengthControl);
        }
        changed = lengthControl.setVisible(lengthVisible) ? true : changed;

        if (minMaxVisible) {
            updateTextControl(minLengthControl, constraintsController.getMinLength());
            updateTextControl(maxLengthControl, constraintsController.getMaxLength());

            minLengthControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MIN_LENGTH_FACET__VALUE, minLengthControl);
            maxLengthControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MAX_LENGTH_FACET__VALUE, maxLengthControl);
        }

        changed = minLengthControl.setVisible(minMaxVisible) ? true : changed;
        changed = maxLengthControl.setVisible(minMaxVisible) ? true : changed;

        if (minMaxExclusiveVisible) {
            updateTextControl(minExclusiveControl, constraintsController.getMinExclusive());
            updateTextControl(maxExclusiveControl, constraintsController.getMaxExclusive());

            minExclusiveControl.getControl().setEnabled(enabled);
            maxExclusiveControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MIN_FACET__EXCLUSIVE, minExclusiveControl);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MAX_FACET__EXCLUSIVE, maxExclusiveControl);
        }
        
        if(minMaxInclusiveVisible){
        	updateTextControl(minInclusiveControl, constraintsController.getMinInclusive());
            updateTextControl(maxInclusiveControl, constraintsController.getMaxInclusive());
            minInclusiveControl.getControl().setEnabled(enabled);
            maxInclusiveControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MIN_FACET__INCLUSIVE, minInclusiveControl);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_MAX_FACET__INCLUSIVE, maxInclusiveControl);            
        }
        changed = minInclusiveControl.setVisible(minMaxInclusiveVisible) ? true : changed;
        changed = maxInclusiveControl.setVisible(minMaxInclusiveVisible) ? true : changed;
        changed = minExclusiveControl.setVisible(minMaxExclusiveVisible) ? true : changed;
        changed = maxExclusiveControl.setVisible(minMaxExclusiveVisible) ? true : changed;

        if (totalDigitsVisible) {
            updateTextControl(totalDigitsControl, constraintsController.getTotalDigits());
            totalDigitsControl.getControl().setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_TOTAL_DIGITS_FACET__VALUE, totalDigitsControl);
        }
        changed = totalDigitsControl.setVisible(totalDigitsVisible) ? true : changed;

        if (fractionDigitsVisible) {
            updateTextControl(fractionDigitsControl, constraintsController.getFractionDigits());
            fractionDigitsControl.getControl().setEnabled(enabled);
        }
        changed = fractionDigitsControl.setVisible(fractionDigitsVisible) ? true : changed;

        int horizontalSpan = 1;
        if (totalDigitsVisible && !fractionDigitsVisible) {
            horizontalSpan = 3;
        }
        final GridData totalDigitsGridData = (GridData) totalDigitsControl.getControl().getLayoutData();
        if (totalDigitsGridData.horizontalSpan != horizontalSpan) {
            totalDigitsGridData.horizontalSpan = horizontalSpan;
            changed = true;
        }

        if (whitespaceVisible) {
            final Whitespace whitespace = constraintsController.getWhitespace();
            for (int ndx = 0; ndx < whitespaceComboItems.length; ndx++) {
                final WhitespaceComboItem item = whitespaceComboItems[ndx];
                if (item.whitespace == whitespace) {
                    whitespaceControl.getControl().setText(item.label);
                }
            }
            whitespaceControl.setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_WHITE_SPACE_FACET__VALUE, whitespaceControl);
        }
        changed = whitespaceControl.setVisible(whitespaceVisible) ? true : changed;

        if (patternsVisible) {
            patternsTableViewer.setInput(constraintsController.getType());

            patternsControl.setEnabled(enabled);
            patternsControl_facetTable.setEnabled(enabled);
        }
        changed = patternsControl.setVisible(patternsVisible) ? true : changed;

        if (enumsVisible) {
            enumsTableViewer.setInput(constraintsController.getType());

            enumsControl.setEnabled(enabled);
            enumsControl_facetTable.setEnabled(enabled);
            getProblemDecorator().bind(XSDPackage.Literals.XSD_ENUMERATION_FACET__VALUE, enumsControl);
        }
        changed = enumsControl.setVisible(enumsVisible) ? true : changed;

        if (changed) {
            redrawSection();
        }

        getProblemDecorator().updateDecorations();
    }

//    private boolean showUnresolvableMessage(final boolean visible) {
//        // do nothing
//        return false;
//        // if (unresolvedLabel.getVisible() != visible) {
//        // unresolvedLabel.setVisible(!visible);
//        // Object layoutData = unresolvedLabel.getLayoutData();
//        // if (layoutData instanceof GridData) {
//        // ((GridData) layoutData).exclude = !visible;
//        // }
//        // return true;
//        // }
//        // return false;
//    }

    public void modifyText(final ModifyEvent e) {
        // TODO Auto-generated method stub

    }

    public void focusGained(final FocusEvent e) {
        // TODO Auto-generated method stub

    }

    public void focusLost(final FocusEvent e) {
        final Object source = e.getSource();
        final boolean updated = true;

        final IConstraintsController constraintsController = detailsController.getConstraintsController();

        if (source == lengthControl.getControl()) {
            constraintsController.setLength(lengthControl.getControl().getText());
        } else if (source == minLengthControl.getControl()) {
            constraintsController.setMinLength(minLengthControl.getControl().getText());
        } else if (source == maxLengthControl.getControl()) {
            constraintsController.setMaxLength(maxLengthControl.getControl().getText());
        } else if (source == minInclusiveControl.getControl()) {
            constraintsController.setMinInclusive(minInclusiveControl.getControl().getText());
        } else if (source == maxInclusiveControl.getControl()) {
            constraintsController.setMaxInclusive(maxInclusiveControl.getControl().getText());
        } else if (source == minExclusiveControl.getControl()) {
            constraintsController.setMinExclusive(minExclusiveControl.getControl().getText());
        } else if (source == maxExclusiveControl.getControl()) {
            constraintsController.setMaxExclusive(maxExclusiveControl.getControl().getText());
        } else if (source == whitespaceControl.getControl()) {
            constraintsController.setWhitespace(getSelectedWhitespace());
        } else if (source == totalDigitsControl.getControl()) {
            constraintsController.setTotalDigits(totalDigitsControl.getControl().getText());
        } else if (source == fractionDigitsControl.getControl()) {
            constraintsController.setFractionDigits(fractionDigitsControl.getControl().getText());
        }

        if (updated) {
            // setDirty(source, false);
            dirtyStateChanged();
        }
    }

    @Override
    protected int getColumnsCount() {
        return 4;
    }

    private void createControls(final FormToolkit toolkit, final Composite clientComposite) {
        final TextControlsCreator textControlsCreator = new TextControlsCreator(toolkit, clientComposite, this);
        createStringControls(textControlsCreator);
        createDigitsControls(textControlsCreator);
        createWhiteSpaceControl(toolkit, clientComposite);
        createPatternsControl(toolkit, clientComposite);
        createEnumsControl(toolkit, clientComposite);
    }

    /**
     * Utility method. Creates the string constraint section controls
     * 
     * @param textControlsCreator
     */
    private void createStringControls(final TextControlsCreator textControlsCreator) {
        lengthControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_length, false, true);
        minLengthControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_min_length, false, false);
        maxLengthControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_max_length, true, false);
    }

    /**
     * Utility method. Creates the digit constraint section controls
     * 
     * @param textControlsCreator
     */
    private void createDigitsControls(final TextControlsCreator textControlsCreator) {
        minInclusiveControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_min_inclusive, false, false);
        maxInclusiveControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_max_inclusive, true, false);
        minExclusiveControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_min_exclusive, false, false);
        maxExclusiveControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_max_exclusive, true, false);
        totalDigitsControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_total_digits, false, false);
        fractionDigitsControl = textControlsCreator.create(Messages.SimpleTypeConstraintsSection_fraction_digits, true, false);
    }

    /**
     * Utility method. Creates the whitespace constraint section controls
     * 
     * @param toolkit
     * @param clientComposite
     */
    private void createWhiteSpaceControl(final FormToolkit toolkit, final Composite clientComposite) {
        whitespaceControl = new LabeledControl<CCombo>(toolkit, clientComposite, Messages.SimpleTypeConstraintsSection_whitespace);
        whitespaceControl.setControl(Activator.getDefault().createCCombo(toolkit, clientComposite,
                SWT.DROP_DOWN | SWT.FLAT | SWT.READ_ONLY));
        final CCombo whitespaceCombo = whitespaceControl.getControl();
        for (final WhitespaceComboItem item : whitespaceComboItems) {
            whitespaceCombo.add(item.label);
        }
        whitespaceCombo.addFocusListener(this);
        whitespaceCombo.addKeyListener(CarriageReturnListener.getInstance());
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.LEFT, SWT.CENTER).span(3, 1).applyTo(whitespaceCombo);
    }

    /**
     * Utility method. Creates the patterns constraint section controls
     * 
     * @param toolkit
     * @param clientComposite
     */
    private void createPatternsControl(final FormToolkit toolkit, final Composite clientComposite) {
        patternsControl = new LabeledControl<Composite>(toolkit, clientComposite, Messages.SimpleTypeConstraintsSection_patterns);
        patternsControl.setControl(new Composite(clientComposite, SWT.NONE));
        final Composite patternsComposite = patternsControl.getControl();
        patternsControl_facetTable = new FacetTable(toolkit, patternsComposite, new PatternsTableContentProvider(),
                new FacetsTableLabelProvider());
        patternsControl_facetTable.setEditingSupport(new PatternsTableEditingSupport(patternsControl_facetTable.getTableViewer(),
                detailsController));
        patternsControl_facetTable.setAddHandler(new FacetTable.AddHandler() {
			public boolean addElement() {
				final InputDialog dlg = getInputDialog(null, Messages.SimpleTypeConstraintsSection_add_pattern_dlg_title,
				        Messages.SimpleTypeConstraintsSection_add_pattern_dlg_msg, null, createDefaultInputValidator());
				final int result = dlg.open();
				if (result == Window.OK) {
				    final String value = dlg.getValue().trim();
				    detailsController.getConstraintsController().addPattern(value);
				    return true;
				}
				return false;
			}
		});
        patternsControl_facetTable.setRemoveHandler(new FacetTable.RemoveHandler() {
			public void removeElement(final Object toRemove) {
				if (toRemove instanceof IFacet) {
				    detailsController.getConstraintsController().deletePattern((IFacet) toRemove);
				}
			}
		});
        patternsTableViewer = patternsControl_facetTable.getTableViewer();
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(
                patternsControl.getControl());
    }

    /**
     * Utility method. Creates the enumeration constraint section controls
     * 
     * @param toolkit
     * @param clientComposite
     */
    private void createEnumsControl(final FormToolkit toolkit, final Composite clientComposite) {
        enumsControl = new LabeledControl<Composite>(toolkit, clientComposite, Messages.SimpleTypeConstraintsSection_enums);
        enumsControl.setControl(new Composite(clientComposite, SWT.NONE));
        final Composite enumsComposite = enumsControl.getControl();
        enumsControl_facetTable = new FacetTable(toolkit, enumsComposite, new EnumsTableContentProvider(),
                new FacetsTableLabelProvider());
        enumsControl_facetTable.setEditingSupport(new EnumsTableEditingSupport(enumsControl_facetTable.getTableViewer(),
                detailsController));
        enumsControl_facetTable.setAddHandler(new FacetTable.AddHandler() {
			public boolean addElement() {
				final InputDialog dlg = getInputDialog(null, Messages.SimpleTypeConstraintsSection_add_enum_dlg_title,
				        Messages.SimpleTypeConstraintsSection_add_enum_dlg_msg, null, createDefaultInputValidator());
				final int result = dlg.open();
				if (result == Window.OK) {
				    final String value = dlg.getValue().trim();
				    detailsController.getConstraintsController().addEnum(value);
				    return true;
				}
				return false;
			}
		});
        enumsControl_facetTable.setRemoveHandler(new FacetTable.RemoveHandler() {
			public void removeElement(final Object toRemove) {
				final IStructuredSelection selection = (IStructuredSelection) enumsTableViewer.getSelection();
				final Object element = selection.getFirstElement();
				if (element instanceof IFacet) {
				    detailsController.getConstraintsController().deleteEnum((IFacet) element);
				}
			}
		});
        enumsTableViewer = enumsControl_facetTable.getTableViewer();
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(enumsControl.getControl());
    }

    @Override
    public boolean isStale() {
        return getModelObject() == null ? false : true;
    }

    protected InputDialog getInputDialog(final Shell parentShell, final String dialogTitle, final String dialogMessage,
            final String initialValue, final IInputValidator validator) {
        return new InputDialog(parentShell, dialogTitle, dialogMessage, initialValue, validator);
    }

    /**
     * Utility method. Creates default input validator - empty strings are
     * treated as invalid input
     * 
     * @return the created input validator
     */
    private IInputValidator createDefaultInputValidator() {
        return new IInputValidator() {
            @Override
            public String isValid(final String newText) {
                if (newText.trim().isEmpty()) {
                    // input invalid - return "error" message
                    return ""; //$NON-NLS-1$
                }
                return null;
            }
        };
    }

    private Whitespace getSelectedWhitespace() {
        final String text = whitespaceControl.getControl().getText();
        for (final WhitespaceComboItem item : whitespaceComboItems) {
            if (item.label.equals(text)) {
                return item.whitespace;
            }
        }
        return null;
    }

    private void updateTextControl(final LabeledControl<Text> control, final String value) {
        final Text text = control.getControl();
        text.setText(value);
    }

    private static class WhitespaceComboItem {
        private final Whitespace whitespace;
        private final String label;

        WhitespaceComboItem(final Whitespace whitespace, final String label) {
            this.whitespace = whitespace;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class TextControlsCreator {
        private final FormToolkit toolkit;
        private final Composite clientComposite;
        private final Point textDimensions;
        private final FocusListener focusListener;

        public TextControlsCreator(final FormToolkit toolkit, final Composite clientComposite, final FocusListener focusListener) {
            this.toolkit = toolkit;
            this.clientComposite = clientComposite;
            this.focusListener = focusListener;
            textDimensions = calculateTextDimensions(clientComposite);
        }

        public LabeledControl<Text> create(final String label, final boolean hasLeftIndent, final boolean spanRow) {
            // Length
            final LabeledControl<Text> labeledControl = new LabeledControl<Text>(toolkit, clientComposite, label);
            final Text text = toolkit.createText(clientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE | SWT.RIGHT);
            labeledControl.setControl(text);
            labeledControl.getControl().addFocusListener(focusListener);
            labeledControl.getControl().addKeyListener(CarriageReturnListener.getInstance());

            if (hasLeftIndent) {
                GridDataFactory.swtDefaults().indent(20, 0).applyTo(labeledControl.getLabel());
            }

            final GridDataFactory textGDF = GridDataFactory.swtDefaults();
            textGDF.hint(textDimensions);

            if (spanRow) {
                textGDF.grab(true, false).align(SWT.BEGINNING, SWT.CENTER).span(3, 1);
            }

            textGDF.applyTo(text);

            return labeledControl;
        }

        private Point calculateTextDimensions(final Composite parent) {
            final Text text = new Text(parent, SWT.BORDER);
            final int columns = 10;
            final GC gc = new GC(text);
            final FontMetrics fm = gc.getFontMetrics();
            final int width = columns * fm.getAverageCharWidth();
            final int height = fm.getHeight();
            gc.dispose();
            text.dispose();
            return new Point(width, height);
        }
    }

    private class PatternsTableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ISimpleType) {
                final IConstraintsController constraintsController = detailsController.getConstraintsController();
                return constraintsController.getPatterns();
            }
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    private class EnumsTableContentProvider extends PatternsTableContentProvider {
        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ISimpleType) {
                final IConstraintsController constraintsController = detailsController.getConstraintsController();
                return constraintsController.getEnums();
            }
            return null;
        }
    }

    private static class FacetsTableLabelProvider extends LabelProvider {
        @Override
        public String getText(final Object element) {
            return ((IFacet) element).getValue();
        }
    }

}
