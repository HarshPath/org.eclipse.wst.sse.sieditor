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
package org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

/**
 * The {@link ElementTypeEditor} type committer helper implementation of the
 * {@link ITypeCommitter} interface
 * 
 * 
 * 
 */
public class ElementTypeEditorTypeCommitter implements ITypeCommitter {

    private ElementNodeDetailsController detailsController;

    public ElementTypeEditorTypeCommitter(final ElementNodeDetailsController detailsController) {
        this.detailsController = detailsController;
    }

    @Override
    public void commitName(final IType type, final String typeName) {
        commitType(type);
    }
    
    @Override
    public void commitType(final IType type) {
        detailsController.setType(type);
    }

    // ===========================================================
    // helpers
    // ===========================================================

    public void setDetailsController(final ElementNodeDetailsController detailsController) {
        this.detailsController = detailsController;
    }

    public ElementNodeDetailsController getDetailsController() {
        return detailsController;
    }

}
