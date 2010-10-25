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
package org.eclipse.wst.sse.sieditor.ui.v2.input;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.StorageDocumentProvider;

public class WritableStorageDocumentProvider extends StorageDocumentProvider {

    @Override
    protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument textDocument, boolean overwrite)
            throws CoreException {
        if (!(element instanceof IStorageEditorInput)) {
            throw new RuntimeException("Only editor inputs of type IStorageEditorInput are supported"); //$NON-NLS-1$
        }
        IWritableStorageEditorInput input = (IWritableStorageEditorInput) element;
        IWritableStorage storage = input.getStorage();

        String contents = textDocument.get();

        // TODO: take into account encoding
        try {
            storage.setContents(new ByteArrayInputStream(contents.getBytes("UTF-8")), monitor); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
