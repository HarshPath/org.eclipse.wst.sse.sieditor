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
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

/**
 * The {@link ParameterTypeEditor} type committer helper implementation of the
 * {@link ITypeCommitter} interface
 * 
 */
public class ParameterTypeEditorTypeCommitter implements ITypeCommitter {

    private final SIFormPageController controller;
    private ITreeNode input;

    public ParameterTypeEditorTypeCommitter(final SIFormPageController controller) {
        this.controller = controller;
    }

    @Override
    public void commitName(final IType type, final String selectedTypeName) {
        controller.editParameterTypeTriggered(input, selectedTypeName);
    }

    @Override
    public void commitType(final IType type) {
        controller.editParameterTypeTriggered(input, type);
    }
    
    public void setInput(final ITreeNode input) {
        this.input = input;
    }

}
