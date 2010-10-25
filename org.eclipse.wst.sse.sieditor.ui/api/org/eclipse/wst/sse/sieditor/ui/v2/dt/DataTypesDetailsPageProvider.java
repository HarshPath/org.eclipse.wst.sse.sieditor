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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;

public class DataTypesDetailsPageProvider implements IDetailsPageProvider {

    public static final String NAMESPACE_KEY = "namespace"; //$NON-NLS-1$
    public static final String SIMPLE_TYPE_KEY = "simpletype"; //$NON-NLS-1$
    public static final String COMPLEX_TYPE_KEY = "complextype"; //$NON-NLS-1$
    public static final String ELEMENT_KEY = "element"; //$NON-NLS-1$

    private final IDataTypesFormPageController controller;
    private final ITypeDisplayer typeDisplayer;

    private final Map<String, IDetailsPage> pages;

    public DataTypesDetailsPageProvider(final IDataTypesFormPageController controller, final ITypeDisplayer typeDisplayer) {
        this.controller = controller;
        this.typeDisplayer = typeDisplayer;
        pages = new HashMap<String, IDetailsPage>();
    }

    public Object getPageKey(final Object object) {
        if (object instanceof INamespaceNode) {
            return NAMESPACE_KEY;
        } else if (object instanceof ISimpleTypeNode) {
            return SIMPLE_TYPE_KEY;
        } else if (object instanceof IStructureTypeNode) {
            final IStructureType modelObject = (IStructureType) ((IStructureTypeNode) object).getModelObject();
            if (modelObject.isElement()) {
                return ELEMENT_KEY;
            }
            return COMPLEX_TYPE_KEY;
        } else if (object instanceof IElementNode) {
            return ELEMENT_KEY;
        }
        return null;
    }

    public IDetailsPage getPage(final Object key) {

        IDetailsPage page = getPages().get(key);
        if (page != null) {
            return page;
        }

        if (NAMESPACE_KEY.equals(key)) {
            page = new NamespaceDetailsPage(controller);
        } else if (ELEMENT_KEY.equals(key)) {
            page = new ElementNodeDetailsPage(controller, typeDisplayer);
        } else if (COMPLEX_TYPE_KEY.equals(key)) {
            page = new StructureNodeDetailsPage(controller);
        } else if (SIMPLE_TYPE_KEY.equals(key)) {
            page = new SimpleTypeNodeDetailsPage(controller, typeDisplayer);
        }

        if (page != null) {
            getPages().put((String) key, page);
        }

        return page;
    }
    
    // ===========================================================
    // for testing purposes only
    // ===========================================================
    
    protected Map<String, IDetailsPage> getPages() {
        return pages;
    }
}