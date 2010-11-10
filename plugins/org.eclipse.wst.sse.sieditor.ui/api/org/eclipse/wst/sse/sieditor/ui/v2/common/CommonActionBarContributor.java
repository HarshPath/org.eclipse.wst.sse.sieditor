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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import org.eclipse.wst.sse.sieditor.ui.AbstractEditorWithSourcePage;

public class CommonActionBarContributor extends MultiPageEditorActionBarContributor {
    @Override
    public void setActivePage(IEditorPart activeEditor) {
    }

    @Override
    public void setActiveEditor(IEditorPart part) {
        super.setActiveEditor(part);

        if (part instanceof AbstractEditorWithSourcePage) {
            ((AbstractEditorWithSourcePage) part).setGlobalActionHandlers();
        }

        getActionBars().updateActionBars();
    }

    @Override
    public void dispose() {
        super.dispose();

        final IActionBars actionBars = getActionBars();
        if (actionBars != null) {
            actionBars.clearGlobalActionHandlers();
            actionBars.updateActionBars();
        }
    }
}
