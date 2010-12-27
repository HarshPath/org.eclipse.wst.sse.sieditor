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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.IFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.StructureTypeDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;

public class StructureNodeDetailsPage extends AbstractDetailsPage {

    private final ITypeDisplayer typeDisplayer;
    private final ElementNodeDetailsController detailsController;

    @Deprecated
    public StructureNodeDetailsPage(final IFormPageController controller) {
        this(controller, null);
    }

    public StructureNodeDetailsPage(final IFormPageController controller, final ITypeDisplayer typeDisplayer) {
        super(controller);
        this.typeDisplayer = typeDisplayer;
        this.detailsController = new ElementNodeDetailsController((IDataTypesFormPageController) controller);
    }

    @Override
    protected void createSections(final Composite parent) {
        final List<IDetailsPageSection> sections = new ArrayList<IDetailsPageSection>();

        final StructureTypeDetailsSection detailsSection = new StructureTypeDetailsSection(getController(), getToolkit(),
                getManagedForm(), detailsController, typeDisplayer);
        detailsSection.createContents(parent);
        sections.add(detailsSection);

        final DocumentationSection documentationSection = new DocumentationSection(getController(), getToolkit(),
                getManagedForm());
        documentationSection.createContents(parent);
        sections.add(documentationSection);

        super.setSections(sections);
    }
}
