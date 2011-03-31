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

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public abstract class AbstractCommandTest extends AbstractBaseCommandTest<IWsdlModelRoot> {

    @Override
    protected String getFilename() {
        return getWsdlFilename();
    }

    protected String getWsdlFilename() {
        return "PurchaseOrderConfirmation.wsdl";
    }

    protected String getWsdlFoldername() {
        return Constants.DATA_PUBLIC_SELF_MIX2;
    }

    @Override
    protected String getFolderName() {
        return getWsdlFoldername();
    }

    @Override
    protected String getEditorId() {
        return ServiceInterfaceEditor.EDITOR_ID;
    }

}
