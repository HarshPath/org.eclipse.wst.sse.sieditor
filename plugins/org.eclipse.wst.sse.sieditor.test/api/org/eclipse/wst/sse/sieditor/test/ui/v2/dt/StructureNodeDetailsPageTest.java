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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StructureNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.StructureTypeDetailsSection;
import org.junit.Test;


public class StructureNodeDetailsPageTest {

    @Test
    public void testCreateSections() {
        final Display display = Display.getDefault();

        final IDataTypesFormPageController controllerMock = createMock(IDataTypesFormPageController.class);
        final ITypeDisplayer typeDisplayer = createMock(ITypeDisplayer.class);

        expect(controllerMock.isResourceReadOnly()).andReturn(false).anyTimes();
        expect(controllerMock.getCommonTypesDropDownList()).andReturn(new String[] {}).anyTimes();

        replay(controllerMock);

        final StructureNodeDetailsPage page = new StructureNodeDetailsPage(controllerMock, typeDisplayer);

        page.createContents(new Shell(display));

        final List<IDetailsPageSection> sections = page.getSections();
        assertEquals(2, sections.size());
        assertTrue(sections.get(0) instanceof StructureTypeDetailsSection);
    }

}
