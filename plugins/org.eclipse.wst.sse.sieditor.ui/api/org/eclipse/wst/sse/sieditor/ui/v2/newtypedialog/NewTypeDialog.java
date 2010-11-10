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
package org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class NewTypeDialog extends Dialog {

    public static final String RADIO_SELECTION_NONE = "no.selection"; //$NON-NLS-1$
    public static final String RADIO_SELECTION_ELEMENT = "element"; //$NON-NLS-1$
    public static final String RADIO_SELECTION_SIMPLE_TYPE = "simple.type"; //$NON-NLS-1$
    public static final String RADIO_SELECTION_FAULT_ELEMENT = "fault.element"; //$NON-NLS-1$
    public static final String RADIO_SELECTION_STRUCTURE_TYPE = "structure.type"; //$NON-NLS-1$

    /**
     * Status returned on ok button press.
     */
    protected IStatus finishStatus;

    protected final ITypeDialogStrategy strategy;

    protected Text nameControl;
    protected ControlDecoration decoration;
    protected Button simpleTypeButton;
    protected Button structureTypeButton;
    protected Button elementButton;

    /**
     * the new type element type to create - element, simple.type,
     * structure.type
     */
    private String newTypeType;

    /**
     * the new type name to create
     */
    private String newTypeName;

    /**
     * flag determining if the user has modified the name field and if it is
     * being modified via the API not the user
     */
    protected boolean cleanFlag;
    /**
     * flag determining if the user has modified the name field and if it is
     * being modified via the API not the user
     */
    protected boolean externalNameModifyFlag;

    public NewTypeDialog(final Shell parentShell, final ITypeDialogStrategy strategy) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.strategy = strategy;
        finishStatus = Status.CANCEL_STATUS;
        cleanFlag = true;
        externalNameModifyFlag = true;
    }

    public IStatus createAndOpen() {
        create();
        getShell().setText(strategy.getDialogTitle());
        setBlockOnOpen(true);
        setDefaultName();
        final int dialogResult = open();
        if (OK == dialogResult) {
            return finishStatus;
        }
        return Status.CANCEL_STATUS;
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);

        createRadioComposite(dialogAreaComposite);
        createNameComposite(dialogAreaComposite);

        return dialogAreaComposite;
    }

    /**
     * Code creating and the composite above the name - allowing the user to
     * chose
     * 
     * @param parent
     */
    protected void createRadioComposite(final Composite parent) {
        final Group radioGroup = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        radioGroup.setLayoutData(gridData);

        radioGroup.setLayout(new GridLayout());
        radioGroup.setText(Messages.NewTypeDialog_group_title_type_category);
        elementButton = new Button(radioGroup, SWT.RADIO);
        structureTypeButton = new Button(radioGroup, SWT.RADIO);
        simpleTypeButton = new Button(radioGroup, SWT.RADIO);

        elementButton.setEnabled(strategy.isElementEnabled());
        structureTypeButton.setEnabled(strategy.isStructureTypeEnabled());
        simpleTypeButton.setEnabled(strategy.isSimpleTypeEnabled());

        final SelectionAdapter selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                radioSelected();
            }
        };
        simpleTypeButton.addSelectionListener(selectionListener);
        structureTypeButton.addSelectionListener(selectionListener);
        elementButton.addSelectionListener(selectionListener);

        simpleTypeButton.setText(Messages.NewTypeDialog_simple_type_radio_button);
        structureTypeButton.setText(Messages.NewTypeDialog_structure_type_radio_button);
        elementButton.setText(Messages.NewTypeDialog_element_radio_button);

        if (strategy.isElementEnabled()) {
            elementButton.setSelection(true);
            elementButton.setFocus();
        } else if (strategy.isStructureTypeEnabled()) {
            structureTypeButton.setSelection(true);
            structureTypeButton.setFocus();
        } else {
            simpleTypeButton.setSelection(true);
            simpleTypeButton.setFocus();
        }
    }

    /**
     * Code creating the composite containing the name control
     * 
     * @param parent
     */
    protected void createNameComposite(final Composite parent) {
        // create a composite to place the name text and label controls
        final Composite nameComposit = new Composite(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        nameComposit.setLayoutData(gridData);

        // set up the layout of the composite
        final GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 7;
        nameComposit.setLayout(layout);

        // create the label before the name text control
        final Label nameLabel = new Label(nameComposit, SWT.NONE);
        nameLabel.setText(Messages.NewTypeDialog_label_name);

        // create and set up the name text control
        nameControl = new Text(nameComposit, SWT.SINGLE | SWT.BORDER);
        final GridData NameGridData = new GridData();
        NameGridData.horizontalAlignment = GridData.FILL;
        NameGridData.grabExcessHorizontalSpace = true;
        NameGridData.widthHint = 200;
        nameControl.setLayoutData(NameGridData);
        nameControl.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                nameModified();
            }
        });

        // create the decoration for the name control
        decoration = new ControlDecoration(nameControl, SWT.LEFT | SWT.TOP);
        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                FieldDecorationRegistry.DEC_ERROR);
        decoration.setImage(fieldDecoration.getImage());
        decoration.hide();// just in case
    }

    protected void setDefaultName() {
        externalNameModifyFlag = false;
        nameControl.setText(strategy.getDefaultName(getSelectedComboType()));
        externalNameModifyFlag = true;
    }

    protected String getSelectedComboType() {
        if (elementButton.getSelection()) {
            return RADIO_SELECTION_ELEMENT;
        }
        if (structureTypeButton.getSelection()) {
            return RADIO_SELECTION_STRUCTURE_TYPE;
        }
        if (simpleTypeButton.getSelection()) {
            return RADIO_SELECTION_SIMPLE_TYPE;
        }
        return RADIO_SELECTION_NONE;
    }

    public void nameModified() {
        if (cleanFlag && externalNameModifyFlag) {
            cleanFlag = false;
        }
        validateAndMark();
    }

    protected void radioSelected() {
        if (cleanFlag) {
            // this will call the validation anyway
            setDefaultName();
        } else {
            validateAndMark();
        }
    }

    /**
     * Validates the text in the name control according to the strategy and xml
     * NCName rules. <br>
     * Sets the error marker and text if invalid, removes them if valid.
     */
    // TODO split this into two methods: validate() && mark()
    protected void validateAndMark() {
        // put code checking the selected radio button and validating name
        // controll content
        final List<String> errorMsgList = new ArrayList<String>();
        final String name = nameControl.getText();

        if (!EmfXsdUtils.isValidNCName(name)) {
            if (EmfXsdUtils.isValidNCName(name.trim())) {
                errorMsgList.add(Messages.NewTypeDialog_msg_error_invalid_name_whitespace);
            } else {
                errorMsgList.add(Messages.NewTypeDialog_msg_error_invalid_name);
            }
        }

        if (strategy.isDuplicateName(name, getSelectedComboType())) {
            errorMsgList.add(strategy.getDuplicateNameErrorMessage(getSelectedComboType()));
        }

        if (errorMsgList.isEmpty()) {
            decoration.hide();
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        } else {
            final StringBuffer message = new StringBuffer(Messages.NewTypeDialog_msg_error_header_invalid_name);
            for (final String string : errorMsgList) {
                message.append(Messages.NewTypeDialog_error_message_new_line);
                message.append(string);
            }
            decoration.setDescriptionText(message.toString());
            decoration.show();
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    @Override
    protected void okPressed() {
        newTypeName = nameControl.getText();
        newTypeType = getSelectedComboType();
        finishStatus = new Status(IStatus.OK, Activator.PLUGIN_ID, null);
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        newTypeName = null;
        newTypeType = null;
        super.cancelPressed();
    }

    // ===========================================================
    // getters
    // ===========================================================

    /**
     * @return the name of the type to create or <code>null</code> if the dialog
     *         was cancelled
     */
    public String getNewTypeName() {
        return newTypeName;
    }

    /**
     * @return the type of the type({@link #RADIO_SELECTION_ELEMENT},
     *         {@link #RADIO_SELECTION_SIMPLE_TYPE}, etc.) to create or
     *         <code>null</code> if the dialog was cancelled
     */
    public String getNewTypeType() {
        return newTypeType;
    }

}
