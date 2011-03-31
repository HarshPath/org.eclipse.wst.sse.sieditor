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
package org.eclipse.wst.sse.sieditor.test.ui.readonly;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IDocSectionContainer;
import org.junit.Test;


public class CommonDocumentationSectionReadOnlyTest {

    private static final String SAMPLE_TEXT = "sample text";

    @Test
    public final void testUpdate() {
        IDocSectionContainer page = createNiceMock(IDocSectionContainer.class);
        expect(page.isReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(page);
        
        DocumentationSection section = new DocumentationSection(page);
        Shell parent = new Shell();
        section.createSection(parent, new FormToolkit(Display.getCurrent()));
        
        section.update(SAMPLE_TEXT);
        
        Composite sectionContent = (Composite) ((Section) parent.getChildren()[0]).getClient();
        assertTrue(((Text) sectionContent.getChildren()[0]).getEditable());
        
        verify(page);
        reset(page);
        expect(page.isReadOnly()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        replay(page);

        section.update(SAMPLE_TEXT);
        
        assertFalse(((Text) sectionContent.getChildren()[0]).getEditable());

        verify(page);
    }

}
