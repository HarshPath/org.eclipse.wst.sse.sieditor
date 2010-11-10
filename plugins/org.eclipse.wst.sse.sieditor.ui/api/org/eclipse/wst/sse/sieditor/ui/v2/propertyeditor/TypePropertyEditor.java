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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractProblemDecoratableControl;
import org.eclipse.wst.sse.sieditor.ui.v2.common.CarriageReturnListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorBrowseButtonSelectionListener;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorNewButtonSelectionListener;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorTypeComboEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLLabelProvider;

public abstract class TypePropertyEditor extends AbstractProblemDecoratableControl {

    private static final int NUMBER_OF_COLUMNS_PER_ROW = 4;

    private final AbstractFormPageController formPageController;

    protected CCombo typeCombo;

    private ITreeNode input;

    // private Label typeErrorLabel;

    private final ITypeDisplayer typeDisplayer;

    protected boolean isTypeDirty;

    protected IManagedForm managedForm;

    protected Hyperlink typeLink;

    private Button browseButton;

    private Button newButton;

    private IType selectedType;

    private String selectedTypeName;

    protected String typeDialogDisplayText = Messages.TypePropertyEditor_type_dialog_text_set_type;

    protected boolean showComplexTypes = true;

    public TypePropertyEditor(final AbstractFormPageController controller, final ITypeDisplayer typeDisplayer) {
        this.formPageController = controller;
        this.typeDisplayer = typeDisplayer;
    }

    public void initialize(final IManagedForm form) {
        managedForm = form;
    }

    public Control createControl(final FormToolkit toolkit, final Composite parent) {

        typeLink = toolkit.createHyperlink(parent, Messages.TypePropertyEditor_type_label, SWT.NONE);
        typeCombo = Activator.getDefault().createCCombo(toolkit, parent, SWT.DROP_DOWN | SWT.FLAT | SWT.READ_ONLY);

        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        typeCombo.setLayoutData(gridData);
        typeCombo.setEnabled(!getFormPageController().isResourceReadOnly());
        typeCombo.setItems(getFormPageController().getCommonTypesDropDownList());

        typeCombo.addFocusListener(new TypePropertyEditorTypeComboEventListener(this));
        typeCombo.addKeyListener(new CarriageReturnListener());

        createDecoration(typeCombo);

        browseButton = toolkit.createButton(parent, Messages.TypePropertyEditor_browse_button, SWT.PUSH | SWT.FLAT);
        browseButton.setEnabled(!getFormPageController().isResourceReadOnly());
        browseButton.addSelectionListener(new TypePropertyEditorBrowseButtonSelectionListener(this));

        newButton = toolkit.createButton(parent, Messages.TypePropertyEditor_new_button, SWT.PUSH | SWT.FLAT);
        newButton.setEnabled(!getFormPageController().isResourceReadOnly());
        newButton.addSelectionListener(new TypePropertyEditorNewButtonSelectionListener(this));

        if (typeDisplayer != null) {
            typeLink.addHyperlinkListener(new HyperlinkAdapter() {
                @Override
                public void linkActivated(final HyperlinkEvent e) {
                    if (!TypePropertyEditor.this.getFormPageController().isPartOfEdittedDocument(
                            TypePropertyEditor.this.selectedType)) {
                        TypePropertyEditor.this.getFormPageController().openInNewEditor(TypePropertyEditor.this.selectedType);
                    } else {
                        update();
                        typeDisplayer.showType(getType());
                    }
                }
            });
        }
        return browseButton;
    }

    protected abstract IType getType();

    public abstract ITypeDialogStrategy createNewTypeDialogStrategy();

    public abstract ITypeCommitter getTypeCommitter();

    public void update() {
        setSelectedType(getType());

        setSelectedTypeName(WSDLLabelProvider.getTypeDisplayText(getSelectedType(), getInput()));
        if (getSelectedTypeName() != null) {
            typeCombo.setText(getSelectedTypeName().trim());
        }

        final IModelObject inputModelObject = input.getModelObject();
        IModelObject faultParameter = null;
        if (inputModelObject instanceof IFault) {
            final Collection<IParameter> faultParameters = ((IFault) inputModelObject).getParameters();
            faultParameter = faultParameters.isEmpty() ? null : faultParameters.iterator().next();
        }
        final boolean isEditable = !getFormPageController().isResourceReadOnly() && (input != null && !input.isReadOnly())
                && (faultParameter == null || getFormPageController().isPartOfEdittedDocument(faultParameter));

        typeLink.setEnabled(canNavigateToType()
                && (isEditable || !getFormPageController().isPartOfEdittedDocument(
                        faultParameter == null ? inputModelObject : faultParameter)));

        typeCombo.setEnabled(isEditable);
        browseButton.setEnabled(isEditable);
        newButton.setEnabled(isEditable);
        isTypeDirty = false;
        managedForm.dirtyStateChanged();
    }

    protected boolean canNavigateToType() {
        final IType selectedType = getSelectedType();
        return (selectedType == null) ? false : !(EmfXsdUtils.getSchemaForSchemaNS().equals(selectedType.getNamespace())
                || selectedType.isAnonymous() || UnresolvedType.instance().equals(selectedType));
    }

    public boolean isDirty() {
        return isTypeDirty;
    }

    public boolean isStale() {
        final IType type = getType();
        if (type == null) {
            return true;
        }
        if (type instanceof IStructureType && ((IStructureType) type).isElement()) {
            return !(type.equals(getSelectedType()) && type.getName().equals(getSelectedTypeName()));
        }
        return !(type.equals(getSelectedType()));
    }

    public int getNumberOfColumns() {
        return NUMBER_OF_COLUMNS_PER_ROW;
    }

    public void setLinkTitle(final String title) {
        typeLink.setText(title);
    }

    public boolean setVisible(final boolean visible) {
        if (visible == typeLink.getVisible()) {
            return false;
        }

        typeLink.setVisible(visible);
        typeCombo.setVisible(visible);
        browseButton.setVisible(visible);
        newButton.setVisible(visible);

        final Composite parent = typeLink.getParent();
        if (parent.getLayout() instanceof GridLayout) {
            ((GridData) typeLink.getLayoutData()).exclude = !visible;
            // ((GridData) typeErrorLabel.getLayoutData()).exclude = !visible;
            ((GridData) typeCombo.getLayoutData()).exclude = !visible;
            ((GridData) browseButton.getLayoutData()).exclude = !visible;
            ((GridData) newButton.getLayoutData()).exclude = !visible;
        }

        return true;
    }

    // ===========================================================
    // helpers
    // ===========================================================

    public CCombo getTypeCombo() {
        return typeCombo;
    }

    public boolean showComplexTypes() {
        return showComplexTypes;
    }

    public void setInput(final ITreeNode input) {
        this.input = input;
    }

    public ITreeNode getInput() {
        return input;
    }

    public String getTypeDialogDisplayText() {
        return typeDialogDisplayText;
    }

    public IType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(final IType selectedType) {
        this.selectedType = selectedType;
    }

    public String getSelectedTypeName() {
        return selectedTypeName;
    }

    public void setSelectedTypeName(final String selectedTypeName) {
        this.selectedTypeName = selectedTypeName;
    }

    public AbstractFormPageController getFormPageController() {
        return formPageController;
    }

}
