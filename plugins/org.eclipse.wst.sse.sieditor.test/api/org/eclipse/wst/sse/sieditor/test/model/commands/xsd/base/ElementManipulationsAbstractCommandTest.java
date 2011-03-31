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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public abstract class ElementManipulationsAbstractCommandTest extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String COMPLEX_TYPE_NAME = "Address";

    protected Schema schema;
    protected IWsdlModelRoot modelRoot;

    protected Element createElement(final IWsdlModelRoot modelRoot, final String newElementName) throws Exception {
        this.modelRoot = modelRoot;

        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);

        final AddElementCommand cmd;

        schema = (Schema) schemas[0];
        cmd = new AddElementCommand(modelRoot, (StructureType) schema.getType(false, COMPLEX_TYPE_NAME), newElementName) {
            @Override
            public boolean canRedo() {
                return false;
            }

            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(cmd);

        return cmd.getElement();
    }
}
