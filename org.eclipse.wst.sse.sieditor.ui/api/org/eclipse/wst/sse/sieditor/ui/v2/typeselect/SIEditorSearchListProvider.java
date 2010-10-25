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
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDElementsSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDTypesSearchListProvider;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

@SuppressWarnings("restriction")
public class SIEditorSearchListProvider extends XSDSearchListProvider implements ISIComponentSearchListProvider {
    final XSDTypesSearchListProvider typesProvider;
    final XSDElementsSearchListProvider elementProvider;

    private IType typeToFilter;
    private final boolean addElements;
    private final boolean showSimpleTypes;
    private final boolean showComplexTypes;

    public void setTypeToFilter(IType typeToFilter) {
        this.typeToFilter = typeToFilter;
    }

    @SuppressWarnings("restriction")
    public SIEditorSearchListProvider(IFile currentFile, XSDSchema[] schemas, boolean showElements, boolean showComplexTypes,
            boolean showSimpleTypes) {
        super(currentFile, schemas);
        this.addElements = showElements;
        this.showComplexTypes = showComplexTypes;
        this.showSimpleTypes = showSimpleTypes;
        typesProvider = new XSDTypesSearchListProvider(currentFile, schemas);
        typesProvider.showComplexTypes(showComplexTypes);

        elementProvider = new XSDElementsSearchListProvider(currentFile, schemas);

    }

    @SuppressWarnings("restriction")
    public void populateComponentList(final IComponentList list, SearchScope scope, IProgressMonitor pm) {
        final FilteringComponentList tempList = new FilteringComponentList();
        if (showSimpleTypes || showComplexTypes) {
            typesProvider.populateComponentList(tempList, scope, pm);
        }

        if (addElements) {
            elementProvider.populateComponentList(tempList, scope, pm);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                Iterator iterator = tempList.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            }
        };
        /**
         * Need to be executed from UI thread, otherwise
         * ConcurentModificationException appears in
         * org.eclipse.wst.common.ui.internal
         * .search.dialogs.ComponentSearchListDialog
         * .fireUpdateList(ComponentList) line 469
         */
        if (Display.getCurrent() == null) {
            Display.getDefault().syncExec(runnable);
        } else {
            runnable.run();
        }
    }

    protected class FilteringComponentList implements IComponentList {
        private Vector objectVector = new Vector();

        public void add(Object o) {
            if (filterOutItem(o)) {
                return;
            }
            objectVector.add(o);
        }

        public Iterator iterator() {
            return objectVector.iterator();
        }

        protected boolean filterOutItem(Object o) {
            return (typeToFilter != null && typeToFilter.getComponent().equals(o)) || objectVector.contains(o);
        }
    }
}
