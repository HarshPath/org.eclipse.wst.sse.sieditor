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
/**
 * 
 */
package org.eclipse.wst.sse.sieditor.ui;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension3;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class EditorActivationListener implements IPartListener, IWindowListener,  IDisposable{
	/**
	 * 
	 */
	private final AbstractEditorWithSourcePage editor;

	private IPartService partService;
	
	private boolean activated;

	private final IWorkbenchPart editorPart;
	
	private long modificationStamp;
	
	public EditorActivationListener(AbstractEditorWithSourcePage editor, IWorkbenchPart editorPart) {
		this.editor = editor;
		this.editorPart = editorPart;
		this.partService = editorPart.getSite().getWorkbenchWindow().getPartService();
		partService.addPartListener(this);
		PlatformUI.getWorkbench().addWindowListener(this);
	}
	
	private void handleActivation() {
		if (activated && isInputChanged()) {
			handleEditorInputChanged();
		}
	}

	@Override
	public void doDispose() {
		partService.removePartListener(this);
		PlatformUI.getWorkbench().removeWindowListener(this);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		activated = part == this.editorPart;
		handleActivation();
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part == this.editorPart) {
			activated = false;
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partClosed(IWorkbenchPart part) {
	}


	public void partOpened(IWorkbenchPart part) {
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		if (window == this.editor.getEditorSite().getWorkbenchWindow()) {
			handleActivation();
		}
	}

	public void windowClosed(IWorkbenchWindow window) {
	}

	public void windowDeactivated(IWorkbenchWindow window) {
	}

	public void windowOpened(IWorkbenchWindow window) {
	}

	private boolean isInputChanged() {
		IDocumentProvider provider = editor.getSourcePage().getDocumentProvider();
		if (provider == null) {
			return false;
		}
	
		IDocumentProviderExtension3 provider3= (IDocumentProviderExtension3) provider;
	
		long stamp= provider.getModificationStamp(editor.getEditorInput());
		if (stamp != modificationStamp) {
			modificationStamp = stamp;
			return !provider3.isSynchronized(editor.getEditorInput());
		}
	
		return false;
	}

	private void handleEditorInputChanged() {
		String message= MessageFormat.format(Messages.ResourceChangeHandler_1 + "\n" + //$NON-NLS-1$ 
	            Messages.ResourceChangeHandler_3, editor.getEditorInput().getName());

		boolean answer = StatusUtils.showDialogWithResult(MessageDialog.QUESTION, Messages.ResourceChangeHandler_0, message);
		
		if (answer) {
			editor.revertContentsToSavedVersion();
		}
	}

}