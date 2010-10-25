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
package org.eclipse.wst.sse.sieditor.ui;

import java.util.HashMap;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

/**
 * Editor Service class to get the active editor instance and the mode of the
 * editor
 * 
 */
public class SIEditorService {
    private static SIEditorService singletonInstance;
    private HashMap<IEditorInput, String> editorModeMap;
    private static String READ_ONLY = "readOnly"; //$NON-NLS-1$
    private static PartListener partListener;

    private SIEditorService() {
        editorModeMap = new HashMap<IEditorInput, String>();
    }

    public static SIEditorService getInstance() {
        if (null == singletonInstance)
            singletonInstance = new SIEditorService();
        return singletonInstance;
    }

   
    public boolean isEditorReadOnly(IEditorInput editorInput) {
        if (editorModeMap.containsKey(editorInput)) {
            String val = editorModeMap.get(editorInput);
            if (READ_ONLY.equalsIgnoreCase(val))
            // editorModeMap.clear(); //clear this entry after the result is
            // processed!
                return true;
        }
        return false;
    }

    private void editorClosed(IEditorInput editorInput) {
        if (editorInput != null)
            editorModeMap.remove(editorInput);
        if (editorModeMap.isEmpty()) {
            enablePartListener(false);
        }
    }

    private void setReadOnlyStatusMessage(IEditorPart editorPart) {
        if (editorPart != null) {
            IEditorInput editorInput = editorPart.getEditorInput();
            if (editorModeMap.containsKey(editorInput)) {
                IWorkbenchPartSite site = editorPart.getSite();
                if (site instanceof IEditorSite) {
                    IActionBars actionBars = ((IEditorSite) site).getActionBars();
                    if (actionBars == null)
                        return;

                    IStatusLineManager statusLineManager = actionBars.getStatusLineManager();

                    if (statusLineManager == null)
                        return;

                    statusLineManager.setMessage(Messages.SIEditorService_status_line_read_only);
                }
            }
        }
    }

    private void enablePartListener(boolean enable) {
        if (enable) {
            if (partListener == null) {
                partListener = new PartListener();
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(partListener);
            }
        } else {
            if (partListener != null) {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(partListener);
                partListener = null;
            }
        }
    }

    // public void openSIEditorReadOnly(IFile file) throws CoreException {
    // if(null!=file){
    // IEditorInput editorInput = new FileEditorInput(file);
    // IWorkbenchWindow window=
    // PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    // IWorkbenchPage page = window.getActivePage();
    // try{
    //				editorModeMap.put(editorInput, "readOnly"); //$NON-NLS-1$
    //				page.openEditor(editorInput, "org.eclipse.wst.sse.sieditor.ui.editor3"); //TODO change the hardcoding here //$NON-NLS-1$
    // }
    // catch(PartInitException e){
    // editorModeMap.clear(); //clear this entry in case of error
    // ErrorDialog.openError(window.getShell() ,
    // Messages.SI_ERROR_XTIT,
    // e.getLocalizedMessage(), e.getStatus());
    // }
    // }
    // }

    // GFB-POC open siEditor in read only mode
    public void openSIEditorReadOnly(final IEditorInput editorInput, String editorId, boolean readOnly) throws PartInitException {
        if (editorInput != null && editorId != null && !(UIConstants.EMPTY_STRING.equalsIgnoreCase(editorId))) {
        	//This has to be done before we open the editor
        	if(readOnly)
                editorModeMap.put(editorInput, READ_ONLY);
        	
        	
            IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput,
                    editorId);
            
            if(readOnly){
                setReadOnlyStatusMessage(editorPart);
                if (partListener == null) {
                    enablePartListener(true);
                }    
            }
        }
    }

    private class PartListener implements IPartListener2 {

        public void partActivated(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub

        }

        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub

        }

        public void partClosed(IWorkbenchPartReference partRef) {
            IWorkbenchPart part = partRef.getPart(false);
            if (part instanceof IEditorPart) {
                IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
                editorClosed(editorInput);
            }
        }

        public void partDeactivated(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub

        }

        public void partHidden(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub

        }

        public void partInputChanged(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub
        }

        public void partOpened(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub
        }

        public void partVisible(IWorkbenchPartReference partRef) {
            // TODO Auto-generated method stub
        }

    }

}
