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
package org.eclipse.wst.sse.sieditor.test.model.commands;

import org.eclipse.wst.sse.sieditor.test.model.Constants;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public abstract class AbstractXSDCommandTest extends AbstractBaseCommandTest<IXSDModelRoot> {

    public AbstractXSDCommandTest() {
    }

    @Override
    protected String getEditorId() {
        return DataTypesEditor.EDITOR_ID;
    }

    @Override
    protected String getFilename() {
        return getXSDFilename();
    }

    protected String getXSDFilename() {
        return "PurchaseOrderConfirmation.wsdl";
    }

    @Override
    protected String getFolderName() {
        return getXSDFoldername();
    }

    protected String getXSDFoldername() {
        return Constants.DATA_PUBLIC_SELF_MIX2;
    }

}
