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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.NamespaceDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.NamespaceDetailsSection;
import org.junit.Test;


public class NamespaceDetailsPageTest {

    @Test
    public void testCreateSections() {
        final Display display = Display.getDefault();
        
        // For JUnit Plug-in Test
        NamespaceDetailsPage page = new NamespaceDetailsPage(createMock(IFormPageController.class));
        
        // For JUnit Test
//        final Image errorImage = new Image(display, 10, 10);
//        NamespaceDetailsPage page = new NamespaceDetailsPage(createMock(IFormPageController.class)) {
//            protected NamespaceDetailsSection createDetailsSection(Composite parent) {
//                NamespaceDetailsSection section = new NamespaceDetailsSection(getController(), getToolkit(), getManagedForm()) {
//                    protected ISharedImages getSharedImages() {
//                        ISharedImages sharedImages = createMock(ISharedImages.class);
//                        expect(sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK)).andReturn(errorImage).once();
//                        replay(sharedImages);
//                        return sharedImages;
//                    }
//                };
//                section.createContents(parent);
//                return section;
//            }
//        };
        
        page.createContents(new Shell(display));
        
        List<IDetailsPageSection> sections = page.getSections();
        assertEquals(1, sections.size());
        assertTrue(sections.get(0) instanceof NamespaceDetailsSection);
    }

}
