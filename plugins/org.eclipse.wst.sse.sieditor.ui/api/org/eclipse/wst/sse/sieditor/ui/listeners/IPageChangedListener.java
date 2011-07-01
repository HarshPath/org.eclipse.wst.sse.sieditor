/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stanislav Nichev - initial API and implementation.
 *    Dimitar Tenev - refactoring.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.listeners;

import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * 
 * This listener is responsible for the handling of the SIE/DTE page changes
 * events (for example page is changed from the source page to the data types
 * page)
 * 
 */
public interface IPageChangedListener {

    public void pageChanged(final int newPageIndex, final int oldPageIndex, final List pages, final IModelRoot modelRoot);

}
