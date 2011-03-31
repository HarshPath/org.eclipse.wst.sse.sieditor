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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections;

import static org.easymock.EasyMock.createNiceMock;

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.xsd.XSDPackage;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

public class TestElementConstraintsController {

    @Test
    public void testWhitespaceControlFocusLostNull() {
        IDataTypesFormPageController controller = createNiceMock(IDataTypesFormPageController.class);
        ElementConstraintsController elementController = new ElementConstraintsController(controller, (IElement) null);
        try {
            elementController.setWhitespace(null);
        } catch (NullPointerException ex) {
            Assert.fail("NPE was thrown, but was not expected.");
        }
    }

    @Test
    public void testAddPatternFacet() {
        IDataTypesFormPageController controller = EasyMock.createMock(IDataTypesFormPageController.class);
        IElement element = createNiceMock(IElement.class);
        String pattern = "[1-9]";
        controller.setElementFacet(element, pattern, XSDPackage.XSD_PATTERN_FACET);
        EasyMock.replay(controller, element);

        ElementConstraintsController elementController = new ElementConstraintsController(controller, element);

        elementController.addPattern(pattern);
        EasyMock.verify(controller);

    }
}
