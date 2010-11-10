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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.sections.tables.editing;

import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

/**
 * Editing support for the enumeration constraint section of the xsd
 * types/elements
 * 
 * 
 * 
 */
public class EnumsTableEditingSupport extends PatternsTableEditingSupport {

    public EnumsTableEditingSupport(final TableViewer viewer, final ElementNodeDetailsController detailsController) {
        super(viewer, detailsController);
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        if (((String) value).trim().isEmpty()) {
            getDetailsController().getConstraintsController().deleteEnum((IFacet) element);
        } else {
            getDetailsController().getConstraintsController().setEnum((IFacet) element, (String) value);
        }
    }

}