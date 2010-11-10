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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.ContextMenuConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;

public class RepairContextMenuListener implements IMenuListener {

    public void menuAboutToShow(IMenuManager manager) {
        final List<IContributionItem> sieItems = new ArrayList<IContributionItem>();
        final IContributionItem[] items = manager.getItems();

        for (final IContributionItem iContributionItem : items) {
            if (iContributionItem == null) {
                continue;
            }
            String id = iContributionItem.getId();
            if (id != null && id.startsWith(ContextMenuConstants.SIE_CONTEXT_MENU_ID_PREFIX)) {
                sieItems.add(iContributionItem);
            }
        }

        manager.removeAll();
        for (final IContributionItem item : sieItems) {
            manager.add(item);
        }
    }
}
