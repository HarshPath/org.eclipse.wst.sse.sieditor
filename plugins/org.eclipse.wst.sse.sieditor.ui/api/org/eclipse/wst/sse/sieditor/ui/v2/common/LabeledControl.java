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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LabeledControl<T extends Control> extends AbstractProblemDecoratableControl {

    private Label label;

    private T control;

    private final Composite parent;

    private final FormToolkit toolkit;

    public LabeledControl(FormToolkit toolkit, Composite parent, String labelString) {
        this.toolkit = toolkit;
        this.parent = parent;
        label = toolkit.createLabel(parent, labelString);
        label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(label);
    }

    public void setControl(T control) {
        this.control = control;
        createDecoration(control);
    }

    public T getControl() {
        return control;
    }

    public Label getLabel() {
        return label;
    }

    // public Label getErrorLabel() {
    // return errorLabel;
    // }

    public void setEnabled(boolean enabled) {
        setCustomEnabled(enabled);
    }

    public boolean setVisible(boolean visible) {
        boolean changed = false;
        if (label.getVisible() != visible) {
            label.setVisible(visible);
            handleGridLayout(label, visible);
            changed = true;
        }

        if (control.getVisible() != visible) {
            control.setVisible(visible);
            handleGridLayout(control, visible);
            changed = true;
        }

        // handleGridLayout(errorLabel, visible);

        return changed;
    }

    // protected Label createErrorLabel() {
    // Label label = toolkit.createLabel(parent, UIConstants.EMPTY_STRING,
    // SWT.NONE);
    // label.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
    // label.setVisible(false);
    // return label;
    // }

    private void handleGridLayout(Control control, boolean visible) {
        Object layoutData = control.getLayoutData();
        if (layoutData instanceof GridData) {
            ((GridData) layoutData).exclude = !visible;
        }
    }

    /**
     * Sets all the controls in the hierarchy enabled or editable for Text
     * controls
     * 
     * @param editable
     */
    private void setCustomEnabled(boolean editable) {
        ArrayList<Control> flatList = new ArrayList<Control>();
        flatList.add(control);
        int index = 0;
        while (index < flatList.size()) {
            Control next = flatList.get(index);
            if (next instanceof Composite && ((Composite) next).getChildren().length != 0) {
                Collections.addAll(flatList, ((Composite) next).getChildren());
            } else {
                setControlReadOnly(editable, next);
            }
            index++;
        }
    }

    private void setControlReadOnly(boolean editable, Control control) {
        if (control instanceof Text) {
            ((Text) control).setEditable(editable);
        } else {
            control.setEnabled(editable);
        }
    }

    public boolean isVisible() {
        return label.getVisible() || control.getVisible();
    }
}
