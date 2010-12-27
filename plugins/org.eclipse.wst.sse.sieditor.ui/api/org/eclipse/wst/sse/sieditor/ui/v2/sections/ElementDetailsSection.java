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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.xsd.XSDPackage;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.common.LabeledControl;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.BaseTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

public class ElementDetailsSection extends AbstractDetailsPageSection implements ModifyListener, FocusListener, SelectionListener {

    protected LabeledControl<Text> nameControl;
    protected LabeledControl<Hyperlink> refControl;
    protected LabeledControl<CCombo> cardinalityControl;
    protected LabeledControl<Button> nillableControl;

    protected ElementTypeEditor typeEditor;

    protected BaseTypeEditor baseTypeEditor;

    private final Set<Object> dirtyControls;

    private final ElementNodeDetailsController detailsController;

    final ITypeDisplayer typeDisplayer;

    public ElementDetailsSection(final ElementNodeDetailsController detailsController, final FormToolkit toolkit,
            final IManagedForm managedForm, final ITypeDisplayer typeDisplayer) {
        super(detailsController.getFormPageController(), toolkit, managedForm);
        this.detailsController = detailsController;
        this.typeDisplayer = typeDisplayer;
        dirtyControls = new HashSet<Object>();
        typeEditor = new ElementTypeEditor(detailsController, typeDisplayer);
        typeEditor.initialize(managedForm);
        baseTypeEditor = new BaseTypeEditor((AbstractFormPageController) detailsController.getFormPageController(), typeDisplayer, detailsController);
        baseTypeEditor.initialize(managedForm);
        typeEditor.setDetailsController(detailsController);
    }

    @Override
    public void createContents(final Composite parent) {
        final FormToolkit toolkit = getToolkit();
        control = createSection(parent, Messages.ElementDetailsSection_section_title);
        final Composite clientComposite = toolkit.createComposite(control);
        control.setClient(clientComposite);
        setCompositeLayout(clientComposite);

        createControls(toolkit, clientComposite);

        toolkit.paintBordersFor(clientComposite);
    }

    @Override
    protected int getColumnsCount() {
        return 4;
    }

    private void createControls(final FormToolkit toolkit, final Composite clientComposite) {
        refControl = new LabeledControl<Hyperlink>(toolkit, clientComposite, Messages.ElementDetailsSection_0);
        refControl.setControl(toolkit.createHyperlink(clientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));

        refControl.getControl().addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(final HyperlinkEvent e) {
                final IModelObject modelObject = getNode().getModelObject();

                if (modelObject instanceof IElement) {
                    final IElement element = (IElement) modelObject;
                    typeDisplayer.showType(element.getType());
                }
            }
        });
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).span(3, 1).applyTo(
                refControl.getControl());

        nameControl = new LabeledControl<Text>(toolkit, clientComposite, Messages.StructureDetailsSection_name_label);
        nameControl.setControl(toolkit.createText(clientComposite, UIConstants.EMPTY_STRING, SWT.SINGLE));

        final Text nameText = nameControl.getControl();
        nameText.addModifyListener(this);
        nameText.addFocusListener(this);
        nameText.addKeyListener(CarriageReturnListener.getInstance());
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(nameText);

        typeEditor.createControl(toolkit, clientComposite);

        baseTypeEditor.createControl(toolkit, clientComposite);
        baseTypeEditor.setLinkTitle(Messages.ElementDetailsSection_baseType);

        cardinalityControl = new LabeledControl<CCombo>(toolkit, clientComposite,
                Messages.ElementDetailsSection_cardinality_label);
        cardinalityControl.setControl(Activator.getDefault().createCCombo(toolkit, clientComposite,
                SWT.DROP_DOWN | SWT.FLAT | SWT.READ_ONLY));
        final CCombo cardinalityComboBox = cardinalityControl.getControl();
        cardinalityComboBox.addFocusListener(this);
        cardinalityComboBox.addKeyListener(CarriageReturnListener.getInstance());
        cardinalityComboBox.add(CardinalityType.ZERO_TO_ONE.toString());
        cardinalityComboBox.add(CardinalityType.ONE_TO_ONE.toString());
        cardinalityComboBox.add(CardinalityType.ZERO_TO_MANY.toString());
        cardinalityComboBox.add(CardinalityType.ONE_TO_MANY.toString());
        GridDataFactory.swtDefaults().grab(true, false).span(3, 1).applyTo(cardinalityComboBox);

        nillableControl = new LabeledControl<Button>(toolkit, clientComposite, Messages.ElementDetailsSection_nillable_label);
        nillableControl.setControl(toolkit.createButton(clientComposite, null, SWT.CHECK));
        final Button nillableCheckBox = nillableControl.getControl();
        nillableCheckBox.addSelectionListener(this);
        nillableCheckBox.addKeyListener(CarriageReturnListener.getInstance());
        GridDataFactory.swtDefaults().grab(true, false).span(3, 1).applyTo(nillableCheckBox);

        cardinalityComboBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setDirty(cardinalityComboBox, true);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                widgetSelected(e);
            }
        });

        cardinalityComboBox.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(final FocusEvent e) {
                if (isDirty(cardinalityComboBox)) {
                    detailsController.setCardinality(getSelectedCardinalityType());
                    setDirty(e.getSource(), false);
                    dirtyStateChanged();
                    setDirty(cardinalityComboBox, false);
                }
            }

            @Override
            public void focusGained(final FocusEvent e) {
            }
        });
    }

    private CardinalityType getSelectedCardinalityType() {
        final CCombo cardinalityComboBox = cardinalityControl.getControl();
        final int selectedIndex = cardinalityComboBox.getSelectionIndex();
        if (selectedIndex >= 0) {
            final String text = cardinalityComboBox.getItem(selectedIndex);
            for (final CardinalityType ct : CardinalityType.values()) {
                if (ct.toString().equals(text)) {
                    return ct;
                }
            }
        }
        return null;
    }

    private Object refreshInput = null;

    @Override
    public void refresh() {
        cleanDirtyStates();
        detailsController.setInput(node);

        final boolean isNodeReference = EmfXsdUtils.isReference(node.getModelObject());
        final boolean editable = isEditable();
        boolean cardinalityEditable = true;
        if (!editable) {
            cardinalityEditable = isWritableElementReference();
        }

        if (isNodeReference) {
            nameControl.setVisible(false);
            refControl.setVisible(true);
            refControl.getControl().setText(detailsController.getName());

            final IType type = detailsController.getType();
            refControl.setEnabled(type != null && !UnresolvedType.instance().equals(type));
            refControl.getControl().pack();
            refControl.getControl().getParent().redraw();

            getProblemDecorator().bind(XSDPackage.Literals.XSD_NAMED_COMPONENT__NAME, refControl);

        } else {
            refControl.setVisible(false);
            nameControl.setVisible(true);

            nameControl.getControl().setText(detailsController.getName());
            nameControl.getControl().setEditable(editable && detailsController.isNameEditable());

            getProblemDecorator().bind(XSDPackage.Literals.XSD_NAMED_COMPONENT__NAME, nameControl);
        }
        nillableControl.getControl().setSelection(detailsController.isNillable());

        final CardinalityType cardinality = detailsController.getCardinality();
        if (cardinality != null) {
            cardinalityControl.getControl().setText(cardinality.toString());
            cardinalityControl.getControl().pack();
            cardinalityControl.getControl().getParent().redraw();
        } else {
            cardinalityControl.getControl().setText(UIConstants.EMPTY_STRING);
        }

        nillableControl.setEnabled(editable && detailsController.isNillableEditable());
        cardinalityControl.setEnabled(cardinalityEditable && detailsController.isCardinalityEditable());

        cardinalityControl.setVisible(detailsController.isCardinalityVisible());
        nillableControl.setVisible(detailsController.isNillableVisible());

        boolean applicable = detailsController.isTypeApplicable();
        if (applicable) {
            typeEditor.setInput(getNode());
            typeEditor.update();
        }
        typeEditor.setVisible(applicable);

        applicable = detailsController.isBaseTypeApplicable();
        if (applicable) {
            baseTypeEditor.setInput(getNode());
            baseTypeEditor.update();
        }
        baseTypeEditor.setVisible(applicable);

        if (refreshInput != null && node != null && refreshInput != node
        // &&
        // !refreshInput.getClass().getName().equals(node.getClass().getName())
        ) {
            redrawSection();
            refreshInput = null;
        } else {
            refreshInput = node;
        }

        getProblemDecorator().bind(XSDPackage.Literals.XSD_TYPE_DEFINITION__BASE_TYPE, baseTypeEditor);
        getProblemDecorator().bind(XSDPackage.Literals.XSD_TYPE_DEFINITION__ANNOTATION, typeEditor);
        getProblemDecorator().updateDecorations();
    }

    @Override
    public boolean isStale() {
        if (getModelObject() == null) {
            return false;
        }
        return true;
    }

    @Override
    protected Text getDefaultControl() {
        return nameControl.getControl();
    }

    public void modifyText(final ModifyEvent e) {
        final Object source = e.getSource();
        if (isDirty(source)) {
            return;
        }

        boolean isDirty = false;

        if (source == nameControl.getControl()) {
            isDirty = !nameControl.getControl().getText().trim().equals(detailsController.getName());
        }

        if (isDirty) {
            setDirty(source, true);
            dirtyStateChanged();
        }
    }

    public void focusGained(final FocusEvent e) {

    }

    public void focusLost(final FocusEvent e) {
        final Object source = e.getSource();

        boolean updated = true;
        if (source == nameControl.getControl() && isDirty(nameControl.getControl())) {
            detailsController.setName(nameControl.getControl().getText().trim());
        } else {
            updated = false;
        }

        if (updated) {
            setDirty(source, false);
            dirtyStateChanged();
        }
    }

    public void widgetDefaultSelected(final SelectionEvent e) {
    }

    public void widgetSelected(final SelectionEvent e) {
        final Object source = e.getSource();
        if (source == nillableControl.getControl()) {
            detailsController.setNillable(nillableControl.getControl().getSelection());
            setDirty(source, false);
            dirtyStateChanged();
        }
    }

    private boolean isDirty(final Object control) {
        return dirtyControls.contains(control);
    }

    private void setDirty(final Object control, final boolean dirty) {
        if (dirty) {
            dirtyControls.add(control);
        } else {
            dirtyControls.remove(control);
        }
    }

    private void cleanDirtyStates() {
        dirtyControls.clear();
    }

    @Override
    public boolean isDirty() {
        return !dirtyControls.isEmpty();
    }

}
