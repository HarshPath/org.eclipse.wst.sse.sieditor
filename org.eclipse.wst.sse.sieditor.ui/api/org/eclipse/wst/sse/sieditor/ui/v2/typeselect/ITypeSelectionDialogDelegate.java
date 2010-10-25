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
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListProvider;
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;

/**
 * Abstracts the type selection dialog from the Service Interface editor
 * controller ( {@link AbstractFormPageController}). The implementation should
 * delegate to the real implementation of the type selection dialog. This
 * interface allows the Type Selection functionality to be testable. Use the
 * default implementation - {@link TypeSelectionDialogDelegate} which uses the
 * standard Eclipse XSD type selection dialog.
 * <p>
 * Typically all methods of this interface should be called from the UI thread.
 * <p>
 * Typical lifecycle of this interface is:
 * <ul>
 * <li>create the dialog by calling method
 * {@link ITypeSelectionDialogDelegate#create(Shell, boolean, String)}
 * <li>open the dialog by calling method
 * {@link ITypeSelectionDialogDelegate#open())}
 * <li>inspect the selection results using the getSelected* methods
 * </ul>
 */
public interface ITypeSelectionDialogDelegate {

    /**
     * Creates the dialog. Typically the implementation will create the wrapped
     * dialog.
     * 
     * @param shell
     *            the shell
     * @param showComplexTypes
     *            <code>true</code> if complex types should be shown
     * @param displayText
     *            for example "Select Parameter Type" or "Select Element Type"
     *            depending on the usage scenario.
     * @param searchListProvder 
     *            the specific provider for the concrete selected node
     */
    void create(Shell shell, String displayText, IComponentSearchListProvider searchListProvder);

    /**
     * Opens the dialog and blocks the execution until it is closed. Returns
     * <code>true</code> if the dialog is closed via the OK button and
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the dialog is closed via the OK button and
     *         <code>false</code> otherwise.
     */
    boolean open();

    /**
     * Returns the selected object in the dialog. Could be instance of an XSD
     * type ({@link XSDNamedComponent}) if the type is an inline type or a
     * org.eclipse.wst.common.core.search.SearchMatch if the type belongs to
     * another, not imported schema. In the second case use the other
     * getSelection* methods to obtain the information needed to import and
     * resolve the type.
     * 
     * @return the selected object in the dialog
     */
    Object getSelectedObject();

    /**
     * Returns the name of the selected type
     * 
     * @return the name of the selected type
     */
    String getSelectedTypeName();

    /**
     * Returns the namespace of the selected type
     * 
     * @return the namespace of the selected type
     */
    String getSelectedTypeNamespace();

    /**
     * Returns the file in the workspace, which contains the definition of the
     * selected file.
     * 
     * @return the file in the workspace, which contains the definition of the
     *         selected file
     */
    IFile getSelectedTypeFile();
}
