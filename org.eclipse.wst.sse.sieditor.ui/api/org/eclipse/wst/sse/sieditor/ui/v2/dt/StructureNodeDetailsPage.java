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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.StructureDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;

public class StructureNodeDetailsPage extends AbstractDetailsPage {

    public StructureNodeDetailsPage(IFormPageController controller) {
        super(controller);
    }
    
    @Override
    protected void createSections(Composite parent) {
        List<IDetailsPageSection> sections = new ArrayList<IDetailsPageSection>();
        
        StructureDetailsSection detailsSection = new StructureDetailsSection(getController(), getToolkit(), getManagedForm());
        detailsSection.createContents(parent);
        sections.add(detailsSection);
        
        DocumentationSection documentationSection = new DocumentationSection(getController(), getToolkit(), getManagedForm());
        documentationSection.createContents(parent);
        sections.add(documentationSection);
        
        super.setSections(sections);
    }
}
