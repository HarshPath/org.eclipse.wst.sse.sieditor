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
package org.eclipse.wst.sse.sieditor.ui.preedit;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.wst.sse.sieditor.core.common.IEditValidator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.HiddenErrorStatus;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;

public class EditValidator implements IEditValidator {

    private final AbstractEditorWithSourcePage editor;

    public EditValidator(AbstractEditorWithSourcePage editor) {
        this.editor = editor;
    }

    @Override
    public IStatus canEdit() {
        return canEdit(editor.getModelRoot());
    }

    protected IStatus canEdit(IFile workSpaceFile) {
        if (workSpaceFile != null) {
            getWorkspace().validateEdit(new IFile[] { workSpaceFile }, IWorkspace.VALIDATE_PROMPT);
            
            if (workSpaceFile.isReadOnly() || (!getWorkspace().isTreeLocked() && !editor.validateEditorInputState())) {
                return HiddenErrorStatus.getInstance();
            }
        }
        return Status.OK_STATUS;
    }

    private IStatus canEdit(IModelRoot root) {
        URI uri = null;
        IStatus status = Status.OK_STATUS;

        if (root instanceof IWsdlModelRoot) {
            uri = ((IWsdlModelRoot) root).getDescription().getContainingResource();
        } else if (root instanceof IXSDModelRoot) {
            uri = ((Schema) ((IXSDModelRoot) root).getSchema()).getContainingResource();
        }
        if (uri != null) {
            IPath path = new Path(uri.toString());
            IFile workSpaceFile = ResourceUtils.getWorkSpaceFile(path);
            status = canEdit(workSpaceFile);
        }
        return status;
    }
    
    protected IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }
}
