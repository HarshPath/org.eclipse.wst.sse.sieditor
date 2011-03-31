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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServiceNode;
import org.eclipse.wst.wsdl.Definition;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public class ImportedServiceNodeTest {

    private static final String NAMESPACE = "http://target/namespace/of/definition.com";
    private static final String LOCATION = "../test/location/of/definition.wsdl";

    @Test
    public void getDisplayName() {
        final IDescription description = createMock(IDescription.class);
        final IWsdlModelRoot modelRoot = createMock(IWsdlModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(description).anyTimes();
        replay(modelRoot);

        expect(description.getNamespace()).andReturn(NAMESPACE).atLeastOnce();
        expect(description.getLocation()).andReturn(LOCATION);
        final Definition definition = createNiceMock(Definition.class);
        expect(description.getComponent()).andReturn(definition);
        replay(description);

        final SIFormPageController controller = EasyMock.createNiceMock(SIFormPageController.class);
        final ITreeNode parent = createNiceMock(ITreeNode.class);
        final ImportedServiceNode importedServiceNode = new ImportedServiceNode(description, parent, controller);

        assertEquals(NAMESPACE + UIConstants.SPACE + UIConstants.OPEN_BRACKET + LOCATION + UIConstants.CLOSE_BRACKET,
                importedServiceNode.getDisplayName());
    }

}
