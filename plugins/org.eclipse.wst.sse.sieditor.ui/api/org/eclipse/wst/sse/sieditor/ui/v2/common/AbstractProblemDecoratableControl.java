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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractProblemDecoratableControl implements IProblemDecoratableControl {
	
	private ControlDecoration decoration;
    
    private int decorationSeverity = IStatus.OK;

    protected void createDecoration(Control control) {
        decoration = new ControlDecoration(control, SWT.TOP | SWT.LEFT);
        decoration.setMarginWidth(2);
        decoration.setImage(INFO_IMAGE);
        decoration.hide();
    }

    @Override
    public void decorate(int severity, String message) {

        decoration.setDescriptionText(message);
        decorationSeverity = severity;

        Image img = null;

        switch (severity) {
        case IStatus.WARNING:
            img = WARNING_IMAGE;
            break;
        case IStatus.ERROR:
            img = ERROR_IMAGE;
            break;
        case IStatus.INFO:
            img = INFO_IMAGE;
        }
        
        if (img != null) {
            decoration.setImage(img);
            decoration.show();
        } else {
            decoration.hide();
        }
    }

    @Override
	public int getDecorateSeverity() {
		return decorationSeverity;
	}

}
