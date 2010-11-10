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
/**
 * 
 */
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.ElementDetailsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.IDetailsPageSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.SimpleTypeConstraintsSection;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;


public class ElementNodeDetailsPage extends AbstractDetailsPage {
    protected ElementNodeDetailsController detailsController;

    private SimpleTypeConstraintsSection constraintsSection;

    private final ITypeDisplayer typeDisplayer;

    public ElementNodeDetailsPage(final IDataTypesFormPageController controller, final ITypeDisplayer typeDisplayer) {
        super(controller);
        this.typeDisplayer = typeDisplayer;
        detailsController = new ElementNodeDetailsController(controller);
    }

    @Override
    protected void createSections(final Composite parent) {
        final List<IDetailsPageSection> sections = new ArrayList<IDetailsPageSection>();

        final ElementDetailsSection detailsSection = createDetailsSection();
        detailsSection.setProblemDecorator(this.getProblemDecorator());
        detailsSection.createContents(parent);
        sections.add(detailsSection);

        constraintsSection = new SimpleTypeConstraintsSection(detailsController, getToolkit(), getManagedForm());
        constraintsSection.createContents(parent);
        sections.add(constraintsSection);

        final DocumentationSection documentationSection = new DocumentationSection(getController(), getToolkit(),
                getManagedForm());
        documentationSection.createContents(parent);
        sections.add(documentationSection);

        super.setSections(sections);
    }

    /**
     * utility method. creates new details section
     * 
     * @return the created details section
     */
    protected ElementDetailsSection createDetailsSection() {
        return new ElementDetailsSection(getDetailsController(), getToolkit(), getManagedForm(), getTypeDisplayer());
    }

    @Override
    public void selectionChanged(final IFormPart part, final ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            final Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            if (firstElement instanceof ITreeNode) {
                detailsController.setInput((ITreeNode) firstElement);
            }
        }
        super.selectionChanged(part, selection);
    }

    // ===========================================================
    // helper getter methods
    // ===========================================================
    
    protected ITypeDisplayer getTypeDisplayer() {
        return typeDisplayer;
    }

    protected ElementNodeDetailsController getDetailsController() {
        return detailsController;
    }
}
