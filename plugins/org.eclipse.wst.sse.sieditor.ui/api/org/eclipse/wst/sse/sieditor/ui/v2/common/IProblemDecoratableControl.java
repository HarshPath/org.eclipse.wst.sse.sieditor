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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Defines a UI control capable of decorating itself with icon and message. Used
 * by the UI controls in the Service Interface editor pages to show the
 * validation errors and warnings.
 * 
 * 
 */
public interface IProblemDecoratableControl {

    public static final Image ERROR_IMAGE = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_ERROR).getImage();
    public static final Image WARNING_IMAGE = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_WARNING).getImage();
    public static final Image INFO_IMAGE = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_INFORMATION).getImage();
    
    /**
     * Decorates the UI control
     * 
     * @param severity
     *            the severity of the problem (See {@link IStatus} severities).
     *            The control should show the appropriate icon
     * @param message
     *            the message to be shown to the user. Could be
     *            <code>null</code> - in this case no message is available.
     */
    void decorate(int severity, String message);
    
    /**
     * Get set decorate severity by {@link #decorate(int, String)}
     * @return set severity on Status.OK if not set.
     */
    int getDecorateSeverity();
}
