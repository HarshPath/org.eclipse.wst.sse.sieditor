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
package org.eclipse.wst.sse.sieditor.model.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class StatusUtils {
	
	public static boolean isUnderJunitExecution = false;
	
	private static long showStatusDialog_calls = 0;
	
	private static long showDialogWithResult_calls = 0;
	
	public static boolean canContinue(IStatus status) {
		return status == null || 
				status.getSeverity() == IStatus.OK ||
				status.getSeverity() == IStatus.INFO ||
				status.getSeverity() == IStatus.WARNING;
	}

	public static void showStatusDialog(String dialogTitle, IStatus statusToShow) {
		showStatusDialog(dialogTitle, "", statusToShow); //$NON-NLS-1$
	}
	
	public static void showStatusDialog(String dialogTitle, String message, IStatus statusToShow) {
		if(statusToShow instanceof HiddenErrorStatus) {
			return;
		}
		
		if(isUnderJunitExecution) {
			showStatusDialog_calls++;
			return;
		}
		
		
		if(message.length() == 0) {
			message = statusToShow.getMessage();
		}
		
		switch(statusToShow.getSeverity()) {
			case IStatus.ERROR:
				ErrorDialog.openError(Display.getDefault().getActiveShell(), dialogTitle, message, statusToShow);
				break;
			case IStatus.WARNING:
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), dialogTitle, message);
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), dialogTitle, message);
				break;
		}
	}
	
	public static boolean showDialogWithResult(int kind, String title, String message) {
		if(isUnderJunitExecution) {
			showDialogWithResult_calls++;
			return true;
		}
		return MessageDialog.open(kind, Display.getDefault().getActiveShell(), title, message, SWT.NONE);
	}
	
	public static long getShowStatusDialog_calls() {
		return showStatusDialog_calls;
	}
	
	public static long getShowDialogWithResult_calls_calls() {
		return showDialogWithResult_calls;
	}
}
