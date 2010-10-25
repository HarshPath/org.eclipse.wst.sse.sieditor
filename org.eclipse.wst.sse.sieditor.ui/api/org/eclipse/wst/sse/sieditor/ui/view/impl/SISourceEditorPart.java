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
package org.eclipse.wst.sse.sieditor.ui.view.impl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xsd.ui.internal.adt.editor.CommonSelectionManager;

import org.eclipse.wst.sse.sieditor.command.common.SaveHandler;
import org.eclipse.wst.sse.sieditor.model.utils.HiddenErrorStatus;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.view.ISIEditorPart;

public class SISourceEditorPart extends StructuredTextEditor implements ISIEditorPart, SaveHandler {
    private static final String SOURCE_PAGE_TEXT = Messages.SISourceEditorPart_source_tab_title;

    public static final String SI_SOURCE_EDITOR_PART_ID = "si.source.editorpart"; //$NON-NLS-1$

    private CommonSelectionManager selectionProvider;

    public SISourceEditorPart() {
        super();
    }

    public String getId() {
        return SI_SOURCE_EDITOR_PART_ID;
    }

    public IEditorSite createSite(MultiPageEditorPart part) {
        return new MultiPageEditorSite(part, this) {
            public String getId() {
                // sets this id so the nested editor is considered xml source
                // page
                return ContentTypeIdForXML.ContentTypeID_XML + ".source"; //$NON-NLS-1$;
            }
        };
    }

    public void initPart(IEditorInput input, MultiPageEditorPart part) {
        if (part == null) {
            throw new IllegalArgumentException("Editor part cannot be null"); //$NON-NLS-1$
        }

        if (input == null) {
            return;
        }

        update();
        setEditorPart(part);

        final CommonSelectionManager selectionManager = getSelectionManager(part);

        // selectionManager.setSelection(new
        // StructuredSelection(getInternalModel()));

        IEditorSite editorSite = getEditorSite();
        if (editorSite == null) {
            editorSite = createSite(part);
        }

        editorSite.setSelectionProvider(selectionManager);
    }

    public CommonSelectionManager getSelectionManager(MultiPageEditorPart part) {
        if (selectionProvider == null) {
            selectionProvider = new CommonSelectionManager(part);
        }

        return selectionProvider;
    }

    public String getPageText() {
        return SOURCE_PAGE_TEXT;
    }

    public IStatus save(IProgressMonitor monitor) {
        IStatus status = Status.OK_STATUS;

        if (getEditorInput() instanceof IFileEditorInput) {
            IFileEditorInput input = (IFileEditorInput) getEditorInput();
            IFile[] workSpaceFile = new IFile[] { input.getFile() };
            if (workSpaceFile[0] != null) {
                ResourcesPlugin.getWorkspace().validateEdit(workSpaceFile, IWorkspace.VALIDATE_PROMPT);
                if (workSpaceFile[0].isReadOnly()) {
                    status = HiddenErrorStatus.getInstance();
                }
            }
        }

        if (status.isOK()) {
            doSave(monitor);
            return Status.OK_STATUS;
        }

        return status;
    }
}