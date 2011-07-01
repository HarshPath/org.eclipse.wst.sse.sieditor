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
package org.eclipse.wst.sse.sieditor.ui.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.ModelReconcilerPageChangedLister;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.SelectionUpdaterPageChangedListener;
import org.eclipse.wst.sse.sieditor.ui.listeners.impl.TreeRefresherPageChangedListener;
import org.eclipse.wst.sse.sieditor.ui.view.impl.SISourceEditorPart;

public class PageChangedListenersManager {

    private List<IPageChangedListener> listeners;

    private final SISourceEditorPart sourcePage;

    public PageChangedListenersManager(SISourceEditorPart sourcePage) {
        super();
        this.sourcePage = sourcePage;
        initializeDefaultPageChangedListeners();
    }

    private void initializeDefaultPageChangedListeners() {
        listeners = new ArrayList<IPageChangedListener>();

        listeners.add(new ModelReconcilerPageChangedLister());
        listeners.add(new SelectionUpdaterPageChangedListener(sourcePage));
        listeners.add(new TreeRefresherPageChangedListener());
    }

    public void notifyPageChanged(int newPageIndex, int oldPageIndex, List pages, IModelRoot modelRoot) {
        for (IPageChangedListener listener : listeners) {
            listener.pageChanged(newPageIndex, oldPageIndex, pages, modelRoot);
        }
    }
    
    public List<IPageChangedListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public void addPageChangeListener(IPageChangedListener pageChangedListener) {
        listeners.add(pageChangedListener);
    }

}
