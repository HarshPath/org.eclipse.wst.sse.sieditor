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

import org.eclipse.core.runtime.Status;

import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * Error status, which is used for transaction revert.
 * StatusUtils can recognise this class and will not show error dialog to the user.
 * @see StatusUtils
 * @see ResourceUtils#canEdit(org.eclipse.core.resources.IFile)
 */
public class HiddenErrorStatus extends Status {

	private static HiddenErrorStatus status = new HiddenErrorStatus(ERROR, Activator.PLUGIN_ID, ERROR, "", null); //$NON-NLS-1$
	
	public static HiddenErrorStatus getInstance() {
		return status;
	}
	
	private HiddenErrorStatus(int severity, String pluginId, int code,
			String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

}
