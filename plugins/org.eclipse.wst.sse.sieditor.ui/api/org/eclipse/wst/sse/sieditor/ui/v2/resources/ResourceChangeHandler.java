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
 *    Vasil Vasilev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;
import org.eclipse.wst.sse.sieditor.ui.Activator;

/**
 * The purpose of this class is to provide refresh functionality for file editor
 * input editors
 * 
 *  Two types of events are handled: 1. Project close 2. Change
 *         of the edited resource from outside
 */
public class ResourceChangeHandler implements IResourceChangeListener, IResourceDeltaVisitor {

    private static ResourceChangeHandler resourceChangeHandler;

    private Set<AbstractEditorWithSourcePage> registeredEditors = new HashSet<AbstractEditorWithSourcePage>();

    public static ResourceChangeHandler getInstance() {
        if (resourceChangeHandler == null) {
            resourceChangeHandler = new ResourceChangeHandler();
        }
        return resourceChangeHandler;
    }

    private ResourceChangeHandler() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    public void registerEditor(AbstractEditorWithSourcePage editor) {
        if (editor.getEditorInput() instanceof IFileEditorInput) {
            registeredEditors.add(editor);
        }
    }

    public void deregisterEditor(AbstractEditorWithSourcePage editor) {
        registeredEditors.remove(editor);
    }

    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    for (AbstractEditorWithSourcePage editor : registeredEditors) {
                        final IWorkbenchPage[] pages = editor.getSite().getWorkbenchWindow().getPages();
                        for (int i = 0; i < pages.length; i++) {
                            final IEditorInput input = editor.getEditorInput();
                            if (((FileEditorInput) input).getFile().getProject().equals(event.getResource())) {
                                final IEditorPart editorPart = pages[i].findEditor(input);
                                if (editorPart != null) {
                                    pages[i].closeEditor(editorPart, true);
                                }
                            }
                        }
                    }
                }
            });
            return;
        }
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            try {
                event.getDelta().accept(this);
            } catch (CoreException e) {
                Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Resource changed visitor failed to accept delta.", e); //$NON-NLS-1$
            }

        }
    }

    public boolean visit(IResourceDelta delta) throws CoreException {
        final int flags = delta.getFlags();

        // see ResourceFileBuffer.resourceChanged for more resource change types
        if (delta.getKind() == IResourceDelta.REMOVED) {
            if ((IResourceDelta.MOVED_TO & flags) != 0) {
                handleResourceMoved(delta.getResource(), delta.getMovedToPath());
                return true;
            } else {
                return false;
            }
        }

        // if the resource content nor markers have changed - proceed with it's
        // children
        if ((flags & (IResourceDelta.CONTENT | IResourceDelta.MARKERS)) == 0) {
            return true;
        }
        if ((flags & IResourceDelta.MARKERS) != 0) {
            return true;
        }
        final IResource resource = delta.getResource();
        // if resource is not an IFile - proceed with children
        if (!(resource instanceof IFile)) {
            return true;
        }

        Set<AbstractEditorWithSourcePage> foundEditors = findEditors((IFile) resource);

        if (foundEditors.size() == 0) {
            return true;
        }
        if ((flags & IResourceDelta.CONTENT) != 0) {
            final List<AbstractEditorWithSourcePage> editorsToRefresh = new ArrayList<AbstractEditorWithSourcePage>();
            for (AbstractEditorWithSourcePage editor : foundEditors) {
                if (editor.isSaving()) {
                    continue;
                }
                editorsToRefresh.add(editor);
            }
            
            Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					for (AbstractEditorWithSourcePage editor : editorsToRefresh) {
						if (!editor.isDirty()) {
							editor.revertContentsToSavedVersion();
						}
					}
				}
            });
        }

        return false;
    }

    private void handleResourceMoved(IResource resource, IPath newPath) {
        if (resource instanceof IFile) {

            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IFile newFile = workspace.getRoot().getFile(newPath);
            final FileEditorInput newInput = new FileEditorInput(newFile);

            Set<AbstractEditorWithSourcePage> editors = findEditors((IFile) resource);

            for (final AbstractEditorWithSourcePage editor : editors) {

                editor.reloadModel(newInput);

            }

        }
    }

    private Set<AbstractEditorWithSourcePage> findEditors(IFile file) {
        Set<AbstractEditorWithSourcePage> result = new HashSet<AbstractEditorWithSourcePage>();
        for (AbstractEditorWithSourcePage editor : registeredEditors) {
            if (file.equals(((IFileEditorInput) editor.getEditorInput()).getFile())) {
                result.add(editor);
            }
        }

        return result;
    }
}
