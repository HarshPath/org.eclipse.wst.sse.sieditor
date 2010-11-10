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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners;

import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ITypeSelectionDialogDelegate;

/**
 * This is the selection listener for the {@link TypePropertyEditor} "Browse..."
 * button. This listener is responsible for the creation of the
 * {@link ITypeSelectionDialogDelegate} dialog and for the creation of the
 * selected type itself
 * 
 * 
 * 
 */
public class TypePropertyEditorBrowseButtonSelectionListener extends AbstractTypePropertyEditorEventListener {

    public TypePropertyEditorBrowseButtonSelectionListener(final TypePropertyEditor propertyEditor) {
        super(propertyEditor);
    }

    public void widgetSelected(final SelectionEvent e) {
        chooseType();
    }

    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

}
